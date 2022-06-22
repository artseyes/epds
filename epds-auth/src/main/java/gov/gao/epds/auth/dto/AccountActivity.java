package gov.gao.epds.auth.dto;

import java.io.Serializable;

import org.joda.time.DateTime;

public class AccountActivity extends DTOValidator implements Serializable{

	
	private static final long serialVersionUID = -8028310828832506920L;
	
	private String email;
	private String activityType;
	private DateTime timeStamp;
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
	 * @return the activityType
	 */
	public String getActivityType() {
		return activityType;
	}
	/**
	 * @param activityType the activityType to set
	 */
	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}
	/**
	 * @return the timeStamp
	 */
	public DateTime getTimeStamp() {
		return timeStamp;
	}
	/**
	 * @param timeStamp the timeStamp to set
	 */
	public void setTimeStamp(DateTime timeStamp) {
		this.timeStamp = timeStamp;
	}
}
