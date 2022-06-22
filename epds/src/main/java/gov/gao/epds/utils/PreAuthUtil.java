package gov.gao.epds.utils;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.SuccessCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import gov.gao.epds.dto.User_info_dto;
import gov.gao.epds.dto.ValidateToken;
import gov.gao.epds.rest.auth.services.AuthUtil;
import gov.gao.epds.session.EpdsSession;

public class PreAuthUtil {

	private final static Logger logger = LoggerFactory
			.getLogger(PreAuthUtil.class);

	public static boolean  checIfTokenIsValid(RestTemplate restTemplate,
			String remoteIp, String tokenValue, HttpServletRequest req)
			throws JsonProcessingException, IOException {
		boolean isValid = false;
		Integer userId = null;
		 AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();
		 String userIdToDateStr = EpdsSession.getUserIdByUniqueSessionId(extractCookieValue(req, "uniqueSessionId"));
		
		if (tokenValue != null){
			
			
			/* Asynchronously validate the token and while the request is processed and if the validation fails then just endUserSession
			 * */			
					ListenableFuture<ResponseEntity<String>> futureEntity = asyncRestTemplate
			            .exchange(PropertyFileEncrypter.decrypt(GlobalParams.prop.getProperty("validateTokenURI")), HttpMethod.POST, createBody(req,tokenValue, Util.getRemoteIp(req)),
			            		String.class);
			 
			 
			 SuccessCallback<ResponseEntity<String>> successCallback = new SuccessCallback<ResponseEntity<String>>() {
			        @Override
			        public void onSuccess(ResponseEntity<String> asyncCallbackResponse) {
			            System.out.println("asyncCallbackResponse : " + asyncCallbackResponse);
			            
	                    boolean isSuccess = false;
	                    String token = null;
	                    JsonNode user_info_dto_response;
						try {
							
							isSuccess = AuthUtil.getJsonNode(asyncCallbackResponse.getBody(), "isSuccess").asBoolean();
							user_info_dto_response = AuthUtil
									.getJsonNode(asyncCallbackResponse.getBody(), "data");
							
							User_info_dto userInfoDTO = (User_info_dto) Util.convertToObject(
		            				user_info_dto_response.toString(), false, User_info_dto.class);
							
							token = AuthUtil.getJsonNode(asyncCallbackResponse.getBody(), "token").asText();

							//if token validation fails remove end user session
							if (!isSuccess
									&& null != token
									&& EpdsSession.getUserIdFromSession(token) != null){
								EpdsSession.endUserSession(userInfoDTO.getUser_id());
							}
							
						} catch (JsonProcessingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	            		
	                
			        }
			    };
			    
			    
			    FailureCallback failureCallback = new FailureCallback() {
			        @Override
			        public void onFailure(Throwable throwable) {
			            System.out.println("???, exception message : " + throwable.getMessage());
			        }
			    };

		
			    futureEntity
			            .addCallback(successCallback,failureCallback);
			
			
			    
			    userId  = EpdsSession.getUserIdFromSession(tokenValue);
			    
			    
			    if (userId != null){
					isValid = true;
				}else if (userIdToDateStr != null){
			    	userId = Integer.valueOf(userIdToDateStr.split(":")[0]);
			    }
			    
			    req.setAttribute("userId",userId);
			
		}
		
		
		if(!isValid){
			req.setAttribute("authenticationFailed", "token invalid");
		}
		return isValid;
	}

	
	public static synchronized Boolean validateSession(HttpServletRequest req,  String tokenValue){
		
		Boolean isValid = true;
		String uniqueSessionId = (String) EpdsSession
				.getAttribute(req, "uniqueSessionId");
		String uniqueIdInCookie = extractCookieValue(req, "uniqueSessionId");
		
		if (uniqueSessionId == null){
			isValid = false;
			req.setAttribute("authenticationFailed", "authentication object not found");
		}else if ( null != uniqueIdInCookie && !uniqueIdInCookie.equals(uniqueSessionId)){
			
			req.setAttribute("authenticationFailed", "concurrentLogin");
			isValid = false;
		}
		
		
		return isValid;
	}
	private static synchronized boolean checkIfThisIsAConcurrentRequest(
			String tokenFromCurrentReq, HttpServletRequest req) {
		
		boolean isOldConcurrentSession = false;

		String tokenStoredInSession = (String) EpdsSession
				.getAttribute(req, "token");

		if (!tokenStoredInSession
				.equalsIgnoreCase(tokenFromCurrentReq)) {
			isOldConcurrentSession = true;
		
		}
		return isOldConcurrentSession;
	}

	/**
	 * Extract the access token from cookies.
	 * 
	 * @param request
	 *            The request.
	 * @return The token, or null if no authorization access token was supplied.
	 */

	public static String extractToken(HttpServletRequest request) {
		String token = null;
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().contains("access_token")) {
					token = cookie.getValue();
					if (token == null) {
						logger.warn("Token not found in cookies.");						
					}
				}
			}
		}
		return token;
	}
	
	
	private static String extractCookieValue(HttpServletRequest request, String cookieName) {
		
		String cookieValue = null;
		Cookie[] cookies = request.getCookies();
		
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().toLowerCase(Locale.ENGLISH)
						.contains(cookieName.toLowerCase(Locale.ENGLISH))) {
					cookieValue = cookie.getValue();
				}
			}
		}
		return cookieValue;
	}
	
	/**
	 * Utility method to check whether we need to refresh the token, from UI we get ping every minute but we will only update every 5 mins
	 * @param req
	 * @param lastActiveRequest 
	 * @return
	 */
	public static synchronized Boolean checkIfDifferenceBetweenLastActiveRequestExceedsThreshold(HttpServletRequest req, Integer threshold, Date lastActiveRequest){
		
		Boolean exceedsThreshold = false;

		Date currentTime = new Date();
		Duration duration = new Duration(new DateTime(lastActiveRequest),new DateTime(currentTime));
		Long differenceInMins  = duration.getStandardMinutes();
		
		if (Math.abs(differenceInMins) >= threshold){

			exceedsThreshold = true;
		}
		
		
		return exceedsThreshold;
	
	}
	
	
	
	public static Long getDiffInMins(Date dateToBeChecked){
		
		DateTime currentTime = new DateTime();
		DateTime timeInsideToken = new DateTime(dateToBeChecked);
		Duration duration = new Duration(timeInsideToken,currentTime);
		Long differenceInDays  = duration.getStandardDays();
		
		return differenceInDays;
	}
	
	public static HttpEntity createBody(HttpServletRequest httpServletRequest, String token, String remoteIp) {
		Integer userId = (Integer) httpServletRequest.getAttribute("userId");
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add("Content-Type", MediaType.APPLICATION_JSON);
		requestHeaders.add("Accept", MediaType.APPLICATION_JSON);
		requestHeaders.add("userId", String.valueOf(userId));
	    
	    
	    
		/*MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", MediaType.APPLICATION_JSON);
		headers.add("userId", String.valueOf(userId));*/
		
		   ValidateToken form = new ValidateToken();
		   form.setRemoteIp(remoteIp);
		   form.setToken(token);
		   
		 //set your entity to send
		   HttpEntity entity = new HttpEntity(form,requestHeaders);
		//HttpEntity<ValidateToken> request = new HttpEntity<ValidateToken>(form, headers);
       
        return entity;
    }
}
