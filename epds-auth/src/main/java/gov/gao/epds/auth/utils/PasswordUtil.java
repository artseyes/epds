package gov.gao.epds.auth.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.LocalDate;

import gov.gao.epds.auth.dao.Login_attempt_dao;
import gov.gao.epds.auth.dao.User_info_dao;
import gov.gao.epds.auth.dto.ServiceResponse;
import gov.gao.epds.auth.dto.User_info_dto;
import gov.gao.epds.auth.persistence.entity.Login_attempt;
import gov.gao.epds.auth.persistence.entity.User_event_log;
import gov.gao.epds.auth.persistence.entity.User_info;
import gov.gao.epds.auth.service.EmailService;
import gov.gao.epds.enums.UserAuthRoles;
import static gov.gao.epds.enums.UserAuthRoles.*;
import gov.gao.epds.passwordutils.PasswordStorage;
import gov.gao.epds.passwordutils.PasswordStorage.CannotPerformOperationException;
import gov.gao.epds.passwordutils.PasswordStorage.InvalidHashException;


public class PasswordUtil {
	public static ServiceResponse handleExpiredOrExpiringPassword(
			ServiceResponse serviceResponse, User_info user_info,
			User_info_dto user_info_dto, User_info_dao user_info_dao,EmailService emailService) {
		
		/*
		 * PSUEDOCODE: - for active account, handle for expired password and
		 * expiring password - for non-active account, handle for expired
		 * temporary password
		 *
		 */

		int numberOfDaysPasswordExpiringIn = 0;
		try {
			//checking when was the last time temp password was assigned
			int passwordAge = getNumberOfDaysAfterPasswordWasAssignedLastTime(
					user_info_dto.getUser_id(), user_info_dao,4);

			
			
			if (user_info_dto.getAccount_status_id() == 1 
					|| user_info_dto.getAccount_status_id() == 7 ) /* New User account or PLCG account reset*/{
				UserAuthRoles role = UserAuthRoles.getByCode(user_info.getRole_id());
				if (role == PROTESTER && passwordAge >= PolicyParam.numberOfDaysToExpireVendorTempPassword) {
					updateUserInfoAndAssignTempPassword(serviceResponse, user_info, user_info_dto, user_info_dao,
							emailService);
				} else if (role != PROTESTER && passwordAge >= PolicyParam.numberOfDaysToExpireNonVendorTempPassword) {
					PasswordUtil.assignTemporaryPassword(user_info,
							 user_info_dto.getAccount_status_id(), user_info_dao, emailService);

					serviceResponse
					.setMessage("Temp. Password Expired");
					user_info_dto.setAccount_status_id(user_info.getAccount_status_id());
				} 
				
				
			}else if (user_info_dto.getAccount_status_id() == 2) /* if active */{
				
				passwordAge = getNumberOfDaysAfterPasswordWasAssignedLastTime(user_info.getUser_id(), user_info_dao, 8);
				
				if (passwordAge >= PolicyParam.numberOfDaysToExpirePassword) {
					user_info.setAccount_status_id(5); // set expired
					user_info_dao.update(user_info);

					user_info_dao
							.setUserEventLog(user_info_dto.getUser_id(), 5);

					serviceResponse.setMessage("Your password has expired. ");
					user_info_dto.setAccount_status_id(5);
				}else {
					
					numberOfDaysPasswordExpiringIn = PolicyParam.numberOfDaysToExpirePassword - passwordAge;
					user_info_dto.setNumOfDaysLeftToExpirePwd(numberOfDaysPasswordExpiringIn);
					if (numberOfDaysPasswordExpiringIn 
							<= PolicyParam.numberOfDaysToNotifyUserOfPasswordExpiration) {
						
						user_info_dto.setPasswordExpiring(true);
						Util.setResponseMessage(serviceResponse,
								"Your password is expiring in "
										+ numberOfDaysPasswordExpiringIn);
					}
				}
				//this check was added to address roshan expired password loop issue.
			} else if (user_info.getAccount_status_id().equals(8)){
				
				// if the user is not new and not active then basically over here we will need to check if the user is  log in with temp password 
				//and if the temp password is expired or not. 
				//if temp password is expired the send a new temp password.
				
				if (checkIfNewTempPasswordNeedsToSent(user_info.getUser_id(),
						user_info, user_info_dao,emailService)) {
					
					PasswordUtil.assignTemporaryPassword(user_info,
							8, user_info_dao, emailService);
					serviceResponse
							.setMessage("Temp. Password Expired");
				}
			}

			serviceResponse.setIsSuccess(true);
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
	 * @param user_info_dto
	 * @param user_info_dao
	 * @param emailService
	 * @throws Exception
	 */
	private static void updateUserInfoAndAssignTempPassword(ServiceResponse serviceResponse, User_info user_info,
			User_info_dto user_info_dto, User_info_dao user_info_dao, EmailService emailService) throws Exception {
		PasswordUtil.assignTemporaryPassword(user_info,
				8, user_info_dao, emailService);

		serviceResponse
		.setMessage("Temp. Password Expired");
		user_info_dto.setAccount_status_id(8);
	}

	/**
	 * @param user_id
	 * @param user_info_dao
	 * @param eventId
	 * @return
	 */
	public static int getNumberOfDaysAfterPasswordWasAssignedLastTime(
			Integer user_id, User_info_dao user_info_dao, Integer eventId) {
		User_event_log user_event_log = user_info_dao.getLastUserEventLog(
				user_id, eventId);
		 
		
		if (user_event_log != null){
			
			int numOfDays = DateUtil
					.getNumberOfDaysPassedAfterProvidedTimeStamp(user_event_log
							.getTime_stamp());
			return numOfDays;	
		}
		
		return 0;
		
	}
	
	
	
	public static boolean checkIfAccountNeedsToBeDeactivated(String email, Login_attempt_dao login_attempt_dao) {
		
		Login_attempt lastSuccessfulLoginAttempt = login_attempt_dao.getLastSuccessfulLoginAttempt(email);
		
		int numOfDays = 0;
		
		if (lastSuccessfulLoginAttempt != null){
		
			DateTime currentTime = new DateTime();
			DateTime storedTime = new DateTime(lastSuccessfulLoginAttempt.getTime_stamp());
			
			numOfDays = Days.daysBetween(new LocalDate(storedTime), new LocalDate(currentTime)).getDays();
		}
		
		return Math.abs(numOfDays) >= PolicyParam.numberOfDaysToDisableAccount ;
	}
	
	public static void handleSelfResetTempPasswords(
			Integer user_id, User_info user_info, User_info_dao user_info_dao,EmailService emailService) throws Exception {
		
		Long differenceInMins = getTimeInMinsWhenTheLastTempPasswordWasAssigned(user_id, user_info_dao);
		
		if (differenceInMins >= PolicyParam.numberOfMinsToExpireSelfResetTempPassword){
			
			PasswordUtil.assignTemporaryPassword(user_info,
					8, user_info_dao, emailService);
			
		}
	}
	
	public static boolean checkIfNewTempPasswordNeedsToSent(
			Integer user_id, User_info user_info, User_info_dao user_info_dao,EmailService emailService) throws Exception {
		
		Long differenceInMins = getTimeInMinsWhenTheLastTempPasswordWasAssigned(user_id, user_info_dao);
		
		/*differenceInMins = new Long(10);*/
		
		
		return (Math.abs(differenceInMins) >= PolicyParam.numberOfMinsToExpireSelfResetTempPassword);
	}

	/**
	 * @param user_id
	 * @param user_info_dao
	 * @return
	 */
	public static Long getTimeInMinsWhenTheLastTempPasswordWasAssigned(Integer user_id, User_info_dao user_info_dao) {
		User_event_log user_event_log = user_info_dao.getLastUserEventLog(
				user_id, 4);
		
		DateTimeZone dateTimeZone = DateTimeZone.forID("America/Chicago");
		DateTime currentTime = DateTime.now(dateTimeZone);
		DateTime storedTime = new DateTime(user_event_log
				.getTime_stamp());
		Duration duration = new Duration(storedTime, currentTime);
		Long differenceInMins  = duration.getStandardMinutes();
		return Math.abs(differenceInMins);
	}

	/**
	 * create PBKDF2WithHmacSHA512 hash
	 * @param password
	 * @return
	 */
	public static String createHash(String password){
		String hash = "";
		try {
			hash = PasswordStorage.createHash(password);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
		return hash;
	}
	public static boolean assignTemporaryPassword(User_info user_info,
			Integer new_account_status_id, User_info_dao user_info_dao,
			EmailService emailService) throws Exception {
		String temporaryPassword = Util.getTemporaryPassword();

		String accountActivityType = "";
		user_info.setPassword(PasswordUtil.createHash(PasswordUtil.generateHash(temporaryPassword)));
		user_info.setAccount_status_id(new_account_status_id);

		user_info_dao.update(user_info);
		user_info_dao.setUserEventLog(user_info.getUser_id(), 4); // password
																// assigned
																	// event
		
		/*
		 * need to test this
		 */	
		
		if (new_account_status_id == 4){
			
			user_info_dao.setUserEventLog(user_info.getUser_id(), 3); // account
			// locked
			// event
		}else if (new_account_status_id == 5){
			
			user_info_dao.setUserEventLog(user_info.getUser_id(), 5); // account
			// expired
			// event
		}else if (new_account_status_id == 6){
			
			user_info_dao.setUserEventLog(user_info.getUser_id(), 6); // account
			// deactivated
			// event
		}

		
		boolean isEmailSent = emailService.emailTemporaryPasswordToUser(
				user_info, temporaryPassword)
				.getIsSuccess();
		
		
		if (new_account_status_id == 4){
			accountActivityType ="ACCOUNT LOCKED";
		}else if (new_account_status_id == 8 
				|| new_account_status_id == 1 
				|| new_account_status_id == 7){
			accountActivityType ="TEMPORARY PASSWORD SENT";
		}
		
		emailService.notifyAccountActivityToSysAdmins(user_info.getEmail(), accountActivityType);

		if (!isEmailSent) {
			// come back later (we can let application admin know or revert back
			// user_info status etc.)
		}

		return isEmailSent;
	}
	
	
	/**
	 * From UI we don't get any plain text password. We get the sha512 hash of the plain text password encoded in base64 with random string.
	 * To get the hash of UI pwd we need to first decode the password using base 64 and then get the hash
	 * 
	 * For security answers: from the UI we get plain text answer generate 512 hash and then create hash of that and store
	 * @param uiPwd
	 * @param passwordStoredInDb
	 * @return
	 */
	public static Boolean comparePasswordOrSecurityAnswers(String uiPwd, String passwordStoredInDb){
		
		
		String uiPasswordHash = getHashFromPwd(uiPwd);
		
		
		return PasswordUtil.compareHash(uiPasswordHash, passwordStoredInDb);
	}
	
   
   /**
		Utility method to generate the SHA 512 hash of the password
	 * @param toHash
	 * @return
	 */
	public static String generateHash(String toHash) {
		    MessageDigest md = null;
		    byte[] hash = null;
		    try {
		        md = MessageDigest.getInstance("SHA-512");
		        hash = md.digest(toHash.getBytes("UTF-8"));
		    } catch (NoSuchAlgorithmException e) {
		        e.printStackTrace();
		    } catch (UnsupportedEncodingException e) {
		        e.printStackTrace();
		    }
		    
		    if (hash != null){
		    
		    	return convertToHex(hash);
		    }
		    
		    return "";
		}
	 
	/**
	* Converts the given byte[] to a hex string.
	* @param raw the byte[] to convert
	* @return the string the given byte[] represents
	*/
	
	private static  String convertToHex(byte[] raw) {
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < raw.length; i++) {
	        sb.append(Integer.toString((raw[i] & 0xff) + 0x100, 16).substring(1));
	    }
	    return sb.toString();
	}
	
	public static Boolean compareHash(String uiPasswordHash,String hashStoredInDb){
		Boolean passwordVerified = false;
		
		try {
			passwordVerified = PasswordStorage.verifyPassword(uiPasswordHash, hashStoredInDb);
		} catch (CannotPerformOperationException | InvalidHashException e) {
			e.printStackTrace();
		}
		return passwordVerified;
	}
	
	
	public static Boolean compareUIHashWithDbStoredHash(String uiPasswordHash,String hashStoredInDb){
		
		return compareHash(uiPasswordHash, hashStoredInDb);
	}
	
	
	/**
		From UI we get password encoded in B64... The password needs to first get decode then remove the hash from the password.
	 * @param uiPwdB64Encoded
	 * @return
	 */
	public static String getHashFromPwd(String uiPwdB64Encoded){

		
        String [] hashParts = null;
        
		if (null != uiPwdB64Encoded) {
			try {
				byte[] base64decodedBytes = Base64.getUrlDecoder().decode(uiPwdB64Encoded);
				hashParts = new String(base64decodedBytes, "utf-8").split(":");

				if (hashParts != null) {
					return hashParts[0];
				}

			} catch (UnsupportedEncodingException e) {
				System.out.println("Error :" + e.getMessage());
			}
		}	
		
		return "";
	}
	
	
	/**
		When validating the password we just encode the password in base 64
	 * @param uiPwdB64Encoded
	 * @return
	 */
	public static String getPwdFromBase64(String uiPwdB64Encoded){
	
		
		try {
			uiPwdB64Encoded = new String(uiPwdB64Encoded.getBytes(),"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		 String hashParts = null;
	        try {
	        	byte[] base64decodedBytes = Base64.getUrlDecoder().decode(uiPwdB64Encoded);
	            hashParts = new String(base64decodedBytes, "UTF-8");
	            
	            if (hashParts != null) {
					return hashParts;
				}
	            
	         }catch(UnsupportedEncodingException e){
	            System.out.println("Error :" + e.getMessage());
	         }
	        
	        return "";
	    
	
	}
}
