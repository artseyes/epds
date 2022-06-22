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

@Entity
@Table
@Audited
public class Invited_User implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3012853816561686608L;
	
	@Id
	@Column
	@GenericGenerator(
	        name = "invitedUserSeqGen", 
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
	        generator = "invitedUserSeqGen"
	    )
	/*@Id
	@Column
	@GeneratedValue(strategy = GenerationType.SEQUENCE)*/
	private int id;
	@Column
	private String invitee_Id;
	@Column
	private String inviter_Id;
	@Column
	private String a_No;
	@Column
	private String status;
	@Column
	private String invitee_Email;
	@Column
	private String inviter_Type;
	
	@Column
	private String company_name;
	
	@Column
	private String company_Address;
	
	@Transient
	private String inviterOrInviteeName;
	@Transient
	private String b_No;
	@Transient
	private String protesterCompanyName;
	@Transient
	private String inviterFirmName;
	
	@Transient
	private String consolidateBNumbers;
	

	public String getInviter_Type() {
		return inviter_Type;
	}

	public void setInviter_Type(String inviter_Type) {
		this.inviter_Type = inviter_Type;
	}

	public String getInviterFirmName() {
		return inviterFirmName;
	}

	public void setInviterFirmName(String inviterFirmName) {
		this.inviterFirmName = inviterFirmName;
	}

	public String getInviterOrInviteeName() {
		return inviterOrInviteeName;
	}

	public void setInviterOrInviteeName(String inviterOrInviteeName) {
		this.inviterOrInviteeName = inviterOrInviteeName;
	}

	public String getProtesterCompanyName() {
		return protesterCompanyName;
	}

	public void setProtesterCompanyName(String protesterCompanyName) {
		this.protesterCompanyName = protesterCompanyName;
	}

	public String getB_No() {
		return b_No;
	}

	public void setB_No(String b_No) {
		this.b_No = b_No;
	}

	public String getInvitee_Email() {
		return invitee_Email;
	}

	public void setInvitee_Email(String invitee_Email) {
		this.invitee_Email = invitee_Email;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getInvitee_Id() {
		return invitee_Id;
	}

	public void setInvitee_Id(String invitee_Id) {
		this.invitee_Id = invitee_Id;
	}

	public String getInviter_Id() {
		return inviter_Id;
	}

	public void setInviter_Id(String inviter_Id) {
		this.inviter_Id = inviter_Id;
	}

	public String getA_No() {
		return a_No;
	}

	public void setA_No(String a_No) {
		this.a_No = a_No;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
	public String getCompany_name() {
		return company_name;
	}

	
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}

	/**
	 * @return the company_Address
	 */
	public String getCompany_Address() {
		return company_Address;
	}

	/**
	 * @param company_Address the company_Address to set
	 */
	public void setCompany_Address(String company_Address) {
		this.company_Address = company_Address;
	}

	/**
	 * @return the consolidateBNumbers
	 */
	public String getConsolidateBNumbers() {
		return consolidateBNumbers;
	}

	/**
	 * @param consolidateBNumbers the consolidateBNumbers to set
	 */
	public void setConsolidateBNumbers(String consolidateBNumbers) {
		this.consolidateBNumbers = consolidateBNumbers;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a_No == null) ? 0 : a_No.hashCode());
		result = prime * result + ((b_No == null) ? 0 : b_No.hashCode());
		result = prime * result + ((company_Address == null) ? 0 : company_Address.hashCode());
		result = prime * result + ((company_name == null) ? 0 : company_name.hashCode());
		result = prime * result + ((consolidateBNumbers == null) ? 0 : consolidateBNumbers.hashCode());
		result = prime * result + id;
		result = prime * result + ((invitee_Email == null) ? 0 : invitee_Email.hashCode());
		result = prime * result + ((invitee_Id == null) ? 0 : invitee_Id.hashCode());
		result = prime * result + ((inviterFirmName == null) ? 0 : inviterFirmName.hashCode());
		result = prime * result + ((inviterOrInviteeName == null) ? 0 : inviterOrInviteeName.hashCode());
		result = prime * result + ((inviter_Id == null) ? 0 : inviter_Id.hashCode());
		result = prime * result + ((inviter_Type == null) ? 0 : inviter_Type.hashCode());
		result = prime * result + ((protesterCompanyName == null) ? 0 : protesterCompanyName.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		if (!(obj instanceof Invited_User))
			return false;
		Invited_User other = (Invited_User) obj;
		if (a_No == null) {
			if (other.a_No != null)
				return false;
		} else if (!a_No.equals(other.a_No))
			return false;
		if (b_No == null) {
			if (other.b_No != null)
				return false;
		} else if (!b_No.equals(other.b_No))
			return false;
		if (company_Address == null) {
			if (other.company_Address != null)
				return false;
		} else if (!company_Address.equals(other.company_Address))
			return false;
		if (company_name == null) {
			if (other.company_name != null)
				return false;
		} else if (!company_name.equals(other.company_name))
			return false;
		if (consolidateBNumbers == null) {
			if (other.consolidateBNumbers != null)
				return false;
		} else if (!consolidateBNumbers.equals(other.consolidateBNumbers))
			return false;
		if (id != other.id)
			return false;
		if (invitee_Email == null) {
			if (other.invitee_Email != null)
				return false;
		} else if (!invitee_Email.equals(other.invitee_Email))
			return false;
		if (invitee_Id == null) {
			if (other.invitee_Id != null)
				return false;
		} else if (!invitee_Id.equals(other.invitee_Id))
			return false;
		if (inviterFirmName == null) {
			if (other.inviterFirmName != null)
				return false;
		} else if (!inviterFirmName.equals(other.inviterFirmName))
			return false;
		if (inviterOrInviteeName == null) {
			if (other.inviterOrInviteeName != null)
				return false;
		} else if (!inviterOrInviteeName.equals(other.inviterOrInviteeName))
			return false;
		if (inviter_Id == null) {
			if (other.inviter_Id != null)
				return false;
		} else if (!inviter_Id.equals(other.inviter_Id))
			return false;
		if (inviter_Type == null) {
			if (other.inviter_Type != null)
				return false;
		} else if (!inviter_Type.equals(other.inviter_Type))
			return false;
		if (protesterCompanyName == null) {
			if (other.protesterCompanyName != null)
				return false;
		} else if (!protesterCompanyName.equals(other.protesterCompanyName))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}
	
	
	

}
