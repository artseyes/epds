package gov.gao.epds.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.gao.epds.dto.SubmitNewDocDTO;
import gov.gao.epds.filestorage.SFTP;
import gov.gao.epds.persistence.entity.File_Info;
import gov.gao.epds.persistence.entity.Protest_Info;
import gov.gao.epds.persistence.entity.User_Info;
import gov.gao.epds.service.CaseDocketSheetService;
import gov.gao.epds.service.DashboardService;
import gov.gao.epds.service.ProtestInfoService;
import gov.gao.epds.utils.EPDS_FileUtils;

/*
 This file has all the helper methods to create a zip file
 */
@Component
public class ZipFile_Util {

	private static Logger logger = Logger.getLogger(ZipFile_Util.class);
	@Autowired
	public static void setIndexNumberForPdfFile(List<File_Info> fileInfoList){
		setIndexNumbersForUseInCaseIndexPdfFile(fileInfoList);
	}

	public static SubmitNewDocDTO insertAllDataAndCreateTheFinalZipFile(HttpServletRequest request,
			List<String> intervenorCompanyNameList, List<Protest_Info> consolidatedProtestInfoList,
			Protest_Info protestInfo, String userId, List<File_Info> fileInfoList, User_Info attorney_Info,
			SubmitNewDocDTO submitNewDocDTO, CaseDocketSheetService caseDocketSheetService,
			ProtestInfoService protestInfoService, DashboardService dashboardService) throws Exception {

		/*ExecutorService threadpool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		List<Future<Boolean>> transfers = new ArrayList<>();
		boolean isSuccess = false;*/

		// a# being used in a path, if it has a '.', needs to be replaced with a '-' to match how file uploads work
		String a_No = protestInfo.getA_No().replaceAll(Pattern.quote("."), "-");
		String caseFolderRootPath = GlobalParams.fileStorageBasePath + a_No;
		File caseFolderDir = new File(caseFolderRootPath);
		caseFolderDir.mkdirs();

		String zipFileRootPath = GlobalParams.fileStorageBasePath + protestInfo.getB_No() + ".zip";

		File directoryToZip = new File(zipFileRootPath);

		String pdfPath = caseFolderRootPath + File.separator + "index.pdf";

		setIndexNumbersForUseInCaseIndexPdfFile(fileInfoList);

		List<File_Info> pdfFilePathList = new ArrayList<File_Info>();
		pdfFilePathList = protestInfoService.getDupsRemovedFileInfoList(fileInfoList);
		dashboardService.populateAgencyNameForAgencyInFileInfoList(pdfFilePathList);
		CaseDocketPDFUtil.createPDF(pdfPath, pdfFilePathList, caseDocketSheetService, protestInfo,
				intervenorCompanyNameList, attorney_Info,false, "Case Closeout PDF");

		ZipFile_Util.createZipFile(fileInfoList, directoryToZip, pdfPath);

        FileUtils.moveFileToDirectory(directoryToZip, new File(caseFolderRootPath), true);

		logger.info("---Done");

		submitNewDocDTO = populateSubmitNewDocWithCaseRelatedData(protestInfo, userId, caseFolderRootPath);

		File zipFilePath = new File(submitNewDocDTO.getFilepath());
		EPDS_FileUtils.encryptFileAndUploadToRemoteServer(zipFilePath);

		EPDS_FileUtils.secureDeleteFile(directoryToZip);

		return submitNewDocDTO;

	}

	/**
	 * @param fileInfoList
	 */
	private static void setIndexNumbersForUseInCaseIndexPdfFile(List<File_Info> fileInfoList) {

		List<File_Info> zipFilePathList = new ArrayList<File_Info>(fileInfoList);

		double lastIndexNumber = 1.0;
		int index = 1;
		File_Info eachFileInfo, eachFileInfo2, priorEachFileInfo=null;

		sortFileInfoListBySubmissionDateAndFileId(fileInfoList);



		for (int i = 0; i < zipFilePathList.size(); i++) {
			eachFileInfo = zipFilePathList.get(i);

			if (priorEachFileInfo != null && !priorEachFileInfo.getOriginalSubmissionDate().equalsIgnoreCase(eachFileInfo.getOriginalSubmissionDate())) {
				index++;
				lastIndexNumber = index;
			}

			for (int j = i; j < zipFilePathList.size(); j++) {

				eachFileInfo2 = zipFilePathList.get(j);

				if (eachFileInfo.getOriginalSubmissionDate().equalsIgnoreCase(eachFileInfo2.getOriginalSubmissionDate())
						&& eachFileInfo2.getFile_identifier().equalsIgnoreCase("P")
						&& eachFileInfo2.getIndexNum() == null) {
					eachFileInfo2.setIndexNum(String.valueOf(index));
//					index++;
				} else if (eachFileInfo.getOriginalSubmissionDate().equalsIgnoreCase(eachFileInfo2.getOriginalSubmissionDate())
						&& eachFileInfo2.getFile_identifier().equalsIgnoreCase("A")
						&& eachFileInfo2.getIndexNum() == null) {
					lastIndexNumber += 0.01;
					eachFileInfo2.setIndexNum(String.valueOf(CaseDocketPDFUtil.round(lastIndexNumber, 2)));

				}
			}

			if (eachFileInfo.getIndexNum() == null) {
				eachFileInfo.setIndexNum(String.valueOf(index));
//				index++;
			}

			priorEachFileInfo = eachFileInfo;
//			lastIndexNumber = index;
		}
	}

	/**
	 * @param fileInfoList
	 */
	public static void sortFileInfoListBySubmissionDateAndFileId(List<File_Info> fileInfoList) {
		Collections.sort(fileInfoList, new Comparator<File_Info>() {
			DateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy HH:mm:ss z");

			@Override
			public int compare(File_Info fileInfo1, File_Info fileInfo2) {

				int compare = 0;

				try {

					compare = dateFormat.parse(fileInfo1.getOriginalSubmissionDate())
							.compareTo(dateFormat.parse(fileInfo2.getOriginalSubmissionDate()));

					if (compare == 0)
						compare = String.valueOf(fileInfo1.getFile_Id())
						.compareTo(String.valueOf(fileInfo2.getFile_Id()));

				} catch (ParseException e) {
					e.printStackTrace();
				}

				return compare;

			}
		});
	}

	private static SubmitNewDocDTO populateSubmitNewDocWithCaseRelatedData(Protest_Info protest_Info, String user_Id,
			String caseFolderRootPath) throws Exception {
		SubmitNewDocDTO submitNewDocDTO = new SubmitNewDocDTO();
		submitNewDocDTO.setSubmissionDate(Date_Util.getCurrentDate());
		submitNewDocDTO.setProtestId(protest_Info.getA_No());
		submitNewDocDTO.setDocId(158);
		submitNewDocDTO.setUser_Id(user_Id);
		submitNewDocDTO.setTypeofdocument("ZIP");
		submitNewDocDTO.setIsDocConfidential("N");
		submitNewDocDTO.setUser_Role("GAO ADMIN");
		submitNewDocDTO.setFilepath(caseFolderRootPath + File.separator + protest_Info.getB_No() + ".zip");

		return submitNewDocDTO;
	}

	public static void createZipFile(List<File_Info> fileInfoList, File directoryToZip, String pdfPath)
			throws IOException, ParseException {

		List<File_Info> pdfFileInfoList = new ArrayList<File_Info>(fileInfoList);
		SFTP sftp = new SFTP();

		if (directoryToZip.exists()) {
			EPDS_FileUtils.secureDeleteFile(directoryToZip, true);
			directoryToZip.createNewFile();
		} else {
			directoryToZip.createNewFile();
		}

		File_Info pdfFileInfo = new File_Info();

		pdfFileInfo.setFile_Path(pdfPath);
		pdfFileInfo.setFile_identifier("P");
		pdfFileInfo.setDocTypeName("indexPdf");
		pdfFileInfo.setSubmission_Date(Date_Util.getCurrentDate());
		pdfFileInfo.setOriginalSubmissionDate(pdfFileInfo.getSubmission_Date());
		pdfFileInfoList.add(pdfFileInfo);

		File pdfCaseDocketSheet = new File(pdfPath);
		ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(directoryToZip));

		try {

			DateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy HH:mm:ss z");

			File_Info eachFileInfo;

			for (int i = 0; i < pdfFileInfoList.size(); i++) {
				ZipEntry entry;
				eachFileInfo = pdfFileInfoList.get(i);

				File eachFile = new File(eachFileInfo.getFile_Path());
				
				
				/*
				 * It shouldn't include 
				 * 1) if it is a directory
				 * 2) if it is a minute entry
				 * 3) if it is a previous existing case closeout zip file
				 */
				
				if (eachFile.isDirectory()
						|| eachFileInfo.getFile_Path().equalsIgnoreCase("/")
						|| eachFileInfo.getFile_Path().equalsIgnoreCase("")
						|| eachFileInfo.getFile_Path().equalsIgnoreCase("C:\\")
						|| eachFileInfo.getDoc_Type_Id() == 158) {
					continue;
				}


				if (null != eachFileInfo.getDocTypeName()
						&& eachFileInfo.getDocTypeName().equalsIgnoreCase("indexPdf")) {

					entry = new ZipEntry("index.pdf");

				} else {
					entry = new ZipEntry(CaseDocketPDFUtil.getFormattedString(eachFileInfo.getIndexNum()) + "_"
							+ FilenameUtils.getBaseName(eachFile.getName()) + "."
							+ FilenameUtils.getExtension(eachFile.getName()));

					entry.setTime(dateFormat.parse(eachFileInfo.getOriginalSubmissionDate()).getTime());
				}

				zipOutputStream.putNextEntry(entry);

				if (null != eachFileInfo.getDocTypeName()
						&& eachFileInfo.getDocTypeName().equalsIgnoreCase("indexPdf")) {

					FileInputStream in = new FileInputStream(eachFile);
					try {
						IOUtils.copy(in, zipOutputStream);
					} finally {
						IOUtils.closeQuietly(in);
					}

				} else {

					sftp.download(eachFile, zipOutputStream);

				}

				zipOutputStream.closeEntry();
			}

		} finally {
			IOUtils.closeQuietly(zipOutputStream);
		}

		EPDS_FileUtils.secureDeleteFile(pdfCaseDocketSheet, true);

		if (pdfCaseDocketSheet.exists()) {
			EPDS_FileUtils.secureDeleteFile(pdfCaseDocketSheet);
		}

	}
}
