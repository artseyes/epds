package gov.gao.epds.auth.dao;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.gao.epds.auth.persistence.DataAccess;
import gov.gao.epds.auth.persistence.entity.Rules_Of_Behavior;
import gov.gao.epds.auth.persistence.entity.User_event_log;
import gov.gao.epds.auth.persistence.entity.User_info;

@Repository
public class User_info_dao extends DataAccess {
	/*
	 * @Autowired private DataAccess access;
	 */

	@Transactional
	public User_info getUserInfoForGivenEmailAndPassword(String user_email,
			String password) {
		String query = "from User_info where email = :email and password = :password";
		User_info user_info = null;

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("email", user_email);
		map.put("password", password);

		List<?> resultList = queryWithParams(query, map);

		if (resultList != null && resultList.size() > 0) {
			user_info = (User_info) resultList.get(0);
		}

		return user_info;
	}
	
	
	@Transactional
	public Rules_Of_Behavior getRulesOfBehaviorTimeStamp(String email) {
		String query = "from Rules_Of_Behavior where email = :email";
		
		Rules_Of_Behavior rob = null;

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("email", email);

		List<?> resultList = queryWithParams(query, map);

		if (resultList != null && resultList.size() > 0) {
			rob = (Rules_Of_Behavior) resultList.get(0);
		}

		return rob;
	}
	

	@Transactional
	public boolean checkIfUserEmailAlreadyExist(String user_email) {
		String query = "from User_info where email = :email";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("email", user_email.toLowerCase(Locale.ENGLISH));

		List<?> resultList = queryWithParams(query, map);
		if (resultList != null && resultList.size() > 0) {
			return true;
		}

		return false;
	}

	@Transactional
	public void setUserStatusAsLocked(String user_email) {
		// TODO Auto-generated method stub

	}

	@Transactional
	public User_info getUserInfoByEmail(String user_email) {
		String query = "from User_info where email = :email";
		User_info user_info = null;

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("email", user_email.toLowerCase(Locale.ENGLISH));

		List<?> resultList = queryWithParams(query, map);

		if (resultList != null && resultList.size() > 0) {
			user_info = (User_info) resultList.get(0);
		}

		return user_info;
	}

	@Transactional
	public User_info getUserInfoByUserId(Integer user_id) {
		String query = "from User_info where user_id = :user_id";
		User_info user_info = null;

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user_id", user_id);

		List<?> resultList = queryWithParams(query, map);

		if (resultList != null && resultList.size() > 0) {
			user_info = (User_info) resultList.get(0);
		}

		return user_info;
	}

	@Transactional
	public User_event_log getLastUserEventLog(Integer user_id,
			Integer user_event_id) {
		String query = "from User_event_log where user_id = :user_id and user_event_id = :user_event_id order by time_stamp asc";
		User_event_log user_event_log = null;

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user_id", user_id);
		map.put("user_event_id", user_event_id);

		List<?> resultList = queryWithParams(query, map);

		if (resultList != null && resultList.size() > 1) {
			user_event_log = (User_event_log) resultList
					.get(resultList.size() - 1);
		}else if (resultList != null && resultList.size() == 1) {
			user_event_log = (User_event_log) resultList
					.get(0);
		}

		return user_event_log;
	}

	@Transactional
	public User_event_log setUserEventLog(Integer user_id, Integer user_event_id) {
		
		DateTimeZone dateTimeZone = DateTimeZone.forID("America/Chicago");
		DateTime currentTime = DateTime.now(dateTimeZone);
		
		User_event_log user_event_log = new User_event_log();
		user_event_log.setUser_event_id(user_event_id);
		user_event_log.setUser_id(user_id);
		user_event_log.setTime_stamp(new Timestamp(currentTime.getMillis()));

		user_event_log = save(user_event_log);

		return user_event_log;
	}

	
	
	@SuppressWarnings({ "unchecked", "deprecation"})
	@Transactional
	public User_event_log getMostRecentUserEventLogByEventId(Integer userId, Integer userEventId) {
		    
		DetachedCriteria maxQuery = DetachedCriteria.forClass(User_event_log.class);
		maxQuery.add(Restrictions.eq("user_id", userId));
		maxQuery.add(Restrictions.eq("user_event_id", userEventId));
		maxQuery.setProjection(Projections.max("time_stamp"));

		Criteria criteria = getCurrentSession().createCriteria(User_event_log.class);
		criteria.add(Property.forName("time_stamp").eq(maxQuery));
		
		List<User_event_log> userEventLog = criteria.list();
		
		if (null !=  userEventLog &&  !userEventLog.isEmpty()){
			return userEventLog.get(0);
		}

		return null;
	}
	@SuppressWarnings("unchecked")
	@Transactional
	public List<User_info> getListOfAllUsers() {
		String query = "from User_info";

		Map<String, Object> map = new HashMap<String, Object>();

		List<?> resultList = queryWithParams(query, map);
		

		return (List<User_info>) resultList;
	}
}
