package gov.gao.epds.logging.ui;

import gov.gao.epds.persistence.entity.User_Info;


import org.slf4j.Logger;
/*import org.apache.log4j.Logger;*/
import org.slf4j.LoggerFactory;

/**
 * @author MHussaini
 *
 *It is used to capture the browser logs 
 */
public class LoggingUtil {
	private final static Logger logger = LoggerFactory.getLogger(LoggingUtil.class);
	
	public static void saveBrowserLogs(User_Info userInfo ,String jsonMessage){

        logger.info("Browser Logs ---> userInfo={}, jsonMessage ={}",
				userInfo, jsonMessage);
	}
	
	
}
