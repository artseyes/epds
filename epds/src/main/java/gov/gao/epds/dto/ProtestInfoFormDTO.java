package gov.gao.epds.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.Pattern;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author MHussaini
 *
 */
public class ProtestInfoFormDTO extends DTOValidator implements Serializable{

	
	private static final long serialVersionUID = -4711641469541716528L;
	@Pattern(regexp = SOLICITATION_PATTERN, message="Invalid input format!")
	private String solicitationNumber;
	
	private String contractorname;
	
	@Pattern(regexp = AGENCYID_PATTERN, message="Invalid input format!")
	private String agency_tier_1;
	
	@Pattern(regexp = AGENCYID_PATTERN, message="Invalid input format!")
	private String agency_tier_2;
	
	
	private String sizeofbusiness;
	
	@Pattern(regexp = COMMENTS_PATTERN, message="Invalid input format!")
	private String comments;
	
	@JsonIgnore
	private String filepath;
	
	private String protective;
	
	private String protectiveorder;

	@Pattern(regexp = PROTEST_ID_PATTERN, message="Invalid input format!")
	private String a_No;
	
	@Pattern(regexp = FIRSTNAME_LASTNAME_PATTERN, message="Invalid input format!")
	private String lastname;
	
	@Pattern(regexp = FIRSTNAME_LASTNAME_PATTERN, message="Invalid input format!")
	private String firstname;
	
	private String email;
	
	private String phonenumber;
	private String faxnumber;
	
	private String street;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String country;
	private String zipcode;
	
	@Pattern(regexp = NAME_OF_FIRM_PATTERN, message="Invalid input format!")
	private String nameoffirm;
	
	@Pattern(regexp = NAME_OF_FIRM_PATTERN, message="Invalid input format!")
	private String company_name;
	
	
	private String company_status;
	
	private String company_street;
	
	private String company_address1;
	
	private String company_address2;
	
	private String company_city;
	
	private String company_state;
	
	private String company_country;
	
	private String company_zipcode;
	
	
	private String addmorestatus;
	
	private String typeofdocument;
	
	private String agency_tracking_id;
	
	private String payDotGovTrackingId;
	
	
	
	@Pattern(regexp = IS_DOC_CONFIDENTIAL_PATTERN, message="Invalid input format!")
	private String isDocConfidential;
	
	private String attachAssociatedDoc;
	
	@Pattern(regexp = PROTEST_ID_PATTERN, message="Invalid input format!")
	private String b_no;
	private Long attorney_id;
	private Integer filerId = 0;

	private List<MultipartFile> files;
	private MultipartFile file;

	public Long getAttorney_id() {
		return attorney_id;
	}

	public void setAttorney_id(Long attorney_id) {
		this.attorney_id = attorney_id;
	}

	public String getB_no() {
		return b_no;
	}

	public void setB_no(String b_no) {
		this.b_no = b_no;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public String getCompany_name() {
		return company_name;
	}

	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}

	public String getCompany_status() {
		return company_status;
	}

	public void setCompany_status(String company_status) {
		this.company_status = company_status;
	}

	public String getA_No() {
		return a_No;
	}

	public void setA_No(String a_No) {
		this.a_No = a_No;
	}

	public String getProtectiveorder() {
		return protectiveorder;
	}

	public void setProtectiveorder(String protectiveorder) {
		this.protectiveorder = protectiveorder;
	}

	public String getCompany_street() {
		return company_street;
	}

	public void setCompany_street(String company_street) {
		this.company_street = company_street;
	}

	public String getCompany_city() {
		return company_city;
	}

	public void setCompany_city(String company_city) {
		this.company_city = company_city;
	}

	public String getCompany_state() {
		return company_state;
	}

	public void setCompany_state(String company_state) {
		this.company_state = company_state;
	}

	public String getCompany_country() {
		return company_country;
	}

	public void setCompany_country(String company_country) {
		this.company_country = company_country;
	}

	public String getCompany_zipcode() {
		return company_zipcode;
	}

	public void setCompany_zipcode(String company_zipcode) {
		this.company_zipcode = company_zipcode;
	}

	public String getProtective() {
		return protective;
	}

	public void setProtective(String protective) {
		this.protective = protective;
	}

	public List<MultipartFile> getFiles() {
		return files;
	}

	public void setFiles(List<MultipartFile> files) {
		this.files = files;
	}

	private String submissionDate;

	public String getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(String submissionDate) {
		this.submissionDate = submissionDate;
	}

	public String getSolicitationNumber() {
		return solicitationNumber;
	}

	public void setSolicitationNumber(String solicitationNumber) {
		this.solicitationNumber = solicitationNumber;
	}

	public String getNameoffirm() {
		return nameoffirm;
	}

	public void setNameoffirm(String nameoffirm) {
		this.nameoffirm = nameoffirm;
	}

	public String getAddmorestatus() {
		return addmorestatus;
	}

	public void setAddmorestatus(String addmorestatus) {
		this.addmorestatus = addmorestatus;
	}

	public String getContractorname() {
		return contractorname;
	}

	public void setContractorname(String contractorname) {
		this.contractorname = contractorname;
	}

	public String getTypeofdocument() {
		return typeofdocument;
	}

	public void setTypeofdocument(String typeofdocument) {
		this.typeofdocument = typeofdocument;
	}

	public String getSizeofbusiness() {
		return sizeofbusiness;
	}

	public String getAgency_tier_1() {
		return agency_tier_1;
	}

	public void setAgency_tier_1(String agency_tier_1) {
		this.agency_tier_1 = agency_tier_1;
	}

	public String getAgency_tier_2() {
		return agency_tier_2;
	}

	public void setAgency_tier_2(String agency_tier_2) {
		this.agency_tier_2 = agency_tier_2;
	}

	public void setSizeofbusiness(String sizeofbusiness) {
		this.sizeofbusiness = sizeofbusiness;
	}

	public String getComments() {
		return comments;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}

	public String getFaxnumber() {
		return faxnumber;
	}

	public void setFaxnumber(String faxnumber) {
		this.faxnumber = faxnumber;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public String getIsDocConfidential() {
		return isDocConfidential;
	}

	public void setIsDocConfidential(String isDocConfidential) {
		this.isDocConfidential = isDocConfidential;
	}

	public String getAttachAssociatedDoc() {
		return attachAssociatedDoc;
	}

	public void setAttachAssociatedDoc(String attachAssociatedDoc) {
		this.attachAssociatedDoc = attachAssociatedDoc;
	}

	/**
	 * @return the filerId
	 */
	public Integer getFilerId() {
		return filerId;
	}

	/**
	 * @param filerId the filerId to set
	 */
	public void setFilerId(Integer filerId) {
		this.filerId = filerId;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCompany_address1() {
		return company_address1;
	}

	public void setCompany_address1(String company_address1) {
		this.company_address1 = company_address1;
	}

	public String getCompany_address2() {
		return company_address2;
	}

	public void setCompany_address2(String company_address2) {
		this.company_address2 = company_address2;
	}

	/**
	 * @return the agency_tracking_id
	 */
	public String getAgency_tracking_id() {
		return agency_tracking_id;
	}

	/**
	 * @param agency_tracking_id the agency_tracking_id to set
	 */
	public void setAgency_tracking_id(String agency_tracking_id) {
		this.agency_tracking_id = agency_tracking_id;
	}

	/**
	 * @return the payDotGovTrackingId
	 */
	public String getPayDotGovTrackingId() {
		return payDotGovTrackingId;
	}

	/**
	 * @param payDotGovTrackingId the payDotGovTrackingId to set
	 */
	public void setPayDotGovTrackingId(String payDotGovTrackingId) {
		this.payDotGovTrackingId = payDotGovTrackingId;
	}

	/*
	 * public List<MultipartFile> getFile() { return file; }
	 * 
	 * public void setFile(List<MultipartFile> file) { this.file = file; }
	 */
}
