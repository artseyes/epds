package gov.gao.epds.fileupload.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import gov.gao.epds.utils.EPDS_FileUtils;

public class FileMaintainanceUtils {
	public static void main(String[] args) throws IOException {
		//deleteEmptyFolders("C:\\Users\\MHussaini\\jboss-eap-7.0\\standalone\\tmp\\tmpFiles");
		
		//recursiveDeleteFilesOlderThanNDays(10, "C:\\Users\\MHussaini\\Downloads\\testinDelete");
		//deleteEmptyFolders("C:\\Users\\MHussaini\\Downloads\\testinDelete");
	}

	
	public static void recursiveDeleteFilesOlderThanNDays(int days, String dirPath) throws IOException {
	    long cutOff = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000);
	    
	    try (Stream<Path> files = Files.list(Paths.get(dirPath))){
	    	
	    	files.forEach(path -> {
		        if (Files.isDirectory(path)) {
		            try {
		                recursiveDeleteFilesOlderThanNDays(days, path.toString());
		            } catch (IOException e) {
		                System.out.println("Exception occurred trying to delete file --- " + path);
		            }
		        } else {
		            try {
		                if (Files.getLastModifiedTime(path).to(TimeUnit.MILLISECONDS) < cutOff) {
		                	EPDS_FileUtils.secureDeleteFile(path.toString());
		                	Thread.sleep(1000);
		                	//Files.delete(path);
		                }
		            } catch (Exception ex) {
		                // log here and move on
		            	System.out.println("Exception occurred trying to delete file --- " + path);
		            }
		        }
		    });
	    }
	    
	}
	public static void deleteEmptyFolders(String folderName) throws FileNotFoundException, InterruptedException {
		File aStartingDir = new File(folderName);
		List<File> emptyFolders = new ArrayList<File>();
		findEmptyFoldersInDir(aStartingDir, emptyFolders);
		List<String> fileNames = new ArrayList<String>();
		for (File f : emptyFolders) {
			String s = f.getAbsolutePath();
			fileNames.add(s);
		}
		for (File f : emptyFolders) {
			boolean isDeleted = f.delete();
			Thread.sleep(1000);
			//EPDS_FileUtils.secureDeleteFile(f.getPath());
			if (isDeleted) {
				System.out.println(f.getPath() + " deleted");
			}
		}
	}

	public static boolean findEmptyFoldersInDir(File folder, List<File> emptyFolders) {
		boolean isEmpty = false;
		
		
		File[] filesAndDirs = folder.listFiles();
		List<File> filesDirs = Arrays.asList(filesAndDirs);
		if (filesDirs.size() == 0) {
			isEmpty = true;
		}
		if (filesDirs.size() > 0) {
			boolean allDirsEmpty = true;
			boolean noFiles = true;
			for (File file : filesDirs) {
				if (!file.isFile()) {
					boolean isEmptyChild = findEmptyFoldersInDir(file, emptyFolders);
					if (!isEmptyChild) {
						allDirsEmpty = false;
					}
				}
				if (file.isFile()) {
					noFiles = false;
				}
			}
			if (noFiles == true && allDirsEmpty == true) {
				isEmpty = true;
			}
		}
		if (isEmpty) {
			emptyFolders.add(folder);
		}
		return isEmpty;
	}
}