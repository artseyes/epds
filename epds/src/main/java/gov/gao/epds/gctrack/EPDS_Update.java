package gov.gao.epds.gctrack;

import gov.gao.epds.persistence.entity.Protest_Info;
import gov.gao.epds.persistence.entity.User_Info;

public class EPDS_Update {
	private String a_No;
	private String b_No;
	private Integer event_Id;
	private String event;
	private String date_Of_Event;
	private Protest_Info protest_Info;
	private User_Info representative_Info;
	private Protest_Info supplemental_Protest_Info;

	public String getDate_Of_Event() {
		return date_Of_Event;
	}

	public void setDate_Of_Event(String date_Of_Event) {
		this.date_Of_Event = date_Of_Event;
	}

	public Integer getEvent_Id() {
		return event_Id;
	}

	public void setEvent_Id(Integer event_Id) {
		this.event_Id = event_Id;
	}

	public String getA_No() {
		return a_No;
	}

	public void setA_No(String a_No) {
		this.a_No = a_No;
	}

	public String getB_No() {
		return b_No;
	}

	public void setB_No(String b_No) {
		this.b_No = b_No;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public Protest_Info getProtest_Info() {
		return protest_Info;
	}

	public void setProtest_Info(Protest_Info protest_Info) {
		this.protest_Info = protest_Info;
	}

	public User_Info getRepresentative_Info() {
		return representative_Info;
	}

	public void setRepresentative_Info(User_Info representative_Info) {
		this.representative_Info = representative_Info;
	}

	public Protest_Info getSupplemental_Protest_Info() {
		return supplemental_Protest_Info;
	}

	public void setSupplemental_Protest_Info(
			Protest_Info supplemental_Protest_Info) {
		this.supplemental_Protest_Info = supplemental_Protest_Info;
	}

}
