package gov.gao.epds.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

public class GlobalParams {
	public static Properties prop = new Properties();
	public static InetAddress IP;
	public static final String PROD_IP_ADDR = "159.142.165.49";
	public final static String regexWithSpecialCharacters = "^[a-zA-Z0-9_-#.() ]*$";
	public final static String regexWithAlphaNumericChars = "^[a-zA-Z0-9]*$";
	public final static String regexWithAlphaChars = "^[a-zA-Z]*$";
	public final static String regexWithNumericChars = "^[0-9]*$";
	private static final String base_dir = System.getProperty("jboss.server.temp.dir"); 
	public static final   String fileStorageBasePath = base_dir + File.separator + "tmpFiles" + File.separator;
	public static String propFileName;
	//public final static String propFileName = "epds.oireasd0110.properties";
	/*public final static String propFileName = "epds.oireasd0108.properties";*/
	public static Map<String, Object> globalParam = new HashMap<String, Object>();
	
	public static Set<String> aNumbersSet = new TreeSet<String>();

	static {
		
		if (prop.size() == 0) {
			
			try {
				
				  IP = InetAddress.getLocalHost();
				//EPDS Dev GAO 005
				 if (IP.toString().contains("10.102.107.139")) {
					
					propFileName = "epds.gaoepds005.properties";
					
					//EPDS PRE-PROD GAO 0011
				}else if (IP.toString().contains("10.102.108.136")) {
					propFileName = "epds.gaoepds0011.properties";
					//PROD GAO --0012
				}else if (IP.toString().contains("199.134.74.73")) {
					propFileName = "epds.gaoepds0012.properties";
					//local
				}  else {
					propFileName = "epds.local.properties";
				}
				
				loadProperties();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void loadProperties() throws IOException {
	
		InputStream inputStream = null;
		String strPropFile = "D:" + File.separator + "EAP-7.4.0" + File.separator + "standalone" + File.separator + "properties" + File.separator + propFileName;

		File propFile = new File(strPropFile);
		
		if (propFile.exists()){
			System.out.println("Load Properties from " + strPropFile);
			inputStream = new FileInputStream(propFile);
		}else{
			inputStream = Thread.currentThread()
			.getContextClassLoader().getResourceAsStream(propFileName);
		}
		
		
		if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFileName
					+ "' not found in the classpath");	
		}
	}
	
	
	public static synchronized void loadProperties(String propFileName,Properties prop) throws IOException {
		
		InputStream inputStream = Thread.currentThread()
				.getContextClassLoader().getResourceAsStream(propFileName);
		if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFileName
					+ "' not found in the classpath");
		}
	}
}
