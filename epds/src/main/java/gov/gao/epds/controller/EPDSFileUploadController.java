package gov.gao.epds.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.gao.epds.dto.FileUploadDTO;
import gov.gao.epds.dto.FileUploadError;
import gov.gao.epds.dto.InputValidationError;
import gov.gao.epds.exception.InvalidFileException;
import gov.gao.epds.filestorage.SFTP;
import gov.gao.epds.fileupload.utils.AVScanClient;
import gov.gao.epds.fileupload.utils.AVScanResult;
import gov.gao.epds.fileupload.utils.ChunkFileInfo;
import gov.gao.epds.fileupload.utils.ChunkFileInfoStorage;
import gov.gao.epds.fileupload.utils.HttpUtils;
import gov.gao.epds.rest.auth.services.AuthUtil;
import gov.gao.epds.utils.EPDS_FileUtils;
import gov.gao.epds.utils.Util;



@Controller
public class EPDSFileUploadController { // NO_UCD (unused code)
	
	private final static Logger logger = LoggerFactory
			.getLogger(EPDSFileUploadController.class);
	
	@Autowired
	private AVScanClient avScanClient;
	
	
	
	/*@PostMapping(value = "/upload-epds-documents")
	//@RequestMapping(value=("/upload-epds-documents"),headers=("content-type=multipart/*"),method=RequestMethod.POST)
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response,@RequestParam("file") MultipartFile file, FileUploadDTO dto) {
		
		MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
		@RequestParam("file") MultipartFile file
		MultipartFile file = request.getParameter("file");
		String testing = request.getParameter("flowChunkNumber");
		System.out.println(testing);
	}*/
	/**
	 * Each file is sent in chunk of 1mb and then the input stream 
	 * is scanned for virus and if the virus is detected then those requests are rejected and removed from the queue.
	 * 
	 * @param request
	 * @param response
	 * @throws Exception 
	 */
	@PostMapping(value = "/upload-epds-documents")
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response, FileUploadDTO dto, @RequestParam("file") MultipartFile file) throws Exception {
		
		Map<String,Object> errorMap = new HashMap<String,Object>();
		ObjectMapper om = new ObjectMapper();
        FileUploadError error = new FileUploadError();
		InputStream inputStream = null;
		
		try {
			
		inputStream = file.getInputStream();
		ChunkFileInfo info = getCurrentFileInfo(request);
		int currentChunkNumber = getCurrentFileChunkNumber(request);
		List<InputValidationError> constraintViolations = AuthUtil.validateDTO(dto);
		Integer fileNameLength = dto.getFlowFilename().length();
		if (!constraintViolations.isEmpty() && null != constraintViolations || fileNameLength > 250 ){
			
			ChunkFileInfoStorage.getInstance().remove(info);
			
			
			error.setFileName(dto.getFlowFilename());
			error.setFileError("File Name : " + dto.getFlowFilename() + ", Error : "+ " :  File name is invalid and will be removed from the upload queue. "
					+ "Please rename the file and upload it again and then submit the form. "
					+ "   The only allowed characters in the file name are a-z, A-Z, 0-9, ., _, #,(),&. Make sure the length of the file name do not exceed 250 characters. ");
			
			
			if (fileNameLength > 250){
				error.setFileError("File Name : " + dto.getFlowFilename() + ", Error : "+ " File name cannot exceed 250 characters!! ");
			}
			
            errorMap.put("error",error);
			populateErrorResp(response, errorMap, om);
			throw new InvalidFileException(dto.getFlowFilename() + " file name is Invalid.");
		}
		
		startFileUpload(request, response, info, inputStream, currentChunkNumber,dto);
		
		
		} catch (FileNotFoundException e) {
            e.printStackTrace();
            
            error.setFileName(dto.getFlowFilename());
            error.setFileError("File Name : " + dto.getFlowFilename() + ", Error : "+ " We are unable to open the file. If it is a PDF, please make sure the file is not certified/signed.");
            errorMap.put("error",error);
			populateErrorResp(response, errorMap, om);
        }catch (Exception e) {
		    e.printStackTrace();
		    
		}

		
	}



	/**
	 * @param response
	 * @param errorMap
	 * @param om
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	private void populateErrorResp(HttpServletResponse response, Map<String, Object> errorMap, ObjectMapper om)
			throws JsonProcessingException, IOException {
		
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, private");
		
		
		String jsonResponse = om.writeValueAsString(errorMap);
		
		PrintWriter out = response.getWriter();
		out.write(jsonResponse);
		out.flush();
	}



	/**
	 * @param response
	 * @param info
	 * @param avScanResult
	 * @throws JsonProcessingException
	 * @throws IOException
	 * @throws InvalidFileException
	 */
	private void failedVirusScanResponse(HttpServletResponse response,
			ChunkFileInfo info, AVScanResult avScanResult)
			throws JsonProcessingException, IOException, InvalidFileException {
		
		Map<String,Object> errorMap = new HashMap<String,Object>();
		ChunkFileInfoStorage.getInstance().remove(info);
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, private");

		ObjectMapper om = new ObjectMapper();

		FileUploadError error = new FileUploadError();
		error.setFileName(info.fileName);
		if (avScanResult != null && null != avScanResult.getException() && avScanResult.getException().getMessage().toLowerCase(Locale.ENGLISH).contains("timed out")){
			error.setFileError("File Name : " + info.fileName + ", Error : "+ "Connection timed out. Server is busy... Please try uploading the file after some time. " );
		}else if (avScanResult != null && null != avScanResult.getException()){
			error.setFileError("File Name : " + info.fileName + ", Error : "+ avScanResult.getException().getMessage() );
		}else{
			error.setFileError("File Name : " + info.fileName + ", Error : "+ "There was a problem uploading file. Please try again after some time. If the issue persists...contact GAO at protests@cbca.gov." );
		}
		
		errorMap.put("error",error);
		populateErrorResp(response, errorMap, om);
		
//		throw new InvalidFileException(error.getFileError());
	}



	/**
	 * @param request
	 * @param response
	 * @param info
	 * @param is2
	 * @param currentChunkNumber
	 * @param dto 
	 * @throws Exception 
	 */
	private void startFileUpload(HttpServletRequest request, HttpServletResponse response, ChunkFileInfo info,
			InputStream is2, int currentChunkNumber, FileUploadDTO dto) throws Exception {
		
		InetAddress IP = InetAddress.getLocalHost();
		RandomAccessFile raf = new RandomAccessFile(info.serverFilePath, "rw");
		AVScanClient avScanClient;
		AVScanResult avScanResult;
		raf.seek((currentChunkNumber - 1) * (long) info.chunkSize);

			long read = 0;
			long content_length = request.getContentLength();
			byte[] bytes = new byte[1024 * 1000];
			while (read < content_length) {
				int r = is2.read(bytes);
				if (r < 0) {
					
					break;
				}
				raf.write(bytes, 0, r);
				read += r;
			}
			raf.close();
			info.uploadedChunks.add(new ChunkFileInfo.CurrentFileUploadChunkNumber(
					currentChunkNumber));
			
			if (info.checkIfUploadFinished()) {
				
				if (!SFTP.isSftpConnectionValid()){
					throw new Exception("SFTP connection is not valid");
				}

				// Commenting out AV scan until CBCA decides on a command line tool and just using else block
//				if (!Util.getRemoteIp(request).contains("127.0.0.1")) {
//					avScanClient = new AVScanClient();
//					avScanResult = avScanClient.scan(info.serverFilePath);
//
//					System.out.println(avScanResult.toString());
//					if (avScanResult.isSucces()){
//
//						EPDS_FileUtils.encryptFileAndUploadToRemoteServer(new File(info.serverFilePath));
//						String filePath = info.serverFilePath;
//						EPDS_FileUtils.setFilePathListInSession(filePath, request,request.getParameter("fileIdentifierCode"),null);
//						ChunkFileInfoStorage.getInstance().remove(info);
//						response.getWriter().print("All finished.");
//					}else{
//
//						failedVirusScanResponse(response, info, avScanResult);
//
//					}
//				}else{
					EPDS_FileUtils.encryptFileAndUploadToRemoteServer(new File(info.serverFilePath));
					String filePath = info.serverFilePath;
					EPDS_FileUtils.setFilePathListInSession(filePath, request,request.getParameter("fileIdentifierCode"),null);
					ChunkFileInfoStorage.getInstance().remove(info);
					response.getWriter().print("All finished.");
//				}
				
				
			} else {
				response.getWriter().print("Upload");
			}
	}
	

	// we need this if we are going to implement resumable uploads
	/**
	 * This is not being Used... Need to confirm if we still need the ability to implement resumable uploads.
	 * 
	 * This allows uploads to automatically 
	 * resume uploading after a network connection is lost either locally or to the server. 
	 * Additionally, it allows for users to pause, resume and even recover uploads without 
	 * losing state because only the currently uploading chunks will be aborted, not the entire upload.
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/upload-epds-documents", method = RequestMethod.GET)
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		int currentChunkNumber = getCurrentFileChunkNumber(request);

		ChunkFileInfo info = getCurrentFileInfo(request);

		if (info.uploadedChunks
				.contains(new ChunkFileInfo.CurrentFileUploadChunkNumber(currentChunkNumber))) {
			response.getWriter().print("Uploaded."); // This Chunk has been Uploaded.
		} else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	/**
	 * @param request
	 * @return
	 */
	private int getCurrentFileChunkNumber(HttpServletRequest request) {
		return HttpUtils.toInt(request.getParameter("flowChunkNumber"), -1);
	}

	/**
	 * get Current File Info From the Request
	 * @param request : request contians all the params related to current file Info.
	 * @return ChunkFileInfo
	 * @throws ServletException
	 */
	private ChunkFileInfo getCurrentFileInfo(HttpServletRequest request)
			throws ServletException {
		int chunkSize = HttpUtils.toInt(request.getParameter("flowChunkSize"),
				-1);
		long totalSize = HttpUtils.toLong(
				request.getParameter("flowTotalSize"), -1);
		String uniqueIdentifier = request.getParameter("flowIdentifier");
		String fileName = request.getParameter("flowFilename");
		String relativeFilePath = request.getParameter("flowRelativePath");
		/*String attachmentType = request.getParameter("attachmentType");*/

		fileName = fileName.replace(" ", "_");
		relativeFilePath = relativeFilePath.replace(" ", "_");
		
		String storage_Path = EPDS_FileUtils.getStoragePath(request, null);

		new File(storage_Path).mkdirs();
		String fileStoragePath = new File(storage_Path, fileName)
				.getAbsolutePath() + ".temp";

		ChunkFileInfoStorage storage = ChunkFileInfoStorage.getInstance();

		ChunkFileInfo info = storage.get(chunkSize, totalSize,
				uniqueIdentifier, fileName, relativeFilePath, fileStoragePath);
		if (!info.vaild()) {
			storage.remove(info);
			throw new ServletException("Invalid request params.");
		}

		return info;
	}
	
}
