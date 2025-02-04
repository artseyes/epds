package gov.gao.epds.auth.scheduler;

import java.util.List;

import gov.gao.epds.auth.dao.Login_attempt_dao;
import gov.gao.epds.auth.dao.User_info_dao;
import gov.gao.epds.auth.persistence.entity.Login_attempt;
import gov.gao.epds.auth.persistence.entity.User_event_log;
import gov.gao.epds.auth.persistence.entity.User_info;
import gov.gao.epds.auth.service.EmailService;
import gov.gao.epds.auth.service.LoginService;
import gov.gao.epds.auth.utils.PolicyParam;
import gov.gao.epds.auth.utils.Util;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author MHussaini
 *
 * Spring task scheduler that runs every day at 1am and check which User Accounts 
 * needs to be deactivated or permanently removed.
 * 
 * 
 * Amer : Need to come back to this . We probably need to also implement a 10 Days warning to the User
 * whose  account is going to be deactivated or deleted. 
 * The email template for Account Deactivated and Account Delete Warning is not yet finalized.. need to discuss.
 * 
 * 
 */

/*Cron expression is represented by six fields:

second, minute, hour, day of month, month, day(s) of week
(*) means match any

(*)/X means "every X"

** "0 0 * * * *" = the top of every hour of every day.
* "(*)/10 * * * * *" = every ten seconds.
* "0 0 8-10 * * *" = 8, 9 and 10 o'clock of every day.
* "0 0/30 8-10 * * *" = 8:00, 8:30, 9:00, 9:30 and 10 o'clock every day.
* "0 0 9-17 * * MON-FRI" = on the hour nine-to-five weekdays
* "0 0 0 25 12 ?" = every Christmas Day at midnight
*/

@Service
public class AccountActivityScheduler {
 
	private final static Logger logger = LoggerFactory
			.getLogger(LoginService.class);
	@Autowired
	User_info_dao user_info_dao;
	
	@Autowired
	Login_attempt_dao  login_attempt_dao;
	
	@Autowired
	EmailService emailService;
	
	@Autowired
	LoginService loginService;
	
	//Every day at 1:01 am the task is scheduled
	@Scheduled(cron = "0 1 1 * * ?")
    public void checkForUserLastActivity() {
        
		/*
		 * @Todo: Select * from EPDS_AUTH.USER_INFO a , EPDS_AUTH.LOGIN_ATTEMPT b where a.EMAIL= b.USER_EMAIL and a.EMAIL = 'epds@cbca.gov'  and b.TIME_STAMP > (getdate()-243);
		 * 
		 * Amer : refactor this so we dont need to load all the system user everytime.. need to only load user who doesn't have any records in last PolicyParam.numberOfDaysToRemoveAccount
		*/
		List<User_info>  listOfUsers = user_info_dao.getListOfAllUsers();
        
		for (User_info eachUserInfo : listOfUsers) {
			Login_attempt eachUserLastSuccessfullLoginAttempt = login_attempt_dao.getLastSuccessfulLoginAttempt(eachUserInfo.getEmail());
		
			
			if (eachUserLastSuccessfullLoginAttempt == null){
				continue;
			}
			
			
        	DateTime currentTime = new DateTime();
    		DateTime lastSuccessLogin = new DateTime(eachUserLastSuccessfullLoginAttempt.getTime_stamp());
    		Duration duration = new Duration(lastSuccessLogin, currentTime);
    		Long differenceInDays  = duration.getStandardDays();
    		Long numOfDaysLeft = PolicyParam.numberOfDaysToRemoveAccount - Math.abs(differenceInDays) ;
    		
    		
    		if (Math.abs(differenceInDays) >= PolicyParam.numberOfDaysToRemoveAccount){
    			
    			try {
    				//account is permanentaly deleted, the record will still be archived to user Info audit log table
					user_info_dao.setUserEventLog(eachUserInfo.getUser_id(), 7);
					user_info_dao.delete(eachUserInfo);
					emailService.sendAccountDeletionWarningOrConfirmation(eachUserInfo.getEmail(),-1,"confirmation");
				} catch (Exception e) {
					e.printStackTrace();
				}
    		}else if (numOfDaysLeft > 0  
    					&& numOfDaysLeft <= 10 
    					&& (numOfDaysLeft == 3 || numOfDaysLeft == 6 || numOfDaysLeft == 9)){
    			emailService.sendAccountDeletionWarningOrConfirmation(eachUserInfo.getEmail(),numOfDaysLeft.intValue(),"warning");
    		}
        
		}
        
        
    }
}
