package gov.gao.epds.auth.utils;

import gov.gao.epds.auth.persistence.entity.Account_status;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class AuthParam {
	public static List<Account_status> accountStatusList = new ArrayList<Account_status>();
	public static String plcgEmail = "gaoplcguser@gmail.com";
	public static String epdsHelpDeskEmail = "epdshelpdesk@gmail.com";
	public static String baseEPDSRestURL = "";
	//public static String baseEPDSRestURL = "http://10.203.65.12:8080/epds/rest/auth-service/";
	//public static String baseEPDSRestURL = "http://10.203.63.12:8080/epds/rest/auth-service/";
	
	static {
		InetAddress IP;
		try {
			
			  IP = InetAddress.getLocalHost();
			  System.out.println
			  ("IP of my system is := "+ IP.getHostAddress()); 
			  
			  //Dev
			if (IP.toString().contains("10.203.63.12")) {
				baseEPDSRestURL = "http://10.203.63.12:8080/epds/rest/auth-service/";

				//Test
			} else if (IP.toString().contains("10.203.65.12")) {
				baseEPDSRestURL = "http://10.203.65.12:8080/epds/rest/auth-service/";
				
				//gaoepds005 test
			}else if (IP.toString().contains("10.102.107.139")) {
				baseEPDSRestURL = "http://10.102.107.139:8080/epds/rest/auth-service/";
				
				//local
			} else if (IP.toString().contains("10.102.108.136")) {
				baseEPDSRestURL = "http://10.102.108.136:8080/epds/rest/auth-service/";
				
				//local
			} else {
				baseEPDSRestURL = "http://localhost:8080/epds/rest/auth-service/";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

}
