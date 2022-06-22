package gov.gao.epds.gctrack;

import java.util.List;

/*
 * Since this has almost the same attributes as new Protest we can just inherit the attrs from  the NewProtest
*/
public class NewEpdsEventsPerCase extends NewProtest {

	// NewProtest doesn't return this, but here we need to
	private String primaryAgency;
	private List<EPDS_event> list_of_EPDS_event;

	public List<EPDS_event> getList_of_EPDS_event() {
		return list_of_EPDS_event;
	}

	public void setList_of_EPDS_event(List<EPDS_event> list_of_EPDS_event) {
		this.list_of_EPDS_event = list_of_EPDS_event;
	}

	public String getPrimaryAgency() {
		return primaryAgency;
	}

	public void setPrimaryAgency(String agency) {
		this.primaryAgency = agency;
	}

}
