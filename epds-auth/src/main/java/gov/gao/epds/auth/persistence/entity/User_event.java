package gov.gao.epds.auth.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class User_event {
	@Id
	@Column
	private Integer user_event_id;
	@Column
	private String user_event_desc;

	public Integer getUser_event_id() {
		return user_event_id;
	}

	public void setUser_event_id(Integer user_event_id) {
		this.user_event_id = user_event_id;
	}

	public String getUser_event_desc() {
		return user_event_desc;
	}

	public void setUser_event_desc(String user_event_desc) {
		this.user_event_desc = user_event_desc;
	}

}
