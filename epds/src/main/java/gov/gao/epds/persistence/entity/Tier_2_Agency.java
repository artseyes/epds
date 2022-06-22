package gov.gao.epds.persistence.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class Tier_2_Agency implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8042444859331070772L;

	@Id
	@Column
	private int agency_Id;

	@Column
	private String agency_Name;

	@Column
	private String phone_No;

	/*@JsonIgnore
	@ManyToOne
    @JoinColumn(name="tier_1_Agency_Id")
	private Tier_1_Agency tier_1_Agency_Id;*/

	@Column
	private int tier_1_Agency_Id;
	
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

	public int getTier_1_Agency_Id() {
		return tier_1_Agency_Id;
	}

	public void setTier_1_Agency_Id(int tier_1_Agency_Id) {
		this.tier_1_Agency_Id = tier_1_Agency_Id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + agency_Id;
		result = prime * result + ((agency_Name == null) ? 0 : agency_Name.hashCode());
		result = prime * result + ((phone_No == null) ? 0 : phone_No.hashCode());
		result = prime * result + tier_1_Agency_Id;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Tier_2_Agency))
			return false;
		Tier_2_Agency other = (Tier_2_Agency) obj;
		if (agency_Id != other.agency_Id)
			return false;
		if (agency_Name == null) {
			if (other.agency_Name != null)
				return false;
		} else if (!agency_Name.equals(other.agency_Name))
			return false;
		if (phone_No == null) {
			if (other.phone_No != null)
				return false;
		} else if (!phone_No.equals(other.phone_No))
			return false;
		if (tier_1_Agency_Id != other.tier_1_Agency_Id)
			return false;
		return true;
	}

	/*public Tier_1_Agency getTier_1_Agency_Id() {
		return tier_1_Agency_Id;
	}

	public void setTier_1_Agency_Id(Collection<Tier_1_Agency> tier_1_Agency_Id) {
		this.tier_1_Agency_Id = (Tier_1_Agency) tier_1_Agency_Id;
	}*/

	
	
}
