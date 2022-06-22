package gov.gao.epds.dto;

import java.io.Serializable;

import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class LoginDTO extends DTOValidator implements Serializable {
	
	
	private static final long serialVersionUID = 9024508165900513823L;
	
	
	private String email;
	
	
	private String password;
	@JsonIgnore
	@Pattern(regexp = IPADDRESS_PATTERN, message="Invalid input format!")
	private String client_ip;
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the client_ip
	 */
	public String getClient_ip() {
		return client_ip;
	}
	/**
	 * @param client_ip the client_ip to set
	 */
	public void setClient_ip(String client_ip) {
		this.client_ip = client_ip;
	}
}
