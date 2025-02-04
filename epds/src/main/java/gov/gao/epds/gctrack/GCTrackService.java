package gov.gao.epds.gctrack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import gov.gao.epds.service.GC_Service;
import gov.gao.epds.utils.SpringApplicationContext;
import gov.gao.epds.utils.Util;

/**
 * Every request from gc-track will is handled by this class.
 * 
 * @author RAdhikari
 * 
 */
@Path("/gc-track-services")
public class GCTrackService {
	
	private final static Logger logger = LoggerFactory
			.getLogger(GCTrackService.class);

	static{
		Authentication authentication =  new UsernamePasswordAuthenticationToken("GCTRACK", null, new ArrayList<GrantedAuthority>());
		if (logger.isDebugEnabled()){
			logger.debug("Logging in with {}", authentication.getPrincipal());	
		}
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
	

	/**
	 * @return a list of new cases created in EDS and a list of new
	 *         developments done on different EDS cases
	 */
	@RolesAllowed("GCTRACK")
	@GET
	@Path("/get-epds-update/")
	@Produces("application/json")
	public Object get_epdsUpdate() {
		GC_Track_Service_Call_Response response = new GC_Track_Service_Call_Response();

		try {

			GC_Service gc_Service = (GC_Service) SpringApplicationContext
					.getBean("GC_Service");
			EpdsUpdate epdsUpdate = gc_Service.get_epdsUpdate(response,null);

			response.setData(epdsUpdate);
			response.setIsSuccess(true);

			if (response.getMessage() == null
					|| response.getMessage().equalsIgnoreCase("")) {
				response.setMessage("Request was processed successfully");

				if (response.getRetry() == null){
					response.setRetry("N");
				}
			}
		}catch (Exception e) {
			response.setIsSuccess(false);
			response.setException(e.getMessage());
			// response.setData(Util.getStackTraceMessage(e));

			handleDBExceptions(response, e);

			if (response.getRetry() == null){
				response.setRetry("N");
			}

		}

		return response;
	}


	/**
	 * @param response
	 * @param e
	 */
	public void handleDBExceptions(GC_Track_Service_Call_Response response, Exception e) {

		if (response.getMessage() == null
				|| response.getMessage().equalsIgnoreCase("")) {
			response.setMessage("Internal error occurred in EDS server while processing the request");
		}

		 if(e.getCause() instanceof org.hibernate.exception.GenericJDBCException){
			 response.setException("Database is down. Please contact EDS support CBCA.IT@CBCA.GOV if problem persists. ");
			 response.setRetry("Y");
		 }

		 if(e.getCause() instanceof org.hibernate.exception.ConstraintViolationException){
			 response.setException(e.getCause().getMessage());
			 response.setMessage("Id already exists. Please try again with the another Id");
			 response.setRetry("N");
		 }

		 if(e.getCause() instanceof org.hibernate.exception.JDBCConnectionException){
			 response.setException("Database is down. Please contact EDS support CBCA.IT@CBCA.GOV if problem persists. ");
			 response.setRetry("Y");
		 }


		 if(e.getCause() instanceof org.hibernate.exception.SQLGrammarException){
			 response.setException("Database is down. Please contact EDS support CBCA.IT@CBCA.GOV if problem persists. ");
			 response.setRetry("N");
		 }
	}


	/**
	 * @return a list of new cases created in EDS and a list of new
	 *         developments done on different EDS cases
	 */
	@RolesAllowed("GCTRACK")
	@GET
	@Path("/get-epds-update/{aNumber}")
	@Produces("application/json")
	public Object get_epdsUpdateByAnum(@PathParam("aNumber") String a_no) {
		GC_Track_Service_Call_Response response = new GC_Track_Service_Call_Response();

		try {
			GC_Service gc_Service = (GC_Service) SpringApplicationContext
					.getBean("GC_Service");
			EpdsUpdate epdsUpdate = gc_Service.get_epdsUpdate(response,a_no);

			response.setData(epdsUpdate);
			response.setIsSuccess(true);

			if (response.getMessage() == null
					|| response.getMessage().equalsIgnoreCase("")) {
				response.setMessage("Request was processed successfully");
			}
		} catch (Exception e) {
			response.setIsSuccess(false);
			response.setException(e.getMessage());
			// response.setData(Util.getStackTraceMessage(e));

			if (response.getMessage() == null
					|| response.getMessage().equalsIgnoreCase("")) {
				response.setMessage("Internal error occurred in EDS server while processing the request");
			}

			handleDBExceptions(response, e);

			if (response.getRetry() == null){
				response.setRetry("N");
			}
		}

		return response;
	}

	/**
	 * @param a_no
	 *            unique id for an EDS case
	 * @return EDS data for a case used by gc-track while filing a new case
	 * @throws Exception
	 */
	@RolesAllowed("GCTRACK")
	@GET
	@Path("/get-case/{aNumber}")
	@Produces("application/json")
	@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
	public Object get_gc_protest_info(@PathParam("aNumber") String a_no)
			throws Exception {
		GC_Track_Service_Call_Response response = new GC_Track_Service_Call_Response();

		try {
			GC_Service gc_Service = (GC_Service) SpringApplicationContext
					.getBean("GC_Service");
			Gc_protest_info gc_protest_info = gc_Service.getGc_protest_info(
					a_no, response);

			response.setIsSuccess(true);
			response.setData(gc_protest_info);

			if (response.getMessage() == null
					|| response.getMessage().equalsIgnoreCase("")) {
				response.setMessage("Request was processed successfully");

				if (response.getRetry() == null){
					response.setRetry("N");
				}
			}
		} catch (Exception e) {
			response.setIsSuccess(false);
			response.setException(e.getMessage());
			// response.setData(Util.getStackTraceMessage(e));

			if (response.getMessage() == null
					|| response.getMessage().equalsIgnoreCase("")) {
				response.setMessage("Internal error occurred in EDS server while processing the request");
			}

			handleDBExceptions(response, e);

			if (response.getRetry() == null){
				response.setRetry("N");
			}
		}

		return response;
	}

	/**
	 * @param a_No
	 *            unique id for an EDS case
	 * @return
	 * @throws Exception
	 */
	@RolesAllowed("GCTRACK")
	@GET
	@Path("/get-events/{aNumber}")
	@Produces("application/json")
	public Object get_list_of_epds_event(@PathParam("aNumber") String a_No) throws Exception {

		GC_Track_Service_Call_Response response = new GC_Track_Service_Call_Response();

		try {
			GC_Service gc_Service = (GC_Service) SpringApplicationContext.getBean("GC_Service");

			List<EPDS_event> list_of_EPDS_event = gc_Service.get_list_of_EPDS_event(a_No, response);

			response.setIsSuccess(true);
			response.setData(list_of_EPDS_event);

			if (response.getMessage() == null
					|| response.getMessage().equalsIgnoreCase("")) {
				response.setMessage("Request was processed successfully");
			}
		} catch (Exception e) {
			response.setIsSuccess(false);
			response.setException(e.getMessage());
			// response.setData(Util.getStackTraceMessage(e));

			if (response.getMessage() == null || response.getMessage().equalsIgnoreCase("")) {
				response.setMessage("Internal error occurred in EDS server while processing the request");
			}



			handleDBExceptions(response, e);

			if (response.getRetry() == null){
				response.setRetry("N");
			}



		}

		return response;
	}

	/**
	 * @param event_id
	 *            unique id to a record in gc_track_webservice_event entity
	 * @return Rest response that carries GC_Track_Service_Call_Response object.
	 *         It basically allows user to know whether the call was successful
	 *         or any exception if it failed
	 * @throws Exception
	 */
	@RolesAllowed("GCTRACK")
	@DELETE
	@Path("/delete-event/{event-id}")
	@Produces("application/json")
	public Response deleteEvent(@PathParam("event-id") String event_id)
			throws Exception {
		GC_Track_Service_Call_Response response = new GC_Track_Service_Call_Response();

		try {
			GC_Service gc_Service = (GC_Service) SpringApplicationContext.getBean("GC_Service");
			gc_Service.deleteEvent(event_id,response);

			response.setIsSuccess(true);
			if (response.getMessage() == null
					|| response.getMessage().equalsIgnoreCase("")) {
				response.setMessage("Request was processed successfully");
			}
		} catch (Exception e) {
			response.setIsSuccess(false);
			response.setException(e.getMessage());

			if (response.getMessage() == null
					|| response.getMessage().equalsIgnoreCase("")) {
				response.setMessage("Internal error occurred in EDS server while processing the request");
			}

			handleDBExceptions(response, e);

			if (response.getRetry() == null){
				response.setRetry("N");
			}
		}

		ObjectMapper objMapper = new ObjectMapper();
		String output = objMapper.writeValueAsString(response);

		return Response.status(200).entity(output).build();
	}

	/**
	 * @param a_no
	 *            unique id for an EDS case
	 * @return Rest response that carries GC_Track_Service_Call_Response object.
	 *         It basically allows user to know whether the call was successful
	 *         or any exception if it failed
	 * @throws Exception
	 */
	@RolesAllowed("GCTRACK")
	@DELETE
	@Path("clear-case-events/{aNumber}")
	@Produces("application/json")
	public Response deleteCaseEvents(@PathParam("aNumber") String a_no)
			throws Exception {
		GC_Track_Service_Call_Response response = new GC_Track_Service_Call_Response();

		try {
			GC_Service gc_Service = (GC_Service) SpringApplicationContext
					.getBean("GC_Service");
			gc_Service.clearCaseEvents(a_no, response);

			response.setIsSuccess(true);
			if (response.getMessage() == null
					|| response.getMessage().equalsIgnoreCase("")) {
				response.setMessage("Request was processed successfully");

				if (response.getRetry() == null){
					response.setRetry("N");
				}
			}
		} catch (Exception e) {
			response.setIsSuccess(false);
			response.setException(e.getMessage());

			if (response.getMessage() == null
					|| response.getMessage().equalsIgnoreCase("")) {
				response.setMessage("Internal error occurred in EDS server while processing the request");
			}

			handleDBExceptions(response, e);

			if (response.getRetry() == null){
				response.setRetry("N");
			}
		}

		return Response.status(200).entity(response).build();
	}

	/**
	 * @param gc_track_case
	 *            data shared between EDS case and gc-track case. EDS syncs
	 *            with gc-track based on this data
	 * @return Rest response that carries GC_Track_Service_Call_Response object.
	 *         It basically allows user to know whether the call was successful
	 *         or any exception if it failed
	 * @throws Exception
	 */
	@RolesAllowed("GCTRACK")
	@PUT
	@Path("/notify-case-created/")
	@Consumes("application/json")
	public Response notify_case_created_in_gc_track(GC_track_case gc_track_case)
			throws Exception {
		
		
		if (logger.isDebugEnabled()){
			logger.debug("notify-case-created", gc_track_case.toString());
		}
		
		System.out.println("notify-case-created " + gc_track_case.toString());
		
		GC_Track_Service_Call_Response response = new GC_Track_Service_Call_Response();

		try {
			GC_Service gc_Service = (GC_Service) SpringApplicationContext
					.getBean("GC_Service");
			gc_track_case.setB_no(Util.getBNumberWithBDashPrefix(gc_track_case
					.getB_no()));

			gc_Service.notify_case_created_in_gc_track(gc_track_case, response);

			response.setIsSuccess(true);
			if (response.getMessage() == null
					|| response.getMessage().equalsIgnoreCase("")) {
				response.setMessage("Request was processed successfully");
			}
		} catch (Exception e) {
			response.setIsSuccess(false);
			response.setException(e.getMessage());

			if (response.getMessage() == null
					|| response.getMessage().equalsIgnoreCase("")) {
				response.setMessage("Internal error occurred in EDS server while processing the request");
			}

			handleDBExceptions(response, e);

			if (response.getRetry() == null){
				response.setRetry("N");
			}
		}

		ObjectMapper objMapper = new ObjectMapper();
		String output = objMapper.writeValueAsString(response);

		return Response.status(200).entity(output).build();
	}

	/**
	 * @param gc_track_case
	 *            data shared between EDS case and gc-track case. EDS syncs
	 *            with gc-track based on this data
	 * @return rest response that carries GC_Track_Service_Call_Response object.
	 *         It basically allows user to know whether the call was successful
	 *         or any exception if it failed
	 * @throws Exception
	 */
	@RolesAllowed("GCTRACK")
	@POST
	@Path("/push-case-data")
	@Consumes("application/json")
	public Response pushCaseData(GC_track_case gc_track_case) throws Exception {
		GC_Track_Service_Call_Response response = new GC_Track_Service_Call_Response();

		
		if (logger.isDebugEnabled()){
			logger.debug("push-case-data",gc_track_case.toString());
		}
		System.out.println("push-case-data " + gc_track_case.toString());
		
		try {
			
			GC_Service gc_Service = (GC_Service) SpringApplicationContext
					.getBean("GC_Service");

			gc_track_case.setB_no(Util.getBNumberWithBDashPrefix(gc_track_case
					.getB_no()));
			gc_Service.update(gc_track_case, response, true);

			response.setIsSuccess(true);

			if (response.getMessage() == null
					|| response.getMessage().equalsIgnoreCase("")) {
				response.setMessage("Request was processed successfully");
			}
			if (response.getException() != null
					&& !response.getException().equalsIgnoreCase("")) {
				response.setIsSuccess(false);
			}
		} catch (Exception e) {
			response.setIsSuccess(false);
			response.setException(e.getMessage());

			if (response.getMessage() == null
					|| response.getMessage().equalsIgnoreCase("")) {
				response.setMessage("Internal error occurred in EDS server while processing the request");
			}


			handleDBExceptions(response, e);

			if (response.getRetry() == null){
				response.setRetry("N");
			}
		}

		ObjectMapper objMapper = new ObjectMapper();
		String output = objMapper.writeValueAsString(response);

		return Response.status(200).entity(output).build();
	}

	/**
	 * @param jsonArray_of_multipleCaseData
	 *            an array mulitpse cases pushed by gc-track to update EDS
	 *            cases
	 * @return Rest response that carries GC_Track_Service_Call_Response object.
	 *         It basically allows user to know whether the call was successful
	 *         or any exception if it failed
	 * @throws Exception
	 */
	@RolesAllowed("GCTRACK")
	@POST
	@Path("/push-multiple-case-data")
	@Consumes("application/json")
	public Response pushMultipleCaseData(JSONArray jsonArray_of_multipleCaseData)
			throws Exception {
		
		if (logger.isDebugEnabled()){
			logger.debug("/push-multiple-case-data",jsonArray_of_multipleCaseData.toJSONString());
		}
		
		
		GC_Track_Service_Call_Response response = new GC_Track_Service_Call_Response();

		
		try {
			GC_Service gc_Service = (GC_Service) SpringApplicationContext
					.getBean("GC_Service");

			List<GC_track_case> list_of_gc_track_case = gc_Service
					.convertTo_list_of_gc_track_case(jsonArray_of_multipleCaseData);
			gc_Service.updateEpdsBasedOnListOf_gc_track_case(
					list_of_gc_track_case, response);

			response.setIsSuccess(true);
			if (response.getMessage() == null
					|| response.getMessage().equalsIgnoreCase("")) {
				response.setMessage("Request was processed successfully");
			}
		} catch (Exception e) {
			response.setIsSuccess(false);
			response.setException(e.getMessage());

			if (response.getMessage() == null
					|| response.getMessage().equalsIgnoreCase("")) {
				response.setMessage("Internal error occurred in EDS server while processing the request");
			}

			handleDBExceptions(response, e);

			if (response.getRetry() == null){
				response.setRetry("N");
			}
		}

		ObjectMapper objMapper = new ObjectMapper();
		String output = objMapper.writeValueAsString(response);

		return Response.status(200).entity(output).build();
	}

	@RolesAllowed("GCTRACK")
	@POST
	@Path("/send-update")
	@Consumes("application/json")
	public Response consume_GC_Track_Update(
			List<GC_track_case> list_Of_GC_Track_Update)
			throws JsonGenerationException, JsonMappingException, IOException {
		GC_Service gc_Service = (GC_Service) SpringApplicationContext
				.getBean("gC_Service");
		
		if (logger.isDebugEnabled()){
			logger.debug("/send-update", Arrays.toString(list_Of_GC_Track_Update.toArray()));
		}

		List<GC_Track_Service_Call_Response> list_Of_GC_Track_Service_Call_Response = gc_Service
				.make_Changes_On_EPDS_Based_GC_Track_Updates(list_Of_GC_Track_Update);

		ObjectMapper objMapper = new ObjectMapper();
		String output = objMapper
				.writeValueAsString(list_Of_GC_Track_Service_Call_Response);

		return Response.status(200).entity(output).build();
	}



}
