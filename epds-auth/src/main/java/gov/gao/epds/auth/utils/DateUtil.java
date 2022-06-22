package gov.gao.epds.auth.utils;

import java.sql.Timestamp;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtil {
	private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);
	public static int getNumberOfDaysPassedAfterProvidedTimeStamp(
			Timestamp accountCreatedtimeStamp) {
		
		int timeElapsed = (int) (((new Date()).getTime() - accountCreatedtimeStamp
				.getTime()) / (1000 * 60 * 60 * 24));
		
				System.out.println(accountCreatedtimeStamp);
		System.out.println(timeElapsed);
		
		return timeElapsed;
	}

	/*public static boolean checkIfTheDateCrossedThreshold(Date dateToBeChecked,
			int thresholdTimeInMins) {
		long currentTimeInMilliSeconds = (new Date()).getTime();
		long dateToBeCheckedInMilliSeconds = dateToBeChecked.getTime();

		long thresholdTimeInMilliSeconds = thresholdTimeInMins * 60 * 1000;

		return (dateToBeCheckedInMilliSeconds - currentTimeInMilliSeconds) > thresholdTimeInMilliSeconds;
	}*/
	
	public static boolean checkIfTheDateCrossedThreshold(Date dateToBeChecked,
			int thresholdTimeInMins) {
		boolean isValid = false;
		
		DateTime currentTime = new DateTime();
		DateTime timeInsideToken = new DateTime(dateToBeChecked);
		Duration duration = new Duration(currentTime, timeInsideToken);
		Long differenceInMins  = duration.getStandardMinutes();
		
		logger.info("checkIfTheDateCrossedThreshold is expired currentTime={}, timeInsideToken ={}",
				currentTime.toLocalDateTime(), timeInsideToken.toLocalDateTime());
		
		logger.info("checkIfTheDateCrossedThreshold duration in minutes durartionInMins={}",differenceInMins);
		
		if (differenceInMins <= thresholdTimeInMins){
			isValid = true;
		}
		
		return isValid;
	}
}
