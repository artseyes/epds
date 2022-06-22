package gov.gao.epds.auth.utils;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PolicyParam {
	private static final Logger logger = LoggerFactory.getLogger(PolicyParam.class);
	public static final Properties prop = new Properties();
	static {
		try {
			GlobalParams.loadProperties("policy.param.properties",prop);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Exception occured while loading file ={}","policy.param.properties",e);
		}
	}
	public static final Integer maxLoginAttemptTimePeriodInMins = Integer.parseInt(prop.getProperty("maxLoginAttemptTimePeriodInMins").trim());
	// public static Integer maxNumberOfUnsuccessfulClientIpAttempt;
	public static Integer maxNumberOfUnsuccessfulLoginAttempt = Integer.parseInt(prop.getProperty("maxNumberOfUnsuccessfulLoginAttempt").trim());
	// public static Integer numberOfLastMinsToCountLastLoginAttemptByClientIp;
	public static Integer numberOfDaysToExpirePassword = Integer.parseInt(prop.getProperty("numberOfDaysToExpirePassword").trim());
	public static Integer numberOfDaysToNotifyUserOfPasswordExpiration = Integer.parseInt(prop.getProperty("numberOfDaysToNotifyUserOfPasswordExpiration").trim());
	public static Integer numberOfMinsToExpireSelfResetTempPassword = Integer.parseInt(prop.getProperty("numberOfMinsToExpireSelfResetTempPassword").trim());
	public static Integer numberOfDaysToExpireNonVendorTempPassword = Integer.parseInt(prop.getProperty("numberOfDaysToExpireNonVendorTempPassword").trim());
	public static Integer numberOfDaysToExpireVendorTempPassword = Integer.parseInt(prop.getProperty("numberOfDaysToExpireVendorTempPassword").trim());
	public static Integer numberOfDaysToExpireTempPwd = Integer.parseInt(prop.getProperty("numberOfDaysToExpireTempPwd").trim());
	public static Integer maxUserInactivityTimeInMins = Integer.parseInt(prop.getProperty("maxUserInactivityTimeInMins").trim());
	public static Integer timeToRenewTokenInMins = Integer.parseInt(prop.getProperty("timeToRenewTokenInMins").trim());
	public static Integer numberOfDaysToDisableAccount = Integer.parseInt(prop.getProperty("numberOfDaysToDisableAccount").trim());
	public static Integer numberOfDaysToRemoveAccount = Integer.parseInt(prop.getProperty("numberOfDaysToRemoveAccount").trim());

}
