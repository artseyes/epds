package gov.gao.epds.session;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.gao.epds.controller.HomeController;

public class EpdsSession implements Serializable {

	private static final long serialVersionUID = -7229859566673146643L;

	public static Map<Integer, Map<String, Object>> userIdToAttributeMap = new ConcurrentHashMap<Integer, Map<String, Object>>();

	public static Map<String, String> uniqueSessionIdToUserIdMap = new ConcurrentHashMap<String, String>();

	private final static Logger logger = LoggerFactory.getLogger(EpdsSession.class);

	/**
	 * @param userId
	 * @param token
	 */
	public static  void startUserSession(Integer userId, String token) {
		Map<String, Object> userAttributeMap = new HashMap<String, Object>();


		userIdToAttributeMap.put(userId, userAttributeMap);
	}

	public static void setAttribute(HttpServletRequest request,
			String attributeName, Object attributeObject) {
		Map<String, Object> userAttributeMap = getUserAttributeMap(request);
//		if (userAttributeMap != null && attributeObject !=null) {
		if (userAttributeMap != null) {
			userAttributeMap.put(attributeName, attributeObject);
		}
	}

	private static  Map<String, Object> getUserAttributeMap(
			HttpServletRequest request) {

		Integer userId = (Integer) request.getAttribute("userId");
		Map<String, Object> userAttributeMap = null;

		if (null != userId){
			userAttributeMap = userIdToAttributeMap.get(userId);
		}

		return userAttributeMap;
	}

	public static  Object getAttribute(HttpServletRequest request,
			String attributeName) {
		Object returnObject = null;
		Map<String, Object> userAttributeMap = getUserAttributeMap(request);
		if (userAttributeMap != null) {
			returnObject = userAttributeMap.get(attributeName);
		}

		return returnObject;
	}

	public static  void removeAttribute(HttpServletRequest request,
			String attributeName) {
		Map<String, Object> userAttributeMap = getUserAttributeMap(request);
		userAttributeMap.remove(attributeName);
	}

	public static  void endUserSession(HttpServletRequest request) {
		Integer userId = (Integer) request.getAttribute("userId");

		logger.info("User for userId={}, requestUrl ={}, eventType={}", userId, request.getRequestURL(), "End User Session");

		Map<String, Object> userAttributeMap = getUserAttributeMap(request);
		if (userAttributeMap != null) {
			userIdToAttributeMap.remove(userId);
		}
	}

	public static  void endUserSession(Integer userId) {
		logger.info("User for userId={},Event Initiation={}, eventType={}", userId,"System Initiated", "End User Session");

		Map<String, Object> userAttributeMap = userIdToAttributeMap.get(userId);
		if (userAttributeMap != null) {
			userIdToAttributeMap.remove(userId);
		}
	}
	public static  Boolean checkIfSessionExists(Integer userId) {
		Boolean isUserSessionExists = false;
		Map<String, Object> userAttributeMap = userIdToAttributeMap.get(userId);
		if (userAttributeMap != null) {
			isUserSessionExists = true;
		}

		return isUserSessionExists;
	}


	public static  Integer getUserIdFromSession(String tokenInRequest) {
		Integer userId = null;

		if (EpdsSession.userIdToAttributeMap.size() > 0 && !EpdsSession.userIdToAttributeMap.isEmpty()) {

			for (Iterator<Entry<Integer, Map<String, Object>>> userIdToAttributeMapIterator = EpdsSession.userIdToAttributeMap.entrySet()
					.iterator(); userIdToAttributeMapIterator.hasNext();) {

				Entry<Integer, Map<String, Object>> entry = userIdToAttributeMapIterator.next();

				Map<String, Object> eachMapObject = entry.getValue();

				String tokenInSession = (String) eachMapObject.get("token");


				if (null  != tokenInSession && tokenInSession.equals(tokenInRequest)){
					userId = entry.getKey();
				}

			}

		}

		return userId;
	}


	public static void setUniqueSessionIdToUserIdMap(HttpServletRequest request, String uniqueSessionId) {
		Integer userId = (Integer) request.getAttribute("userId");
		String userIdAndDate = userId + ":" + new Date().getTime();
		uniqueSessionIdToUserIdMap.put(uniqueSessionId, userIdAndDate);

		return;
	}

	public static String getUserIdByUniqueSessionId(String uniqueSessionId) {
		return uniqueSessionIdToUserIdMap.get(uniqueSessionId);
	}
}
