package gov.gao.epds.gctrack;

import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import gov.gao.epds.utils.GlobalParams;
import gov.gao.epds.utils.PropertyFileEncrypter;
import gov.gao.epds.utils.Util;

public class AuthenticationService {
	
	public static boolean authenticate(String authCredentials) throws Exception {
		String password = decrypt(authCredentials);

		if (password.equals(PropertyFileEncrypter.decrypt(GlobalParams.prop
				.getProperty("gcTrackAuthPwd")))) {
			return true;
		} else {
			return false;
		}
	}

	private static String decrypt(String encryptedValue) throws Exception {
		
		String keyValue = PropertyFileEncrypter.decrypt(GlobalParams.prop
				.getProperty("gcTrackDecryptPwd"));
		Key key = generateKey(keyValue);
		Cipher c = Cipher.getInstance("AES");
		c.init(Cipher.DECRYPT_MODE, key);
		byte[] decodedValue = Base64.getDecoder().decode(encryptedValue);
		byte[] decValue = c.doFinal(decodedValue);
		String decryptedValue = new String(decValue);
		return decryptedValue;
	}

	private static Key generateKey(String keyword) {
		Key key = new SecretKeySpec(keyword.getBytes(), "AES");
		return key;
	}

	public static boolean authenticateIpAddress(
			HttpServletRequest httpServletRequest) {
		String ip = Util.getRemoteIp(httpServletRequest);

		boolean isCorrectIp = false;
		// come back
		if (ip.equalsIgnoreCase(PropertyFileEncrypter.decrypt(GlobalParams.prop
				.getProperty("gcTrackIpAddress")))) {
			isCorrectIp = true;
		}

		return isCorrectIp;
	}
}
