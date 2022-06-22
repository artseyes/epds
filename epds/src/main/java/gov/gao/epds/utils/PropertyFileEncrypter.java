/**
 *
 */
package gov.gao.epds.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Security;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimplePBEConfig;
import org.jasypt.properties.PropertyValueEncryptionUtils;

/**
 * @author MHussaini
 *
 */
public class PropertyFileEncrypter {


	private static StandardPBEStringEncryptor getEncrpytor(){


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


	private static synchronized String encrypt(String value){

		String encodedValue = PropertyValueEncryptionUtils.encrypt(value, getEncrpytor());

		return encodedValue;
	}


	public static synchronized String decrypt(String encodedValue){
		String decodedValue = PropertyValueEncryptionUtils.decrypt(encodedValue, getEncrpytor());

		return decodedValue;
	}

	public static void main(String[] args) throws IOException {

		String decrpt = encrypt("/resources/epds_prod_cert.p12");
		System.out.println(decrpt);
       /* String encryptedValue = encrypt("https://10.102.107.11/EPDS_Web/payment-status");
        System.out.println(encryptedValue);*/

		//this helps in updating properties in a property file
		/*Properties properties = new Properties();
		try {
		  properties.load(new FileInputStream("C:/AmersFiles/Gitworkspace/epds/src/main/resources/epds.gaoepds0011.properties"));
		} catch (IOException e) {

		}*/


		encryptOrDecryptAllFileProps();

		/*String decryptedValue = decrypt("ENC(Fq+oHsDl6+1D09z/IXR4pOG0nkzVDWPt)");
		System.out.println(decryptedValue);*/

	}


	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void encryptOrDecryptAllFileProps() throws FileNotFoundException, IOException {
		String inputOutputPropFilePath = "epds/src/main/resources/epds.local.properties";
//		String inputOutputPropFilePath = "epds-auth/src/main/resources/epds.local.properties";
//		String inputOutputPropFilePath = "epds-auth/src/main/resources/keystore.properties";
//		String inputOutputPropFilePath = "epds/src/main/resources/file-storage-keystore.properties";



		FileInputStream fis = new FileInputStream(inputOutputPropFilePath);
		Properties props = new Properties();
		props.load(fis);
		fis.close();

		FileOutputStream out = new FileOutputStream(inputOutputPropFilePath);


		SortedMap sortedSystemProperties = new TreeMap(props);
		Set keySet = sortedSystemProperties.keySet();
		Iterator iterator = keySet.iterator();
		while (iterator.hasNext())
		{
			String key = (String) iterator.next();

			try {
				props.setProperty(key, encrypt(props.getProperty(key)));
//				props.setProperty(key, decrypt(props.getProperty(key)));
			} catch (Exception e){
				System.out.println("En/Decrypt failed with: " + e.toString() + " with " + key + " = " + props.getProperty(key));
			}

			System.out.println(key + " = " + props.getProperty(key));
		}

		props.store(out, null);
		out.close();
	}

}
