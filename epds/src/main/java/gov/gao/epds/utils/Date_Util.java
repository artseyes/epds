package gov.gao.epds.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Date_Util {

	private static final String zoneId = "America/New_York";
	/*
	 * public static String getCurrentDate() { Date currentDate = new Date();
	 * SimpleDateFormat dateFormat = new SimpleDateFormat(
	 * "EEE MMM dd HH:mm:ss z yyyy");
	 * 
	 * TimeZone estTime = TimeZone.getTimeZone("EST");
	 * dateFormat.setTimeZone(estTime);
	 * 
	 * return dateFormat.format(currentDate); }
	 */
	
	public static ZonedDateTime getESTTimeStamp(String dateInString){

        LocalDateTime ldt = LocalDateTime.parse(dateInString, DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm:ss z"));
        ZoneId newYokZoneId = ZoneId.of(zoneId);
        ZonedDateTime nyDateTime = ldt.atZone(newYokZoneId);
        
        return nyDateTime;
    
	}
	
	public static Long getESTTimeStampInLong(String dateInString){

        ZonedDateTime nyDateTime = getESTTimeStamp(dateInString);
        
        return nyDateTime.toInstant().toEpochMilli();
    
	}

	public static String getCurrentDate() {
		Date currentDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy HH:mm:ss z");

		TimeZone estTime = TimeZone.getTimeZone(zoneId);
		dateFormat.setTimeZone(estTime);

		return dateFormat.format(currentDate);
	}

	public static String getCurrentDate(boolean isMMDDYY) {
		Date currentDate = new Date();
		SimpleDateFormat dateFormat;

		if (isMMDDYY) {
			dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		} else {
			dateFormat = new SimpleDateFormat("MMM dd yyyy HH:mm:ss z");
		}

		TimeZone estTime = TimeZone.getTimeZone(zoneId);
		dateFormat.setTimeZone(estTime);

		return dateFormat.format(currentDate);
	}

	public static String convertToMMMDDYYYYHHMMSSZFormat(String dateInMMDDYYYY) throws ParseException {
		TimeZone estTime = TimeZone.getTimeZone(zoneId);
		DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		format.setTimeZone(estTime);

		Date date = format.parse(dateInMMDDYYYY);

		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy HH:mm:ss z");

		dateFormat.setTimeZone(estTime);

		return dateFormat.format(date);
	}

	public static String convertToSpecifiedDateFormatIfNotAlready(String oldDate, String newDateFormat) {
		DateFormat format1 = new SimpleDateFormat("MM/dd/yyyy");
		DateFormat format2 = new SimpleDateFormat("MMM dd yyyy HH:mm:ss z");
		TimeZone estTime = TimeZone.getTimeZone(zoneId);
		format1.setTimeZone(estTime);
		format2.setTimeZone(estTime);

		Date date = getDate(format1, oldDate);
		if (date == null) {
			date = getDate(format2, oldDate);
		}

		if (date == null) {
			return oldDate;
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat(newDateFormat);

		dateFormat.setTimeZone(estTime);

		return dateFormat.format(date);
	}

	public static Date getDate(DateFormat format1, String oldDate) {
		Date date = null;
		try {
			date = format1.parse(oldDate);
		} catch (ParseException e) {
			// e.printStackTrace();
		}

		return date;
	}

	public static String convertToSpecifiedFormat(String oldDate, String oldDateFormat, String newDateFormat)
			throws ParseException {
		TimeZone estTime = TimeZone.getTimeZone(zoneId);
		DateFormat format = new SimpleDateFormat(oldDateFormat);
		format.setTimeZone(estTime);

		Date date = format.parse(oldDate);

		SimpleDateFormat dateFormat = new SimpleDateFormat(newDateFormat);

		dateFormat.setTimeZone(estTime);

		return dateFormat.format(date);
	}

	public static String getCurrentDate(String format) {
		Date currentDate = new Date();
		SimpleDateFormat dateFormat;

		dateFormat = new SimpleDateFormat(format);

		TimeZone estTime = TimeZone.getTimeZone(zoneId);
		dateFormat.setTimeZone(estTime);

		return dateFormat.format(currentDate);
	}

	/**
	 * Convert Date with timestamp set to start time of the day Example :
	 * converts 06/25/2018 16:18:15 EDT to 06/25/2018 00:00:00 EDT
	 * 
	 * @param dateString
	 */
	public static String getDateWithTimeSetToStartTimeOfTheDay(String dateString) {
		String dateTimeFormat = "MM/dd/yyyy HH:mm:ss z";
		dateString = convertToSpecifiedDateFormatIfNotAlready(dateString, "MM/dd/yyyy HH:mm:ss z");
		DateTimeFormatter DATEFORMATTER = DateTimeFormatter.ofPattern(dateTimeFormat);
		LocalDateTime ldt = LocalDateTime.parse(dateString, DATEFORMATTER);
		ZoneId newYokZoneId = ZoneId.of(zoneId);
		System.out.println("TimeZone : " + newYokZoneId);
		ZonedDateTime zoneDateTime = ldt.atZone(newYokZoneId);

		System.out.println("Date (New York) : " + DATEFORMATTER.format(zoneDateTime.truncatedTo(ChronoUnit.DAYS)));

		return DATEFORMATTER.format(zoneDateTime.truncatedTo(ChronoUnit.DAYS));
	}
	
	public static String getDateWithTimeSetToEndTimeOfTheDay(String dateString) {
		String dateTimeFormat = "MM/dd/yyyy HH:mm:ss z";
		dateString = convertToSpecifiedDateFormatIfNotAlready(dateString, dateTimeFormat);
		DateTimeFormatter DATEFORMATTER = DateTimeFormatter.ofPattern(dateTimeFormat);
		LocalDateTime ldt = LocalDateTime.parse(dateString, DATEFORMATTER);
		ZoneId newYokZoneId = ZoneId.of(zoneId);
		System.out.println("TimeZone : " + newYokZoneId);
		ZonedDateTime zoneDateTime = ldt.atZone(newYokZoneId);
		zoneDateTime = zoneDateTime.with(LocalTime.MAX);
		System.out.println("Date (New York) : " + DATEFORMATTER.format(zoneDateTime));

		return DATEFORMATTER.format(zoneDateTime);
	}

	public static String agencyReportDueDate(String submissionDate, String caseType) throws ParseException {

		int numOfDays = 30;

		if (caseType.equalsIgnoreCase("entitlement") || caseType.toUpperCase(Locale.ENGLISH).contains("ENT")
				|| caseType.equalsIgnoreCase("cost-claim") || caseType.toUpperCase(Locale.ENGLISH).contains("COST")) {
			numOfDays = 15;
		}

		return getDueDate(submissionDate, numOfDays);
	}

	public static String getDueDate(String submissionDate, int days) throws ParseException {
		
		PublicHoliday.setPublicHolidayDatesAfterCurrentDate();
		
		ZonedDateTime nyDateTime = getESTTimeStamp(submissionDate);//get the date in EST/EDT time
	
		//if the submission date time is after 5:30pm EST then move it to the next day 
		if(nyDateTime.getHour() > 17 || (nyDateTime.getHour() == 17 && nyDateTime.getMinute() > 30))  {
			nyDateTime = nyDateTime.plusDays(1);
		}
		
		Calendar submissionDateCal = getCalendar(nyDateTime);
		//if the date falls on weekend or any public holiday move to the next business day
		moveDateToNextBusinessDay(submissionDateCal);
		// add 'x' day(s) to the submission date to get the due date
		submissionDateCal.add(Calendar.DATE, days);
		moveDateToNextBusinessDay(submissionDateCal);
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		return sdf.format(submissionDateCal.getTime());
	}

	/**
	 * @param calendar
	 */
	public static void moveDateToNextBusinessDay(Calendar calendar) {
		boolean isLastDayHoliday = PublicHoliday.findIfHoliday(calendar);
		int numberOfHolidaysSkipped = 0;
		while (isLastDayHoliday) {
			calendar.add(Calendar.DATE, 1);
			isLastDayHoliday = PublicHoliday.findIfHoliday(calendar);
			numberOfHolidaysSkipped++;
		}

		System.out.println("Total number of holidays skipped : " + numberOfHolidaysSkipped);
	}

	/**
	 * @param nyDateTime
	 * @return
	 */
	public static Calendar getCalendar(ZonedDateTime nyDateTime) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, nyDateTime.getDayOfMonth());
		cal.set(Calendar.YEAR, nyDateTime.getYear());
		cal.set(Calendar.MONTH, nyDateTime.getMonth().getValue() - 1);
		return cal;
	}

	public static int getNumberOfDaysRemaining(String dueDateInString) {
		PublicHoliday.setPublicHolidayDatesAfterCurrentDate();

		Calendar iteratorCalendar = Calendar.getInstance();
		iteratorCalendar.setTime(new Date());
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		// boolean isHoliday = false;

		System.out.println(sdf.format(iteratorCalendar.getTime()));
		if ((sdf.format(iteratorCalendar.getTime()).equals(dueDateInString)))
			return 0;
		int numberOfDaysRemaining = 0;
		int count = 0;
		while (!sdf.format(iteratorCalendar.getTime()).equals(dueDateInString)) {
			// System.out.println(sdf.format(iteratorCalendar.getTime()));

			iteratorCalendar.add(Calendar.DATE, 1);
			/*
			 * isHoliday = PublicHoliday.findIfHoliday(iteratorCalendar); if
			 * (!isHoliday) { numberOfBusinessDays++; }
			 */

			numberOfDaysRemaining++;
			count++;

			if (count > 200) { // need to be revisited later; it is implemented
								// to avoid infinite loop scenario
				break;
			}

		}

		if (numberOfDaysRemaining > 100)
			numberOfDaysRemaining = 100;

		return numberOfDaysRemaining;
	}

	// ascending order
	public static int compareDate(String submission_Date, String submission_Date2) {
		String[] dateParams = submission_Date.split(" ");
		String[] dateParams2 = submission_Date2.split(" ");

		if (dateParams[5].compareTo(dateParams2[5]) != 0) {
			return dateParams[5].compareTo(dateParams2[5]);
		} else if (dateParams[1].compareTo(dateParams2[1]) != 0) {
			String month1 = convertToNumericalMonth(dateParams[1]);
			String month2 = convertToNumericalMonth(dateParams2[1]);
			return month1.compareTo(month2);
		} else if (dateParams[2].compareTo(dateParams2[2]) != 0) {
			return dateParams[2].compareTo(dateParams2[2]);
		} else if (dateParams[3].compareTo(dateParams2[3]) != 0) {
			return dateParams[3].compareTo(dateParams2[3]);
		} else if (dateParams[0].compareTo(dateParams2[0]) != 0) {
			String day1 = convertToNumericalDay(dateParams[0]);
			String day2 = convertToNumericalDay(dateParams2[0]);
			return day1.compareTo(day2);
		}

		return 0;
	}

	private static String convertToNumericalDay(String day) {
		if (day.equalsIgnoreCase("SUN")) {
			return "1";
		}
		if (day.equalsIgnoreCase("MON")) {
			return "2";
		}
		if (day.equalsIgnoreCase("TUE")) {
			return "3";
		}
		if (day.equalsIgnoreCase("WED")) {
			return "4";
		}
		if (day.equalsIgnoreCase("THU")) {
			return "5";
		}
		if (day.equalsIgnoreCase("FRI")) {
			return "6";
		} else
			return "7";

	}

	private static String convertToNumericalMonth(String month) {
		if (month.equalsIgnoreCase("JAN")) {
			return "01";
		}
		if (month.equalsIgnoreCase("FEB")) {
			return "02";
		}
		if (month.equalsIgnoreCase("MAR")) {
			return "03";
		}
		if (month.equalsIgnoreCase("APR")) {
			return "04";
		}
		if (month.equalsIgnoreCase("MAY")) {
			return "05";
		}
		if (month.equalsIgnoreCase("JUN")) {
			return "06";
		}
		if (month.equalsIgnoreCase("JUL")) {
			return "07";
		}
		if (month.equalsIgnoreCase("AUG")) {
			return "08";
		}
		if (month.equalsIgnoreCase("SEP")) {
			return "09";
		}
		if (month.equalsIgnoreCase("OCT")) {
			return "10";
		}
		if (month.equalsIgnoreCase("NOV")) {
			return "11";
		} else
			return "12";
	}

	public static String getCurrentDatePlusOrMinusSpecifiedInDays(int numOfDays, String dateFormatInString) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, numOfDays);

		DateFormat dateFormat = new SimpleDateFormat(dateFormatInString);
		TimeZone estTime = TimeZone.getTimeZone(zoneId);
		dateFormat.setTimeZone(estTime);

		System.out.println(calendar.getTime());

		return dateFormat.format(calendar.getTime());
	}

	private static Date convertToDate(String submission_Date, String dateFormatInString) throws ParseException {
		TimeZone estTime = TimeZone.getTimeZone(zoneId);
		DateFormat format = new SimpleDateFormat(dateFormatInString);
		format.setTimeZone(estTime);

		Date date = format.parse(submission_Date);

		return date;
	}

	private static int getNumberOfDaysBetweenTwoDates(Date newerDate, Date olderDate) {
		return (int) ((newerDate.getTime() - olderDate.getTime()) / (1000 * 60 * 60 * 24));
	}

	public static int getNumberOfDaysOld(String submission_Date, String dateFormatInString) {
		int numberOfDaysOld = 0;

		try {
			Date submissionDate = convertToDate(submission_Date, dateFormatInString);
			numberOfDaysOld = Date_Util.getNumberOfDaysBetweenTwoDates(new Date(), submissionDate);
		} catch (Exception e) {
			// need to come back
		}

		return numberOfDaysOld;
	}

}
