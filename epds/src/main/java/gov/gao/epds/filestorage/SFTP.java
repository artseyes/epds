/**
 *
 */

package gov.gao.epds.filestorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.Security;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.util.StringUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import gov.gao.epds.utils.EPDS_FileUtils;
import gov.gao.epds.utils.GlobalParams;
import gov.gao.epds.utils.PropertyFileEncrypter;

/**
 * @author MHussaini
 *
 */
public class SFTP {

	/*
	 * Below we have declared and defined the SFTP HOST, PORT, USER and Local
	 * private key from where you will make connection
	 */

	private final static String SFTPHOST = PropertyFileEncrypter.decrypt(GlobalParams.prop.getProperty("sftpHost"));
	private final static Integer SFTPPORT = Integer
			.parseInt(PropertyFileEncrypter.decrypt(GlobalParams.prop.getProperty("sftpPort")));

	private final static String SFTPUSER = PropertyFileEncrypter.decrypt(GlobalParams.prop.getProperty("sftpUser"));
	private final static String SFTPPASS = PropertyFileEncrypter.decrypt(GlobalParams.prop.getProperty("sftpPass"));
	
	// Windows supports forward slashes for ssh/sftp paths
	private final static String SFTP_TMP_BKUP_PATH = "D:/UpFiles/backups";
	
	private final static String EPDS_SHRED_CMD = "$HOME/bin/epds_shred ";
	
	private final static String APP_BACKUPS_SHRED_CMD = "$HOME/bin/app_backup_shred ";

	

	private static String privateKey = PropertyFileEncrypter
			.decrypt(GlobalParams.prop.getProperty("sftpPrivateKeyLoc"));
	/* private static String SFTPWORKINGDIR = "/tmp/app/epds/"; */
	// sftp requires an absolute path, must start with /. (/app/... or /d:/...)
	private static String SFTPWORKINGDIR = PropertyFileEncrypter
			.decrypt(GlobalParams.prop.getProperty("sftpWorkingDir"));

	public static Session getSftpSession() {

		JSch jSch = new JSch();
		Session session = null;
		Properties config = new Properties();
		try {
			InetAddress IP = InetAddress.getLocalHost();

			session = jSch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);

			if (IP.toString().contains("199.134.74.73")) {
				jSch.addIdentity(privateKey);
				System.out.println("Private Key Added.");

			} else {
				// adding this to be able to locally and with password on dev
				// and test servers
				session.setPassword(SFTPPASS);
			}

			config.put("PreferredAuthentications", "publickey,keyboard-interactive,password");
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);

		} catch (JSchException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return session;

	}

	public static synchronized boolean uploadToServer(File localFile) {

		if (!localFile.exists()) {
			return false;
		}
		boolean isUploadSuccess = false;
		String localFileBasePath = GlobalParams.fileStorageBasePath;
		// while testing locally the file jboss file path will be null so we
		// neet to hardcode it
//		if (!GlobalParams.fileStorageBasePath.contains("jboss-eap")) {
//			localFileBasePath = "C:/Users/mhussaini/devstudio/runtimes/jboss-eap/standalone/tmp/tmpFiles";
//		} else {
//			localFileBasePath = GlobalParams.fileStorageBasePath;
//		}

		String relativePath = getRelativePath(localFileBasePath, localFile.getAbsolutePath());

		Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;
		try {

			session = getSftpSession();
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			System.out.println("Shell channel connected....");
			channelSftp = (ChannelSftp) channel;

			channelSftp.cd(SFTPWORKINGDIR);

			System.out.println("Changed the directory...");
			prepareUpload(channelSftp, relativePath);
			InputStream fis = null;
			try {
				System.out.println(localFile.getAbsolutePath());
				fis = new FileInputStream(localFile);
				channelSftp.put(fis, channelSftp.pwd() + "/" + localFile.getName());
				isUploadSuccess = true;
			} finally {
				if (null != fis) {
					IOUtils.closeQuietly(fis);
				}
			}

			// if successfully uploaded then delete the local file

			// localFile.delete();

		} catch (Exception e) {
			isUploadSuccess = false;
			e.printStackTrace();
		} /*
			 * catch (JSchException e) { e.printStackTrace(); } catch
			 * (SftpException e) { e.printStackTrace(); } catch
			 * (FileNotFoundException e) { e.printStackTrace(); } catch
			 * (IOException e) { e.printStackTrace(); }
			 */finally {
			if (channelSftp != null) {
				channelSftp.disconnect();
				channelSftp.exit();
			}
			if (channel != null)
				channel.disconnect();

			if (session != null)
				session.disconnect();
		}

		return isUploadSuccess;

	}

	public static void uploadDirectoryToRemoteServer() {

		String localFileBasePath = GlobalParams.fileStorageBasePath;
		// while testing locally the file jboss file path will be null so we
		// neet to hardcode it
//		if (!GlobalParams.fileStorageBasePath.contains("jboss-eap")) {
//			localFileBasePath = "C:/Users/mhussaini/devstudio/runtimes/jboss-eap/standalone/tmp/tmpFiles";
//		} else {
//			localFileBasePath = GlobalParams.fileStorageBasePath;
//		}

		Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;
		try {

			session = getSftpSession();
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			System.out.println("Shell channel connected....");
			channelSftp = (ChannelSftp) channel;

			channelSftp.cd(SFTPWORKINGDIR);

			System.out.println("Changed the directory...");

			try (Stream<Path> paths = Files.walk(Paths.get(GlobalParams.fileStorageBasePath))) {
				List<File> filesInFolder = paths.filter(Files::isRegularFile).map(Path::toFile)
						.collect(Collectors.toList());

				for (File eachLocalFile : filesInFolder) {
					System.out.println(eachLocalFile.getAbsolutePath());
					String relativePath = getRelativePath(localFileBasePath, eachLocalFile.getAbsolutePath());
					prepareUpload(channelSftp, relativePath);
					InputStream fis = null;
					try {
						System.out.println(eachLocalFile.getAbsolutePath());
						fis = new FileInputStream(eachLocalFile);
						channelSftp.put(fis, channelSftp.pwd() + "/" + eachLocalFile.getName());

						if (eachLocalFile.exists()){
							EPDS_FileUtils.secureDeleteFile(eachLocalFile);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally {
						if (null != fis) {
							IOUtils.closeQuietly(fis);
						}
					}
				}
			}

			// if successfully uploaded then delete the local file

			// localFile.delete();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (channelSftp != null) {
				channelSftp.disconnect();
				channelSftp.exit();
			}
			if (channel != null)
				channel.disconnect();

			if (session != null)
				session.disconnect();
		}

		return;

	}

	public static boolean isSftpConnectionValid() {

		Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;
		boolean isConnectionValid = false;

		try {

			session = getSftpSession();
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			System.out.println("Shell channel connected...., dir: " + SFTPWORKINGDIR);
			channelSftp = (ChannelSftp) channel;
			channelSftp.cd(SFTPWORKINGDIR);
			isConnectionValid = true;

		} catch (Exception e) {
			e.printStackTrace();
			isConnectionValid = false;
		} finally {
			if (channelSftp != null) {
				channelSftp.disconnect();
				channelSftp.exit();
			}
			if (channel != null)
				channel.disconnect();

			if (session != null)
				session.disconnect();
		}

		return isConnectionValid;

	}
	
	
	

	private static String getRelativePath(String localFileBasePath, String absoluteFilePath) {

		return new File(localFileBasePath).toURI().relativize(new File(absoluteFilePath).toURI()).getPath();
	}

	public void download(File localFile, java.io.OutputStream outputStream) {
		Security.addProvider(new BouncyCastleProvider());

		
		String relativePath = getFilePathRelativeToRemoteSvr(localFile);


		InputStream fileInputStream = null;
		FileStorageEncryption fileStorageEncryption = new FileStorageEncryption();
		Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;
		Key key;

		try {
			key = FileStorageCryptoUtils.getSecurityKey();
			session = getSftpSession();
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			System.out.println("shell channel connected....");
			channelSftp = (ChannelSftp) channel;

			channelSftp.cd(SFTPWORKINGDIR);

			if (localFile.exists()) {
				fileInputStream = new FileInputStream(localFile);
			} else if (!localFile.exists()) {
				// below line is mostly useful while testing locally since
				// locally we have windows and on server it is linux.....
				relativePath = relativePath.replace("\\", "/");// whether
																// testing
																// locally or
																// from server
																// this
				fileInputStream = channelSftp.get(SFTPWORKINGDIR + relativePath);
			}
			if (fileInputStream != null) {
				fileStorageEncryption.decrypt(fileInputStream, outputStream, key);
			}

		} catch (JSchException e) {
			e.printStackTrace();
		} catch (SftpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (channelSftp != null) {
				channelSftp.disconnect();
				channelSftp.exit();
			}
			if (channel != null)
				channel.disconnect();

			if (session != null)
				session.disconnect();

			if (fileInputStream != null) {
				IOUtils.closeQuietly(fileInputStream);
			}
		}

		/* return fileInputStream; */
		return;
	}

	private static String getFilePathRelativeToRemoteSvr(File localFile) {
		
		String localFileBasePath = GlobalParams.fileStorageBasePath;
		
		String relativePath;
		if (File.separator.contains("\\")) {
			String[] filePaths = StringUtils.split(localFile.getAbsolutePath(), "tmpFiles");
			relativePath = filePaths[1];
		} else {
			relativePath = getRelativePath(localFileBasePath, localFile.getAbsolutePath());
		}
		return relativePath;
	}

	public static boolean removeFile(File localFile) {
		
		boolean isSuccess = false;
		
		if (!localFile.getAbsolutePath().contains("tmpFiles")){
			return true;
		}
		
		String relativePath = getFilePathRelativeToRemoteSvr(localFile);;

        // below line is mostly useful while testing locally since locally
        // we have windows and on server it is linux.....
        relativePath = relativePath.replace("\\", "/");// whether testing
                                                        // locally or from
                                                        // server this

		// check for dangerous string that could cause a root path to be passed for removal
		if (relativePath.equals("") || relativePath.equals("/"))
			return true;

		// commenting for CBCA for now. backing up of files won't currently work without changes/conversion of backup script
		// and the backup really was for a GAO corner case to keep files around for an extra hour to make sure they were backed up by tape
//		isSuccess = backupFiles(relativePath,getSftpSession(),false);

//		if (isSuccess){
			System.out.println("removeFile : " + SFTPWORKINGDIR +  relativePath);
			// shred won't work until needed/converted. specifying remote windows command
//			String cmd = EPDS_SHRED_CMD + SFTPWORKINGDIR + "/" + relativePath;//		String cmd = "rm " + SFTPWORKINGDIR +  relativePath;
//			isSuccess = runRemoteSFTPCommand(cmd, getSftpSession());
//		}
		Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;
		boolean isConnectionValid = false;

		try {

			session = getSftpSession();
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			System.out.println("Shell channel connected...., dir: " + SFTPWORKINGDIR);
			channelSftp = (ChannelSftp) channel;
			channelSftp.rm(SFTPWORKINGDIR +  relativePath);
			isSuccess = true;

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (channelSftp != null) {
				channelSftp.disconnect();
				channelSftp.exit();
			}
			if (channel != null)
				channel.disconnect();

			if (session != null)
				session.disconnect();
		}
			return isSuccess;
	}

	/**
	 * method to remove remote directory
	 * @param isBackupNeeded 
	 *
	 * @param relativeFilePath - relative to @SFTPWORKINGDIR
	 * 
	 * @Example:
	 *  if path /app/epds/A-XXXXX then just pass A-XXXXX
	 * 
	 * @return
	 */
	public static boolean removeDir(String relativeFilePath, boolean isBackupNeeded) {

		boolean isSuccess = false;
		
		// check for dangerous string that could cause a root path to be passed for removal
		if (relativeFilePath.equals("") || relativeFilePath.equals("/") || relativeFilePath.equals("\\"))
			return true;

		// commenting for CBCA for now. backing up of files won't currently work without changes/conversion of backup script
		// and the backup really was for a GAO corner case to keep files around for an extra hour to make sure they were backed up by tape
//		if (isBackupNeeded){
//			isSuccess = backupFiles(relativeFilePath,getSftpSession(),true);
//		}else{
			isSuccess = true;
//		}

        if (isSuccess){
        	System.out.println("secureRemove : " + SFTPWORKINGDIR + "\\" + relativeFilePath);
			// shred won't work until needed/converted. specifying remote windows command
//			String cmd = EPDS_SHRED_CMD + SFTPWORKINGDIR + "/" + relativeFilePath;
			String cmd = "rmdir /q /s " + SFTPWORKINGDIR + "\\" + relativeFilePath;
			isSuccess = runRemoteSFTPCommand(cmd, getSftpSession());
		}


		return isSuccess;
	}
	
	

	private boolean isDirectory(String remoteDirectory, ChannelSftp sftpChannel) throws SftpException {
		return sftpChannel.stat(remoteDirectory).isDir();
	}

	public static void prepareUpload(ChannelSftp sftpChannel, String relativeFilePath)
			throws SftpException, IOException, FileNotFoundException {

		// Build romote path subfolders inclusive:
		String[] folders = relativeFilePath.split("/");
		for (String folder : folders) {
			if (folder.length() > 0 && !folder.contains(".")) {
				// This is a valid folder:
				try {
					sftpChannel.cd(folder);
					System.out.println("Current Dir : " + sftpChannel.pwd());
				} catch (SftpException e) {
					// No such folder yet:
					sftpChannel.mkdir(folder);
					sftpChannel.cd(folder);
				}
			}
		}

	}

	
	public static boolean backupFiles(String relativePath, Session session, boolean isDir) {
		
		boolean isSuccess = false;
		
		if (!relativePath.startsWith("/")){
			relativePath = "/" + relativePath;
		}
		
		String backupFilePath = SFTP_TMP_BKUP_PATH + relativePath;
		
		String copyCmd = "xcopy " + (isDir ? " /E " : "");
		
	    String cmd = copyCmd +  SFTPWORKINGDIR + relativePath  + " " +  backupFilePath;
	    
	    //prepare backup directory
	    isSuccess = prepareBackupDir(backupFilePath);
	    
	    
	    if (isSuccess){
	    	isSuccess = runRemoteSFTPCommand(cmd, session); 	
	    }
	     
	    return isSuccess;
	}

	
	public static void main(String[] args) {
		
		String filePath = FilenameUtils.getFullPathNoEndSeparator("/app/backups/sub1/sub2/sub3/abc.pdf");
		
		System.out.println(filePath);
	}
	
	
	private static boolean prepareBackupDir(String backupFilePath) {
		
		
		String basePath = FilenameUtils.getFullPathNoEndSeparator(backupFilePath);
		String cmd = "mkdir " +  basePath;
		boolean isSuccess = runRemoteSFTPCommand(cmd, getSftpSession());
		
		return isSuccess;
		
	}
	
	
	public static void cleanupBackupDirectory(){
		runRemoteSFTPCommand(APP_BACKUPS_SHRED_CMD,getSftpSession());
	}

	public static boolean runRemoteSFTPCommand(String cmd, Session session) {
		//	    String cmd = "/export/home/S_GAOEPDS-SFTP/bin/epds_shred " + path;
	    boolean isSuccess = false;
	    Channel channel = null;

	    System.out.println("commandExecuting-----> : " + cmd);
	    try {
    	    session.connect();
    	    channel = session.openChannel("exec");
    	    ((ChannelExec)channel).setCommand(cmd);
			channel.setInputStream(null);
    	    ((ChannelExec) channel).setErrStream(System.err);
            InputStream in = channel.getInputStream();

            System.out.println("commandExecuting : Connect to session...");
    	    channel.connect();

    	    byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    System.out.print(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    System.out.println("commandExecuting: exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }
            isSuccess = true;
	    } catch (JSchException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (channel != null)
                channel.disconnect();

            if (session != null)
                session.disconnect();
        }
	    return isSuccess;
	}
}
