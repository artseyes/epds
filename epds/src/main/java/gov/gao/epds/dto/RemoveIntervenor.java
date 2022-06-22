package gov.gao.epds.dto;

import java.io.Serializable;

/**
 * @author MHussaini
 *
 */
public class RemoveIntervenor extends DTOValidator implements Serializable{

	
	
	private static final long serialVersionUID = -348579801262405506L;
	private String userId;
	private CompanyInfo  companyInfo;
	
	
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * @return the companyInfo
	 */
	public CompanyInfo getCompanyInfo() {
		return companyInfo;
	}
	/**
	 * @param companyInfo the companyInfo to set
	 */
	public void setCompanyInfo(CompanyInfo companyInfo) {
		this.companyInfo = companyInfo;
	}
	
}
