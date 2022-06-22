package gov.gao.epds.filestorage.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import gov.gao.epds.filestorage.SFTP;

public class SFTPTest {

	/*
	 * Below we have declared and defined the SFTP HOST, PORT, USER and Local
	 * private key from where you will make connection
	 */

	/*private final static String SFTPHOST = "10.203.65.13";
	private final static int SFTPPORT = 22;
	private final static String SFTPUSER = "S_EPDS-SFTP";
	private final static String SFTPPASS = "P@ssword!ktg";
	// this file can be id_rsa or id_dsa based on which algorithm is used to
	// create the key
	private static String privateKey = "/home/phussm01/.ssh/id_rsa";
	private static String SFTPWORKINGDIR = "/app/test";*/
	
	private final static String SFTPHOST = /*"10.203.65.13"*/ "10.102.107.140";
	private final static int SFTPPORT = 22;
	private final static String SFTPUSER = "XXXX_SFTP_USER";
	private final static String SFTPPASS = "XXXX_SFTP_PASS";
	// this file can be id_rsa or id_dsa based on which algorithm is used to
	// create the key
	private static String privateKey = "/home/phussm01/.ssh/id_rsa";
	private static String SFTPWORKINGDIR = "/app/epds/";

	Session session = null;
	Channel channel = null;
	ChannelSftp channelSftp = null;

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		JSch jSch = new JSch();
		Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;
		try {

			//uncomment this if using private key
			
			/*jSch.addIdentity(privateKey);
			System.out.println("Private Key Added.");
			session = jSch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
			System.out.println("Session created.");*/

			session = jSch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
			session.setPassword(SFTPPASS);
			
			
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			
			// adding this to be able to locally
			config.put("PreferredAuthentications", "publickey,keyboard-interactive,password");
			session.setConfig(config);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			System.out.println("Shell channel connected....");
			channelSftp = (ChannelSftp) channel;

			channelSftp.cd("/app/test");
			System.out.println(channelSftp.pwd());
			System.out.println("Changed the directory...");
			//SFTP.prepareUpload(channelSftp, SFTPWORKINGDIR);
			
			
		} catch (JSchException e) {
			e.printStackTrace();
		} catch (SftpException e) {
			e.printStackTrace();
		} /*catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/ finally {
			if (channelSftp != null) {
				channelSftp.disconnect();
				channelSftp.exit();
			}
			if (channel != null)
				channel.disconnect();

			if (session != null)
				session.disconnect();
		}
	}
	
	public void cp (Session session, String source, String target) throws Exception {
	   
		System.out.println("COMMAND: cp " + source + " " + target);

	    if (!session.isConnected()) {
	    	System.out.println("Session is not connected");
	        throw new Exception("Session is not connected...");
	    }
	    Channel upChannel = null;
	    Channel downChannel = null;
	    ChannelSftp uploadChannel = null;
	    ChannelSftp downloadChannel = null;
	    try {
	        upChannel = session.openChannel("sftp");
	        downChannel = session.openChannel("sftp");
	        upChannel.connect();
	        downChannel.connect();
	        uploadChannel = (ChannelSftp) upChannel;
	        downloadChannel = (ChannelSftp) downChannel;
	        /*FileProgressMonitor monitor = new FileProgressMonitor();*/
	        InputStream inputStream = uploadChannel.get(source);
	        
	        //need to decrypt the input stream...
	        downloadChannel.put(inputStream, target/*, monitor*/);
	    } catch (JSchException e) {
	       e.printStackTrace();
	        throw new Exception(e);
	    } finally {
	        if (upChannel == null || downChannel == null) {
	            System.out.println("Channel is null ...");
	        }else if (uploadChannel != null && !uploadChannel.isClosed()){
	            uploadChannel.exit();
	            downloadChannel.exit();
	            uploadChannel.disconnect();
	            downloadChannel.disconnect();
	        }else if (!upChannel.isClosed()) {
	            upChannel.disconnect();
	            downChannel.disconnect();
	        }
	        session.disconnect();
	    }
	}

}
