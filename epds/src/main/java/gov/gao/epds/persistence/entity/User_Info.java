package gov.gao.epds.persistence.entity;

import gov.gao.epds.dto.CompanyInfo;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table
@Audited
public class User_Info implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5833438329404965962L;
	@Id
	@Column
	private String user_Id;
	@Column
	private String first_Name;
	@Column
	private String last_Name;
	
	@Column
	@JsonIgnore
	private String password;
	@Column
	private String firm_Name;
	@Column
	private String phone_No;
	@Column
	private String fax_No;
	@Column
	private String street;
	@Column
	private String city;
	@Column
	private String state;
	@Column
	private String country;
	@Column
	private String zip_Code;
	@Column
	private String email;

	@Transient
	private String role;
	@Transient
	private String intervenor_Company_Name;
	@Transient
	private String intervenor_Company_Address;
	@Transient
	private String intervenorCompanyDetail;
	
	@Transient
	private int group_No;
	@Transient
	private int gao_user_id;
	@Transient
	private String po;
	
	@Transient
	private CompanyInfo intervenorCompanyInfo;

	@Column
	private Integer role_id;
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
	
	@Transient
	private String title;
	
	@Transient
	private boolean isEditable;
	
	@Transient
	private String invitationStatus;
	
	@JsonIgnore
	@Column(name="CDS_PREFERENCES", 
	columnDefinition="CLOB")
	@Lob
	@Basic(fetch=FetchType.LAZY)
	private String cds_preferences;
	
	@Column(name = "Global_Email_Pref")
	private String globalEmailPref;
	
	@Column(name = "ANumNotifications")
	private String aNumNotifications;

	public String getIntervenorCompanyDetail() {
		return intervenorCompanyDetail;
	}

	public void setIntervenorCompanyDetail(String intervenorCompanyDetail) {
		this.intervenorCompanyDetail = intervenorCompanyDetail;
	}

	public String getPo() {
		return po;
	}

	public void setPo(String po) {
		this.po = po;
	}

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

	public Integer getRole_id() {
		return role_id;
	}

	public void setRole_id(Integer role_id) {
		this.role_id = role_id;
	}

	public Integer getFirm_id() {
		return firm_id;
	}

	public void setFirm_id(Integer firm_id) {
		this.firm_id = firm_id;
	}

	public int getGao_user_id() {
		return gao_user_id;
	}

	public void setGao_user_id(int gao_user_id) {
		this.gao_user_id = gao_user_id;
	}

	public int getGroup_No() {
		return group_No;
	}

	public void setGroup_No(int group_No) {
		this.group_No = group_No;
	}

	public String getIntervenor_Company_Name() {
		return intervenor_Company_Name;
	}

	public void setIntervenor_Company_Name(String intervenor_Company_Name) {
		this.intervenor_Company_Name = intervenor_Company_Name;
	}

	public String getIntervenor_Company_Address() {
		return intervenor_Company_Address;
	}

	public void setIntervenor_Company_Address(String intervenor_Company_Address) {
		this.intervenor_Company_Address = intervenor_Company_Address;
	}

	public String getUser_Id() {
		return user_Id;
	}

	public void setUser_Id(String user_Id) {
		this.user_Id = user_Id;
	}

	public String getFirst_Name() {
		return first_Name;
	}

	public void setFirst_Name(String first_Name) {
		this.first_Name = first_Name;
	}

	public String getLast_Name() {
		return last_Name;
	}

	public void setLast_Name(String last_Name) {
		this.last_Name = last_Name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirm_Name() {
		return firm_Name;
	}

	public void setFirm_Name(String firm_Name) {
		this.firm_Name = firm_Name;
	}

	public String getPhone_No() {
		return phone_No;
	}

	public void setPhone_No(String phone_No) {
		this.phone_No = phone_No;
	}

	public String getFax_No() {
		return fax_No;
	}

	public void setFax_No(String fax_No) {
		this.fax_No = fax_No;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getZip_Code() {
		return zip_Code;
	}

	public void setZip_Code(String zip_Code) {
		this.zip_Code = zip_Code;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * @return the intervenorCompanyInfo
	 */
	public CompanyInfo getIntervenorCompanyInfo() {
		return intervenorCompanyInfo;
	}

	/**
	 * @param intervenorCompanyInfo the intervenorCompanyInfo to set
	 */
	public void setIntervenorCompanyInfo(CompanyInfo intervenorCompanyInfo) {
		this.intervenorCompanyInfo = intervenorCompanyInfo;
	}

	

	public String getCds_preferences() {
		return cds_preferences;
	}

	public void setCds_preferences(String cds_preferences) {
		this.cds_preferences = cds_preferences;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address1 == null) ? 0 : address1.hashCode());
		result = prime * result + ((address2 == null) ? 0 : address2.hashCode());
		result = prime * result + ((cds_preferences == null) ? 0 : cds_preferences.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((fax_No == null) ? 0 : fax_No.hashCode());
		result = prime * result + ((firm_Name == null) ? 0 : firm_Name.hashCode());
		result = prime * result + ((firm_id == null) ? 0 : firm_id.hashCode());
		result = prime * result + ((first_Name == null) ? 0 : first_Name.hashCode());
		result = prime * result + gao_user_id;
		result = prime * result + group_No;
		result = prime * result + ((intervenorCompanyDetail == null) ? 0 : intervenorCompanyDetail.hashCode());
		result = prime * result + ((intervenorCompanyInfo == null) ? 0 : intervenorCompanyInfo.hashCode());
		result = prime * result + ((intervenor_Company_Address == null) ? 0 : intervenor_Company_Address.hashCode());
		result = prime * result + ((intervenor_Company_Name == null) ? 0 : intervenor_Company_Name.hashCode());
		result = prime * result + ((last_Name == null) ? 0 : last_Name.hashCode());
		result = prime * result + ((middle_initial == null) ? 0 : middle_initial.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((phone_No == null) ? 0 : phone_No.hashCode());
		result = prime * result + ((po == null) ? 0 : po.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		result = prime * result + ((role_id == null) ? 0 : role_id.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((street == null) ? 0 : street.hashCode());
		result = prime * result + ((suffix == null) ? 0 : suffix.hashCode());
		result = prime * result + ((user_Id == null) ? 0 : user_Id.hashCode());
		result = prime * result + ((zip_Code == null) ? 0 : zip_Code.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User_Info other = (User_Info) obj;
		if (address1 == null) {
			if (other.address1 != null)
				return false;
		} else if (!address1.equals(other.address1))
			return false;
		if (address2 == null) {
			if (other.address2 != null)
				return false;
		} else if (!address2.equals(other.address2))
			return false;
		if (cds_preferences == null) {
			if (other.cds_preferences != null)
				return false;
		} else if (!cds_preferences.equals(other.cds_preferences))
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (fax_No == null) {
			if (other.fax_No != null)
				return false;
		} else if (!fax_No.equals(other.fax_No))
			return false;
		if (firm_Name == null) {
			if (other.firm_Name != null)
				return false;
		} else if (!firm_Name.equals(other.firm_Name))
			return false;
		if (firm_id == null) {
			if (other.firm_id != null)
				return false;
		} else if (!firm_id.equals(other.firm_id))
			return false;
		if (first_Name == null) {
			if (other.first_Name != null)
				return false;
		} else if (!first_Name.equals(other.first_Name))
			return false;
		if (gao_user_id != other.gao_user_id)
			return false;
		if (group_No != other.group_No)
			return false;
		if (intervenorCompanyDetail == null) {
			if (other.intervenorCompanyDetail != null)
				return false;
		} else if (!intervenorCompanyDetail.equals(other.intervenorCompanyDetail))
			return false;
		if (intervenorCompanyInfo == null) {
			if (other.intervenorCompanyInfo != null)
				return false;
		} else if (!intervenorCompanyInfo.equals(other.intervenorCompanyInfo))
			return false;
		if (intervenor_Company_Address == null) {
			if (other.intervenor_Company_Address != null)
				return false;
		} else if (!intervenor_Company_Address.equals(other.intervenor_Company_Address))
			return false;
		if (intervenor_Company_Name == null) {
			if (other.intervenor_Company_Name != null)
				return false;
		} else if (!intervenor_Company_Name.equals(other.intervenor_Company_Name))
			return false;
		if (last_Name == null) {
			if (other.last_Name != null)
				return false;
		} else if (!last_Name.equals(other.last_Name))
			return false;
		if (middle_initial == null) {
			if (other.middle_initial != null)
				return false;
		} else if (!middle_initial.equals(other.middle_initial))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (phone_No == null) {
			if (other.phone_No != null)
				return false;
		} else if (!phone_No.equals(other.phone_No))
			return false;
		if (po == null) {
			if (other.po != null)
				return false;
		} else if (!po.equals(other.po))
			return false;
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		if (role_id == null) {
			if (other.role_id != null)
				return false;
		} else if (!role_id.equals(other.role_id))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (street == null) {
			if (other.street != null)
				return false;
		} else if (!street.equals(other.street))
			return false;
		if (suffix == null) {
			if (other.suffix != null)
				return false;
		} else if (!suffix.equals(other.suffix))
			return false;
		if (user_Id == null) {
			if (other.user_Id != null)
				return false;
		} else if (!user_Id.equals(other.user_Id))
			return false;
		if (zip_Code == null) {
			if (other.zip_Code != null)
				return false;
		} else if (!zip_Code.equals(other.zip_Code))
			return false;
		return true;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isEditable() {
		return isEditable;
	}

	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

	/**
	 * @return the globalEmailPref
	 */
	public String getGlobalEmailPref() {
		return globalEmailPref;
	}

	/**
	 * @param globalEmailPref the globalEmailPref to set
	 */
	public void setGlobalEmailPref(String globalEmailPref) {
		this.globalEmailPref = globalEmailPref;
	}

	/**
	 * @return the aNumNotifications
	 */
	public String getaNumNotifications() {
		return aNumNotifications;
	}

	/**
	 * @param aNumNotifications the aNumNotifications to set
	 */
	public void setaNumNotifications(String aNumNotifications) {
		this.aNumNotifications = aNumNotifications;
	}

	public String getInvitationStatus() {
		return invitationStatus;
	}

	public void setInvitationStatus(String invitationStatus) {
		this.invitationStatus = invitationStatus;
	}
	
	
	
	
	

}
