package gov.gao.epds.gctrack;

public class EPDS_event {
	private String event_Id;
	private Integer event_type_id;
	private String event_type;
	private String event_description;
	private String info;
	private String event_date;

	private RepInfo repInfo;
	private OrgInfo orgInfo;

	public Integer getEvent_type_id() {
		return event_type_id;
	}

	public void setEvent_type_id(Integer event_type_id) {
		this.event_type_id = event_type_id;
	}

	public String getEvent_date() {
		return event_date;
	}

	public void setEvent_date(String event_date) {
		this.event_date = event_date;
	}

	public String getEvent_description() {
		return event_description;
	}

	public void setEvent_description(String event_description) {
		this.event_description = event_description;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String newValue) {
		this.info = newValue;
	}

	public String getEvent_type() {
		return event_type;
	}

	public void setEvent_type(String event_type) {
		this.event_type = event_type;
	}

	public String getEvent_Id() {
		return event_Id;
	}

	public void setEvent_Id(String event_Id) {
		this.event_Id = event_Id;
	}

	public RepInfo getRepInfo() {
		return repInfo;
	}

	public void setRepInfo(RepInfo repInfo) {
		this.repInfo = repInfo;
	}

	public OrgInfo getOrgInfo() {
		return orgInfo;
	}

	public void setOrgInfo(OrgInfo orgInfo) {
		this.orgInfo = orgInfo;
	}

}
