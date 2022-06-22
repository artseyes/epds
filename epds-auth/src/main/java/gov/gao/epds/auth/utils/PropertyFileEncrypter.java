/**
 * 
 */
package gov.gao.epds.auth.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Security;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimplePBEConfig;
import org.jasypt.properties.PropertyValueEncryptionUtils;

import gov.gao.epds.tokenutils.RemoveUnlimitedJCERestriction;

/**
 * This class is used to encrypt or decrypt property files.
 * @author MHussaini
 *
 */
public class PropertyFileEncrypter {

	
	public static StandardPBEStringEncryptor getEncrpytor(){
		
		Security.addProvider(new BouncyCastleProvider());
		RemoveUnlimitedJCERestriction.removeCryptographyRestrictions();
		
		SimplePBEConfig config = new SimplePBEConfig(); 
		config.setAlgorithm("PBEWithMD5AndTripleDES");
		config.setKeyObtentionIterations(1000);
		config.setPassword("JeXDER1J3gSJNjo");

		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setConfig(config);
		encryptor.initialize();
		
		
		return encryptor;
	}
	
	
	public static synchronized String encrypt(String value){
		
		String encodedValue = PropertyValueEncryptionUtils.encrypt(value, getEncrpytor());

		return encodedValue;
	}
	
	
	public static synchronized String decrypt(String encodedValue){
		
		String decodedValue = "";
		
		if (null != encodedValue){
			
			decodedValue = PropertyValueEncryptionUtils.decrypt(encodedValue, getEncrpytor());	
		}
		

		return decodedValue;
	}

	
	private static void encryptPropertyFile(String filePath){
		
		doCrypto(filePath,"encrypt");

	}


	/**
	 * @param filePath
	 * @param mode 
	 */
	private static void doCrypto(String filePath, String mode) {
		Properties prop = new Properties();
		InputStream input = null;
		FileOutputStream out = null;
		try {

			input = new FileInputStream(filePath);

			// load a properties file
			prop.load(input);
			input.close();
			 for(Entry<Object, Object> e : prop.entrySet()) {
				 if (mode.equalsIgnoreCase("encrypt")){
					 prop.setProperty(e.getKey().toString(), encrypt(e.getValue().toString()));
				 }else  if (mode.equalsIgnoreCase("decrypt")){
					 prop.setProperty(e.getKey().toString(), decrypt(e.getValue().toString()));
				 }
				 
		        }
			 
			 out = new FileOutputStream(filePath);
			 prop.store(out, null);

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			IOUtils.closeQuietly(out);
		}
	}	
	private static void decryptPropertyFile(String filePath){
		
		doCrypto(filePath,"decrypt");
		}
	
	
	public static void main(String[] args) {
		encryptPropertyFile("C:/Users/mhussaini/GAO/app-certs/keystore.properties");
	}
}
