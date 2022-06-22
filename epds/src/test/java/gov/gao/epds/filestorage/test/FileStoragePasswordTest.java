package gov.gao.epds.filestorage.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import gov.gao.epds.filestorage.FileStorageCryptoUtils;
import gov.gao.epds.filestorage.FileStorageEncryption;

public class FileStoragePasswordTest {

	public static void main(String[] args) throws Exception {
		
		FileStorageEncryption fileStorageEncryption = new FileStorageEncryption();
		File encryptedFile = new File("C:/Users/mhussaini/GAO/encrypdecrypttest/testing.txt");
		File decryptedFile = new File("C:/Users/mhussaini/GAO/encrypdecrypttest/testing1.txt");
		
		if (!decryptedFile.exists()){
			decryptedFile.createNewFile();
		}
		
		OutputStream fileOutputStream = new FileOutputStream(decryptedFile);
		Key key = FileStorageCryptoUtils.getSecurityKey();
		System.out.println(key);
		// get base64 encoded version of the key
		String orignalPassword = Base64.getEncoder().encodeToString(key.getEncoded());
		
		System.out.println("ENCODED KEY --------->" + orignalPassword);
		try {
		/*for (int i=0; i<10;i++){}*/
			

			
			//fileStorageEncryption.encryptFile(encryptedFile,key);
			fileStorageEncryption.decrypt(new FileInputStream(encryptedFile), fileOutputStream, key);
		
		
		
		}finally {
		    if (fileOutputStream != null) {
		    	fileOutputStream.close();
		    	fileOutputStream.flush();
		    }
		}
		

	}

	
	public static String generate(String p) throws Exception {
		  // Generate 160 bit Salt using SHA-1 PRNG
		  SecureRandom r = SecureRandom.getInstance("SHA1PRNG");
		  byte[] salt = new byte[48]; r.nextBytes(salt);
		 
		  // Generate 384 bit Password Hash using PBKDF2 (100,000 Iterations)
		  byte[] hash = deriveKey(p, salt, 100000, 384);
		  
		  // Construct Output as "SALT + HASH"
		  byte[] os = new byte[48 + 48];
		  System.arraycopy(salt, 0, os, 0, 48);
		  System.out.println(os.length);
		  System.arraycopy(hash, 0, os, 48, 48);
		  System.out.println(os.length);
		 
		  // Return a Base64 Encoded String
		  return new String(Base64.getEncoder().encodeToString(os));
		}
	
	public static boolean authenticate(String p, String h) throws Exception {
		  // Recover our Byte Array by Base64 Decoding
		  byte[] os = Base64.getDecoder().decode(h);
		 
		  // Check Length (SALT (48) + HASH (48))
		  if (os.length == 96) {
		    // Recover Elements from String
		    byte[] salt = Arrays.copyOfRange(os, 0, 48);
		    byte[] hash = Arrays.copyOfRange(os, 48, 96);
		 
		    // Regenerate Password Hash using Recovered Salt and Password
		    byte[] phash = deriveKey(p, salt, 100000, 384);
		 
		    // Do they Match (using Time Constant Comparison)?
		    return MessageDigest.isEqual(hash, phash);
		  }
		  return false;
		}
	
	public static byte[] deriveKey(String p, byte[] s, int i, int l) throws Exception {
		  PBEKeySpec ks = new PBEKeySpec(p.toCharArray(), s, i, l);
		  SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA384");
		  return skf.generateSecret(ks).getEncoded();
		}
}
