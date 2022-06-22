

package gov.gao.epds.persistence.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;

import org.hibernate.envers.Audited;

/*@GenericGenerator(
        name = "sequenceGenerator", 
        strategy = "enhanced-sequence",
        parameters = {
        	@org.hibernate.annotations.Parameter(name="prefer_sequence_per_entity", value="true"),
        	
            @org.hibernate.annotations.Parameter(
                name = "optimizer",
                value = "none"
            ),
            @org.hibernate.annotations.Parameter(
                name = "initial_value", 
                value = "1"
            ),
            @org.hibernate.annotations.Parameter(
                name = "increment_size", 
                value = "5"
            )
        }
    )*/
@Entity
@Table
@Audited
public class User_Protest_Role_Bridge implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -13206893025408162L;

	
	@Id
	@Column
	@GenericGenerator(
	        name = "sequenceGenerator", 
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
	        generator = "sequenceGenerator"
	    )
	private Long id;

	@Column
	private String a_No;

	@Column
	private String user_Id;

	@Column
	private String po;

	@Column
	private int role_Id;

	@Column
	private String intervenor_Company_Name;

	@Column
	private String intervenor_Company_Address;

	@Column
	private String consolidated_A_No;

	@Column
	private String casedocket_email_preferences;

	@Transient
	private String roleDesc;

	public String getRoleDesc() {
		return roleDesc;
	}

	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}

	public User_Protest_Role_Bridge() {
		super();
	}

	public User_Protest_Role_Bridge(
			User_Protest_Role_Bridge userProtest_Role_Bridge) {
		this.a_No = userProtest_Role_Bridge.getA_No();
		this.user_Id = userProtest_Role_Bridge.getUser_Id();
		this.po = userProtest_Role_Bridge.getPo();
		this.role_Id = userProtest_Role_Bridge.getRole_Id();
		this.intervenor_Company_Name = userProtest_Role_Bridge
				.getIntervenor_Company_Name();
		this.intervenor_Company_Address = userProtest_Role_Bridge
				.getIntervenor_Company_Address();
		this.consolidated_A_No = userProtest_Role_Bridge.getConsolidated_A_No();
		this.casedocket_email_preferences = userProtest_Role_Bridge
				.getCasedocket_email_preferences();
	}

	public String getConsolidated_A_No() {
		return consolidated_A_No;
	}

	public void setConsolidated_A_No(String consolidated_A_No) {
		this.consolidated_A_No = consolidated_A_No;
	}

	public String getIntervenor_Company_Address() {
		return intervenor_Company_Address;
	}

	public void setIntervenor_Company_Address(String intervenor_Company_Address) {
		this.intervenor_Company_Address = intervenor_Company_Address;
	}

	/**
	 * It returns intervenor or agency rep company name
	 * @return
	 */
	public String getIntervenor_Company_Name() {
		return intervenor_Company_Name;
	}

	/**
	 * Amer: since now we are going to have two agencies we need to start storing company name in the database when assigning agency reps.
	 * 
	 * We can refactor this column in the database to just say companyName ...but for now I will just use Intervenor_Company_Name column to store both intervenor as well as agency representative company name
	 * @param intervenor_Company_Name
	 */
	public void setIntervenor_Company_Name(String intervenor_Company_Name) {
		this.intervenor_Company_Name = intervenor_Company_Name;
	}

	

	public String getA_No() {
		return a_No;
	}

	public void setA_No(String a_No) {
		this.a_No = a_No;
	}

	public String getUser_Id() {
		return user_Id;
	}

	public void setUser_Id(String user_Id) {
		this.user_Id = user_Id;
	}

	public String getPo() {
		return po;
	}

	public void setPo(String po) {
		this.po = po;
	}

	public int getRole_Id() {
		return role_Id;
	}

	public void setRole_Id(int role_Id) {
		this.role_Id = role_Id;
	}

	/**
	 * @return the casedocket_email_preferences
	 */

	public String getCasedocket_email_preferences() {
		return casedocket_email_preferences;
	}

	/**
	 * @param casedocket_email_preferences
	 *            the casedocket_email_preferences to set
	 */
	public void setCasedocket_email_preferences(
			String casedocket_email_preferences) {
		this.casedocket_email_preferences = casedocket_email_preferences;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
}
