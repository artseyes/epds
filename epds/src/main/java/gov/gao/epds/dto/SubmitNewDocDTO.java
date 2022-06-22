package gov.gao.epds.dto;

import java.io.Serializable;

import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class SubmitNewDocDTO  extends DTOValidator implements Serializable{
	
	private static final long serialVersionUID = 653104735392395668L;
	
	@Pattern(regexp = TYPE_OF_DOCUMENT_PATTERN, message="Invalid input format!")
	private String typeofdocument;
	
	@JsonIgnore
	private String submissionDate;
	
	@Pattern(regexp = IS_DOC_CONFIDENTIAL_PATTERN, message="Invalid input format!")
	private String isDocConfidential;
	
	@Pattern(regexp = NAME_OF_FIRM_PATTERN, message="Invalid input format!")
	private String intervenorCompanyName;
	
	@Pattern(regexp = PROTEST_ID_PATTERN, message="Invalid input format!")
	private String protestId;
	
	@JsonIgnore
	private String filepath;
	
	private int docId;
	
	@Pattern(regexp = USER_ID_PATTERN, message="Invalid input format!")
	private String user_Id;
	
	@Pattern(regexp = COMMENTS_PATTERN, message="Invalid input format!")
	private String comments;
	
	
	private String po;
	
	
	@Pattern(regexp = EMAIL_PATTERN, message="Invalid input format!")
	private String intervenorEmailAddress;
	
	private String street;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String country;
	private String zipCode;
	
	
	private String company_Address;
	
	@Pattern(regexp = NAME_OF_FIRM_PATTERN, message="Invalid input format!")
	private String company_Name;
	
	private String docDescFiller;
	
	@Pattern(regexp = ROLE_PATTERN, message="Invalid input format!")
	private String user_Role;
	
	@Pattern(regexp = INTEGER_PATTERN, message="Invalid input format!")
	private String docketEntryNumber;
	
	@Pattern(regexp = COMMENTS_PATTERN, message="Invalid input format!")
	private String attorney_note;
	
	@Pattern(regexp = IS_DOC_CONFIDENTIAL_PATTERN, message="Invalid input format!")
	private String isInterveneApproved;
	
	@Pattern(regexp = IS_DOC_CONFIDENTIAL_PATTERN, message="Invalid input format!")
	private String caseAccessRequestStatus;
	
	
	private String listOfProtestIds;
	private String consolidatedBNumber;
	
	
	private String consolidatedANumber;
	
	@Pattern(regexp = TYPE_OF_DOCUMENT_PATTERN, message="Invalid input format!")
	private String docketEntryTitle;

	public String getCaseAccessRequestStatus() {
		return caseAccessRequestStatus;
	}

	public void setCaseAccessRequestStatus(String caseAccessRequestStatus) {
		this.caseAccessRequestStatus = caseAccessRequestStatus;
	}

	public String getIsInterveneApproved() {
		return isInterveneApproved;
	}

	public void setIsInterveneApproved(String isInterveneApproved) {
		this.isInterveneApproved = isInterveneApproved;
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

	public String getAttorney_note() {
		return attorney_note;
	}

	public void setAttorney_note(String attorney_note) {
		this.attorney_note = attorney_note;
	}

	public String getUser_Role() {
		return user_Role;
	}

	public void setUser_Role(String user_Role) {
		this.user_Role = user_Role;
	}

	public String getCompany_Name() {
		return company_Name;
	}

	public void setCompany_Name(String company_Name) {
		this.company_Name = company_Name;
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

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getCompany_Address() {
		return company_Address;
	}

	public void setCompany_Address(String company_Address) {
		this.company_Address = company_Address;
	}

	public String getUser_Id() {
		return user_Id;
	}

	public void setUser_Id(String user_Id) {
		this.user_Id = user_Id;
	}

	public String getTypeofdocument() {
		return typeofdocument;
	}

	public void setTypeofdocument(String typeofdocument) {
		this.typeofdocument = typeofdocument;
	}

	public String getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(String submissionDate) {
		this.submissionDate = submissionDate;
	}

	public String getIntervenorCompanyName() {
		return intervenorCompanyName;
	}

	public void setIntervenorCompanyName(String intervenorCompanyName) {
		this.intervenorCompanyName = intervenorCompanyName;
	}

	public String getProtestId() {
		return protestId;
	}

	public void setProtestId(String protestId) {
		this.protestId = protestId;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public String getIsDocConfidential() {
		return isDocConfidential;
	}

	public void setIsDocConfidential(String isDocConfidential) {
		this.isDocConfidential = isDocConfidential;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getDocDescFiller() {
		return docDescFiller;
	}

	public void setDocDescFiller(String docDescFiller) {
		this.docDescFiller = docDescFiller;
	}

	/**
	 * @return the docketEntryNumber
	 */
	public String getDocketEntryNumber() {
		return docketEntryNumber;
	}

	/**
	 * @param docketEntryNumber
	 *            the docketEntryNumber to set
	 */
	public void setDocketEntryNumber(String docketEntryNumber) {
		this.docketEntryNumber = docketEntryNumber;
	}

	/**
	 * @return the listOfProtestIds
	 */
	public String getListOfProtestIds() {
		return listOfProtestIds;
	}

	/**
	 * @param listOfProtestIds the listOfProtestIds to set
	 */
	public void setListOfProtestIds(String listOfProtestIds) {
		this.listOfProtestIds = listOfProtestIds;
	}

	/**
	 * @return the consolidatedBNumber
	 */
	public String getConsolidatedBNumber() {
		return consolidatedBNumber;
	}

	/**
	 * @param consolidatedBNumber the consolidatedBNumber to set
	 */
	public void setConsolidatedBNumber(String consolidatedBNumber) {
		this.consolidatedBNumber = consolidatedBNumber;
	}

	/**
	 * @return the consolidatedANumber
	 */
	public String getConsolidatedANumber() {
		return consolidatedANumber;
	}

	/**
	 * @param consolidatedANumber the consolidatedANumber to set
	 */
	public void setConsolidatedANumber(String consolidatedANumber) {
		this.consolidatedANumber = consolidatedANumber;
	}

	/**
	 * @return the docketEntryTitle
	 */
	public String getDocketEntryTitle() {
		return docketEntryTitle;
	}

	/**
	 * @param docketEntryTitle the docketEntryTitle to set
	 */
	public void setDocketEntryTitle(String docketEntryTitle) {
		this.docketEntryTitle = docketEntryTitle;
	}

	/**
	 * @return the intervenorEmailAddress
	 */
	public String getIntervenorEmailAddress() {
		return intervenorEmailAddress;
	}

	/**
	 * @param intervenorEmailAddress the intervenorEmailAddress to set
	 */
	public void setIntervenorEmailAddress(String intervenorEmailAddress) {
		this.intervenorEmailAddress = intervenorEmailAddress;
	}

	public String getPo() {
		return po;
	}

	public void setPo(String po) {
		this.po = po;
	}

}
