package gov.gao.epds.filestorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.gao.epds.dto.FileStorage;
import gov.gao.epds.utils.PropertyFileEncrypter;
import gov.gao.epds.utils.RemoveUnlimitedJCERestriction;

/**
 * A utility class that encrypts or decrypts a file.
 * 
 * @author MHussaini
 *
 */
public class FileStorageCryptoUtils {
	/* private static final String ALGORITHM = "AES"; */
	private static final String TRANSFORMATION = "AES";
	private final static String propFileName = "file-storage-keystore.properties";

	private static final Logger logger = LoggerFactory
			.getLogger(FileStorageEncryption.class);
	static {
		RemoveUnlimitedJCERestriction.removeCryptographyRestrictions();
	}

	private static Properties prop = new Properties();

	public static Key getSecurityKey() throws IOException {
			
		
		Security.addProvider(new BouncyCastleProvider());

		InputStream inputStream = Thread.currentThread()
				.getContextClassLoader().getResourceAsStream(propFileName);
		if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFileName
					+ "' not found in the classpath");
		}

		InputStream keystoreStream = Thread
				.currentThread()
				.getContextClassLoader()
				.getResourceAsStream(
						PropertyFileEncrypter.decrypt(prop
								.getProperty("keystore.location")));
		if (keystoreStream == null) {
			throw new FileNotFoundException("keystore cert '"
					+ PropertyFileEncrypter.decrypt(prop
							.getProperty("keystore.location"))
					+ " not found in the classpath");
		}

		KeyStore keystore = null;
		try {
			keystore = KeyStore.getInstance(PropertyFileEncrypter.decrypt(prop
					.getProperty("storetype")));
		} catch (KeyStoreException e3) {

			e3.printStackTrace();
		}
		try {
			keystore.load(keystoreStream,
					PropertyFileEncrypter
							.decrypt(prop.getProperty("storepass"))
							.toCharArray());
		} catch (CertificateException | IOException e2) {
			e2.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		try {
			if ( null != keystore && !keystore.containsAlias(PropertyFileEncrypter.decrypt(prop
					.getProperty("alias")))) {
				throw new RuntimeException("Alias for key not found");
			}
		} catch (KeyStoreException e2) {
			e2.printStackTrace();
		}
		Key key = null;
		try {
			
			if (keystore != null){
			
				key = keystore.getKey(PropertyFileEncrypter.decrypt(prop
						.getProperty("alias")),
						PropertyFileEncrypter.decrypt(prop.getProperty("keypass"))
								.toCharArray());
			}
			
		} catch (UnrecoverableKeyException | KeyStoreException e1) {
			e1.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return key;

	}
	
}