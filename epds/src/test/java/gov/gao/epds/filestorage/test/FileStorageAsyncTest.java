package gov.gao.epds.filestorage.test;

import java.io.File;

import gov.gao.epds.filestorage.SFTP;
import gov.gao.epds.utils.EPDS_FileUtils;

/**
 * @author MHussaini
 *
 */
public class FileStorageAsyncTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		/*File file = new File(
				"C:/Users/mhussaini/devstudio/runtimes/jboss-eap/standalone/tmp/tmpFiles/MyTesting/testing123.txt");*/

		 SFTP.removeDir("MyTesting/testing123.txt",false); 
		
		/*if (file.isDirectory()){
			
			File[] files = file.listFiles();
			//CompletableFuture<?>[] completableFutures = new CompletableFuture<?>[files.length];
			for (int i=0; i < files.length;i++){
				TimeWatch watch = TimeWatch.start();
				//completableFutures [i] = encryptFileAndUploadToRemoteServer(files[i]);
				EPDS_FileUtils.encryptFileAndUploadToRemoteServer(files[i]);
			    
			}
			
			//CompletableFuture.allOf(completableFutures).join();
		}*/
		

	}

}
