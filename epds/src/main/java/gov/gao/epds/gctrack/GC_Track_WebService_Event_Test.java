package gov.gao.epds.gctrack;

import gov.gao.epds.persistence.entity.GC_Track_Service_Event;
import gov.gao.epds.persistence.entity.Protest_Info;
import gov.gao.epds.service.GC_Service;
import gov.gao.epds.utils.SpringApplicationContext;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/gc-test")
public class GC_Track_WebService_Event_Test {

	@GET
	@Path("/webservice-event-send/")
	@Produces("application/json")
	public List<GC_Track_Service_Event> testSending_WebService_Event() throws Exception {
		GC_Service gc_Service = (GC_Service) SpringApplicationContext
				.getBean("GC_Service");

		Protest_Info protest_Info = new Protest_Info();
		protest_Info.setCase_Type("New Protest");
		protest_Info.setA_No("A-E1VRO");

		gc_Service.set_Event_For_New_Protest_Info(protest_Info);

		return gc_Service.get_List_Of_GC_Track_Service_Events();
	}
}
