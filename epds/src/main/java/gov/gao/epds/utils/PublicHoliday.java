package gov.gao.epds.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PublicHoliday {
	private static Date newYearsDayObserved = null;
	private static Date martinLutherKingObserved = null;
	private static Date presidentsDayObserved = null;
	private static Date memorialDayObserved = null;
	private static Date independenceDayObserved = null;
	private static Date laborDayObserved = null;
	private static Date columbusDayObserved = null;
	private static Date veteransDayObserved = null;
	private static Date thanksgivingObserved = null;
	private static Date christmasDayObserved = null;
	public static int year;

	public static void setPublicHolidayDatesAfterCurrentDate() {
		Date currentDate = Calendar.getInstance().getTime();

		int currentYear = currentDate.getYear();
		if (currentYear > 1900) {
			currentYear -= 1900;
		}

		newYearsDayObserved = getNewYearsDayObserved(currentYear);
		if (newYearsDayObserved.before(currentDate)) {
			newYearsDayObserved = getNewYearsDayObserved(currentYear + 1);
		}
		martinLutherKingObserved = getMartinLutherKingObserved(currentYear);
		if (martinLutherKingObserved.before(currentDate)) {
			martinLutherKingObserved = getMartinLutherKingObserved(currentYear + 1);
		}
		presidentsDayObserved = getPresidentsDayObserved(currentYear);
		if (presidentsDayObserved.before(currentDate)) {
			presidentsDayObserved = getPresidentsDayObserved(currentYear + 1);
		}
		memorialDayObserved = getMemorialDayObserved(currentYear);
		if (memorialDayObserved.before(currentDate)) {
			memorialDayObserved = getMemorialDayObserved(currentYear + 1);
		}
		independenceDayObserved = getIndependenceDayObserved(currentYear);
		if (independenceDayObserved.before(currentDate)) {
			independenceDayObserved = getIndependenceDayObserved(currentYear + 1);
		}
		laborDayObserved = getLaborDayObserved(currentYear);
		if (laborDayObserved.before(currentDate)) {
			laborDayObserved = getLaborDayObserved(currentYear + 1);
		}
		columbusDayObserved = getColumbusDayObserved(currentYear);
		if (columbusDayObserved.before(currentDate)) {
			columbusDayObserved = getColumbusDayObserved(currentYear + 1);
		}
		veteransDayObserved = getVeteransDayObserved(currentYear);
		if (veteransDayObserved.before(currentDate)) {
			veteransDayObserved = getVeteransDayObserved(currentYear + 1);
		}
		thanksgivingObserved = getThanksgivingObserved(currentYear);
		if (thanksgivingObserved.before(currentDate)) {
			thanksgivingObserved = getThanksgivingObserved(currentYear + 1);
		}
		christmasDayObserved = getChristmasDayObserved(currentYear);
		if (christmasDayObserved.before(currentDate)) {
			christmasDayObserved = getChristmasDayObserved(currentYear + 1);
		}

		year = currentYear;
	}

	@SuppressWarnings("deprecation")
	public static Date getNewYearsDayObserved(int currentYear) {
		int day;
		int month = 0; // January
		int monthDecember = 11; // December
		Date date;

		date = new Date(currentYear, month, 1);
		day = date.getDay();

		switch (day) {
		case 0: // Sunday
			return new Date(currentYear, month, 2);
		case 1: // Monday
		case 2: // Tuesday
		case 3: // Wednesday
		case 4: // Thursday
		case 5: // Friday
			return new Date(currentYear, month, 1);
		default:
			// Saturday, then observe on friday of previous year
			return new Date(--currentYear, monthDecember, 31);
		}
	}

	@SuppressWarnings("deprecation")
	public static Date getMartinLutherKingObserved(int year) {
		// Third Monday in January
		int day;
		int month = 0; // January
		Date date;

		date = new Date(year, month, 1);
		day = date.getDay();
		switch (day) {
		case 0: // Sunday
			return new Date(year, month, 16);
		case 1: // Monday
			return new Date(year, month, 15);
		case 2: // Tuesday
			return new Date(year, month, 21);
		case 3: // Wednesday
			return new Date(year, month, 20);
		case 4: // Thursday
			return new Date(year, month, 19);
		case 5: // Friday
			return new Date(year, month, 18);
		default: // Saturday
			return new Date(year, month, 17);
		}
	}

	@SuppressWarnings("deprecation")
	public static Date getPresidentsDayObserved(int year) {
		// Third Monday in February
		int day;
		int month = 1; // February
		Date date;

		date = new Date(year, month, 1);
		day = date.getDay();
		switch (day) {
		case 0: // Sunday
			return new Date(year, month, 16);
		case 1: // Monday
			return new Date(year, month, 15);
		case 2: // Tuesday
			return new Date(year, month, 21);
		case 3: // Wednesday
			return new Date(year, month, 20);
		case 4: // Thursday
			return new Date(year, month, 19);
		case 5: // Friday
			return new Date(year, month, 18);
		default: // Saturday
			return new Date(year, month, 17);
		}
	}

	@SuppressWarnings("deprecation")
	public static Date getMemorialDayObserved(int year) {
		// Last Monday in May
		int day;
		int month = 4; // May
		Date date;

		date = new Date(year, month, 31);
		day = date.getDay();
		switch (day) {
		case 0: // Sunday
			return new Date(year, month, 25);
		case 1: // Monday
			return new Date(year, month, 31);
		case 2: // Tuesday
			return new Date(year, month, 30);
		case 3: // Wednesday
			return new Date(year, month, 29);
		case 4: // Thursday
			return new Date(year, month, 28);
		case 5: // Friday
			return new Date(year, month, 27);
		default: // Saturday
			return new Date(year, month, 26);
		}
	}

	@SuppressWarnings("deprecation")
	public static Date getIndependenceDayObserved(int year) {
		int day;
		int month = 6; // July
		Date date;

		date = new Date(year, month, 4);
		day = date.getDay();
		switch (day) {
		case 0: // Sunday
			return new Date(year, month, 5);
		case 1: // Monday
		case 2: // Tuesday
		case 3: // Wednesday
		case 4: // Thursday
		case 5: // Friday
			return new Date(year, month, 4);
		default:
			// Saturday
			return new Date(year, month, 3);
		}
	}

	@SuppressWarnings("deprecation")
	public static Date getLaborDayObserved(int year) {
		// The first Monday in September
		int day;
		int month = 8; // September
		Date date;

		date = new Date(year, month, 1);
		day = date.getDay();
		switch (day) {
		case 0: // Sunday
			return new Date(year, month, 2);
		case 1: // Monday
			return new Date(year, month, 7);
		case 2: // Tuesday
			return new Date(year, month, 6);
		case 3: // Wednesday
			return new Date(year, month, 5);
		case 4: // Thursday
			return new Date(year, month, 4);
		case 5: // Friday
			return new Date(year, month, 3);
		default: // Saturday
			return new Date(year, month, 2);
		}
	}

	@SuppressWarnings("deprecation")
	public static Date getColumbusDayObserved(int year) {
		// Second Monday in October
		int day;
		int month = 9; // October
		Date date;

		date = new Date(year, month, 1);
		day = date.getDay();
		switch (day) {
		case 0: // Sunday
			return new Date(year, month, 9);
		case 1: // Monday
			return new Date(year, month, 15);
		case 2: // Tuesday
			return new Date(year, month, 14);
		case 3: // Wednesday
			return new Date(year, month, 13);
		case 4: // Thursday
			return new Date(year, month, 12);
		case 5: // Friday
			return new Date(year, month, 11);
		default: // Saturday
			return new Date(year, month, 10);
		}

	}

	@SuppressWarnings("deprecation")
	public static Date getVeteransDayObserved(int year) {
		// November 11th
		int month = 10; // November

		Date date;

		date = new Date(year, month, 1);
		int day = date.getDay();

		if (year > 1900) {
			year -= 1900;
		}

		switch (day) {
		case 0: // Sunday, then the day after
			return new Date(year, month, 12);
		case 1: // Monday
		case 2: // Tuesday
		case 3: // Wednesday
		case 4: // Thursday
		case 5: // Friday
			return new Date(year, month, 11);
		default: // saturday then the day before
			return new Date(--year, month, 10);
		}
	}

	@SuppressWarnings("deprecation")
	public static Date getThanksgivingObserved(int year) {
		int day;
		int month = 10; // November
		Date date;

		date = new Date(year, month, 1);
		day = date.getDay();

		switch (day) {
		case 0: // Sunday
			return new Date(year, month, 26);
		case 1: // Monday
			return new Date(year, month, 25);
		case 2: // Tuesday
			return new Date(year, month, 24);
		case 3: // Wednesday
			return new Date(year, month, 23);
		case 4: // Thursday
			return new Date(year, month, 22);
		case 5: // Friday
			return new Date(year, month, 28);
		default: // Saturday
			return new Date(year, month, 27);
		}
	}

	@SuppressWarnings("deprecation")
	public static Date getChristmasDayObserved(int year) {
		int day;
		int month = 11; // December
		Date date;

		date = new Date(year, month, 25);
		day = date.getDay();

		switch (day) {
		case 0: // Sunday
			return new Date(year, month, 26);
		case 1: // Monday
		case 2: // Tuesday
		case 3: // Wednesday
		case 4: // Thursday
		case 5: // Friday
			return new Date(year, month, 25);
		default:
			// Saturday
			return new Date(year, month, 24);
		}
	}

	public static boolean findIfHoliday(Calendar dueDateCalendar) {
		if (checkIfWeekend(dueDateCalendar)) {
			return true;
		}

		Date dueDate = dueDateCalendar.getTime();

		/*if (dueDate.getDay() == memorialDayObserved.getDay()
				&& dueDate.getMonth() == memorialDayObserved.getMonth()) {
			if (dueDateCalendar.getTime().equals(PublicHoliday.memorialDayObserved)) {
			System.out.println("memorialDayObserved: "
					+ PublicHoliday.memorialDayObserved);
			Date tempDate = PublicHoliday.memorialDayObserved;
			System.out.println(tempDate.getMonth());
		}*/

		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

		/*System.out.println(sdf.format(dueDate));
		System.out.println(sdf.format(veteransDayObserved));*/

		if (sdf.format(dueDate).equals(sdf.format(christmasDayObserved))) {
			return true;
		} else if (sdf.format(dueDate).equals(sdf.format(columbusDayObserved))) {
			return true;
		} else if (sdf.format(dueDate).equals(
				sdf.format(independenceDayObserved))) {
			return true;
		} else if (sdf.format(dueDate).equals(sdf.format(laborDayObserved))) {
			return true;
		} else if (sdf.format(dueDate).equals(
				sdf.format(martinLutherKingObserved))) {
			return true;
		} else if (sdf.format(dueDate).equals(sdf.format(memorialDayObserved))) {
			return true;
		} else if (sdf.format(dueDate).equals(sdf.format(newYearsDayObserved))) {
			return true;
		} else if (sdf.format(dueDate)
				.equals(sdf.format(presidentsDayObserved))) {
			return true;
		} else if (sdf.format(dueDate).equals(sdf.format(thanksgivingObserved))) {
			return true;
		} else if (sdf.format(dueDate).equals(sdf.format(veteransDayObserved))) {
			return true;
		}

		return false;
	}

	public static boolean checkIfWeekend(Calendar dueDateCalendar) {
		int dayOfWeek = dueDateCalendar.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
			return true;
		}

		return false;
	}

	 
	/*public static Date getDate(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		Date date = cal.getTime();

		return date;
	}*/
	 
	 
	 /*public static Date addBusinessDays(Date date, int days) {

		  	DateTime result = new DateTime(date);
		   
		  	result =  result.plusDays(days);
		    
		  	while (isWeekEnd(result)) {
		    	result = result.plusDays(1);
		    }
	        
		    return result.toDate();
		}
	 
	 
	 private static boolean isWeekEnd(DateTime dateTime) {
		 
		    int dayOfWeek = dateTime.getDayOfWeek();
		    return dayOfWeek == DateTimeConstants.SATURDAY || dayOfWeek == DateTimeConstants.SUNDAY;
	 }*/
}
