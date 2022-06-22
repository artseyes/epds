/**
 * 
 */
package gov.gao.epds.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.SafeHtml;


/**
 * @author MHussaini
 *
 */
public class DashboardDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4747277164540225367L;
	
	@SafeHtml
	@Pattern(regexp=DTOValidator.INTEGER_PATTERN)
	private String caseStatusList;
	@SafeHtml
	private List<Integer> attorneyGroupIds;
	@SafeHtml
	@Pattern(regexp=DTOValidator.INTEGER_PATTERN)
	private Integer startLimit;
	@SafeHtml
	@Pattern(regexp=DTOValidator.INTEGER_PATTERN)
	private Integer endLimit;
	
	@SafeHtml
	private List<String> alreadyAvailableANums;
	private boolean isFullReloadReq = true;
	
	
	/**
	 * @return the caseStatusList
	 */
	public String getCaseStatusList() {
		return caseStatusList;
	}
	/**
	 * @param caseStatusList the caseStatusList to set
	 */
	public void setCaseStatusList(String caseStatusList) {
		this.caseStatusList = caseStatusList;
	}
	
	/**
	 * @return the startLimit
	 */
	public Integer getStartLimit() {
		return startLimit;
	}
	/**
	 * @param startLimit the startLimit to set
	 */
	public void setStartLimit(Integer startLimit) {
		this.startLimit = startLimit;
	}
	/**
	 * @return the endLimit
	 */
	public Integer getEndLimit() {
		return endLimit;
	}
	/**
	 * @param endLimit the endLimit to set
	 */
	public void setEndLimit(Integer endLimit) {
		this.endLimit = endLimit;
	}
	/**
	 * @return the alreadyAvailableANums
	 */
	public List<String> getAlreadyAvailableANums() {
		return alreadyAvailableANums;
	}
	/**
	 * @param alreadyAvailableANums the alreadyAvailableANums to set
	 */
	public void setAlreadyAvailableANums(List<String> alreadyAvailableANums) {
		this.alreadyAvailableANums = alreadyAvailableANums;
	}
	/**
	 * @return the isFullReloadReq
	 */
	public boolean isFullReloadReq() {
		return isFullReloadReq;
	}
	/**
	 * @param isFullReloadReq the isFullReloadReq to set
	 */
	public void setFullReloadReq(boolean isFullReloadReq) {
		this.isFullReloadReq = isFullReloadReq;
	}
	/**
	 * @return the attorneyGroupIds
	 */
	public List<Integer> getAttorneyGroupIds() {
		return attorneyGroupIds;
	}
	/**
	 * @param attorneyGroupIds the attorneyGroupIds to set
	 */
	public void setAttorneyGroupIds(List<Integer> attorneyGroupIds) {
		this.attorneyGroupIds = attorneyGroupIds;
	}
	
	
	
	
	
}
