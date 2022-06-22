package com.EPDS.US;

import java.util.Comparator;
import java.util.List;

public class Student implements Comparable<Student> {
	private String name;
	private String phone_No;
	private String schoolLevel;
	private List<Student> studentList;

	public Student(String name, String phone_No, String schoolLevel) {
		this.name = name;
		this.phone_No = phone_No;
		this.schoolLevel = schoolLevel;
	}

	public List<Student> getStudentList() {
		return studentList;
	}

	public void setStudentList(List<Student> studentList) {
		this.studentList = studentList;
	}

	public static Comparator<Student> getPhoneNoComparator() {
		return phoneNoComparator;
	}

	public static void setPhoneNoComparator(
			Comparator<Student> phoneNoComparator) {
		Student.phoneNoComparator = phoneNoComparator;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone_No() {
		return phone_No;
	}

	public void setPhone_No(String phone_No) {
		this.phone_No = phone_No;
	}

	public String getSchoolLevel() {
		return schoolLevel;
	}

	public void setSchoolLevel(String schoolLevel) {
		this.schoolLevel = schoolLevel;
	}

	@Override
	public int compareTo(Student o) {
		return (this.name.compareTo(o.name));
	}

	public static Comparator<Student> phoneNoComparator = new Comparator<Student>() {

		public int compare(Student student1, Student student2) {

			String studentPhoneNo1 = student1.getPhone_No().toUpperCase();
			String studentPhoneNo2 = student2.getPhone_No().toUpperCase();

			return studentPhoneNo1.compareTo(studentPhoneNo2);
		}
	};

	@Override
	public String toString() {
		return "Student [name=" + name + ", phone_No=" + phone_No
				+ ", schoolLevel=" + schoolLevel + "]";
	}

	@Override
	public boolean equals(Object obj) {
		Student toBeCompared = (Student) obj;
		return (toBeCompared.schoolLevel.equals(this.schoolLevel));
	}

}
