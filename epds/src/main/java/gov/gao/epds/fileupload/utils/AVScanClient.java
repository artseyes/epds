package gov.gao.epds.fileupload.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class AVScanClient {

	private String filePath;

	private StringWriter infos;
    private StringWriter errors;

	public static void main(String[] args) throws IOException {

		String jbossTempPath = "/app/jboss-eap-7.0/standalone/tmp/tmpFiles/eicar.com";
		AVScanClient avScanClient = new AVScanClient();
		avScanClient.scan(jbossTempPath);

	}

	/**
	 * @param jbossTempPath temporary filePath where the file is stored
	 * @throws IOException
	 */
	public AVScanResult scan(String jbossTempPath) throws IOException {
		infos = new StringWriter();
        errors = new StringWriter();
        File homeDirectory = new File("/app/home/appadmin");
		AVScanResult avScanResult = null;

		List<String> commands = new ArrayList<String>();
		commands.add("/usr/bin/nice");
		commands.add("/app/uvscan/uvscan");
		commands.add("--unzip");
		commands.add("-c");
		commands.add(jbossTempPath);

		ProcessBuilder processBuilder = new ProcessBuilder(commands);

		/*if (homeDirectory.exists()){
			processBuilder.directory(new File("/app/home/appadmin"));
		}*/

		Process process = processBuilder.start();

		 StreamThread streamInfo = new StreamThread(process.getInputStream(), new PrintWriter(infos, true));
	     StreamThread streamError = new StreamThread(process.getErrorStream(), new PrintWriter(errors, true));


		try {
				streamInfo.start();
		        streamError.start();
		        // Wait to get exit value
		        int exitValue = process.waitFor();
		        avScanResult = processErrorCodes(avScanResult,exitValue);
		        streamInfo.join();
		        streamError.join();

			System.out.println("\n\nExit Value is " + exitValue);
		} catch (InterruptedException e) {
			e.printStackTrace();
			avScanResult = new AVScanResult(e,null,null, false);
		}

		return avScanResult;
	}

	/**
	 * @param avScanResult
	 * @ExitCodes
	 * Code Description
	 * 0 The scanner found no viruses or other potentially unwanted software and returned no errors.
	 * 2 Integrity check on a DAT file failed.
	 * 6 A general problem occurred.
	 * 8 The scanner could not find a DAT file.
	 * 12 The scanner tried to clean a file, and that attempt failed for some reason, and the file is still infected.
	 * 13 The scanner found one or more viruses or hostile objects - such as a Trojan-horse program, joke program, or test file.
	 * 15 The scanner's self-check failed; it may be infected or damaged.
	 * 19 The scanner succeeded in cleaning all infected files.
	 *
	 * @TODO: Currently if the return code is 0 then it is a success else for all other cases just ask the user to re-upload the file..
	 * maybe later on for 2,8,15 we might need to send email to system admins if McAfee is not working
	 *
	 * @param exitValue
	 * @return
	 */
	private AVScanResult processErrorCodes(AVScanResult avScanResult, int exitValue) {
		avScanResult = new AVScanResult();

		boolean isSuccess = false;
		String exitCodeDesc = "";

		switch (exitValue) {

		case 0:
			isSuccess = true;
			exitCodeDesc = "The scanner found no viruses or other potentially unwanted software and returned no errors.";
			break;
		case 2:
			exitCodeDesc = "Integrity check on a DAT file failed. ";
			break;
		case 6:
			exitCodeDesc = "A general problem occurred.";
			break;
		case 8:
			exitCodeDesc = "The scanner could not find a DAT file.";
			break;
		case 12:
			exitCodeDesc = "The scanner tried to clean a file, and that attempt failed for some reason, and the file is still infected.";
			break;
		case 13:
			exitCodeDesc = "The scanner found one or more viruses or hostile objects - such as a Trojan-horse program, joke program, or test file";
			break;
		case 15:
			exitCodeDesc = "The scanner's self-check failed; it may be infected or damaged.";
			break;
		case 19:
			exitCodeDesc = "The scanner succeeded in cleaning all infected files.";
			break;
		default:
			break;
		}

		avScanResult = new AVScanResult(exitValue,exitCodeDesc, isSuccess);
		return avScanResult;
	}

	public String getFilePath() {
		return filePath;
	}

}

class StreamThread extends Thread {
	private InputStream in;
	private PrintWriter pw;

	StreamThread(InputStream in, PrintWriter pw) {
		this.in = in;
		this.pw = pw;
	}

	@Override
	public void run() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = br.readLine()) != null) {
				pw.println(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
