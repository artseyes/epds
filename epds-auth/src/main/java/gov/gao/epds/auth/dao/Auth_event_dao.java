package gov.gao.epds.auth.dao;

import gov.gao.epds.auth.persistence.DataAccess;
import gov.gao.epds.auth.persistence.entity.Auth_event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class Auth_event_dao extends DataAccess {
	/*@Autowired
	private DataAccess access;*/

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Auth_event> getListOfAuth_eventBasedOnUserId(String user_id) {
		String query = "from Auth_event where user_id =:user_id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user_id", user_id);

		List<?> resultList = queryWithParams(query, map);

		if (resultList != null && resultList.size() > 0) {
			return (List<Auth_event>) resultList;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Auth_event> getListOfAuth_eventBasedOnClient_ip(String client_ip) {
		String query = "from Auth_event where client_ip =:client_ip";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("client_ip", client_ip);

		List<?> resultList = queryWithParams(query, map);

		if (resultList != null && resultList.size() > 0) {
			return (List<Auth_event>) resultList;
		}

		return null;
	}

	@Transactional
	public Auth_event getAuth_eventBasedOnEvent_ip(Integer event_id) {
		String query = "from Auth_event where event_id =:event_id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("event_id", event_id);

		List<?> resultList = queryWithParams(query, map);

		if (resultList != null && resultList.size() > 0) {
			return (Auth_event) resultList.get(0);
		}

		return null;
	}

}
