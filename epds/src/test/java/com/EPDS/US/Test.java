package com.EPDS.US;

import gov.gao.epds.utils.Date_Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class Test {

	public static void main(String[] args) throws IOException {
		// testSorting();
		// testRepeationRemover1();
		// testRepeationRemover2();
		// System.out.println("01".compareTo("11"));
		// testCompareDate();
		// testSplitBySlash();
		// testListElementRemoval();
		// testRegex();

		// testListOfEntityInsideEntity();
		//testSwitchCase();

		testStringReplace();

	}

	private static void testStringReplace() {
		String text = "This - text ! has \\ /allot # of % special % characters";
		text = text.replaceAll("[^a-zA-Z0-9]", "");
		System.out.println(text);
		String html = "This is bold";
		html = html.replaceAll("[^a-zA-Z0-9\\s+]", "");
		System.out.println(html);
	}


	private static void testSwitchCase() {
		String animal = "jldf";

		switch (animal) {
		case "tiger":
			System.out.println(animal);
		case "cat":
			System.out.println(animal);
		case "mouse":
			System.out.println(animal);
			break;
		}

	}

	private static void testListOfEntityInsideEntity()
			throws JsonParseException, JsonMappingException, JsonGenerationException, IOException {
		Student s1 = new Student("Rosh", "1212121", "HS");
		Student s2 = new Student("Nisha", "2323232", "BS");
		Student s3 = new Student("Suraj", "2323111", "PHD");
		Student s4 = new Student("Bishnu", "1212121", "MS");

		List<Student> s3_StudentList = new ArrayList<Student>();
		s3_StudentList.add(s4);
		s3.setStudentList(s3_StudentList);

		List<Student> s1_StudentList = new ArrayList<Student>();
		s1_StudentList.add(s2);
		s1_StudentList.add(s3);
		s1.setStudentList(s1_StudentList);

		ObjectMapper objMapper = new ObjectMapper();

		System.out.println(objMapper.writeValueAsString(s1));

	}

	private static void testRegex() throws IOException {
		String test = "This is just a test:::" + Date_Util.getCurrentDate();
		String[] output = test.split(":::");
		for (String each : output) {
			System.out.println(each);
		}
	}

	private static void testListElementRemoval() {
		List<String> testList = new ArrayList<String>();
		testList.add("fort Collins");
		testList.add("durango");

		System.out.println(testList);

		testList.remove("durango");
		testList.remove("denver");
		System.out.println(testList);
	}

	private static void testSplitBySlash() {
		String file_Path = "C:\\User\\radhikari\\dev";
		String[] file_Path_Loc = file_Path.split("\\\\");

		System.out.println(file_Path_Loc);

	}

	private static void testCompareDate() {

		/*
		 * String date1 = "Wed Apr 25 09:41:11 EST 2015"; String date2 =
		 * "Wed Mar 25 09:41:11 EST 2015";
		 * 
		 * System.out.println(Util.compareDate(date1, date2));
		 */
	}

	private static void testRepeationRemover2() {
		List<Student> studentList = new ArrayList<Student>();
		Student student1 = new Student("Bibek", "2", "H");
		Student student2 = new Student("Bhisma", "2", "H");
		Student student3 = new Student("Niju", "1", "G");

		studentList.add(student1);
		studentList.add(student2);
		studentList.add(student3);

		List<Student> tempStudentList = new ArrayList<Student>();

		for (Student each : studentList) {
			if (!tempStudentList.contains(each)) {
				tempStudentList.add(each);
			}
		}

		for (Student each : tempStudentList) {
			System.out.println(each);
		}
	}

	private static void testRepeationRemover1() {
		List<Student> studentList = new ArrayList<Student>();
		Student student1 = new Student("Bibek", "2", "H");
		Student student2 = new Student("Bhisma", "2", "H");
		Student student3 = new Student("Niju", "1", "G");

		studentList.add(student1);
		studentList.add(student2);
		studentList.add(student3);

		Set<Student> studentSet = new LinkedHashSet<Student>();
		studentSet.addAll(studentList);
		studentList.clear();
		studentList.addAll(studentSet);

		for (Student each : studentList) {
			System.out.println(each);
		}
	}

	private static void testSorting() {
		List<Student> studentList = new ArrayList<Student>();
		Student student1 = new Student("Bibek", "2", "H");
		Student student2 = new Student("Bhisma", "3", "U");
		Student student3 = new Student("Niju", "1", "G");

		studentList.add(student1);
		studentList.add(student2);
		studentList.add(student3);

		// Collections.sort(studentList);
		Collections.sort(studentList, Student.phoneNoComparator);

		for (Student each : studentList) {
			System.out.println(each);
		}
	}

}
