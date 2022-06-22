/*package gov.gao.epds.filestorage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import gov.gao.epds.utils.GlobalParams;
import gov.gao.epds.utils.PropertyFileEncrypter;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.unzip.UnzipUtil;
import net.lingala.zip4j.util.Zip4jConstants;

public class ZipUnZipUtils {

	private static Properties prop = new Properties();

	static {
		try {
			GlobalParams.loadProperties("file-storage-keystore.properties", prop);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private final static int BUFF_SIZE = 4096;

	public static synchronized void unzip(File zip, Boolean deleteZipFile) throws InterruptedException, IOException {

		unzip(zip, PropertyFileEncrypter.decrypt(prop.getProperty("zippass")));

		if (deleteZipFile) {
			Thread.sleep(1000);
			deleteZIP(zip);
		}

	}

	public static synchronized void createZipAndDeleteUnzippedDir(File zip) throws InterruptedException, IOException {
		CreateZipFileWithAESEncryption(zip, PropertyFileEncrypter.decrypt(prop.getProperty("zippass")));

	}

	public static void unzip(File zip, String password) {

		ZipInputStream is = null;
		OutputStream os = null;
		Boolean found = false;
		try {
			// Initiate the ZipFile
			ZipFile zipFile = new ZipFile(zip);
			String dir = "";

			// If zip file is password protected then set the password
			if (zipFile.isEncrypted()) {
				zipFile.setPassword(password);
			}

			String fileNameWithOutExt = FilenameUtils.removeExtension(zip.getName());

			@SuppressWarnings("unchecked")
			List<FileHeader> fileHeaders = zipFile.getFileHeaders();

			for (FileHeader fileHeader : fileHeaders) {

				if (fileHeader.isDirectory() && fileHeader.getFileName().toUpperCase(Locale.ENGLISH)
						.contains(fileNameWithOutExt.toUpperCase(Locale.ENGLISH))) {
					found = true;
					break;
				}
			}

			if (found) {

				dir = zip.getAbsolutePath().substring(0, zip.getAbsolutePath().lastIndexOf(File.separator) + 1);

			} else {

				dir = zip.getAbsolutePath().substring(0, zip.getAbsolutePath().lastIndexOf(File.separator) + 1)
						+ fileNameWithOutExt;
			}

			// Get a list of FileHeader. FileHeader is the header information
			// for all the
			// files in the ZipFile
			@SuppressWarnings("unchecked")
			List<FileHeader> fileHeaderList = zipFile.getFileHeaders();

			// Loop through all the fileHeaders
			for (int i = 0; i < fileHeaderList.size(); i++) {
				FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
				if (fileHeader != null) {

					// Build the output file
					String outFilePath = dir + System.getProperty("file.separator") + fileHeader.getFileName();
					File outFile = new File(outFilePath);

					// Checks if the file is a directory
					if (fileHeader.isDirectory()) {
						// This functionality is up to your requirements
						// For now I create the directory
						outFile.mkdirs();
						continue;
					}

					// Check if the directories(including parent directories)
					// in the output file path exists
					File parentDir = outFile.getParentFile();
					if (!parentDir.exists()) {
						parentDir.mkdirs();
					}

					// Get the InputStream from the ZipFile
					is = zipFile.getInputStream(fileHeader);
					// Initialize the output stream
					os = new FileOutputStream(outFile);

					int readLen = -1;
					byte[] buff = new byte[BUFF_SIZE];

					// Loop until End of File and write the contents to the
					// output stream
					while ((readLen = is.read(buff)) != -1) {
						os.write(buff, 0, readLen);
					}

					// Please have a look into this method for some important
					// comments
					closeFileHandlers(is, os);

					// To restore File attributes (ex: last modified file time,
					// read only flag, etc) of the extracted file, a utility
					// class
					// can be used as shown below
					UnzipUtil.applyFileAttributes(fileHeader, outFile);

					System.out.println("Done extracting: " + fileHeader.getFileName());
				} else {
					System.err.println("fileheader is null. Shouldn't be here");
				}
			}
		} catch (ZipException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				closeFileHandlers(is, os);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void closeFileHandlers(ZipInputStream is, OutputStream os) throws IOException {
		// Close output stream
		if (os != null) {
			os.close();
			os = null;
		}

		// Closing inputstream also checks for CRC of the the just extracted
		// file.
		// If CRC check has to be skipped (for ex: to cancel the unzip
		// operation, etc)
		// use method is.close(boolean skipCRCCheck) and set the flag,
		// skipCRCCheck to false
		// NOTE: It is recommended to close outputStream first because Zip4j
		// throws
		// an exception if CRC check fails
		if (is != null) {
			is.close();
			is = null;
		}
	}

	// use this method to delete the files in the directory before deleting it.
	private static void deleteZIP(File f) throws IOException {
		if (f.isDirectory()) {
			for (File c : f.listFiles())
				deleteZIP(c);
		}
		if (!f.delete())
			throw new FileNotFoundException("Failed to delete file: " + f);
	}

	private static synchronized void CreateZipFileWithAESEncryption(File zipFilePath, String password) throws IOException {

		try {
			// Initiate ZipFile object with the path/name of the zip file.
			ZipFile zipFile = new ZipFile(zipFilePath);

			// Initiate Zip Parameters which define various properties such
			// as compression method, etc. More parameters are explained in
			// other
			// examples
			ZipParameters parameters = getZipParameters(password);

			// Now add files to the zip file
			// Note: To add a single file, the method addFile can be used
			// Note: If the zip file already exists and if this zip file is a
			// split file
			// then this method throws an exception as Zip Format Specification
			// does not
			// allow updating split zip files
			String fileNameWithOutExt = FilenameUtils.removeExtension(zipFilePath.getName());
			String unzippedDir = zipFilePath.getAbsolutePath().substring(0,
					zipFilePath.getAbsolutePath().lastIndexOf(File.separator) + 1) + fileNameWithOutExt;

			// extra check to make sure the zip file doesn't exist other wise
			// create zip will throw exception
			if (zipFilePath.exists()) {
				deleteZIP(zipFilePath);
			}

			zipFile.createZipFileFromFolder(unzippedDir, parameters, false, 10);

			// After password protected zip file is created we need to make sure
			// we deleted the unzipped directory
			if (zipFilePath.exists()) {
				Thread.sleep(1000);
				deleteZIP(new File(unzippedDir));
			}

		} catch (ZipException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	
	
	public static void CreateZipFileWithAESEncryption(File unzippedDir,File zipFilePath) throws IOException {

		try {
			// Initiate ZipFile object with the path/name of the zip file.
			ZipFile zipFile = new ZipFile(zipFilePath);

			// Initiate Zip Parameters which define various properties such
			// as compression method, etc. More parameters are explained in
			// other
			// examples
			ZipParameters parameters = getZipParameters(PropertyFileEncrypter.decrypt(prop.getProperty("zippass")));

			// Now add files to the zip file
			// Note: To add a single file, the method addFile can be used
			// Note: If the zip file already exists and if this zip file is a
			// split file
			// then this method throws an exception as Zip Format Specification
			// does not
			// allow updating split zip files
			String fileNameWithOutExt = FilenameUtils.removeExtension(zipFilePath.getName());
			String unzippedDir = zipFilePath.getAbsolutePath().substring(0,
					zipFilePath.getAbsolutePath().lastIndexOf(File.separator) + 1) + fileNameWithOutExt;
			// extra check to make sure the zip file doesn't exist other wise
			// create zip will throw exception
			if (zipFilePath.exists()) {
				deleteZIP(zipFilePath);
			}

			zipFile.createZipFileFromFolder(unzippedDir, parameters, false, 10);

			// After password protected zip file is created we need to make sure
			// we deleted the unzipped directory
			if (zipFilePath.exists()) {
				Thread.sleep(1000);
				deleteZIP(unzippedDir);
			}

		} catch (ZipException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public static Map<String, InputStream> decryptPasswordProtectedZipFile(File inputFile, String pass) throws IOException {

		downloadZipFileToLocalServer(zipfile);
		
		
		ZipInputStream is = null;
		OutputStream os = null;
		Map<String, InputStream> inMemoryFiles = new HashMap<String, InputStream>();

		try {

			ZipFile zipFile = new ZipFile(inputFile);

			if (zipFile.isEncrypted()) {
				zipFile.setPassword(pass);
			}

			@SuppressWarnings("unchecked")
			List<FileHeader> fileHeaderList = zipFile.getFileHeaders();

			for (int i = 0; i < fileHeaderList.size(); i++) {

				FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
				
				if (fileHeader != null) {

					String path = new File(".").getCanonicalPath();
					// Build the output file
                    String outFilePath = path
                        + System.getProperty("file.separator")
                        + fileHeader.getFileName();
                    File outFile = new File(outFilePath);
                    // Checks if the file is a directory
                    if (fileHeader.isDirectory()) {
                        // This functionality is up to your requirements
                        // For now I create the directory
                        outFile.mkdirs();
                        continue;
                    }
                    // Check if the directories(including parent directories)
                    // in the output file path exists
                    File parentDir = outFile.getParentFile();
                    if (!parentDir.exists()) {
                        parentDir.mkdirs();
                    }
                    
					is = zipFile.getInputStream(fileHeader);
					
					int uncompressedSize = (int) fileHeader.getUncompressedSize();
					 
					// Initialize the output stream
                    os = new FileOutputStream(outFile);
					
                    int bytesRead = -1;;
					
                    byte[] buffer = new byte[4096];

					while ((bytesRead = is.read(buffer)) != -1) {
						os.write(buffer, 0, bytesRead);
						os.flush();
					}

					byte[] uncompressedBytes = ((ByteArrayOutputStream) os).toByteArray();

					if (!fileHeader.isDirectory()) {
						inMemoryFiles.put(fileHeader.getFileName(), new ByteArrayInputStream(uncompressedBytes));
						inMemoryFiles.put(fileHeader.getFileName(), new FileInputStream(outFile));
					}
					closeFileHandlers(is, os);
					if (outFile.exists()){
						outFile.delete();
					}

				} else {
					System.err.println("fileheader is null. Shouldn't be here");
				}

			}

		} catch (ZipException e) {
			e.printStackTrace();
		} finally {
			closeFileHandlers(is, os);
		}

		return inMemoryFiles;
	}

	*//** WHen downloading ZIP file the file first need to downloaded to local server and then decrypt the file
	 * @param zipfile
	 * @throws FileNotFoundException
	 * @throws IOException
	 *//*
	private static void downloadZipFileToLocalServer(File zipfile) throws FileNotFoundException, IOException {


		InputStream fin = null;
		OutputStream out = null;
		SFTP sftp = new SFTP();
		  try {
			    fin = sftp.download(zipfile,out);
			    out = new FileOutputStream(zipfile);
				  IOUtils.copy(fin, out);
				  fin.close();
				  out.close();
			}finally{
				if(null != fin) {
			        IOUtils.closeQuietly(fin);
			    }
			    if(null != out) {
			        IOUtils.closeQuietly(out);
			    }
			}
		
		
		
	
	}

	private static ZipParameters getZipParameters(String password) {

		// Initiate Zip Parameters which define various properties such
		// as compression method, etc. More parameters are explained in other
		// examples
		ZipParameters parameters = new ZipParameters();
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE); // set
																		// compression
																		// method
																		// to
																		// deflate
																		// compression

		// Set the compression level. This value has to be in between 0 to 9
		// Several predefined compression levels are available
		// DEFLATE_LEVEL_FASTEST - Lowest compression level but higher speed of
		// compression
		// DEFLATE_LEVEL_FAST - Low compression level but higher speed of
		// compression
		// DEFLATE_LEVEL_NORMAL - Optimal balance between compression
		// level/speed
		// DEFLATE_LEVEL_MAXIMUM - High compression level with a compromise of
		// speed
		// DEFLATE_LEVEL_ULTRA - Highest compression level but low speed
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

		// Set the encryption flag to true
		// If this is set to false, then the rest of encryption properties are
		// ignored
		parameters.setEncryptFiles(true);

		// Set the encryption method to AES Zip Encryption
		parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);

		// Set AES Key strength. Key strengths available for AES encryption are:
		// AES_STRENGTH_128 - For both encryption and decryption
		// AES_STRENGTH_192 - For decryption only
		// AES_STRENGTH_256 - For both encryption and decryption
		// Key strength 192 cannot be used for encryption. But if a zip file
		// already has a
		// file encrypted with key strength of 192, then Zip4j can decrypt this
		// file
		parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);

		// Set password
		parameters.setPassword(password);

		return parameters;
	}

}
*/