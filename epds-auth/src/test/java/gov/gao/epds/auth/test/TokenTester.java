package gov.gao.epds.auth.test;

import gov.gao.epds.auth.dto.ServiceResponse;
import gov.gao.epds.auth.persistence.entity.User_info;
import gov.gao.epds.tokenutils.TokenUtils;

import java.io.IOException;
import java.util.Date;

import com.nimbusds.jwt.JWTClaimsSet;

public class TokenTester {
	public static void main(String[] args) throws Exception {
		testTokenValidation();

	}

	private static void testTokenValidation() {
		String token = createToken();
		System.out.println(token);

	}

	private static String createToken() {
		String output = "";

		User_info user_info = new User_info();
		user_info.setAccount_status_id(1);
		user_info.setAddress1("123 Road");
		user_info.setCity("Fort Collins");
		user_info.setCountry("USA");

		ServiceResponse serviceResponse = new ServiceResponse();
		serviceResponse.setData(user_info);
		serviceResponse.setException("N/A");
		serviceResponse.setIsSuccess(true);
		serviceResponse.setMessage("");

		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
				.subject("authentication").issuer("epds-auth")
				.expirationTime(new Date(new Date().getTime() + 60 * 1000))
				.claim("serviceResponse", serviceResponse).build();

		try {
			output = TokenUtils.encryptJWT(claimsSet);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return output;
	}

}
