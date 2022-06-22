package gov.gao.epds.zipfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

public class ZipFileTest {

	public static void main(String[] args) throws Exception {
		/*ZipUtil.pack(new File("demo"), new File("demo.zip"));*/
		
		/*File src = new File("");
	    byte[] bytes = ZipUtil.packEntry(src);
	    
	    try {
			File zipFile = File.createTempFile("B-12345", "zip");
			
			 boolean processed = ZipUtil.handle(new ByteArrayInputStream(bytes), "TestFile_1.txt", new ZipEntryCallback() {

			      public void process(InputStream in, ZipEntry zipEntry) throws IOException {
			    	  
			      }
			    });
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		//testUnpackEntryFromFile();
		//testPackEntryFileWithNameParameter();
		
		File dir = new File("C:/Users/mhussaini/GAO/zipfileTest/testFolder");
		
		File dest = new File("C:/Users/mhussaini/GAO/zipfileTest/B-123.zip");
	    
	    if (dest.exists()){
	    	dest.delete();
	    }else{
	    	dest.createNewFile();
	    }
    
	ZipOutputStream out = new ZipOutputStream(new FileOutputStream(dest));

	try {
	  /*File[] files = dir.listFiles();*/
		List<File> fileList = new ArrayList<File>();
		getAllFiles(dir, fileList);
		
	  for (int i = 0; i < fileList.size(); i++) {
	    File file = fileList.get(i);

	    if (file.isDirectory()){
	    	continue;
	    }
	    
	    ZipEntry entry = new ZipEntry(FilenameUtils.getBaseName(file.getName()) 
	    		+ "_" + "changed" 
	    		+ "." +  FilenameUtils.getExtension(file.getName()));
	    
	    /*entry.setSize(file.length());
	    entry.setTime(file.lastModified());*/
	    
	    out.putNextEntry(entry);
	    FileInputStream in = new FileInputStream(file);
	    try {
	      IOUtils.copy(in, out);
	    } finally {
	      IOUtils.closeQuietly(in);
	    }
	    out.closeEntry();
	  }
	} finally {
	  IOUtils.closeQuietly(out);
	}

	}
	
	
	
	/*
	 * Get reference to all the files
	 */

	public static void getAllFiles(File dir, List<File> fileList) {

		try {
			File[] files = dir.listFiles();
			
			if (fileList !=null && files.length > 0)
			for (File file : files) {
				fileList.add(file);
				
				if (file.isDirectory()) {
					System.out.println("directory:" + file.getCanonicalPath());
					getAllFiles(file, fileList);
				} else {
					System.out.println("     file:" + file.getCanonicalPath());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
	public static void testPackEntryFileWithNameParameter() throws Exception {
	    File fileToPack = new File("C:/Users/mhussaini/GAO/zipfileTest/TestFile.txt");
	    
	    File dest = new File("C:/Users/mhussaini/GAO/zipfileTest/B-123.zip");
	    
	   /* if (dest.exists()){
	    	dest.delete();
	    }else{
	    	dest.createNewFile();
	    }*/
	    
	   // ZipUtil.packEntry(fileToPack, dest, FilenameUtils.getBaseName(fileToPack.getName()) + "_" + "index" + "2");
	   /* assertTrue(dest.exists());*/

	    /*ZipUtil.explode(dest);*/
	    /*assertTrue((new File(dest, "TestFile-II.txt")).exists());
	    // if fails then maybe somebody changed the file contents and did not update
	    // the test
	    assertEquals(108, (new File(dest, "TestFile-II.txt")).length());*/
	  }
	
	
	
	public static void testUnpackEntryFromFile() throws IOException {
	    final String name = "foo";
	    final byte[] contents = "bar".getBytes();


	    File file = File.createTempFile("B-123", ".zip", new File("C:/Users/mhussaini/GAO/zipfileTest"));
	    
	    try {
	    	
	      // Create the ZIP file
	      ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file));
	      try {
	        zos.putNextEntry(new ZipEntry(name));
	        zos.write(contents);
	        zos.closeEntry();
	      }
	      finally {
	        IOUtils.closeQuietly(zos);
	      }

	    }
	    finally {
	      /*FileUtils.deleteQuietly(file);*/
	    }
	  }
	
}
