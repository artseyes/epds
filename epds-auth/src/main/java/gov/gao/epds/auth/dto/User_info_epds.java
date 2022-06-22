package gov.gao.epds.auth.dto;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User_info_epds extends DTOValidator{
	private String user_Id;
	private String first_Name;
	private String last_Name;
	private String password;
	private String firm_Name;
	private String phone_No;
	private String fax_No;
	private String street;
	private String city;
	private String state;
	private String country;
	private String zip_Code;
	private String email;

	private String role;
	private String intervenor_Company_Name;
	private String intervenor_Company_Address;
	private String intervenorCompanyDetail;

	private int group_No;
	private int gao_user_id;
	private String po;

	private Integer role_id;
	private Integer firm_id;
	private String address1;
	private String address2;
	private String middle_initial;
	private String prefix;
	private String suffix;

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
}
