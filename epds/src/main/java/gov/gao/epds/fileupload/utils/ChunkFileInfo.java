package gov.gao.epds.fileupload.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.io.FilenameUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import gov.gao.epds.dto.FileStorage;
import gov.gao.epds.filestorage.FileStorageEncryption;
import gov.gao.epds.filestorage.SFTP;
import gov.gao.epds.utils.EPDS_FileUtils;

public class ChunkFileInfo {

	public int chunkSize;
	public long totalSize;
	public String uniqueIdentifier;
	public String fileName;
	public String relativeFilePath;
	public String serverFilePath;
	public HashSet<CurrentFileUploadChunkNumber> uploadedChunks = new HashSet<CurrentFileUploadChunkNumber>();

	public static class CurrentFileUploadChunkNumber {

		public CurrentFileUploadChunkNumber(int number) {
			this.number = number;
		}

		public int number;

		@Override
		public boolean equals(Object obj) {
			return obj instanceof CurrentFileUploadChunkNumber ? ((CurrentFileUploadChunkNumber) obj).number == this.number
					: false;
		}

		@Override
		public int hashCode() {
			return number;
		}
	}

	public boolean vaild() {
		if (chunkSize < 0 || totalSize < 0
				|| HttpUtils.isEmpty(uniqueIdentifier)
				|| HttpUtils.isEmpty(fileName)
				|| HttpUtils.isEmpty(relativeFilePath)) {
			return false;
		} else {
			return true;
		}
	}


	/**
	 * check if file upload is finished...i.e if all the chunks of a particular file has been uploaded.
	 *
	 * https://codereview.stackexchange.com/questions/41041/creating-a-thread-for-file-transfer
	 * @return
	 */
	public boolean checkIfUploadFinished() {

		/*ExecutorService threadpool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		List<Future<Boolean>> transfers = new ArrayList<>();
		Boolean isSuccess = false;*/
		String fileExtension = "";
		String new_path = "";
		File newFile = null;

		int count = (int) Math
				.ceil(((double) totalSize) / ((double) chunkSize));

		for (int i = 1; i < count; i++) {

			if (!uploadedChunks.contains(new CurrentFileUploadChunkNumber(i))) {

				return false;
			}
		}

		File file = new File(serverFilePath);
		fileExtension = FilenameUtils.getExtension(file.getName());
		if ( null != fileExtension && fileExtension.equals("temp")){

			new_path = file.getAbsolutePath().substring(0,
					file.getAbsolutePath().length() - ".temp".length());
			newFile = new File(new_path);
			if (newFile.exists()){
				EPDS_FileUtils.secureDeleteFile(newFile, true);
			}
			file.renameTo(newFile);

			serverFilePath = new_path;
		}


		System.out.println("file path for scanning" + serverFilePath);

		/*File newFile = new File(new_path);
		fileExtension = FilenameUtils.getExtension(newFile.getName());

		if(fileExtension.endsWith(".temp")){

			System.out.println("file name endss with tmp--->");
			file.renameTo(newFile);
			System.out.println("file name endss with tmp--->" + file.getAbsolutePath());
		}*/



		/*isSuccess =  FileStorageEncryption.encryptFile(newFile);

		//check if encryption is successfull

		 * Amer : Temporarily we will store both on app server and DB server eventually we will only upload on DB server
		 * later on uncomment isSuccess  once testing is done
		 *
		if (isSuccess){
			//upload to the remote server

			threadpool.submit(new Callable<Boolean>() {
				public Boolean call() throws IOException {
					return SFTP.uploadToServer(newFile);
				}
			});
		}
		threadpool.shutdown();
		transfers.add();*/

		return true;
	}


}
