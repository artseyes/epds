package gov.gao.edps.webservice;

import java.util.List;

public class GcTrackCase2 {
	private String a_no;
	private String b_no;
	private Long attorney_id;
	private List<String> child_b_numbers;
	private Long case_type;
	private Long case_status;
	private String due_date;

	public String getDue_date() {
		return due_date;
	}

	public void setDue_date(String due_date) {
		this.due_date = due_date;
	}

	public String getA_no() {
		return a_no;
	}

	public void setA_no(String a_no) {
		this.a_no = a_no;
	}

	public String getB_no() {
		return b_no;
	}

	public void setB_no(String b_no) {
		this.b_no = b_no;
	}

	public Long getAttorney_id() {
		return attorney_id;
	}

	public void setAttorney_id(Long attorney_id) {
		this.attorney_id = attorney_id;
	}

	public List<String> getChild_b_numbers() {
		return child_b_numbers;
	}

	public void setChild_b_numbers(List<String> child_b_numbers) {
		this.child_b_numbers = child_b_numbers;
	}

	public Long getCase_type() {
		return case_type;
	}

	public void setCase_type(Long case_type) {
		this.case_type = case_type;
	}

	public Long getCase_status() {
		return case_status;
	}

	public void setCase_status(Long case_status) {
		this.case_status = case_status;
	}

}
