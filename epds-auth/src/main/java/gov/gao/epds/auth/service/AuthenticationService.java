package gov.gao.epds.auth.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import gov.gao.epds.auth.dto.AuthParam;
import gov.gao.epds.auth.dto.ServiceResponse;
import gov.gao.epds.auth.dto.User_info_dto;
import gov.gao.epds.auth.dto.User_session;
import gov.gao.epds.auth.dto.ValidateToken;
import gov.gao.epds.auth.persistence.entity.User_info;
import gov.gao.epds.auth.utils.PasswordUtil;
import gov.gao.epds.auth.utils.SpringApplicationContext;
import gov.gao.epds.auth.utils.Util;
import gov.gao.epds.tokenutils.TokenUtils;

@Path("/auth-service")
public class AuthenticationService {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
	
	
	private LoginService loginService = (LoginService) SpringApplicationContext
			.getBean("loginService");
	
	private UserService userService = (UserService) SpringApplicationContext
			.getBean("userService");
	
	@Autowired
	private EmailService emailService = (EmailService) SpringApplicationContext
			.getBean("emailService");
	
	@Autowired
	private AccountUnlockService accountUnlockService = (AccountUnlockService) SpringApplicationContext
			.getBean("accountUnlockService");
	
	@Context
	private static HttpServletRequest httpRequest;

	 
	

	// temporary
	static {
		
		User_session user_session = new User_session();
		user_session.setUser_id(65537);
		AuthParam.mapOfUserAuthTokenToUserSession.put("aeq142", user_session);
	}

	/**
	 * This is used to register or update account information
	 * @param user_info_dto
	 * @param requestType
	 * @param isActiveUser
	 * @return
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@POST
	@Path("/user/{requestType}/{isActiveUser}")
	@Consumes("application/json")
	public Response registerOrUpdateUser(User_info_dto user_info_dto,
			@PathParam("requestType") String requestType,
			@PathParam("isActiveUser") String isActiveUser)
			throws JsonGenerationException, JsonMappingException, IOException {
		
		logger.info("/user/requestType/isActiveUser requestType = {}, isActiveUser = {}, type Of Update {}", requestType,isActiveUser, user_info_dto.getTypeOfUpdate());
		
		if (logger.isDebugEnabled()){
			logger.debug("/user/requestType/isActiveUser requestType = {}, isActiveUser = {}", requestType,isActiveUser);
		}
		
		String userId = (String) httpRequest.getHeader("userId");
		
		
		
		
		if (user_info_dto.getPassword() != null){
			user_info_dto.setPassword(PasswordUtil.getPwdFromBase64(user_info_dto.getPassword()));
		}
		
		if (user_info_dto.getTypeOfUpdate() != null && 
				user_info_dto.getTypeOfUpdate().equalsIgnoreCase("changePassword")){
			user_info_dto.setPassword(PasswordUtil.getPwdFromBase64(user_info_dto.getNewPassword()));
			user_info_dto.setOldPassword(PasswordUtil.getPwdFromBase64(user_info_dto.getOldPassword()));
		}
		
		ServiceResponse serviceResponse = userService.registerOrUpdateUser(
				user_info_dto, requestType, isActiveUser);
		boolean isAuthentication = false;
		
		if (requestType.equalsIgnoreCase("update")){
			isAuthentication = true;
		}
		
		try {
			if("updateProfile".equalsIgnoreCase(user_info_dto.getTypeOfUpdate())){
				isAuthentication = false;
			}
			
			if("changeSecQues".equalsIgnoreCase(user_info_dto.getTypeOfUpdate())){
				isAuthentication = false;
			}
		}catch (Exception e){
			e.printStackTrace();
		}

		return Util.getRestResponse(serviceResponse, isAuthentication,user_info_dto.getClient_ip());
	}

	@GET
	@Path("/check-if-user-exists/{user_email}")
	@Produces("application/json")
	public ServiceResponse checkIfUserExists(
			@PathParam("user_email") String user_email) {
		
		
		if (logger.isDebugEnabled()){
			logger.debug("/check-if-user-exists/user_email");
		}
		ServiceResponse serviceResponse = userService
				.checkIfUserEmailAlreadyExist(user_email);

		return serviceResponse;
	}

	/**
	 * @param user_info_dto
	 * @return
	 * @throws Exception
	 */
	
	@PUT
	@Path("/user/change-password")
	@Consumes("application/json")
	public Response changePassword(User_info_dto user_info_dto)
			throws Exception {
		
		if (logger.isDebugEnabled()){
			logger.debug("/user/change-password");
		}
		
		user_info_dto.setOldPassword(PasswordUtil.getPwdFromBase64(user_info_dto.getOldPassword()));
		user_info_dto.setNewPassword(PasswordUtil.getPwdFromBase64(user_info_dto.getNewPassword()));
		
		ServiceResponse serviceResponse = userService.changeUserPassword(
				user_info_dto.getEmail(), user_info_dto.getOldPassword(),
				user_info_dto.getNewPassword(), null, false);

		return Util.getRestResponse(serviceResponse, false, "");
	}

	
	//Amer  : This is not being used anywhere in the project may be we can just get rid of this
	/**
	 * @param user_info_dto
	 * @return
	 * @throws Exception
	 */
	@PUT
	@Path("/user/update-password-and-or-security-answers")
	@Consumes("application/json")
	public Response updatePasswordAndOrSecurityAnswers(
			User_info_dto user_info_dto) throws Exception {
		
		if (logger.isDebugEnabled()){
			logger.debug("/user/update-password-and-or-security-answers");
		}
		
		if (user_info_dto.getPassword() != null){
			final char[] password = new String(Base64.decodeBase64(user_info_dto.getPassword()), "UTF-8").toCharArray();
			
			user_info_dto.setPassword(String.valueOf(password));
			
			Arrays.fill(password, ' ');
			
		}

		ServiceResponse serviceResponse = userService
				.updatePasswordAndOrSecurityAnswers(user_info_dto,false);

		return Util.getRestResponse(serviceResponse, true,
				user_info_dto.getClient_ip());
	}

	/**
	 * This is added temporarily to test email functionality. Need to remove this
	 * @return
	 */
	@GET
	@Path("/test-email")
	@Produces("application/json")
	public ServiceResponse testEmail() {
		ServiceResponse serviceResponse = emailService.testEmail();

		return serviceResponse;
	}

	/**
	 * Authenticate Users
	 * @param user_info_dto
	 * @return
	 */
	@POST
	@Path("/authenticate-user")
	@Consumes("application/json")
	public Response authenticateUser(User_info_dto user_info_dto) {
		if (logger.isDebugEnabled()){
			logger.debug("/authenticate-user");
		}

		ServiceResponse serviceResponse = loginService.getLoginResponse(user_info_dto);

		/*List<User_info> user_infos = userService.getListOfUserInfo();
		
		ServiceResponse serviceResponse = new ServiceResponse();*/
		
		return Util.getRestResponse(serviceResponse, true,
				user_info_dto.getClient_ip());
	}

	/**
	 * Validate new password
	 * @param user_info_dto
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@POST
	@Path("/user/validate-password")
	@Produces("application/json")
	public ServiceResponse validateNewPassword(User_info_dto user_info_dto) throws UnsupportedEncodingException {
		
		if (logger.isDebugEnabled()){
			logger.debug("/user/validate-password");
		}
		
		String password = PasswordUtil.getPwdFromBase64(user_info_dto.getPassword());
		
		return userService.getPasswordValidationResponse(password, user_info_dto.getUser_id());
	}

	
	@POST
	@Path("/user/validate-old-password/")
	@Produces("application/json")
	public ServiceResponse validatePassword(User_info_dto user_info_dto) throws UnsupportedEncodingException {
		
		if (logger.isDebugEnabled()){
			logger.debug("/user/validate-old-password/");
		}
		
		
		user_info_dto.
		setOldPassword(PasswordUtil.generateHash(PasswordUtil.
				getPwdFromBase64(user_info_dto.getOldPassword())));
		
		
		
		return userService.getPasswordValidationResponse(user_info_dto);
	}
	
	@POST
	@Path("/user/update-username/")
	@Produces("application/json")
	public ServiceResponse updateUserName(User_info_dto user_info_dto) throws UnsupportedEncodingException {
		
		if (logger.isDebugEnabled()){
			logger.debug("/user/update-username/");
		}
		
		user_info_dto.setPassword(PasswordUtil.generateHash(PasswordUtil.getPwdFromBase64(user_info_dto.getOldPassword())));
		
		return userService.updateUserName(user_info_dto);
	}
	
	/**
	 * Update Security Questions 
	 * @param user_info_dto
	 * @return
	 */
	@POST
	@Path("/update-security-questions")
	@Consumes("application/json")
	public Response updateSecurityQuestions(User_info_dto user_info_dto) {
		
		if (logger.isDebugEnabled()){
			logger.debug("/update-security-questions");
		}
		
		ServiceResponse serviceResponse = userService.addSecurityQuestions(
				user_info_dto, user_info_dto.getUser_id());

		return Util.getRestResponse(serviceResponse, false, "");
	}

	/**
	 * Retrieve list of security questions
	 * @return
	 */
	@GET
	@Path("/get-security-questions")
	@Produces("application/json")
	public ServiceResponse getSecurityQuestions() {
		
		if (logger.isDebugEnabled()){
			logger.debug("/get-security-questions");
		}
		
		return userService.getListOfSecurityQuestion();
	}

	/*
	 * @POST
	 * 
	 * @Path("/send-temporary-password/{user_email}")
	 * 
	 * @Consumes("application/json") public ServiceResponse
	 * sendTemporaryPassword(
	 * 
	 * @PathParam("user_email") String user_email) { ServiceResponse
	 * serviceResponse = userService .sendTemporaryPasswordToUser(user_email);
	 * 
	 * return serviceResponse; }
	 */

	/**
	 * For every request to protected resources EPDS validates the token before granting access to protected resources
	 * @param validateToken
	 * @return
	 * @throws InterruptedException 
	 */
	@POST
	@Path("/validate-token")
	@Consumes("application/json")
	public Response validateToken(ValidateToken validateToken) throws InterruptedException {
		
		if (logger.isDebugEnabled()){
			logger.debug("/validate-token validateToken = {}", validateToken.toString());
		}
		ServiceResponse tokenServiceResponse = TokenService.validateToken(
				validateToken.getToken(), validateToken.getRemoteIp());
		
		return Util.getRestResponse(tokenServiceResponse);
	}
	
	/**
	 * @param dto
	 * @return
	 */
	@POST
	@Path("/rules-of-behavior")
	@Consumes("application/json")
	public Response rulesOfBehavior(User_info_dto dto) {
		
		if (logger.isDebugEnabled()){
			logger.debug("/validate-token validateToken = {}", dto.toString());
		}
		ServiceResponse serviceResponse = loginService.rulesOfBehavior(dto.getEmail());

		return Util.getRestResponse(serviceResponse);
	}

	/** This service is used to refresh the token periodically after 5 mins
	 * @param validateToken : Dto which contains the client IP and token
	 * @return
	 * @throws InterruptedException 
	 */
	@POST
	@Path("/refresh-token")
	@Consumes("application/json")
	public Response refreshToken(ValidateToken validateToken) throws InterruptedException {
		
		if (logger.isDebugEnabled()){
			logger.debug("/refresh-token validateToken = {}", validateToken);
		}
		ServiceResponse tokenServiceResponse = new ServiceResponse();
		
		String userId = (String) httpRequest.getHeader("userId");
		
		if (userId != null){
		
			tokenServiceResponse = TokenService.refreshToken(validateToken.getToken(),
					validateToken.getRemoteIp());
		}
		
		
		return Util.getRestResponse(tokenServiceResponse);
	}

	/**
	 * It is used for any user to reset the account themselves by selecting forgot password
	 * @param email : Email address of the user whose account needs to be reset.
	 * @return
	 */
	@GET
	@Path("/forgot-password/submit-email/{email}")
	@Produces("application/json")
	public ServiceResponse getResponseForEmailSubmissionForForgotPassword(
			@PathParam("email") String email) {
		
		if (logger.isDebugEnabled()){
			logger.debug("/forgot-password/submit-email/ validateToken = {}", email);
		}
		
		try {
			email = new String(Base64.decodeBase64(email), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		}
		
		ServiceResponse emailSubmissionResponse = accountUnlockService
				.getResponseForForgotPasswordEmailSubmission(email);

		return emailSubmissionResponse;
	}

	/**
	 * Validate User response to security questions.
	 * @param user_id - UserId of the User who is updating the Info.
	 * @param secQId : Security Question Id of the question that was asked to the user.
	 * @param answer : User response to the question.
	 * @param numberOfAttempts : numberOf Unsuccessful attempts.
	 * @return
	 */
	@GET
	@Path("/forgot-password/check-security-answer/{user_id}/{secQId}/{answer}/{numberOfAttempts}")
	@Produces("application/json")
	public ServiceResponse checkSecurityAnswer(
			@PathParam("user_id") String user_id,
			@PathParam("secQId") String secQId,
			@PathParam("answer") String answer,
			@PathParam("numberOfAttempts") Integer numberOfAttempts) {
		
		if (logger.isDebugEnabled()){
			logger.debug("/forgot-password/check-security-answer/");
		}
		
		ServiceResponse checkSecurityAnswerResponse = accountUnlockService
				.getResponseForCheckSecurityAnswer(user_id, secQId, answer,
						numberOfAttempts);

		return checkSecurityAnswerResponse;
	}

	/**
	 * retrieve the User Info of the User by Email.
	 * @param email : The email address of the user whose user Info needs to be retrieved
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@GET
	@Path("/user/get-user-info/{email}")
	@Produces("application/json")
	public ServiceResponse getUserInfo(@PathParam("email") String email) throws UnsupportedEncodingException {
		
		if (logger.isDebugEnabled()){
			logger.debug("/user/get-user-info/ email = {}" , email);
		}

		if (!email.equals("all")) {
			email = new String(Base64.decodeBase64(email), "UTF-8");
		}

		ServiceResponse serviceResponse = userService.getUserInfo(email.trim());

		return serviceResponse;
	}

	/**
	 * This service is used by PLCG to primarily reset non-vendor accounts
	 * @param userId - The userId of the User whose accounts needs to be reset.
	 * @return
	 */
	@POST
	@Path("/reset-account/{user_id}")
	@Produces("application/json")
	public ServiceResponse resetUserAccount(@PathParam("user_id") String userId) {
		
		if (logger.isDebugEnabled()){
			logger.debug("/user/reset-account/");
		}
		
		ServiceResponse serviceResponse = userService.resetAccount(userId);

		return serviceResponse;
	}
	
	/**
	 * This service is used by PLCG to permanently remove Agency Reps. or GAO User Accounts from  EPDS.
	 * vendor account are automatically deleted based on inactivity
	 * @param userId The userId of the User whose account needs to be permanently removed.
	 * @return
	 */
	@POST
	@Path("/delete-account/{user_id}")
	@Produces("application/json")
	public ServiceResponse deleteUserAccount(@PathParam("user_id") String userId) {
		
		if (logger.isDebugEnabled()){
			logger.debug("/user/delete-account/user_id" , userId);
		}
		
		ServiceResponse serviceResponse = userService.deleteUserAccount(userId);
		
		return serviceResponse;
	}
	
	
	/**
	 * This service is used by PLCG to update firm name
	 * @param user_info_dto
	 * @return
	 */
	@POST
	@Path("/util")
	@Produces("application/json")
	
	public Response utilServices(User_info_dto user_info_dto) {
		ServiceResponse serviceResponse = new ServiceResponse();
		loginService.updateFirmName(user_info_dto);
		return Util.getRestResponse(serviceResponse, false, "");
	}
	
	//Amer :This is a temporary method to reset password for test accounts
	
	@GET
	@Path("/user/updatePasswords")
	@Produces("application/json")
	public void updatePasswords() {
		
		loginService.updateUserPasswords();
	//loginService.createUserEventLogRecordForPasswordAssigned();
	}
	
	@GET
	@Path("/user/updateSecQues")
	@Produces("application/json")
	public void updateSecQues() {
		
		
		//userService.getListOfUserInfo();
		
	}
	

	
}
