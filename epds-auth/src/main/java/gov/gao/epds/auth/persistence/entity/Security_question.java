package gov.gao.epds.auth.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class Security_question {
	@Id
	@Column
	private Integer security_q_id;
	@Column
	private String security_question;

	public Integer getSecurity_q_id() {
		return security_q_id;
	}

	public void setSecurity_q_id(Integer security_q_id) {
		this.security_q_id = security_q_id;
	}

	public String getSecurity_question() {
		return security_question;
	}

	public void setSecurity_question(String security_question) {
		this.security_question = security_question;
	}

}
