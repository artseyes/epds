package gov.gao.epds.filestorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.Security;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import gov.gao.epds.utils.EPDS_FileUtils;
import gov.gao.epds.utils.fileencryption.Encryptor;
/*import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;*/


/**
 * Please refer as to why we are using SHA1PRNG 
 * http://stackoverflow.com/questions/12731283/using-securerandom-with-sha-256
 * 
 * 
 * 
 * http://stackoverflow.com/questions/11174851/how-to-use-zip4j-to-extract-an-zip-file-with-password-protection
 * 
 * @author MHussaini
 *
 */
public class FileStorageEncryption {
	
	// These constants may be changed without breaking existing hashes.
	public static final int SALT_BYTE_SIZE = 32;
	public static final int HASH_BYTE_SIZE = 256;
	public static final int PBKDF2_ITERATIONS = 64000;
	
	public static final  String algorithm = "AES/CTR/NoPadding";
	
	private static int bufferSize = 65536;
		
	
	public static void main(String[] args) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		Key key = FileStorageCryptoUtils.getSecurityKey();
		// get base64 encoded version of the key
		String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
		
		File file = new File("C:/Users/mhussaini/Downloads/Large_Test_File.pdf");
		/*String str = FileUtils.readFileToString(file, StandardCharsets.UTF_8);*/
		
		byte [] strByte = FileUtils.readFileToByteArray(file);
		
		/*encrypt(strByte,encodedKey,file);*/
		
		//FileOutputStream fos = new FileOutputStream(file);
		/*IOUtils.write(encryptedValue, fos,StandardCharsets.UTF_8);
		System.out.println(encryptedValue);
		fos.close();*/
		
		
		// decode the base64 encoded string
		byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
		// rebuild key using SecretKeySpec
		Key originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 
		
		encodedKey = Base64.getEncoder().encodeToString(originalKey.getEncoded());
		
		/*String encrypted = FileUtils.readFileToString(file, StandardCharsets.UTF_8);*/
		
		FileInputStream fis = new FileInputStream(file);
		
		byte [] encryptedBytes = IOUtils.toByteArray(new Base64InputStream(fis));
		
		//String encryptedString = new String(encryptedBytes,StandardCharsets.UTF_8);
		/*byte [] decryptedbytes = decryptBytes(encryptedBytes,encodedKey);*/
		
		FileOutputStream fos2 = new FileOutputStream(file);
		/*IOUtils.write(decryptedbytes, fos2);*/
		fos2.close();
		
				
	}
	
	
	
	/**
	 * 
	 * 
	 * @param file :Input file that needs to be encrypted
	 * @return 
	 */
	public static synchronized boolean encryptFile(File originalFile){
		
		if (!originalFile.exists()){
			return false;
		}
		
		Security.addProvider(new BouncyCastleProvider());
		Boolean isSuccess = false;
		File tempFile = new File(originalFile.getPath() + ".temp" );
		Key key;
		try {
			key = FileStorageCryptoUtils.getSecurityKey();
			Encryptor encryptor = new Encryptor(key, algorithm, 16);
			EPDS_FileUtils.checkIfTempFileExists(tempFile);
			encrypt(originalFile, tempFile,encryptor);
			EPDS_FileUtils.renameTempFile(originalFile, tempFile);
			isSuccess = true;
		}catch (Exception e) {
			isSuccess = false;
			e.printStackTrace();
		}finally{
			
		}
		
		return isSuccess;
	}
	
	/**
	 * 
	 * 
	 * @param file :Input file that needs to be encrypted
	 * @return 
	 */
	public boolean encryptFile(File originalFile,Key key){
		
		Security.addProvider(new BouncyCastleProvider());
		Boolean isSuccess = false;
		File tempFile = new File(originalFile.getPath() + ".temp" );
		try {
			Encryptor encryptor = new Encryptor(key, algorithm, 16);
			EPDS_FileUtils.checkIfTempFileExists(tempFile);
			encrypt(originalFile, tempFile,encryptor);
			EPDS_FileUtils.renameTempFile(originalFile, tempFile);
			isSuccess = true;
		}catch (Exception e) {
			isSuccess = false;
			e.printStackTrace();
		}finally{
			
		}
		
		return isSuccess;
	}
	
	
	/**
	 * @param originalFile
	 * @param secretKey
	 * @throws GeneralSecurityException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public synchronized void decrypt(InputStream fis, OutputStream outputStream, Key secretKey)
			throws GeneralSecurityException, IOException, FileNotFoundException {
		
		Encryptor encryptor = new Encryptor(secretKey, algorithm, 16);

		InputStream is = null;
	

		try {
		  is = encryptor.wrapInputStream(fis);
		  byte[] buffer = new byte[4096];
		  int nRead;
		  while((nRead = is.read(buffer)) != -1) {
		    outputStream.write(buffer, 0, nRead);
		  }
		  outputStream.flush();
		} finally {
		  if(is != null) {
		    is.close();
		  }
		  
		}
	}
	
	
	public static synchronized byte[] deriveKey(String p, byte[] s, int l) throws Exception {
		  PBEKeySpec ks = new PBEKeySpec(p.toCharArray(), s, PBKDF2_ITERATIONS, l);
		  SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		  return skf.generateSecret(ks).getEncoded();
		}
	
	
	
	
	/**
	 * <p>Reads and encrypts file <code>src</code> and writes the encrypted result to file <code>dest</code>.</p>
	 * @param src
	 * @param dst
	 * @throws FileNotFoundException
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public static void encrypt(File src, File dest, Encryptor encryptor) throws FileNotFoundException, GeneralSecurityException, IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(src);
			os = encryptor.wrapOutputStream(new FileOutputStream(dest));
			copy(is, os);
		} finally {
			if(is != null) {
				is.close();
			}
			if(os != null) {
				os.close();
			}
		}
	}
	
	/**
	 * <p>Reads and decrypts file <code>src</code> and writes the decrypted result to file <code>dest</code>.</p>
	 * @param src
	 * @param dest
	 * @throws FileNotFoundException
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public void decrypt(File src, File dest,Encryptor encryptor) throws FileNotFoundException, GeneralSecurityException, IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = encryptor.wrapInputStream(new FileInputStream(src));
			os = new FileOutputStream(dest);
			copy(is, os);
		} finally {
			if(is != null) {
				is.close();
			}
			if(os != null) {
				os.close();
			}
		}
	}
	
	/**
	 * <p>Reads and decrypts file <code>src</code> and writes the decrypted result to file <code>output stream</code>.</p>
	 * @param src
	 * @param dest
	 * @throws FileNotFoundException
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public void decrypt(File src, OutputStream os,Encryptor encryptor) throws FileNotFoundException, GeneralSecurityException, IOException {
		InputStream is = null;
		try {
			is = encryptor.wrapInputStream(new FileInputStream(src));
			copy(is, os);
		} finally {
			if(is != null) {
				is.close();
			}
			if(os != null) {
				os.close();
			}
		}
	}
	
	/**
	 * <p>Reads data from the <code>InputStream</code> and writes it to the <code>OutputStream</code>.</p>
	 * @param is
	 * @param os
	 * @throws IOException
	 */
	private static void copy(InputStream is, OutputStream os) throws IOException {
		byte[] buffer = new byte[bufferSize];
		int nRead;
		while((nRead = is.read(buffer)) != -1) {
			os.write(buffer, 0, nRead);
		}
		os.flush();
	}
	
	
}
