/**
 * 
 */
package gov.gao.epds.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author MHussaini
 *
 */
public class EditProtestInfo extends DTOValidator implements Serializable{

	
	private static final long serialVersionUID = 2827145887471423132L;
	
	@NotNull
	private String newValue;
	private String newValue2;
	private String listOfBNumbers;
	@NotNull
	private String oldValue;
	
	private String aNum;
	
	@Pattern(regexp = AGENCYID_PATTERN, message="Invalid input format!")
	private String agency_tier1;
	@Pattern(regexp = AGENCYID_PATTERN, message="Invalid input format!")
	private String agency_tier2;
	
	
	public String getNewValue() {
		return newValue;
	}
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
	public String getNewValue2() {
		return newValue2;
	}
	public void setNewValue2(String newValue2) {
		this.newValue2 = newValue2;
	}
	public String getListOfBNumbers() {
		return listOfBNumbers;
	}
	public void setListOfBNumbers(String listOfBNumbers) {
		this.listOfBNumbers = listOfBNumbers;
	}
	public String getOldValue() {
		return oldValue;
	}
	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}
	public String getAgency_tier1() {
		return agency_tier1;
	}
	public void setAgency_tier1(String agency_tier1) {
		this.agency_tier1 = agency_tier1;
	}
	public String getAgency_tier2() {
		return agency_tier2;
	}
	public void setAgency_tier2(String agency_tier2) {
		this.agency_tier2 = agency_tier2;
	}
	/**
	 * @return the aNum
	 */
	public String getaNum() {
		return aNum;
	}
	/**
	 * @param aNum the aNum to set
	 */
	public void setaNum(String aNum) {
		this.aNum = aNum;
	}
	
	
	
	
	/*@RequestParam("newValue") String newValue,
	@RequestParam(value = "newValue2", required = false) String newValue2,
	@RequestParam(value = "listOfBNumbers", required = false) String listOfBNumbers,
	@RequestParam("oldValue") String oldValue,
	@RequestParam(value = "agency_tier1", required = false) String agencyTier_1_Id,
	@RequestParam(value = "agency_tier2", required = false) String agencyTier_2_Id*/

}
