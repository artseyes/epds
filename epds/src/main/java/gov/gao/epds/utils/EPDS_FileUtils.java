package gov.gao.epds.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.SystemUtils;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.gao.epds.dto.DTOValidator;
import gov.gao.epds.dto.SubmitNewDocDTO;
import gov.gao.epds.dto.UploadedFileIdentifier;
import gov.gao.epds.filestorage.FileStorageEncryption;
import gov.gao.epds.filestorage.SFTP;
import gov.gao.epds.persistence.entity.File_Info;
import gov.gao.epds.persistence.entity.Protest_Info;
import gov.gao.epds.persistence.entity.User_Info;
import gov.gao.epds.session.EpdsSession;
import gov.gao.epds.utils.templates.HtmlToPDF;
import gov.gao.epds.utils.templates.TemplateUtils;

public class EPDS_FileUtils {

	private static Properties prop = new Properties();

	private final static Logger logger = LoggerFactory
			.getLogger(EPDS_FileUtils.class);

	static {
		try {
			GlobalParams.loadProperties("file-storage-keystore.properties",prop);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static List<File_Info> fillupEachFile_InfoWithFileName(
			List<File_Info> file_InfoList) {
		List<File_Info> newFile_InfoList = new ArrayList<File_Info>();
		String fileName = "";
		for (File_Info eachFile_Info : file_InfoList) {
			fileName = getFileName(eachFile_Info.getFile_Path());
			eachFile_Info.setFileName(fileName);
			newFile_InfoList.add(eachFile_Info);
		}

		return newFile_InfoList;
	}

	private static String getFileName(String file_Path) {

		String filename = FileUtils.getFile(new File(file_Path)).getName();
		
		
		if (null != filename && filename.startsWith("C:")){
			return "";
		}

		if ((filename == null || filename.equalsIgnoreCase("")) && !"/".equalsIgnoreCase(file_Path)){
			String pattern = Pattern.quote(System.getProperty("file.separator"));
			String[] file_Path_Locations = file_Path.split(pattern);
			int length = file_Path_Locations.length;

			try {
				if (length == 1){
					file_Path_Locations = file_Path.split("\\\\");
				}
			}catch (Exception e){
				e.printStackTrace();
				file_Path_Locations[0] = " ";
				
			}

			return file_Path_Locations[file_Path_Locations.length - 1];
		}

		return filename;

	}

	public static String splitFiles(String filepath, String a_No) {
		String pattern = Pattern.quote(System.getProperty("file.separator"));
		String fileName = null;

		// Roshan please come back over here ..... before we were using
		// different directory structure with ("CASE_") prefix to store files
		// ......
		if (!filepath.contains("CASE_0") && filepath.contains(a_No)) {
			String splittedFilePath[] = filepath.split(pattern);
			int flength = splittedFilePath.length;
			fileName = splittedFilePath[flength - 1];
		}

		return fileName;
	}







	public static Boolean checkIfThisDocDoesNotRequireFileUpload(Integer docId){

		Integer [] listOfDocIds = {27,38,12,179,53,100,91,82,67,154,
                234,233,231,232,215,214,213,212,211,210};;


		return ArrayUtils.contains( listOfDocIds, docId ) ;
	}





	public static String findPO(List<File_Info> file_InfoList) {
		for (File_Info eachFile_Info : file_InfoList) {
			return eachFile_Info.getIs_Confidential();
		}
		return "";
	}



	public static String getFileAlert(File_Info eachFile_Info, String user_Id) {
		String fileAlert = "Y";
		String alreadyViewedBy = eachFile_Info.getAlready_viewed_by();

		switch (eachFile_Info.getDocTypeName().toLowerCase(Locale.ENGLISH)) {
		case "minute entry":
		case "request to intervene approved":
		case "notice of appearance acknowledged":
			fileAlert = "N";
			break;
		}

		if (alreadyViewedBy != null && !fileAlert.equalsIgnoreCase("N")) {
			String[] listOfAlreadyViewedByUserIds = alreadyViewedBy.split(";");

			if (listOfAlreadyViewedByUserIds != null
					&& listOfAlreadyViewedByUserIds.length > 0) {
				for (String each : listOfAlreadyViewedByUserIds) {
					if (each.equalsIgnoreCase(user_Id)) {
						fileAlert = "N";
						break;
					}
				}
			}
		}

		return fileAlert;
	}

	public static String getStoragePath(HttpServletRequest request,
			String attachmentType) {
		/*String base_dir = request.getServletContext().getRealPath(
				"/tempFileStorage");*/
		String base_dir = System.getProperty("jboss.server.temp.dir"),
				uniqueIdentifier = request.getParameter("flowIdentifier");

		if (null == attachmentType) {
			attachmentType = request.getParameter("attachmentType");

		}


		attachmentType = attachmentType.replaceAll("[^a-zA-Z0-9]", "");
		attachmentType = attachmentType.substring(0, Math.min(attachmentType.length(), 50));

		User_Info userInfo = (User_Info) EpdsSession.getAttribute(request,
				"user_Info");
		String user_Id = userInfo.getUser_Id();
		String a_No;
		Protest_Info protestInfo = (Protest_Info) EpdsSession.getAttribute(
				request, "protestInfo");

		if (null != request.getParameter("a_No")) {
			a_No = request.getParameter("a_No");
		} else {
			if (protestInfo != null) {
				a_No = protestInfo.getA_No();
			} else {
				a_No = (String) EpdsSession.getAttribute(request, "a_No");
			}
		}

		if (null != a_No && null != attachmentType){

			if (!a_No.matches(DTOValidator.PROTEST_ID_PATTERN)
					|| !attachmentType.matches(DTOValidator.ATTACHMENT_TYPE_PATTERN)){
				throw new IllegalArgumentException("Invalid Input format");
			}
		}

		a_No = a_No.replaceAll(Pattern.quote("."), "-");

		if (uniqueIdentifier == null){
			uniqueIdentifier = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		}

		String storage_Path = base_dir + File.separator + "tmpFiles"
				+ File.separator + a_No + File.separator + user_Id
				+ File.separator + attachmentType + File.separator + uniqueIdentifier;

		return storage_Path;
	}

	public static void saveTemplateDocument(HttpServletRequest request,
			SubmitNewDocDTO submitNewDocDTO, String attachmentType) throws Exception {

		int docTypeId = submitNewDocDTO.getDocId();
		String content = TemplateUtils.cleanHtmlContentBeforeCreatingPdf(
				(String) request.getParameter("content"),
				Whitelist.relaxed()
				.addAttributes(":all", "style")
				.preserveRelativeLinks(true).removeTags("code"));
		Map<String, String> mapOfFileMeta = TemplateUtils
				.getMapOfFileMetaData(docTypeId + "");

		String typeOfDoc = submitNewDocDTO.getTypeofdocument();

		if (mapOfFileMeta.get("fileName").contains("Blank")){

		    if (mapOfFileMeta.get("fileName").equalsIgnoreCase("Blank")
		            && submitNewDocDTO.getDocDescFiller() != null) {
		        typeOfDoc = submitNewDocDTO.getDocDescFiller();
            }
			typeOfDoc = typeOfDoc.replaceAll("[^a-zA-Z0-9]", "");
			typeOfDoc = typeOfDoc.substring(0, Math.min(typeOfDoc.length(), 100));



			mapOfFileMeta.put("fileName",WordUtils.capitalizeFully(typeOfDoc));
		}

		new HtmlToPDF().createPdf(request, attachmentType, content,
				mapOfFileMeta);
	}

	/*@SuppressWarnings("unchecked")
	public static void setFilePathListInSession(String filePath,
			HttpServletRequest request) {
		List<String> filePathList = null;

		if (EpdsSession.getAttribute(request, "filePathList") != null) {
			filePathList = (List<String>) EpdsSession.getAttribute(request,
					"filePathList");
		} else {
			filePathList = new ArrayList<String>();
		}

		filePathList.add(filePath);


		EpdsSession.setAttribute(request, "filePathList", filePathList);
	}*/

	@SuppressWarnings("unchecked")
	public static void setFilePathListInSession(String filePath,
			HttpServletRequest request,String fileTypeCode, Map<String, String> errorMap) {

		List<UploadedFileIdentifier> filePathList = null;
		if (EpdsSession.getAttribute(request, "filePathList") != null) {

			filePathList = (List<UploadedFileIdentifier>) EpdsSession.getAttribute(request,
					"filePathList");
		} else {
			filePathList = new ArrayList<UploadedFileIdentifier>();
		}

		UploadedFileIdentifier upfi = new UploadedFileIdentifier();

		upfi.setFileIdentifierCode(fileTypeCode);
		upfi.setFilePath(filePath);

		filePathList.add(upfi);

		Set<UploadedFileIdentifier> filePathSet = new HashSet<UploadedFileIdentifier>(filePathList);

		filePathList = new ArrayList<UploadedFileIdentifier>(filePathSet);

		EpdsSession.setAttribute(request, "filePathList", filePathList);
	}



	/*public static void downloadZipFile(OutputStream outputStream, File encryptedZipFile) throws IOException{

		Map<String, InputStream> inMemoryFiles= ZipUnZipUtils.decryptPasswordProtectedZipFile(encryptedZipFile, PropertyFileEncrypter.decrypt(prop.getProperty("zippass")));

		ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(outputStream));

		try {
			if (inMemoryFiles.isEmpty() || inMemoryFiles.size() <=0){

				fileDoesNotExist(outputStream);

			}else{

				for (String fileName : inMemoryFiles.keySet()) {

		            InputStream is = new BufferedInputStream(inMemoryFiles.get(fileName));
		            ZipEntry anEntry = new ZipEntry(fileName);
		            zos.putNextEntry(anEntry);

		            byte[] buf= new byte[8192];
		            int bytesread = 0, bytesBuffered = 0;
			            while( (bytesread = is.read( buf )) > -1 ) {
			                zos.write( buf, 0, bytesread );
			                bytesBuffered += bytesread;
			                if (bytesBuffered > 1024 * 1024) {
			                	//flush after 1MB
			                    bytesBuffered = 0;
			                    zos.flush();
			                }
			            }

			           is.close();
			           zos.closeEntry();
		        	}
			}


			} finally {

	            if (zos != null) {
	            	zos.close();
	                zos.flush();
	            }



	        }


	}*/

	/**
	 * @param outputStream
	 * @throws IOException
	 */
	public static  void fileDoesNotExist(OutputStream outputStream) throws IOException {
		String errorMessage = "Sorry. The file you are looking for does not exist";
		outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
		outputStream.close();
	}


	/**
	 * @param inputFile
	 * @param out
	 * @throws IOException
	 */
	public void downloadFiles(File inputFile, OutputStream out) throws IOException {
		FileInputStream fileInputStream = null;
		try
		{
		    fileInputStream  =  new FileInputStream(inputFile);
		    byte[] buf=new byte[8192];
		    int bytesread = 0, bytesBuffered = 0;
		    while( (bytesread = fileInputStream.read( buf )) > -1 ) {
		        out.write( buf, 0, bytesread );
		        bytesBuffered += bytesread;
		        if (bytesBuffered > 1024 * 1024) {
		        	//flush after 1MB
		            bytesBuffered = 0;
		            out.flush();
		        }
		    }
		}
		finally {
		    if (out != null) {
		    	out.close();
		        out.flush();
		    }
		    if (fileInputStream != null){
		    	fileInputStream.close();
		    }
		}
	}



	/**
	 * @param inputFile
	 * @param out
	 * @throws IOException
	 */
	public void downloadOtherFiles(File inputFile, OutputStream out) throws IOException {
		try
		{
			SFTP sftp = new SFTP();
			sftp.download(inputFile,out);
		}
		finally {
		    if (out != null) {
		    	out.close();
		        out.flush();
		    }
		}
	}


	/**
	 * @param outputBytes
	 * @param out
	 * @throws IOException
	 */
	public static void storeFilesInBase64OutputStream(byte[] outputBytes, OutputStream out) throws IOException {

		ByteArrayInputStream fileInputStream = null;
		try
		{
		    fileInputStream  =  new ByteArrayInputStream(outputBytes);
		    byte[] buf=new byte[8192];
		    int bytesread = 0, bytesBuffered = 0;
		    while( (bytesread = fileInputStream.read( buf )) > -1 ) {
		        out.write( buf, 0, bytesread );
		        bytesBuffered += bytesread;
		        if (bytesBuffered > 1024 * 1024) {
		        	//flush after 1MB
		            bytesBuffered = 0;
		            out.flush();
		        }
		    }
		}
		finally {
		    if (out != null) {
		    	out.close();
		        out.flush();
		    }
		    if (fileInputStream != null){
		    	fileInputStream.close();
		    }
		}
	}

	/**
	 * @param file-- file that need to be moved to the remote server.
	 */
	public static void uploadToRemoteServer(File file){

		ExecutorService threadpool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		List<Future<Boolean>> transfers = new ArrayList<Future<Boolean>>();

		transfers.add(threadpool.submit(new Callable<Boolean>() {
			public Boolean call() throws IOException {
				return SFTP.uploadToServer(file);
			}
		}));

		// all copy actions are submitted now... so we wait for the threadpool.
		threadpool.shutdown(); // orderly shutdown, all tasks are completed.
		for (Future<Boolean> fut : transfers) {
		    try {
		        fut.get();
		    } catch (Exception ioe) {
		    	ioe.getMessage();
		    	logger.warn("Unable to transfer file: " + ioe.getMessage(), ioe);
		    }
		}


	}

	/**
	 * @param tempFile
	 * @throws IOException
	 */
	public static void checkIfTempFileExists(File tempFile) throws IOException {
		if (!tempFile.exists()){
			tempFile.createNewFile();
		}
	}


	/**
	 * @param originalFile
	 * @param tempFile
	 */
	public static void renameTempFile(File originalFile, File tempFile) {
		String new_path = tempFile.getAbsolutePath().substring(0,
				tempFile.getAbsolutePath().length() - ".temp".length());


		if (!tempFile.renameTo(new File(new_path))){
			secureDeleteFile(originalFile, true);
			tempFile.renameTo(new File(new_path));
		};
	}


	/**
	 * @param file
	 */
	public static CompletableFuture<File> encryptFileAndUploadToRemoteServer(File file) {

		ExecutorService executor = Executors.newFixedThreadPool(2);
		System.out.println("encryptFileAndUploadToRemoteServer " + file.getAbsolutePath());
        System.out.println("encryptFileAndUploadToRemoteServer length: " + file.length());

		// first encrypt the file
		CompletableFuture<File> future = CompletableFuture.supplyAsync((Supplier<File>) () -> {

			System.out.println("File encryption is being processed");
			if (!FileStorageEncryption.encryptFile(file)) {
				throw new RuntimeException("Error occured during file encryption....");
			}

			return file;
		},executor);


		  future.exceptionally(ex ->{
			  System.out.println("Exception occured while encrypting file");
			  executor.shutdown();
		  return file;
		  });

		  future.thenAcceptAsync(encryptedFile ->{

		      System.out.println("File is being uploaded to remote server" + encryptedFile.getAbsolutePath());
		      if (SFTP.uploadToServer(encryptedFile)) {
					secureDeleteFile(encryptedFile);
					executor.shutdown();
				}else{
					System.out.println("Exception occured when transferring the file" + encryptedFile.getAbsolutePath());
					throw new RuntimeException("File transfer was not successfull" + encryptedFile.getAbsolutePath());

				}

			  });

		  future.exceptionally(ex ->{
			  System.out.println("Exception occured while transferring file to remote server");
			  ex.printStackTrace();
			  executor.shutdown();
		  return file;
		  });

		  return future;
	}

	// secure deletion only supported on unix, for windows just doing standard File.delete()
	// can delete a file or directory
	public static void secureDeleteFile(String filePath) {
		secureDeleteFile(filePath, false);
	}
    public static void secureDeleteFile(String filePath, boolean wait) {
        if (SystemUtils.IS_OS_WINDOWS) {
            secureDeleteFile(new File(filePath), wait);
        } else {
            String cmd = "/home/appadmin/bin/epds_shred " + filePath;
            try {
                System.out.println("secureDeleteFile: " + filePath);
                Process p = Runtime.getRuntime().exec(cmd);
                if (wait) {
					// need to wait for delete to finish else will delete what your doing when it completes
					p.waitFor();
				}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

	public static void secureDeleteFile(File file) {
		secureDeleteFile(file, false);
	}
    public static void secureDeleteFile(File file, boolean wait) {
        if (SystemUtils.IS_OS_WINDOWS) {
            file.delete();
        } else {
            secureDeleteFile(file.getPath(), wait);
        }
    }

}

