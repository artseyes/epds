package gov.gao.epds.filters;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.client.RestTemplate;

import gov.gao.epds.persistence.entity.User_Info;
import gov.gao.epds.service.UserInfoService;
import gov.gao.epds.session.EpdsSession;
import gov.gao.epds.utils.ClientInfo;
import gov.gao.epds.utils.GlobalParams;
import gov.gao.epds.utils.PreAuthUtil;
import gov.gao.epds.utils.Util;

public class CustomPreAuthenticationFilter extends
		AbstractPreAuthenticatedProcessingFilter {

	private final RestTemplate restTemplate;
	private final static Logger logger = LoggerFactory
			.getLogger(CustomPreAuthenticationFilter.class);

	@Autowired
	public CustomPreAuthenticationFilter(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	@Autowired
	UserInfoService userInfoService;

	@Override
	protected Object getPreAuthenticatedCredentials(HttpServletRequest req) {

		return "";
	}

	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest req) {
		
		String tokenValue = PreAuthUtil.extractToken(req);
		String remoteIp = Util.getRemoteIp(req);


		boolean isTokenValid = false;
		boolean isSessionValid = false;
		String authenticationFailed = "";

		if (logger.isDebugEnabled()){
			logger.debug("Validating token for remoteIp={}, tokenValue ={}",
					remoteIp, tokenValue);
		}
		
		
		if (tokenValue != null && !tokenValue.equalsIgnoreCase("null")) {
			
			try {
				isTokenValid = PreAuthUtil.checIfTokenIsValid(restTemplate,remoteIp, tokenValue,req);
				isSessionValid = PreAuthUtil.validateSession(req, tokenValue);
				
			} catch (Exception e) {
				logger.error("Exception Occured when validating token Exception = {}, token ={}, IP= {}",e, tokenValue,Util.getRemoteIp(req));
				e.printStackTrace();
				
			}
		}

		if (isTokenValid && isSessionValid) {
			Integer userId  = (Integer) req.getAttribute("userId");
			String encodedUserId ="";
			try {
				User_Info  user_Info = userInfoService.getUserInfoByUsername(String.valueOf(userId));
				
				if (null != user_Info && user_Info.getRole_id().equals(7)  && !user_Info.getEmail().equalsIgnoreCase("epds@cbca.gov")
						&& !user_Info.getEmail().equalsIgnoreCase("scott.sylke@cbca.gov") && !user_Info.getEmail().equalsIgnoreCase("charity.barnett@cbca.gov")
						&& (!ClientInfo.getClientIpAddr(req).startsWith("159.142") && !ClientInfo.getClientIpAddr(req).contains("127.0.0.1"))){
					// check for bypass file so admin users can login from non-gao network when GAOs network denies access for whatever reason
					// does not bypass needing valid email and password!
                    // commenting it out for now because of security concerns - may be enabled later
//					File f = new File("/app/properties/gao_ip_bypass");
//					if(!f.exists()) {
						req.setAttribute("authenticationFailed", "unauthorized network login");
						return new PreAuthenticatedAuthenticationToken(null, null);
//					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			try {
				encodedUserId = Base64.getEncoder().encodeToString(String.valueOf(userId).getBytes("UTF-8"));
				
			} catch (UnsupportedEncodingException e1) {
				encodedUserId = String.valueOf(userId);
				e1.printStackTrace();
			}
			
			
			ClientInfo.printClientInfo(req,encodedUserId);
			logger.info("User for userId={}, requestUrl ={}, eventType={}", encodedUserId, req.getRequestURL(),"View");
			
			PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(
					req.getAttribute("userId"), tokenValue);
			
			
			Date lastActiveRequest = (Date) EpdsSession.getAttribute(req, "lastReqTimeStamp");
			
			
			if (PreAuthUtil.checkIfDifferenceBetweenLastActiveRequestExceedsThreshold(req, 20,lastActiveRequest)){
				
				logger.info("User for userId={}, requestUrl ={}, eventType={}", encodedUserId, req.getRequestURL(), "checkIfDifferenceBetweenLastActiveRequestExceedsThreshold");
				
				EpdsSession.endUserSession(req);
				req.setAttribute("authenticationFailed", "authentication object not found");
				
				return new PreAuthenticatedAuthenticationToken(null, null); 
				
				
			}
		
			EpdsSession.setAttribute(req, "lastReqTimeStamp", new Date());
			
			
			return authentication;
		}
		
		
		authenticationFailed = (String) req.getAttribute("authenticationFailed");
		if (authenticationFailed == null){
			req.setAttribute("authenticationFailed", "authentication object not found");
		}
		
		ClientInfo.printClientInfo(req,tokenValue);

		return new PreAuthenticatedAuthenticationToken(null, null);

	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException failed) {
		try {
			super.unsuccessfulAuthentication(request, response, failed);
		} catch (IOException | ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

}
