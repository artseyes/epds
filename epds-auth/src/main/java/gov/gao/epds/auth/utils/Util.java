package gov.gao.epds.auth.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

import gov.gao.epds.auth.dao.User_info_dao;
import gov.gao.epds.auth.dto.ServiceResponse;
import gov.gao.epds.auth.dto.User_info_dto;
import gov.gao.epds.auth.dto.User_info_epds;
import gov.gao.epds.auth.persistence.entity.Rules_Of_Behavior;
import gov.gao.epds.auth.persistence.entity.Security_question;
import gov.gao.epds.auth.persistence.entity.User_info;
import gov.gao.epds.auth.persistence.entity.User_security_answer;
import gov.gao.epds.tokenutils.TokenUtils;


public class Util {


	public static final String SPECIALCHARACTERS = "!#$*-%+=?:;~";
	public static final String AUTHORIZATON_HEADER_PASS = "5q3fJsFHZ6a#!gv&t7@xwF_y";

	public static String getTemporaryPassword() {

		List<CharacterRule> rules = Arrays.asList(
				// at least one upper-case character
				new CharacterRule(EnglishCharacterData.UpperCase, 1),

				// at least one lower-case character
				new CharacterRule(EnglishCharacterData.LowerCase, 1),

				// at least one digit character
				new CharacterRule(EnglishCharacterData.Digit, 1),

				//at lease one special character from list of chars
				new CharacterRule(new org.passay.CharacterData() {
					@Override
					public String getErrorCode() {
						return "INVALID_SPECIAL_CHARS";
					}

					@Override
					public String getCharacters() {
						return Util.SPECIALCHARACTERS;
					}
				}, 1)

		);


		PasswordGenerator generator = new PasswordGenerator();

		// Generated password is 12 characters long, which complies with policy
		String password = generator.generatePassword(12, rules);

		return password.trim();
	}



	public static void setResponseMessage(ServiceResponse serviceResponse,
			String newResponseMessage) {
		String oldResponseMessage = serviceResponse.getMessage();
		if (oldResponseMessage == null) {
			oldResponseMessage = "";
		}

		serviceResponse
				.setMessage(oldResponseMessage
						+ ((newResponseMessage.length() > 0) ? ("; " + newResponseMessage)
								: ""));

	}

	public static ServiceResponse getPasswordValidationResponse(String password, String password_history) {

		ServiceResponse serviceResponse = new ServiceResponse();

		try {
			List<String> validationMessages = PwdValidator.validatePassword(
					password, password_history);

			if (validationMessages.size() > 0
					&& validationMessages.get(0).equalsIgnoreCase(
							"Valid password")) {
				serviceResponse.setMessage("Valid password");
			} else {
				serviceResponse.setMessage("Invalid password");
				serviceResponse.setData(validationMessages);
			}
			serviceResponse.setIsSuccess(true);
		} catch (Exception e) {
			serviceResponse.setException(Util.getStackTraceMessage(e));
			serviceResponse.setIsSuccess(false);
		} finally {

		}

		return serviceResponse;
	}

	public static String getStackTraceMessage(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		e.printStackTrace(pw);

		return pw.toString();
	}

	public static Response getRestResponse(ServiceResponse serviceResponse) {
		String output;

		ObjectMapper objMapper = new ObjectMapper();
		try {
			output = objMapper.writeValueAsString(serviceResponse);
		} catch (Exception e) {
			output = "couldn't write response with jackson mapper";
		}

		return Response.status(200).entity(output).build();
	}

	public static String getUpdatedPasswordHistory(User_info user_info,String shaHash) {
		String password_history = user_info.getPassword_history();

		if (password_history != null && !password_history.
				equalsIgnoreCase("") && !password_history.
				equalsIgnoreCase("null")) {

			password_history = removeOldestPasswordIfNumberOfPasswordsGreaterThanLimit(password_history);

			password_history += ";" + shaHash;
		} else {
			password_history = shaHash;
		}

		return password_history;
	}

	/**
	 *
	 * @param password_history
	 */
	private static String removeOldestPasswordIfNumberOfPasswordsGreaterThanLimit(
			String password_history) {
		String[] arrayOfOldPasswords = password_history.split(";");
		List<String> listOfOldPasswords = convertArrayToList(arrayOfOldPasswords);

		while (listOfOldPasswords.size() >= 10) {
			listOfOldPasswords.remove(0);
		}

		password_history = convertToSemicolonSeparatedString(listOfOldPasswords);
		return password_history;
	}

	private static String convertToSemicolonSeparatedString(
			List<String> listOfString) {
		String semicolonSeparatedStrings = "";

		for (String eachString : listOfString) {
			semicolonSeparatedStrings += eachString + ";";
		}

		if (semicolonSeparatedStrings.endsWith(";"))
			semicolonSeparatedStrings = semicolonSeparatedStrings.substring(0,
					semicolonSeparatedStrings.length() - 1);

		return semicolonSeparatedStrings;
	}

	private static <T> List<T> convertArrayToList(T[] arrayOfOldPasswords) {
		List<T> listOfArrayElements = new ArrayList<T>();

		for (T eachArrayEntry : arrayOfOldPasswords) {
			listOfArrayElements.add(eachArrayEntry);
		}

		return listOfArrayElements;
	}

	public static Response getRestResponseForUserAuthentication(
			ServiceResponse serviceResponse, String clientIp) {

		if (serviceResponse.getData() != null) {
			User_info_dto user_info_dto = (User_info_dto) serviceResponse
					.getData();

			if (user_info_dto.getAccount_status_id() == 2) {
				String token = "";
				try {
					token = TokenUtils.getToken(user_info_dto, clientIp);
					serviceResponse.setToken(token);
				} catch (Exception e) {
					e.printStackTrace();
					serviceResponse.setException("couldn't write response with jackson mapper: " + e.getMessage());
				}
			}
		} else {

		}

		return getRestResponse(serviceResponse);
	}

	public static Response getRestResponse(ServiceResponse serviceResponse,
			boolean isAuthentication, String clientIp) {
		Response restResponse;

		if (isAuthentication && serviceResponse.getIsSuccess()) {
			User_info_dto user_info_dto = (User_info_dto) serviceResponse.getData();

			if(user_info_dto.getAccount_status_id() ==2){
				restResponse = getRestResponseForUserAuthentication(
						serviceResponse, clientIp);
			}else{
				restResponse = getRestResponse(serviceResponse);
			}
		} else {
			restResponse = getRestResponse(serviceResponse);
		}

		return restResponse;
	}

	/*http://stackoverflow.com/questions/24139097/resteasy-client-nosuchmethoderror*/
	public static <T> Object getResponseFromRestService(Object payLoad,
			boolean isOutputListType, Class<T> outputClassType,
			String relativePath, String httpMethod) {
		Object output = null;

		try {

			ResteasyClient client = new ResteasyClientBuilder().build();

			ResteasyWebTarget target = client.target(AuthParam.baseEPDSRestURL
					+ relativePath);

			Response response = null;
			if (httpMethod.equalsIgnoreCase("post")) {
				response = target.request()
						.header("Authorization", AUTHORIZATON_HEADER_PASS)
						.post(
						Entity.entity(payLoad, "application/json"));
			} else if (httpMethod.equalsIgnoreCase("put")) {
				response = target.request()
						.header("Authorization", AUTHORIZATON_HEADER_PASS)
						.put(Entity.entity(payLoad, "application/json"));
			} else if (httpMethod.equalsIgnoreCase("GET")) {
				response = target.request()
						.header("Authorization", AUTHORIZATON_HEADER_PASS)
						.get();
			}

			if ( response != null && response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			// System.out.println(response.readEntity(String.class));

			if (response != null){
				String jsonResponse = response.readEntity(String.class);
				System.out.println("Server response : \n" + jsonResponse);
				response.close();

				return convertToObject(jsonResponse, isOutputListType,
						outputClassType);
			}



		} catch (Exception e) {
			e.printStackTrace();
		}

		return output;
	}

	private static <T> Object convertToObject(String responseInJson,
			boolean isListType, Class<T> t) throws JsonParseException,
			JsonMappingException, IOException {
		Object obj = null;

		ObjectMapper mapper = new ObjectMapper();
		if (isListType) {
			obj = mapper.readValue(responseInJson,
					new TypeReference<List<User_info_epds>>() {
					});
		} else {
			obj = mapper.readValue(responseInJson, t);
		}

		return obj;
	}

	public static JsonNode getJSONNode(String response, String nodePath)
			throws JsonProcessingException, IOException {
		JsonNode jsonNode = new ObjectMapper().readTree(response);
		JsonNode message = jsonNode.path(nodePath);
		return message;
	}

	public static <T> List<T> getDesiredClassObjectList(List<?> list,
			Class<T> entityClass) {
		if (list == null)
			return new ArrayList<T>();

		List<T> toBeReturnedList = new ArrayList<T>();

		for (Object eachObj : list) {
			for (Object eachObj2 : (Object[]) eachObj) {
				if (eachObj2.getClass().equals(entityClass)) {
					toBeReturnedList.add(entityClass.cast(eachObj2));
				}
			}
		}

		return toBeReturnedList;
	}

	public static void fillUpQuestionField(
			List<User_security_answer> listOfUserSecurityAnswer,
			List<Security_question> listOfSecurityQuestion) {
		User_security_answer user_security_answer;
		Security_question security_question;

		for (int i = 0; i < listOfUserSecurityAnswer.size(); i++) {
			user_security_answer = listOfUserSecurityAnswer.get(i);
			security_question = listOfSecurityQuestion.get(i);

			user_security_answer.setSecurityQuestion(security_question
					.getSecurity_question());
		}
	}

	public static Map<Integer, String> convertToMapOfSecQIdToSecurityQuestion(
			List<User_security_answer> listOfUserSecurityAnswer) {
		Map<Integer, String> mapOfSecQIdToSecurityQuestion = new HashMap<Integer, String>();

		if (null != listOfUserSecurityAnswer && !listOfUserSecurityAnswer.isEmpty()){
			for (User_security_answer eachUserSecurityAnswer : listOfUserSecurityAnswer) {
				mapOfSecQIdToSecurityQuestion.put(
						eachUserSecurityAnswer.getSecurity_q_id(),
						eachUserSecurityAnswer.getSecurityQuestion());
			}

		}

		return mapOfSecQIdToSecurityQuestion;
	}

	public static User_info_dto getUserInfoDtoForLoginSuccess(
			User_info user_info, User_info_dao user_info_dao) {

		User_info_dto user_info_dto = new User_info_dto();

		user_info_dto.setUser_id(user_info.getUser_id());
		user_info_dto.setAccount_status_id(user_info.getAccount_status_id());


		user_info_dto.setEmail(user_info.getEmail());
		Rules_Of_Behavior rob = new Rules_Of_Behavior();
		rob  = user_info_dao.getRulesOfBehaviorTimeStamp(user_info.getEmail());


		if (
				user_info.getAccount_status_id() == 1
				||
				user_info.getAccount_status_id() == 2
				||
				user_info.getAccount_status_id() == 3
				||
				user_info.getAccount_status_id() == 5
				||
				user_info.getAccount_status_id() == 6
				||
				user_info.getAccount_status_id() == 7
				||
				user_info.getAccount_status_id() == 8

				){

			if(null != rob){
				Integer days = getDifferenceBetweenTwoDates(rob.getTime_stamp());

				if (days >= 365){
					user_info_dto.setIsROBRequired(true);
				}else{
					user_info_dto.setIsROBRequired(false);
				}

			}else{
				user_info_dto.setIsROBRequired(true);
			}
		}





		user_info_dto.setAuth_role_id(user_info.getRole_id());


		return user_info_dto;
	}

	public static boolean checkIfElementIsAlreadyInList(String element,
			List<String> listOfElements) {
		boolean returnValue = false;

		for (String eachEmail : listOfElements) {
			if (element.equalsIgnoreCase(eachEmail)) {
				returnValue = true;
				break;
			}
		}

		return returnValue;

	}




	public static Integer getDifferenceBetweenTwoDates(Timestamp timeStamp){

		Integer days  = null;
		try {
			DateTime dt1 = new DateTime(timeStamp);
			DateTime dt2 = new DateTime(new Date());
			days = Days.daysBetween(dt1, dt2).getDays();


		 } catch (Exception e) {
			e.printStackTrace();
		 }


		return days;
	}
}
