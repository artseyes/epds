/**
 * 
 */
package gov.gao.epds.gctrack;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author MHussaini
 *
 */
@JsonInclude(Include.NON_NULL)
public class RepInfo extends PartyInfo{

	private String first_Name;
	private String middle_initial;
	private String last_Name;
	private String firm_Name;
	private Integer firm_id;
	private String email;
	private String prefix;
	private String suffix;
	
	private String partyType;
	
	
	
	public String getFirst_Name() {
		return first_Name;
	}
	public void setFirst_Name(String first_Name) {
		this.first_Name = first_Name;
	}
	public String getMiddle_initial() {
		return middle_initial;
	}
	public void setMiddle_initial(String middle_initial) {
		this.middle_initial = middle_initial;
	}
	public String getLast_Name() {
		return last_Name;
	}
	public void setLast_Name(String last_Name) {
		this.last_Name = last_Name;
	}
	public String getFirm_Name() {
		return firm_Name;
	}
	public void setFirm_Name(String firm_Name) {
		this.firm_Name = firm_Name;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	public String getPartyType() {
		return partyType;
	}
	public void setPartyType(String partyType) {
		this.partyType = partyType;
	}
	public Integer getFirm_id() {
		return firm_id;
	}
	public void setFirm_id(Integer firm_id) {
		this.firm_id = firm_id;
	}
	
	
}
