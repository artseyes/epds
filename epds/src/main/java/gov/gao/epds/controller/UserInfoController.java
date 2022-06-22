package gov.gao.epds.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import gov.gao.epds.persistence.entity.*;
import org.codehaus.jackson.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import gov.gao.epds.dto.CompanyInfo;
import gov.gao.epds.dto.DTOValidator;
import gov.gao.epds.dto.EmailNotification;
import gov.gao.epds.dto.InputValidationError;
import gov.gao.epds.dto.User_info_dto;
import gov.gao.epds.enums.UserRoles;
import static gov.gao.epds.enums.UserRoles.*;

import gov.gao.epds.rest.auth.services.AuthUtil;
import gov.gao.epds.service.DashboardService;
import gov.gao.epds.service.EmailService;
import gov.gao.epds.service.GC_Service;
import gov.gao.epds.service.ProtestInfoService;
import gov.gao.epds.service.UserInfoService;
import gov.gao.epds.session.EpdsSession;
import gov.gao.epds.utils.EPDS_FileUtils;
import gov.gao.epds.utils.GlobalParams;
import gov.gao.epds.utils.RegistrationUtil;

/**
 * Handles requests for the application home page.
 */
@Controller
public class UserInfoController { // NO_UCD (unused code)

	private final static Logger logger = LoggerFactory
			.getLogger(UserInfoController.class);

	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private GC_Service gc_Service;
	@Autowired
	private ProtestInfoService protestInfoService;
	@Autowired
	private EmailService emailService;
	
	
	@Autowired
	AuthUtil authUtil;
	
	@Autowired
	private DashboardService dashboardService;

	@RequestMapping(value = "/reset-agency-accounts")
	public void updateAgencyInfo(
			HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirecmap,User_info_dto user_info_dto)
			throws Exception {

		
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request, "user_Info");

		if (!user_Info.getRole_id().equals(GAO_ADMIN.getCode())) {
			throw new IllegalAccessError("Unauthorized!!");
		}
		userInfoService.updateAgencyUserInfo(request,user_info_dto);


	}


	/**
	 * Invite Protester/Intervenor Secondary Representatives to the case.
	 *
	 * @param map
	 * @param secondary_Email
	 * @param inviter_Type
	 * @param intervenorCompanyName
	 * @param intervenorCompanyAddr
	 * @param request
	 * @param redirectAttributes
	 * @throws Exception
	 */
	@RequestMapping(value = "/invite-secondary-user", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public void inviteSecondaryUser(
			ModelMap map,
			@RequestParam("email") String secondary_Email,
			@RequestParam("inviter_Type") String inviter_Type,
			@RequestParam("aNum") String aNum,
			@RequestParam(value = "companyName", required = false) String intervenorCompanyName,
			@RequestParam(value = "companyAddr", required = false) String intervenorCompanyAddr,
			HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
		String validation = "noSuchEmail";

		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request,
				"user_Info");
		
		
		Protest_Info protestInfo = dashboardService.getProtestInfoBasedOnUserIdAndANo(user_Info, aNum);
		
		
		checkIfThisUserHasAccessToPerformThisAction(inviter_Type, user_Info, protestInfo,intervenorCompanyName);

		if (logger.isDebugEnabled()){
			logger.debug("inviteSecondaryUser secondary email={}, inviter_Type ={}, a_No={}",
					secondary_Email, inviter_Type,protestInfo.getA_No());
		}
		if (!secondary_Email.matches(DTOValidator.EMAIL_PATTERN)
				|| !inviter_Type.matches(DTOValidator.ATTACHMENT_TYPE_PATTERN)){

			throw new IllegalArgumentException("Invalid Input Format!!");
		}


		String a_No = protestInfo.getA_No();

		String primary_Id = user_Info.getUser_Id();

		User_Info secondaryUserInfo = userInfoService.getUser_InfoByEmail(secondary_Email);

		// validation only checks epds.user_info which still has an entry after the auth user_info account has been deleted
		// this checks the auth.user_info to see if the account exists to prevent inviting a deleted account
		String message = userInfoService.checkIfAuthUserExists(secondary_Email);
		if ("Y".equals(message)) {
			validation = userInfoService.getValidation(a_No,
					secondary_Email.trim(), user_Info, inviter_Type,secondaryUserInfo,intervenorCompanyName,intervenorCompanyAddr);
		}

		if ("valid".equals(validation)) {
			String secondary_Id = secondaryUserInfo.getUser_Id();
			userInfoService.inviteSecondaryUser(a_No, primary_Id, secondary_Id,
					secondary_Email, inviter_Type, intervenorCompanyName,
					intervenorCompanyAddr);
			emailService.sendNotificationToSecondaryRep(request, protestInfo, secondary_Email);
		}

		map.addAttribute("validation", validation);
		map.addAttribute("protest_Info", protestInfo);

	}


	private void checkIfThisUserHasAccessToPerformThisAction(String inviter_Type, User_Info user_Info,
			Protest_Info protestInfo, String intervenorCompanyName) throws IllegalAccessError {

		//user is not a GAO admin
		if (!user_Info.getRole_id().equals(GAO_ADMIN.getCode())){
			
			User_Protest_Role_Bridge primaryRepUBrb = dashboardService.checkIfUPRBExists(protestInfo, user_Info);
			
			//check based on inviter type if this is a primary protester then make sure this is a primary protester doing this action
			if (inviter_Type.equalsIgnoreCase("protester") 
					&& (primaryRepUBrb == null  || primaryRepUBrb.getRole_Id() != PROTESTER.getCode())){
				
				throw new IllegalAccessError("Unauthorized!!");
			}
			//if this is an intervenor 
			//check if this is a primary intervenor from the same intervenor comp name
			if (inviter_Type.equalsIgnoreCase("intervenor") 
					&& (primaryRepUBrb == null  
					|| primaryRepUBrb.getRole_Id() != INTERVENOR.getCode()
					|| !primaryRepUBrb.getIntervenor_Company_Name()
					.equalsIgnoreCase(intervenorCompanyName))){
				
				throw new IllegalAccessError("Unauthorized!!");
			}
			
		}

		//check if this user has "ready only" access
		if (protestInfo.isViewOnly()){
			throw new IllegalAccessError("Unauthorized!!");
		}
	}



	/**
	 * When Secondary Reps are invited they have the option to accept/reject the invitation to join the case.
	 *
	 * @param map
	 * @param response Y/N
	 * @param invite_A_No a_No of the Protest
	 * @param request
	 * @param redirectAttributes
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/respond-to-invitation", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody
	String respondToInvitation(ModelMap map,
			@RequestParam("response") String response,
			@RequestParam("invite_A_No") String invite_A_No,
			HttpServletRequest request, RedirectAttributes redirectAttributes)
			throws Exception {


		if (logger.isDebugEnabled()){
			logger.debug("respondToInvitation invite_A_No ={}",invite_A_No);
		}


		if (!response.matches(DTOValidator.ALPHA_PATTERN)
				|| !invite_A_No.matches(DTOValidator.PROTEST_ID_PATTERN)){

			throw new IllegalArgumentException("Invalid Input Format!!");
		}

		String secondary_Id = ((User_Info) EpdsSession.getAttribute(request,
				"user_Info")).getUser_Id();

		userInfoService.acceptOrRejectInvitation(invite_A_No, secondary_Id,
				response);



		return null;
	}

	


	/**
	 * 
	 * 
	 * @param map
	 * @param secondary_User_Id
	 * @param request
	 * @param redirectAttributes
	 * @throws Exception
	 */
	@RequestMapping("/delete-secondary-user")
	public void removeSecondaryUserAccess(ModelMap map,
			@RequestParam("secondary_User_Id") String secondary_User_Id,
			@RequestParam("aNum") String aNum,
			HttpServletRequest request, RedirectAttributes redirectAttributes)
			throws Exception {
		
		
		try {
		if (!secondary_User_Id.matches(DTOValidator.INTEGER_PATTERN)){
			throw new IllegalArgumentException("Invalid Input format!!");
		}

		
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request,
				"user_Info");
		Protest_Info protestInfo = dashboardService.getProtestInfoBasedOnUserIdAndANo(user_Info, aNum);
		
		checkIfThisUserCanPerformThisAction(secondary_User_Id, user_Info, protestInfo);
		
		
		if (logger.isDebugEnabled()){
			logger.debug("removeSecondaryUserAccess a_No ={}, secondary_User_Id = {}",aNum,secondary_User_Id);
		}
		userInfoService.removeAccessOfSecondaryUser(aNum, secondary_User_Id);
		map.addAttribute("isSuccess",true);
		}catch (Exception e){
			e.printStackTrace();
			map.addAttribute("isSuccess",false);
		}


	}


	private void checkIfThisUserCanPerformThisAction(String secondary_User_Id, User_Info user_Info,
			Protest_Info protestInfo) throws Exception, IllegalAccessError {
		User_Info secondaryUserInfo = userInfoService.getUserInfoByUsername(secondary_User_Id);

        if (!user_Info.getRole_id().equals(GAO_ADMIN.getCode())){
			User_Protest_Role_Bridge primaryRepUBrb = dashboardService.checkIfUPRBExists(protestInfo, user_Info);
			User_Protest_Role_Bridge secondaryRepUBrb = dashboardService.checkIfUPRBExists(protestInfo, secondaryUserInfo);
		   	Invited_User secondaryInvitedUser = null;

			if (secondaryRepUBrb == null) {
				secondaryInvitedUser = userInfoService.getSecondaryUser(protestInfo.getA_No(), secondary_User_Id, "INVITED");
			}

		    if (secondaryRepUBrb == null && secondaryInvitedUser == null) {
				// secondary user doesn't exist
			    throw new IllegalAccessError("Unauthorized!!");
		    }

			//if this is a primary protester then make sure this is a primary protester doing this action
			if (( (secondaryRepUBrb != null && secondaryRepUBrb.getRole_Id() == SECONDARY_PROTESTER.getCode()) ||
				  (secondaryInvitedUser != null && secondaryInvitedUser.getInviter_Type().equalsIgnoreCase(PROTESTER.getName())) )
				&& (primaryRepUBrb == null  || primaryRepUBrb.getRole_Id() != PROTESTER.getCode())){
				
				throw new IllegalAccessError("Unauthorized!!");
			}
			
			//if this is secondary intervenor
			//check if this is a primary intervenor from the same intervenor comp name
			String companyName;
			if (secondaryRepUBrb != null)
				companyName = secondaryRepUBrb.getIntervenor_Company_Name();
			else
				companyName = secondaryInvitedUser.getCompany_name();

			if (( (secondaryRepUBrb != null && secondaryRepUBrb.getRole_Id() == SECONDARY_INTERVENOR.getCode()) ||
					(secondaryInvitedUser != null && secondaryInvitedUser.getInviter_Type().equalsIgnoreCase(INTERVENOR.getName())) )
					&& (primaryRepUBrb == null  || primaryRepUBrb.getRole_Id() != INTERVENOR.getCode()
					|| !primaryRepUBrb.getIntervenor_Company_Name().equalsIgnoreCase(companyName))){
				
				throw new IllegalAccessError("Unauthorized!!");
			}
		}

		//check if this user has "ready only" access
		if (protestInfo.isViewOnly()){
			throw new IllegalAccessError("Unauthorized!!");
		}
	}

	@RequestMapping("/remove-intervenor")
	public void removeIntervenorAccess(ModelMap map,
			@RequestParam("aNum") String aNum,
			HttpServletRequest request, RedirectAttributes redirectAttributes, CompanyInfo dto)
			throws Exception {

		User_Info userInfo = (User_Info) EpdsSession.getAttribute(request,"user_Info");

		if (logger.isDebugEnabled()){
			logger.debug("removeIntervenorAccess a_No ={}",aNum);
		}

		
		if (!userInfo.getRole_id().equals(GAO_ADMIN.getCode())){
			throw new IllegalAccessError("Unauthorized!!");
		}
		
		Protest_Info protest_Info = dashboardService.getProtestInfoBasedOnUserIdAndANo(userInfo, aNum);
		
		
		List<File_Info> fileInfoList = dashboardService.getFileInfoList(protest_Info, userInfo.getUser_Id(), true);
		
		fileInfoList = userInfoService.revokeIntervenorAccess(aNum,fileInfoList, dto);
		
		EpdsSession.setAttribute(request, "caseDocket_File_Info_List",
				fileInfoList);
	}

	/**
	 * Delete Agency POC's or GAO User Permanently from EPDS application.
	 * @param map
	 * @param user_Id
	 * @param isGAO_User
	 * @param request
	 * @param redirectAttributes
	 * @throws Exception
	 */
	@RequestMapping("/delete-user")
	public void deleteUser(ModelMap map,
			@RequestParam("user_Id") String user_Id,
			@RequestParam("isGAO_User") String isGAO_User,
			HttpServletRequest request, RedirectAttributes redirectAttributes)
			throws Exception {

		String user_Role = (String) EpdsSession.getAttribute(request, "user_Role");


		if ((user_Id != null && !user_Id.matches(DTOValidator.INTEGER_PATTERN))
				|| (isGAO_User != null && !isGAO_User.matches(DTOValidator.ALPHA_PATTERN))){
			throw new IllegalArgumentException("Invalid Input format!!");
		}

		if (logger.isDebugEnabled()){
			logger.debug("deleteUser user_Id ={}",user_Id);
		}

		User_Info usrInfo = userInfoService.getUserInfoByUsername(user_Id);
		
		if (user_Role.equalsIgnoreCase("GAO ADMIN")) {

			String response = authUtil.getAuthJSONResponse("deleteAccountURI", user_Id,new User_info_dto(),request);

			boolean isSuccess = AuthUtil.getJsonNode(response, "isSuccess")
					.asBoolean();
			if (isSuccess){
				if (isGAO_User != null && isGAO_User.equals("Y")) {
						userInfoService.deleteGAO_User(user_Id);
					}

					if (usrInfo.getRole_id().equals(AGENCY_ADMIN.getCode())){
						userInfoService.removeAgencyPOC(usrInfo);
					}
					userInfoService.deleteUser(user_Id);

				}
			map.addAttribute("authorized", true);
			map.addAttribute("isSuccess", isSuccess);
		}else {
			map.addAttribute("authorized", false);
		}


	}

	/**
	 * Register GAO User into EPDS.
	 * @param request
	 * @param map
	 * @param redirecmap
	 * @param user_info_dto
	 * @throws Exception
	 */
	@RequestMapping(value = "/register-gao-info", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public void registerAttorneyInfo(HttpServletRequest request,
			ModelMap map, RedirectAttributes redirecmap,User_info_dto user_info_dto) throws Exception {

		String user_Role = (String) EpdsSession.getAttribute(request, "user_Role");

		if (logger.isDebugEnabled()){
			logger.debug("registerAttorneyInfo ");
		}
		if (user_Role.equalsIgnoreCase("GAO ADMIN")){
			List<InputValidationError> constraintViolations = AuthUtil.validateDTO(user_info_dto);


			if (!constraintViolations.isEmpty() && null != constraintViolations ){
				map.addAttribute("inputErrors", constraintViolations);
			}else{
				User_Info userInfo = RegistrationUtil.registerUser(user_info_dto,
						userInfoService, "GAO USER", true,request);
				if (userInfo != null){
					map.addAttribute("success",true);
				}
			}

		}else{
			map.addAttribute("authorized",false);
		}
	}

	/**
	 * Register Agency Point Of Contacts. Agency Representative can create their own account from registration page.
	 * @param request
	 * @param map
	 * @param redirecmap
	 * @param user_info_dto
	 * @throws Exception
	 */
	@RequestMapping(value = "/register-agency-info", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public void registerAgencyInfo(HttpServletRequest request,
			ModelMap map, RedirectAttributes redirecmap,User_info_dto user_info_dto) throws Exception {

		if (logger.isDebugEnabled()){
			logger.debug("registerAgencyInfo ");
		}
		String user_Role = (String) EpdsSession.getAttribute(request, "user_Role");
		user_info_dto.setEpds_role_id(5);
		user_info_dto.setRole("AGENCY ADMIN");
		User_Info userInfo = null;

		if (user_Role.equalsIgnoreCase("GAO ADMIN")){


			List<InputValidationError> constraintViolations = AuthUtil.validateDTO(user_info_dto);


			if (!constraintViolations.isEmpty() && null != constraintViolations ){
				map.addAttribute("inputErrors", constraintViolations);
			}else {

				//userInfo = userInfoService.getUser_InfoByEmail(user_info_dto.getEmail().toLowerCase(Locale.ENGLISH));

				userInfo = RegistrationUtil.registerUser(user_info_dto, userInfoService, "", false,request);
				if (userInfo != null){
					map.addAttribute("success",true);
				}

			}



		}else{
			map.addAttribute("authorized",false);
		}
	}



	/**
	 * GAO response for Request to intervene /Notice of Appearance
	 * @param request
	 * @param response
	 * @param file_Id
	 * @param accessType
	 * @throws Exception
	 */
	@RequestMapping(value = "/respond-request-to-access", method = RequestMethod.POST)
	public void respondToRequestToAccessCase(HttpServletRequest request,
			@RequestParam("response") String response,
			@RequestParam("file_Id") String file_Id,
			@RequestParam("accessType") String accessType) throws Exception {

		File_Info file_Info = userInfoService.updateApprovedStatus(file_Id, response, accessType);

		User_Info loggedInUserInfo = (User_Info) EpdsSession.getAttribute(request,"user_Info");

		Protest_Info protest_Info = dashboardService.getProtestInfoBasedOnUserIdAndANo(loggedInUserInfo, file_Info.getA_No());

		dashboardService.validateAccess(loggedInUserInfo, protest_Info);

        UserRoles role = UserRoles.getByCode(loggedInUserInfo.getRole_id());
		if ( role != GAO_ADMIN && role != GAO_SUPERVISOR && role != GAO_ATTORNEY){
			throw new IllegalAccessError("Unauthorized!!");
		}
		
		if (protest_Info.isViewOnly() && role != GAO_ADMIN){
			throw new IllegalAccessError("Unauthorized!!");
		}
		
		if (!file_Id.matches(DTOValidator.INTEGER_PATTERN)
				|| !response.matches(DTOValidator.ALPHA_PATTERN)
				|| !accessType.matches(DTOValidator.ATTACHMENT_TYPE_PATTERN)){

			throw new IllegalArgumentException("Invalid Input format!!");

		}

		if (logger.isDebugEnabled()){

			logger.debug("respondToRequestToAccessCase a_No = {}", protest_Info.getA_No());
		}



		if (response.equalsIgnoreCase("Y")) {

			if (accessType.equalsIgnoreCase("intervene")) {
				gc_Service.set_Intervener_Approved_Event(protest_Info,
						file_Info);
			} else if (accessType.equalsIgnoreCase("agency-rep-access")) {
			    // user_Info of the file submitted rather than the one approving the access
				gc_Service.set_Agency_Representative_Assigned_Notice(protest_Info, userInfoService.getUserInfoByUsername(file_Info.getSubmitter_User_Id()));
			}

			protestInfoService.addRequestToAccessCaseApprovedDocumentEntry(
					request, file_Info.getCompany_Name(), accessType,
					protest_Info.getAgency_Name(),userInfoService.getUserInfoByUsername(file_Info.getSubmitter_User_Id()),loggedInUserInfo,protest_Info);
		}else if (response.equalsIgnoreCase("N")){
			protestInfoService.notifyAgencyRepOrIntervenorAboutAccessDenied(request,accessType,file_Info,protest_Info);
		}


	}

	/**
	 *
	 * Retrieve Attorney Info by attorney Email address.
	 * @param map
	 * @param attorney_Email
	 * @param inviter_Type
	 * @param request
	 */

	@RequestMapping(value = "/get-attorney-info", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public void getAttorney_User_Info(ModelMap map,
			@RequestParam("email") String attorney_Email,
			@RequestParam("inviter_Type") String inviter_Type,
			@RequestParam("aNum") String a_No,
			HttpServletRequest request) throws Exception {
		String validation = "noSuchEmail";


		if (!attorney_Email.matches(DTOValidator.EMAIL_PATTERN)
				|| !inviter_Type.matches(DTOValidator.ATTACHMENT_TYPE_PATTERN)){

			throw new IllegalArgumentException("Invalid Input format!!");

		}
		if (logger.isDebugEnabled()){
			logger.debug("/get-attorney-info a_No = {}", a_No);
		}
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request,"user_Info");
		User_Info invitee_User_Info = userInfoService.getUser_InfoByEmail(attorney_Email);

		// validation only checks epds.user_info which still has an entry after the auth user_info account has been deleted
		// this checks the auth.user_info to see if the account exists to prevent inviting a deleted account
		String message = userInfoService.checkIfAuthUserExists(attorney_Email);
		if ("Y".equals(message)) {
			validation = userInfoService.getValidation(a_No, attorney_Email.trim(),
					user_Info, inviter_Type,invitee_User_Info,null,null);//come back to this
		}

		if ("valid".equals(validation)) {

			map.addAttribute("attorney_Info", invitee_User_Info);
		}

		map.addAttribute("validation", validation);
	}


	@RequestMapping(value = "/assign-rep", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public void assignUserRole(
			ModelMap map,
			@RequestParam("email") String userEmail,
			@RequestParam("assignType") String assignType,
			@RequestParam("aNum") String a_No,
			@RequestParam(value = "companyName", required = false) String intervenorCompanyName,
			@RequestParam(value = "companyAddr", required = false) String intervenorCompanyAddr,
			HttpServletRequest request, RedirectAttributes redirectAttributes)
			throws Exception {
		
		User_Info loggedInUserInfo = (User_Info) EpdsSession.getAttribute(request, "user_Info");
		
		Protest_Info protest_Info = dashboardService.getProtestInfoBasedOnUserIdAndANo(loggedInUserInfo, a_No);

		if (protest_Info.isViewOnly()){
			return;
		}

        UserRoles role = UserRoles.getByCode(loggedInUserInfo.getRole_id());
		if (role != GAO_ADMIN && role != GAO_ATTORNEY && role != GAO_SUPERVISOR && role != AGENCY_ADMIN && role != AGENCY_ATTORNEY){
			throw new IllegalAccessError("Request Cannot be completed");
		}

		if (!userEmail.matches(DTOValidator.EMAIL_PATTERN)
				|| !assignType.matches(DTOValidator.ATTACHMENT_TYPE_PATTERN)){

			throw new IllegalArgumentException("Invalid Input format!!");

		}

		if (logger.isDebugEnabled()){
			logger.debug("/assign-rep a_No = {}",protest_Info.getA_No());
		}
		//String a_No = protest_Info.getA_No();
		User_Info user_info = userInfoService.getUser_InfoByEmail(userEmail);
		userInfoService.assignRole(a_No, user_info, assignType,
				intervenorCompanyName, intervenorCompanyAddr);



		if (assignType.equals("agency-attorney")) {

			protestInfoService.addRequestToAccessCaseApprovedDocumentEntry(
					request, null, "agency-rep-access",
					user_info.getFirm_Name(),user_info,loggedInUserInfo,protest_Info);

			gc_Service.set_Agency_Representative_Assigned_Notice(protest_Info,
					user_info);
		}


	}

	/**
	 *
	 * Edit User Info
	 * @param map
	 * @param userInfoDTO
	 * @param isGAO_User
	 * @param request
	 * @param redirectAttributes
	 * @throws Exception
	 */
	@RequestMapping(value = "/edit-user-info", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody ModelMap editUser_Info(ModelMap map,
			@ModelAttribute User_info_dto userInfoDTO,
			@RequestParam("isGAO_User") String isGAO_User,
			HttpServletRequest request, RedirectAttributes redirectAttributes)
			throws Exception {


		if (logger.isDebugEnabled()){
			logger.debug("/edit-user-info ");
		}

		String user_Role = (String) EpdsSession.getAttribute(request, "user_Role");
		List<InputValidationError> constraintViolations = AuthUtil.validateDTO(userInfoDTO);

		if (!constraintViolations.isEmpty() && null != constraintViolations ){
			return map.addAttribute("inputErrors", constraintViolations);
		}



		User_Info userInfo = userInfoService
				.getUser_InfoByEmail(userInfoDTO.getEmail());
		
		userInfoDTO.setUser_id(Integer.valueOf(userInfo.getUser_Id()));


		if (isGAO_User != null
				&& isGAO_User.equalsIgnoreCase("Y")
				&& user_Role.equalsIgnoreCase("GAO ADMIN")) {
			RegistrationUtil.populateGaoUserField(userInfoDTO);
			userInfoService.editGAO_User(userInfoDTO);
		}else{
				if (user_Role.equalsIgnoreCase("GAO ADMIN")
						|| user_Role.equalsIgnoreCase("GAO SUPERVISOR")
						|| user_Role.equalsIgnoreCase("GAO ATTORNEY")){
					userInfoDTO.setRole(user_Role);
					
					
					if (GlobalParams.IP.equals(GlobalParams.PROD_IP_ADDR)
							&& (!userInfoDTO.getEmail().endsWith(".gov"))){
						throw new IllegalArgumentException("Email Address should contains .gov ");
					}
					
					RegistrationUtil.populateGaoUserField(userInfoDTO);
				}else if (user_Role.equalsIgnoreCase("AGENCY ADMIN")
						|| user_Role.equalsIgnoreCase("AGENCY ATTORNEY")){
					//User_Info user_Info = userInfoService.getUser_InfoByEmail(userInfoDTO.getEmail());
					userInfoDTO.setAuth_role_id(2);

					
					if (GlobalParams.IP.equals(GlobalParams.PROD_IP_ADDR)
							&& (!userInfoDTO.getEmail().endsWith(".gov") 
									&& !userInfoDTO.getEmail().endsWith(".mil") 
							&& !userInfoDTO.getEmail().endsWith(".edu") )){
						throw new IllegalArgumentException("Email Address should contains .gov or .mil");
					}
					Integer firm_id = userInfoService.getAgencyId(
							userInfoDTO.getTier1_agency_id(),
							userInfoDTO.getTier2_agency_id());
					String agencyName;

					if (firm_id != null){
						userInfoDTO.setFirm_id(firm_id);
					}else{
						userInfoDTO.setFirm_id(userInfo.getFirm_id());
					}
					 agencyName = userInfoService.getAgencyNamebyAgencyInfoId(userInfoDTO
								.getFirm_id());



						if (userInfoDTO.getEpds_role_id() == 6
								|| userInfoDTO.getEpds_role_id() == 5) {

							 userInfoDTO.setNameOfFirm(agencyName);
							/*if (userInfoDTO.getNameOfFirm() == null) {
								String agencyName = userInfoService.getAgencyNamebyAgencyInfoId(userInfoDTO
										.getFirm_id());
								userInfoDTO.setNameOfFirm(agencyName);
							}*/
						}
				} else {
                    // Epds_role_id 1, 2, 4, 9
					userInfoDTO.setAuth_role_id(1);
				}
		}
		userInfoDTO.setTypeOfUpdate("updateProfile");
		userInfoDTO.setAccount_status_id(2);

		String jsonResponse = authUtil.getAuthJSONResponse("updateProfileURI",
				"Y", userInfoDTO,request);

		boolean isUpdateSuccess = AuthUtil
				.getJsonNode(jsonResponse, "isSuccess").asBoolean();

		if (isUpdateSuccess || "Y".equalsIgnoreCase(isGAO_User)) {
			User_Info updatedUsrInfo = userInfoService.updatePersonalInfo(userInfoDTO);
			
			if (!"Y".equalsIgnoreCase(isGAO_User) && null != isGAO_User){
				EpdsSession.setAttribute(request,"user_Info",updatedUsrInfo);
			}

			map.addAttribute("success",true);
		}else{
			map.addAttribute("success",false);
		}

		return map;
	}

	/**
	 * Edit Agency POC Info
	 * @param map
	 * @param userInfoDTO
	 * @param request
	 * @param redirectAttributes
	 * @throws Exception
	 */
	@RequestMapping(value = "/edit-agency-info", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody ModelMap editAgencyInfo(ModelMap map,
			@ModelAttribute User_info_dto userInfoDTO,
			HttpServletRequest request,
			HttpServletResponse response,
			RedirectAttributes redirectAttributes)
			throws Exception {

		if (logger.isDebugEnabled()){
			logger.debug("/edit-agency-info");
		}

		List<InputValidationError> constraintViolations = AuthUtil.validateDTO(userInfoDTO);


		if (!constraintViolations.isEmpty() && null != constraintViolations ){
			return map.addAttribute("inputErrors", constraintViolations);
		}
		String user_Role = (String) EpdsSession.getAttribute(request, "user_Role");

		User_Info userInfo = userInfoService
				.getUser_InfoByEmail(userInfoDTO.getEmail());
		
		
		User_Info loggedInUserInfo = (User_Info) EpdsSession.getAttribute(request, "user_Info");
		if (!loggedInUserInfo.getRole_id().equals(GAO_ADMIN.getCode()) ){
			throw new IllegalAccessError("Unauthorized!!");
		}

		userInfoDTO.setAuth_role_id(2);
		userInfoDTO.setFirm_id(userInfo.getFirm_id());
		userInfoDTO.setNameOfFirm(userInfo.getFirm_Name());
		userInfoDTO.setUser_id(Integer.valueOf(userInfo.getUser_Id()));
		userInfoDTO.setTypeOfUpdate("updateProfile");
		userInfoDTO.setAccount_status_id(2);
		userInfoDTO.setEpds_role_id(5);

		String jsonResponse = authUtil.getAuthJSONResponse("updateProfileURI",
				"Y", userInfoDTO,request);

		boolean isUpdateSuccess = AuthUtil
				.getJsonNode(jsonResponse, "isSuccess").asBoolean();

		if (isUpdateSuccess){
			User_Info updatedUsrInfo = userInfoService.updatePersonalInfo(userInfoDTO);

			map.addAttribute("success",true);
		}else{
			map.addAttribute("success",false);
		}

		return map;
	}


	/**
	 * Edit Agency POC Info
	 * @param map
	 * @param userInfoDTO
	 * @param request
	 * @param redirectAttributes
	 * @throws Exception
	 */
	@RequestMapping(value = "/update-agency-add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody ModelMap updateAgencyAddress(ModelMap map,
			@ModelAttribute User_info_dto userInfoDTO,
			HttpServletRequest request,  HttpServletResponse response, RedirectAttributes redirectAttributes)
			throws Exception {

		if (logger.isDebugEnabled()){
			logger.debug("/update-agency-add");
		}

		List<InputValidationError> constraintViolations = AuthUtil.validateDTO(userInfoDTO);


		if (!constraintViolations.isEmpty() && null != constraintViolations ){
			return map.addAttribute("inputErrors", constraintViolations);
		}
		
		User_Info loggedInUserInfo = (User_Info) EpdsSession.getAttribute(request, "user_Info");
		
		if (!loggedInUserInfo.getRole_id().equals(GAO_ADMIN.getCode())){
			throw new IllegalAccessError("Unauthorized!!");
		}

		Integer firm_id = userInfoService.getAgencyId(
				userInfoDTO.getTier1_agency_id(),
				userInfoDTO.getTier2_agency_id());

		List<User_Info> agencyPOCUserInfo = userInfoService.getAgencyPOCUserInfos(firm_id);

		for (User_Info eachAgencyPoc : agencyPOCUserInfo){

			eachAgencyPoc.setAddress1(userInfoDTO.getAddress1());
			eachAgencyPoc.setAddress2(userInfoDTO.getAddress2());
			eachAgencyPoc.setCity(userInfoDTO.getCity());
			eachAgencyPoc.setCountry(userInfoDTO.getCountry());
			eachAgencyPoc.setState(userInfoDTO.getState());
			eachAgencyPoc.setZip_Code(userInfoDTO.getZipCode());

			updateAgencyAddress(map, userInfoDTO,eachAgencyPoc,request);
		}

		List<User_Info> user_Info_List = userInfoService
				.getUser_Info_List_AssociatedWithAgency(
						userInfoDTO.getTier1_agency_id(),
						userInfoDTO.getTier2_agency_id());

		for (User_Info eachAgencyPoc : user_Info_List){

			eachAgencyPoc.setAddress1(userInfoDTO.getAddress1());
			eachAgencyPoc.setAddress2(userInfoDTO.getAddress2());
			eachAgencyPoc.setCity(userInfoDTO.getCity());
			eachAgencyPoc.setCountry(userInfoDTO.getCountry());
			eachAgencyPoc.setState(userInfoDTO.getState());
			eachAgencyPoc.setZip_Code(userInfoDTO.getZipCode());

			updateAgencyAddress(map, userInfoDTO,eachAgencyPoc,request);
		}

		return map;
	}



	/**
	 * download user guides and FAQ's
	 * @param request
	 * @param map
	 * @param response
	 * @param userId
	 * @param userGuideType
	 * @throws Exception
	 */
	@RequestMapping(value = "/user-guides", method = RequestMethod.POST)
	public void downloadUserGuidesAndFAQs(HttpServletRequest request, ModelMap map,
			HttpServletResponse response, @RequestParam("userId") String userId, @RequestParam(value="guideType", required = false) String userGuideType) throws Exception {

	    request.setAttribute("userId",Integer.valueOf(userId));

		EPDS_FileUtils fileUtils = new EPDS_FileUtils();
		String baseUserGuidePath = "/resources/user-guides/";
		String userGuideName = "register_login_guide.pdf";
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request, "user_Info");
		ServletOutputStream outputStream = response.getOutputStream();

		if (null != userGuideType && "bestPractices".equalsIgnoreCase(userGuideType)){
			userGuideName = "bestPractices.pdf";
		}else if (null != userGuideType && "faq".equalsIgnoreCase(userGuideType)){
			userGuideName = "faq.pdf";
		}else if (null != userGuideType && "instructions".equalsIgnoreCase(userGuideType)){
			userGuideName = "instructions.pdf";
		}else if (user_Info != null){
            UserRoles role = UserRoles.getByCode(user_Info.getRole_id());
			if (role == GAO_ADMIN){ // GAO ADMIN
			    userGuideName = "plcg.pdf";
			}else if (role == AGENCY_ADMIN || role == AGENCY_ATTORNEY){ //AGENCY REP and AGENCY POC
			    userGuideName = "agency.pdf";
			}else if (role == GAO_ATTORNEY|| role == GAO_SUPERVISOR){ //GAO ATTORNEY and GAO SUPERVISOR
			    userGuideName = "supervisor_attorney.pdf";
			}else if (role == PROTESTER){ //PROTESTER/INTERVENOR
			    userGuideName = "protester.pdf";
				//LOGIN and REGISTRATION page
			}
		}

        File inputFile;
		inputFile = new File(request.getServletContext().getRealPath(baseUserGuidePath + userGuideName));

		response.setContentType("application/pdf");
		response.setHeader("Content-disposition", "inline; filename=\""+ inputFile.getName() + "\"");

		String mimeType= URLConnection.guessContentTypeFromName(inputFile.getName());

        response.setContentType(mimeType);
        response.setHeader("Content-Disposition", String.format("inline; filename=\"" + inputFile.getName() +"\""));
        response.setContentLength((int)inputFile.length());

        fileUtils.downloadFiles(inputFile, outputStream);

	}


	/**
	 * @param map
	 * @param userInfoDTO
	 * @throws NumberFormatException
	 * @throws JsonProcessingException
	 * @throws IOException
	 * @throws Exception
	 */
	private void updateAgencyAddress(ModelMap map, User_info_dto userInfoDTO,User_Info userInfo, HttpServletRequest request)
			throws NumberFormatException, JsonProcessingException, IOException, Exception {

		gov.gao.epds.utils.Util.getPopulatedUserInfoDTOFromUserInfo(userInfo,userInfoDTO);

		userInfoDTO.setAuth_role_id(2);
		userInfoDTO.setTypeOfUpdate("updateProfile");
		userInfoDTO.setAccount_status_id(2);

		String jsonResponse = authUtil.getAuthJSONResponse("updateProfileURI",
				"Y", userInfoDTO,request);

		boolean isUpdateSuccess = AuthUtil
				.getJsonNode(jsonResponse, "isSuccess").asBoolean();

		if (isUpdateSuccess){
			User_Info updatedUsrInfo = userInfoService.updatePersonalInfo(userInfoDTO);

			map.addAttribute("success",true);
		}else{
			map.addAttribute("success",false);
		}
	}

	/**
	 * Change Password from User Profile View
	 * @param map
	 * @param userInfoDTO
	 * @param request
	 * @param redirectAttributes
	 * @throws Exception
	 */
	@RequestMapping(value = "/change-password", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody ModelMap changePassword(ModelMap map,
			@ModelAttribute User_info_dto userInfoDTO,
			HttpServletRequest request, RedirectAttributes redirectAttributes)
			throws Exception {

		List<InputValidationError> constraintViolations = AuthUtil.validateDTO(userInfoDTO);

		if (!constraintViolations.isEmpty() && null != constraintViolations ){
			return map.addAttribute("inputErrors", constraintViolations);
		}
		String user_Role = (String) EpdsSession.getAttribute(request, "user_Role");
		User_Info userInfo = (User_Info) EpdsSession.getAttribute(request, "user_Info");
        UserRoles role = UserRoles.getByCode(userInfo.getRole_id());

		if (logger.isDebugEnabled()){
			logger.debug("/change-password");
		}
		userInfoDTO.setRole(user_Role);
		if (role == GAO_ATTORNEY || role == GAO_ADMIN || role == GAO_SUPERVISOR)  {
			RegistrationUtil.populateGaoUserField(userInfoDTO);
		}

		userInfoDTO.setTypeOfUpdate("changePassword");
		userInfoDTO.setUser_id(Integer.valueOf(userInfo.getUser_Id().trim()));
		String jsonResponse = authUtil.getAuthJSONResponse("updateProfileURI",
				"Y", userInfoDTO,request);

		boolean isUpdateSuccess = AuthUtil
				.getJsonNode(jsonResponse, "isSuccess").asBoolean();

		EpdsSession.setAttribute(request, "numberOfDaysLeftToExpirePwd",30);

		return map;

	}


	/**
	 * Change user name from User Profile View
	 * @param map
	 * @param userInfoDTO
	 * @param request
	 * @param redirectAttributes
	 * @throws Exception
	 */
	@RequestMapping(value = "/update-username", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody ModelMap updateUserName(ModelMap map,
			@ModelAttribute User_info_dto userInfoDTO,
			HttpServletRequest request, RedirectAttributes redirectAttributes)
			throws Exception {


		List<InputValidationError> constraintViolations = AuthUtil.validateDTO(userInfoDTO);


		if (!constraintViolations.isEmpty() && null != constraintViolations ){
			return map.addAttribute("inputErrors", constraintViolations);
		}
		String user_Role = (String) EpdsSession.getAttribute(request, "user_Role");
		User_Info userInfo = (User_Info) EpdsSession.getAttribute(request,
				"user_Info");

		if (user_Role.toUpperCase().contains("PROT") || user_Role.toUpperCase().contains("INT")){

			//extra check

			if (userInfoDTO.getOld_email().equalsIgnoreCase(userInfo.getEmail())){

				String jsonResponse = authUtil.getAuthJSONResponse("updateUserNameURI",
						null, userInfoDTO,request);

				/*String message = AuthUtil.getJsonNode(jsonResponse, "message").asText();*/

				boolean isUpdateSuccess = AuthUtil
						.getJsonNode(jsonResponse, "isSuccess").asBoolean();

				if (isUpdateSuccess){
					try {
						userInfoService.updateEmailAddress(userInfoDTO.getOld_email(),userInfoDTO.getEmail(),
								userInfo);

						EpdsSession.setAttribute(request, "user_Info", userInfo);
						map.addAttribute("isSuccess", true);
					} catch (Exception e) {
						e.printStackTrace();
						map.addAttribute("isSuccess", false);
					}

				}else{
					map.addAttribute("isSuccess", false);
				}
			}else{
				map.addAttribute("isSuccess", false);
			}

		}

		return map;

	}

	/**
	 *
	 * Change Security Questions:  need to implement it...It is not yet impelemted
	 * @param map
	 * @param userInfoDTO
	 * @param request
	 * @param redirectAttributes
	 * @throws Exception
	 */
	@RequestMapping(value = "/change-sec-ques", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody ModelMap changeSecQuestions(ModelMap map,
			@ModelAttribute User_info_dto userInfoDTO,
			HttpServletRequest request, RedirectAttributes redirectAttributes)
			throws Exception {

		String user_Role = (String) EpdsSession.getAttribute(request, "user_Role");
		User_Info userInfo = (User_Info) EpdsSession.getAttribute(request, "user_Info");

		if (logger.isDebugEnabled()){
			logger.debug("/change-password");
		}
		userInfoDTO.setRole(user_Role);

		List<InputValidationError> constraintViolations = AuthUtil.validateDTO(userInfoDTO);

		if (!constraintViolations.isEmpty() && null != constraintViolations ){
			return map.addAttribute("inputErrors", constraintViolations);
		}

        UserRoles role = UserRoles.getByCode(userInfo.getRole_id());
		if (role == GAO_ATTORNEY || role == GAO_ADMIN || role == GAO_SUPERVISOR)  {
			AuthUtil.populateSecQIdToAnswerMap(userInfoDTO);
			RegistrationUtil.populateGaoUserField(userInfoDTO);

		}else if (role == AGENCY_ADMIN || role == AGENCY_ATTORNEY){
			AuthUtil.populateSecQIdToAnswerMap(userInfoDTO);
		}
		userInfoDTO.setTypeOfUpdate("changeSecQues");
		userInfoDTO.setUser_id(Integer.valueOf(userInfo.getUser_Id().trim()));

		String jsonResponse = authUtil.getAuthJSONResponse("updateProfileURI",
				"Y", userInfoDTO,request);

		return map;
	}


	/**
	 * View Agency User Info
	 * @param map
	 * @param tier1_Agency_Id
	 * @param tier2_Agency_Id
	 * @param request
	 * @param redirectAttributes
	 * @throws Exception
	 */
	@RequestMapping(value = "/view-agency-user-info", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody ModelMap viewAgency_User_Info(ModelMap map,
			@RequestParam("tier1_Agency_Id") String tier1_Agency_Id,
			@RequestParam("tier2_Agency_Id") String tier2_Agency_Id,
			HttpServletRequest request, RedirectAttributes redirectAttributes, HttpServletResponse response)
			throws Exception {

		if (logger.isDebugEnabled()){
			logger.debug("/view-agency-user-info");
		}

		if (!tier1_Agency_Id.matches(DTOValidator.AGENCYID_PATTERN)){
			throw new IllegalArgumentException("Invalid Input format!!");
		}

		User_Info userInfo = (User_Info) EpdsSession.getAttribute(request,"user_Info");

        UserRoles role = UserRoles.getByCode(userInfo.getRole_id());
		if (role != GAO_ADMIN && role != AGENCY_ADMIN && role != AGENCY_ATTORNEY){
			throw new IllegalAccessError("Unauthorized!!");
		}

		if (null != tier2_Agency_Id &&
				!"null".equalsIgnoreCase(tier2_Agency_Id)
				&& "undefined".equalsIgnoreCase(tier2_Agency_Id)
				&& !tier2_Agency_Id.matches(DTOValidator.AGENCYID_PATTERN)){
			throw new IllegalArgumentException("Invalid Input format!!");
		}
		List<User_Info> user_Info_List = userInfoService
				.getUser_Info_List_AssociatedWithAgency(tier1_Agency_Id,
						tier2_Agency_Id);
		if (null != user_Info_List && user_Info_List.size() > 0){

			user_Info_List.removeAll(Collections.singleton(null));
		}else{
			user_Info_List = null;
		}

		map.addAttribute("user_Info_List", user_Info_List);

		return map;
	}


	@RequestMapping(value = "/validate-gao-id", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody ModelMap validateGAOId(ModelMap map,
			@RequestParam("gaoId") Integer gaoId,
			HttpServletRequest request)
			throws Exception {
		
		User_Info userInfo = (User_Info) EpdsSession.getAttribute(request, "user_Info");
		
		if (!userInfo.getRole_id().equals(GAO_ADMIN.getCode())){
			throw new IllegalAccessError("Unauthorized!!");
		}
		
		boolean isExists = userInfoService.checkIfGAOIdExists(gaoId);

		map.addAttribute("isExists", isExists);
		map.addAttribute("gaoId", gaoId);

		return map;
	}
	
	@RequestMapping(value = "/notification", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody ModelMap notifyAllSystemUsers(ModelMap map,
			EmailNotification emailNotification,
			HttpServletRequest request)
			throws Exception {
		
		User_Info userInfo = (User_Info) EpdsSession.getAttribute(request,"user_Info");

		if (userInfo.getRole_id().equals(GAO_ADMIN.getCode())){
			userInfoService.notifyAllSystemUsers(emailNotification);
		}

		return map;
	}
}
