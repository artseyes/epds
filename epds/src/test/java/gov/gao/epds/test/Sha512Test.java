package gov.gao.epds.test;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha512Test {

	public static void main(String[] args) {

		System.out.println(generateHash("admin"));
	}
	
	public static String generateHash(String toHash) {
		
	    MessageDigest md = null;
	    byte[] hash = null;
	    try {
	        md = MessageDigest.getInstance("SHA-512");
	        hash = md.digest(toHash.getBytes("UTF-8"));
	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
	    }
	    return convertToHex(hash);
	}
	 
	/**
	* Converts the given byte[] to a hex string.
	* @param raw the byte[] to convert
	* @return the string the given byte[] represents
	*/
	private static  String convertToHex(byte[] raw) {
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < raw.length; i++) {
	        sb.append(Integer.toString((raw[i] & 0xff) + 0x100, 16).substring(1));
	    }
	    return sb.toString();
	}

}
