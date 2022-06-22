package gov.gao.epds.gctrack;

/**
 * @author MHussaini
 *
 */
public abstract class PartyInfo {

	private Integer id;
	
	private String phone_No;
	private String fax_No;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String country;
	private String zip_Code;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getZip_Code() {
		return zip_Code;
	}

	public void setZip_Code(String zip_Code) {
		this.zip_Code = zip_Code;
	}
	
	
	
	
}
