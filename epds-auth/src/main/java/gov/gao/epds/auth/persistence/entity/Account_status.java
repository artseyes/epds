package gov.gao.epds.auth.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class Account_status {
	@Id
	@Column
	private Integer status_id;
	@Column
	String status_acronym;
	@Column
	String description;

	public String getStatus_acronym() {
		return status_acronym;
	}

	public Integer getStatus_id() {
		return status_id;
	}

	public void setStatus_id(Integer status_id) {
		this.status_id = status_id;
	}

	public void setStatus_acronym(String status_acronym) {
		this.status_acronym = status_acronym;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
