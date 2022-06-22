package gov.gao.epds.gctrack;

import java.util.List;

public class EpdsUpdate {
	List<NewProtest> list_of_new_protest;
	List<NewEpdsEventsPerCase> list_of_newEpdsEventsPerCase;

	public List<NewProtest> getList_of_new_protest() {
		return list_of_new_protest;
	}

	public void setList_of_new_protest(List<NewProtest> list_of_new_protest) {
		this.list_of_new_protest = list_of_new_protest;
	}

	public List<NewEpdsEventsPerCase> getList_of_newEpdsEventsPerCase() {
		return list_of_newEpdsEventsPerCase;
	}

	public void setList_of_newEpdsEventsPerCase(
			List<NewEpdsEventsPerCase> list_of_newEpdsEventsPerCase) {
		this.list_of_newEpdsEventsPerCase = list_of_newEpdsEventsPerCase;
	}

}
