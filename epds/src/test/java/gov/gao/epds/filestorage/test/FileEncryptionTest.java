/**
 * 
 */
package gov.gao.epds.filestorage.test;

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

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import gov.gao.epds.filestorage.FileStorageCryptoUtils;
import gov.gao.epds.filestorage.FileStorageEncryption;
import gov.gao.epds.utils.fileencryption.Encryptor;


/**
 * @author MHussaini
 *
 */
public class FileEncryptionTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		
		File originalFile = new File("C:/Users/mhussaini/GAO/encrypdecrypttest/largePdf_2.pdf");
		
		File tempFile = new File(originalFile.getPath() + ".temp" );
		
		Key secretKey = FileStorageCryptoUtils.getSecurityKey();
		
		String password = "mysupersecretpassword";
		
		Encryptor encryptor = new Encryptor(secretKey, "AES/CTR/NoPadding", 16);
		
		/*FileEncryptor fe = new FileEncryptor(secretKey);*/
		
		checkIfTempFileExists(tempFile);
		FileStorageEncryption.encrypt(originalFile, tempFile,encryptor);
		
		renameTempFile(originalFile, tempFile);
		
		checkIfTempFileExists(tempFile);
		
		decrypt(new FileInputStream (originalFile), new FileOutputStream(tempFile),secretKey);
		
		renameTempFile(originalFile, tempFile);
		
		
		
		/*encrypt(secretKey,originalFile);
		
		// Assuming the same secret key is used
		decrypt(originalFile, secretKey);*/
	}


	/**
	 * @param tempFile
	 * @throws IOException
	 */
	private static void checkIfTempFileExists(File tempFile) throws IOException {
		if (!tempFile.exists()){
			tempFile.createNewFile();
		}
	}


	/**
	 * @param originalFile
	 * @param tempFile
	 */
	private static void renameTempFile(File originalFile, File tempFile) {
		String new_path = tempFile.getAbsolutePath().substring(0,
				tempFile.getAbsolutePath().length() - ".temp".length());
		
		
		if (!tempFile.renameTo(new File(new_path))){
			originalFile.delete();
			tempFile.renameTo(new File(new_path));
		};
	}


	/**
	 * @param originalFile
	 * @param secretKey
	 * @throws GeneralSecurityException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private static void decrypt(FileInputStream fis,FileOutputStream fos, Key secretKey)
			throws GeneralSecurityException, IOException, FileNotFoundException {
		Encryptor encryptor = new Encryptor(secretKey, "AES/CTR/NoPadding", 16);

		InputStream is = null;
		OutputStream os = null;

		try {
		  is = encryptor.wrapInputStream(fis);
		  os = fos;
		  byte[] buffer = new byte[4096];
		  int nRead;
		  while((nRead = is.read(buffer)) != -1) {
		    os.write(buffer, 0, nRead);
		  }
		  os.flush();
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
	 * @param secretKey
	 * @throws FileNotFoundException
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	private static void encrypt(Key secretKey,File file) throws FileNotFoundException, GeneralSecurityException, IOException {
		Encryptor encryptor = new Encryptor(secretKey, "AES/CTR/NoPadding", 16);

		InputStream is = null;
		OutputStream os = null;
		try {
		  is = new FileInputStream(file);
		  os = encryptor.wrapOutputStream(new FileOutputStream(file));
		  byte[] buffer = new byte[4096];
		  int nRead;
		  while((nRead = is.read(buffer)) != -1) {
		    os.write(buffer, 0, nRead);
		  }
		  os.flush();
		} finally {
		  if(is != null) {
		    is.close();
		  }
		  if(os != null) {
		    os.close();
		  }
		}
	}

	
	
	
}
