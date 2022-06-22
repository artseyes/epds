package gov.gao.epds.persistence.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class User_Role implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1764706090759325423L;

	@Id
	@Column
	private int role_Id;

	@Column
	private String role_Desc;

	public int getRole_Id() {
		return role_Id;
	}

	public void setRole_Id(int role_Id) {
		this.role_Id = role_Id;
	}

	public String getRole_Desc() {
		return role_Desc;
	}

	public void setRole_Desc(String role_Desc) {
		this.role_Desc = role_Desc;
	}

}
