package gov.gao.epds.dto;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Pattern;

/*
 * tier2AgencyId is used when looking for a specific agency
 * listOfTier2AgencyIds is used when search for all the tier2Agency from tier 1
*/ 
public class AdvanceSearchDTO extends DTOValidator {


	@Pattern(regexp = PROTEST_ID_PATTERN, message="Invalid input format!")
	private String a_No;

	@Pattern(regexp = PROTEST_ID_PATTERN, message="Invalid input format!")
	private String b_No;
	
	@Pattern(regexp = ALPHA_PATTERN, message="Invalid input format!")
	private String case_Status;
	
	@Pattern(regexp = ALPHA_PATTERN, message="Invalid input format!")
	private String case_Type;
	
	@Pattern(regexp = NAME_OF_FIRM_PATTERN, message="Invalid input format!")
	private String company_Name;
	
	@Pattern(regexp = NAME_OF_FIRM_PATTERN, message="Invalid input format!")
	private String partyInfo;
	
	@Pattern(regexp = NAME_OF_FIRM_PATTERN, message="Invalid input format!")
	private String intervenorCompName;
	
	private String startSubmission_Date;
	private String endSubmission_Date;
	private String startDue_Date;
	private String endDue_Date;
	
	//need to do more
	@Pattern(regexp = SOLICITATION_PATTERN, message="Invalid input format!")
	private String solicitation_No;
	
	@Pattern(regexp = GROUP_NO_PATTERN, message="Invalid input format!")
	private String lawGroup;
	
	@Pattern(regexp = USER_ID_PATTERN, message="Invalid input format!")
	private String attorneyId;
	
	@Pattern(regexp = AGENCYID_PATTERN, message="Invalid input format!")
	private String tier1AgencyId;
	
	@Pattern(regexp = AGENCYID_PATTERN, message="Invalid input format!")
	private String tier2AgencyId;
	
	private List<Integer> listOfAgencyInfoIds = new ArrayList<Integer>();
	
	private String partyUserIds;

	// only A#'s without a '.' in them
	private boolean onlyPrimaryANos;

	/**
	 * @return the a_No
	 */
	public String getA_No() {
		return a_No;
	}

	/**
	 * @param a_No the a_No to set
	 */
	public void setA_No(String a_No) {
		this.a_No = a_No;
	}

	/**
	 * @return the b_No
	 */
	public String getB_No() {
		return b_No;
	}

	/**
	 * @param b_No the b_No to set
	 */
	public void setB_No(String b_No) {
		this.b_No = b_No;
	}

	/**
	 * @return the case_Status
	 */
	public String getCase_Status() {
		return case_Status;
	}

	/**
	 * @param case_Status the case_Status to set
	 */
	public void setCase_Status(String case_Status) {
		this.case_Status = case_Status;
	}

	/**
	 * @return the company_Name
	 */
	public String getCompany_Name() {
		return company_Name;
	}

	/**
	 * @param company_Name the company_Name to set
	 */
	public void setCompany_Name(String company_Name) {
		this.company_Name = company_Name;
	}

	/**
	 * @return the startSubmission_Date
	 */
	public String getStartSubmission_Date() {
		return startSubmission_Date;
	}

	/**
	 * @param startSubmission_Date the startSubmission_Date to set
	 */
	public void setStartSubmission_Date(String startSubmission_Date) {
		this.startSubmission_Date = startSubmission_Date;
	}

	/**
	 * @return the endSubmission_Date
	 */
	public String getEndSubmission_Date() {
		return endSubmission_Date;
	}

	/**
	 * @param endSubmission_Date the endSubmission_Date to set
	 */
	public void setEndSubmission_Date(String endSubmission_Date) {
		this.endSubmission_Date = endSubmission_Date;
	}

	/**
	 * @return the startDue_Date
	 */
	public String getStartDue_Date() {
		return startDue_Date;
	}

	/**
	 * @param startDue_Date the startDue_Date to set
	 */
	public void setStartDue_Date(String startDue_Date) {
		this.startDue_Date = startDue_Date;
	}

	/**
	 * @return the endDue_Date
	 */
	public String getEndDue_Date() {
		return endDue_Date;
	}

	/**
	 * @param endDue_Date the endDue_Date to set
	 */
	public void setEndDue_Date(String endDue_Date) {
		this.endDue_Date = endDue_Date;
	}

	/**
	 * @return the solicitation_No
	 */
	public String getSolicitation_No() {
		return solicitation_No;
	}

	/**
	 * @param solicitation_No the solicitation_No to set
	 */
	public void setSolicitation_No(String solicitation_No) {
		this.solicitation_No = solicitation_No;
	}

	/**
	 * @return the lawGroup
	 */
	public String getLawGroup() {
		return lawGroup;
	}

	/**
	 * @param lawGroup the lawGroup to set
	 */
	public void setLawGroup(String lawGroup) {
		this.lawGroup = lawGroup;
	}

	/**
	 * @return the attorneyId
	 */
	public String getAttorneyId() {
		return attorneyId;
	}

	/**
	 * @param attorneyId the attorneyId to set
	 */
	public void setAttorneyId(String attorneyId) {
		this.attorneyId = attorneyId;
	}

	/**
	 * @return the tier1AgencyId
	 */
	public String getTier1AgencyId() {
		return tier1AgencyId;
	}

	/**
	 * @param tier1AgencyId the tier1AgencyId to set
	 */
	public void setTier1AgencyId(String tier1AgencyId) {
		this.tier1AgencyId = tier1AgencyId;
	}

	
	
	/**
	 * @return the tier2AgencyId
	 */
	public String getTier2AgencyId() {
		return tier2AgencyId;
	}

	/**
	 * @param tier2AgencyId the tier2AgencyId to set
	 */
	public void setTier2AgencyId(String tier2AgencyId) {
		this.tier2AgencyId = tier2AgencyId;
	}

 	
	
	public void addTier2Ids(Integer tier2Id){
		listOfAgencyInfoIds.add(tier2Id);
	}

	/**
	 * @return the case_Type
	 */
	public String getCase_Type() {
		return case_Type;
	}

	/**
	 * @param case_Type the case_Type to set
	 */
	public void setCase_Type(String case_Type) {
		this.case_Type = case_Type;
	}

	/**
	 * @return the listOfAgencyInfoIds
	 */
	public List<Integer> getListOfAgencyInfoIds() {
		return listOfAgencyInfoIds;
	}

	/**
	 * @param listOfAgencyInfoIds the listOfAgencyInfoIds to set
	 */
	public void setListOfAgencyInfoIds(List<Integer> listOfAgencyInfoIds) {
		this.listOfAgencyInfoIds = listOfAgencyInfoIds;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a_No == null) ? 0 : a_No.hashCode());
		result = prime * result
				+ ((attorneyId == null) ? 0 : attorneyId.hashCode());
		result = prime * result + ((b_No == null) ? 0 : b_No.hashCode());
		result = prime * result
				+ ((case_Status == null) ? 0 : case_Status.hashCode());
		result = prime * result
				+ ((case_Type == null) ? 0 : case_Type.hashCode());
		result = prime * result
				+ ((company_Name == null) ? 0 : company_Name.hashCode());
		result = prime * result
				+ ((endDue_Date == null) ? 0 : endDue_Date.hashCode());
		result = prime
				* result
				+ ((endSubmission_Date == null) ? 0 : endSubmission_Date
						.hashCode());
		result = prime * result
				+ ((lawGroup == null) ? 0 : lawGroup.hashCode());
		result = prime
				* result
				+ ((listOfAgencyInfoIds == null) ? 0 : listOfAgencyInfoIds
						.hashCode());
		result = prime * result
				+ ((solicitation_No == null) ? 0 : solicitation_No.hashCode());
		result = prime * result
				+ ((startDue_Date == null) ? 0 : startDue_Date.hashCode());
		result = prime
				* result
				+ ((startSubmission_Date == null) ? 0 : startSubmission_Date
						.hashCode());
		result = prime * result
				+ ((tier1AgencyId == null) ? 0 : tier1AgencyId.hashCode());
		result = prime * result
				+ ((tier2AgencyId == null) ? 0 : tier2AgencyId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AdvanceSearchDTO other = (AdvanceSearchDTO) obj;
		if (a_No == null) {
			if (other.a_No != null)
				return false;
		} else if (!a_No.equals(other.a_No))
			return false;
		if (attorneyId == null) {
			if (other.attorneyId != null)
				return false;
		} else if (!attorneyId.equals(other.attorneyId))
			return false;
		if (b_No == null) {
			if (other.b_No != null)
				return false;
		} else if (!b_No.equals(other.b_No))
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
		if (company_Name == null) {
			if (other.company_Name != null)
				return false;
		} else if (!company_Name.equals(other.company_Name))
			return false;
		if (endDue_Date == null) {
			if (other.endDue_Date != null)
				return false;
		} else if (!endDue_Date.equals(other.endDue_Date))
			return false;
		if (endSubmission_Date == null) {
			if (other.endSubmission_Date != null)
				return false;
		} else if (!endSubmission_Date.equals(other.endSubmission_Date))
			return false;
		if (lawGroup == null) {
			if (other.lawGroup != null)
				return false;
		} else if (!lawGroup.equals(other.lawGroup))
			return false;
		if (listOfAgencyInfoIds == null) {
			if (other.listOfAgencyInfoIds != null)
				return false;
		} else if (!listOfAgencyInfoIds.equals(other.listOfAgencyInfoIds))
			return false;
		if (solicitation_No == null) {
			if (other.solicitation_No != null)
				return false;
		} else if (!solicitation_No.equals(other.solicitation_No))
			return false;
		if (startDue_Date == null) {
			if (other.startDue_Date != null)
				return false;
		} else if (!startDue_Date.equals(other.startDue_Date))
			return false;
		if (startSubmission_Date == null) {
			if (other.startSubmission_Date != null)
				return false;
		} else if (!startSubmission_Date.equals(other.startSubmission_Date))
			return false;
		if (tier1AgencyId == null) {
			if (other.tier1AgencyId != null)
				return false;
		} else if (!tier1AgencyId.equals(other.tier1AgencyId))
			return false;
		if (tier2AgencyId == null) {
			if (other.tier2AgencyId != null)
				return false;
		} else if (!tier2AgencyId.equals(other.tier2AgencyId))
			return false;
		return true;
	}

	/**
	 * @return the partyInfo
	 */
	public String getPartyInfo() {
		return partyInfo;
	}

	/**
	 * @param partyInfo the partyInfo to set
	 */
	public void setPartyInfo(String partyInfo) {
		this.partyInfo = partyInfo;
	}

	/**
	 * @return the intervenorCompName
	 */
	public String getIntervenorCompName() {
		return intervenorCompName;
	}

	/**
	 * @param intervenorCompName the intervenorCompName to set
	 */
	public void setIntervenorCompName(String intervenorCompName) {
		this.intervenorCompName = intervenorCompName;
	}

	/**
	 * @return the partyUserIds
	 */
	public String getPartyUserIds() {
		return partyUserIds;
	}

	/**
	 * @param partyUserIds the partyUserIds to set
	 */
	public void setPartyUserIds(String partyUserIds) {
		this.partyUserIds = partyUserIds;
	}

	public boolean isOnlyPrimaryANos() {
		return onlyPrimaryANos;
	}

	public void setOnlyPrimaryANos(boolean onlyPrimaryANos) {
		this.onlyPrimaryANos = onlyPrimaryANos;
	}

}
