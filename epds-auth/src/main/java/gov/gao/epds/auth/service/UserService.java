package gov.gao.epds.auth.service;

import gov.gao.epds.auth.dao.Login_attempt_dao;
import gov.gao.epds.auth.dao.User_info_dao;
import gov.gao.epds.auth.dao.User_security_answer_dao;
import gov.gao.epds.auth.dto.ServiceResponse;
import gov.gao.epds.auth.dto.User_info_dto;
import gov.gao.epds.auth.persistence.entity.User_info;
import gov.gao.epds.auth.persistence.entity.User_security_answer;
import gov.gao.epds.auth.utils.PasswordUtil;
import gov.gao.epds.auth.utils.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
	
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	Login_attempt_dao login_attempt_dao;
	@Autowired
	User_info_dao user_info_dao;
	@Autowired
	User_security_answer_dao user_security_answer_dao;
	@Autowired
	EmailService emailService;

	@Transactional
	public ServiceResponse registerOrUpdateUser(User_info_dto user_info_dto,
			 String requestType, String isActiveUser)
			throws JsonGenerationException, JsonMappingException, IOException {
		ServiceResponse serviceResponse = new ServiceResponse();
		serviceResponse.setIsSuccess(false);

		User_info user_info = new User_info();

		try {
			if (requestType.equalsIgnoreCase("register")) {
				
				logger.info("Attempting to register new user     " + user_info_dto.getEmail());
				setUser_info(user_info, user_info_dto);

				String temporaryPassword = Util.getTemporaryPassword();
				/*temporaryPassword = PasswordUtil.createHash(temporaryPassword);*/
				// debug warning - never step over createHash. Takes forever. Put breakpoint and doa run/continue
				String hashedPwd = PasswordUtil.createHash(PasswordUtil.generateHash(temporaryPassword));
				user_info.setAccount_status_id(1);
				user_info.setPassword(hashedPwd);
				user_info = user_info_dao.save(user_info);

				/*if (user_info_dto.getUser_id() != null){
					//if this user already existed and the account was deleted because of user inactivity
					//associate with the old user ID
					
					04:23:53,313 ERROR [io.undertow.request] (default task-66) UT005023: Exception handling 
					request to /epds-auth/rest/auth-service/user/register/n: org.jboss.resteasy.spi.UnhandledException: 
					org.springframework.orm.hibernate5.HibernateSystemException: 
					identifier of an instance of gov.gao.epds.auth.persistence.entity.User_info was altered from 3213810 to 327682; 
					nested exception is org.hibernate.HibernateException: identifier of an instance of gov.gao.epds.auth.persistence.entity.User_info was altered from 3213810 to 327682
						at org.jboss.resteasy.core.ExceptionHandler.handleApplicationException(ExceptionHandler.java:76)
						at org.jboss.resteasy.core.ExceptionHandler.handleException(ExceptionHandler.java:212)

					user_info.setUser_id(user_info_dto.getUser_id());
					user_info = user_info_dao.update(user_info);
				}*/
				user_info_dao.setUserEventLog(user_info.getUser_id(), 1);
				user_info_dao.setUserEventLog(user_info.getUser_id(), 4);
				if (user_info.getUser_id() != null) {
					
					ServiceResponse emailResponse = emailService
							.emailTemporaryPasswordToUser(user_info,
									temporaryPassword);
					emailService.notifyAccountActivityToSysAdmins(user_info.getEmail(), "ACCOUNT CREATED");

					if (emailResponse.getIsSuccess()) {
						serviceResponse.setIsSuccess(true);
						serviceResponse.setData(user_info);
					} else {
						user_info_dao.delete(user_info);
						serviceResponse = emailResponse;
					}
					
				}
			} else if (isActiveUser.equalsIgnoreCase("Y")) {
				
				if (user_info_dto.getTypeOfUpdate().equalsIgnoreCase("updateProfile")){
					user_info = user_info_dao.getUserInfoByEmail(user_info_dto.getEmail());
					setUser_info(user_info, user_info_dto);
					user_info_dao.update(user_info);
					serviceResponse.setIsSuccess(true);
					serviceResponse.setData(user_info_dto);
				}else if ("changePassword".equalsIgnoreCase(user_info_dto.getTypeOfUpdate())){
					serviceResponse = updatePasswordAndOrSecurityAnswers(user_info_dto,false);
				}else if("changeSecQues".equalsIgnoreCase(user_info_dto.getTypeOfUpdate())){
					serviceResponse = new ServiceResponse();
					user_info = user_info_dao
							.getUserInfoByUserId(user_info_dto.getUser_id());
					serviceResponse = updateSecurityQuestions(user_info_dto, serviceResponse, user_info);
				}
				

			} else {
				serviceResponse = updatePasswordAndOrSecurityAnswers(user_info_dto,true);
			}
		} catch (Exception e) {
			serviceResponse.setIsSuccess(false);
			serviceResponse.setException(Util.getStackTraceMessage(e));
			logger.error("registerOrUpdateUser exception: " + Util.getStackTraceMessage(e));
			e.printStackTrace();
		} finally {

		}

		return serviceResponse;
	}

	@Transactional
	public ServiceResponse addSecurityQuestions(User_info_dto user_info_dto,
			Integer user_id) {
		
		ServiceResponse serviceResponse = new ServiceResponse();

		ServiceResponse removeOldSecAnsResponse = removeOldSecurityAnswers(user_id);

		if (user_info_dto.getSecQIdToAnswerMap() != null
				&& removeOldSecAnsResponse.getIsSuccess()) {
			
			try {
				Map<Integer, String> secQIdToAnswerMap = user_info_dto
						.getSecQIdToAnswerMap();
				List<User_security_answer> listOfUserSecurityAnswer = getListOfUserSecurityAnswer(
						secQIdToAnswerMap, user_id);

				//need to test basically this should encrypt all the security answers when adding
				for (int i=0; i< listOfUserSecurityAnswer.size();i++){
				
					listOfUserSecurityAnswer.get(i)
					.setSecurity_ans(PasswordUtil
							.createHash(PasswordUtil
									.generateHash(listOfUserSecurityAnswer.get(i).
											getSecurity_ans().trim().toLowerCase(Locale.ENGLISH))));;
				}
				
				user_info_dao.save(listOfUserSecurityAnswer);

				serviceResponse.setIsSuccess(true);
			} catch (Exception e) {
				logger.error("addSecurityQuestions exception: " + Util.getStackTraceMessage(e));
				serviceResponse.setException(Util.getStackTraceMessage(e));
				serviceResponse.setIsSuccess(false);
			} finally {

			}
		} else {
			serviceResponse = removeOldSecAnsResponse;
		}

		return serviceResponse;
	}

	private ServiceResponse removeOldSecurityAnswers(Integer user_id) {
		ServiceResponse serviceResponse = new ServiceResponse();

		try {
			List<User_security_answer> existingSecurityQuestions  = user_security_answer_dao.getListOfUserSecurityAnswer(user_id);
			
			if (null != existingSecurityQuestions && !existingSecurityQuestions.isEmpty()){
				for (User_security_answer eachExistingUserSecQues : existingSecurityQuestions){
					user_security_answer_dao.delete(eachExistingUserSecQues);
				}	
			}
			serviceResponse.setIsSuccess(true);
			/*serviceResponse = user_security_answer_dao
					.removeOldSecurityAnswersByUserId(user_id);*/
		} catch (Exception e) {
			logger.error("removeOldSecurityAnswers exception: " + Util.getStackTraceMessage(e));
			serviceResponse.setException(Util.getStackTraceMessage(e));
			serviceResponse.setIsSuccess(false);
		} finally {

		}

		return serviceResponse;
	}

	private List<User_security_answer> getListOfUserSecurityAnswer(
			Map<Integer, String> secQIdToAnswerMap, Integer user_id) {
		List<User_security_answer> listOfUserSecurityAnswer = new ArrayList<User_security_answer>();

		User_security_answer user_security_answer = null;
		for (Entry<Integer, String> eachEntry : secQIdToAnswerMap.entrySet()) {
			user_security_answer = new User_security_answer();

			user_security_answer.setSecurity_q_id(eachEntry.getKey());
			user_security_answer.setSecurity_ans(eachEntry.getValue());
			user_security_answer.setUser_id(user_id);

			listOfUserSecurityAnswer.add(user_security_answer);
		}

		return listOfUserSecurityAnswer;
	}

	private void setUser_info(User_info user_info, User_info_dto user_info_dto) {
		
		user_info.setAddress1(user_info_dto.getAddress1());
		user_info.setAddress2(user_info_dto.getAddress2());
		user_info.setCity(user_info_dto.getCity());
		user_info.setCountry(user_info_dto.getCountry());
		user_info.setEmail(user_info_dto.getEmail().toLowerCase(Locale.ENGLISH));
		user_info.setFax_no(user_info_dto.getFaxNo());
		user_info.setFirm_id(user_info_dto.getFirm_id());
		user_info.setFirm_name(user_info_dto.getNameOfFirm());
		user_info.setFirst_name(user_info_dto.getFirstName());
		user_info.setLast_name(user_info_dto.getLastName());
		user_info.setMiddle_initial(user_info_dto.getMiddle_initial());
		//Amer  : since we never store the the password in EPDS and if he is active user 
		//updating the info then we dont need to set the password
		//updating password happens when the user uses change password option
		/*user_info.setPassword(user_info_dto.getPassword());*/
		user_info.setPhone_no(user_info_dto.getPhoneNo());
		user_info.setPrefix(user_info_dto.getPrefix());
		user_info.setRole_id(user_info_dto.getAuth_role_id());
		user_info.setState(user_info_dto.getState());
		user_info.setSuffix(user_info_dto.getSuffix());
		user_info.setZip_code(user_info_dto.getZipCode());

		// password validation (this is repeatative measure as password
		// validation will already be done in frontend before final submission)
		/*
		 * String password = user_info_dto.getPassword(); if(password!=null &&
		 * Util.getValidationResponse(password)){
		 * 
		 * }
		 */
	}

	@Transactional
	public ServiceResponse changeUserPassword(String user_email,
			String oldPassword, String newPassword, User_info user_info,
			boolean isNonActiveUser) throws Exception {
		ServiceResponse serviceResponse = new ServiceResponse();

		try {
			if (user_info == null) {
				user_info = user_info_dao.getUserInfoByEmail(user_email);
			}

			if (user_info.getPassword() != null
					&& (PasswordUtil.
							compareUIHashWithDbStoredHash(PasswordUtil.generateHash((oldPassword != null ? oldPassword : "")), user_info.getPassword())
							 || isNonActiveUser)) {
				
				ServiceResponse validationResponse = Util
						.getPasswordValidationResponse(newPassword,
								user_info.getPassword_history());

				if (validationResponse.getIsSuccess()
						&& validationResponse.getMessage() != null
						&& validationResponse.getMessage().equalsIgnoreCase(
								"Valid password")) {
					user_info.setPassword(PasswordUtil.createHash(PasswordUtil.generateHash(newPassword)));
					user_info.setPassword_history(Util
							.getUpdatedPasswordHistory(user_info,PasswordUtil.generateHash(newPassword)));
					user_info_dao.update(user_info);
					// I think account activated status should be assign first time when user activates the account
					//and subsequently whenever the account status changes from expired,locked,... to active
					user_info_dao.setUserEventLog(user_info.getUser_id(), 2);

					serviceResponse.setMessage("Password changed");
				} else {
					serviceResponse = validationResponse;
				}
			} else {
				serviceResponse.setMessage("Invalid old password");
			}

			serviceResponse.setIsSuccess(true);
		} catch (Exception e) {
			logger.error("changeUserPassword exception: " + Util.getStackTraceMessage(e));
			serviceResponse.setIsSuccess(false);
			serviceResponse.setException(Util.getStackTraceMessage(e));
		} finally {

		}

		return serviceResponse;
	}

	public ServiceResponse checkIfUserEmailAlreadyExist(String user_email) {
		ServiceResponse serviceResponse = new ServiceResponse();
		try {
			if (user_info_dao.checkIfUserEmailAlreadyExist(user_email)) {
				serviceResponse.setMessage("Y");
			} else {
				serviceResponse.setMessage("N");
			}
			serviceResponse.setIsSuccess(true);
		} catch (Exception e) {
			serviceResponse.setIsSuccess(false);
			serviceResponse.setException(Util.getStackTraceMessage(e));
			logger.error("checkIfUserEmailAlreadyExist exception: " + Util.getStackTraceMessage(e));
		}

		return serviceResponse;
	}

	@Transactional
	public ServiceResponse updatePasswordAndOrSecurityAnswers(
			User_info_dto user_info_dto,boolean isNonActiveUser) {
		ServiceResponse serviceResponse = new ServiceResponse();

		try {
			User_info user_info = user_info_dao
					.getUserInfoByUserId(user_info_dto.getUser_id());
			
			//need to come back over it was throwing null point exception when the password was expired and user_info_dto.getTypeOfUpdate() was nto set.
			/*if (!user_info_dto.getTypeOfUpdate().equalsIgnoreCase("changeSecQues")){
				
			}*/

			ServiceResponse passwordUpdateResponse = changeUserPassword(
					user_info_dto.getEmail(), user_info_dto.getOldPassword(),
					user_info_dto.getPassword(), user_info, isNonActiveUser);

			boolean isSecurityQuestionsUpdateNeeded = getIsSecurityQuestionsUpdateNeeded(user_info_dto);
			/*String test = user_info_dto.getSecQIdToAnswerMap().get(
					user_info_dto.getSeqQue1Id());
			System.out.println(test);*/

			ServiceResponse securityAnswersUpdateResponse = new ServiceResponse();
			if (passwordUpdateResponse.getMessage().equalsIgnoreCase(
					"Password Changed")
					&& isSecurityQuestionsUpdateNeeded) {
				securityAnswersUpdateResponse = updateSecurityQuestions(user_info_dto, serviceResponse, user_info);
			} else {
				serviceResponse = passwordUpdateResponse;
			}

			if (securityAnswersUpdateResponse.getIsSuccess()
					|| passwordUpdateResponse.getIsSuccess()) {
				user_info.setAccount_status_id(2);
				user_info_dao.update(user_info);
				user_info_dao.setUserEventLog(user_info.getUser_id(), 2);
				user_info_dao.setUserEventLog(user_info.getUser_id(), 8);

				serviceResponse.setMessage("Updated successfully");
			}

			user_info_dto = Util.getUserInfoDtoForLoginSuccess(user_info,user_info_dao);

			serviceResponse.setData(user_info_dto);
			serviceResponse.setIsSuccess(true);
		} catch (Exception e) {
			serviceResponse.setIsSuccess(false);
			serviceResponse.setException(Util.getStackTraceMessage(e));
			logger.error("updatePasswordAndOrSecurityAnswers exception: " + Util.getStackTraceMessage(e));
		} finally {

		}

		return serviceResponse;
	}

	/**
	 * Update Security Questions
	 * @param user_info_dto
	 * @param serviceResponse
	 * @param user_info
	 * @return
	 * @throws Exception
	 */
	public ServiceResponse updateSecurityQuestions(User_info_dto user_info_dto, ServiceResponse serviceResponse,
			User_info user_info) throws Exception {
		ServiceResponse securityAnswersUpdateResponse;
		securityAnswersUpdateResponse = addSecurityQuestions(
				user_info_dto, user_info.getUser_id());

		if (securityAnswersUpdateResponse.getIsSuccess()) {
			user_info.setAccount_status_id(2);
			user_info_dao.update(user_info);
			user_info_dao.setUserEventLog(user_info.getUser_id(), 2);

			serviceResponse.setMessage("Updated successfully");
		}
		return securityAnswersUpdateResponse;
	}

	private boolean getIsSecurityQuestionsUpdateNeeded(
			User_info_dto user_info_dto) {
		boolean isSecurityQuestionsUpdateNeeded = false;

		try {
			isSecurityQuestionsUpdateNeeded = user_info_dto
					.getSecQIdToAnswerMap().get(
							Integer.valueOf(user_info_dto.getSeqQue1Id())) != null
					&& !user_info_dto.getSecQIdToAnswerMap()
							.get(Integer.valueOf(user_info_dto.getSeqQue1Id()))
							.equalsIgnoreCase("");
		} catch (Exception e) {

		}
		return isSecurityQuestionsUpdateNeeded;
	}

	public ServiceResponse getPasswordValidationResponse(String password,
			Integer user_id) {
		User_info user_info = user_info_dao.getUserInfoByUserId(user_id);

		return Util.getPasswordValidationResponse(password,
				user_info.getPassword_history());
	}
	
	public ServiceResponse getPasswordValidationResponse(User_info_dto userInfoDto) {
		User_info user_info = user_info_dao.getUserInfoByUserId(userInfoDto.getUser_id());

		ServiceResponse serviceResponse = new ServiceResponse();
		
		if (PasswordUtil.compareUIHashWithDbStoredHash(userInfoDto.getOldPassword(), user_info.getPassword())){
			serviceResponse.setMessage("Valid");
			serviceResponse.setIsSuccess(true);
		}else{
			serviceResponse.setMessage("Invalid");
			serviceResponse.setIsSuccess(false);
		}
		
		return serviceResponse;
	}
	
	public ServiceResponse updateUserName(User_info_dto userInfoDto) {
		
		User_info user_info = user_info_dao.getUserInfoByEmail(userInfoDto.getOld_email().toLowerCase(Locale.ENGLISH));

		ServiceResponse serviceResponse = new ServiceResponse();
		
		if (PasswordUtil.compareUIHashWithDbStoredHash(userInfoDto.getPassword(), user_info.getPassword())){
			
			
			if (checkIfUserEmailAlreadyExist(userInfoDto.getEmail()).getMessage().equalsIgnoreCase("N")){
				
				try {
					user_info.setPrevious_email(userInfoDto.getOld_email().toLowerCase(Locale.ENGLISH));
					user_info.setEmail(userInfoDto.getEmail().toLowerCase(Locale.ENGLISH));
					user_info_dao.update(user_info);
					serviceResponse.setIsSuccess(true);
				} catch (Exception e) {
					logger.error("updateUserName exception: " + Util.getStackTraceMessage(e));
					e.printStackTrace();
					serviceResponse.setIsSuccess(false);
					serviceResponse.setException(e.getMessage());
				}
				
				if (serviceResponse.getIsSuccess()){
				
					serviceResponse = emailService.sendUpdateAccountInfoAlertToOldAndNewEmailAddress(userInfoDto.getEmail(),userInfoDto.getOld_email());
				}
				
				
			}else{
				serviceResponse.setMessage("Old email is already assigned.");
				serviceResponse.setIsSuccess(false);
			}
			
		}else{
			serviceResponse.setMessage("Password doesn't Match.");
			serviceResponse.setIsSuccess(false);
		}
		
		return serviceResponse;
	}

	public ServiceResponse getListOfSecurityQuestion() {
		ServiceResponse serviceResponse = new ServiceResponse();

		try {
			serviceResponse.setData(user_security_answer_dao
					.getSecurityQuestions());
			serviceResponse.setIsSuccess(true);
		} catch (Exception e) {
			logger.error("getListOfSecurityQuestion exception: " + Util.getStackTraceMessage(e));
			serviceResponse.setException(Util.getStackTraceMessage(e));
			serviceResponse.setIsSuccess(false);
		}

		return serviceResponse;
	}

	/*
	 * public ServiceResponse sendTemporaryPasswordToUser(String user_email) {
	 * ServiceResponse serviceResponse = new ServiceResponse();
	 * 
	 * try { User_info locked_user_info = user_info_dao
	 * .getUserInfoByEmail(user_email);
	 * PasswordUtil.assignTemporaryPassword(locked_user_info, 4, user_info_dao,
	 * emailService); serviceResponse.setIsSuccess(true); } catch (Exception e)
	 * { serviceResponse.setException(Util.getStackTraceMessage(e));
	 * serviceResponse.setIsSuccess(false); }
	 * 
	 * return serviceResponse; }
	 */

	/**
	 * Reset user accounts.
	 * @param userId
	 * @return
	 */
	@Transactional
	public ServiceResponse resetAccount(String userId) {
		ServiceResponse serviceResponse = new ServiceResponse();

		try {
			User_info user_info = user_info_dao.getUserInfoByUserId(Integer
					.valueOf(userId.trim()));
			String temporaryPassword = Util.getTemporaryPassword();
			user_info.setAccount_status_id(7);
			user_info.setPassword(PasswordUtil.createHash(PasswordUtil.generateHash(temporaryPassword)));
			user_info = user_info_dao.update(user_info);

			ServiceResponse emailResponse = emailService
					.emailTemporaryPasswordToUser(user_info,
							temporaryPassword);
			emailService.notifyAccountActivityToSysAdmins(user_info.getEmail(), "ACCOUNT RESET");
			if (emailResponse.getIsSuccess()) {
				serviceResponse.setIsSuccess(true);
				serviceResponse.setData(user_info);
			} else {
				/*user_info_dao.delete(user_info);*/
				
				serviceResponse = emailResponse;
			}

			serviceResponse.setSuccess(true);
		} catch (Exception e) {
			serviceResponse.setSuccess(false);
			serviceResponse.setException(e.getMessage());
			logger.error("resetAccount exception: " + Util.getStackTraceMessage(e));
		}

		return serviceResponse;
	}
	
	
	@Transactional
	public ServiceResponse deleteUserAccount(String userId) {
		ServiceResponse serviceResponse = new ServiceResponse();

		try {
			User_info user_info = user_info_dao.getUserInfoByUserId(Integer
					.valueOf(userId.trim()));

			if (user_info != null) {
				serviceResponse.setIsSuccess(true);
				user_info_dao.setUserEventLog(user_info.getUser_id(), 7);
				user_info_dao.delete(user_info);
			}else {
				serviceResponse.setIsSuccess(true);
			}

		} catch (Exception e) {
			serviceResponse.setSuccess(false);
			serviceResponse.setException(e.getMessage());
			logger.error("deleteUserAccount exception: " + Util.getStackTraceMessage(e));
		}

		return serviceResponse;
	}
	
	public ServiceResponse getUserInfo(String email) {
		ServiceResponse serviceResponse = new ServiceResponse();

		try {
			if (email.equals("all")) {
				List<User_info> user_infos = new ArrayList<User_info>();
				user_infos = user_info_dao.getListOfAllUsers();
				if (user_infos == null) {
					serviceResponse.setIsSuccess(false);
				} else {
					serviceResponse.setData(user_infos);
					serviceResponse.setIsSuccess(true);
				}
			} else {
				User_info userInfo = user_info_dao.getUserInfoByEmail(email);

				if (userInfo == null) {
					serviceResponse.setIsSuccess(false);
				} else {
					User_info_dto userInfoDTO = new User_info_dto();
					userInfoDTO.setFirstName(userInfo.getFirst_name());
					userInfoDTO.setLastName(userInfo.getLast_name());
					userInfoDTO.setNameOfFirm(userInfo.getFirm_name());
					userInfoDTO.setUser_id(userInfo.getUser_id());

					serviceResponse.setData(userInfoDTO);
					serviceResponse.setIsSuccess(true);
				}
			}
		} catch (Exception e) {
			serviceResponse.setIsSuccess(true);
			serviceResponse.setException(e.getMessage());
		}

		return serviceResponse;
	}
	
	@Transactional
	public List<User_info> getListOfUserInfo() {
		
		List<User_info> user_infos = new ArrayList<User_info>();
		
		try {
			user_infos = user_info_dao.getListOfAllUsers();
			
			
			for (User_info each : user_infos){

				List<User_security_answer> listOfUserSecurityAnswer = user_security_answer_dao
				.getListOfUserSecurityAnswer(each.getUser_id());
				
				//need to test basically this should encrypt all the security answers when adding
				
				if(listOfUserSecurityAnswer != null && !each.getUser_id().equals(229376)) {
				
					for (int i=0; i< listOfUserSecurityAnswer.size();i++){
					
						
					if (!listOfUserSecurityAnswer.get(i).getSecurity_ans().contains("sha2:64000:256:")){
						
						listOfUserSecurityAnswer.get(i)
						.setSecurity_ans(PasswordUtil
								.createHash(PasswordUtil
										.generateHash(listOfUserSecurityAnswer.get(i).
												getSecurity_ans().trim().toLowerCase(Locale.ENGLISH))));
						
						//user_security_answer_dao.update(listOfUserSecurityAnswer.get(i));
					}
					
					
						
					}
				}
						
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return user_infos;
	}
	

}
