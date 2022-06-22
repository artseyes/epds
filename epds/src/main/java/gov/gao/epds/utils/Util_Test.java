package gov.gao.epds.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Util_Test {

	public static void main(String[] args) {
		// System.out.println(Date_Util.getNumberOfDaysRemaining("05/03/2015"));

		// testConvertToJavaDate();
		// testGetDueDate();
		testListIteration();
	}

	private static void testListIteration() {
		List<String> testList = new ArrayList<String>();
		testList.add("a");
		testList.add("b");
		testList.add("c");
		testList.add("a");
		testList.add("e");

		for (int i = 0; i < testList.size(); i++) {
			if (testList.get(i).equals("a") || testList.get(i).equals("e"))
				testList.remove(testList.get(i));
		}

		for (String each : testList) {
			System.out.println(each);
		}

	}

	private static Date getDateInJavaDateFormat(String dateInString) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		Date date = null;
		try {
			date = sdf.parse(dateInString);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return date;
	}

	private static void testConvertToJavaDate() {
		Date date = getDateInJavaDateFormat("01/02/2015");

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		System.out.println(cal.get(Calendar.MONTH));
		System.out.println(cal.get(Calendar.DAY_OF_MONTH));
	}

	private static void testGetDueDate() {
		// System.out.println(Calendar.getInstance().get(Calendar.YEAR));
		// System.out.println(Calendar.getInstance().getTime());
		// System.out.println("May: " + (Calendar.MAY));
		// System.out.println(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

		// System.out.println("Sunday: " + Calendar.SUNDAY);
		// System.out.println("Saturday: " + Calendar.SATURDAY);

		System.out.println("Day: " + (new Date()).getDay() + ", Month: "
				+ (new Date()).getMonth());
		System.out.println((new Date()).toString());
		// System.out.println(Date_Util.getDueDate());

		/*System.out.println("christmasDayObserved: "
				+ PublicHoliday.christmasDayObserved);
		System.out.println("martinLutherKingObserved: "
				+ PublicHoliday.martinLutherKingObserved);
		System.out.println("presidentsDayObserved: "
				+ PublicHoliday.presidentsDayObserved);
		System.out.println("memorialDayObserved: "
				+ PublicHoliday.memorialDayObserved);
		System.out.println("independencyDayObserved: "
				+ PublicHoliday.independencyDayObserved);
		System.out.println("laborDayObserved: "
				+ PublicHoliday.laborDayObserved);
		System.out.println("columbusDayObserved: "
				+ PublicHoliday.columbusDayObserved);
		System.out.println("veteransDayObserved: "
				+ PublicHoliday.veteransDayObserved);
		System.out.println("thanksgivingObserved: "
				+ PublicHoliday.thanksgivingObserved);
		System.out.println("newYearsDayObserved: "
				+ PublicHoliday.newYearsDayObserved);*/
	}

}
