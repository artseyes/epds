
package gov.gao.epds.tokenutils;

import gov.gao.epds.auth.dto.User_info_dto;
import gov.gao.epds.auth.service.TokenService;
import gov.gao.epds.auth.utils.DateUtil;
import gov.gao.epds.auth.utils.PolicyParam;
import gov.gao.epds.auth.utils.PropertyFileEncrypter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

import gov.gao.epds.auth.utils.Util;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

public class TokenUtils {
	
	private final static String ALGORITHM = "AES/GCM/NOPADDING";
	static {
		RemoveUnlimitedJCERestriction.removeCryptographyRestrictions();
	}
	public static Map<String, Date> tokenToLastActivityDateMap = new HashMap<String, Date>();

private static Properties prop = new Properties();

	public volatile static String decrypedTokenUserId ="";
	private final static String propFileName = "keystore.properties";

	private static final Logger logger = LoggerFactory.getLogger(TokenUtils.class);
	protected static Key getSecurityKey() throws IOException {
		Security.addProvider(new BouncyCastleProvider());

		InputStream inputStream = Thread.currentThread()
				.getContextClassLoader().getResourceAsStream(propFileName);
		if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFileName
					+ "' not found in the classpath");
		}
		InputStream keystoreStream = Thread.currentThread()
				.getContextClassLoader().getResourceAsStream(PropertyFileEncrypter.decrypt(prop.getProperty("keystore.location")));
		if (keystoreStream == null) {
			throw new FileNotFoundException("keystore cert '" + PropertyFileEncrypter.decrypt(prop.getProperty("keystore.location"))
					+ "' not found in the classpath");
		} 

		KeyStore keystore = null;
		try {
			keystore = KeyStore.getInstance(PropertyFileEncrypter.decrypt(prop.getProperty("storetype")));
		} catch (KeyStoreException e3) {

			e3.printStackTrace();
		}
		try {
			if (keystore != null){
			
				keystore.load(keystoreStream, PropertyFileEncrypter.decrypt(prop.getProperty("storepass"))
						.toCharArray());
			}
			
		} catch (CertificateException | IOException e2) {
			e2.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		try {
			if (keystore != null && !keystore.containsAlias(PropertyFileEncrypter.decrypt(prop.getProperty("alias")))) {
				throw new RuntimeException("Alias for key not found");
			}
		} catch (KeyStoreException e2) {
			e2.printStackTrace();
		}
		Key key = null;
		try {
			if (keystore != null ){
				
				key = keystore.getKey(PropertyFileEncrypter.decrypt(prop.getProperty("alias")),
						PropertyFileEncrypter.decrypt(prop.getProperty("keypass")).toCharArray());
			}
		} catch (UnrecoverableKeyException | KeyStoreException e1) {
			e1.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return key;

	}

	public static synchronized JWEObject decryptJWEObject(String encryptedJWE) throws IOException {
		
		Key key = getSecurityKey();
		// Parse the JWE string
		JWEObject jweObject = null;
		try {
			jweObject = JWEObject.parse(encryptedJWE);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(ALGORITHM, "BC");
		} catch (NoSuchAlgorithmException | NoSuchProviderException
				| NoSuchPaddingException e2) {
			e2.printStackTrace();
		}

		try {
			if(key !=null){
				cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(jweObject
						.getIV().decode()));	
			}else{
				throw new InvalidKeyException("Key is null");
			}
			
		} catch (InvalidKeyException e1) {

			e1.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {

			e.printStackTrace();
		}

		// Decrypt with shared key
		try {
			if (key != null && jweObject != null){
			
				jweObject.decrypt(new DirectDecrypter(key.getEncoded()));
			}
			
		} catch (JOSEException e) {
			e.printStackTrace();
		}

		return jweObject;
	}

	//  verify JSON WEB ENCRYPTED Object
	public static synchronized SignedJWT verifyJWE(JWEObject jweObject) throws IOException {

		Key key  = getSecurityKey();
		// Extract payload
		SignedJWT signedJWT = jweObject.getPayload().toSignedJWT();

		Assert.notNull(signedJWT, "Payload Not signed");

		// Check the HMAC
		try {
			Assert.isTrue(signedJWT.verify(new MACVerifier(key.getEncoded())));
		} catch (JOSEException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return signedJWT;

	}

	// encrypt Json web token
	public static synchronized String encryptJWT(JWTClaimsSet claimsSet) throws IOException {
		Key key = getSecurityKey();
		byte[] iv = getIV();
		String jweString = null;
		Cipher cipher;
		try {
			cipher = Cipher.getInstance(ALGORITHM, "BC");
			cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
			SignedJWT signedJWT = getSignedJWT(key, claimsSet);

			// Perform encryption
			JWEObject jweObject = createJWEObjectFromSignedJWT(iv, signedJWT);
			jweObject.encrypt(new DirectEncrypter(key.getEncoded()));

			jweString = jweObject.serialize();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			logger.error("encryptJWT exception: " + Util.getStackTraceMessage(e));
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			logger.error("encryptJWT exception: " + Util.getStackTraceMessage(e));
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			logger.error("encryptJWT exception: " + Util.getStackTraceMessage(e));
		} catch (KeyLengthException e) {
			e.printStackTrace();
			logger.error("encryptJWT exception: " + Util.getStackTraceMessage(e));
		} catch (JOSEException e) {
			e.printStackTrace();
			logger.error("encryptJWT exception: " + Util.getStackTraceMessage(e));
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			logger.error("encryptJWT exception: " + Util.getStackTraceMessage(e));
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			logger.error("encryptJWT exception: " + Util.getStackTraceMessage(e));
		}
		return jweString;
	}

	// return initialization vector
	public static byte[] getIV() {
		final int AES_KEYLENGTH = 256;
		byte[] iv = new byte[AES_KEYLENGTH / 8];
		SecureRandom prng = new SecureRandom();
		prng.nextBytes(iv);

		return iv;
	}

	public static synchronized SignedJWT getSignedJWT(Key key, JWTClaimsSet claimsSet) {

		// Create HMAC signer
		JWSSigner signer = null;
		try {
			signer = new MACSigner(key.getEncoded());
		} catch (KeyLengthException e) {

			e.printStackTrace();
		}

		SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256),claimsSet);
		// Apply the HMAC
		try {
			signedJWT.sign(signer);
		} catch (JOSEException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		return signedJWT;
	}

	// Create JWE object with signed JWT as payload
	public static JWEObject createJWEObjectFromSignedJWT(byte[] iv,
			SignedJWT signedJWT) {

		JWEObject jweObject = new JWEObject(new JWEHeader.Builder(
				JWEAlgorithm.DIR, EncryptionMethod.A256GCM)
				 .contentType("JWT")
				.iv(Base64URL.encode(iv)).build(), new Payload(signedJWT));

		return jweObject;
	}


	
	public static boolean findIfTokenIsExpired(JWTClaimsSet claimSet,
			String token) {
		
		return DateUtil.checkIfTheDateCrossedThreshold(claimSet.getExpirationTime(),0);
	}

	public static boolean findIfTokenIsRenewable(JWTClaimsSet claimSet) {
		
		/*Date lastActivityDate = TokenUtils.tokenToLastActivityDateMap.get("");
		return DateUtil.checkIfTheDateCrossedThreshold(lastActivityDate,
				PolicyParam.timeToRenewTokenInMins);*/
		
		DateTime currentTime = new DateTime();
		DateTime timeInsideToken = new DateTime(claimSet.getIssueTime());
		Duration duration = new Duration(timeInsideToken, currentTime);
		Long differenceInMins  = duration.getStandardMinutes();
		
		return differenceInMins >= PolicyParam.timeToRenewTokenInMins;
	}

	public static String getToken(User_info_dto user_info_dto, String clientIp)
			throws IOException {
		// Prepare JWT with claims set
		
		ObjectMapper om = new ObjectMapper();
	
		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
				.subject("authentication")
				.issuer("epds-auth")
				.claim("user_info", om.writerWithDefaultPrettyPrinter().writeValueAsString(user_info_dto))
				.claim("clientIp", clientIp)
				.claim("createdDate", new Date())
				.expirationTime(new Date(new Date().getTime() + 1000 * 60 * PolicyParam.maxUserInactivityTimeInMins))//15 mins.
				.issueTime(new Date()).build();

		// Serialise to JWE compact form
		return encryptJWT(claimsSet);
	}

	public static boolean validateIpAddress(JWTClaimsSet claimSet,
			String clientIp) {
		
		logger.info("Validating token for remoteIp={}, IpAddressInsideToken ={}",clientIp, claimSet.getClaim("clientIp").toString());
		String originalClientIp =  claimSet.getClaim("clientIp").toString();
		return clientIp.equalsIgnoreCase(originalClientIp);
	}
	

	public static String getNewToken(User_info_dto usre_info_dto, String clientIp,
			String token) throws IOException {
		String newToken = getToken(usre_info_dto, clientIp);
		tokenToLastActivityDateMap.remove(token);

		return newToken;
	}

	
	public static User_info_dto getUserInfoDto(org.codehaus.jackson.JsonNode serviceResponse) throws JsonMappingException {

		ObjectMapper om = new ObjectMapper();
		User_info_dto userInfoDto = null;
		try {
			userInfoDto = om.readValue(serviceResponse.path("data").toString(),
					User_info_dto.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return userInfoDto;
	}
}

/*
 * https://www.owasp.org/index.php/Using_the_Java_Cryptographic_Extensions
 */
