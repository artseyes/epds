/**
 * 
 */
package gov.gao.epds.dto;

import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author MHussaini
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EditPartyInfo extends DTOValidator{

	
	private String aNum;
	@Pattern(regexp = NAME_OF_FIRM_PATTERN, message="Invalid input format!")
	private String companyName;
	private String address1;
	private String address2;
	private String city;
	private String state;
	@Pattern(regexp =COUNTRY_PATTERN, message="Invalid input format!")
	private String country;
	@Pattern(regexp =ZIPCODE_PATTERN, message="Invalid input format!")
	private String zipCode;
	private Integer intervenorFileId;
	@Pattern(regexp =ALPHA_PATTERN, message="Invalid input format!")
	private String partyType;
	@Pattern(regexp =INTEGER_PATTERN, message="Invalid input format!")
	private String userId;
	@Pattern(regexp = NAME_OF_FIRM_PATTERN, message="Invalid input format!")
	private String oldCompanyName;
	
	
	/**
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}
	/**
	 * @param companyName the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	/**
	 * @return the address1
	 */
	public String getAddress1() {
		return address1;
	}
	/**
	 * @param address1 the address1 to set
	 */
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	/**
	 * @return the address2
	 */
	public String getAddress2() {
		return address2;
	}
	/**
	 * @param address2 the address2 to set
	 */
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}
	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}
	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	/**
	 * @return the zipCode
	 */
	public String getZipCode() {
		return zipCode;
	}
	/**
	 * @param zipCode the zipCode to set
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	/**
	 * @return the intervenorFileId
	 */
	public Integer getIntervenorFileId() {
		return intervenorFileId;
	}
	/**
	 * @param intervenorFileId the intervenorFileId to set
	 */
	public void setIntervenorFileId(Integer intervenorFileId) {
		this.intervenorFileId = intervenorFileId;
	}
	/**
	 * @return the partyType
	 */
	public String getPartyType() {
		return partyType;
	}
	/**
	 * @param partyType the partyType to set
	 */
	public void setPartyType(String partyType) {
		this.partyType = partyType;
	}
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
	/**
	 * @return the oldCompanyName
	 */
	public String getOldCompanyName() {
		return oldCompanyName;
	}
	/**
	 * @param oldCompanyName the oldCompanyName to set
	 */
	public void setOldCompanyName(String oldCompanyName) {
		this.oldCompanyName = oldCompanyName;
	}
	
}
