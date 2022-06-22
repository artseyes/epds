package com.EPDS.US;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import java.util.Base64;

public class AESenc {
  private static final String ALGO = "AES";
  private static byte[] keyValue =
            new byte[]{'T', 'h', 'e', 'B', 'e', 's', 't', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y'};

    /**
     * Encrypt a string with AES algorithm.
     *
     * @param args is a string
     * @return the encrypted string
     * @throws Exception 
     */
	  public static void main(String[] args) throws Exception {
		String password = "CBCA_app_pwd123";
		String encryptedPasswordB64 = encrypt(password);
		System.out.println(encryptedPasswordB64);
		String decryptedValue = decrypt(encryptedPasswordB64);
		System.out.println(decryptedValue);
	
	  }
    public static String encrypt(String data) throws Exception {
        Key key = generateKey("CBCA_app_Decrypt123");
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encVal);
    }

    /**
     * Decrypt a string with AES algorithm.
     *
     * @param encryptedData is a string
     * @return the decrypted string
     */
    public static String decrypt(String encryptedData) throws Exception {
        Key key = generateKey("CBCA_app_Decrypt123");
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = Base64.getDecoder().decode(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);
        return new String(decValue);
    }

    /**
     * Generate a new encryption key.
     */
    private static Key generateKey(String key) throws Exception {
        return new SecretKeySpec(key.getBytes(), ALGO);
    }

}
