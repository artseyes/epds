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
public class User_event_log {
	@Id
	/*@GeneratedValue(strategy = GenerationType.TABLE)*/
	
	@GenericGenerator(
	        name = "userEventLogSeqGen", 
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
        generator = "userEventLogSeqGen"
    )
	@Column
	private Long user_event_log_id;
	@Column
	private Integer user_id;
	@Column
	private Integer user_event_id;
	@Column
	private Timestamp time_stamp;

	public Timestamp getTime_stamp() {
		return time_stamp;
	}

	public void setTime_stamp(Timestamp time_stamp) {
		this.time_stamp = time_stamp;
	}

	public Long getUser_event_log_id() {
		return user_event_log_id;
	}

	public void setUser_event_log_id(Long user_event_log_id) {
		this.user_event_log_id = user_event_log_id;
	}

	public Integer getUser_id() {
		return user_id;
	}

	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}

	public Integer getUser_event_id() {
		return user_event_id;
	}

	public void setUser_event_id(Integer user_event_id) {
		this.user_event_id = user_event_id;
	}

}
