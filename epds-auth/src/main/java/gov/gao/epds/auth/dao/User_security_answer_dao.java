package gov.gao.epds.auth.dao;

import gov.gao.epds.auth.dto.ServiceResponse;
import gov.gao.epds.auth.persistence.DataAccess;
import gov.gao.epds.auth.persistence.entity.Security_question;
import gov.gao.epds.auth.persistence.entity.User_security_answer;
import gov.gao.epds.auth.utils.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class User_security_answer_dao extends DataAccess {
	@Transactional
	public ServiceResponse removeOldSecurityAnswersByUserId(Integer user_id) {
		ServiceResponse serviceResponse = new ServiceResponse();

		String query = "delete from User_security_answer where user_id = "
				+ user_id;
		try {
			executeUpdateSQL(query);
			serviceResponse.setIsSuccess(true);
		} catch (Exception e) {
			serviceResponse.setException(Util.getStackTraceMessage(e));
			serviceResponse.setIsSuccess(false);
		} finally {

		}

		return serviceResponse;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Security_question> getSecurityQuestions() {
		String query = "from Security_question";

		Map<String, Object> map = new HashMap<String, Object>();

		List<?> resultList = queryWithParams(query, map);
		if (resultList != null && resultList.size() > 0) {
			return (List<Security_question>) resultList;
		}

		return null;
	}

	@Transactional
	public List<User_security_answer> getListOfUserSecurityAnswer(
			Integer user_id) {
		String query = "from User_security_answer a, Security_question b where a.user_id = :user_id and a.security_q_id = b.security_q_id";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user_id", user_id);

		List<?> resultList = queryWithParams(query, map);
		List<User_security_answer> listOfUserSecurityAnswer = null;

		if (resultList != null && resultList.size() > 0) {
			listOfUserSecurityAnswer = Util.getDesiredClassObjectList(
					resultList, User_security_answer.class);
			List<Security_question> listOfSecurityQuestion = Util
					.getDesiredClassObjectList(resultList,
							Security_question.class);

			Util.fillUpQuestionField(listOfUserSecurityAnswer,
					listOfSecurityQuestion);
		}
		return listOfUserSecurityAnswer;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public User_security_answer getUserSecurityAnswer(String user_id,
			String secQId) {
		User_security_answer user_security_answer = null;

		String query = "from User_security_answer where user_id = :user_id and security_q_id = :security_q_id";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user_id", Integer.valueOf(user_id));
		map.put("security_q_id", Integer.valueOf(secQId));

		List<?> resultList = queryWithParams(query, map);
		if (resultList != null) {
			user_security_answer = ((List<User_security_answer>) resultList)
					.get(0);
		}

		return user_security_answer;
	}
}
