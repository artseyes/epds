package gov.gao.epds.auth.service;

import gov.gao.epds.auth.dto.ServiceResponse;
import gov.gao.epds.auth.dto.User_info_dto;
import gov.gao.epds.auth.utils.Util;
import gov.gao.epds.tokenutils.TokenUtils;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JWEObject;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Service
public class TokenService {

	private static final Logger logger = LoggerFactory.getLogger(TokenService.class);
	
	public static synchronized ServiceResponse validateToken(String token, String clientIp) {
		
		ServiceResponse serviceResponse = new ServiceResponse();
			if (logger.isDebugEnabled()){
				logger.debug("Validating token for remoteIp={}",clientIp);
			}
			
		try {
			
			JWEObject jweObject = TokenUtils.decryptJWEObject(token);
			SignedJWT signedJWT = TokenUtils.verifyJWE(jweObject);

			if (signedJWT != null) {
				
				JWTClaimsSet claimSet = signedJWT.getJWTClaimsSet();
				
				//we cannot really do IP validation because this causes problem
				/*boolean isIpAddressValid =  true;*//*TokenUtils.validateIpAddress(
						claimSet, clientIp);*/
				
				User_info_dto user_info_dto = getUserInfoDto(claimSet.getClaims()
						.get("user_info").toString());
				
				TokenUtils.decrypedTokenUserId = String.valueOf(user_info_dto.getUser_id());
				
				/*if (isIpAddressValid) {} else {
					serviceResponse.setMessage("Ip address not valid");
					serviceResponse.setSuccess(false);
				}*/
				

				boolean isTokenExpired = TokenUtils
						.findIfTokenIsExpired(claimSet, token);

				if (!isTokenExpired) {
					

					/*boolean isTokenRenewable = TokenUtils
							.findIfTokenIsRenewable(claimSet);

					if (isTokenRenewable) {
						String newToken = TokenUtils.getNewToken(user_info_dto,
								clientIp, token);

						token = newToken;
						serviceResponse.setMessage("Token renewed");
						serviceResponse.setSuccess(true);
					}*/

					serviceResponse.setToken(token);
					serviceResponse.setData(user_info_dto);
					serviceResponse.setSuccess(true);
				} else {
					logger.warn("token is expired userId ={}", user_info_dto.getUser_id());
					serviceResponse.setMessage("Token Expired");
					serviceResponse.setSuccess(false);
				}
			

			} else {
				logger.warn("Invalid Token");
				serviceResponse.setToken("Invalid Token");
				serviceResponse.setSuccess(false);
			}
		} catch (Exception e) {
			logger.error("Invalid Token ={}", e);
			serviceResponse.setSuccess(false);
			serviceResponse.setStackTraceDetail(Util.getStackTraceMessage(e));
			e.printStackTrace();
		}

		return serviceResponse;
	}

	
	public static synchronized ServiceResponse refreshToken (String token,String clientIp) {
		ServiceResponse serviceResponse = new ServiceResponse();

		try {
			JWEObject jweObject = TokenUtils.decryptJWEObject(token);
			SignedJWT signedJWT = TokenUtils.verifyJWE(jweObject);

			if (signedJWT != null) {
				JWTClaimsSet claimSet = signedJWT.getJWTClaimsSet();


				User_info_dto user_info_dto = getUserInfoDto(claimSet.getClaims().get("user_info").toString());


					boolean isTokenRenewable = TokenUtils.findIfTokenIsRenewable(claimSet);
			
					if (isTokenRenewable) {
					String newToken = TokenUtils.getNewToken(user_info_dto,
								clientIp, token);
			
						//token = newToken;
						serviceResponse.setMessage("Token renewed");
						serviceResponse.setToken(newToken);
						serviceResponse.setData(user_info_dto);
						serviceResponse.setSuccess(true);
					}else{
						serviceResponse.setToken(token);
						serviceResponse.setData(user_info_dto);
						serviceResponse.setSuccess(false);
					}
				
				/*String newToken = TokenUtils.getNewToken(user_info_dto,
						clientIp, token);

				serviceResponse.setMessage("Token renewed");
				serviceResponse.setToken(newToken);
				serviceResponse.setData(user_info_dto);
				serviceResponse.setSuccess(true);*/
			

			} else {
				serviceResponse.setToken("Invalid Token");
				serviceResponse.setSuccess(false);
			}
		} catch (Exception e) {
			serviceResponse.setSuccess(false);
			serviceResponse.setStackTraceDetail(Util.getStackTraceMessage(e));
			e.printStackTrace();
		}

		return serviceResponse;
	}
	public static User_info_dto getUserInfoDto(String data) {

		ObjectMapper om = new ObjectMapper();
		User_info_dto userInfoDto = null;
		try {
			userInfoDto = om.readValue(data,
					User_info_dto.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return userInfoDto;
	}
}
