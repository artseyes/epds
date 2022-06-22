package gov.gao.epds.rest.auth.services;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.validator.routines.EmailValidator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import gov.gao.epds.dto.DTOValidator;
import gov.gao.epds.dto.InputValidationError;
import gov.gao.epds.dto.LoginDTO;
import gov.gao.epds.dto.User_info_dto;
import gov.gao.epds.enums.UserRoles;
import static gov.gao.epds.enums.UserRoles.*;
import gov.gao.epds.persistence.entity.User_Info;
import gov.gao.epds.service.UserInfoService;
import gov.gao.epds.session.EpdsSession;
import gov.gao.epds.utils.ClientInfo;
import gov.gao.epds.utils.GlobalParams;
import gov.gao.epds.utils.PreAuthUtil;
import gov.gao.epds.utils.PropertyFileEncrypter;
import gov.gao.epds.utils.RegistrationUtil;
import gov.gao.epds.utils.Util;

@Controller
public class AuthenticationController { // NO_UCD (unused code)
	
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
	
	
	@Autowired
	UserInfoService userInfoService;
	@Autowired
	AuthUtil authUtil;
	private final RestTemplate restTemplate;
	
	@Autowired
	public AuthenticationController(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@RequestMapping(value = { "/" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody ModelMap homePage(HttpServletRequest req,
			HttpServletResponse res,
			ModelMap model) throws JsonProcessingException, IOException {
		
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(req,"user_Info");
		
		if (user_Info != null){
			model.addAttribute("roleId", user_Info.getRole_id());	
		}
		
		
		return model;
		 
	}
	
	
	/**
	 * Handles the request to authenticate the User to EPDS
	 * @param model
	 * @param req
	 * @param response
	 * @param loginDTO
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = { "/user/login" }, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody ModelMap loginPage(ModelMap model, HttpServletRequest req,
			HttpServletResponse response, @RequestBody LoginDTO loginDTO) throws Exception {
		loginDTO = userInfoService.getEmail(loginDTO);
		ClientInfo.printClientInfo(req,loginDTO.getEmail());
		// HttpSession session = req.getSession();
		Boolean isUserSessionExists = false;
		User_info_dto user_info_dto = new User_info_dto();
		user_info_dto.setClient_ip(Util.getRemoteIp(req));
		user_info_dto.setEmail(loginDTO.getEmail());

		List<InputValidationError> constraintViolations = AuthUtil.validateDTO(loginDTO);
		EmailValidator validator = EmailValidator.getInstance();
		
		if (!constraintViolations.isEmpty() && null != constraintViolations ){
			model.addAttribute("inputErrors", constraintViolations);
			return model;
		}

		if (!validator.isValid(loginDTO.getEmail().trim())){
			throw new IllegalArgumentException("Email is not Valid");
		}
		 
		user_info_dto.setPassword(loginDTO.getPassword());

		String jsonResponse = authUtil.getAuthJSONResponse("accessURI", "",user_info_dto,req);
		String exception = AuthUtil.getJsonNode(jsonResponse, "exception").asText();
		String stackTraceDetail = AuthUtil.getJsonNode(jsonResponse, "stackTraceDetail").asText();
		if ( (null != exception && !exception.equals("null")) || (null != stackTraceDetail && !stackTraceDetail.equals("null")) ) {
			logger.error("loginPage getAuthJSONResponse exception: {}", exception);
			logger.error("loginPage getAuthJSONResponse stackTraceDetail: {}", stackTraceDetail);
		}

		String token = AuthUtil.getJsonNode(jsonResponse, "token").asText();
		String authMessage = AuthUtil.getJsonNode(jsonResponse, "message").asText();
		
		JsonNode user_info_dto_response = AuthUtil.getJsonNode(jsonResponse,"data");
		
		String uiMessage = AuthUtil.convertToUIMessage(authMessage);
		
		boolean isLoginSuccess = AuthUtil.getJsonNode(jsonResponse, "isSuccess").asBoolean();

		User_info_dto userInfoDTO = (User_info_dto) Util.convertToObject(
				user_info_dto_response.toString(), false, User_info_dto.class);
		
		//if the user is active and successfully logs in then we can check if the session already exists.
		
		if (isLoginSuccess 
				&& (null != userInfoDTO.getAccount_status_id() && userInfoDTO.getAccount_status_id() == 2)){
			
			User_Info userInfo = userInfoService.getUser_InfoByEmail(loginDTO.getEmail());
			if (userInfo != null){
				isUserSessionExists = EpdsSession.checkIfSessionExists(Integer.valueOf(userInfo.getUser_Id()));	
			}
			
			if (isUserSessionExists){
				
				req.setAttribute("userId", Integer.valueOf(userInfo.getUser_Id()));
				logger.info("User for userId={}, requestUrl ={}, eventType={}", userInfo.getUser_Id(), req.getRequestURL(), "Ending Existing Session");
				EpdsSession.endUserSession(req);
				isUserSessionExists = false;
			}
		}
		
	
		if (isUserSessionExists){
			
			model.addAttribute("isUserSessionExists", isUserSessionExists);
			model.addAttribute("isLoginSuccess", false);
			model.addAttribute("message", uiMessage);
			
		}else if (isLoginSuccess && !isUserSessionExists) {
			
			model.addAttribute("isUserSessionExists", isUserSessionExists);
			model.addAttribute("isLoginSuccess", isLoginSuccess);
			model.addAttribute("message", uiMessage);
			model.addAttribute("data", user_info_dto_response);
			setLoginSuccessAttributes(model, req, response, token,user_info_dto_response);
			
		}else{
			model.addAttribute("isLoginSuccess", isLoginSuccess);
			model.addAttribute("message", uiMessage);
			model.addAttribute("data", user_info_dto_response);
		}

		return model;
	}

	
	private void setLoginSuccessAttributes(ModelMap model,
			HttpServletRequest req, HttpServletResponse response, String token,
			JsonNode user_info_dto_response) throws JsonParseException,
			JsonMappingException, IOException, Exception {
		
		User_info_dto userInfoDTO = (User_info_dto) Util.convertToObject(
				user_info_dto_response.toString(), false, User_info_dto.class);

		if (userInfoDTO.getAccount_status_id() == 2){
			
			EpdsSession.startUserSession(userInfoDTO.getUser_id(), token);
			req.setAttribute("userId", userInfoDTO.getUser_id());
			EpdsSession.setAttribute(req, "numberOfDaysLeftToExpirePwd",userInfoDTO.getNumOfDaysLeftToExpirePwd());
			EpdsSession.setAttribute(req, "lastReqTimeStamp", new Date());
			EpdsSession.setAttribute(req, "clientIP", Util.getRemoteIp(req));
			
			AuthUtil.setUniqueSessionId(response,req);
		}
		
		
		User_Info user_info = userInfoService.getUserProfileInfo(
				userInfoDTO.getUser_id() + "", req);
		userInfoService.getUserRole(req, user_info);

		userInfoDTO.setRole(user_info.getRole_id() + "");
		userInfoDTO.setAuth_token(token);
		model.addAttribute("data", userInfoDTO);

		if (token != null && !token.equalsIgnoreCase("null")) {
			
			model.addAttribute("sessionValid", true);
			EpdsSession.setAttribute(req, "userLoggedInTimeStamp", new Date());
			AuthUtil.setAuthorizationToken(response, token, req);
			
		}
	}
	
	/*@RequestMapping(value = { "/user/invalidateSession" }, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public void invalidateUserSession(HttpServletRequest request, ModelMap model, LoginDTO dto)
			throws Exception {
		
		EmailValidator validator = EmailValidator.getInstance();
		
		if (validator.isValid(dto.getEmail().trim()) && null != dto.getEmail()){
			
			User_Info userInfo = userInfoService.getUser_InfoByEmail(dto.getEmail());
			
			if (null != userInfo){

				request.setAttribute("userId", Integer.valueOf(userInfo.getUser_Id()));
				EpdsSession.endUserSession(request);
			}
			
		}
		
		
	}*/

	@RequestMapping(value = { "/user/register" }, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody ModelMap registerUser(ModelMap model, @RequestBody User_info_dto user_info_dto, BindingResult results, HttpServletRequest request)
			throws Exception {
		
		/*
		 * PSEUDOCODE - populate user_info_dto with firm_id - register user_info
		 * (x) in epds_auth with user_info_dto - register user_info in epds with
		 * user_info (x)
		 */

		boolean isSuccess = true;

		UserRoles role = UserRoles.getByCode(user_info_dto.getEpds_role_id());
		if (role != AGENCY_ATTORNEY && role != PROTESTER){
			throw new IllegalArgumentException("Invalid Input");
		}
		
		if (role == AGENCY_ATTORNEY && GlobalParams.IP.equals(GlobalParams.PROD_IP_ADDR)
				&& (!user_info_dto.getEmail().endsWith(".gov") 
						&& !user_info_dto.getEmail().endsWith(".mil") 
				&& !user_info_dto.getEmail().endsWith(".edu") )){
			throw new IllegalArgumentException("Email Address should contains .gov or .mil");
		}
		
		byte[] base64decodedBytes = java.util.Base64.getDecoder().decode(user_info_dto.getEmail().trim());
		
		user_info_dto.setEmail(new String(base64decodedBytes, "utf-8"));
		
		ClientInfo.printClientInfo(request,user_info_dto.getEmail());
		List<InputValidationError> constraintViolations = AuthUtil.validateDTO(user_info_dto);
		
		
		if (!constraintViolations.isEmpty() && null != constraintViolations ){
			
			return model.addAttribute("inputErrors", constraintViolations);
		}
		
		EmailValidator validator = EmailValidator.getInstance();
		
		if (!validator.isValid(user_info_dto.getEmail().trim())){
			throw new IllegalArgumentException("Email is not Valid");
		}
		
		User_Info userInfo = RegistrationUtil.registerUser(user_info_dto,
				userInfoService, "", false,request);

		if (userInfo == null) {
			isSuccess = false;
		}
		
		model.addAttribute("isSuccess", isSuccess);
		

		return model;
	}
	
	
	@RequestMapping(value = { "/user/validate-register-form" }, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody ModelMap validateRegisterUser(ModelMap model, @RequestBody User_info_dto user_info_dto, BindingResult results)
			throws Exception {
		
		boolean isSuccess = true;

		UserRoles role = UserRoles.getByCode(user_info_dto.getEpds_role_id());
		if (role != AGENCY_ATTORNEY && role != PROTESTER){
			throw new IllegalArgumentException("Invalid Input");
		}
		
		byte[] base64decodedBytes = java.util.Base64.getDecoder().decode(user_info_dto.getEmail().trim());
		
		user_info_dto.setEmail(new String(base64decodedBytes, "utf-8"));
		
		List<InputValidationError> constraintViolations = AuthUtil.validateDTO(user_info_dto);

		if (!constraintViolations.isEmpty() && null != constraintViolations ){
			
			return model.addAttribute("inputErrors", constraintViolations);
		}
		
		EmailValidator validator = EmailValidator.getInstance();
		
		if (!validator.isValid(user_info_dto.getEmail().trim())){
			throw new IllegalArgumentException("Email is not Valid");
		}
		
		model.addAttribute("isSuccess", isSuccess);
		
		return model;
	}

	
	@RequestMapping(value = { "/user/update-profile/{isActiveUser}" }, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody
	ModelMap updateUserProfile(ModelMap model, User_info_dto user_info_dto,
			@PathVariable("isActiveUser") String isActiveUser,
			HttpServletRequest req, HttpServletResponse response)
			throws Exception {
		
		
		
		EmailValidator validator = EmailValidator.getInstance();
		
		if (user_info_dto.getEmail() != null){

			if (!validator.isValid(user_info_dto.getEmail().trim())){
				throw new IllegalArgumentException("Email is not Valid");
			}
			
		}else if (!isActiveUser.matches(GlobalParams.regexWithAlphaChars) && isActiveUser.length() != 1){
			throw new IllegalArgumentException("Illegal Character");
		}
		
		
		AuthUtil.populateSecQIdToAnswerMap(user_info_dto);
		user_info_dto.setClient_ip(Util.getRemoteIp(req));
		/*user_info_dto.setPassword(new String(Base64.encodeBase64(user_info_dto.getPassword().getBytes()), "UTF-8"));*/
		/*user_info_dto.setClient_ip(Util.getRemoteIp(req));*/

		String jsonResponse = authUtil.getAuthJSONResponse("updateProfileURI",
				isActiveUser, user_info_dto,req);
		String token = AuthUtil.getJsonNode(jsonResponse, "token").asText();
		boolean isSuccess = AuthUtil.getJsonNode(jsonResponse, "isSuccess")
				.asBoolean();
		JsonNode user_info_dto_response = AuthUtil.getJsonNode(jsonResponse,
				"data");

		model.addAttribute("isSuccess", isSuccess);
		model.addAttribute("data", user_info_dto_response);

		if (isSuccess) {
			setLoginSuccessAttributes(model, req, response, token,
					user_info_dto_response);
		}
		return model;
	}

	@RequestMapping(value = { "/user/check-if-user-exists/" }, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public @ResponseBody ModelMap checkIfUserExists(
			@RequestBody LoginDTO loginDTO, ModelMap model)
			throws IOException {
		/* user_email = new String(Base64.decodeBase64(user_email), "UTF-8"); */
		
		loginDTO = userInfoService.getEmail(loginDTO);

		EmailValidator validator = EmailValidator.getInstance();
		
		if (validator.isValid(loginDTO.getEmail().trim()) && loginDTO.getEmail().trim().matches(DTOValidator.EMAIL_PATTERN) ){
			
			String response = authUtil.getForObject("checkEmailExistsURI",
					loginDTO.getEmail(), String.class);

			JsonNode message = AuthUtil.getJsonNode(response, "message");

			model.addAttribute("message", message);
		}else{
			model.addAttribute("message", "Email doesn't contain valid characters");
		}
		
		
		return model;
	}

	@RequestMapping(value = { "/user/validate-password" }, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public void checkIfThisPasswordIsValid(
			@RequestParam("user_id") 
			String user_id,
			@RequestParam("password") String password, ModelMap model,HttpServletRequest request)
			throws IOException {

		/*password = new String(Base64.encodeBase64(password.getBytes()), "UTF-8");*/
		 String validUserId = null;
		if (!user_id.matches(GlobalParams.regexWithNumericChars)){
			throw new IllegalArgumentException("User Id is not valid");
		}else{
			validUserId = user_id;
		}
		User_info_dto user_info_dto = new User_info_dto();
		user_info_dto.setUser_id(Integer.valueOf(validUserId));
		user_info_dto.setPassword(password);
		
		String response = authUtil.getAuthJSONResponse("validatePasswordURI",null, user_info_dto,request);
		JsonNode message = AuthUtil.getJsonNode(response, "message");

		JsonNode arrNode = null;
		
		if(response!=null){
			arrNode = /*new ObjectMapper().readTree(response).get(
					"data")*/AuthUtil.getJsonNode(response,"data");
		}
		List<String> invalidPasswordMessagesList = new ArrayList<String>();
		if (null != arrNode && 
				null != response 
				&& arrNode.isArray()) {
			for (JsonNode objNode : arrNode) {
				invalidPasswordMessagesList.add(objNode.asText());
			}
		}
		model.addAttribute("message", message);
		model.addAttribute("data", invalidPasswordMessagesList);
	}

	@RequestMapping(value = { "/user/validate-old-password" }, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public void validateOldPassword(
			@RequestParam("user_id") String user_id,
			@RequestParam("password") String password, ModelMap model, HttpServletRequest request)
			throws IOException {
		
		
		
		User_info_dto user_info_dto = new User_info_dto();
		user_info_dto.setUser_id(Integer.valueOf(user_id));
		user_info_dto.setOldPassword(password);
		
		String response = authUtil.getAuthJSONResponse("validateOldPasswordURI",null, user_info_dto,request);
		JsonNode message = AuthUtil.getJsonNode(response, "message");
		
		model.addAttribute("message", message);
	}
	@RequestMapping(value = { "/user/get-security-questions" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	public void getListOfSecurityQuestions(ModelMap model) throws IOException {

		String response = authUtil.getForObject("getListOfSecurityQuestionURI",
				"", String.class);
		JsonNode message = AuthUtil.getJsonNode(response, "data");

		model.addAttribute("list", message);
	}

	@RequestMapping(value = { "user/forgot-password/submit-email" }, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public void getResponseForForgotPasswordEmailSubmission(ModelMap model,
			@RequestParam(value = "email") String email) {
		String message = null;
		JsonNode user_info_dto_response = null;
		
		if (logger.isDebugEnabled()){
			logger.debug("user/forgot-password/submit-email email = {}", email);
		}

		
		try {
			String jsonResponse = authUtil.getForObject(
					"forgotPasswordSubmitEmailURI", email, String.class);

			message = AuthUtil.getJsonNode(jsonResponse, "message").asText();
			user_info_dto_response = AuthUtil.getJsonNode(jsonResponse, "data");
			User_info_dto userInfoDTO = (User_info_dto) Util.convertToObject(
					user_info_dto_response.toString(), false, User_info_dto.class);
			
			
			if (null != userInfoDTO && !userInfoDTO.getAccount_status_id().equals(1)
					&&  (message.equalsIgnoreCase("ROLE: NON-VENDOR") || (message.equalsIgnoreCase("ROLE: VENDOR")))){
				model.addAttribute("message", message);
				model.addAttribute("data", user_info_dto_response);	
			}else if (null != userInfoDTO && userInfoDTO.getAccount_status_id().equals(1) && message.equalsIgnoreCase("ROLE: NON-VENDOR")){
				model.addAttribute("message", message);
				model.addAttribute("isNonVendorNewAcct", true);
				model.addAttribute("data", user_info_dto_response);	
			} else{
				model.addAttribute("message", message);
				model.addAttribute("data", user_info_dto_response);	
			}
		} catch (Exception e) {
			logger.error("Exception occured while processing forgot password request -----------> ",e.getMessage());
		}

		
		
		
	}

	@RequestMapping(value = { "user/forgot-password/check-security-answer/" }, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public void checkSecurityAnswer(ModelMap model,
			@RequestParam(value = "user_id") String user_id,
			@RequestParam(value = "secQId") String secQId,
			@RequestParam(value = "answer") String answer,
			@RequestParam(value = "numberOfAttempts") Integer numberOfAttempts) {

		/*
		 * Input: user_id, secQId, answer, count Output: isCorrect (Y/N), count
		 */
		if (logger.isDebugEnabled()){
			try {
				logger.debug("user/forgot-password/check-security-answer/ userType = {}", new String(Base64.encodeBase64(user_id.getBytes()), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		String isCorrect = null;
		String responseMessage = "";
		try {
			String jsonResponse = authUtil.getForObject("checkSecurityAnswer",
					user_id + "/" + secQId + "/" + answer + "/"
							+ numberOfAttempts, String.class);

			isCorrect = AuthUtil.getJsonNode(jsonResponse, "data").asText();

			if (numberOfAttempts == 6) {
				responseMessage = AuthUtil.getJsonNode(jsonResponse, "")
						.asText();
			}
		} catch (Exception e) {
			logger.error("exception occurred Exception={}",e.getMessage());
			model.addAttribute("exception", e.getMessage());
			model.addAttribute("stackTraceDetail", Util.getStackTraceMessage(e));
		}

		model.addAttribute("message", responseMessage);
		model.addAttribute("isCorrect", isCorrect);
		model.addAttribute("numberOfAttempts", ++numberOfAttempts);
	}

	@RequestMapping(value = { "user/bulk-upload/{userType}" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	public void makeBulkUpload(@PathVariable("userType") String userType,HttpServletRequest request)
			throws JsonProcessingException, IOException, Exception {
		
		ClientInfo.printClientInfo(request,"System Executed Script");
		
		if (logger.isDebugEnabled()){
			logger.debug("user/bulk-upload userType = {}", userType);
		}
		
		RegistrationUtil.bulkUpload(userType, userInfoService,request);
	}

	@RequestMapping(value = { "/user/get-user-info/{email}" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	public void getUserInfo(@PathVariable("email") String email, ModelMap model)
			throws JsonProcessingException, IOException {
		
		
		if (logger.isDebugEnabled()){
			logger.debug("/user/get-user-info email = {}", email);
		}
		
		
		String jsonResponse = authUtil.getForObject("getUserInfoURI", email,String.class);

		boolean isSuccess = AuthUtil.getJsonNode(jsonResponse, "isSuccess")
				.asBoolean();

		JsonNode userInfoDTO = AuthUtil.getJsonNode(jsonResponse, "data");
		if (isSuccess) {
			model.addAttribute("data", userInfoDTO);
		}

		model.addAttribute("isSuccess", isSuccess);
	}

	@RequestMapping(value = { "/user/reset-account/{userId}" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	public void resetAccount(@PathVariable("userId") String userId,
			ModelMap model, HttpServletRequest request) throws JsonProcessingException, IOException {
		
		
		if (logger.isDebugEnabled()){
			try {
				logger.debug("/user/reset-account/ userId = {}", new String(Base64.encodeBase64(userId.getBytes()), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		String response = authUtil.getAuthJSONResponse("resetAccountURI", userId,
				new User_info_dto(),request);

		boolean isSuccess = AuthUtil.getJsonNode(response, "isSuccess")
				.asBoolean();

		model.addAttribute("isSuccess", isSuccess);
		
		
	}
	
	@RequestMapping(value = { "/user/rulesOfBehavior" }, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	public ModelMap rulesOfBehavior(@RequestParam("email") String email,
			ModelMap model,HttpServletRequest request) throws JsonProcessingException, IOException {
		
		
		if (logger.isDebugEnabled()){
			logger.debug("/user/ruleOfBehavior/ email = {}", email);
		}
		
		User_info_dto user_info_dto = new User_info_dto();
		
		user_info_dto.setEmail(email);
		byte[] base64decodedBytes = java.util.Base64.getDecoder().decode(user_info_dto.getEmail().trim());
		
		user_info_dto.setEmail(new String(base64decodedBytes, "utf-8"));
		
		List<InputValidationError> constraintViolations = AuthUtil.validateDTO(user_info_dto);
		
		
		if (!constraintViolations.isEmpty() && null != constraintViolations ){
			
			return model.addAttribute("inputErrors", constraintViolations);
		}
		
		String jsonResponse = authUtil.getAuthJSONResponse("rulesOfBehaviorURI", "",user_info_dto,request);
		

		boolean isSuccess = AuthUtil.getJsonNode(jsonResponse, "isSuccess")
				.asBoolean();

		model.addAttribute("isSuccess", isSuccess);
		
		return model;
		
		
	}

	/**
	 * Refresh token service is used to refresh token ..... UI pings the server every minute.. But we update the token only after every 5 minutes.
	 * @param request
	 * @param response
	 * 
	 */
	
	@RequestMapping(value = { "/refresh" },method = RequestMethod.GET)
	public void refreshToken(HttpServletRequest request,
			HttpServletResponse response)  {

		
		Date lastActiveRequest = (Date) EpdsSession.getAttribute(request, "refreshedTokenTimeStamp");
		
		if (lastActiveRequest == null){
			lastActiveRequest = (Date) EpdsSession.getAttribute(request, "userLoggedInTimeStamp");
		}
		
		if (PreAuthUtil.checkIfDifferenceBetweenLastActiveRequestExceedsThreshold(request,5,lastActiveRequest)){
			
			
			boolean isValid = false;

			String token = PreAuthUtil.extractToken(request);

			String newToken = null;
			String jsonResponse = null;
	
			try {
				
				jsonResponse = restTemplate.postForObject(PropertyFileEncrypter.decrypt(GlobalParams.prop.getProperty("refreshTokenURI")), PreAuthUtil.createBody(request,token, Util.getRemoteIp(request)), String.class);
				
				isValid = AuthUtil.getJsonNode(jsonResponse, "isSuccess").asBoolean();
				if (isValid) {
					newToken = AuthUtil.getJsonNode(jsonResponse, "token").asText();
					EpdsSession.setAttribute(request, "refreshedTokenTimeStamp", new Date());
					AuthUtil.setAuthorizationToken(response, newToken, request);
					logger.info("Token refreshed at date = {}", new Date());

				}
			} catch (JsonProcessingException e) {
				logger.error("Refreshing Token failed due to JSON Processing Exception = {} , response ={}", e,response);
				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
		};
	}
}
