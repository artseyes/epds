package gov.gao.epds.persistence.entity;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;

import gov.gao.epds.dto.AgencyRepInfo;
import gov.gao.epds.dto.CaseCompletionStatus;
import gov.gao.epds.utils.Date_Util;
import gov.gao.epds.utils.Util;

@Entity
@Table
@Audited
public class Protest_Info implements Serializable {
	private static final long serialVersionUID = 4323329776229959475L;
	@Id
	@Column
	private String a_No;
	@Column
	private String b_No;
	@Column
	private String submission_Date;
	@Column
	private String due_Date;
	@Column
	private String po;
	@Column
	private int agency_Info_Id;
	@Column
	private String case_Status;
	@Column
	private String company_Name;
	@Column
	private String company_Street;
	@Column
	private String company_address1;
	@Column
	private String company_address2;
	@Column
	private String company_City;
	@Column
	private String company_State;
	@Column
	private String company_Country;
	@Column
	private String company_Zipcode;
	@Column
	private String company_Status;
	@Column
	private String solicitation_No;
	@Column(length=300)
	private String comments;
	@Column(name = "rep_FName")
	private String representative_First_Name;
	@Column(name = "rep_LName")
	private String representative_Last_Name;
	@Column(name = "rep_Email")
	private String representative_Email;
	@Column(name = "rep_Phone_No")
	private String representative_Phone_No;
	@Column(name = "rep_Fax_No")
	private String representative_Fax_No;
	@Column(name = "rep_Street")
	private String representative_Street;
	@Column(name = "rep_address1")
	private String representative_address1;
	@Column(name = "rep_address2")
	private String representative_address2;
	@Column(name = "rep_City")
	private String representative_City;
	@Column(name = "rep_State")
	private String representative_State;
	@Column(name = "rep_Country")
	private String representative_Country;
	@Column(name = "rep_Zipcode")
	private String representative_Zipcode;
	@Column
	private String case_Type;
	@Column
	private String parent_A_No;
	@Column
	private String attorney_Name;
	@Column
	private Integer attorney_Group_Id;
	@Column
	private String agency_tracking_id;
	@Column
	private String last_parent_a_no;
	
	@Column
	private Date public_decision_date;
	
	@Column
	private String pay_dot_gov_id;
	
	@Column
	private String transaction_Status;
	
	@Column
	private String reasonForDeletion;
	
	@Column
	private Long submissionDateTime;
	
	@Transient
	private String agency_Name;
	@Transient
	private String role;
	@Transient
	private String isUserAdmittedToPO;
	@Transient
	private List<Protest_Info> children_Protest_InfoList = new ArrayList<Protest_Info>();
	@Transient
	private String supplemental_A_Nos;
	@Transient
	private String supplemental_B_Nos;
	@Transient
	private String isUserConsolidated;
	@Transient
	private List<Protest_Info> listOf_ConsolidatedProtest_Info;
	@Transient
	private String caseAccessRequestStatus;
	@Transient
	private String caseAccessRequestType;
	@Transient
	private String companyNameUserRepresentingTo;
	@Transient
	private Integer deniedIndicatingDocTypeId;
	@Transient
	private String deniedDate;
	@Transient
	private String casedocket_email_preferences;
	@Transient
	private int roleId;
	@Transient
	private boolean isViewOnly;
	
	@Transient
	private Integer payDotGovTransactionAmt;
	
	@Transient
	private Integer deniedFileId;
	
	@Transient
	private CaseCompletionStatus caseCompletionStatus;
	
	@Transient
	private List<AgencyRepInfo> agencyRepInfos;
	
	
	@Transient
	private List<Integer> primaryAgencyInfoIds;
	
	public Protest_Info(Protest_Info protest_Info) {
		
		this.a_No = protest_Info.getA_No();
		this.b_No = protest_Info.getB_No();
		this.submission_Date = protest_Info.getSubmission_Date();
		this.due_Date = protest_Info.getDue_Date();
		this.po = protest_Info.getPo();
		this.agency_Info_Id = protest_Info.getAgency_Info_Id();
		this.case_Status = protest_Info.getCase_Status();
		this.company_Name = protest_Info.getCompany_Name();
		this.company_Street = protest_Info.getCompany_Street();
		this.company_address1 = protest_Info.getCompany_address1();
		this.company_address2 = protest_Info.getCompany_address2();
		this.company_City = protest_Info.getCompany_City();
		this.company_State = protest_Info.getCompany_State();
		this.company_Country = protest_Info.getCompany_Country();
		this.company_Zipcode = protest_Info.getCompany_Zipcode();
		this.company_Status = protest_Info.getCompany_Status();
		this.solicitation_No = protest_Info.getSolicitation_No();
		this.comments = protest_Info.getComments();
		this.representative_First_Name = protest_Info
				.getRepresentative_First_Name();
		this.representative_Last_Name = protest_Info
				.getRepresentative_Last_Name();
		this.representative_Email = protest_Info.getRepresentative_Email();
		this.representative_Phone_No = protest_Info
				.getRepresentative_Phone_No();
		this.representative_Fax_No = protest_Info.getRepresentative_Fax_No();
		this.representative_Street = protest_Info.getRepresentative_Street();
		this.representative_address1 = protest_Info.getRepresentative_address1();
		this.representative_address2 = protest_Info.getRepresentative_address2();
		this.representative_City = protest_Info.getRepresentative_City();
		this.representative_State = protest_Info.getRepresentative_State();
		this.representative_Country = protest_Info.getRepresentative_Country();
		this.representative_Zipcode = protest_Info.getRepresentative_Zipcode();
		this.case_Type = protest_Info.getCase_Type();
		this.parent_A_No = protest_Info.getParent_A_No();
		this.attorney_Name = protest_Info.getAttorney_Name();
		this.role = protest_Info.getRole();
		this.isUserAdmittedToPO = protest_Info.getIsUserAdmittedToPO();
		/*
		 * this.children_Protest_InfoList = protest_Info
		 * .getChildren_Protest_InfoList();
		 */
		this.supplemental_A_Nos = protest_Info.getSupplemental_A_Nos();
		this.supplemental_B_Nos = protest_Info.getSupplemental_B_Nos();
		this.isUserConsolidated = protest_Info.getIsUserConsolidated();
		this.transaction_Status = protest_Info.getTransaction_Status();
	}

	public Protest_Info() {
		super();
	}
	
	public boolean isViewOnly() {
		return isViewOnly;
	}

	public void setViewOnly(boolean isViewOnly) {
		this.isViewOnly = isViewOnly;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public String getCasedocket_email_preferences() {
		return casedocket_email_preferences;
	}

	public void setCasedocket_email_preferences(
			String casedocket_email_preferences) {
		this.casedocket_email_preferences = casedocket_email_preferences;
	}

	public String getLast_parent_a_no() {
		return last_parent_a_no;
	}

	public void setLast_parent_a_no(String last_parent_a_no) {
		this.last_parent_a_no = last_parent_a_no;
	}

	public Integer getDeniedIndicatingDocTypeId() {
		return deniedIndicatingDocTypeId;
	}

	public void setDeniedIndicatingDocTypeId(Integer deniedIndicatingDocTypeId) {
		this.deniedIndicatingDocTypeId = deniedIndicatingDocTypeId;
	}

	public String getDeniedDate() {
		return deniedDate;
	}

	public void setDeniedDate(String deniedDate) {
		this.deniedDate = deniedDate;
	}

	public String getCompanyNameUserRepresentingTo() {
		return companyNameUserRepresentingTo;
	}

	public void setCompanyNameUserRepresentingTo(
			String companyNameUserRepresentingTo) {
		this.companyNameUserRepresentingTo = companyNameUserRepresentingTo;
	}

	public String getCaseAccessRequestStatus() {
		return caseAccessRequestStatus;
	}

	public void setCaseAccessRequestStatus(String caseAccessRequestStatus) {
		this.caseAccessRequestStatus = caseAccessRequestStatus;
	}

	public String getAgency_tracking_id() {
		return agency_tracking_id;
	}

	public void setAgency_tracking_id(String agency_tracking_id) {
		this.agency_tracking_id = agency_tracking_id;
	}

	public List<Protest_Info> getListOf_ConsolidatedProtest_Info() {
		return listOf_ConsolidatedProtest_Info;
	}

	public void setListOf_ConsolidatedProtest_Info(
			List<Protest_Info> listOf_ConsolidatedProtest_Info) {
		this.listOf_ConsolidatedProtest_Info = listOf_ConsolidatedProtest_Info;
	}

	public String getIsUserConsolidated() {
		return isUserConsolidated;
	}

	public void setIsUserConsolidated(String isUserConsolidated) {
		this.isUserConsolidated = isUserConsolidated;
	}

// TODO Remove unused code found by UCDetector
// 	public static Comparator<Protest_Info> getB_No_Comparator_AlphabeticAscendingOrder() {
// 		return B_No_Comparator_AlphabeticAscendingOrder;
// 	}

// TODO Remove unused code found by UCDetector
// 	public static void setB_No_Comparator_AlphabeticAscendingOrder(
// 			Comparator<Protest_Info> b_No_Comparator_AlphabeticAscendingOrder) {
// 		B_No_Comparator_AlphabeticAscendingOrder = b_No_Comparator_AlphabeticAscendingOrder;
// 	}

	public String getSupplemental_A_Nos() {
		return supplemental_A_Nos;
	}

	public void setSupplemental_A_Nos(String supplemental_A_Nos) {
		this.supplemental_A_Nos = supplemental_A_Nos;
	}

	public String getSupplemental_B_Nos() {
		return supplemental_B_Nos;
	}

	public void setSupplemental_B_Nos(String supplemental_B_Nos) {
		this.supplemental_B_Nos = supplemental_B_Nos;
	}

	public String getAttorney_Name() {
		return attorney_Name;
	}

	public void setAttorney_Name(String attorney_Name) {
		this.attorney_Name = attorney_Name;
	}

	public Integer getAttorney_Group_Id() {
		return attorney_Group_Id;
	}

	public void setAttorney_Group_Id(Integer attorney_Group_Id) {
		this.attorney_Group_Id = attorney_Group_Id;
	}

	public String getParent_A_No() {
		return parent_A_No;
	}

	public void setParent_A_No(String parent_A_No) {
		this.parent_A_No = parent_A_No;
	}

	public List<Protest_Info> getChildren_Protest_InfoList() {
		return children_Protest_InfoList;
	}

	public void setChildren_Protest_InfoList(
			List<Protest_Info> children_Protest_InfoList) {
		this.children_Protest_InfoList = children_Protest_InfoList;
	}

	public String getIsUserAdmittedToPO() {
		return isUserAdmittedToPO;
	}

	public void setIsUserAdmittedToPO(String isUserAdmittedToPO) {
		this.isUserAdmittedToPO = isUserAdmittedToPO;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getCase_Type() {
		return case_Type;
	}

	public void setCase_Type(String case_Type) {
		this.case_Type = case_Type;
	}

	public static Comparator<Protest_Info> getSubmissionDateComparatorOldToNew() {
		return SubmissionDateComparatorOldToNew;
	}

	public static void setSubmissionDateComparatorOldToNew(
			Comparator<Protest_Info> submissionDateComparatorOldToNew) {
		SubmissionDateComparatorOldToNew = submissionDateComparatorOldToNew;
	}

	public static Comparator<Protest_Info> getSubmissionDateComparatorNewToOld() {
		return SubmissionDateComparatorNewToOld;
	}

	public static void setSubmissionDateComparatorNewToOld(
			Comparator<Protest_Info> submissionDateComparatorNewToOld) {
		SubmissionDateComparatorNewToOld = submissionDateComparatorNewToOld;
	}

	public static Comparator<Protest_Info> getCaseStatusComparatorOpentoClosedAndSubmissionDateNewToOld() {
		return CaseStatusComparatorOpentoClosedAndSubmissionDateNewToOld;
	}

	public static void setCaseStatusComparatorOpentoClosedAndSubmissionDateNewToOld(
			Comparator<Protest_Info> caseStatusComparatorOpentoClosedAndSubmissionDateNewToOld) {
		CaseStatusComparatorOpentoClosedAndSubmissionDateNewToOld = caseStatusComparatorOpentoClosedAndSubmissionDateNewToOld;
	}

	public static Comparator<Protest_Info> getCaseStatusComparatorOpentoClosedAndSubmissionDateOldToNew() {
		return CaseStatusComparatorOpentoClosedAndSubmissionDateOldToNew;
	}

	public static void setCaseStatusComparatorOpentoClosedAndSubmissionDateOldToNew(
			Comparator<Protest_Info> caseStatusComparatorOpentoClosedAndSubmissionDateOldToNew) {
		CaseStatusComparatorOpentoClosedAndSubmissionDateOldToNew = caseStatusComparatorOpentoClosedAndSubmissionDateOldToNew;
	}

	public static Comparator<Protest_Info> getCaseStatusComparatorClosedToOpenAndSubmissionDateNewToOld() {
		return CaseStatusComparatorClosedToOpenAndSubmissionDateNewToOld;
	}

	public static void setCaseStatusComparatorClosedToOpenAndSubmissionDateNewToOld(
			Comparator<Protest_Info> caseStatusComparatorClosedToOpenAndSubmissionDateNewToOld) {
		CaseStatusComparatorClosedToOpenAndSubmissionDateNewToOld = caseStatusComparatorClosedToOpenAndSubmissionDateNewToOld;
	}

	public static Comparator<Protest_Info> getCaseStatusComparatorClosedToOpenAndSubmissionDateOldToNew() {
		return CaseStatusComparatorClosedToOpenAndSubmissionDateOldToNew;
	}

	public static void setCaseStatusComparatorClosedToOpenAndSubmissionDateOldToNew(
			Comparator<Protest_Info> caseStatusComparatorClosedToOpenAndSubmissionDateOldToNew) {
		CaseStatusComparatorClosedToOpenAndSubmissionDateOldToNew = caseStatusComparatorClosedToOpenAndSubmissionDateOldToNew;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getAgency_Name() {
		return agency_Name;
	}

	public void setAgency_Name(String agency_Name) {
		this.agency_Name = agency_Name;
	}

	public String getRepresentative_First_Name() {
		return representative_First_Name;
	}

	public void setRepresentative_First_Name(String representative_First_Name) {
		this.representative_First_Name = representative_First_Name;
	}

	public String getRepresentative_Last_Name() {
		return representative_Last_Name;
	}

	public void setRepresentative_Last_Name(String representative_Last_Name) {
		this.representative_Last_Name = representative_Last_Name;
	}

	public String getRepresentative_Email() {
		return representative_Email;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public void setRepresentative_Email(String representative_Email) {
		this.representative_Email = representative_Email;
	}

	public String getRepresentative_Phone_No() {
		return representative_Phone_No;
	}

	public void setRepresentative_Phone_No(String representative_Phone_No) {
		this.representative_Phone_No = representative_Phone_No;
	}

	public String getRepresentative_Fax_No() {
		return representative_Fax_No;
	}

	public void setRepresentative_Fax_No(String representative_Fax_No) {
		this.representative_Fax_No = representative_Fax_No;
	}

	public String getRepresentative_Street() {
		return representative_Street;
	}

	public void setRepresentative_Street(String representative_Street) {
		this.representative_Street = representative_Street;
	}

	public String getRepresentative_City() {
		return representative_City;
	}

	public void setRepresentative_City(String representative_City) {
		this.representative_City = representative_City;
	}

	public String getRepresentative_State() {
		return representative_State;
	}

	public void setRepresentative_State(String representative_State) {
		this.representative_State = representative_State;
	}

	public String getRepresentative_Country() {
		return representative_Country;
	}

	public void setRepresentative_Country(String representative_Country) {
		this.representative_Country = representative_Country;
	}

	public String getRepresentative_Zipcode() {
		return representative_Zipcode;
	}

	public void setRepresentative_Zipcode(String representative_Zipcode) {
		this.representative_Zipcode = representative_Zipcode;
	}

	public String getA_No() {
		return a_No;
	}

	public void setA_No(String a_No) {
		this.a_No = a_No;
	}

	public String getB_No() {
		return b_No;
	}

	public void setB_No(String b_No) {
		// b_No = Util.getBNumberWithBDashPrefix(b_No);
		this.b_No = b_No;
	}

	public String getSubmission_Date() {
		return submission_Date;
	}

	public void setSubmission_Date(String submission_Date) {
		this.submission_Date = submission_Date;
	}

	public String getDue_Date() {
		return due_Date;
	}

	public void setDue_Date(String due_Date) {
		this.due_Date = due_Date;
	}

	public String getPo() {
		return po;
	}

	public void setPo(String po) {
		this.po = po;
	}

	public int getAgency_Info_Id() {
		return agency_Info_Id;
	}

	public void setAgency_Info_Id(int agency_Info_Id) {
		this.agency_Info_Id = agency_Info_Id;
	}

	public String getCase_Status() {
		return case_Status;
	}

	public void setCase_Status(String case_Status) {
		this.case_Status = case_Status;
	}

	public String getCompany_Name() {
		return company_Name;
	}

	public void setCompany_Name(String company_Name) {
		this.company_Name = company_Name;
	}

	public String getCompany_Street() {
		return company_Street;
	}

	public void setCompany_Street(String company_Street) {
		this.company_Street = company_Street;
	}

	public String getCompany_City() {
		return company_City;
	}

	public void setCompany_City(String company_City) {
		this.company_City = company_City;
	}

	public String getCompany_State() {
		return company_State;
	}

	public void setCompany_State(String company_State) {
		this.company_State = company_State;
	}

	public String getCompany_Country() {
		return company_Country;
	}

	public void setCompany_Country(String company_Country) {
		this.company_Country = company_Country;
	}

	public String getCompany_Zipcode() {
		return company_Zipcode;
	}

	public void setCompany_Zipcode(String company_Zipcode) {
		this.company_Zipcode = company_Zipcode;
	}

	public String getCompany_Status() {
		return company_Status;
	}

	public void setCompany_Status(String company_Status) {
		this.company_Status = company_Status;
	}

	public String getSolicitation_No() {
		return solicitation_No;
	}

	public void setSolicitation_No(String solicitation_No) {

		this.solicitation_No = solicitation_No;
	}

	
	/**
	 * @return the caseAccessRequestType
	 */
	public String getCaseAccessRequestType() {
		return caseAccessRequestType;
	}

	/**
	 * @param caseAccessRequestType the caseAccessRequestType to set
	 */
	public void setCaseAccessRequestType(String caseAccessRequestType) {
		this.caseAccessRequestType = caseAccessRequestType;
	}

	/**
	 * @return the pay_dot_gov_id
	 */
	public String getPay_dot_gov_id() {
		return pay_dot_gov_id;
	}

	/**
	 * @param pay_dot_gov_id the pay_dot_gov_id to set
	 */
	public void setPay_dot_gov_id(String pay_dot_gov_id) {
		this.pay_dot_gov_id = pay_dot_gov_id;
	}
	
	public static Comparator<Protest_Info> SubmissionDateComparatorOldToNew = new Comparator<Protest_Info>() {
		DateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy HH:mm:ss z");
		
					public int compare(Protest_Info protest_Info1,
							Protest_Info Protest_Info2) {
						try {
							return dateFormat.parse(protest_Info1.getSubmission_Date()).compareTo(dateFormat.parse(Protest_Info2.getSubmission_Date()));
						} catch (ParseException e) {
							throw new IllegalArgumentException(e);
						}
					}
				};

	public static Comparator<Protest_Info> SubmissionDateComparatorNewToOld = new Comparator<Protest_Info>() {

		public int compare(Protest_Info protest_Info1,
				Protest_Info protest_Info2) {

			return Date_Util.compareDate(protest_Info2.submission_Date,
					protest_Info1.submission_Date);
		}

	};

	public static Comparator<Protest_Info> CaseStatusComparatorOpentoClosedAndSubmissionDateNewToOld = new Comparator<Protest_Info>() {

		public int compare(Protest_Info protest_Info1,
				Protest_Info protest_Info2) {

			int result = (protest_Info2.getCase_Status()
					.compareTo(protest_Info1.getCase_Status()));

			return result == 0 ? SubmissionDateComparatorNewToOld.compare(
					protest_Info1, protest_Info2) : result;
		}

	};

	public static Comparator<Protest_Info> CaseStatusComparatorOpentoClosedAndSubmissionDateOldToNew = new Comparator<Protest_Info>() {

		public int compare(Protest_Info protest_Info1,
				Protest_Info protest_Info2) {
			int result = (protest_Info2.getCase_Status()
					.compareTo(protest_Info1.getCase_Status()));

			return result == 0 ? SubmissionDateComparatorOldToNew.compare(
					protest_Info1, protest_Info2) : result;
		}

	};

	public static Comparator<Protest_Info> CaseStatusComparatorClosedToOpenAndSubmissionDateNewToOld = new Comparator<Protest_Info>() {

		public int compare(Protest_Info protest_Info1,
				Protest_Info protest_Info2) {
			int result = (protest_Info1.getCase_Status()
					.compareTo(protest_Info2.getCase_Status()));

			return result == 0 ? SubmissionDateComparatorNewToOld.compare(
					protest_Info1, protest_Info2) : result;
		}

	};

	public static Comparator<Protest_Info> B_No_Comparator_AlphabeticAscendingOrder = new Comparator<Protest_Info>() {
		public int compare(Protest_Info protest_Info1,
				Protest_Info protest_Info2) {
			
			if (protest_Info1 .getB_No() != null){
			
				return (protest_Info1.getB_No().compareTo(protest_Info2.getB_No()));
			}
			
			return 0;
			
		}

	};

	public static Comparator<Protest_Info> CaseStatusComparatorClosedToOpenAndSubmissionDateOldToNew = new Comparator<Protest_Info>() {

		public int compare(Protest_Info protest_Info1,
				Protest_Info protest_Info2) {
			int result = (protest_Info1.getCase_Status()
					.compareTo(protest_Info2.getCase_Status()));

			return result == 0 ? SubmissionDateComparatorOldToNew.compare(
					protest_Info1, protest_Info2) : result;
		}

	};

	/**
	 * @return the public_decision_date
	 */
	public Date getPublic_decision_date() {
		return public_decision_date;
	}

	/**
	 * @param public_decision_date the public_decision_date to set
	 */
	public void setPublic_decision_date(Date public_decision_date) {
		this.public_decision_date = public_decision_date;
	}

	/**
	 * @return the caseCompletionStatus
	 */
	public CaseCompletionStatus getCaseCompletionStatus() {
		return caseCompletionStatus;
	}

	/**
	 * @param caseCompletionStatus the caseCompletionStatus to set
	 */
	public void setCaseCompletionStatus(CaseCompletionStatus caseCompletionStatus) {
		this.caseCompletionStatus = caseCompletionStatus;
	}

	public Integer getPayDotGovTransactionAmt() {
		return payDotGovTransactionAmt;
	}

	public void setPayDotGovTransactionAmt(Integer payDotGovTransactionAmt) {
		this.payDotGovTransactionAmt = payDotGovTransactionAmt;
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

	

	public String getRepresentative_address1() {
		return representative_address1;
	}

	public void setRepresentative_address1(String representative_address1) {
		this.representative_address1 = representative_address1;
	}

	public String getRepresentative_address2() {
		return representative_address2;
	}

	public void setRepresentative_address2(String representative_address2) {
		this.representative_address2 = representative_address2;
	}

	
	/**
	 * @return the agencyRepInfo
	 */
	public List<AgencyRepInfo> getAgencyRepInfos() {
		return agencyRepInfos;
	}
	
	/**
	 * @param agencyRepInfos the agencyRepInfo to set
	 */
	public void setAgencyRepInfos(List<AgencyRepInfo> agencyRepInfos) {
		this.agencyRepInfos = agencyRepInfos;
	}

	

	/**
	 * @return the primaryAgencyInfoIds
	 */
	public List<Integer> getPrimaryAgencyInfoIds() {
		return primaryAgencyInfoIds;
	}

	/**
	 * @param primaryAgencyInfoIds the primaryAgencyInfoIds to set
	 */
	public void setPrimaryAgencyInfoIds(List<Integer> primaryAgencyInfoIds) {
		this.primaryAgencyInfoIds = primaryAgencyInfoIds;
	}


	/**
	 * @return the transaction_Status
	 */
	public String getTransaction_Status() {
		return transaction_Status;
	}

	/**
	 * @param transaction_Status the transaction_Status to set
	 */
	public void setTransaction_Status(String transaction_Status) {
		this.transaction_Status = transaction_Status;
	}

	/**
	 * @return the reasonForDeletion
	 */
	public String getReasonForDeletion() {
		return reasonForDeletion;
	}

	/**
	 * @param reasonForDeletion the reasonForDeletion to set
	 */
	public void setReasonForDeletion(String reasonForDeletion) {
		this.reasonForDeletion = reasonForDeletion;
	}


	/**
	 * @return the submissionDateTime
	 */
	public Long getSubmissionDateTime() {
		return submissionDateTime;
	}

	/**
	 * @param submissionDateTime the submissionDateTime to set
	 */
	public void setSubmissionDateTime(Long submissionDateTime) {
		this.submissionDateTime = submissionDateTime;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a_No == null) ? 0 : a_No.hashCode());
		result = prime * result + ((agencyRepInfos == null) ? 0 : agencyRepInfos.hashCode());
		result = prime * result + agency_Info_Id;
		result = prime * result + ((agency_Name == null) ? 0 : agency_Name.hashCode());
		result = prime * result + ((agency_tracking_id == null) ? 0 : agency_tracking_id.hashCode());
		result = prime * result + ((attorney_Group_Id == null) ? 0 : attorney_Group_Id.hashCode());
		result = prime * result + ((attorney_Name == null) ? 0 : attorney_Name.hashCode());
		result = prime * result + ((b_No == null) ? 0 : b_No.hashCode());
		result = prime * result + ((caseAccessRequestStatus == null) ? 0 : caseAccessRequestStatus.hashCode());
		result = prime * result + ((caseAccessRequestType == null) ? 0 : caseAccessRequestType.hashCode());
		result = prime * result + ((caseCompletionStatus == null) ? 0 : caseCompletionStatus.hashCode());
		result = prime * result + ((case_Status == null) ? 0 : case_Status.hashCode());
		result = prime * result + ((case_Type == null) ? 0 : case_Type.hashCode());
		result = prime * result
				+ ((casedocket_email_preferences == null) ? 0 : casedocket_email_preferences.hashCode());
		result = prime * result + ((children_Protest_InfoList == null) ? 0 : children_Protest_InfoList.hashCode());
		result = prime * result + ((comments == null) ? 0 : comments.hashCode());
		result = prime * result
				+ ((companyNameUserRepresentingTo == null) ? 0 : companyNameUserRepresentingTo.hashCode());
		result = prime * result + ((company_City == null) ? 0 : company_City.hashCode());
		result = prime * result + ((company_Country == null) ? 0 : company_Country.hashCode());
		result = prime * result + ((company_Name == null) ? 0 : company_Name.hashCode());
		result = prime * result + ((company_State == null) ? 0 : company_State.hashCode());
		result = prime * result + ((company_Status == null) ? 0 : company_Status.hashCode());
		result = prime * result + ((company_Street == null) ? 0 : company_Street.hashCode());
		result = prime * result + ((company_Zipcode == null) ? 0 : company_Zipcode.hashCode());
		result = prime * result + ((company_address1 == null) ? 0 : company_address1.hashCode());
		result = prime * result + ((company_address2 == null) ? 0 : company_address2.hashCode());
		result = prime * result + ((deniedDate == null) ? 0 : deniedDate.hashCode());
		result = prime * result + ((deniedIndicatingDocTypeId == null) ? 0 : deniedIndicatingDocTypeId.hashCode());
		result = prime * result + ((due_Date == null) ? 0 : due_Date.hashCode());
		result = prime * result + ((isUserAdmittedToPO == null) ? 0 : isUserAdmittedToPO.hashCode());
		result = prime * result + ((isUserConsolidated == null) ? 0 : isUserConsolidated.hashCode());
		result = prime * result + (isViewOnly ? 1231 : 1237);
		result = prime * result + ((last_parent_a_no == null) ? 0 : last_parent_a_no.hashCode());
		result = prime * result
				+ ((listOf_ConsolidatedProtest_Info == null) ? 0 : listOf_ConsolidatedProtest_Info.hashCode());
		result = prime * result + ((parent_A_No == null) ? 0 : parent_A_No.hashCode());
		result = prime * result + ((payDotGovTransactionAmt == null) ? 0 : payDotGovTransactionAmt.hashCode());
		result = prime * result + ((pay_dot_gov_id == null) ? 0 : pay_dot_gov_id.hashCode());
		result = prime * result + ((po == null) ? 0 : po.hashCode());
		result = prime * result + ((primaryAgencyInfoIds == null) ? 0 : primaryAgencyInfoIds.hashCode());
		result = prime * result + ((public_decision_date == null) ? 0 : public_decision_date.hashCode());
		result = prime * result + ((reasonForDeletion == null) ? 0 : reasonForDeletion.hashCode());
		result = prime * result + ((representative_City == null) ? 0 : representative_City.hashCode());
		result = prime * result + ((representative_Country == null) ? 0 : representative_Country.hashCode());
		result = prime * result + ((representative_Email == null) ? 0 : representative_Email.hashCode());
		result = prime * result + ((representative_Fax_No == null) ? 0 : representative_Fax_No.hashCode());
		result = prime * result + ((representative_First_Name == null) ? 0 : representative_First_Name.hashCode());
		result = prime * result + ((representative_Last_Name == null) ? 0 : representative_Last_Name.hashCode());
		result = prime * result + ((representative_Phone_No == null) ? 0 : representative_Phone_No.hashCode());
		result = prime * result + ((representative_State == null) ? 0 : representative_State.hashCode());
		result = prime * result + ((representative_Street == null) ? 0 : representative_Street.hashCode());
		result = prime * result + ((representative_Zipcode == null) ? 0 : representative_Zipcode.hashCode());
		result = prime * result + ((representative_address1 == null) ? 0 : representative_address1.hashCode());
		result = prime * result + ((representative_address2 == null) ? 0 : representative_address2.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		result = prime * result + roleId;
		result = prime * result + ((solicitation_No == null) ? 0 : solicitation_No.hashCode());
		result = prime * result + ((submissionDateTime == null) ? 0 : submissionDateTime.hashCode());
		result = prime * result + ((submission_Date == null) ? 0 : submission_Date.hashCode());
		result = prime * result + ((supplemental_A_Nos == null) ? 0 : supplemental_A_Nos.hashCode());
		result = prime * result + ((supplemental_B_Nos == null) ? 0 : supplemental_B_Nos.hashCode());
		result = prime * result + ((transaction_Status == null) ? 0 : transaction_Status.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Protest_Info))
			return false;
		Protest_Info other = (Protest_Info) obj;
		if (a_No == null) {
			if (other.a_No != null)
				return false;
		} else if (!a_No.equals(other.a_No))
			return false;
		if (agencyRepInfos == null) {
			if (other.agencyRepInfos != null)
				return false;
		} else if (!agencyRepInfos.equals(other.agencyRepInfos))
			return false;
		if (agency_Info_Id != other.agency_Info_Id)
			return false;
		if (agency_Name == null) {
			if (other.agency_Name != null)
				return false;
		} else if (!agency_Name.equals(other.agency_Name))
			return false;
		if (agency_tracking_id == null) {
			if (other.agency_tracking_id != null)
				return false;
		} else if (!agency_tracking_id.equals(other.agency_tracking_id))
			return false;
		if (attorney_Group_Id == null) {
			if (other.attorney_Group_Id != null)
				return false;
		} else if (!attorney_Group_Id.equals(other.attorney_Group_Id))
			return false;
		if (attorney_Name == null) {
			if (other.attorney_Name != null)
				return false;
		} else if (!attorney_Name.equals(other.attorney_Name))
			return false;
		if (b_No == null) {
			if (other.b_No != null)
				return false;
		} else if (!b_No.equals(other.b_No))
			return false;
		if (caseAccessRequestStatus == null) {
			if (other.caseAccessRequestStatus != null)
				return false;
		} else if (!caseAccessRequestStatus.equals(other.caseAccessRequestStatus))
			return false;
		if (caseAccessRequestType == null) {
			if (other.caseAccessRequestType != null)
				return false;
		} else if (!caseAccessRequestType.equals(other.caseAccessRequestType))
			return false;
		if (caseCompletionStatus == null) {
			if (other.caseCompletionStatus != null)
				return false;
		} else if (!caseCompletionStatus.equals(other.caseCompletionStatus))
			return false;
		if (case_Status == null) {
			if (other.case_Status != null)
				return false;
		} else if (!case_Status.equals(other.case_Status))
			return false;
		if (case_Type == null) {
			if (other.case_Type != null)
				return false;
		} else if (!case_Type.equals(other.case_Type))
			return false;
		if (casedocket_email_preferences == null) {
			if (other.casedocket_email_preferences != null)
				return false;
		} else if (!casedocket_email_preferences.equals(other.casedocket_email_preferences))
			return false;
		if (children_Protest_InfoList == null) {
			if (other.children_Protest_InfoList != null)
				return false;
		} else if (!children_Protest_InfoList.equals(other.children_Protest_InfoList))
			return false;
		if (comments == null) {
			if (other.comments != null)
				return false;
		} else if (!comments.equals(other.comments))
			return false;
		if (companyNameUserRepresentingTo == null) {
			if (other.companyNameUserRepresentingTo != null)
				return false;
		} else if (!companyNameUserRepresentingTo.equals(other.companyNameUserRepresentingTo))
			return false;
		if (company_City == null) {
			if (other.company_City != null)
				return false;
		} else if (!company_City.equals(other.company_City))
			return false;
		if (company_Country == null) {
			if (other.company_Country != null)
				return false;
		} else if (!company_Country.equals(other.company_Country))
			return false;
		if (company_Name == null) {
			if (other.company_Name != null)
				return false;
		} else if (!company_Name.equals(other.company_Name))
			return false;
		if (company_State == null) {
			if (other.company_State != null)
				return false;
		} else if (!company_State.equals(other.company_State))
			return false;
		if (company_Status == null) {
			if (other.company_Status != null)
				return false;
		} else if (!company_Status.equals(other.company_Status))
			return false;
		if (company_Street == null) {
			if (other.company_Street != null)
				return false;
		} else if (!company_Street.equals(other.company_Street))
			return false;
		if (company_Zipcode == null) {
			if (other.company_Zipcode != null)
				return false;
		} else if (!company_Zipcode.equals(other.company_Zipcode))
			return false;
		if (company_address1 == null) {
			if (other.company_address1 != null)
				return false;
		} else if (!company_address1.equals(other.company_address1))
			return false;
		if (company_address2 == null) {
			if (other.company_address2 != null)
				return false;
		} else if (!company_address2.equals(other.company_address2))
			return false;
		if (deniedDate == null) {
			if (other.deniedDate != null)
				return false;
		} else if (!deniedDate.equals(other.deniedDate))
			return false;
		if (deniedIndicatingDocTypeId == null) {
			if (other.deniedIndicatingDocTypeId != null)
				return false;
		} else if (!deniedIndicatingDocTypeId.equals(other.deniedIndicatingDocTypeId))
			return false;
		if (due_Date == null) {
			if (other.due_Date != null)
				return false;
		} else if (!due_Date.equals(other.due_Date))
			return false;
		if (isUserAdmittedToPO == null) {
			if (other.isUserAdmittedToPO != null)
				return false;
		} else if (!isUserAdmittedToPO.equals(other.isUserAdmittedToPO))
			return false;
		if (isUserConsolidated == null) {
			if (other.isUserConsolidated != null)
				return false;
		} else if (!isUserConsolidated.equals(other.isUserConsolidated))
			return false;
		if (isViewOnly != other.isViewOnly)
			return false;
		if (last_parent_a_no == null) {
			if (other.last_parent_a_no != null)
				return false;
		} else if (!last_parent_a_no.equals(other.last_parent_a_no))
			return false;
		if (listOf_ConsolidatedProtest_Info == null) {
			if (other.listOf_ConsolidatedProtest_Info != null)
				return false;
		} else if (!listOf_ConsolidatedProtest_Info.equals(other.listOf_ConsolidatedProtest_Info))
			return false;
		if (parent_A_No == null) {
			if (other.parent_A_No != null)
				return false;
		} else if (!parent_A_No.equals(other.parent_A_No))
			return false;
		if (payDotGovTransactionAmt == null) {
			if (other.payDotGovTransactionAmt != null)
				return false;
		} else if (!payDotGovTransactionAmt.equals(other.payDotGovTransactionAmt))
			return false;
		if (pay_dot_gov_id == null) {
			if (other.pay_dot_gov_id != null)
				return false;
		} else if (!pay_dot_gov_id.equals(other.pay_dot_gov_id))
			return false;
		if (po == null) {
			if (other.po != null)
				return false;
		} else if (!po.equals(other.po))
			return false;
		if (primaryAgencyInfoIds == null) {
			if (other.primaryAgencyInfoIds != null)
				return false;
		} else if (!primaryAgencyInfoIds.equals(other.primaryAgencyInfoIds))
			return false;
		if (public_decision_date == null) {
			if (other.public_decision_date != null)
				return false;
		} else if (!public_decision_date.equals(other.public_decision_date))
			return false;
		if (reasonForDeletion == null) {
			if (other.reasonForDeletion != null)
				return false;
		} else if (!reasonForDeletion.equals(other.reasonForDeletion))
			return false;
		if (representative_City == null) {
			if (other.representative_City != null)
				return false;
		} else if (!representative_City.equals(other.representative_City))
			return false;
		if (representative_Country == null) {
			if (other.representative_Country != null)
				return false;
		} else if (!representative_Country.equals(other.representative_Country))
			return false;
		if (representative_Email == null) {
			if (other.representative_Email != null)
				return false;
		} else if (!representative_Email.equals(other.representative_Email))
			return false;
		if (representative_Fax_No == null) {
			if (other.representative_Fax_No != null)
				return false;
		} else if (!representative_Fax_No.equals(other.representative_Fax_No))
			return false;
		if (representative_First_Name == null) {
			if (other.representative_First_Name != null)
				return false;
		} else if (!representative_First_Name.equals(other.representative_First_Name))
			return false;
		if (representative_Last_Name == null) {
			if (other.representative_Last_Name != null)
				return false;
		} else if (!representative_Last_Name.equals(other.representative_Last_Name))
			return false;
		if (representative_Phone_No == null) {
			if (other.representative_Phone_No != null)
				return false;
		} else if (!representative_Phone_No.equals(other.representative_Phone_No))
			return false;
		if (representative_State == null) {
			if (other.representative_State != null)
				return false;
		} else if (!representative_State.equals(other.representative_State))
			return false;
		if (representative_Street == null) {
			if (other.representative_Street != null)
				return false;
		} else if (!representative_Street.equals(other.representative_Street))
			return false;
		if (representative_Zipcode == null) {
			if (other.representative_Zipcode != null)
				return false;
		} else if (!representative_Zipcode.equals(other.representative_Zipcode))
			return false;
		if (representative_address1 == null) {
			if (other.representative_address1 != null)
				return false;
		} else if (!representative_address1.equals(other.representative_address1))
			return false;
		if (representative_address2 == null) {
			if (other.representative_address2 != null)
				return false;
		} else if (!representative_address2.equals(other.representative_address2))
			return false;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		if (roleId != other.roleId)
			return false;
		if (solicitation_No == null) {
			if (other.solicitation_No != null)
				return false;
		} else if (!solicitation_No.equals(other.solicitation_No))
			return false;
		if (submissionDateTime == null) {
			if (other.submissionDateTime != null)
				return false;
		} else if (!submissionDateTime.equals(other.submissionDateTime))
			return false;
		if (submission_Date == null) {
			if (other.submission_Date != null)
				return false;
		} else if (!submission_Date.equals(other.submission_Date))
			return false;
		if (supplemental_A_Nos == null) {
			if (other.supplemental_A_Nos != null)
				return false;
		} else if (!supplemental_A_Nos.equals(other.supplemental_A_Nos))
			return false;
		if (supplemental_B_Nos == null) {
			if (other.supplemental_B_Nos != null)
				return false;
		} else if (!supplemental_B_Nos.equals(other.supplemental_B_Nos))
			return false;
		if (transaction_Status == null) {
			if (other.transaction_Status != null)
				return false;
		} else if (!transaction_Status.equals(other.transaction_Status))
			return false;
		return true;
	}

	/**
	 * @return the deniedFileId
	 */
	public Integer getDeniedFileId() {
		return deniedFileId;
	}

	/**
	 * @param deniedFileId the deniedFileId to set
	 */
	public void setDeniedFileId(Integer deniedFileId) {
		this.deniedFileId = deniedFileId;
	}

	
	

	
	
	

}
