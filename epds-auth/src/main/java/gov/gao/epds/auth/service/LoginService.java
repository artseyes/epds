package gov.gao.epds.auth.service;

import gov.gao.epds.auth.dao.Login_attempt_dao;
import gov.gao.epds.auth.dao.User_info_dao;
import gov.gao.epds.auth.dto.ServiceResponse;
import gov.gao.epds.auth.dto.User_info_dto;
import gov.gao.epds.auth.persistence.entity.Login_attempt;
import gov.gao.epds.auth.persistence.entity.Rules_Of_Behavior;
import gov.gao.epds.auth.persistence.entity.User_event_log;
import gov.gao.epds.auth.persistence.entity.User_info;
import gov.gao.epds.auth.utils.AccountLockUtil;
import gov.gao.epds.auth.utils.DateUtil;
import gov.gao.epds.auth.utils.LoginUtil;
import gov.gao.epds.auth.utils.PasswordUtil;
import gov.gao.epds.auth.utils.PolicyParam;
import gov.gao.epds.auth.utils.Util;
import gov.gao.epds.enums.UserAuthRoles;
import static gov.gao.epds.enums.UserAuthRoles.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
	@Autowired
	Login_attempt_dao login_attempt_dao;
	@Autowired
	User_info_dao user_info_dao;
	@Autowired
	EmailService emailService;
	@Autowired
	AccountUnlockService accountUnlockService;
	
	private final static Logger logger = LoggerFactory
			.getLogger(LoginService.class);


	@GET
	@Path("/authenticate/")
	@Produces("application/json")
	public ServiceResponse getLoginResponse(User_info_dto user_info_dto) {
		/* PSEUDOCODE:
		- case a: provided email and password don't match, 
			- case a.1: email doesn't exist: set error message 
			- case a.2: account is locked:
					- If max login attempt crossed ?
								- Case a.2.1: user is vendor, send new temporary password and set error message that conveys user...
								- Case a.2.2: user is non-vendor, set account as locked and set error message that convey user to wait for account manager action
					- else, set error message that will convey user to get temporary password from email
			- Case a.3: password doesn't match with user_id
					- If max login attempt crossed ?
								- Case a.3.1: user is vendor, send new temporary password and set error message that conveys user...
								- Case a.3.2: user is non-vendor, set account as locked and set error message that convey user to wait for account manager action
					- else, set error message that will convey user that userId/password don't match
		- case b: provided email and password matches
			- case b.1: Password expired. Set message
			- case b.2: Password is expiring. Set message	
			- case b.3: Account deactivated, set message	
			
		*/
		ServiceResponse serviceResponse = new ServiceResponse();
		serviceResponse.setIsSuccess(false);

		String user_email = user_info_dto.getEmail();
		
		String password = user_info_dto.getPassword();

		try {
			User_info user_info = user_info_dao.getUserInfoByEmail(user_email);
			Boolean comparePassword = false;
			
			if(user_info != null){
				// debugger gets confused in 'pbkdf2' if step over
				comparePassword = PasswordUtil.comparePasswordOrSecurityAnswers(password, user_info.getPassword());

				if (PasswordUtil.checkIfAccountNeedsToBeDeactivated(user_info.getEmail(), login_attempt_dao)){
						PasswordUtil.assignTemporaryPassword(user_info, 6,
								user_info_dao, emailService);

					serviceResponse.setMessage("Account Deactivated");
				}
			}

			addLoginAttemptRecord(user_info_dto, comparePassword);

			if (!comparePassword) {
				// get user_info by email
				// if null wrong email, set wrong email message
				// else, check if locked
				// if locked, set locked message

				if (user_info == null) {
					serviceResponse.setMessage("Email doesn't exist");
				} else if (user_info.getAccount_status_id() == 4) {
					UserAuthRoles role = UserAuthRoles.getByCode(user_info.getRole_id());

					if (checkIfMaxLoginAttemptCrossed(user_info,PolicyParam.maxLoginAttemptTimePeriodInMins)) {
						
						if (role == PROTESTER) {
							/*boolean isAccountLocked = AccountLockUtil.lockUser(user_info, user_info_dao, emailService);*/
							lockVendorUser(serviceResponse, user_info,false);
						} else {
							serviceResponse
									.setMessage("You account is locked please check your email for further information");
						}
					} else {
						if (role == PROTESTER) {
							serviceResponse
									.setMessage("Your account is locked. Please check in your email for temporary password");
						} else {
							serviceResponse
									.setMessage("You account is locked please check your email for further information");
						}
					}

					user_info_dto = Util.getUserInfoDtoForLoginSuccess(user_info,user_info_dao);
					serviceResponse.setData(user_info_dto);
					serviceResponse.setIsSuccess(false);

				} else if (user_info.getAccount_status_id() == 3) {

					/*
					 * Problem : Account status is 3 user enters wrong password and it is redirecting to answer security questions page.
					 * 
					 * Solution :
					 * Step 1) Check for account status 3 when user id and password doesn't match.
					 * Step 2) if Account Status is 3 then check for maximum number of unsuccessful attempts from the time the temp password was assigned.
					 * 
					 * When Non-Vendor Account is gets temp locked we just need to provide correct message and we dont need to send any password because
					 * they need to answer security questions correctly to get temporary password. and once the assign the temp pass
					 * 
					 
					 */	
					
					
					user_info_dto = Util
							.getUserInfoDtoForLoginSuccess(user_info,user_info_dao);
					
					if (checkIfMaxLoginAttemptCrossed(user_info,PolicyParam.maxLoginAttemptTimePeriodInMins)){
						Map<Integer, String> mapOfSecQIdToSecurityQuestion = accountUnlockService
								.getMapOfSecQIdToSecurityQuestion(user_info);
						user_info_dto
								.setSecQIdToQuestionMap(mapOfSecQIdToSecurityQuestion);
					}else{
						serviceResponse.setMessage("Incorrect Password");
					}
					
					serviceResponse.setData(user_info_dto);
					serviceResponse.setIsSuccess(false);
					
				} else {
				
					if (checkIfMaxLoginAttemptCrossed(user_info,PolicyParam.maxLoginAttemptTimePeriodInMins)) {
						UserAuthRoles role = UserAuthRoles.getByCode(user_info.getRole_id());
						if (role == PROTESTER) {
							
							lockVendorUser(serviceResponse, user_info,true);
							
						} else if (role != PROTESTER && user_info.getAccount_status_id() == 1) {/*first time non-vendor user*/
							
							serviceResponse.setMessage("Account Locked : Status Not changed");
							PasswordUtil.assignTemporaryPassword(user_info, 1,
									user_info_dao, emailService);
							
						}else if (user_info.getAccount_status_id() != 3) {
							
							
							user_info.setAccount_status_id(3);
							user_info_dao.update(user_info);
						}

						
						
					} else if (!"Account Deactivated".equalsIgnoreCase(serviceResponse.getMessage())){
						serviceResponse.setMessage("Incorrect Password");
					}

					user_info_dto = Util
							.getUserInfoDtoForLoginSuccess(user_info,user_info_dao);
					if (user_info.getAccount_status_id() == 3) {
						Map<Integer, String> mapOfSecQIdToSecurityQuestion = accountUnlockService
								.getMapOfSecQIdToSecurityQuestion(user_info);
						user_info_dto
								.setSecQIdToQuestionMap(mapOfSecQIdToSecurityQuestion);
					}

					serviceResponse.setData(user_info_dto);
					serviceResponse.setIsSuccess(false);
				}
			} else {
				user_info_dto = Util.getUserInfoDtoForLoginSuccess(user_info,user_info_dao);
				
				if (user_info.getAccount_status_id() == 3) {
					Map<Integer, String> mapOfSecQIdToSecurityQuestion = accountUnlockService
							.getMapOfSecQIdToSecurityQuestion(user_info);
					user_info_dto
							.setSecQIdToQuestionMap(mapOfSecQIdToSecurityQuestion);
				}

				serviceResponse = PasswordUtil.handleExpiredOrExpiringPassword(
						serviceResponse, user_info, user_info_dto,
						user_info_dao,emailService);

				serviceResponse.setData(user_info_dto);
				serviceResponse.setIsSuccess(true);
			}

		} catch (Exception e) {
			serviceResponse.setException(e.getMessage());
			serviceResponse.setStackTraceDetail(Util.getStackTraceMessage(e));
			serviceResponse.setIsSuccess(false);
		}

		return serviceResponse;
	}

	/**
	 * @param serviceResponse
	 * @param user_info
	 * @throws Exception
	 */
	private void lockVendorUser(ServiceResponse serviceResponse, User_info user_info, boolean passwordNeedsToBeSent) throws Exception {
		boolean isAccountLocked = false;
		
		//this will happen when account status changes to account locked
		if (passwordNeedsToBeSent){
			isAccountLocked = AccountLockUtil.lockUser(
					user_info, user_info_dao, emailService);
			serviceResponse
			.setMessage("Your account is locked. Please check in your email for temporary password.");
		}else {
			// the account is already locked
			// need to check if temp password is expired
			//if expired then send a new temp password
			Boolean isTempPasswordExpired = PasswordUtil.checkIfNewTempPasswordNeedsToSent(
					user_info.getUser_id(),user_info,user_info_dao,emailService);
			
			if(isTempPasswordExpired){
				isAccountLocked = AccountLockUtil.lockUser(
						user_info, user_info_dao, emailService);
				/*serviceResponse.setMessage("Temporary login attempt crossed the maximum limit. New password has been emailed.");*/
				if (isAccountLocked)
				serviceResponse.setMessage("Temp. Password Expired");
			}else{
				serviceResponse.setMessage("Your account is locked. Please check in your email for temporary password.");
			}
		}
		
		
		/*if (isAccountLocked) {
			
			serviceResponse.setMessage("Temporary login attempt crossed the maximum limit. New password has been emailed.");
		}else{
			//come back later (basically exception handling e.g. may be notify Application admin)
		}*/
	}

	private void addLoginAttemptRecord(User_info_dto user_info_dto, boolean passGood) {
		DateTimeZone dateTimeZone = DateTimeZone.forID("America/Chicago");
		DateTime currentTime = DateTime.now(dateTimeZone);
		Timestamp timeStamp = new Timestamp(currentTime.getMillis());
		
		Login_attempt login_attempt = new Login_attempt();
		login_attempt.setBrowser_type("");
		login_attempt.setClient_ip(user_info_dto.getClient_ip());
		login_attempt.setPassword(user_info_dto.getPassword());
		
		login_attempt.setTime_stamp(timeStamp);
		
		if (passGood) {
			login_attempt.setSuccess('Y');
		} else {
			login_attempt.setSuccess('N');
		}
		login_attempt.setUser_email(user_info_dto.getEmail());

		login_attempt_dao.save(login_attempt);
	}

	/*private boolean checkIfMaxClientIpLoginAttempCrossed(String client_ip) {
		List<Login_attempt> listOfUnsuccessfulLoginAttemptByClientIpForGivenTime = login_attempt_dao
				.getListOfLoginAttemptBasedOnClientIp(
						client_ip,
						PolicyParam.numberOfLastMinsToCountLastLoginAttemptByClientIp);
		return (listOfUnsuccessfulLoginAttemptByClientIpForGivenTime != null && listOfUnsuccessfulLoginAttemptByClientIpForGivenTime
				.size() > PolicyParam.maxNumberOfUnsuccessfulClientIpAttempt);
	}*/

	
	
	/**
	 * This is to check whether the current user has crossed the maximum login attempt.
	 * @param user_info
	 * @return
	 */
	private boolean checkIfMaxLoginAttemptCrossed(User_info user_info,Integer timeInMins) {
		
		
		/*
		 * Pseudo Code:
		 *  Get List of all the login attempts, both successful and unsuccessful in desc order. in the last PolicyParam.maxLoginAttemptTimePeriodInMins
		 *   Iterate through this list to check for consecutive unsuccessful login attempts
		 *  */
		
		User_event_log userEventLog = user_info_dao.getMostRecentUserEventLogByEventId(user_info.getUser_id(), 4);
		if (userEventLog == null) {
			return false;
		}
		
		List<Login_attempt> listOfLoginAttempts = null;
		Long timeElapsedSinceLastTempPwdAssigend = new Duration(new DateTime(userEventLog.getTime_stamp()),new DateTime()).getStandardMinutes();
		int count = 0;
		
		/*
		 * Basically I think we normally check by timeInmins but this condition is to make sure that if the temp password was recently assigned 
		 * then get the time elapsed since last temp password was assigned 
		 */		
		if (Math.abs(timeElapsedSinceLastTempPwdAssigend) <= timeInMins){
			timeInMins = timeElapsedSinceLastTempPwdAssigend.intValue();
		}
		
		listOfLoginAttempts = login_attempt_dao
				.getListOfLoginAttempt(user_info.getEmail(),
						timeInMins);
		
		Login_attempt eachLoginAttempt;
		
		for (int i=0; (i < listOfLoginAttempts.size() && i < PolicyParam.maxNumberOfUnsuccessfulLoginAttempt);i++){
			eachLoginAttempt = listOfLoginAttempts.get(i);

			if (String.valueOf(eachLoginAttempt.getSuccess()).equalsIgnoreCase("N")){
			count++;	
			}else if (String.valueOf(eachLoginAttempt.getSuccess()).equalsIgnoreCase("Y")){
			count = 0;
			break;
			}
			
		}
		
		
		return (count >= PolicyParam.maxNumberOfUnsuccessfulLoginAttempt);
	}

	public ServiceResponse getUserInfoForGivenEmailAndPassword(
			String user_email, String password) {
		
		User_info user_info = user_info_dao
				.getUserInfoForGivenEmailAndPassword(user_email, password);

		ServiceResponse response = new ServiceResponse();

		if (user_info != null) {
			response.setData(user_info);
		}

		response.setIsSuccess(true);
		return response;
	}

	public void setTemporaryPassword(String user_email, String client_ip) {
		
		User_info user_info = user_info_dao.getUserInfoByEmail(user_email);
		
		String temporaryPassword = Util.getTemporaryPassword();

		// getUpdatedPasswordHistory(user_info);
		if (temporaryPassword !=null){
		
			user_info.setPassword(PasswordUtil.createHash(PasswordUtil.generateHash(temporaryPassword)));
		}
		
		user_info.setAccount_status_id(4);

		user_info_dao.save(user_info);
	}

	public int getNumberOfDaysForPasswordExpiration(Integer user_id) {
		
		User_event_log user_event_log = user_info_dao.getLastUserEventLog(user_id, 4);

		return DateUtil
				.getNumberOfDaysPassedAfterProvidedTimeStamp(user_event_log.getTime_stamp());
	}
	
	
	//Amer : This is a temporary method if we need to update the password for any account
	
	public void updateUserPasswords() {
		
//		User_info user_info  = user_info_dao.getUserInfoByEmail("tsagaoprotests@tsa.dhs.gov");
		User_info user_info  = user_info_dao.getUserInfoByEmail("epdsagencyrep1@gmail.com".toLowerCase());
		user_info.setAccount_status_id(2);
		user_info.setPassword(PasswordUtil.createHash(PasswordUtil.generateHash("WV5yH2^!GShK")));
		//user_info.setPassword(PasswordUtil.createHash(PasswordUtil.generateHash("xvjaRSry!38x")));
		
		try {
			//user_info_dao.delete(user_info);
		    user_info_dao.update(user_info);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public void updateFirmName(User_info_dto user_info_dto) {
		User_info user_info  = user_info_dao.getUserInfoByEmail(user_info_dto.getEmail());
		if (user_info != null){
			//if we are switching from Protester account to an agency account then in that case we need to reset the account status to 1 and roleId 2 so the user can reset password and security questions
			if(user_info.getRole_id().equals(PROTESTER.getCode())){
				user_info.setRole_id(2);
				user_info.setAccount_status_id(1);
			}
			
			user_info.setFirm_id(user_info_dto.getFirm_id());
			user_info.setFirm_name(user_info_dto.getNameOfFirm());
			
			try {
				user_info_dao.update(user_info);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		
	}

	public void createUserEventLogRecordForPasswordAssigned() {
		
		/*List<User_info> userInfos  = user_info_dao.getListOfAllUsers();
		
		
		try {
			for (User_info eachUserInfo : userInfos){
				user_info_dao.setUserEventLog(eachUserInfo.getUser_id(),2);
				//user_info_dao.setUserEventLog(eachUserInfo.getUser_id(),4);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
	}
	public ServiceResponse rulesOfBehavior(String email) {
		
		ServiceResponse response = new ServiceResponse();
		
		response.setIsSuccess(false);
		try {
			
			Rules_Of_Behavior rob = null;
			rob = user_info_dao.getRulesOfBehaviorTimeStamp(email);
			
			if (null != rob){
				rob.setEmail(email);
				rob.setTime_stamp(new Timestamp(new Date().getTime()));;
				user_info_dao.update(rob);
			}else{
				rob = new Rules_Of_Behavior();
				rob.setEmail(email);
				rob.setTime_stamp(new Timestamp(new Date().getTime()));;
				user_info_dao.save(rob);
			}

			
			response.setIsSuccess(true);
		} catch (Exception e) {
			response.setIsSuccess(false);
			e.printStackTrace();
		}
	

		
		return response;
	}

}
