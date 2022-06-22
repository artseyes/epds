package gov.gao.epds.persistence.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;


@Entity
@Table
@Audited
public class Protest_Dm_Info implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3642314601658912299L;
	
	@Id
	@Column
	@GenericGenerator(
	        name = "protestDMInfoSeqGen", 
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
	        generator = "protestDMInfoSeqGen"
	    )
	/*@Id
	@Column
	@GeneratedValue(strategy = GenerationType.SEQUENCE)*/
	private Integer id;
	@Column
	private String a_No;
	@Column
	private Integer gc_Track_Dm_No;
	@Column
	private String verified_By;
	@Column
	private Date date_verified;
	
	@Column
	private String completed_By;
	
	@Column
	private String dm_no_entered_By;
	
	@Column
	private String dir_Del;
	
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * @return the a_No
	 */
	public String getA_No() {
		return a_No;
	}
	/**
	 * @param a_No the a_No to set
	 */
	public void setA_No(String a_No) {
		this.a_No = a_No;
	}
	/**
	 * @return the verified_By
	 */
	public String getVerified_By() {
		return verified_By;
	}
	/**
	 * @param verified_By the verified_By to set
	 */
	public void setVerified_By(String verified_By) {
		this.verified_By = verified_By;
	}
	/**
	 * @return the date_verified
	 */
	public Date getDate_verified() {
		return date_verified;
	}
	/**
	 * @param date_verified the date_verified to set
	 */
	public void setDate_verified(Date date_verified) {
		this.date_verified = date_verified;
	}
	/**
	 * @return the gc_Track_Dm_No
	 */
	public Integer getGc_Track_Dm_No() {
		return gc_Track_Dm_No;
	}
	/**
	 * @param gc_Track_Dm_No the gc_Track_Dm_No to set
	 */
	public void setGc_Track_Dm_No(Integer gc_Track_Dm_No) {
		this.gc_Track_Dm_No = gc_Track_Dm_No;
	}
	/**
	 * @return the completed_By
	 */
	public String getCompleted_By() {
		return completed_By;
	}
	/**
	 * @param completed_By the completed_By to set
	 */
	public void setCompleted_By(String completed_By) {
		this.completed_By = completed_By;
	}
	/**
	 * @return the dm_no_entered_By
	 */
	public String getDm_no_entered_By() {
		return dm_no_entered_By;
	}
	/**
	 * @param dm_no_entered_By the dm_no_entered_By to set
	 */
	public void setDm_no_entered_By(String dm_no_entered_By) {
		this.dm_no_entered_By = dm_no_entered_By;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a_No == null) ? 0 : a_No.hashCode());
		result = prime * result + ((completed_By == null) ? 0 : completed_By.hashCode());
		result = prime * result + ((date_verified == null) ? 0 : date_verified.hashCode());
		result = prime * result + ((dm_no_entered_By == null) ? 0 : dm_no_entered_By.hashCode());
		result = prime * result + ((gc_Track_Dm_No == null) ? 0 : gc_Track_Dm_No.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((verified_By == null) ? 0 : verified_By.hashCode());
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
		if (!(obj instanceof Protest_Dm_Info))
			return false;
		Protest_Dm_Info other = (Protest_Dm_Info) obj;
		if (a_No == null) {
			if (other.a_No != null)
				return false;
		} else if (!a_No.equals(other.a_No))
			return false;
		if (completed_By == null) {
			if (other.completed_By != null)
				return false;
		} else if (!completed_By.equals(other.completed_By))
			return false;
		if (date_verified == null) {
			if (other.date_verified != null)
				return false;
		} else if (!date_verified.equals(other.date_verified))
			return false;
		if (dm_no_entered_By == null) {
			if (other.dm_no_entered_By != null)
				return false;
		} else if (!dm_no_entered_By.equals(other.dm_no_entered_By))
			return false;
		if (gc_Track_Dm_No == null) {
			if (other.gc_Track_Dm_No != null)
				return false;
		} else if (!gc_Track_Dm_No.equals(other.gc_Track_Dm_No))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (verified_By == null) {
			if (other.verified_By != null)
				return false;
		} else if (!verified_By.equals(other.verified_By))
			return false;
		return true;
	}
	public String getDir_Del() {
		return dir_Del;
	}
	public void setDir_Del(String dir_Del) {
		this.dir_Del = dir_Del;
	}

	
	
	
}
