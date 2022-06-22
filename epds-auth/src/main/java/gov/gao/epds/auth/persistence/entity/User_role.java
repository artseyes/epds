package gov.gao.epds.auth.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class User_role {
	@Id
	@Column
	private Integer role_id;
	@Column
	private String role;
	@Column
	private Integer role_group;

	public Integer getRole_id() {
		return role_id;
	}

	public void setRole_id(Integer role_id) {
		this.role_id = role_id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Integer getRole_group() {
		return role_group;
	}

	public void setRole_group(Integer role_group) {
		this.role_group = role_group;
	}

}
