package gov.gao.epds.persistence.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class Tier_1_Agency implements Serializable{

	private static final long serialVersionUID = -6319984908285554056L;

	@Id
	@Column
	private int agency_Id;

	@Column
	private String agency_Name;

	@Column
	private String phone_No;

	@Column(name="access_flag", length=20)
	private String tier2AgencyAccessFlag;

	/*@JsonIgnore
	@OneToMany(mappedBy="tier_1_Agency_Id")
	private Collection<Tier_2_Agency> tier_1_Agency_Id;
	*/
	
	public int getAgency_Id() {
		return agency_Id;
	}

	public void setAgency_Id(int agency_Id) {
		this.agency_Id = agency_Id;
	}

	public String getAgency_Name() {
		return agency_Name;
	}

	public void setAgency_Name(String agency_Name) {
		this.agency_Name = agency_Name;
	}

	public String getPhone_No() {
		return phone_No;
	}

	public void setPhone_No(String phone_No) {
		this.phone_No = phone_No;
	}

	/** The value can be "ALL"
	 * ALL --"All the tier 2 agencies have equal access to other agencies in the tier
	 * @return the tier2AgencyAccessFlag - flag to determine tier2 Agency access
	 */
	public String getTier2AgencyAccessFlag() {
		return tier2AgencyAccessFlag;
	}

	/**
	 * @param tier2AgencyAccessFlag the tier2AgencyAccessFlag to set
	 */
	public void setTier2AgencyAccessFlag(String tier2AgencyAccessFlag) {
		this.tier2AgencyAccessFlag = tier2AgencyAccessFlag;
	}

	

	

	
	/*public Collection<Tier_2_Agency> getTier2AgenciesByTier1AgencyId() {
		return tier_1_Agency_Id;
	}

	public void setTier2AgenciesByTier1AgencyId(
			Collection<Tier_2_Agency> tier_1_Agency_Id) {
		this.tier_1_Agency_Id = tier_1_Agency_Id;
	}*/
	
	
	

}
