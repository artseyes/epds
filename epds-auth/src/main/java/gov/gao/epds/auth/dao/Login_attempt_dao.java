package gov.gao.epds.auth.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.gao.epds.auth.persistence.DataAccess;
import gov.gao.epds.auth.persistence.entity.Login_attempt;

@Repository
public class Login_attempt_dao extends DataAccess {
	/*
	 * @Autowired private DataAccess access;
	 */

	@Transactional
	public Login_attempt getLogin_attempt(Integer login_attempt_id) {
		String query = "from Login_attempt where login_attempt_id = :login_attempt_id";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("login_attempt_id", login_attempt_id);

		List<?> resultList = queryWithParams(query, map);
		if (resultList != null && resultList.size() > 0) {
			return (Login_attempt) resultList.get(0);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Login_attempt> getListOfLoginAttemptBasedOnClientIp(
			String client_ip,
			Integer numberOfLastMinsToCountLastLoginAttemptByClientIp) {
		String query = "from Login_attempt where client_ip = :client_ip";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("client_ip", client_ip);

		List<?> resultList = queryWithParams(query, map);
		if (resultList != null && resultList.size() > 0) {
			return (List<Login_attempt>) resultList;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Login_attempt> getListOfLogin_attemptBasedOnClientIpForLastGivenMins(
			String client_ip, String numberOfMinutest) {
		String query = "from Login_attempt where client_ip = :client_ip and (timestamp >= getdate() - (interval :mins minute))";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("client_ip", client_ip);
		map.put("mins", numberOfMinutest);

		List<?> resultList = queryWithParams(query, map);
		if (resultList != null && resultList.size() > 0) {
			return (List<Login_attempt>) resultList;
		}

		return null;
	}

	
	/*Amer : Come back to this one the previous query was not getting the correct number of records.
	 * from Login_attempt where user_email = :user_email and (time_stamp >= :lastTimeStamp) and success = 'N'
	 *  was not calculating the correct number of records.
	 * right now I have hardcoded 10 mins in the query but need to make it dynamic
	 * 
	@SuppressWarnings("unchecked")
	@Transactional
	public List<Login_attempt> getListOfLoginAttempt(String user_email,
			int numberOfLastMinutes) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -numberOfLastMinutes);

		Timestamp lastTimeStamp = new Timestamp(calendar.getTimeInMillis());
		System.out.println(lastTimeStamp.getTime());

		String query = "from Login_attempt where user_email = :user_email and time_Stamp > getdate() - (10/1440) and success = 'N'";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user_email", user_email);
		map.put("lastTimeStamp", lastTimeStamp);
		map.put("numOfMins", (numberOfLastMinutes/1440));

		List<?> resultList = queryWithParams(query, map);
		if (resultList != null && resultList.size() > 0) {
			for (Object each : resultList) {
				Login_attempt eachLA = (Login_attempt) each;
				System.out.println(eachLA.getTime_stamp().toString());
			}

			return (List<Login_attempt>) resultList;
		}

		return null;
	}*/

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Login_attempt> getListOfLoginAttempt(String email,
			int numberOfLastMinutes) {


		String timeStamp = "(" + String.valueOf(numberOfLastMinutes) + "/" + "1440"+ ")";
		
		String query = "from Login_attempt where user_email = :user_email ";
		
		query = query + " and time_Stamp > getdate() - " + timeStamp + " ORDER BY time_Stamp DESC";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user_email", email);

		List<?> resultList = queryWithParams(query, map);
		if (resultList != null && resultList.size() > 0) {
			return (List<Login_attempt>) resultList;
		}else{
			return  new ArrayList<Login_attempt>();
		}

	}
	
	
	
	@SuppressWarnings({ "unchecked", "deprecation"})
	@Transactional
	public Login_attempt getLastSuccessfulLoginAttempt(String email) {
		    
		DetachedCriteria maxQuery = DetachedCriteria.forClass(Login_attempt.class);
		maxQuery.add(Restrictions.eq("user_email", email));
		maxQuery.setProjection(Projections.max("time_stamp"));

		Criteria criteria = getCurrentSession().createCriteria(Login_attempt.class);
		criteria.add(Property.forName("time_stamp").eq(maxQuery));
		
		List<Login_attempt> login_attempts = criteria.list();
		
		if (null !=  login_attempts &&  !login_attempts.isEmpty()){
			return login_attempts.get(0);
		}

		return null;
	}
	
	
	
	
}
