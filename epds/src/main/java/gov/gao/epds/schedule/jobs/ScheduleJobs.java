package gov.gao.epds.schedule.jobs;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import gov.gao.epds.dto.RemoveCaseDto;
import gov.gao.epds.filestorage.SFTP;
import gov.gao.epds.fileupload.utils.FileMaintainanceUtils;
import gov.gao.epds.persistence.entity.Protest_Dm_Info;
import gov.gao.epds.persistence.entity.Protest_Info;
import gov.gao.epds.service.DashboardService;
import gov.gao.epds.service.HomeService;
import gov.gao.epds.service.ProtestInfoService;
import gov.gao.epds.service.UserInfoService;
import gov.gao.epds.session.EpdsSession;
import gov.gao.epds.utils.GlobalParams;

/**
 * @author MHussaini
 *
 *
 */

/*
 * Cron expression is represented by six fields:
 * 
 * second, minute, hour, day of month, month, day(s) of week (*) means match any
 * 
 * (*)/X means "every X"
 ** 
 * "0 0 * * * *" = the top of every hour of every day. 
 * "(*)/10 * * * * *" = every ten seconds. 
 * "0 0 8-10 * * *" = 8, 9 and 10 o'clock of every day.
 * "0 0/30 8-10 * * *" = 8:00, 8:30, 9:00, 9:30 and 10 o'clock every day.
 * "0 0 9-17 * * MON-FRI" = on the hour nine-to-five weekdays 
 * "0 0 0 25 12 ?" = every Christmas Day at midnight
 */

@Component
public class ScheduleJobs {
	
	@Autowired
	private DashboardService dashboardService;
	
	@Autowired
	private ProtestInfoService protestInfoService;
	
	@Autowired
	private HomeService homeService;
	
	@Autowired
	private UserInfoService userInfoService;

	// Every 30 mins the task is scheduled
	/* @Scheduled(cron = "0 (*)/20 * * * ?") */
	@Scheduled(cron = "(*)/30 * * * * ?")
	public void checkForUserLastActivity() {

		if (EpdsSession.userIdToAttributeMap.size() > 0 && !EpdsSession.userIdToAttributeMap.isEmpty()) {

			for (Iterator<Entry<Integer, Map<String, Object>>> it = EpdsSession.userIdToAttributeMap.entrySet()
					.iterator(); it.hasNext();) {
				Entry<Integer, Map<String, Object>> entry = it.next();

				Map<String, Object> eachMapObject = entry.getValue();

				Date lastActiveRequest = (Date) eachMapObject.get("lastReqTimeStamp");

				Date currentTime = new Date();
				Duration duration = new Duration(new DateTime(currentTime), new DateTime(lastActiveRequest));
				Long differenceInMins = duration.getStandardMinutes();

				if (Math.abs(differenceInMins) >= 20) {
					it.remove();
				}

			}

		}

	}
	
	
	
	// 
		/**
		 * Runs Every day at 2:01 am the task is scheduled
		 * Automatically deletes all updaid transactions which are 15 days old
		 * @throws Exception
		 */
		@Scheduled(cron = "0 2 1 * * ?")
		public void autoDeleteFailedCases() {
			try {
				List<Protest_Info> allUnpaidTransactions = protestInfoService.getListOfAllCasesWithUnpaidTransactionStatus();
				
				for (Protest_Info eachProtestInfo : allUnpaidTransactions){
					
					RemoveCaseDto removeCaseDto = new RemoveCaseDto();
					removeCaseDto.setaNum(eachProtestInfo.getA_No());
					
					removeCaseDto.setReasonForDeletion("SYSTEM: Protest was 15 days old with UNPAID transaction status. ");
					
					dashboardService.removeCase(removeCaseDto);	
					
				}
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
	
		
		/**
		 * Runs the top of every hour of every day. 
		 *  The cron expression itself will run every hour, and it only calls the external script.
		 *  The external scripts itself will check for files older than 2 hours and shreds them 
		 */
//		@Scheduled(cron = "0 0 * * * *")
//		public void cleanupRemoteBackupFolder() {
//			SFTP.cleanupBackupDirectory();
//		}
	
		/**
		 * Runs Every day at 3:01 am the task is scheduled
		 * Automatically cleans up the JBOSS temp directory.
		 * 	Deletes all the files 30 days older and removes all empty directories
		 * @throws Exception
		 */
		@Scheduled(cron = "0 3 1 * * ?")
		public void cleanUpJBOSSTempDirectory (){
			
			try {
				FileMaintainanceUtils.recursiveDeleteFilesOlderThanNDays(10, GlobalParams.fileStorageBasePath);
				FileMaintainanceUtils.deleteEmptyFolders(GlobalParams.fileStorageBasePath);
			} catch (Exception ex) {
				System.out.println("Exception occured clean JBOSS");
				ex.printStackTrace();
			}
			
		}
		
		
		/*second, minute, hour, day of month, month, day(s) of week (*) means match any
		"0 0 9-17 * * MON-FRI" = on the hour nine-to-five weekdays */
		/*
		 * At 01:00:00am, on every Sunday, every month
		*/
		@Scheduled(cron = "0 1 1 * * *")
		public void deletedVerifiedCasesOlderThan10Days() {

			try {

				List<Protest_Dm_Info> allVerifiedCases = protestInfoService.getListOfAllVerifiedCasesOlderThan10Days();

				for (Protest_Dm_Info eachVerifiedCase : allVerifiedCases) {

					// Extra check

					Instant dbInstant = eachVerifiedCase.getDate_verified().toInstant();
					// changed to 10 days 200911
					Instant nDaysAgo = ZonedDateTime.now().minusDays(10).toInstant();

					boolean beforeLast10Days = dbInstant.isBefore(nDaysAgo);

					if (beforeLast10Days) {
						boolean isDirDeleted = SFTP.removeDir(eachVerifiedCase.getA_No(),false);

						if (isDirDeleted) {
							eachVerifiedCase.setDir_Del("Y");
							protestInfoService.updateDmInfo(eachVerifiedCase);
						}
						
						Thread.sleep(1000);

					}

				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
		
		
	
	//Every day at 2:01 am the task is scheduled
	/**
	 * 
	 * Upload all the local files from app server to database server
	 * @throws IOException
	 */
	/*@Scheduled(cron = "0 2 1 * * ?")
	public void uploadFilesToRemoteServer() throws IOException {

		SFTP.uploadDirectoryToRemoteServer();

	}*/
	
	
	/*@Scheduled(cron = "(*)/60 * * * * ?")
	public void clearCache() {

		if (GlobalParams.globalParam.size() > 0 && !GlobalParams.globalParam.isEmpty()) {

			GlobalParams.globalParam = new HashMap<String, Object>();//clear the cache and try again

		}

	}*/
	
	
	
	
	/**
	 * schedule job that remove the A numbers from GAO Supervisor and Agency Admin Email preferences if the cases has been completed
	 * review the code ...this works fine but right now it is generating huge records in the AUdit Log table
	 */
	
	/*@Scheduled(cron = "0 0 9-17 * * MON-FRI")
	public void removeANumsBasedOnClosedCaseStatus(){
		
		List<User_Info> userInfoList = userInfoService.getListOfSupervisorandAgencyPOCUserInfoList();
		
		
		for (User_Info eachUserInfo : userInfoList){
			
			userInfoService.updateAgencyPOCAndSupervisorEmailPreferences(eachUserInfo);
			
		}
		
		
	}*/

	
	
	
}
