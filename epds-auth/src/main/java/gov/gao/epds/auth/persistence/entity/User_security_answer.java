package gov.gao.epds.auth.persistence.entity;

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
public class User_security_answer {
	
	@Id
	/*@GeneratedValue(strategy = GenerationType.TABLE)*/
	@GenericGenerator(
	        name = "userSecAnsSeqGen", 
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
        generator = "userSecAnsSeqGen"
    )
	@Column
	private Integer user_sec_a_id;
	@Column
	private Integer user_id;
	@Column
	private Integer security_q_id;
	@Column
	private String security_ans;
	@Transient
	private String securityQuestion;

	public String getSecurityQuestion() {
		return securityQuestion;
	}

	public void setSecurityQuestion(String securityQuestion) {
		this.securityQuestion = securityQuestion;
	}

	public Integer getUser_sec_a_id() {
		return user_sec_a_id;
	}

	public void setUser_sec_a_id(Integer user_sec_a_id) {
		this.user_sec_a_id = user_sec_a_id;
	}

	public Integer getUser_id() {
		return user_id;
	}

	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}

	public Integer getSecurity_q_id() {
		return security_q_id;
	}

	public void setSecurity_q_id(Integer security_q_id) {
		this.security_q_id = security_q_id;
	}

	public String getSecurity_ans() {
		return security_ans;
	}

	public void setSecurity_ans(String security_ans) {
		this.security_ans = security_ans;
	}

}
