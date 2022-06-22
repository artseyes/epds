package gov.gao.epds.gctrack;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.Pattern;

import gov.gao.epds.dto.DTOValidator;

public class GC_track_case extends DTOValidator implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2092718907753220878L;
	
	@Pattern(regexp = PROTEST_ID_PATTERN, message="Invalid input format!")
	private String a_no;
	@Pattern(regexp = PROTEST_ID_PATTERN, message="Invalid input format!")
	private String b_no;
	
	private Long attorney_id;
	
	private String parent_b_no;
	private List<String> child_b_numbers;
	private Long case_type;
	private Long case_status;
	private String due_date;
	private Party protester;
	private Party primaryAgency;

	public String getParent_b_no() {
		return parent_b_no;
	}

	public void setParent_b_no(String parent_b_no) {
		this.parent_b_no = parent_b_no;
	}

	public Party getProtester() {
		return protester;
	}

	public void setProtester(Party protester) {
		this.protester = protester;
	}

	public Party getPrimaryAgency() {
		return primaryAgency;
	}

	public void setPrimaryAgency(Party primaryAgency) {
		this.primaryAgency = primaryAgency;
	}

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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GC_track_case [a_no=");
		builder.append(a_no);
		builder.append(", b_no=");
		builder.append(b_no);
		builder.append(", attorney_id=");
		builder.append(attorney_id);
		builder.append(", parent_b_no=");
		builder.append(parent_b_no);
		builder.append(", child_b_numbers=");
		builder.append(child_b_numbers);
		builder.append(", case_type=");
		builder.append(case_type);
		builder.append(", case_status=");
		builder.append(case_status);
		builder.append(", due_date=");
		builder.append(due_date);
		builder.append(", protester=");
		builder.append(protester);
		builder.append(", primaryAgency=");
		builder.append(primaryAgency);
		builder.append("]");
		return builder.toString();
	}
	
	
	

}
