package gov.gao.epds.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

import gov.gao.epds.dto.AdvanceSearchDTO;
import gov.gao.epds.dto.DTOValidator;
import gov.gao.epds.dto.DashboardPreferences;
import gov.gao.epds.dto.RemoveCaseDto;
import gov.gao.epds.enums.UserRoles;
import static gov.gao.epds.enums.UserRoles.*;
import gov.gao.epds.persistence.entity.File_Info;
import gov.gao.epds.persistence.entity.Protest_Info;
import gov.gao.epds.persistence.entity.Tier_1_Agency;
import gov.gao.epds.persistence.entity.User_Info;
import gov.gao.epds.persistence.entity.User_Protest_Role_Bridge;
import gov.gao.epds.service.DashboardService;
import gov.gao.epds.service.HomeService;
import gov.gao.epds.service.ProtestInfoService;
import gov.gao.epds.service.UserInfoService;
import gov.gao.epds.session.EpdsSession;
import gov.gao.epds.utils.Date_Util;
import gov.gao.epds.utils.ExportToExcel;
import gov.gao.epds.utils.GlobalParams;
import gov.gao.epds.utils.Util;
import gov.gao.epds.utils.templates.TemplateUtils;


/**
 * DashboardController contains list of resources for all the options shown in different dashboards
 * @author MHussaini
 *
 */
@Controller
public class DashboardController { // NO_UCD (unused code)

	private final static Logger logger = LoggerFactory
			.getLogger(DashboardController.class);
	@Autowired
	private DashboardService dashboardService;
	
	@Autowired
	private ProtestInfoService protestInfoService;
	
	@Autowired
	private UserInfoService userInfoService;
	
	@Autowired
	private HomeService homeService;

	/**
	 * Populate the data for File New Protest View. 
	 * @param map
	 * @param request
	 * @param typeOfProtest
	 * @param protestId
	 * @param redirectMap
	 * @throws Exception
	 */
	@RequestMapping(value = "/protest-request", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON )
	public @ResponseBody ModelMap getProtestInfoForm_View(
			ModelMap map,
			HttpServletRequest request,
			@RequestParam(value = "typeOfProtest", required = false) String typeOfProtest,
			@RequestParam(value = "protestId", required = false) String protestId,
			RedirectAttributes redirectMap) throws Exception {
		
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request,
				"user_Info");
		String aNum = Util.getA_Num();

        UserRoles role = UserRoles.getByCode(user_Info.getRole_id());
		if (role != GAO_ADMIN
				&&  role != PROTESTER
				&& "protest".equalsIgnoreCase(typeOfProtest)){
			throw new IllegalAccessError("The request cannot be completed!! ");
		}
		EpdsSession.removeAttribute(request, "filePathList");
		
		if ("protest".equalsIgnoreCase(typeOfProtest)) {
			List<Tier_1_Agency> tier_1_Agency_List = dashboardService
					.getTier_1_Agency_List();
			if (tier_1_Agency_List != null) {
				map.addAttribute("AgencyTier1List", tier_1_Agency_List);
			}
		} else {
			Protest_Info protest_Info = dashboardService.getProtestInfoBasedOnUserIdAndANo(user_Info, protestId);

			aNum = protestInfoService.getNewAnumForSecondaryProtest(protest_Info.getA_No());

            User_Protest_Role_Bridge uprb = dashboardService.checkIfUPRBExists(protest_Info, user_Info);
			protestInfoService.validateAccess(typeOfProtest, user_Info, protest_Info, uprb);
			
			List<String> intervenorCompanyNameList = dashboardService.getIntervenorCompanyNameList(protest_Info.getA_No());

			if (logger.isDebugEnabled()){
				logger.debug("/protest-request a_No={}, typeOfProtest = {}",
						protest_Info.getA_No(), typeOfProtest);
			}
			if("reconsideration".equalsIgnoreCase(typeOfProtest)){
				List<String> agencyNameList = new ArrayList<String>();
				
				agencyNameList.add(protest_Info.getAgency_Name());
				
				List<User_Info> secondaryAgencyUserInfoList = userInfoService.getSecondaryAgencyUserInfoList(protest_Info);
				
				if (secondaryAgencyUserInfoList != null && secondaryAgencyUserInfoList.size() > 0){
					agencyNameList.add(secondaryAgencyUserInfoList.get(0).getFirm_Name());
				}
				
				map.addAttribute("agencyNameList", agencyNameList);
			}
			
			String userProtestRole = protest_Info.getRole();
			map.addAttribute("intervenorCompNameList", intervenorCompanyNameList);
			map.addAttribute("typeOfProtest", typeOfProtest);
			map.addAttribute("protestInfo", protest_Info);
			map.addAttribute("role", userProtestRole);
		}

		map.addAttribute("user_Info", user_Info);
		
		map.addAttribute("user_Role",
				(String) EpdsSession.getAttribute(request, "user_Role"));

		// need to check both at same time, if global check separate, might generate an existing A#
		while( (protestInfoService.getProtest_Info_ByA_No(aNum) != null) || GlobalParams.aNumbersSet.contains(aNum) ) {
			aNum = Util.getA_Num();
		}

		EpdsSession.setAttribute(request, "a_No", aNum);
		
		map.addAttribute("aNum", aNum);
		GlobalParams.aNumbersSet.add(aNum);
		
		return map;
	}

	/**
	 * Populate Data and redirect to Request to Intervene/Join A Case View
	 * 
	 * @param request
	 * @param map
	 * @param redirecmap
	 * @throws Exception
	 */
	
	@RequestMapping(value = "/request-to-case-access", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON )
	public @ResponseBody ModelMap getRequestToIntervene_View(HttpServletRequest request,
			ModelMap map, RedirectAttributes redirecmap) throws Exception {
		
		EpdsSession.removeAttribute(request, "filePathList");
		
		String role = (String) EpdsSession.getAttribute(request, "user_Role");
		
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request,
				"user_Info");
		
		map.addAttribute("user_Info", user_Info);

		if (logger.isDebugEnabled()){
			logger.debug("/request-to-case-access");
		}
		
		if (!role.equalsIgnoreCase("AGENCY ATTORNEY")) {
			role = "INTERVENOR";
		}

		map.addAttribute("user_Role", role);
		
		return map;
	}
	
	
	
	/**
	 * Save the user response for Password expiry warning in the Dashboard for the duration of the session
	 * @param request
	 * @param map
	 * @param redirecmap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/cancelPasswordExpiryWarning", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON )
	public @ResponseBody ModelMap saveResponseToPasswordExpiry(HttpServletRequest request,
			ModelMap map, RedirectAttributes redirecmap) throws Exception {
		
		
		EpdsSession.removeAttribute(request, "numberOfDaysLeftToExpirePwd");
		
		return map;
	}
	
	
	/**
	 * Toggle Email Preferences for Supervisor and clear email notifications for PLCG
	 * @param request
	 * @param map
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/toggle-email-preferences/{yOrN}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON )
	public @ResponseBody ModelMap toggleGlobalEmailPreferences(HttpServletRequest request,
			ModelMap map, @PathVariable("yOrN") String response) throws Exception {
		
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request, "user_Info");
        UserRoles role = UserRoles.getByCode(user_Info.getRole_id());

		if (role != GAO_ADMIN) {
			user_Info.setGlobalEmailPref(response);
		}

        if (role == GAO_ADMIN) {
			// for PLCG it will just clear all the A# email subscription temporary.. maybe we will discuss and make it so they can remove individual A#
			user_Info.setaNumNotifications(null);
		}
		
		userInfoService.updateUserInfo(user_Info);
		
		
		
		
		EpdsSession.setAttribute(request, "user_Info",user_Info);
		
		map.addAttribute("userInfo", user_Info);
		return map;
	}
	/**
	 * Store Dashboard Preferences
	 * @param request
	 * @param map
	 * @param redirecmap
	 * @param dto
	 * @throws Exception
	 */
	@RequestMapping(value = "/store-preferences", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON )
	public @ResponseBody ModelMap storeDashboardPreferences(HttpServletRequest request,
			ModelMap map, RedirectAttributes redirecmap, DashboardPreferences dto) throws Exception {
		dto.getPreferences();
		
		return map;
	}

	/**
	 * Redirects to Advance Search View
	 * @param request
	 * @param map
	 * @param redirecmap
	 * @throws Exception
	 */
	@RequestMapping(value = "/advance-search-view", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON )
	public @ResponseBody ModelMap getAdvanceSearch_View(HttpServletRequest request, ModelMap map,
			RedirectAttributes redirecmap, HttpServletResponse response) throws Exception {
		
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request, "user_Info");
        UserRoles role = UserRoles.getByCode(user_Info.getRole_id());

        if (role != GAO_ADMIN && role != GAO_SUPERVISOR && role != GAO_ATTORNEY) {
			throw new IllegalAccessError("Unauthorized!!");
		}

		map.addAttribute("user_Info", user_Info);

		map.addAttribute("user_Role",
				(String) EpdsSession.getAttribute(request, "user_Role"));
		
		return map;
	}

	/**
	 * Redirects to manage agency view page
	 * @param request
	 * @param map
	 * @param redirecmap
	 * @throws Exception
	 */
	@RequestMapping(value = "/manage-agency-contacts-view", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON )
	public @ResponseBody ModelMap getManageAgencyContacts_View(HttpServletRequest request,
			ModelMap map, RedirectAttributes redirecmap) throws Exception {
		
		String user_Role = (String) EpdsSession.getAttribute(request, "user_Role");
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request,"user_Info");
		
		if (user_Role.equalsIgnoreCase("GAO ADMIN")){
			
			map.addAttribute("user_Info", user_Info);
			map.addAttribute("user_Role",
					user_Role);
			map.addAttribute("authorized",true);
		}else{
			map.addAttribute("authorized",false);
		}
		
		return map;
	}
	
	
	

	/**
	 * 
	 * redirects Manage attorney contacts view page
	 * @param request
	 * @param map
	 * @param redirecmap
	 * @throws Exception
	 */
	@RequestMapping(value = "/manage-attorney-contacts-view", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON )
	public @ResponseBody ModelMap getManageAttorneyContacts_View(HttpServletRequest request,
			ModelMap map, RedirectAttributes redirecmap, HttpServletResponse response) throws Exception {

		String user_Role = (String) EpdsSession.getAttribute(request, "user_Role");
		
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request,"user_Info");
		
		
		if ("GAO ADMIN".equalsIgnoreCase(user_Role) && user_Role != null){
			map.addAttribute("user_Info", user_Info);
			map.addAttribute("user_Role",
					user_Role);
			map.addAttribute("authorized",true);
		}else{
			map.addAttribute("authorized",false);
		}
		
		return map;
		
	}

	
	/**
	 * redirects to manage account reset
	 * @param request
	 * @param map
	 * @param redirecmap
	 * @throws Exception
	 */
	@RequestMapping(value = "/manage-account-reset", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON )
	public @ResponseBody ModelMap manageAccountReset(HttpServletRequest request,
			ModelMap map, RedirectAttributes redirecmap) throws Exception {

		String user_Role = (String) EpdsSession.getAttribute(request, "user_Role");
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request,"user_Info");
		
		if (user_Role.equalsIgnoreCase("GAO ADMIN")){
			
			map.addAttribute("user_Info", user_Info);
			map.addAttribute("user_Role",user_Role);
			map.addAttribute("authorized",true);
			
		}else{
			map.addAttribute("authorized",false);
		}
		
		return map;
	}
	
	/**
	 * redirects to edit templates view
	 * @param request
	 * @param map
	 * @param redirecmap
	 * @throws Exception
	 */
	@RequestMapping(value = "/edit-templates-view", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON )
	public @ResponseBody ModelMap getEditTemplates_View(HttpServletRequest request, ModelMap map,
			RedirectAttributes redirecmap, HttpServletResponse response) throws Exception {
		
		
		
		String user_Role = (String) EpdsSession.getAttribute(request, "user_Role");
		
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request,
				"user_Info");
		
	
		if (user_Role.equalsIgnoreCase("GAO ADMIN")){
			homeService.setWholeDocInfoList(request);
			map.addAttribute("user_Info", user_Info);
			map.addAttribute("user_Role",
					user_Role);
			map.addAttribute("docInfoList",
					GlobalParams.globalParam.get("whole_Doc_Info_List"));
			map.addAttribute("authorized",true);
			
		}else{
			map.addAttribute("authorized",false);
			
		}
		
		return map;
		
	}

	/**
	 * Redirects Manage Account Reset.. This API is not being used anywhere. 
	 * @param request
	 * @param map
	 * @param redirecmap
	 * @throws Exception
	 */
	@RequestMapping(value = "/manage-account-resets-view", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON )
	@Deprecated
	public @ResponseBody ModelMap getManageAccountResets_View(HttpServletRequest request,
			ModelMap map, RedirectAttributes redirecmap,HttpServletResponse response) throws Exception {
		
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request, "user_Info");
		
		if (user_Info.getRole_id().equals(GAO_ADMIN.getCode())){
			map.addAttribute("user_Info", user_Info);
			map.addAttribute("user_Role",
					(String) EpdsSession.getAttribute(request, "user_Role"));
		}else{
			throw new IllegalAccessError("Unauthorized!!");
		}
		
		
		return map;
	}

	
	/**
	 * Redirects to Case Docket Sheet
	 * @param map
	 * @param aNo : aNo of the case that it belongs to.
	 * @param request
	 * @param redirectAttributes
	 * @throws Exception
	 */
	@RequestMapping(value = "/casedocketsheet", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON )
	public @ResponseBody ModelMap getCaseDocketSheet_View(ModelMap map,
			@RequestParam("a_No") String aNo, HttpServletRequest request,
			RedirectAttributes redirectAttributes) throws Exception {
		
		if (!aNo.matches(DTOValidator.PROTEST_ID_PATTERN)){
			map.addAttribute("inputErrors", new ArrayList<String>(Arrays.asList("protestId is invalid")));
			
			return map;
		}
		
		homeService.setWholeDocInfoList(request);

		if (logger.isDebugEnabled()){
			logger.debug("/casedocketsheet a_No = {}",aNo);
		}
		
		
		User_Info userInfo = (User_Info) EpdsSession.getAttribute(request,"user_Info");
		String user_role = (String) EpdsSession.getAttribute(request, "user_Role");
		Protest_Info protestInfo = null;
		try {
			
			protestInfo = protestInfoService.getProtest_Info_ByA_No(aNo);
			if (protestInfo != null && "SUPPLEMENTAL".equalsIgnoreCase(protestInfo.getCase_Type())){
				if (protestInfo.getParent_A_No() != null && !protestInfo.getParent_A_No().equalsIgnoreCase("")){
					aNo = protestInfo.getParent_A_No();
				}
			}
			
			protestInfo = dashboardService.getProtestInfoBasedOnUserIdAndANo(userInfo, aNo);
		}catch(Exception e){
			e.printStackTrace();
		}

		if (protestInfo != null  && protestInfo.getA_No() != null){
			protestInfo = protestInfoService.getPendingOrDeniedRequestToInterveneProtestInfo(userInfo.getUser_Id(), user_role,protestInfo);
			
			if (null != protestInfo && protestInfo.getCaseAccessRequestType() != null){
				map.addAttribute("protestInfo", protestInfo);
				return map;
			}

			dashboardService.validateAccess(userInfo, protestInfo);

			List<String> intervenorCompanyNameList = dashboardService.getIntervenorCompanyNameList(aNo);
			User_Info attorney_Info = dashboardService.getAttorneyInfo(aNo);
			int daysRemaining = Date_Util.getNumberOfDaysRemaining(protestInfo.getDue_Date());
			List<File_Info> fileInfoList = dashboardService.getFileInfoList(protestInfo, userInfo.getUser_Id(),true);

			dashboardService.populateAgencyNameForAgencyInFileInfoList(fileInfoList);
			
			
			EpdsSession.setAttribute(request, "a_No", protestInfo.getA_No());
			EpdsSession.setAttribute(request, "protestInfo", protestInfo);
			EpdsSession.setAttribute(request, "protest_Info", protestInfo);
			EpdsSession.setAttribute(request, "attorney_Info", attorney_Info);
			EpdsSession.setAttribute(request, "caseDocket_File_Info_List", fileInfoList);
			// think this should be 'role' since I can't find reference to it in the html code and see it being passed back in other places as 'role'
			// So adding role, but leaving user_Protest_Role as well as it may be passed back in
			EpdsSession.setAttribute(request, "user_Protest_Role", protestInfo.getRole());
			EpdsSession.setAttribute(request, "role", protestInfo.getRole());
			EpdsSession.setAttribute(request, "intervenorCompanyNameList",intervenorCompanyNameList);
			
			map.addAttribute("user_Info", userInfo);
			map.addAttribute("protestInfo", protestInfo);
			map.addAttribute("attorneyInfo", attorney_Info);
			map.addAttribute("fileInfoList", fileInfoList);
			map.addAttribute("daysRemaining", daysRemaining);
			map.addAttribute("intervenorCompanyNameList", intervenorCompanyNameList);
			map.addAttribute("whole_Doc_Info_Map",
					GlobalParams.globalParam.get("whole_Doc_Info_List"));
			
			
		}else{
			dashboardService.validateAccess(userInfo, protestInfo);

			map.addAttribute("caseDocketNotFound", true);
		}
		
		
		return map;	
	}


	@RequestMapping("/userprofile")
	public @ResponseBody ModelMap getUserProfile_View(HttpServletRequest request, ModelMap map,
			RedirectAttributes redirecmap) throws Exception {
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request,
				"user_Info");
		
		map.addAttribute("userProfileInfo", user_Info);
		
		return map;
	}


	
	/**
	 * get list of search result based on the criteria set by the user
	 * @param request
	 * @param advancedSearchDTO
	 * @param map
	 * @throws Exception
	 */
	@RequestMapping(value = "/advance-search", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON )
	public @ResponseBody ModelMap advanceSearch(HttpServletRequest request,
			AdvanceSearchDTO advancedSearchDTO, ModelMap map, HttpServletResponse response) {
		String user_Role = (String) EpdsSession.getAttribute(request,
				"user_Role");

		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request, "user_Info");
        UserRoles role = UserRoles.getByCode(user_Info.getRole_id());

        if (role != GAO_ADMIN && role != GAO_SUPERVISOR && role != GAO_ATTORNEY) {
					throw new IllegalAccessError("Unauthorized!!");
		}

		if (logger.isDebugEnabled()){
			logger.debug("advanceSearch advanceSearchDTO = {}",advancedSearchDTO.toString());
		}
		
		try {
				List<Protest_Info> protest_InfoList = dashboardService
						.getSearchResultsBasedOnAdvancedSearch(advancedSearchDTO);
		
				protest_InfoList = homeService
						.fillUpEachProtestInfoWithAgencyName(protest_InfoList);
		
				EpdsSession.setAttribute(request, "payDotGovReport", protest_InfoList);
				if (protest_InfoList != null) {
					Map<String, Protest_Info> protest_InfoMap = homeService
							.getProtestInfoMap(protest_InfoList);
		
					EpdsSession.setAttribute(request, "protest_InfoMap",protest_InfoMap);
		
					protest_InfoList = homeService.assignParentAndChildRelation(protest_InfoMap, protest_InfoList, user_Role);
					map.addAttribute("protestInfoList", protest_InfoList);
					map.addAttribute("role", user_Role.trim());
				} else {
					map.addAttribute("response", "does not exist");
				}
		}catch (Exception e) {
			e.printStackTrace();
			map.addAttribute("response", "does not exist");
		}
		
		
		return map;
	}

	
	/*
	 * Amer : 
	 * EPDS_Web\WebContent\scripts\vendor\ckeditor\plugins\strinsert\plugin.js
	 * go to this above location and add dynamic data fields
	 */
	/**
	 * Save edited template. AngularJS by default sanitizes the html before sending the data to the backend.
	 * @param map
	 * @param request
	 * @param docId
	 * @param content
	 * @throws Exception
	 */
	@RequestMapping(value = "/save-edited-template", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON )
	public void  saveEditedTemplateInHtml(ModelMap map, HttpServletRequest request,
			@RequestParam("docId") String docId,
			@RequestParam("content") String content) throws Exception {

		
		if (logger.isDebugEnabled()){
			
			logger.debug("/save-edited-template docId = {}",docId);
		}
		
		String user_Role = (String) EpdsSession.getAttribute(request, "user_Role");
		
		
		if (user_Role.equalsIgnoreCase("GAO ADMIN")){
			
			Document doc = Jsoup.parse(content, "UTF-8");
			
			String cleanHtml = Jsoup.clean(doc.toString(),
					"https://epdstest.edc.usda.gov/",
					Whitelist.relaxed().addTags("span", "p", "br")
					.addAttributes(":all", "style").preserveRelativeLinks(true),
					new Document.OutputSettings().prettyPrint(true));
			
			Map<String, String> mapOfFileMeta = TemplateUtils
					.getMapOfFileMetaData(docId);
			
			String filePath = request.getServletContext().getRealPath(mapOfFileMeta.get("html"));
			
			
			if (GlobalParams.IP.toString().contains("159.142.165.49")) {
				filePath = TemplateUtils.getTemplateFilePathInProd(mapOfFileMeta.get("html"));
			} 
			
			final File htmlFile = new File(filePath);
			
			if (!htmlFile.exists()){
				htmlFile.createNewFile();
			}
	        
			FileUtils.writeStringToFile(htmlFile, cleanHtml, "UTF-8");
	       
		}
		

	}
	
	
	/**
	 * need to remove records from invited user,protestDMInfo,
	 * @param req
	 * @param removeCaseDto
	 * @throws Exception
	 */
	
	@RequestMapping(value = "/remove-case", method = RequestMethod.POST, produces = "application/json")
	public void removeCase(
	        HttpServletRequest req,
			RemoveCaseDto removeCaseDto, HttpServletResponse response) throws Exception {
		
		
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(req, "user_Info");
		
		if (user_Info.getRole_id().equals(GAO_ADMIN.getCode())){
			dashboardService.removeCase(removeCaseDto);	
		}else{
			throw new IllegalAccessError("Unauthorized!!");
		
		}
		
	}
	
	@RequestMapping(value = {"/export-to-excel", "/advanceSearch-export-to-excel"}, method = RequestMethod.GET)
	public void  exportToExcel(HttpServletRequest request,HttpServletResponse response)
			throws Exception {
		
		if (logger.isDebugEnabled()){
			logger.debug("exportToExcel ");
		}
		
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request, "user_Info");
		
		if (!user_Info.getRole_id().equals(GAO_ADMIN.getCode())) {
			throw new IllegalAccessError("Unauthorized!!");
		}

		@SuppressWarnings("unchecked")
		List<Protest_Info> listOfProtestInfo = (List<Protest_Info>) EpdsSession.getAttribute(
				request, "payDotGovReport");
		
		ServletOutputStream outStream = response.getOutputStream();  
		try
        {		
		 	HSSFWorkbook workbook = null;
		 	if(request.getRequestURI().contains("advanceSearch-export-to-excel")){
				workbook = ExportToExcel.getAdvanceSearchWorkBook(listOfProtestInfo);
			} else if(request.getRequestURI().contains("export-to-excel")){
		 		workbook = ExportToExcel.getWorkBook(listOfProtestInfo);
			}

			// write it as an excel attachment
			ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
			workbook.write(outByteStream);
			byte [] outArray = outByteStream.toByteArray();
			
			/*response.setContentType("application/ms-excel");
			response.setContentType("application/vnd.ms-excel");
	        response.setHeader("Content-Disposition", "attachment; filename=payDotGov.xls");
			response.setContentLength(outArray.length);
			response.setHeader("Expires:", "0"); // eliminates browser caching*/
			
			response.setContentType("application/ms-excel");
	        response.setContentLength(outArray.length);
	         
	        /* "Content-Disposition : attachment" will be directly download, may provide save as popup, based on your browser setting*/
			if(request.getRequestURI().contains("advanceSearch-export-to-excel")) {
				response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", "advanceSearchResults.xls"));
			} else if(request.getRequestURI().contains("export-to-excel")) {
				response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", "payDotGov.xls"));
			}
			outStream.write(outArray);
			outStream.flush();
        }finally{

            if (outStream != null) {
            	outStream.close();
            	outStream.flush();
            }
        
        }
		
	}
	
}
