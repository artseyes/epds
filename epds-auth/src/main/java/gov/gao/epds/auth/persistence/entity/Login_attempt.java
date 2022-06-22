package gov.gao.epds.auth.persistence.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table
public class Login_attempt {
	@Id
	/*@GeneratedValue(strategy = GenerationType.TABLE)*/
	@GenericGenerator(
	        name = "loginAttemptSeqGen", 
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
	        generator = "loginAttemptSeqGen"
	    )
	@Column
	private Long login_attempt_id;
	@Column
	private String user_email;
	@Column
	private Timestamp time_stamp;
	@Column
	private String client_ip;
	@Column
	private String password;
	@Column
	private String browser_type;
	@Column
	private char success;

	public String getUser_email() {
		return user_email;
	}

	public Long getLogin_attempt_id() {
		return login_attempt_id;
	}

	public void setLogin_attempt_id(Long login_attempt_id) {
		this.login_attempt_id = login_attempt_id;
	}

	public void setUser_email(String user_email) {
		this.user_email = user_email;
	}

	public Timestamp getTime_stamp() {
		return time_stamp;
	}

	public void setTime_stamp(Timestamp time_stamp) {
		this.time_stamp = time_stamp;
	}

	public String getClient_ip() {
		return client_ip;
	}

	public void setClient_ip(String client_ip) {
		this.client_ip = client_ip;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getBrowser_type() {
		return browser_type;
	}

	public void setBrowser_type(String browser_type) {
		this.browser_type = browser_type;
	}

	public char getSuccess() {
		return success;
	}

	public void setSuccess(char success) {
		this.success = success;
	}

}