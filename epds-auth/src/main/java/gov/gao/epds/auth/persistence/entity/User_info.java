package gov.gao.epds.auth.persistence.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Entity
@Table
@Audited
public class User_info implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1447334663667601690L;
	@Id
	/*@GeneratedValue(strategy = GenerationType.TABLE)*/
	@GenericGenerator(
	        name = "userInfoSeqGen", 
	        strategy = "enhanced-sequence",
	        parameters = {
	        		
	        	@org.hibernate.annotations.Parameter(name="prefer_sequence_per_entity", value="true"),	
	        		
	            @org.hibernate.annotations.Parameter(
	                name = "optimizer",
	                value = "pooled-lo"
	            ),
	            @org.hibernate.annotations.Parameter(
	                name = "initial_value", 
	                value = "1"
	            ),
	            @org.hibernate.annotations.Parameter(
	                name = "increment_size", 
	                value = "1"
	            )
	        }
	    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE, 
        generator = "userInfoSeqGen"
    )
	@Column
	private Integer user_id;
	@Column
	private String email;
	@Column
	@NotAudited
	private String password;
	@Column
	private Integer role_id;
	@Column
	private Integer account_status_id;
	@Column
	@NotAudited
	private String password_history;
	@Column
	private String first_name;
	@Column
	private String last_name;
	@Column
	private String firm_name;
	@Column
	private String phone_no;
	@Column
	private String fax_no;
	@Column
	private String city;
	@Column
	private String state;
	@Column
	private String country;
	@Column
	private String zip_code;
	@Column
	private Integer firm_id;

	@Column
	private String address1;
	@Column
	private String address2;
	@Column
	private String middle_initial;
	@Column
	private String prefix;
	@Column
	private String suffix;
	
	@Column
	private String previous_email;

	
	
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

	public String getMiddle_initial() {
		return middle_initial;
	}

	public void setMiddle_initial(String middle_initial) {
		this.middle_initial = middle_initial;
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

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getFirm_name() {
		return firm_name;
	}

	public void setFirm_name(String firm_name) {
		this.firm_name = firm_name;
	}

	public String getPhone_no() {
		return phone_no;
	}

	public void setPhone_no(String phone_no) {
		this.phone_no = phone_no;
	}

	public String getFax_no() {
		return fax_no;
	}

	public void setFax_no(String fax_no) {
		this.fax_no = fax_no;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getZip_code() {
		return zip_code;
	}

	public void setZip_code(String zip_code) {
		this.zip_code = zip_code;
	}

	public Integer getFirm_id() {
		return firm_id;
	}

	public void setFirm_id(Integer firm_id) {
		this.firm_id = firm_id;
	}

	public String getPassword_history() {
		return password_history;
	}

	public void setPassword_history(String password_history) {
		this.password_history = password_history;
	}

	public Integer getRole_id() {
		return role_id;
	}

	public void setRole_id(Integer role_id) {
		this.role_id = role_id;
	}

	public Integer getUser_id() {
		return user_id;
	}

	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getAccount_status_id() {
		return account_status_id;
	}

	public void setAccount_status_id(Integer account_status_id) {
		this.account_status_id = account_status_id;
	}

	public String getPrevious_email() {
		return previous_email;
	}

	public void setPrevious_email(String previous_email) {
		this.previous_email = previous_email;
	}

	


}
