package gov.gao.epds.rest.auth.services;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.ws.rs.core.MediaType;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.gao.epds.dto.InputValidationError;
import gov.gao.epds.dto.User_info_dto;
import gov.gao.epds.enums.UserRoles;
import static gov.gao.epds.enums.UserRoles.*;
import gov.gao.epds.session.EpdsSession;
import gov.gao.epds.utils.GlobalParams;
import gov.gao.epds.utils.PropertyFileEncrypter;
import gov.gao.epds.utils.SessionIdentifierGenerator;


@Component
public class AuthUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthUtil.class);
	
	private static Properties prop = GlobalParams.prop;
	
	private RestTemplate restTemplate;
	
	@Autowired
	public AuthUtil(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	

	public static String convertToUIMessage(String authMessage) { // NO_UCD (use default)
		String returnValue = "";

		if (authMessage == null) {
			returnValue = "NULL";
		} else if (authMessage.equalsIgnoreCase("Email doesn't exist")
				|| authMessage.equalsIgnoreCase("Wrong password")) {
			returnValue = "User Id / Password doesn't match";
		} else if (authMessage.equalsIgnoreCase("STATUS: TEMPORARY")
				|| authMessage.equalsIgnoreCase("STATUS: NEW")
				|| authMessage.equalsIgnoreCase("STATUS: LOCKED")) {
			returnValue = "STATUS: TEMPORARY";
		} else {
			returnValue = authMessage;
		}

		return returnValue;
	}

	public String getAuthJSONResponse(String requestURI,
			String pathParam, User_info_dto user_info_dto,HttpServletRequest httpServletRequest) {
		
		Integer userId = (Integer) httpServletRequest.getAttribute("userId");
		String uri = PropertyFileEncrypter.decrypt(prop.getProperty(requestURI));
		
		//this is track userId for the request being sent.
		logger.info("epds-auth req url ={}, userId={}",uri + pathParam,userId);
		
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add("Content-Type", MediaType.APPLICATION_JSON);
		headers.add("Accept", MediaType.APPLICATION_JSON);
		headers.add("userId", String.valueOf(userId)) ;
		
		restTemplate.getMessageConverters().add(
				new MappingJackson2HttpMessageConverter());
		HttpEntity<User_info_dto> request = new HttpEntity<User_info_dto>(
				user_info_dto, headers);
		
		String jsonResponse = "";
		if (pathParam != null && !pathParam.equalsIgnoreCase("") && uri != null){
			 jsonResponse = restTemplate
					.postForObject(uri + pathParam,
							request, String.class);

		}else if (uri != null){
			jsonResponse = restTemplate
					.postForObject(uri,
							request, String.class);
		}

		return jsonResponse;
	}
	

	public static JsonNode getJsonNode(String response, String nodePath)
			throws JsonProcessingException, IOException {
		
		String validResponse =  null;
		
		if (response != null){
			validResponse = response;
		}
		
		JsonNode jsonNode = new ObjectMapper().readTree(validResponse);
		JsonNode message = jsonNode.path(nodePath);
		return message;
	}


	public static Object convertToPOJO(JsonNode dataJsonNode, Class<?> pojoClass) {
		Object pojo = null;
		ObjectMapper jsonObjectMapper = new ObjectMapper();

		try {
			pojo = jsonObjectMapper.treeToValue(dataJsonNode, pojoClass);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return pojo;
	}

	public String getForObject(String uri, String pathVariable,
			Class<String> responseClass) {
		
		HttpClient httpClient = HttpClientBuilder.create().build();
		ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		
		restTemplate = new RestTemplate(requestFactory);
		String targetURI = PropertyFileEncrypter.decrypt(prop.getProperty(uri)) + pathVariable;
		return restTemplate.getForObject(targetURI, responseClass);
	}

	public static Integer getEpdsAuthRoleId(UserRoles epdsRoleId) {
		int epdsAuthRoleId;

		if (epdsRoleId == AGENCY_ADMIN || epdsRoleId == AGENCY_ATTORNEY) {
			epdsAuthRoleId = 2;
		} else if (epdsRoleId == GAO_ATTORNEY || epdsRoleId == GAO_ADMIN || epdsRoleId == GAO_SUPERVISOR) {
			epdsAuthRoleId = 3;
		} else {
			epdsAuthRoleId = 1;
		}

		return epdsAuthRoleId;
	}

	public static void populateSecQIdToAnswerMap(User_info_dto user_info_dto) {

		try {
			Map<Integer, String> secQIdToAnswerMap = new HashMap<Integer, String>();

			secQIdToAnswerMap.put(
					Integer.valueOf(user_info_dto.getSeqQue1Id()),
					user_info_dto.getAnswer1());
			secQIdToAnswerMap.put(
					Integer.valueOf(user_info_dto.getSeqQue2Id()),
					user_info_dto.getAnswer2());
			secQIdToAnswerMap.put(
					Integer.valueOf(user_info_dto.getSeqQue3Id()),
					user_info_dto.getAnswer3());

			user_info_dto.setSecQIdToAnswerMap(secQIdToAnswerMap);
		} catch (Exception e) {

		}

	}

	public static void setAuthorizationToken(HttpServletResponse response, // NO_UCD (use default)
			String token, HttpServletRequest req) throws UnsupportedEncodingException {

		Integer userId = (Integer) req.getAttribute("userId");
		/*String encodedUserId =  Base64.getUrlEncoder().encodeToString(String.valueOf(userId).getBytes("UTF-8")) ;*/
		EpdsSession.setAttribute(req, "token", token);
		/* "Authorization: Bearer " + */

		Cookie tokenCookie = new Cookie("access_token", token);
		tokenCookie.setHttpOnly(true);
		//tokenCookie.setMaxAge(60*60);
		
		if(req.isSecure()){
			tokenCookie.setSecure(true);	
		}
		
		tokenCookie.setPath("/epds");
		response.addCookie(tokenCookie);
		
	}

	
	public static void setUniqueSessionId(HttpServletResponse response,HttpServletRequest req) {

		SessionIdentifierGenerator uniqueSessionIdentifierGenerator = new SessionIdentifierGenerator();
		String uniqueSessionId = uniqueSessionIdentifierGenerator.nextSessionId();

		EpdsSession.setAttribute(req, "uniqueSessionId", uniqueSessionId);
		EpdsSession.setUniqueSessionIdToUserIdMap(req,uniqueSessionId);
		Cookie sessionCookie = new Cookie("uniqueSessionId", uniqueSessionId);
		sessionCookie.setHttpOnly(true);
		//sessionCookie.setMaxAge(-1);
		
		if(req.isSecure()){
			sessionCookie.setSecure(true);
		}
		sessionCookie.setPath("/epds");
		response.addCookie(sessionCookie);	

	}

	public static void setEPDSRoleAndRoleId(User_info_dto user_info_dto) {
		String role = user_info_dto.getRole();
		int epds_role_id = GAO_ATTORNEY.getCode();
		if (role.equalsIgnoreCase("SUPERVISOR")) {
			epds_role_id = GAO_SUPERVISOR.getCode();
		} else if (role.equalsIgnoreCase("ADMIN")) {
			epds_role_id = GAO_ADMIN.getCode();
		}
		user_info_dto.setRole(role);
		user_info_dto.setEpds_role_id(epds_role_id);

	}
	
	
	public static <T> List<InputValidationError> validateDTO(T dto) {
		
		
		ObjectMapper om = new ObjectMapper();
		ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
		Validator validator = vf.getValidator();
		List<InputValidationError> errors = new ArrayList<InputValidationError>();
		
		
		Set<ConstraintViolation<T>> constraintViolations = validator
		        .validate(dto);
		
		for (ConstraintViolation<?> cv : constraintViolations) {
		     
		      InputValidationError error  = new InputValidationError();
		      error.setFieldName(cv.getPropertyPath().toString());
		      error.setInvalidValue(cv.getInvalidValue());
		      error.setMessage(cv.getMessage());
		      
		      errors.add(error);
		}
		
		if (!constraintViolations.isEmpty() && null != constraintViolations){
			
			try {
				logger.info("ConstraintViolations={}", om.writeValueAsString(errors));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		
		return errors;
	}

	

}
