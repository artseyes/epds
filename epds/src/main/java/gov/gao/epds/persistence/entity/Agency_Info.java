package gov.gao.epds.persistence.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table
@Audited
public class Agency_Info implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2965182527308149592L;

	

	@Id
	@Column
	private int agency_Info_Id;

	@Column
	private int agency_Id;

	@Column
	private String tier;

	@Column
	@JsonIgnore
	private String user_Id;
	
	
	@Column
	private String is_Equals_To;
	
	@Column
	private String is_Not_Equals_To;;

	public int getAgency_Info_Id() {

		return agency_Info_Id;
	}

	public void setAgency_Info_Id(int agency_Info_Id) {
		this.agency_Info_Id = agency_Info_Id;
	}

	public int getAgency_Id() {
		return agency_Id;
	}

	public void setAgency_Id(int agency_Id) {
		this.agency_Id = agency_Id;
	}

	public String getTier() {
		return tier;
	}

	public void setTier(String tier) {
		this.tier = tier;
	}

	public String getUser_Id() {
		return user_Id;
	}

	public void setUser_Id(String user_Id) {
		this.user_Id = user_Id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + agency_Id;
		result = prime * result + agency_Info_Id;
		result = prime * result + ((tier == null) ? 0 : tier.hashCode());
		result = prime * result + ((user_Id == null) ? 0 : user_Id.hashCode());
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
		if (!(obj instanceof Agency_Info))
			return false;
		Agency_Info other = (Agency_Info) obj;
		if (agency_Id != other.agency_Id)
			return false;
		if (agency_Info_Id != other.agency_Info_Id)
			return false;
		if (tier == null) {
			if (other.tier != null)
				return false;
		} else if (!tier.equals(other.tier))
			return false;
		if (user_Id == null) {
			if (other.user_Id != null)
				return false;
		} else if (!user_Id.equals(other.user_Id))
			return false;
		return true;
	}

	/**
	 * @return the is_Equals_To
	 */
	public String getIs_Equals_To() {
		return is_Equals_To;
	}

	/**
	 * @param is_Equals_To the is_Equals_To to set
	 */
	public void setIs_Equals_To(String is_Equals_To) {
		this.is_Equals_To = is_Equals_To;
	}

	/**
	 * returns 
	 * @return the is_Not_Equals_To
	 */
	public String getIs_Not_Equals_To() {
		return is_Not_Equals_To;
	}

	/**
	 * @param is_Not_Equals_To the is_Not_Equals_To to set
	 */
	public void setIs_Not_Equals_To(String is_Not_Equals_To) {
		this.is_Not_Equals_To = is_Not_Equals_To;
	}
	
	
	
	

}
