package gov.gao.epds.auth.utils;

import gov.gao.epds.auth.dao.User_info_dao;
import gov.gao.epds.auth.dto.User_info_epds;
import gov.gao.epds.auth.persistence.entity.User_info;
import gov.gao.epds.auth.service.EmailService;
import gov.gao.epds.enums.UserAuthRoles;
import static gov.gao.epds.enums.UserAuthRoles.*;

import java.util.ArrayList;
import java.util.List;

public class AccountLockUtil {

	public static boolean lockUser(User_info user_info,
			User_info_dao user_info_dao, EmailService emailService) {
		boolean isEmailSent = false;

		try {
			UserAuthRoles role = UserAuthRoles.getByCode(user_info.getRole_id());
			if (role == PROTESTER) /*if vendor*/{
				isEmailSent = PasswordUtil.assignTemporaryPassword(user_info,
						4, user_info_dao, emailService);
			}else{
				user_info.setAccount_status_id(4);
				
				user_info_dao.update(user_info); //update account
				user_info_dao.setUserEventLog(user_info.getUser_id(), 3); // account locked event
				
				if (role == AGENCY) /*if Agency User*/ {
					List<String> agencyPOCEmails = getAgencyPocEmails(user_info);
					
					isEmailSent = emailService.notifyLockStatusToAgencyAndMayBePLCG(
							agencyPOCEmails, user_info);
				} else if (role == GAO) /*if GAO User*/ {
					List<String> gaoAdminEmails = getGAOAdminEmails(); 
					
					isEmailSent = emailService.notifyLockStatusToGAOUserAndMaybeEASSupport(gaoAdminEmails, user_info);
				}
				
				if(!isEmailSent){
				//come back later (maybe let application admin know)	
				}
				
			}
		} catch (Exception e) {
			isEmailSent = false;
		}

		return isEmailSent;
	}

	@SuppressWarnings("unchecked")
	private static List<String> getGAOAdminEmails() {
		List<User_info_epds> listOfUserInfoEpds = (List<User_info_epds>) Util
				.getResponseFromRestService("", true, User_info_epds.class,
						"get-gao-admin-user_infos/", "get");

		return convertToEmail(listOfUserInfoEpds);
	}
	

	@SuppressWarnings("unchecked")
	public static List<String> getAgencyPocEmails(User_info user_info) {
		List<User_info_epds> listOfUserInfoEpds = (List<User_info_epds>) Util
				.getResponseFromRestService("", true, User_info_epds.class,
						"get-agency-poc-user_infos/" + user_info.getUser_id(), "get");

		return convertToEmail(listOfUserInfoEpds);
	}

	private static List<String> convertToEmail(
			List<User_info_epds> listOfUserInfo) {
		List<String> listOfEmail = new ArrayList<String>();

		for (User_info_epds user_info_epds : listOfUserInfo) {
			if(!user_info_epds.getEmail().equalsIgnoreCase("cbca.eds@cbca.gov "))/*temporary for testing, we need to get rid of this if block for production*/{
				listOfEmail.add(user_info_epds.getEmail());	
			}
		}

		return listOfEmail;
	}

}
