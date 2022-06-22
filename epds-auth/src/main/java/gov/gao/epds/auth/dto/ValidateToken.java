package gov.gao.epds.auth.dto;

import java.io.Serializable;

import javax.validation.constraints.Pattern;

public class ValidateToken extends DTOValidator implements Serializable {

	private static final long serialVersionUID = 4925325813385736793L;
	
	private String token;
	
	@Pattern(regexp = IPADDRESS_PATTERN, message="Invalid input format!")
	private String remoteIp;
	
	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}
	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}
	/**
	 * @return the remoteIp
	 */
	public String getRemoteIp() {
		return remoteIp;
	}
	/**
	 * @param remoteIp the remoteIp to set
	 */
	public void setRemoteIp(String remoteIp) {
		this.remoteIp = remoteIp;
	}

}
