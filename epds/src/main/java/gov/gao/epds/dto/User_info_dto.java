package gov.gao.epds.dto;

import java.io.Serializable;
import java.util.Map;

import javax.validation.constraints.Pattern;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.validator.constraints.Range;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User_info_dto extends DTOValidator implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 154931819983818213L;
	
	
	@Pattern(regexp = PREFIX_SUFFIX_PATTERN, message="Invalid input format!")
	private String prefix;
	
	@Pattern(regexp = FIRSTNAME_LASTNAME_PATTERN, message="Invalid input format!")
	private String lastName;
	
	@Pattern(regexp = FIRSTNAME_LASTNAME_PATTERN, message="Invalid input format!")
	private String firstName;
	
	//@Pattern(regexp = MIDDLE_INITIAL_PATTERN, message="Invalid input format!")
	private String middle_initial;
	
	@Pattern(regexp = PREFIX_SUFFIX_PATTERN, message="Invalid input format!")
	private String suffix;
	
	@Pattern(regexp = EMAIL_PATTERN, message="Invalid input format!")
	private String email;
	
	@Pattern(regexp = EMAIL_PATTERN, message="Invalid input format!")
	private String old_email;
	
	
	@Pattern(regexp = CONTACT_NUM_PATTERN, message="Invalid input format!")
	private String phoneNo;
	
	@Pattern(regexp = CONTACT_NUM_PATTERN, message="Invalid input format!")
	private String faxNo;
	
	@Pattern(regexp = ADDRESS_PATTERN, message="Invalid input format!")
	private String address1;
	@Pattern(regexp = ADDRESS_PATTERN, message="Invalid input format!")
	private String address2;
	@Pattern(regexp = CITY_PATTERN, message="Invalid input format!")
	private String city;
	@Pattern(regexp = STATE_PATTERN, message="Invalid input format!")
	private String state;
	@Pattern(regexp = COUNTRY_PATTERN, message="Invalid input format!")
	private String country;
	
	@Pattern(regexp = ZIPCODE_PATTERN, message="Invalid input format!")
	private String zipCode;
	
	/*@Pattern(regexp = USER_ID_PATTERN, message="Invalid input format!")*/
	private Integer user_id;
	
	
	private String password;
	private String gender;
	private String desc;
	
	@Pattern(regexp = NAME_OF_FIRM_PATTERN, message="Invalid input format!")
	private String nameOfFirm;
	
	@Range(min=0, max=7)
	private Integer groupNo;
	
	@Pattern(regexp = ROLE_PATTERN, message="Invalid input format!")
	private String role;
	
	@Range(min=0, max=300)
	private Integer firm_id;
	
	@Range(min=1, max=3)
	private Integer auth_role_id;
	
	@Pattern(regexp = AGENCYID_PATTERN, message="Invalid input format!")
	private String tier1_agency_id;
	
	@Pattern(regexp = AGENCYID_PATTERN, message="Invalid input format!")
	private String tier2_agency_id;
	
	private String oldPassword;
	private String newPassword;
	
	private Map<Integer, String> secQIdToAnswerMap;
	
	
	private String auth_token;
	
	
	@Pattern(regexp = IPADDRESS_PATTERN, message="Invalid input format!")
	private String client_ip;
	
	@Pattern(regexp = SECQID_PATTERN, message="Invalid input format!")
	private String seqQue1Id;
	
	@Pattern(regexp = SECQID_PATTERN, message="Invalid input format!")
	private String seqQue2Id;
	
	@Pattern(regexp = SECQID_PATTERN, message="Invalid input format!")
	private String seqQue3Id;
	
	@Pattern(regexp = ALPHA_PATTERN, message="Invalid input format!")
	private String answer1;
	
	@Pattern(regexp = ALPHA_PATTERN, message="Invalid input format!")
	private String answer2;
	
	@Pattern(regexp = ALPHA_PATTERN, message="Invalid input format!")
	private String answer3;
	
	private Map<Integer, String> secQIdToQuestionMap;
	
	
	@Range(min=1, max=9)
	private Integer account_status_id;
	
	
	private boolean isPasswordExpiring;
	private boolean isPasswordExpired;
	
	private Boolean isROBRequired;
	
	
	private String title;
	
	
	private Integer gaoId;
	
	
	@Range(min=1, max=9)
	private Integer epds_role_id;
	
	@Pattern(regexp = ALPHA_PATTERN, message="Invalid input format!")
	private String typeOfUpdate;
	
	private Integer numOfDaysLeftToExpirePwd;

	public Integer getNumOfDaysLeftToExpirePwd() {
		return numOfDaysLeftToExpirePwd;
	}

	public void setNumOfDaysLeftToExpirePwd(Integer numOfDaysLeftToExpirePwd) {
		this.numOfDaysLeftToExpirePwd = numOfDaysLeftToExpirePwd;
	}

	public Integer getEpds_role_id() {
		return epds_role_id;
	}

	public void setEpds_role_id(Integer epds_role_id) {
		this.epds_role_id = epds_role_id;
	}

	public String getTitle() {
		return title;
	}

	public boolean isPasswordExpired() {
		return isPasswordExpired;
	}

	public void setPasswordExpired(boolean isPasswordExpired) {
		this.isPasswordExpired = isPasswordExpired;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getGaoId() {
		return gaoId;
	}

	public void setGaoId(Integer gaoId) {
		this.gaoId = gaoId;
	}

	public boolean isPasswordExpiring() {
		return isPasswordExpiring;
	}

	public void setPasswordExpiring(boolean isPasswordExpiring) {
		this.isPasswordExpiring = isPasswordExpiring;
	}

	public Integer getAccount_status_id() {
		return account_status_id;
	}

	public void setAccount_status_id(Integer account_status_id) {
		this.account_status_id = account_status_id;
	}

	public Map<Integer, String> getSecQIdToQuestionMap() {
		return secQIdToQuestionMap;
	}

	public void setSecQIdToQuestionMap(Map<Integer, String> secQIdToQuestionMap) {
		this.secQIdToQuestionMap = secQIdToQuestionMap;
	}

	public String getSeqQue1Id() {
		return seqQue1Id;
	}

	public void setSeqQue1Id(String seqQue1Id) {
		this.seqQue1Id = seqQue1Id;
	}

	public String getSeqQue2Id() {
		return seqQue2Id;
	}

	public void setSeqQue2Id(String seqQue2Id) {
		this.seqQue2Id = seqQue2Id;
	}

	public String getSeqQue3Id() {
		return seqQue3Id;
	}

	public void setSeqQue3Id(String seqQue3Id) {
		this.seqQue3Id = seqQue3Id;
	}

	public String getAnswer1() {
		return answer1;
	}

	public void setAnswer1(String answer1) {
		this.answer1 = answer1;
	}

	public String getAnswer2() {
		return answer2;
	}

	public void setAnswer2(String answer2) {
		this.answer2 = answer2;
	}

	public String getAnswer3() {
		return answer3;
	}

	public void setAnswer3(String answer3) {
		this.answer3 = answer3;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddle_initial() {
		return middle_initial;
	}

	public void setMiddle_initial(String middle_initial) {
		this.middle_initial = middle_initial;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getFaxNo() {
		return faxNo;
	}

	public void setFaxNo(String faxNo) {
		this.faxNo = faxNo;
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

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public Integer getUser_id() {
		return user_id;
	}

	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getNameOfFirm() {
		return nameOfFirm;
	}

	public void setNameOfFirm(String nameOfFirm) {
		this.nameOfFirm = nameOfFirm;
	}

	public Integer getGroupNo() {
		return groupNo;
	}

	public void setGroupNo(Integer groupNo) {
		this.groupNo = groupNo;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Integer getFirm_id() {
		return firm_id;
	}

	public void setFirm_id(Integer firm_id) {
		this.firm_id = firm_id;
	}

	public Integer getAuth_role_id() {
		return auth_role_id;
	}

	public void setAuth_role_id(Integer auth_role_id) {
		this.auth_role_id = auth_role_id;
	}

	public String getTier1_agency_id() {
		return tier1_agency_id;
	}

	public void setTier1_agency_id(String tier1_agency_id) {
		this.tier1_agency_id = tier1_agency_id;
	}

	public String getTier2_agency_id() {
		return tier2_agency_id;
	}

	public void setTier2_agency_id(String tier2_agency_id) {
		this.tier2_agency_id = tier2_agency_id;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public Map<Integer, String> getSecQIdToAnswerMap() {
		return secQIdToAnswerMap;
	}

	public void setSecQIdToAnswerMap(Map<Integer, String> secQIdToAnswerMap) {
		this.secQIdToAnswerMap = secQIdToAnswerMap;
	}

	public String getAuth_token() {
		return auth_token;
	}

	public void setAuth_token(String auth_token) {
		this.auth_token = auth_token;
	}

	public String getClient_ip() {
		return client_ip;
	}

	public void setClient_ip(String client_ip) {
		this.client_ip = client_ip;
	}

	/**
	 * @return the typeOfUpdate
	 */
	public String getTypeOfUpdate() {
		return typeOfUpdate;
	}

	/**
	 * @param typeOfUpdate the typeOfUpdate to set
	 */
	public void setTypeOfUpdate(String typeOfUpdate) {
		this.typeOfUpdate = typeOfUpdate;
	}

	public Boolean getIsROBRequired() {
		return isROBRequired;
	}

	public void setIsROBRequired(Boolean isROBRequired) {
		this.isROBRequired = isROBRequired;
	}

	public String getOld_email() {
		return old_email;
	}

	public void setOld_email(String old_email) {
		this.old_email = old_email;
	}

}
