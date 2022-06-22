package gov.gao.epds.auth.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class GlobalParams {
	public static final Properties prop = new Properties();
	public static String propFileName;
	//public final static String propFileName = "epds.oireasd0110.properties";
	/*public final static String propFileName = "epds.oireasd0108.properties";*/
	public static Map<String, Object> globalParam = new HashMap<String, Object>();

	static {
		InetAddress IP;
		if (prop.size() == 0) {
			
			try {
				
				  IP = InetAddress.getLocalHost();
				  
				  //Dev
				if (IP.toString().contains("10.203.63.12")) {
					propFileName = "epds.oireasd0108.properties";

					//Test
				} else if (IP.toString().contains("10.203.65.12")) {
					propFileName = "epds.oireasd0110.properties";
					
					//local
				} else {
					propFileName = "epds.local.properties";
				}
				
				loadProperties(propFileName,prop);
			} catch (Exception e) {
				e.printStackTrace();
			}
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
