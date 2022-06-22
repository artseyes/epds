package gov.gao.epds.controller;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import gov.gao.epds.dto.DTOValidator;
import gov.gao.epds.dto.InputValidationError;
import gov.gao.epds.dto.LoginDTO;
import gov.gao.epds.dto.ProtestInfoFormDTO;
import gov.gao.epds.dto.SubmitNewDocDTO;
import gov.gao.epds.enums.UserRoles;
import static gov.gao.epds.enums.UserRoles.*;
import gov.gao.epds.persistence.entity.File_Info;
import gov.gao.epds.persistence.entity.Protest_Info;
import gov.gao.epds.persistence.entity.Tier_2_Agency;
import gov.gao.epds.persistence.entity.User_Info;
import gov.gao.epds.persistence.entity.User_Protest_Role_Bridge;
import gov.gao.epds.rest.auth.services.AuthUtil;
import gov.gao.epds.service.CaseDocketSheetService;
import gov.gao.epds.service.DashboardService;
import gov.gao.epds.service.GC_Service;
import gov.gao.epds.service.HomeService;
import gov.gao.epds.service.ProtestInfoService;
import gov.gao.epds.service.UserInfoService;
import gov.gao.epds.session.EpdsSession;
import gov.gao.epds.utils.Date_Util;
import gov.gao.epds.utils.EPDS_FileUtils;
import gov.gao.epds.utils.Params;
//import gov.gao.epds.utils.PayDotGovProdUtil;
import gov.gao.epds.utils.PayDotGovUtil;
import gov.gao.epds.utils.Protest_info_util;
import gov.gao.epds.utils.Util;
import gov.gao.epds.utils.ZipFile_Util;
import gov.treas.fms.services.tcsonline_3_0.TCSServiceFault_Exception;

@Controller
public class ProtestInfoController { // NO_UCD (unused code)

	private final static Logger logger = LoggerFactory
			.getLogger(ProtestInfoController.class);

	@Autowired
	private ProtestInfoService protestInfoService;

	@Autowired
	private HomeService homeService;

	@Autowired
	private CaseDocketSheetService caseDocketSheetService;

	@Autowired
	private GC_Service gc_Service;

	@Autowired
	private DashboardService dashboardService;

	@Autowired
	private UserInfoService userInfoService;


	/**
	 * 
	 * protests, supplemental protests, entitlements, and costs can only be filed by protester OR PLCG.
	 * Recons can be filed by protester, intervenor, agency rep, agency POC, and PLCG (basically all users except GAO attorneys or supervisors).

	 * Register Protest based on the type of protest. In case of Protest we register the protest before redirecting to Pay.gov
	 * @param typeOfProtest : Protest,Supplemental , RECON,ENT, COST CLAIMS
	 * @param protestId :Required if it is not a brand new protest
	 * @param protestInfoFormDTO : captures the protest info form data
	 * @param map
	 * @param request
	 * @param redirectAttrs
	 * @throws Exception
	 */
	@RequestMapping(value = "/register-protest", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON )
	public @ResponseBody ModelMap registerProtest(
			@RequestParam(value = "typeOfProtest", required = true) String typeOfProtest,
			@RequestParam(value = "protestId", required = false) String protestId,
			@ModelAttribute ProtestInfoFormDTO protestInfoFormDTO,
			ModelMap map, HttpServletRequest request,
			RedirectAttributes redirectAttrs) throws Exception {

		EpdsSession.removeAttribute(request, "protest_InfoList");

		if (logger.isDebugEnabled()){
			logger.debug("/register-protest a_No = {}, typeOfRequest = {}",protestId,typeOfProtest);
		}


		List<InputValidationError> constraintViolations = AuthUtil.validateDTO(protestInfoFormDTO);


		if (!constraintViolations.isEmpty() && null != constraintViolations ){

			map.addAttribute("inputErrors", constraintViolations);

			return map;
		}

		User_Info loggedInUserInfo = (User_Info) EpdsSession.getAttribute(request,"user_Info");

		String userId  = loggedInUserInfo.getUser_Id();
		map.addAttribute("userProfileInfo", loggedInUserInfo);

		String user_Role = (String) EpdsSession.getAttribute(request,"user_Role");
		Integer filerId = protestInfoFormDTO.getFilerId();

		Protest_Info protest_Info = null;

		UserRoles role = UserRoles.getByCode(loggedInUserInfo.getRole_id());
		if (role != GAO_ADMIN &&  role != PROTESTER
				&& "protest".equalsIgnoreCase(typeOfProtest)){
			throw new IllegalAccessError("The request cannot be completed!! ");
		}

		User_Info protestRepUserInfo = null;

		if (role == GAO_ADMIN
				&& null != typeOfProtest && "protest".equalsIgnoreCase(typeOfProtest)){

			protestRepUserInfo = userInfoService.getUser_InfoByEmail(protestInfoFormDTO.getEmail());
			userId = protestRepUserInfo.getUser_Id();
		}

		if ("protest".equalsIgnoreCase(typeOfProtest)) {

			protestInfoFormDTO.setSubmissionDate(Date_Util.getCurrentDate());
			protest_Info = protestInfoService.registerProtestInfo(
					protestInfoFormDTO, loggedInUserInfo, user_Role,
					null,null,protestRepUserInfo);
			protestInfoFormDTO.setA_No(protest_Info.getA_No());

			SubmitNewDocDTO submitNewDocDTO = getSubmitNewDocDTO(
					protest_Info.getA_No(), userId,
					protestInfoFormDTO.getIsDocConfidential(),
					protest_Info.getCompany_Name(), protest_Info.getComments(),typeOfProtest,filerId);
			protestInfoService.saveFilesToDB(submitNewDocDTO, request);
		} else {
			Protest_Info protestInfoByA_no = dashboardService.getProtestInfoBasedOnUserIdAndANo(loggedInUserInfo, protestId);

			User_Protest_Role_Bridge uprb = dashboardService.checkIfUPRBExists(protestInfoByA_no, loggedInUserInfo);

			protestInfoService.validateAccess(typeOfProtest, loggedInUserInfo, protestInfoByA_no, uprb);

			if (filerId == 0) {
				// non GAO user submitting, convert user_Role to filerId
				if (protestInfoByA_no.getRole().toUpperCase(Locale.ENGLISH).contains("AGENCY")) {
					filerId = 3;
				} else if (protestInfoByA_no.getRole().toUpperCase(Locale.ENGLISH).contains("INTERVENOR")) {
					filerId = 2;
				}
			}
			
			// leave protest company name the same, just doc needs which company/firm submitted
//			protestInfoByA_no.setCompany_Name(company_Name);
			protest_Info = protestInfoService.registerOtherProtestInfo(
					loggedInUserInfo.getUser_Id(), protestInfoByA_no, typeOfProtest,
					null/*,filerId,user_Role*/);


			// get company name for doc submission
            String company_Name = protestInfoService.getCompanyName(filerId, protestInfoByA_no, userId, protestInfoFormDTO);
			SubmitNewDocDTO submitNewDocDTO = getSubmitNewDocDTO(
					protest_Info.getA_No(), loggedInUserInfo.getUser_Id(),
					protestInfoFormDTO.getIsDocConfidential(),
					company_Name, protest_Info.getComments(),typeOfProtest,filerId);
			protestInfoService.saveFilesToDB(submitNewDocDTO, request);
		}

		if (protest_Info != null) {

			try {
				if (protest_Info.getCase_Type().toUpperCase(Locale.ENGLISH).contains("RECON")
						|| protest_Info.getCase_Type().toUpperCase(Locale.ENGLISH).contains("ENT")
						|| protest_Info.getCase_Type().toUpperCase(Locale.ENGLISH).contains("COST")){
				    gc_Service.set_Event_For_New_Protest_Info(protest_Info);
				   
					protestInfoService.sendNotificationToPartiesAssociatedWithTheCase(protest_Info,request);
				} else if (protest_Info.getCase_Type().toUpperCase(Locale.ENGLISH).contains("PROTEST") &&
				        role == GAO_ADMIN ) {
				    gc_Service.set_Event_For_New_Protest_Info(protest_Info);
				    protestInfoService.sendNotificationToAgencyAdminAndProtester(protest_Info,request,"newProtest");
				}
			}catch (Exception e){
				e.printStackTrace();
				System.out.println("ProtestInfoController.registerProtest()");
			}
			map.addAttribute("user_Role",
					(String) EpdsSession.getAttribute(request, "user_Role"));
		}

		return map;
	}


	


	@RequestMapping(value = "/validate-protest", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON )
	public @ResponseBody ModelMap validateProtestInfoDTO(
			@RequestParam(value = "typeOfProtest", required = true) String typeOfProtest,
			@RequestParam(value = "protestId", required = false) String protestId,
			@ModelAttribute ProtestInfoFormDTO protestInfoFormDTO,
			ModelMap map, HttpServletRequest request,
			RedirectAttributes redirectAttrs) throws Exception {


		List<InputValidationError> constraintViolations = AuthUtil.validateDTO(protestInfoFormDTO);


		if (!constraintViolations.isEmpty() && null != constraintViolations ){

			map.addAttribute("inputErrors", constraintViolations);

			return map;
		}else{
			map.addAttribute("isSuccess", true);
		}

		return map;
	}

	/**
	 * Populate Submit New Doc DTO for other protest types
	 * @param a_No
	 * @param user_Id
	 * @param isDocConfidential
	 * @param company_Name
	 * @param comments
	 * @param typeOfProtest
	 * @return
	 * @throws Exception
	 */
	private SubmitNewDocDTO getSubmitNewDocDTO(String a_No, String user_Id,
			String isDocConfidential, String company_Name, String comments,String typeOfProtest, Integer filerId)
			throws Exception {

		SubmitNewDocDTO submitNewDocDTO = new SubmitNewDocDTO();
		int docId = 0;

		if (typeOfProtest.equalsIgnoreCase("protest")){
			docId = 1;
		}else if (typeOfProtest.equalsIgnoreCase("reconsideration")
				     || typeOfProtest.toUpperCase(Locale.ENGLISH).contains("RECON") ){
			docId = 163;
		}else if (typeOfProtest.equalsIgnoreCase("entitlement")
				|| typeOfProtest.toUpperCase(Locale.ENGLISH).contains("ENT")){
			docId = 164;
		}else if (typeOfProtest.equalsIgnoreCase("cost-claim")
				|| typeOfProtest.toUpperCase(Locale.ENGLISH).contains("COST")){
			docId = 165;
		}

		switch (filerId) {
			case 2:
				submitNewDocDTO.setUser_Role("INTERVENOR");
				break;
			case 3:
				submitNewDocDTO.setUser_Role("AGENCY");
				break;
			default:
				break;
		}

		submitNewDocDTO.setComments(comments);
		submitNewDocDTO.setSubmissionDate(Date_Util.getCurrentDate());
		submitNewDocDTO.setProtestId(a_No);
		submitNewDocDTO.setDocId(docId);
		submitNewDocDTO.setUser_Id(user_Id);
		submitNewDocDTO.setTypeofdocument("Protest");
		submitNewDocDTO.setIsDocConfidential(isDocConfidential);
		submitNewDocDTO.setCompany_Name(company_Name);

		return submitNewDocDTO;
	}

	/**
	 * Retrieves list of Tier 2 Agencies based on tier 1 Id
	 * @param id
	 * @param map
	 * @throws Exception
	 */
	@RequestMapping(value = "tier2/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON )
	public @ResponseBody ModelMap getListOfTier2_Agency(@PathVariable int id, ModelMap map)
			throws Exception {

		if (logger.isDebugEnabled()){
			logger.debug("getListOfTier2_Agency id = {}",id);
		}
		List<Tier_2_Agency> tier2List = protestInfoService.getAgencyTier2(id);

		if (tier2List != null && tier2List.size() > 0) {
			map.addAttribute("tier2AgencyList", tier2List);
		} else {
			map.addAttribute("tier2AgencyList", "empty");
		}

		return map;
	}


	/**
	 * This is not being Used.
	 * It is used to retrieve information about a case by BNum for agency rep (Join a Case ) or Intervenor (Request to intervene)
	 * @param request
	 * @param bNum
	 * @param isJoinUnjoinValidation
	 * @param map
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "searchBnumber", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON )
	public @ResponseBody
	ModelMap getResponseForSearchBNumber(
			HttpServletRequest request,
			@RequestParam("b_Num") String bNum,
			@RequestParam(value = "isJoinUnjoinValidation", required = false) String isJoinUnjoinValidation,
			ModelMap map) throws Exception {

		if (logger.isDebugEnabled()){
			logger.debug("/searchBnumber b_No = {}",bNum);
		}


		if (!bNum.matches(DTOValidator.PROTEST_ID_PATTERN)){
			map.addAttribute("inputErrors", new ArrayList<String>(Arrays.asList("protestId is invalid")));

			return map;
		}

		String userId = (String) EpdsSession.getAttribute(request,
				"userprofileId");
		bNum = Util.getBNumberWithBDashPrefix(bNum);
		Protest_Info protest_Info = protestInfoService
				.getProtestInfoByBNum(bNum);
		if (protest_Info != null) {
			boolean alreadyHasAccessToCase = protestInfoService
					.findIfUserAlreadyHasAccessToCase(userId, bNum);

			if (!alreadyHasAccessToCase) {
				// need to make sure if this is being used anywhere ----- joinUnjoin Validation we are using Case Docket Sheet Controller

				if (!isJoinUnjoinValidation.equalsIgnoreCase("Y")) {
					EpdsSession.setAttribute(request, "a_No",
							protest_Info.getA_No());
				}

				String agencyName = protestInfoService
						.getAgencyName(protest_Info.getAgency_Info_Id());
				String response = protestInfoService
						.getResponseForRequestToIntervene(userId,
								protest_Info.getA_No());

				map.addAttribute("protestInfo", protest_Info);
				map.addAttribute("agencyName", agencyName);
				map.addAttribute("response", response);

			} else {
				map.addAttribute("response", "already has access");
			}

		} else {
			map.addAttribute("response", "does not exist");
		}
		return map;
	}

	/**
	 * This is used to store all the Attachments
	 * @param attachmentType
	 * @param content
	 * @param submitNewDocDTO
	 * @param map
	 * @param request
	 * @param redirectAttributes
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/add-attachments/{attachmentType}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON )
	public ModelMap addAttachments(
			@PathVariable("attachmentType") String attachmentType,
			@RequestParam(value = "content", required = false) String content,
			@ModelAttribute("submitNewDocDTO") SubmitNewDocDTO submitNewDocDTO,
			ModelMap map, HttpServletRequest request,
			RedirectAttributes redirectAttributes) throws Exception {
		
		
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request, "user_Info");
		UserRoles role = UserRoles.getByCode(user_Info.getRole_id());

		String userProtestRole = null;
		String companyName = null;
		Protest_Info protestInfo;
		int docTypeId = submitNewDocDTO.getDocId();


		String typeOfDoc = "";
		submitNewDocDTO.setCompany_Name((String) EpdsSession.getAttribute(
				request, "company_Name"));
		
		List<InputValidationError> constraintViolations = AuthUtil.validateDTO(submitNewDocDTO);


		if (!constraintViolations.isEmpty() && null != constraintViolations ){

			map.addAttribute("inputErrors", constraintViolations);

			return map;
		}

		protestInfo = dashboardService.getProtestInfoBasedOnUserIdAndANo(user_Info, submitNewDocDTO.getProtestId());
		
		// this check will fail in cases where the external user Agency Rep and Protester/Intervenor does not have access to the case....
		if (protestInfo.isViewOnly() 
				&& !"intervene".equals(attachmentType) 
				&& "!agency-rep-request".equalsIgnoreCase(attachmentType)){
			throw new IllegalAccessError("Request cannot be completed!!");
		}
		
		
		if ("intervene".equals(attachmentType)) {
			typeOfDoc = "Request to Intervene";
			userProtestRole = "INTERVENOR";
			companyName = submitNewDocDTO.getIntervenorCompanyName();
			submitNewDocDTO.setDocId(56);
			submitNewDocDTO.setIsInterveneApproved("P");
			submitNewDocDTO.setCaseAccessRequestStatus("P");
		} else if ("minute-entry".equalsIgnoreCase(attachmentType)) {
			submitNewDocDTO.setProtestId(protestInfo.getA_No());
			submitNewDocDTO.setDocId(157);
			submitNewDocDTO.setIsDocConfidential("N");
			typeOfDoc = "Minute Entry";
			userProtestRole = "GAO";
			submitNewDocDTO.setDocketEntryTitle("Minute Entry");
		} else if ("agency-rep-request".equalsIgnoreCase(attachmentType)) {
			typeOfDoc = "Notice of ";
			userProtestRole = "AGENCY";
			submitNewDocDTO.setDocDescFiller("Appearance");
			/*submitNewDocDTO.setDocDescFiller(null);*/
			submitNewDocDTO.setDocId(79);
			submitNewDocDTO.setCaseAccessRequestStatus("P");
		} else if (docTypeId == 160 || docTypeId == 161) {
			submitNewDocDTO.setDocDescFiller(null); //Set document description filler to null in case of "Denial of Request to Intervene" OR "Denial of Notice of Appearance"
			userProtestRole = "GAO";
			typeOfDoc = caseDocketSheetService.getTypeOfDocFromDocId(docTypeId);
			if (null != content) {
				EPDS_FileUtils.saveTemplateDocument(request,
						submitNewDocDTO, attachmentType);
			}
		} else {

			if (null != content) {
				EPDS_FileUtils.saveTemplateDocument(request,
						submitNewDocDTO, attachmentType);
			}

			Map<Integer, String> doc_InfoMap = (Map<Integer, String>) EpdsSession.getAttribute(request, "doc_InfoMap");

			typeOfDoc = doc_InfoMap.get(submitNewDocDTO.getDocId());
			userProtestRole = protestInfo.getRole();
			companyName = protestInfo.getCompanyNameUserRepresentingTo();
		}

		submitNewDocDTO.setSubmissionDate(Date_Util.getCurrentDate());
		if ("intervene".equals(attachmentType)  
				&& role == GAO_ADMIN
				&& submitNewDocDTO.getIntervenorEmailAddress() != null) {
			User_Info intervenorUserInfo = userInfoService.getUser_InfoByEmail(submitNewDocDTO.getIntervenorEmailAddress());
			submitNewDocDTO.setUser_Id(intervenorUserInfo.getUser_Id());
		}else{
			submitNewDocDTO.setUser_Id(user_Info.getUser_Id());
		}
		
		submitNewDocDTO.setTypeofdocument(typeOfDoc);
		submitNewDocDTO.setUser_Role(userProtestRole);
		submitNewDocDTO.setCompany_Name(companyName);

		if (logger.isDebugEnabled()){
			logger.debug("addAttachments attachmentType = {}, submitNewDocDTO = {}",attachmentType, submitNewDocDTO);
		}

		
		boolean canSubmitReqToInterve = role == GAO_ADMIN || role == PROTESTER;
		
		if(typeOfDoc.toLowerCase(Locale.ENGLISH)
				.contains("Supplemental Protest".toLowerCase(Locale.ENGLISH)) && !canSubmitReqToInterve){
			throw new IllegalAccessError("Request cannot be completed!!");
		}
		
		protestInfoService.saveFilesToDB(submitNewDocDTO, request);

		return map;


	}



	/**
	 * It is used to download files from Document View Page
	 * @param request
	 * @param map
	 * @param response
	 * @param fileId
	 * @param redirecmap
	 * @throws Exception
	 */
	@RequestMapping(value = "/downloaddashboardfile", method = RequestMethod.GET)
	public void downloadFiles(HttpServletRequest request, ModelMap map,
			HttpServletResponse response,
			@RequestParam("fileId") int fileId,
			RedirectAttributes redirecmap) throws Exception {

		if (logger.isDebugEnabled()){
			logger.debug("downloadFiles fileId = {}",fileId);
		}
		EPDS_FileUtils fileUtils = new EPDS_FileUtils();

		ServletOutputStream outputStream = response.getOutputStream();
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request,
				"user_Info");

		File_Info file_Info = protestInfoService.getFileInfoByFileId(fileId);
		File file = null;

		if (file_Info != null){
			
			
			
			Protest_Info protest_Info = dashboardService.getProtestInfoBasedOnUserIdAndANo(user_Info, file_Info.getA_No());
			
			//adding an extra check to make sure even if the user has access to the but if this file is not visible Ex: Admitted to PO? 
			boolean isThisFileVisible = "Y".equalsIgnoreCase(dashboardService.checkIfThisFileIsVisible(file_Info,protest_Info,user_Info.getUser_Id()));
			
			
			dashboardService.validateAccess(user_Info, protest_Info, file_Info);
			
			if (!isThisFileVisible){
				
				throw new IllegalAccessError("Unauthorized!!");
			}
			
			
			
			file = new File(file_Info.getFile_Path());
	        String mimeType= URLConnection.guessContentTypeFromName(file.getName());

	        if(mimeType==null){
	            System.out.println("mimetype is not detectable, will take default");
	            mimeType = "application/octet-stream";
	        }

	        System.out.println("mimetype : "+mimeType + "fileName" + file.getName());

	        response.setContentType(mimeType);



	        /* "Content-Disposition : inline" will show viewable types [like images/text/pdf/anything viewable by browser] right on browser
	            while others(zip e.g) will be directly downloaded [may provide save as popup, based on your browser setting.]*/
	        /*response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() +"\""));*/

	        /* "Content-Disposition : attachment" will be directly download, may provide save as popup, based on your browser setting*/
	        response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));

			fileUtils.downloadOtherFiles(file, outputStream);


	    }


	}



	/**
	 * Admit Parties to Protective Order (PO)
	 * @param request
	 * @param map
	 * @param userId
	 * @param shouldAdmit
	 * @throws Exception
	 */
	@RequestMapping(value = "/admit-to-po", method = RequestMethod.POST)
	public void admitToPO(HttpServletRequest request, ModelMap map,
			@RequestParam("userId") String userId,
			@RequestParam("shouldAdmit") String shouldAdmit,
			@RequestParam("aNum") String aNum) throws Exception {

		

		if (!userId.matches(DTOValidator.INTEGER_PATTERN)
				|| !shouldAdmit.matches(DTOValidator.ALPHA_PATTERN)){
			throw new IllegalArgumentException("Invalid Input format");
		}
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request, "user_Info");
		UserRoles role = UserRoles.getByCode(user_Info.getRole_id());

		
		Protest_Info protest_Info = dashboardService.getProtestInfoBasedOnUserIdAndANo(user_Info, aNum);

		if (logger.isDebugEnabled()){
			logger.debug("/admit-to-po a_No = {}",aNum);
		}
		
		
		if (role == GAO_ADMIN  || role == GAO_SUPERVISOR || role == GAO_ATTORNEY){
			if (!protest_Info.isViewOnly()){
				protestInfoService.admitToPO(userId, shouldAdmit, aNum);
			}
		}else{
			throw new IllegalAccessError("Unauthorized!!");
		}
		
		
		
	}


	/**
	 * Case Status changes to complete whenever 60 Days has been passed since public decision is issued.
	 * GAO User Completes the case and ceate zip file which consist of offline case docket sheet to store in
	 * GAO Docket Management System.
	 *
	 * Amer : 11/27/17 review to code ...I think we can refactor this code so that we can just calculate consolidated protestInfo list which will have all the protests including the current protest
	 *
	 * also this will be very time consuming process so we need to implement multi threading so it happens in the back ground
	 *
	 * or do it asynchronously
	 * @param map
	 * @param request
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/case-completed", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON )
	public @ResponseBody ModelMap markCaseAsCompletedAndMakeAZipFile(ModelMap map,
			HttpServletRequest request, HttpServletResponse response, @RequestParam("aNum") String aNum) throws Exception {

		
		if (!aNum.matches(DTOValidator.PROTEST_ID_PATTERN)){
			map.addAttribute("inputErrors", new ArrayList<String>(Arrays.asList("protestId is invalid")));

			return map;
		}
		
		
		
		
		Protest_Info protest_Info = null;
		List<Protest_Info> consolidatedProtestInfoList = new ArrayList<Protest_Info>();
		List<File_Info> fileInfoList = new ArrayList<File_Info>();
		List<String> intervenorCompanyNameList = new ArrayList<String>();

		SubmitNewDocDTO submitNewDocDTO = new SubmitNewDocDTO();

		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request, "user_Info");
		protest_Info = dashboardService.getProtestInfoBasedOnUserIdAndANo(user_Info, aNum);
		User_Info attorney_Info = dashboardService.getAttorneyInfo(protest_Info.getA_No());

		if (logger.isDebugEnabled()){
			logger.debug("/case-completed a_No = {}", protest_Info.getA_No());
		}

		if (!user_Info.getRole_id().equals(GAO_ADMIN.getCode())){
			throw new IllegalAccessError("Unauthorized!!");
		}

		consolidatedProtestInfoList = protest_Info.getListOf_ConsolidatedProtest_Info();//protestInfoService.getConsolidatedProtestInfoListWithParentAndChildSupplementalProtests(protest_Info.getA_No());

		//consolidatedProtestInfoList.remove(0);// the first element is the protest info itself ....for now we will do it like but I think it will be better to just iterate through the list and do it all at once

		// parent case
		fileInfoList = dashboardService.getFileInfoList(protest_Info, user_Info.getUser_Id(),true);

		intervenorCompanyNameList = dashboardService.getIntervenorCompanyNameList(protest_Info.getA_No());

		consolidatedProtestInfoList = homeService
				.fillUpEachProtestInfoWithAgencyName(protest_Info
						.getListOf_ConsolidatedProtest_Info());

		submitNewDocDTO = ZipFile_Util.insertAllDataAndCreateTheFinalZipFile(
				request, intervenorCompanyNameList,
				consolidatedProtestInfoList, protest_Info,
				user_Info.getUser_Id(), fileInfoList, attorney_Info,
				new SubmitNewDocDTO(),caseDocketSheetService,protestInfoService, dashboardService);

		protestInfoService.saveFilesToDB(submitNewDocDTO, request);

		// child cases
		for (Protest_Info eachProtestInfo : consolidatedProtestInfoList) {


			Protest_Info parentProtestInfo = new Protest_Info();
			parentProtestInfo.setA_No(protest_Info.getA_No());
			parentProtestInfo.setB_No(protest_Info.getB_No());
			parentProtestInfo.setCompany_Name(protest_Info.getCompany_Name());

			ListIterator<Protest_Info> eachProtestInfoConsolidatedProtestInfoListItr = consolidatedProtestInfoList
					.listIterator();

			eachProtestInfoConsolidatedProtestInfoListItr
					.add(parentProtestInfo);

			while (eachProtestInfoConsolidatedProtestInfoListItr.hasNext()) {
				Protest_Info eachProtest = eachProtestInfoConsolidatedProtestInfoListItr
						.next();
				if (eachProtest.getA_No().equalsIgnoreCase(
						eachProtestInfo.getA_No())) {
					eachProtestInfoConsolidatedProtestInfoListItr.remove();
				}
			}

			intervenorCompanyNameList = dashboardService
					.getIntervenorCompanyNameList(eachProtestInfo.getA_No());

			fileInfoList = dashboardService.getFileInfoList(eachProtestInfo,
					user_Info.getUser_Id(),false);

			eachProtestInfo = dashboardService.getProtestInfoBasedOnUserIdAndANo(user_Info, eachProtestInfo.getA_No());

			submitNewDocDTO = ZipFile_Util
					.insertAllDataAndCreateTheFinalZipFile(request,
							intervenorCompanyNameList,
							consolidatedProtestInfoList, eachProtestInfo,
							user_Info.getUser_Id(), fileInfoList,
							attorney_Info, new SubmitNewDocDTO(),caseDocketSheetService,protestInfoService, dashboardService);

			caseDocketSheetService.addDmInfo(eachProtestInfo, user_Info);

			protestInfoService.saveFilesToDB(submitNewDocDTO, request);
		}

		caseDocketSheetService.addDmInfo(protest_Info, user_Info);
		map.addAttribute("isSuccess", true);

		return map;
	}

	/**
	 * Start Pay.gov Online Collection
	 * @param map
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping(value = "/startPayDotGovOnlineCollection", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON )
	public @ResponseBody ModelMap startPayDotGovOnlineCollection(ModelMap map,
			HttpServletRequest request,@RequestParam("a_No") String a_No) throws Exception {
		HttpsURLConnection payDotGovURL_connection = null;


		if (!a_No.matches(DTOValidator.PROTEST_ID_PATTERN)){
			map.addAttribute("inputErrors", new ArrayList<String>(Arrays.asList("protestId is invalid")));

			return map;
		}
		map.addAttribute("a_No",a_No);
		logger.info("/startPayDotGovOnlineCollection a_No = {}",a_No);

		try {
			payDotGovURL_connection = Protest_info_util
					.getPayDotGovURL_connection(request.getServletContext()
							.getRealPath(Params.payDotGovCertRelativePath));


			PayDotGovUtil.startOnlineCollectionRequest(map, request,protestInfoService);

			EpdsSession.setAttribute(request, "payDotGovTransactionANum",a_No);

		} catch (TCSServiceFault_Exception e) {
			
			e.printStackTrace();
			logger.error("Pay.gov return error code ---->" + e.getFaultInfo().getReturnCode());
			logger.error("Pay.gov return Detail ---->" + e.getFaultInfo().getReturnDetail());
			
			
			Map<String, Object> errorMap = new HashMap<String,Object>();
			errorMap.put("errorCode", e.getFaultInfo().getReturnCode());
			errorMap.put("errorDetail", e.getFaultInfo().getReturnDetail());
			map.addAttribute("payDotGovError", errorMap);
		}catch (Exception e) {
			
			logger.error("Error occured in startPayDotGovOnlineCollection" + e.getMessage());
			e.printStackTrace();
			
		} finally {
			if (payDotGovURL_connection != null) {
				payDotGovURL_connection.disconnect();
			}
		}

		EpdsSession.setAttribute(request, "payDotGovTransactionANum",a_No);

		return map;
	}


	/**
	 *
	 * we are using localstorage and url params to get the aNum and token but for any reason if it is not passed from UI because of browser we will get it from EpdsSession object
	 * Check Pay.gov transaction status
	 *
	 *one user will always file one protest at a time. from with in the same browser
	 * @param map
	 * @param request
	 * @param token
	 * @throws Exception
	 */
	@RequestMapping(value = "/check-transaction-status", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON )
	public @ResponseBody ModelMap checkTransactionStatus(ModelMap map,
			HttpServletRequest request, @RequestParam(value="token", required= false) String token,@RequestParam(value="a_Num", required= false) String a_Num)
			throws Exception {
		HttpsURLConnection payDotGovURL_connection = null;


		String payDotGovToken = (String) EpdsSession.getAttribute(request, "payDotGovToken");

		if (token == null){
			token  =  payDotGovToken;
		}


		if (a_Num == null){

			a_Num = (String) EpdsSession.getAttribute(request, "payDotGovTransactionANum");
		}

		logger.info("/check-transaction-status a_No = {}",a_Num);



		try {
			payDotGovURL_connection = Protest_info_util.getPayDotGovURL_connection(request.getServletContext()
							.getRealPath(Params.payDotGovCertRelativePath));

			PayDotGovUtil.checkPayDotGovTransactionStatus(map, request, token);

		} catch (TCSServiceFault_Exception e) {
			
			e.printStackTrace();
			logger.error("Pay.gov return error code ---->" + e.getFaultInfo().getReturnCode());
			logger.error("Pay.gov return Detail ---->" + e.getFaultInfo().getReturnDetail());
			
			map.addAttribute("isPaymentSuccess", "N");
			Map<String, Object> errorMap = new HashMap<String,Object>();
			errorMap.put("errorCode", e.getFaultInfo().getReturnCode());
			errorMap.put("errorDetail", e.getFaultInfo().getReturnDetail());
			map.addAttribute("payDotGovError", errorMap);
		}catch (Exception e) {
			e.printStackTrace();
			map.addAttribute("isPaymentSuccess", "N");
		} finally {
			if (payDotGovURL_connection != null) {
				payDotGovURL_connection.disconnect();
			}
		}

		String agencyId = (String) EpdsSession.getAttribute(request, "agency_tracking_id");
		String trackingId = (String) EpdsSession.getAttribute(request, "payDotGovTrackingId");
		String transactionStatus = (String) map.get("transactionStatus");
		String isPaymentSuccess = (String) map.get("isPaymentSuccess");
		//String error = (String) map.get("error");

		String transInfo = String.format("Pay.gov transaction status: \"%s\", isPaymentSuccess: \"%s\", agency_tracking_id: \"%s\", payDotGovTrackingId:  \"%s\", error: \"%s\"",
		        transactionStatus, isPaymentSuccess, agencyId, trackingId, "");
		logger.info(transInfo);

	    protestInfoService.updatePayDotGovResults(a_Num, agencyId, trackingId, transactionStatus,request);

		return map;
	}


	/**
	 * Test Pay.gov Online Collection
	 * @param map
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping(value = "/testPayDotGov", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON )
	public @ResponseBody ModelMap TestPayDotGovConnection(ModelMap map,
			HttpServletRequest request) throws Exception {


		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request,
				"user_Info");

		if (logger.isDebugEnabled()){
			logger.debug("/TestPayDotGovConnection a_No = {}",Util.getRemoteIp(request));
		}

		if (null != user_Info){
			map.addAttribute("isSuccess",true);
//			PayDotGovUtil.testPayDotGovConnection(map, request,protestInfoService);
		}

		return map;
	}

	/**
	 * Get User Info By Email From Session Object...
	 *
	 * @param map
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping(value = "/protestInfo/{aNum:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON )
	public @ResponseBody ModelMap getUserInfoByEmail(ModelMap map,
			HttpServletRequest request,@PathVariable ("aNum") String aNum) throws Exception {


		if (logger.isDebugEnabled()){
			logger.debug("/userInfo");
		}

		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request, "user_Info");
		Protest_Info protest_Info = dashboardService.getProtestInfoBasedOnUserIdAndANo(user_Info, aNum);

		if (user_Info.getRole_id().equals(GAO_ADMIN.getCode())){
			map.addAttribute("protestInfo", protest_Info);	
		}

		return map;
	}
	
	/**
	 * Get Protest Info By ANum 
	 *
	 * @param map
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping(value = "/userInfoByEmail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON )
	public @ResponseBody ModelMap getUserInfoByEmail(ModelMap map,
			HttpServletRequest request,@RequestBody LoginDTO loginDTO) throws Exception {


		if (logger.isDebugEnabled()){
			logger.debug("/userInfo");
		}

		User_Info user_Info = userInfoService.getUser_InfoByEmail(userInfoService.getEmail(loginDTO).getEmail());


		map.addAttribute("user_Info", user_Info);
		return map;
	}


	/**
	 * Get get list Of protests for Agency Rep Access to submit Notice Of Appearance
	 * @param request
	 * @param map
	 * @param bNo
	 * @throws Exception
	 */
	@RequestMapping(value = "/get-list-of-protests-for-agency-rep-access", method = RequestMethod.POST)
	public @ResponseBody ModelMap getListOfProtestsForAgencyRepAccessRequest(
			HttpServletRequest request, ModelMap map,
			@RequestParam("bNo") String bNo) throws Exception {

		bNo = Util.getBNumberWithBDashPrefix(bNo);


		if (!bNo.matches(DTOValidator.PROTEST_ID_PATTERN)){
			map.addAttribute("inputErrors", new ArrayList<String>(Arrays.asList("protestId is invalid")));

			return map;
		}

		if (logger.isDebugEnabled()){
			logger.debug("/get-list-of-protests-for-agency-rep-access b_No = {}",bNo);
		}

		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request, "user_Info");
		UserRoles role = UserRoles.getByCode(user_Info.getRole_id());

		if (role == AGENCY_ADMIN || role == AGENCY_ATTORNEY){
			@SuppressWarnings("unchecked")
			Map<String, Protest_Info> protest_InfoMap = (Map<String, Protest_Info>) EpdsSession.getAttribute(request, "protest_InfoMap");

			List<Protest_Info> protestInfoList = protestInfoService
					.getListOfProtestInfoAgencyRepCanRequestToAccess(bNo,
							user_Info.getFirm_id(), user_Info.getUser_Id(),protest_InfoMap);

			if (protestInfoList != null) {
				protestInfoList = homeService
						.fillUpEachProtestInfoWithAgencyName(protestInfoList);
				 protest_InfoMap = homeService
						.getProtestInfoMap(protestInfoList);


				protestInfoList = homeService.assignParentAndChildRelation(
						protest_InfoMap, protestInfoList, "");

				map.addAttribute("protestInfoList", protestInfoList);
				map.addAttribute("role", user_Info.getRole_id());

			}else{
				map.addAttribute("protestInfoList", new ArrayList<Protest_Info>());
			}
		}

		return map;
	}


	/**
	 *
	 * Get get list Of protests for Intervenor  to submit Request to Intervene
	 * @param request
	 * @param map
	 * @param bNo
	 * @throws Exception
	 * 
	 * 
	 */
	@RequestMapping(value = "/get-list-of-protests-for-intervenor-access", method = RequestMethod.POST)
	public @ResponseBody ModelMap getListOfProtestsForIntervenorAccess(
			HttpServletRequest request, ModelMap map,
			@RequestParam("bNo") String bNo) throws Exception {
		bNo = Util.getBNumberWithBDashPrefix(bNo);

		if (!bNo.matches(DTOValidator.PROTEST_ID_PATTERN)){
			map.addAttribute("inputErrors", new ArrayList<String>(Arrays.asList("protestId is invalid")));

			return map;
		}

		
		if (bNo.length() < 6){
			return map.addAttribute("inputErrors", new ArrayList<String>(Arrays.asList("B# has to be at least 6 digits B-XXXXXX.")));
		}
		if (logger.isDebugEnabled()){
			logger.debug("/get-list-of-protests-for-intervenor-access b_No = {}",bNo);
		}
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request, "user_Info");
		UserRoles role = UserRoles.getByCode(user_Info.getRole_id());
		
		if (role == PROTESTER || role == SECONDARY_PROTESTER || role == SECONDARY_INTERVENOR || role == INTERVENOR){
			@SuppressWarnings("unchecked")
			Map<String, Protest_Info> protest_InfoMap = (Map<String, Protest_Info>) EpdsSession.getAttribute(request, "protest_InfoMap");

			List<Protest_Info> protestInfoList = protestInfoService
					.getListOfProtestInfointervenorCanRequestToAccess(bNo, user_Info.getUser_Id(),protest_InfoMap);


			if (protestInfoList != null) {
				protestInfoList = homeService
						.fillUpEachProtestInfoWithAgencyName(protestInfoList);
				 protest_InfoMap = homeService
						.getProtestInfoMap(protestInfoList);


				protestInfoList = homeService.assignParentAndChildRelation(
						protest_InfoMap, protestInfoList, "");

				map.addAttribute("protestInfoList", protestInfoList);
				map.addAttribute("role", user_Info.getRole_id());

			}else{
				map.addAttribute("protestInfoList", new ArrayList<Protest_Info>());
			}
		}

		return map;
	}

}
