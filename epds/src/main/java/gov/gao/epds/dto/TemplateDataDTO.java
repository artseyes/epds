package gov.gao.epds.dto;

import gov.gao.epds.persistence.entity.Protest_Info;
import gov.gao.epds.persistence.entity.User_Info;

import org.jsoup.safety.Whitelist;

public class TemplateDataDTO {
	private String filePath;
	private Whitelist whiteList;
	private Protest_Info protestInfo;
	private User_Info attorneyInfo;
	private User_Info primaryProtesterRepInfo;
	private User_Info agencyRepInfo;

	private String consolidateBNums;
	private String consolidatedProtesterNames;
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Whitelist getWhiteList() {
		return whiteList;
	}

	public void setWhiteList(Whitelist whiteList) {
		this.whiteList = whiteList;
	}

	public Protest_Info getProtestInfo() {
		return protestInfo;
	}

	public void setProtestInfo(Protest_Info protestInfo) {
		this.protestInfo = protestInfo;
	}

	public User_Info getAttorneyInfo() {
		return attorneyInfo;
	}

	public void setAttorneyInfo(User_Info attorneyInfo) {
		this.attorneyInfo = attorneyInfo;
	}

	public User_Info getPrimaryProtesterRepInfo() {
		return primaryProtesterRepInfo;
	}

	public void setPrimaryProtesterRepInfo(User_Info primaryProtesterRepInfo) {
		this.primaryProtesterRepInfo = primaryProtesterRepInfo;
	}

	public User_Info getAgencyRepInfo() {
		return agencyRepInfo;
	}

	public void setAgencyRepInfo(User_Info agencyRepInfo) {
		this.agencyRepInfo = agencyRepInfo;
	}

	/**
	 * @return the consolidateBNums
	 */
	public String getConsolidateBNums() {
		return consolidateBNums;
	}

	/**
	 * @param consolidateBNums the consolidateBNums to set
	 */
	public void setConsolidateBNums(String consolidateBNums) {
		this.consolidateBNums = consolidateBNums;
	}

	/**
	 * @return the consolidatedProtesterNames
	 */
	public String getConsolidatedProtesterNames() {
		return consolidatedProtesterNames;
	}

	/**
	 * @param consolidatedProtesterNames the consolidatedProtesterNames to set
	 */
	public void setConsolidatedProtesterNames(String consolidatedProtesterNames) {
		this.consolidatedProtesterNames = consolidatedProtesterNames;
	}

}
