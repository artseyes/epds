package gov.gao.epds.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import gov.gao.epds.dto.AddAttorneyNote;
import gov.gao.epds.dto.CompanyInfo;
import gov.gao.epds.dto.DTOValidator;
import gov.gao.epds.dto.EditFileInfo;
import gov.gao.epds.dto.EditPartyInfo;
import gov.gao.epds.dto.EditProtestInfo;
import gov.gao.epds.dto.InputValidationError;
import gov.gao.epds.dto.SubmitNewDocDTO;
import gov.gao.epds.dto.TemplateDataDTO;
import gov.gao.epds.dto.ViewCaseDocketFileInfo;
import gov.gao.epds.enums.UserRoles;
import static gov.gao.epds.enums.UserRoles.*;
import gov.gao.epds.exception.InvalidInputException;
import gov.gao.epds.persistence.entity.Doc_Info;
import gov.gao.epds.persistence.entity.File_Info;
import gov.gao.epds.persistence.entity.Protest_Info;
import gov.gao.epds.persistence.entity.Tier_1_Agency;
import gov.gao.epds.persistence.entity.User_Info;
import gov.gao.epds.rest.auth.services.AuthUtil;
import gov.gao.epds.service.CaseDocketSheetService;
import gov.gao.epds.service.DashboardService;
import gov.gao.epds.service.ProtestInfoService;
import gov.gao.epds.service.UserInfoService;
import gov.gao.epds.session.EpdsSession;
import gov.gao.epds.utils.Date_Util;
import gov.gao.epds.utils.EPDS_FileUtils;
import gov.gao.epds.utils.templates.HtmlToPDF;
import gov.gao.epds.utils.templates.TemplateUtils;

@Controller
public class CaseDocketSheetController { // NO_UCD (unused code)
	@Autowired
	private CaseDocketSheetService caseDocketService;

	@Autowired
	private ProtestInfoService protestInfoService;
	
	@Autowired
	private UserInfoService userInfoService;
	
	@Autowired
	private DashboardService dashboardService;
	

	private final static Logger logger = LoggerFactory.getLogger(CaseDocketSheetController.class);

	/**
	 * Redirects to submit new document form
	 * 
	 * @param request
	 * @param map
	 * @param redirecmap
	 * @throws Exception
	 */
	@RequestMapping(value = "/submit-new-doc-form", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody ModelMap viewSubmitNewDocForm(HttpServletRequest request, ModelMap map,
			RedirectAttributes redirecmap,@RequestParam("aNum") String aNum) throws Exception {

		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request, "user_Info");
		
		Protest_Info protestInfo = dashboardService.getProtestInfoBasedOnUserIdAndANo(user_Info, aNum);
		
		if (logger.isDebugEnabled()) {
			logger.debug("viewSubmitNewDocForm a_No={}", protestInfo.getA_No());
		}

		
		if (protestInfo.isViewOnly()){
			
			throw new IllegalAccessError("Request cannot be completed!!!");
		}
		EpdsSession.removeAttribute(request, "filePathList");
		
		User_Info attorney_Info = (User_Info) EpdsSession.getAttribute(request, "attorney_Info");

		List<Doc_Info> doc_InfoList = caseDocketService.getDoc_InfoList(protestInfo);
		EpdsSession.setAttribute(request, "doc_InfoMap", caseDocketService.getDoc_InfoMap(doc_InfoList));

		map.addAttribute("submitNewDocDTO", new SubmitNewDocDTO());
		map.addAttribute("userProfileInfo", user_Info);
		map.addAttribute("protestInfo", protestInfo);
		map.addAttribute("doc_InfoList", doc_InfoList);
		map.addAttribute("attorneyInfo", attorney_Info);

		return map;

	}
	
	
	/**
	 * Redirects to submit new document form
	 * 
	 * @param request
	 * @param map
	 * @param aNum
	 * @throws Exception
	 */
	@RequestMapping(value = "/download-offline-cds/{aNum:.+}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody ModelMap downloadOfflineCds(HttpServletRequest request, ModelMap map,
			@PathVariable("aNum") String aNum,HttpServletResponse response) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("/download-offline-cds/ a_No={}", aNum);
		}
		
		User_Info loginUserInfo = (User_Info) EpdsSession.getAttribute(request, "user_Info");
		Protest_Info protestInfo = dashboardService.getProtestInfoBasedOnUserIdAndANo(loginUserInfo, aNum);

		dashboardService.validateAccess(loginUserInfo, protestInfo);
		
		ServletOutputStream outputStream = response.getOutputStream();
		EPDS_FileUtils fileUtils = new EPDS_FileUtils();
		File inputFile = caseDocketService.downloadOfflineCds(request, aNum);
		
		

		response.setContentType("application/pdf");
		response.setHeader("Content-disposition", "inline; filename=\""+ inputFile.getName() + "\"");

		String mimeType= URLConnection.guessContentTypeFromName(inputFile.getName());

        response.setContentType(mimeType);
        response.setHeader("Content-Disposition", String.format("inline; filename=\"" + inputFile.getName() +"\""));
        response.setContentLength((int)inputFile.length());

        fileUtils.downloadFiles(inputFile, outputStream);
        //inputFile.delete();
		return map;

	}


	

	/**
	 * Redirects to parties page.
	 * 
	 * @param request
	 * @param map
	 * @param redirecmap
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/view-manage-parties")
	public @ResponseBody ModelMap viewManageOrParties(HttpServletRequest request,
			HttpServletResponse response, ModelMap map,
			RedirectAttributes redirecmap, @RequestParam("aNum") String aNum) throws Exception {

		User_Info loginUserInfo = (User_Info) EpdsSession.getAttribute(request, "user_Info");
		
		Protest_Info protestInfo = dashboardService.getProtestInfoBasedOnUserIdAndANo(loginUserInfo, aNum);

		dashboardService.validateAccess(loginUserInfo, protestInfo);
		
		
		User_Info attorney_Info = dashboardService.getAttorneyInfo(protestInfo.getA_No());
		if (logger.isDebugEnabled()) {
			logger.debug("viewManageOrParties a_No={}", protestInfo.getA_No());
		}

		List<User_Info> user_InfoList = caseDocketService.getUserInfoListByANum(protestInfo.getA_No()); 
		
		if (user_InfoList != null && !user_InfoList.isEmpty()){
			user_InfoList = (List<User_Info>) protestInfoService.getUniqueList(user_InfoList, new User_Info());	
		}

		
		
		List<User_Info> protester_parties_list = caseDocketService.getProtester_parties_list(user_InfoList,aNum);

		List<List<User_Info>> listOfIntervenorParty = caseDocketService.getListOfIntervenorList(user_InfoList,aNum);

		List<CompanyInfo> orphanIntevenorCompanyInfoList = caseDocketService.getOrphanIntervenorCompanyInfoList(request,
				listOfIntervenorParty);

		caseDocketService.fillUpIntervenorCompanyInfo(request, listOfIntervenorParty);
		List<Integer> primaryAgencyInfoIds = dashboardService.getListOfAssociatedAgencyIdsByAgencyInfoId(protestInfo.getAgency_Info_Id(),true);
		protestInfo.setPrimaryAgencyInfoIds(primaryAgencyInfoIds);
		List<User_Info> primaryAgencyList = caseDocketService.getPrimaryAgencyList(user_InfoList, protestInfo,primaryAgencyInfoIds);

		List<User_Info> secondaryAgencyList = caseDocketService.getSecondaryAgencyList(user_InfoList, protestInfo,primaryAgencyInfoIds);

		map.addAttribute("attorneyInfo", attorney_Info);
		map.addAttribute("a_No", protestInfo.getA_No());
		map.addAttribute("role", protestInfo.getRole());
		map.addAttribute("user_Info", loginUserInfo);
		map.addAttribute("protestInfo", protestInfo);
		map.addAttribute("isViewOnly", protestInfo.isViewOnly());
		map.addAttribute("protester_parties_list", protester_parties_list);
		map.addAttribute("listOfIntervenorParty", listOfIntervenorParty);
		map.addAttribute("orphanIntComDetailList", orphanIntevenorCompanyInfoList);
		map.addAttribute("primary_agency_list", primaryAgencyList);
		map.addAttribute("primaryAgencyInfoIds", primaryAgencyInfoIds);
		map.addAttribute("secondary_agency_list", secondaryAgencyList);
		map.addAttribute("intervenorCompanyNameList", dashboardService.getIntervenorCompanyNameList(protestInfo.getA_No()));

		return map;

	}

	/**
	 * Redirects to Document View Page
	 * 
	 * @param map
	 * @param dto
	 * @param request
	 * @param redirectAttributes
	 * @throws Exception
	 */
	@RequestMapping(value = "/viewcasedocketFileInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody ModelMap viewCaseDocketFileInfo(ModelMap map,
			
			ViewCaseDocketFileInfo dto, HttpServletRequest request, RedirectAttributes redirectAttributes, HttpServletResponse response)
					throws Exception {

		List<InputValidationError> constraintViolations = AuthUtil.validateDTO(dto);

		if (!constraintViolations.isEmpty() && null != constraintViolations) {

			return map.addAttribute("inputErrors", constraintViolations);

		}
		
		
		
		EpdsSession.removeAttribute(request, "filePathList");
		
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request, "user_Info");
		

		Protest_Info protest_Info = dashboardService.getProtestInfoBasedOnUserIdAndANo(user_Info, dto.getProtestId());
		
		
		dashboardService.validateAccess(user_Info, protest_Info);
		
	
		
		
		String doc_Type_Name = caseDocketService.getDocTypeName(dto.getDoc_Type_Id());

		if (logger.isDebugEnabled()) {
			logger.debug("viewCaseDocketFileInfo a_No={},docTypeName = {}", dto.getProtestId(), doc_Type_Name);
		}

		List<File_Info> file_InfoList = caseDocketService
				.getFile_InfoListBasedOnSubmissionDateAndDocumentTypeAndANum(dto.getSubmissionDate(), dto.getDoc_Type_Id(),dto.getProtestId());
		dashboardService.populateFileInfoTransientAttributes(protest_Info, user_Info.getUser_Id(), false, file_InfoList);

		file_InfoList = EPDS_FileUtils.fillupEachFile_InfoWithFileName(file_InfoList);
		caseDocketService.indicateAsViewed(file_InfoList, dto.getFileAlert(), user_Info.getUser_Id());

		String po = EPDS_FileUtils.findPO(file_InfoList);


		map.addAttribute("caseStatus", protest_Info.getCase_Status());
		map.addAttribute("type_Of_Doc", doc_Type_Name);
		map.addAttribute("user_Info", user_Info);
		map.addAttribute("file_InfoList", file_InfoList);
		map.addAttribute("a_No", dto.getProtestId());
		map.addAttribute("PO", po);
		map.addAttribute("protestInfo", protest_Info);

		return map;

	}

	/**
	 * This is used when Request to Intervene / Join a Case is denied. User can
	 * access the Denial document from the Dashboard page. Retrieves denied
	 * request document.
	 * 
	 * @param map
	 * @param dto
	 * @param request
	 * @param redirectAttributes
	 * @throws Exception
	 */
	@RequestMapping(value = "/case-access-request-denied-file-info", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody ModelMap getFileInfoListWhenCaseAccessRequestDenied(ModelMap map,
			
			ViewCaseDocketFileInfo dto, HttpServletRequest request, RedirectAttributes redirectAttributes)
					throws Exception {

		List<InputValidationError> constraintViolations = AuthUtil.validateDTO(dto);
		List<File_Info> file_InfoList =null;
		if (!constraintViolations.isEmpty() && null != constraintViolations) {

			return map.addAttribute("inputErrors", constraintViolations);

		}

		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request, "user_Info");
		
		
		
		String doc_Type_Name = caseDocketService.getDocTypeName(dto.getDoc_Type_Id());

		if (logger.isDebugEnabled()) {
			logger.debug("getFileInfoListWhenCaseAccessRequestDenied a_No={},docTypeName = {}", dto.getProtestId(),
					doc_Type_Name);
		}
		
		
		
		List<Protest_Info> consolidatedProtestInfo = protestInfoService.getConsolidatedProtestInfoListWithParentAndChildSupplementalProtests(dto.getProtestId());
		
		for (Protest_Info eachProtestInfo : consolidatedProtestInfo){
		
			file_InfoList = caseDocketService
					.getFile_InfoListBasedOnSubmissionDateAndDocumentTypeAndANum(dto.getSubmissionDate(), dto.getDoc_Type_Id(),eachProtestInfo.getA_No());
			
			if (file_InfoList != null && !file_InfoList.isEmpty()){
				file_InfoList = EPDS_FileUtils.fillupEachFile_InfoWithFileName(file_InfoList);

				break;
			}

			
		}

		map.addAttribute("user_Info", user_Info);
		map.addAttribute("file_InfoList", file_InfoList);
		map.addAttribute("a_No", dto.getProtestId());

		return map;
	}


	/**
	 * Adding /Editing attorney note
	 * 
	 * @param modelMap
	 * @param dto
	 * @param request
	 * @throws Exception
	 */

	@RequestMapping(value = "add-attorney-note", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody ModelMap addOrChangeAttorneyNote(ModelMap modelMap,
			AddAttorneyNote dto, HttpServletRequest request) throws Exception {
		String gaoNote = dto.getNote();

		List<InputValidationError> constraintViolations = AuthUtil.validateDTO(dto);
		
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request, "user_Info");


        UserRoles role = UserRoles.getByCode(user_Info.getRole_id());
		if (role != GAO_ADMIN
				&& role !=GAO_ATTORNEY
				&& role !=GAO_SUPERVISOR){
			throw new IllegalAccessError("Unauthorized!!");
		}
		if (!constraintViolations.isEmpty() && null != constraintViolations) {

			return modelMap.addAttribute("inputErrors", constraintViolations);

		} else {
			String note = gaoNote + ":::" + Date_Util.getCurrentDate();

			
			caseDocketService.addOrEditAttorneyNoteInFileInfoRecord(dto.getFileId(), note, gaoNote, request);

			modelMap.addAttribute("note", note);
			modelMap.addAttribute("currentTime", Date_Util.getCurrentDate());
		}
		return modelMap;
	}
	
	
	

	/**
	 * Update Email Preferences
	 * 
	 * @param request
	 * @param yOrNValue
	 * @param map
	 * @throws Exception
	 */
	@RequestMapping(value = "update-email-preferences", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody ModelMap updateEmailPreferences(HttpServletRequest request,
			@RequestParam("value") String yOrNValue, ModelMap map, @RequestParam("aNum") String aNum) throws Exception {

		if (null == yOrNValue || yOrNValue.trim().length() != 1) {
			throw new IllegalArgumentException("Param is not valid");
		} else {

			User_Info user_Info = (User_Info) EpdsSession.getAttribute(request, "user_Info");
			Protest_Info protestInfo = dashboardService.getProtestInfoBasedOnUserIdAndANo(user_Info, aNum);

			if (logger.isDebugEnabled()) {
				logger.debug("updateEmailPreferences a_No={}", protestInfo.getA_No());
			}

			String a_No = protestInfo.getA_No();
			
			caseDocketService.updateCaseDocketEmailPreferences(a_No, user_Info, yOrNValue);
			
			if (protestInfo.getParent_A_No() == null || protestInfo.getParent_A_No().equalsIgnoreCase("")){
				
				for (Protest_Info eachProtestInfo : protestInfo.getListOf_ConsolidatedProtest_Info()){
					caseDocketService.updateCaseDocketEmailPreferences(eachProtestInfo.getA_No(), user_Info, yOrNValue);
				}
			}
			
			

			protestInfo.setCasedocket_email_preferences(yOrNValue);
			EpdsSession.setAttribute(request, "user_Info", user_Info);

			map.addAttribute("protestInfo", protestInfo);
			

		}

		return map;
	}

	/**
	 * Validate GC track DM#
	 * 
	 * @param request
	 * @param dmNumber
	 * @param map
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "validateDmInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody ModelMap checkInfDmInfoExists(HttpServletRequest request, @RequestParam("id") Integer dmNumber,@RequestParam(value = "aNum", required= false) String aNum,
			ModelMap map, HttpServletResponse response) throws Exception {

		
		
		if (null !=  aNum && !aNum.matches(DTOValidator.PROTEST_ID_PATTERN)){
			map.addAttribute("inputErrors", new ArrayList<String>(Arrays.asList("protestId is invalid")));

			return map;
		}
		
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request, "user_Info");

		if (logger.isDebugEnabled()) {
			logger.debug("validateDmInfo a_No={}", aNum);
		}

		String user_Role = (String) EpdsSession.getAttribute(request, "user_Role");
		if (user_Role.toUpperCase(Locale.ENGLISH).contains("GAO")) {

			Boolean flag = caseDocketService.checkIfDmNumberExists(dmNumber);
			map.addAttribute("isExists", flag);
		} else {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}

		return map;
	}

	/**
	 * 
	 * Verify GC Track DM#
	 * 
	 * @param request
	 * @param dmNumber
	 * @param map
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "verifyDmInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody ModelMap verifyDmInfo(HttpServletRequest request, @RequestParam("id") Integer dmNumber,
			ModelMap map, HttpServletResponse response, @RequestParam("aNum") String aNum) throws Exception {

		
		
		if (null !=  aNum && !aNum.matches(DTOValidator.PROTEST_ID_PATTERN)){
			map.addAttribute("inputErrors", new ArrayList<String>(Arrays.asList("protestId is invalid")));

			return map;
		}
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request, "user_Info");
		Protest_Info protestInfo = protestInfoService.getProtest_Info_ByA_No(aNum);

		if (logger.isDebugEnabled()) {
			logger.debug("verifyDmInfo a_No={}", protestInfo.getA_No());
		}

		String user_Role = (String) EpdsSession.getAttribute(request, "user_Role");

		if (user_Role.toUpperCase(Locale.ENGLISH).contains("GAO")) {

//			Boolean flag = caseDocketService.verifyDMInfo(protestInfo, dmNumber, user_Info);
			Integer dbDMNumber = caseDocketService.verifyDMInfo(protestInfo, dmNumber, user_Info);

			map.addAttribute("isEqual", dbDMNumber.equals(dmNumber));
			map.addAttribute("protestInfo", protestInfo);
			map.addAttribute("dbDMNumber", dbDMNumber);

		} else {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}

		
		return map;
	}

	/**
	 * Assign DM#
	 * 
	 * @param request
	 * @param dmNumber
	 * @param map
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "assignDmInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody ModelMap addDMInfo(HttpServletRequest request, @RequestParam("id") Integer dmNumber,
			ModelMap map, HttpServletResponse response, @RequestParam("aNum") String aNum) throws Exception {

		
		if (null !=  aNum && !aNum.matches(DTOValidator.PROTEST_ID_PATTERN)){
			map.addAttribute("inputErrors", new ArrayList<String>(Arrays.asList("protestId is invalid")));

			return map;
		}
		
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request, "user_Info");
		Protest_Info protestInfo = protestInfoService.getProtest_Info_ByA_No(aNum);

		if (logger.isDebugEnabled()) {
			logger.debug("assignDmInfo a_No={}", protestInfo.getA_No());
		}

		String user_Role = (String) EpdsSession.getAttribute(request, "user_Role");

		if (user_Role.toUpperCase(Locale.ENGLISH).contains("GAO")) {

			caseDocketService.assignDmInfo(protestInfo, dmNumber, user_Info);

			map.addAttribute("protestInfo", protestInfo);

		} else {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}

		return map;
	}

	/**
	 * Update DM #
	 * 
	 * @param request
	 * @param dmNumber
	 * @param map
	 * @throws Exception
	 */
	@RequestMapping(value = "updateDmInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody ModelMap updateDMInfo(HttpServletRequest request, @RequestParam("id") Integer dmNumber,
			ModelMap map,@RequestParam("aNum") String aNum) throws Exception {
		
		if (null !=  aNum && !aNum.matches(DTOValidator.PROTEST_ID_PATTERN)){
			map.addAttribute("inputErrors", new ArrayList<String>(Arrays.asList("protestId is invalid")));

			return map;
		}
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request, "user_Info");
		Protest_Info protestInfo = protestInfoService.getProtest_Info_ByA_No(aNum);

		if (logger.isDebugEnabled()) {
			logger.debug("updateDmInfo a_No={}", protestInfo.getA_No());
		}
		
		String user_Role = (String) EpdsSession.getAttribute(request, "user_Role");

		if (user_Role.toUpperCase(Locale.ENGLISH).contains("GAO")) {
			caseDocketService.assignDmInfo(protestInfo, dmNumber, user_Info);

		}
		map.addAttribute("protestInfo", protestInfo);

		return map;
	}

	/**
	 * Change Protest Info Attributes. All header values in the case docket
	 * sheet that can be edited
	 * 
	 * @param request
	 * @param typeOfchange
	 * @param changeProtestInfo
	 * @param map
	 * @throws Exception
	 */

	@RequestMapping(value = "/change-protest_Info-attribute/{typeOfchange}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody ModelMap changeProtest_Info_Attribute(HttpServletRequest request,
			@PathVariable("typeOfchange") String typeOfchange, EditProtestInfo changeProtestInfo, ModelMap map)
					throws Exception {
		
		List<String> bNumberList = new ArrayList<String>();
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request, "user_Info");
		if (null != changeProtestInfo.getListOfBNumbers()) {
			bNumberList = Arrays.asList(changeProtestInfo.getListOfBNumbers().split("\\s*,\\s*"));
		}

		Protest_Info protestInfo = dashboardService.getProtestInfoBasedOnUserIdAndANo(user_Info, changeProtestInfo.getaNum());
		
		if(protestInfo.isViewOnly()){
			throw new IllegalArgumentException("Error " + "You don't have access to this docket" );
		}

		if (logger.isDebugEnabled()) {
			logger.debug("changeProtest_Info_Attribute a_No={}, typeOfChange = {}", protestInfo.getA_No(),
					typeOfchange);
		}

		validFileInfoAttrsBasedOnTypeOfChange(changeProtestInfo.getNewValue(),typeOfchange);
		Protest_Info new_Protest_Info = null;

        UserRoles role = UserRoles.getByCode(user_Info.getRole_id());

        // most of these changes only GAO_ADMIN can do
        // exception is Protective Order, which GAO_ATTORNEY and GAO_SUPERVISOR can do
		if (role != GAO_ADMIN) {
            if ( !"Protective Order".equalsIgnoreCase(typeOfchange) || (role !=GAO_ATTORNEY && role !=GAO_SUPERVISOR) ) {
                throw new IllegalAccessError("Unauthorized!!");
            }
		}

		if ("B Number".equalsIgnoreCase(typeOfchange)) {

//			if (!changeProtestInfo.getNewValue().contains(".")){
//
//				if (!changeProtestInfo.getNewValue().matches(DTOValidator.PROTEST_ID_PATTERN)){
//					throw new IllegalArgumentException(changeProtestInfo.getNewValue() + "");
//				}
//				changeProtestInfo.setNewValue(changeProtestInfo.getNewValue() + ".1");
//			}
			Protest_Info protest_Info = protestInfoService.getProtestInfoByBNum(changeProtestInfo.getNewValue());

			if (null == protest_Info) {
				new_Protest_Info = caseDocketService.changeProtest_Info_Attribute(protestInfo.getA_No(), typeOfchange,
						changeProtestInfo.getNewValue(), changeProtestInfo.getNewValue2(), bNumberList,
						changeProtestInfo.getOldValue(), changeProtestInfo.getAgency_tier1(),
						changeProtestInfo.getAgency_tier2(), request, protestInfo);
			} else {
				map.addAttribute("isBNumberExists", "Y");
			}

		} else {
			new_Protest_Info = caseDocketService.changeProtest_Info_Attribute(protestInfo.getA_No(), typeOfchange,
					changeProtestInfo.getNewValue(), changeProtestInfo.getNewValue2(), bNumberList,
					changeProtestInfo.getOldValue(), changeProtestInfo.getAgency_tier1(),
					changeProtestInfo.getAgency_tier2(), request, protestInfo);
		}

		
		map.addAttribute("protestInfo", protestInfo);
		map.addAttribute("isSuccess", true);

		return map;
	}

	public void validFileInfoAttrsBasedOnTypeOfChange(String newValue,String typeOfChange) throws InvalidInputException{
	
		boolean isValid = false;
		
		if ("B Number".equalsIgnoreCase(typeOfChange) && null != newValue && !newValue.matches(DTOValidator.PROTEST_ID_PATTERN)){
			
		}else if ("type-of-document".equalsIgnoreCase(typeOfChange) && null != newValue && !newValue.matches(DTOValidator.TYPE_OF_DOCUMENT_PATTERN)){
			
		}else if ("confidential".equalsIgnoreCase(typeOfChange) && null != newValue && !newValue.matches(DTOValidator.IS_DOC_CONFIDENTIAL_PATTERN)){
			
		}else if ("comments".equalsIgnoreCase(typeOfChange) && null != newValue && !newValue.matches(DTOValidator.COMMENTS_PATTERN)){
			
		}/*else if ("submission Date".equalsIgnoreCase(typeOfChange) && null != newValue && !newValue.matches(DTOValidator)){
			isValid = false;
		}*/else if (("Protester Company Name".equalsIgnoreCase(typeOfChange) 
				|| "INTERVENOR Company Name".equalsIgnoreCase(typeOfChange)) && null != newValue 
				&& !newValue.matches(DTOValidator.NAME_OF_FIRM_PATTERN)){
			
		}else if ("Solicitation Number".equalsIgnoreCase(typeOfChange) && null != newValue && !newValue.matches(DTOValidator.SOLICITATION_PATTERN)){
		}else if ("Case Status".equalsIgnoreCase(typeOfChange) && null != newValue && !newValue.matches(DTOValidator.ALPHA_PATTERN)){
		}else if ("case Type".equalsIgnoreCase(typeOfChange) && null != newValue && !newValue.matches(DTOValidator.ALPHA_PATTERN)){
		}else if ("Company Status".equalsIgnoreCase(typeOfChange) && null != newValue && !newValue.matches(DTOValidator.ALPHA_PATTERN)){
		}/*else if ("joinCases".equalsIgnoreCase(typeOfChange) || "unJoinCases".equalsIgnoreCase(typeOfChange)){
			isValid = false;
		}*/else if ("Attorney Info".equalsIgnoreCase(typeOfChange) && null != newValue && !newValue.matches(DTOValidator.INTEGER_PATTERN)){
		}else if ("Company Status".equalsIgnoreCase(typeOfChange) && null != newValue && !newValue.matches(DTOValidator.NAME_OF_FIRM_PATTERN)){
		}else{
			isValid = true;
		}
		
		
		
		if (!isValid){
			throw new IllegalArgumentException(newValue + " ------ " + "Error " + typeOfChange + " is invalid input format!!" );	
		}
		
	}
	/**
	 * It is used to change the file info attributes in the case docket sheet
	 * like comments,typeOfDocument, change confidential status..
	 * 
	 * @param request
	 * @param typeOfchange
	 * @param changeFileInfo
	 * @throws Exception
	 */

	@RequestMapping(value = "/change-file-attribute/{typeOfchange}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public void changeFile_Info_Attribute(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("typeOfchange") String typeOfchange, EditFileInfo changeFileInfo) throws Exception {

		ModelMap model = new ModelMap();

		
		validFileInfoAttrsBasedOnTypeOfChange(changeFileInfo.getNewValue(),typeOfchange);
		String user_Role = (String) EpdsSession.getAttribute(request, "user_Role");

		if (user_Role.toUpperCase(Locale.ENGLISH).contains("GAO")) {


			if (logger.isDebugEnabled()) {
				logger.debug("change-file-attribute fileId={}", changeFileInfo.getFile_Id());
			}

			List<InputValidationError> constraintViolations = AuthUtil.validateDTO(changeFileInfo);

			if (!constraintViolations.isEmpty() && null != constraintViolations) {

				model.addAttribute("inputErrors", constraintViolations);

				throw new IllegalArgumentException("Input not valid");
			} else {
				caseDocketService.changeFile_Info(changeFileInfo.getFile_Id(), changeFileInfo.getNewValue(),
						typeOfchange, request);
			}

		} else {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
		
		

	}
	
	@RequestMapping(value = "/update-party-info", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public void updatePartyInfo(HttpServletRequest request, HttpServletResponse response, EditPartyInfo editPartyInfo)
			throws Exception {

		ModelMap model = new ModelMap();

		String user_Role = (String) EpdsSession.getAttribute(request, "user_Role");

		User_Info loggedInUserInfo = (User_Info) EpdsSession.getAttribute(request, "user_Info");

		Protest_Info protestInfo = dashboardService.getProtestInfoBasedOnUserIdAndANo(loggedInUserInfo,
				editPartyInfo.getaNum());
		dashboardService.validateAccess(loggedInUserInfo, protestInfo);

        UserRoles role = UserRoles.getByCode(loggedInUserInfo.getRole_id());
		if (role != GAO_ADMIN && role != GAO_SUPERVISOR && role != GAO_ATTORNEY) {

			throw new IllegalAccessError("Unauthorized!!");
		}

		// guaranteed to be one of the above: GAO_ADMIN, GAO_SUPERVISOR, GAO_ATTORNEY
		// if view only, only GAO_ADMIN
		if (protestInfo.isViewOnly() && role != GAO_ADMIN){
			throw new IllegalAccessError("Unauthorized!!");
		}

		if (user_Role.toUpperCase(Locale.ENGLISH).contains("GAO")) {

			if (logger.isDebugEnabled()) {
				logger.debug("change-file-attribute a_No={}", editPartyInfo.getaNum());
			}

			List<InputValidationError> constraintViolations = AuthUtil.validateDTO(editPartyInfo);

			if (!constraintViolations.isEmpty() && null != constraintViolations) {

				model.addAttribute("inputErrors", constraintViolations);

				throw new IllegalArgumentException("Input not valid");
			} else {
				caseDocketService.updatePartyInfo(editPartyInfo);
				model.addAttribute("isSuccess", true);
			}

		} else {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}

	}

	/**
	 * Retrieves list of GAO Users Consisting of Attorneys and Supervisor Only
	 * 
	 * @param request
	 * @param map
	 */
	@RequestMapping(value = "/get-attorney-list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody ModelMap getAttorneyList(HttpServletRequest request, ModelMap map,
			HttpServletResponse response) {

		String user_Role = (String) EpdsSession.getAttribute(request, "user_Role");

		if (user_Role.toUpperCase(Locale.ENGLISH).contains("GAO")) {

			List<User_Info> gao_User_Info_List = caseDocketService.getListOfGAOUserConsistOfAttorneyAndSupervisor();

			map.addAttribute("gao_User_Info_List", gao_User_Info_List);
		} else {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}

		return map;
	}

	/**
	 * Retirves list of all gao user's
	 * 
	 * @param request
	 * @param map
	 */
	@RequestMapping(value = "/get-gao-user-list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody ModelMap getGAOUsrList(HttpServletRequest request, ModelMap map,
			HttpServletResponse response) {

		String user_Role = (String) EpdsSession.getAttribute(request, "user_Role");

		if ( null != user_Role && user_Role.toUpperCase(Locale.ENGLISH).contains("GAO")) {
			List<User_Info> gao_User_Info_List = caseDocketService.getListOfAllGAOUsers();

			map.addAttribute("gao_User_Info_List", gao_User_Info_List);
		} else {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}

		return map;
	}

	/**
	 * Retrieves list of tier 1 agencies
	 * 
	 * @param map
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping(value = "/get-tier_1-agency-list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody ModelMap getTier1AgencyList(ModelMap map, HttpServletRequest request) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("/get-tier_1-agency-list");
		}

		List<Tier_1_Agency> tier_1_Agency_List = caseDocketService.getTier_1_Agency_List();
		if (tier_1_Agency_List != null) {
			map.addAttribute("AgencyTier1List", tier_1_Agency_List);
		}

		return map;

	}

	/**
	 * Validate if two cases can be joined
	 * 
	 * @param map
	 * @param request
	 * @param b_Num
	 * @throws Exception
	 */
	@RequestMapping(value = "/validate-join", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody ModelMap validateIfItCanBeJoined(ModelMap map, HttpServletRequest request,
			@RequestParam("b_Num") String b_Num,@RequestParam("parentBnum") String parentBNum) throws Exception {

		if (!b_Num.matches(DTOValidator.PROTEST_ID_PATTERN)) {
			map.addAttribute("inputErrors", new ArrayList<String>().add("Invalid input format!!"));

			return map;
		}

		User_Info loggedInUserInfo = (User_Info) EpdsSession.getAttribute(request, "user_Info");

		if (!loggedInUserInfo.getRole_id().equals(GAO_ADMIN.getCode())) {
			throw new IllegalAccessError("Unauthorized!!");
		}

		if (logger.isDebugEnabled()) {
			logger.debug("validateIfItCanBeJoined b_No={},parentBNum", b_Num,parentBNum);
		}

		if (parentBNum != null && !" ".equalsIgnoreCase(parentBNum)){
			String response = caseDocketService.validateForJoining(b_Num,parentBNum);
			map.addAttribute("response", response);
		}else{
			map.addAttribute("response", "cannot be joined");	
		}
		
		

		return map;
	}

	/**
	 * Retrives the HTML content of the template based on doc Id
	 * 
	 * @param map
	 * @param request
	 * @param docId
	 * @throws Exception
	 */
	@RequestMapping(value = "/templates", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody ModelMap getTemplates(ModelMap map, HttpServletRequest request,
			@RequestParam("docId") String docId,@RequestParam(value = "aNum", required = false) String aNum) throws Exception {

		if (!docId.matches(DTOValidator.INTEGER_PATTERN)) {
			map.addAttribute("inputErrors", new ArrayList<String>().add("Invalid input format!!"));

			return map;
		}

		User_Info loggedInUserInfo = (User_Info) EpdsSession.getAttribute(request, "user_Info");
        UserRoles role = UserRoles.getByCode(loggedInUserInfo.getRole_id());

        if (role != GAO_ADMIN && role != GAO_SUPERVISOR && role != GAO_ATTORNEY) {

			throw new IllegalAccessError("Unauthorized!!");
		}
		TemplateDataDTO templateDataDTO = caseDocketService.getTemplateDataDTO(request, docId,aNum);

		if (logger.isDebugEnabled()) {
			logger.debug("getTemplates docId={}", docId);
		}
		String content = TemplateUtils.getTheContentOfHtmlFile(request,templateDataDTO);
		map.addAttribute("protestInfo", templateDataDTO.getProtestInfo());
		map.addAttribute("attorneyInfo", templateDataDTO.getAttorneyInfo());
		map.addAttribute("consolidatedBnums", templateDataDTO.getConsolidateBNums());
		map.addAttribute("consolidatedProtesterNames", templateDataDTO.getConsolidatedProtesterNames());
		map.addAttribute("attorneyInfo", templateDataDTO.getAttorneyInfo());

		if (null != templateDataDTO.getProtestInfo()) {
			map.addAttribute("reportDueDate",
					Date_Util.agencyReportDueDate(templateDataDTO.getProtestInfo().getSubmission_Date(),templateDataDTO.getProtestInfo().getCase_Type()));
		}

		map.addAttribute("htmlContent", content);

		return map;
	}

	/**
	 * Create Temp PDF of the template for verifying before the final submission
	 * 
	 * @param map
	 * @param request
	 * @param docId
	 * @param content
	 * @throws Exception
	 */
	@RequestMapping(value = "/create-pdf", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody ModelMap createTempPDF(ModelMap map, HttpServletRequest request,
			@RequestParam("docId") String docId, @RequestParam("content") String content) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("createTempPDF docId={}", docId);
		}

		User_Info loggedInUserInfo = (User_Info) EpdsSession.getAttribute(request, "user_Info");
        UserRoles role = UserRoles.getByCode(loggedInUserInfo.getRole_id());

        if (role != GAO_ADMIN && role != GAO_SUPERVISOR && role != GAO_ATTORNEY) {
			throw new IllegalAccessError("Unauthorized!!");
		}

		if (!docId.matches(DTOValidator.INTEGER_PATTERN)) {
			map.addAttribute("inputErrors", new ArrayList<String>().add("Invalid input format!!"));

			return map;
		}

		// may be we need to implement more stringent whitelist
		content = TemplateUtils.cleanHtmlContentBeforeCreatingPdf(
				content,
				Whitelist.relaxed()
						.addAttributes(":all", "style")
						.preserveRelativeLinks(true).removeTags("code"));
		Map<String, String> mapOfFileMeta = TemplateUtils.getMapOfFileMetaData(docId);

		new HtmlToPDF().createTempPdf(request, content, mapOfFileMeta);
		map.addAttribute("fileName", mapOfFileMeta.get("fileName"));

		return map;
	}

	/**
	 * Downloading The temp PDF file which we create in the above method
	 * 
	 * @param map
	 * @param request
	 * @param response
	 * @param docId
	 * @throws Exception
	 */
	@RequestMapping(value = "/download-temp-pdf", method = RequestMethod.POST)
	public void downloadTempPdf(ModelMap map, HttpServletRequest request, HttpServletResponse response,
			@RequestParam("docId") String docId) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("downloadTempPdf docId={}", docId);
		}

		if (!docId.matches(DTOValidator.INTEGER_PATTERN)) {
			throw new IllegalArgumentException("Invalid Input format!!");

		}
		
		User_Info loggedInUserInfo = (User_Info) EpdsSession.getAttribute(request, "user_Info");
        UserRoles role = UserRoles.getByCode(loggedInUserInfo.getRole_id());

        if (role != GAO_ADMIN && role != GAO_SUPERVISOR && role != GAO_ATTORNEY) {
			throw new IllegalAccessError("Unauthorized!!");
		}
		
		Map<String, String> mapOfFileMeta = TemplateUtils.getMapOfFileMetaData(docId);
		String storagePath = request.getServletContext().getRealPath(mapOfFileMeta.get("tempStoragePath"));

		String fileName = mapOfFileMeta.get("fileName") + ".pdf";
		String fullPath = (String) EpdsSession.getAttribute(request, "tempPdfFilePath");

		ServletContext ctx = request.getServletContext();

		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

		InputStream fis = new FileInputStream(fullPath);
		String mimeType = ctx.getMimeType(fullPath);
		response.setContentType(mimeType != null ? mimeType : "application/octet-stream");

		ServletOutputStream os = response.getOutputStream();
		byte[] bufferData = new byte[1024];
		int read = 0;
		while ((read = fis.read(bufferData)) != -1) {
			os.write(bufferData, 0, read);
		}
		os.flush();
		os.close();
		fis.close();

	}

}
