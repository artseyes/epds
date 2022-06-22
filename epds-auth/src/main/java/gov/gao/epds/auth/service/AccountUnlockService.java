package gov.gao.epds.auth.service;

import gov.gao.epds.auth.dao.User_info_dao;
import gov.gao.epds.auth.dao.User_security_answer_dao;
import gov.gao.epds.auth.dto.ServiceResponse;
import gov.gao.epds.auth.dto.User_info_dto;
import gov.gao.epds.auth.persistence.entity.User_info;
import gov.gao.epds.auth.persistence.entity.User_security_answer;
import gov.gao.epds.auth.utils.AccountLockUtil;
import gov.gao.epds.auth.utils.PasswordUtil;
import gov.gao.epds.auth.utils.Util;

import static gov.gao.epds.enums.UserAuthRoles.*;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountUnlockService {
	@Autowired
	User_info_dao user_info_dao;
	@Autowired
	User_security_answer_dao user_security_answer_dao;
	@Autowired
	EmailService emailService;

	public ServiceResponse getResponseForForgotPasswordEmailSubmission(String email) {
		/*PSEUDOCODE
		 - get user_info
		 - based on user_info.role_id set role in response message
		 */
		ServiceResponse serviceResponse = new ServiceResponse();

		try {
			User_info user_info = user_info_dao.getUserInfoByEmail(email);

			if (user_info == null) {
				serviceResponse.setMessage("Email doesn't exist");
				serviceResponse.setSuccess(false);
			} /*else if (user_info.getAccount_status_id() != 2) {
				serviceResponse.setMessage(Util.getErrorMessage(user_info
						.getAccount_status_id()));
				serviceResponse.setSuccess(true);
				}*/
			else {
				if (user_info.getRole_id().equals(PROTESTER.getCode())) {
					boolean isTemporaryPasswordSent = PasswordUtil
							.assignTemporaryPassword(user_info, 8,
									user_info_dao, emailService);

					if (isTemporaryPasswordSent) {
						serviceResponse.setMessage("ROLE: VENDOR");
						serviceResponse.setIsSuccess(true);
						
					} else {
						serviceResponse
								.setMessage("Internal error occurred while sending email");
					}
				} else {
					

					User_info_dto user_info_dto = new User_info_dto();
					
					if (!user_info.getAccount_status_id().equals(4)){
						
						Map<Integer, String> mapOfSecQIdToSecurityQuestion = getMapOfSecQIdToSecurityQuestion(user_info);
						user_info_dto.setUser_id(user_info.getUser_id());
						user_info_dto
								.setSecQIdToQuestionMap(mapOfSecQIdToSecurityQuestion);
					}
					user_info_dto.setAccount_status_id(user_info.getAccount_status_id());

					serviceResponse.setData(user_info_dto);
					serviceResponse.setMessage("ROLE: NON-VENDOR");
					serviceResponse.setIsSuccess(true);
				}
			}
		} catch (Exception e) {
			serviceResponse.setMessage("Internal Server Error");
			serviceResponse.setException(e.getMessage());
			serviceResponse.setStackTraceDetail(Util.getStackTraceMessage(e));
			serviceResponse.setIsSuccess(false);
		}

		return serviceResponse;
	}

	public Map<Integer, String> getMapOfSecQIdToSecurityQuestion(
			User_info user_info) {
		List<User_security_answer> listOfUserSecurityAnswer = user_security_answer_dao
				.getListOfUserSecurityAnswer(user_info.getUser_id());

		Map<Integer, String> mapOfSecQIdToSecurityQuestion = Util
				.convertToMapOfSecQIdToSecurityQuestion(listOfUserSecurityAnswer);
		return mapOfSecQIdToSecurityQuestion;
	}

	public ServiceResponse getResponseForCheckSecurityAnswer(String user_id,
			String secQId, String answer, Integer numberOfAttempts) {
		ServiceResponse serviceResponse = new ServiceResponse();

		try {
			User_security_answer user_security_answer = user_security_answer_dao
					.getUserSecurityAnswer(user_id, secQId);

			if (user_security_answer != null) {
				
				Boolean compareSecAns = PasswordUtil.compareHash(PasswordUtil
						.generateHash(answer.trim().toLowerCase(Locale.ENGLISH)), user_security_answer.getSecurity_ans());
				
				if (compareSecAns) {
					serviceResponse.setData("Y");

					User_info user_info = user_info_dao
							.getUserInfoByUserId(Integer.valueOf(user_id));
					
					
					//I think when we can set account status 2 and when they login 
					
					/*user_info.setAccount_status_id(2);
					user_info_dao.update(user_info);*/
					
					PasswordUtil.assignTemporaryPassword(user_info, 8,
							user_info_dao, emailService);

					serviceResponse.setMessage("Temporary Password sent");
				} else {
					serviceResponse.setData("N");

					if (numberOfAttempts == 6) {
						User_info user_info = user_info_dao
								.getUserInfoByUserId(Integer.valueOf(user_id));
						AccountLockUtil.lockUser(user_info, user_info_dao, emailService);

						serviceResponse.setMessage("User has been locked");
					}

				}
			} else {
				serviceResponse.setMessage("No record found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			serviceResponse.setException(e.getMessage());
			serviceResponse.setStackTraceDetail(Util.getStackTraceMessage(e));
			serviceResponse.setIsSuccess(false);
		}

		return serviceResponse;
	}
}
