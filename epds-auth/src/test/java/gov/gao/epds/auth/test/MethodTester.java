package gov.gao.epds.auth.test;

import gov.gao.epds.auth.utils.DateUtil;

import java.sql.Timestamp;
import java.util.Calendar;

public class MethodTester {

	public static void main(String[] args) {
		testGetNumberOfDaysPassedAfterProvidedTimeStamp();
	}

	private static void testGetNumberOfDaysPassedAfterProvidedTimeStamp() {
		Calendar calendar1 = Calendar.getInstance();
		Timestamp timestamp1 = new Timestamp(calendar1.getTimeInMillis());

		System.out.println(DateUtil
				.getNumberOfDaysPassedAfterProvidedTimeStamp(timestamp1));
	}
}
