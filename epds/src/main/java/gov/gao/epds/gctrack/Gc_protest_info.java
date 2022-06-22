package gov.gao.epds.gctrack;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Gc_protest_info {
	private String a_no;
	private String b_no;
	private String filed_date;
	private String due_date;
	private String case_status;

	private String protester;
	private String protester_street;
	private String protester_city;
	private String protester_state;
	private String protester_country;
	private String protester_zipcode;
	private String protester_status;
	private String solicitation_no;
	private String comments;

	@JsonIgnore
	private String representative_firm_name;
	@JsonIgnore
	private String representative_first_name;
	@JsonIgnore
	private String representative_last_name;
	@JsonIgnore
	private String representative_email;
	@JsonIgnore
	private String representative_phone_no;
	@JsonIgnore
	private String representative_fax_no;
	@JsonIgnore
	private String representative_street;
	@JsonIgnore
	private String representative_city;
	@JsonIgnore
	private String representative_state;
	@JsonIgnore
	private String representative_country;
	@JsonIgnore
	private String representative_zipcode;
	private String case_type;
	private String parent_a_no;
	private String agency_name;
	private String attorney_name;
	private List<String> child_b_numbers;


	private List<RepInfo> parties;
	private List<OrgInfo> organizations;

	public List<String> getChild_b_numbers() {
		return child_b_numbers;
	}

	public void setChild_b_numbers(List<String> child_b_numbers) {
		this.child_b_numbers = child_b_numbers;
	}

	public String getAttorney_name() {
		return attorney_name;
	}

	public void setAttorney_name(String attorney_name) {
		this.attorney_name = attorney_name;
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

	public String getFiled_date() {
		return filed_date;
	}

	public void setFiled_date(String filed_date) {
		this.filed_date = filed_date;
	}

	public String getDue_date() {
		return due_date;
	}

	public void setDue_date(String due_date) {
		this.due_date = due_date;
	}

	public String getCase_status() {
		return case_status;
	}

	public void setCase_status(String case_status) {
		this.case_status = case_status;
	}

	public String getProtester() {
		return protester;
	}

	public void setProtester(String protester) {
		this.protester = protester;
	}

	public String getProtester_street() {
		return protester_street;
	}

	public void setProtester_street(String protester_street) {
		this.protester_street = protester_street;
	}

	public String getProtester_city() {
		return protester_city;
	}

	public void setProtester_city(String protester_city) {
		this.protester_city = protester_city;
	}

	public String getProtester_state() {
		return protester_state;
	}

	public void setProtester_state(String protester_state) {
		this.protester_state = protester_state;
	}

	public String getProtester_country() {
		return protester_country;
	}

	public void setProtester_country(String protester_country) {
		this.protester_country = protester_country;
	}

	public String getProtester_zipcode() {
		return protester_zipcode;
	}

	public void setProtester_zipcode(String protester_zipcode) {
		this.protester_zipcode = protester_zipcode;
	}

	public String getProtester_status() {
		return protester_status;
	}

	public void setProtester_status(String protester_status) {
		this.protester_status = protester_status;
	}

	public String getSolicitation_no() {
		return solicitation_no;
	}

	public void setSolicitation_no(String solicitation_no) {
		this.solicitation_no = solicitation_no;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getRepresentative_first_name() {
		return representative_first_name;
	}

	public void setRepresentative_first_name(String representative_first_name) {
		this.representative_first_name = representative_first_name;
	}

	public String getRepresentative_last_name() {
		return representative_last_name;
	}

	public void setRepresentative_last_name(String representative_last_name) {
		this.representative_last_name = representative_last_name;
	}

	public String getRepresentative_email() {
		return representative_email;
	}

	public void setRepresentative_email(String representative_email) {
		this.representative_email = representative_email;
	}

	public String getRepresentative_phone_no() {
		return representative_phone_no;
	}

	public void setRepresentative_phone_no(String representative_phone_no) {
		this.representative_phone_no = representative_phone_no;
	}

	public String getRepresentative_fax_no() {
		return representative_fax_no;
	}

	public void setRepresentative_fax_no(String representative_fax_no) {
		this.representative_fax_no = representative_fax_no;
	}

	public String getRepresentative_street() {
		return representative_street;
	}

	public void setRepresentative_street(String representative_street) {
		this.representative_street = representative_street;
	}

	public String getRepresentative_city() {
		return representative_city;
	}

	public void setRepresentative_city(String representative_city) {
		this.representative_city = representative_city;
	}

	public String getRepresentative_state() {
		return representative_state;
	}

	public void setRepresentative_state(String representative_state) {
		this.representative_state = representative_state;
	}

	public String getRepresentative_country() {
		return representative_country;
	}

	public void setRepresentative_country(String representative_country) {
		this.representative_country = representative_country;
	}

	public String getRepresentative_zipcode() {
		return representative_zipcode;
	}

	public void setRepresentative_zipcode(String representative_zipcode) {
		this.representative_zipcode = representative_zipcode;
	}

	public String getCase_type() {
		return case_type;
	}

	public void setCase_type(String case_type) {
		this.case_type = case_type;
	}

	public String getParent_a_no() {
		return parent_a_no;
	}

	public void setParent_a_no(String parent_a_no) {
		this.parent_a_no = parent_a_no;
	}

	public String getAgency_name() {
		return agency_name;
	}

	public void setAgency_name(String agency_name) {
		this.agency_name = agency_name;
	}

	public List<RepInfo> getParties() {
		return parties;
	}

	public void setParties(List<RepInfo> parties) {
		this.parties = parties;
	}

	public List<OrgInfo> getOrganizations() {
		return organizations;
	}

	public void setOrganizations(List<OrgInfo> organizations) {
		this.organizations = organizations;
	}

	public String getRepresentative_firm_name() {
		return representative_firm_name;
	}

	public void setRepresentative_firm_name(String representative_firm_name) {
		this.representative_firm_name = representative_firm_name;
	}

}
