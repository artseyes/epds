package gov.gao.epds.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import gov.gao.epds.dao.Agency_Info_DAO;
import gov.gao.epds.dao.File_Info_DAO;
import gov.gao.epds.dao.GC_Track_Service_DAO;
import gov.gao.epds.dao.Protest_Info_DAO;
import gov.gao.epds.dao.User_Info_DAO;
import gov.gao.epds.gctrack.BatchUpdateLogInfo;
import gov.gao.epds.gctrack.EPDS_event;
import gov.gao.epds.gctrack.EpdsUpdate;
import gov.gao.epds.gctrack.GC_Track_Service_Call_Response;
import gov.gao.epds.gctrack.GC_track_case;
import gov.gao.epds.gctrack.Gc_protest_info;
import gov.gao.epds.gctrack.NewEpdsEventsPerCase;
import gov.gao.epds.gctrack.NewProtest;
import gov.gao.epds.gctrack.OrgInfo;
import gov.gao.epds.gctrack.RepInfo;
import gov.gao.epds.persistence.entity.File_Info;
import gov.gao.epds.persistence.entity.GC_Track_Service_Event;
import gov.gao.epds.persistence.entity.Protest_Info;
import gov.gao.epds.persistence.entity.User_Info;
import gov.gao.epds.persistence.entity.User_Protest_Role_Bridge;
import gov.gao.epds.utils.Date_Util;
import gov.gao.epds.utils.GC_track_util;
import gov.gao.epds.utils.SpringApplicationContext;
import gov.gao.epds.utils.Util;

/**
 * This class helps to read or write data onto gc_track_webservice_event db
 * table.
 * 
 * @author RAdhikari
 * 
 */
@Service
public class GC_Service {
	@Autowired
	private GC_Track_Service_DAO gc_Track_Service_DAO;
	@Autowired
	private Protest_Info_DAO protest_Info_DAO;
	@Autowired
	private User_Info_DAO user_info_DAO;
	@Autowired
	private Agency_Info_DAO agency_info_DAO;
	@Autowired
	private File_Info_DAO file_info_DAO;
	
	@Autowired
	private DashboardService dashboardService;

	/**
	 * 
	 * @param protest_Info
	 *            : new protest that has been filed
	 * @throws Exception 
	 */
	public void set_Event_For_New_Protest_Info(Protest_Info protest_Info) throws Exception {
		GC_Track_Service_Event gc_Track_Service_Event = get_gc_track_service_event(
				protest_Info, "New Protest");

		gc_Track_Service_DAO.save(gc_Track_Service_Event);	
	}

	/**
	 * In order to create record in gc-track_service_event table, this method
	 * helps to set up a basic template
	 * 
	 * @param protest_Info
	 *            : protest for which the event is recorded
	 * @param event_Type
	 *            : one of different evenTypes (refer to 'Gc-Track Events and
	 *            Requests.xlsx')
	 * @return
	 * @throws Exception 
	 */
	private GC_Track_Service_Event get_gc_track_service_event(
			Protest_Info protest_Info, String event_Type) throws Exception {
		GC_Track_Service_Event gc_Track_Service_Event = new GC_Track_Service_Event();
		
		gc_Track_Service_Event.setA_No(protest_Info.getA_No());
		gc_Track_Service_Event.setB_No(protest_Info.getB_No());
		gc_Track_Service_Event.setEvent_Date(Date_Util
				.getCurrentDate("MM/dd/yyyy HH:mm:ss z"));
		gc_Track_Service_Event.setStatus("New");
		gc_Track_Service_Event.setEvent_Type(event_Type);
		gc_Track_Service_Event.setCase_type(protest_Info.getCase_Type());
		try {
			gc_Track_Service_Event.setFiled_date(Date_Util
					.convertToSpecifiedFormat(
							protest_Info.getSubmission_Date(),
							"MMM dd yyyy HH:mm:ss z", "MM/dd/yyyy HH:mm:ss z"));
		} catch (ParseException e) {
			// come back later
			gc_Track_Service_Event.setFiled_date(protest_Info
					.getSubmission_Date());
			e.printStackTrace();
		}
		gc_Track_Service_Event.setProtester(protest_Info.getCompany_Name());
		gc_Track_Service_Event.setSolicitation_no(protest_Info
				.getSolicitation_No());
		gc_Track_Service_Event.setAgency(agency_info_DAO.getAgencyName(protest_Info.getAgency_Info_Id()));

		return gc_Track_Service_Event;
	}

	/**
	 * When join/unjoin event occurs or any attribute in case headers displayed
	 * in case-docket-sheet is changed by GAO User, 'Change in Protest' event
	 * will occur
	 * 
	 * @param protest_info
	 *            : protest for which the event is recorded
	 * @param typeOfChange
	 *            : refer to CaseDocketSheetService ->
	 *            changeProtest_Info_Attribute
	 * @param newValue
	 *            : new value, refer to CaseDocketSheetService ->
	 *            changeProtest_Info_Attribute
	 * @throws Exception 
	 */
	public void set_ChangeIn_Protest_Info_Event(Protest_Info protest_info,
			String typeOfChange, String newValue) throws Exception {
		GC_Track_Service_Event gc_Track_Service_Event = get_gc_track_service_event(
				protest_info, "Change in Protest");

		String eventDescription = "Change of " + typeOfChange;
		if (typeOfChange.equalsIgnoreCase("joinCases")
				|| typeOfChange.equalsIgnoreCase("unJoinCases")) {
			eventDescription = typeOfChange;
		}

		gc_Track_Service_Event.setEvent_description(eventDescription);
		gc_Track_Service_Event.setInfo("New " + typeOfChange + ": " + newValue);

		gc_Track_Service_DAO.save(gc_Track_Service_Event);
	}

	/**
	 * When a new agency attorney is assigned to case or gets access to a case,
	 * this event will be captured
	 * 
	 * 
	 * @param protest_Info
	 *            : protest for which the event is recorded
	 * @param user_info
	 *            : agency representative
	 * @throws Exception 
	 */
	public void set_Agency_Representative_Assigned_Notice(
			Protest_Info protest_Info, User_Info user_info) throws Exception {
		GC_Track_Service_Event gc_Track_Service_Event = get_gc_track_service_event(
				protest_Info, "Agency Representative Assigned");
		gc_Track_Service_Event.setEvent_type_id(4);
		gc_Track_Service_Event.setInfo(user_info.getLast_Name() + ", " + user_info.getFirst_Name());
		
		gc_Track_Service_Event.setRepresentative_User_Id(user_info.getUser_Id());

		gc_Track_Service_DAO.save(gc_Track_Service_Event);
	}

	/**
	 * @param response
	 *            : holds service-response data to be sent to gc-track
	 * @return: Date needed In gc-track -> EPDS landing page (refer to
	 *          'Gc-track-doc' -> Gc-Track-EPDS-Landing-Page)
	 */
	public EpdsUpdate get_epdsUpdate(GC_Track_Service_Call_Response response,String aNum) {
		EpdsUpdate epdsUpdate = new EpdsUpdate();
		EPDS_event epds_event;
		List<EPDS_event> list_of_epds_event;

		List<Protest_Info> list_of_new_protest_info = new ArrayList<Protest_Info>();
		List<GC_Track_Service_Event> list_Of_GC_Track_Service_Event = new ArrayList<GC_Track_Service_Event>();

		if (aNum != null){
			list_of_new_protest_info = gc_Track_Service_DAO
					.get_new_protest_Info_byAnum(aNum);
		}else{
			list_of_new_protest_info = gc_Track_Service_DAO
					.get_list_of_new_protest_Info();
		}

		List<NewProtest> list_of_newProtest = get_list_of_newProtest(list_of_new_protest_info, response);

		if (aNum != null){
			list_Of_GC_Track_Service_Event = gc_Track_Service_DAO
					.get_list_of_GC_Track_webService_eventByAnum(aNum);
		}else{
			list_Of_GC_Track_Service_Event = gc_Track_Service_DAO
					.get_list_of_GC_Track_webService_event();
		}

		List<NewEpdsEventsPerCase> list_of_newEpdsEventsPerCase = new ArrayList<NewEpdsEventsPerCase>();

		if (list_Of_GC_Track_Service_Event != null) {
			for (int i = 0; i < list_Of_GC_Track_Service_Event.size(); i++) {
				GC_Track_Service_Event gc_track_service_event = list_Of_GC_Track_Service_Event.get(i);

				if (gc_track_service_event.getEvent_Type().equalsIgnoreCase("Change in Protest")) {
					continue;
				}

				list_of_epds_event = new ArrayList<EPDS_event>();

				epds_event = setEpdsEvent(gc_track_service_event);

				list_of_epds_event.add(epds_event);

				for (int j = i + 1; j < list_Of_GC_Track_Service_Event.size(); j++) {
					GC_Track_Service_Event gc_track_service_event2 = list_Of_GC_Track_Service_Event
							.get(j);

					if (gc_track_service_event2.getEvent_Type()
							.equalsIgnoreCase("Change in Protest")) {
						continue;
					}

					if (gc_track_service_event.getA_No().equalsIgnoreCase(
							gc_track_service_event2.getA_No())) {
						epds_event = setEpdsEvent(gc_track_service_event2);

						list_of_epds_event.add(epds_event);

						list_Of_GC_Track_Service_Event.remove(j);
						j--;
					}
				}

				gc_track_service_event
						.setList_of_EPDS_event(list_of_epds_event);

				NewEpdsEventsPerCase newEpdsEventsPerCase = setNewEpdsEventsPerCase(
						list_of_epds_event, gc_track_service_event);

				list_of_newEpdsEventsPerCase.add(newEpdsEventsPerCase);
			}
		}

		epdsUpdate.setList_of_new_protest(list_of_newProtest);
		epdsUpdate.setList_of_newEpdsEventsPerCase(list_of_newEpdsEventsPerCase);

		return epdsUpdate;
	}

	/**
	 * @param list_of_epds_event
	 * @param gc_track_service_event
	 * @return: object of type NewEpdsEventsPerCase that captures basic
	 *          attributes and a list of events that happened for a case
	 */
	private NewEpdsEventsPerCase setNewEpdsEventsPerCase(
			List<EPDS_event> list_of_epds_event,
			GC_Track_Service_Event gc_track_service_event) {
		NewEpdsEventsPerCase newEpdsEventsPerCase = new NewEpdsEventsPerCase();
		newEpdsEventsPerCase.setA_no(gc_track_service_event.getA_No());
		newEpdsEventsPerCase.setPrimaryAgency(gc_track_service_event.getAgency());
		newEpdsEventsPerCase.setB_no(Util.stripBDashPrefix(gc_track_service_event.getB_No()));
		newEpdsEventsPerCase.setCase_type(gc_track_service_event.getCase_type());

		newEpdsEventsPerCase.setFiled_date(Date_Util
				.convertToSpecifiedDateFormatIfNotAlready(
						gc_track_service_event.getEvent_Date(),
						"MM/dd/yyyy HH:mm:ss z"));

		newEpdsEventsPerCase.setProtester(gc_track_service_event.getProtester());
		newEpdsEventsPerCase.setSolicitation_no(gc_track_service_event.getSolicitation_no());

		newEpdsEventsPerCase.setList_of_EPDS_event(list_of_epds_event);
		return newEpdsEventsPerCase;
	}

	/**
	 * @param gc_track_service_event
	 *            : database record of gc-track event
	 * @return: object of type EPDS_event that captured different identifying
	 *          information of a gc-track event
	 */
	private EPDS_event setEpdsEvent(
			GC_Track_Service_Event gc_track_service_event) {
		EPDS_event epds_event;
		epds_event = new EPDS_event();
		epds_event.setEvent_Id(gc_track_service_event.getEvent_Id() + "");
		epds_event.setEvent_type_id(gc_track_service_event.getEvent_type_id());
		epds_event.setEvent_type(gc_track_service_event.getEvent_Type());
		/*try {} catch (ParseException e) {
			epds_event.setEvent_date(gc_track_service_event.getEvent_Date());
			e.printStackTrace();
		}*/

		/*epds_event.setEvent_date(Date_Util
				.convertToSpecifiedDateFormatIfNotAlready(
						gc_track_service_event.getEvent_Date(),
						"MM/dd/yyyy HH:mm:ss z"));*/
		
		epds_event.setEvent_date(gc_track_service_event.getEvent_Date());


           if (gc_track_service_event.getRepresentative_User_Id() != null){

			try {

				User_Info userInfo = user_info_DAO.getUser_Info_By_User_Id(gc_track_service_event.getRepresentative_User_Id());


				if (userInfo != null){

					epds_event.setRepInfo(populateRepInfo(userInfo));

					if (userInfo.getRole_id() == 1) {//protester/intervenor
						User_Protest_Role_Bridge uprb = user_info_DAO.getUser_Protest_Role_Bridge(gc_track_service_event.getA_No(),gc_track_service_event.getRepresentative_User_Id());

						if (uprb != null && (uprb.getRole_Id() == 2  || uprb.getRole_Id() == 9 ) ){//primary or secondary intervenor
							epds_event.setOrgInfo(populateOrgInfo(uprb.getIntervenor_Company_Name(), uprb.getIntervenor_Company_Address()));
						}
					}else if (userInfo.getRole_id() == 5 || userInfo.getRole_id() == 6) {//Agency Rep or Agency POC

						OrgInfo orgInfo = new OrgInfo();

						orgInfo.setId(userInfo.getFirm_id());
						orgInfo.setOrgName(userInfo.getFirm_Name());

						epds_event.setOrgInfo(orgInfo);

					}
				}




			} catch (Exception e) {

				System.out.println("There was problem populating repInfo and orgInfo " + gc_track_service_event.getA_No());
				e.printStackTrace();
			}
		}

		epds_event.setEvent_description(gc_track_service_event.getEvent_description());
		epds_event.setInfo(gc_track_service_event.getInfo());
		return epds_event;
	}

	private OrgInfo populateOrgInfo(String intervenor_Company_Name, String intervenor_Company_Address) {
		OrgInfo orgInfo = new OrgInfo();

		orgInfo.setOrgName(intervenor_Company_Name);
		orgInfo.setOrgAddress(intervenor_Company_Address);

		return orgInfo;
	}

	private RepInfo populateRepInfo(User_Info userInfo) {

		PropertyUtilsBean propUtils = new PropertyUtilsBean();
		RepInfo repInfo = new RepInfo();

		if (userInfo != null){
			repInfo.setFirst_Name(userInfo.getFirst_Name());
			repInfo.setMiddle_initial(userInfo.getMiddle_initial());
			repInfo.setLast_Name(userInfo.getLast_Name());
			repInfo.setEmail(userInfo.getEmail());
			repInfo.setAddress1(userInfo.getAddress1());
			repInfo.setAddress2(userInfo.getAddress2());
			repInfo.setCity(userInfo.getCity());
			repInfo.setCountry(userInfo.getCountry());
			repInfo.setState(userInfo.getState());
			repInfo.setZip_Code(userInfo.getZip_Code());
			repInfo.setFirm_Name(userInfo.getFirm_Name());
			repInfo.setPhone_No(userInfo.getPhone_No());
			repInfo.setFax_No(userInfo.getFax_No());
			repInfo.setPrefix(userInfo.getPrefix());
			repInfo.setSuffix(userInfo.getSuffix());
			repInfo.setFirm_id(userInfo.getFirm_id());
			/*try {
				propUtils.copyProperties(protestRepInfo, user_Info);
			}catch (Exception ex) {
				ex.printStackTrace();
			}*/
		}
		return repInfo;
	}



	/**
	 * @param list_of_new_protest_info
	 *            : list of new EPDS cases that gc-track needs to know
	 * @param response
	 *            : data response sent to gc-track
	 * @return: list of gc-track version of EPDS cases (all new that gc-track
	 *          needs to know)
	 */
	private List<NewProtest> get_list_of_newProtest(
			List<Protest_Info> list_of_new_protest_info,
			GC_Track_Service_Call_Response response) {
		List<NewProtest> list_of_newProtest = new ArrayList<NewProtest>();

		if (list_of_new_protest_info != null
				&& list_of_new_protest_info.size() > 0) {

			for (Protest_Info eachProtest_Info : list_of_new_protest_info) {
				NewProtest newProtest = new NewProtest();
				newProtest.setA_no(eachProtest_Info.getA_No());
				newProtest.setCase_type(eachProtest_Info.getCase_Type());
				newProtest.setB_no(Util
						.stripBDashPrefix(eachProtest_Info.getB_No()));
				newProtest
						.setCase_type(eachProtest_Info.getCase_Type());

				try {
					newProtest.setFiled_date(Date_Util.convertToSpecifiedFormat(
    					eachProtest_Info.getSubmission_Date(), "MMM dd yyyy HH:mm:ss z", "MM/dd/yyyy HH:mm:ss z"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				populatePartiesAndOrgInfo(newProtest,eachProtest_Info.getA_No());
				newProtest.setProtester(eachProtest_Info.getCompany_Name());
				newProtest.setSolicitation_no(eachProtest_Info.getSolicitation_No());
				newProtest.setTransaction_status(eachProtest_Info.getTransaction_Status());

				list_of_newProtest.add(newProtest);
			}

		}

		return list_of_newProtest;
	}


	
	private void populatePartiesAndOrgInfo(NewProtest newProtest, String a_No){
		List<User_Info> userInfos = null;
		try {
			userInfos = user_info_DAO.getUser_InfoListByAnum(a_No);

			List<RepInfo> repInfos = new ArrayList<RepInfo>();
			List<OrgInfo> orgInfos = new ArrayList<OrgInfo>();

			Set<String> dupes = new HashSet<String>();
			if (userInfos != null){

				for (User_Info eachUserInfo : userInfos) {
					RepInfo repInfo = populateRepInfo(eachUserInfo);
					OrgInfo orgInfo = new OrgInfo();

					switch (eachUserInfo.getRole_id()){

					case 1:
						repInfo.setPartyType("PROTESTER");
						break;
					case 2:

						if (!dupes.contains(eachUserInfo.getIntervenor_Company_Name())){
							dupes.add(eachUserInfo.getIntervenor_Company_Name());
							orgInfo.setOrgName(eachUserInfo.getIntervenor_Company_Name());
							orgInfo.setOrgAddress(eachUserInfo.getIntervenor_Company_Address());
							orgInfo.setOrgType("INTERVENOR");
							orgInfos.add(orgInfo);
						}

						repInfo.setPartyType("INTERVENOR");
						break;
					case 4:
						repInfo.setPartyType("SECONDARY PROTESTER");
						break;
					case 5:
					case 6:

						if (!dupes.contains(eachUserInfo.getFirm_id().toString())){
							dupes.add(eachUserInfo.getFirm_id().toString());
							orgInfo.setId(eachUserInfo.getFirm_id());
							orgInfo.setOrgName(eachUserInfo.getFirm_Name());
							orgInfo.setOrgType("AGENCY");
							orgInfos.add(orgInfo);
						}

						repInfo.setPartyType("AGENCY REP");
						break;
					case 3:
					case 7:
					case 8:
						repInfo.setPartyType("GAO");
						break;
					case 9:
						if (!dupes.contains(eachUserInfo.getIntervenor_Company_Name())){
							dupes.add(eachUserInfo.getIntervenor_Company_Name());
							orgInfo.setOrgName(eachUserInfo.getIntervenor_Company_Name());
							orgInfo.setOrgAddress(eachUserInfo.getIntervenor_Company_Address());
							orgInfo.setOrgType("INTERVENOR");
							orgInfos.add(orgInfo);
						}
						repInfo.setPartyType("SECONDARY INTERVENOR");
						break;
					}

					repInfos.add(repInfo);
				}


				newProtest.setParties(repInfos);
				newProtest.setOrganizations(orgInfos);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}




	}


	private void populatePartiesAndOrgInfo(Gc_protest_info newProtest, String a_No){
		List<User_Info> userInfos = null;
		try {
			userInfos = user_info_DAO.getUser_InfoListByAnum(a_No);

			List<RepInfo> repInfos = new ArrayList<RepInfo>();
			List<OrgInfo> orgInfos = new ArrayList<OrgInfo>();

			Set<String> dupes = new HashSet<String>();
			if (userInfos != null){

				for (User_Info eachUserInfo : userInfos) {
					RepInfo repInfo = populateRepInfo(eachUserInfo);
					OrgInfo orgInfo = new OrgInfo();

					switch (eachUserInfo.getRole_id()){

					case 1:
						repInfo.setPartyType("PROTESTER");
						break;
					case 2:

						if (!dupes.contains(eachUserInfo.getIntervenor_Company_Name())){
							dupes.add(eachUserInfo.getIntervenor_Company_Name());
							orgInfo.setOrgName(eachUserInfo.getIntervenor_Company_Name());
							orgInfo.setOrgAddress(eachUserInfo.getIntervenor_Company_Address());
							orgInfo.setOrgType("INTERVENOR");
							orgInfos.add(orgInfo);
						}

						repInfo.setPartyType("INTERVENOR");
						break;
					case 4:
						repInfo.setPartyType("SECONDARY PROTESTER");
						break;
					case 5:
					case 6:

						if (!dupes.contains(eachUserInfo.getFirm_id().toString())){
							dupes.add(eachUserInfo.getFirm_id().toString());
							orgInfo.setId(eachUserInfo.getFirm_id());
							orgInfo.setOrgName(eachUserInfo.getFirm_Name());
							orgInfo.setOrgType("AGENCY");
							orgInfos.add(orgInfo);
						}

						repInfo.setPartyType("AGENCY REP");
						break;
					case 3:
					case 7:
					case 8:
						repInfo.setPartyType("GAO");
						break;
					case 9:
						if (!dupes.contains(eachUserInfo.getIntervenor_Company_Name())){
							dupes.add(eachUserInfo.getIntervenor_Company_Name());
							orgInfo.setOrgName(eachUserInfo.getIntervenor_Company_Name());
							orgInfo.setOrgAddress(eachUserInfo.getIntervenor_Company_Address());
							orgInfo.setOrgType("INTERVENOR");
							orgInfos.add(orgInfo);
						}
						repInfo.setPartyType("SECONDARY INTERVENOR");
						break;
					}

					repInfos.add(repInfo);
				}


				newProtest.setParties(repInfos);
				newProtest.setOrganizations(orgInfos);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}




	}

	private void setParseExceptionMessage(
			GC_Track_Service_Call_Response response, String a_no,
			String typeOfDate) {
		String response_message = response.getMessage();
		if (response_message != null && !response_message.equalsIgnoreCase("")) {
			response_message += ": ";
		} else {
			response_message = "";
		}

		response_message += "Unable to parse " + typeOfDate
				+ " date of protest case with a_no: " + a_no;
		response.setMessage(response_message);
	}

	/**
	 * When an intervenor is approved to access a case, this event will be
	 * captured
	 * 
	 * @param protest_Info
	 *            : protest for which the event is recorded
	 * @param file_Info
	 *            :
	 * @throws Exception 
	 */
	public void set_Intervener_Approved_Event(Protest_Info protest_Info,
			File_Info file_Info) throws Exception {
		GC_Track_Service_Event gc_Track_Service_Event = get_gc_track_service_event(
				protest_Info, "Intervenor Approved");
		gc_Track_Service_Event.setEvent_type_id(1);
		gc_Track_Service_Event.setEvent_description("INTERVENOR Approved");
		/*
		 * gc_Track_Service_Event.setInfo("INTERVENOR Company Name: " +
		 * intervenorCompanyName);
		 */
		gc_Track_Service_Event.setRepresentative_User_Id(file_Info.getSubmitter_User_Id());
		
		gc_Track_Service_Event.setInfo(file_Info.getCompany_Name());

		gc_Track_Service_DAO.save(gc_Track_Service_Event);
	}

	
	/**
	 * When agency-report or agency-report comment is filed for the first time,
	 * this event is captured (ignore 'Notice of Appearance')
	 * 
	 * @param user_Info
	 * @param docTypeId
	 * @param aNum 
	 * @throws Exception 
	 */
	
	public void set_Event_For_Special_Doc_Types(
			User_Info user_Info, int docTypeId, String aNum) throws Exception {
		boolean is_Agency_Report_Filed_For_First_Time = false;
		boolean is_Agency_Report_Comments_Filed_For_First_Time = false;
		boolean is_notice_of_appearance_filed_for_first_time = false;
		boolean is_Acknowledgment_Package = false;

		// check if event triggerable type of document is being submitted
		switch (docTypeId) {
		case 14:
		case 216:
			is_Agency_Report_Comments_Filed_For_First_Time = true;
			break;
		case 74:
			is_Agency_Report_Filed_For_First_Time = true;
			break;
        case 103:
        case 104:
            is_Acknowledgment_Package = true;
            break;
		default:
			return;
		}

		Protest_Info protest_Info = dashboardService.getProtestInfoBasedOnUserIdAndANo(user_Info, aNum);

		String user_Protest_Role = protest_Info.getRole();

	    if (user_Protest_Role == null)
			return;

		// check if event triggerable type of document is being submitted for
		// the first time
		if (is_Agency_Report_Filed_For_First_Time
				|| is_Agency_Report_Comments_Filed_For_First_Time
				|| is_notice_of_appearance_filed_for_first_time) {
			
			List<File_Info> file_Info_List = file_info_DAO.getFileInfoByDocIdAndANo(docTypeId, aNum);
			
			Set<String> dupe = new HashSet<String>();
			file_Info_List.removeIf(fileInfo->!dupe.add(fileInfo.getOriginalSubmissionDate()));
			
			
			if (null != file_Info_List && file_Info_List.size() > 1){
				is_Agency_Report_Comments_Filed_For_First_Time = false;
				is_Agency_Report_Filed_For_First_Time = false;
			}
			
			
		}

		if (is_Agency_Report_Filed_For_First_Time) {
			set_Event_For_Agency_Report_Filed_For_First_Time(protest_Info);
		} else if (is_Agency_Report_Comments_Filed_For_First_Time) {
			set_Event_For_Agency_Report_Comments_Filed_For_First_Time(protest_Info);
		} else if (is_Acknowledgment_Package) {
            set_Event_For_Acknowledgment_Package(protest_Info);
        }
		
		/*
		 * else if (is_notice_of_appearance_filed_for_first_time) {
		 * set_event_for_notice_of_appearance_filed_for_first_time
		 * (protest_Info); }
		 */

	}
	public void set_event_for_notice_of_appearance_filed_for_first_time(
			Protest_Info protest_Info) throws Exception {
		GC_Track_Service_Event gc_Track_Service_Event = get_gc_track_service_event(
				protest_Info, "Notice of Appearance Actual Date");

		gc_Track_Service_Event
				.setEvent_description("Notice of Appearance filed for first time");

		gc_Track_Service_Event.setInfo("Filed Date: "
				+ gc_Track_Service_Event.getEvent_Date());
		gc_Track_Service_DAO.save(gc_Track_Service_Event);

	}

	/**
	 * "First Agency Report Comments" event recorded
	 * 
	 * @param protest_Info
	 *            : protest for which the event is recorded
	 * @throws Exception 
	 */
	public void set_Event_For_Agency_Report_Comments_Filed_For_First_Time(
			Protest_Info protest_Info) throws Exception {
		GC_Track_Service_Event gc_Track_Service_Event = get_gc_track_service_event(
				protest_Info, "First Agency Report Comments");
		gc_Track_Service_Event.setEvent_type_id(3);
		gc_Track_Service_Event
				.setEvent_description("Agency Report Comments Filed for first time");

		/*
		 * gc_Track_Service_Event.setInfo("Filed Date: " +
		 * gc_Track_Service_Event.getEvent_Date());
		 */
		gc_Track_Service_Event.setInfo(gc_Track_Service_Event.getEvent_Date());
		gc_Track_Service_DAO.save(gc_Track_Service_Event);
	}

	/**
	 * "First Agency Report Submitted" event recorded
	 * 
	 * @param protest_Info
	 *            : protest for which the event is recorded
	 * @throws Exception 
	 */
	public void set_Event_For_Agency_Report_Filed_For_First_Time(
			Protest_Info protest_Info) throws Exception {
		GC_Track_Service_Event gc_Track_Service_Event = get_gc_track_service_event(
				protest_Info, "First Agency Report Submitted");

		gc_Track_Service_Event.setEvent_type_id(2);
		gc_Track_Service_Event
				.setEvent_description("Agency Report Filed for first time");
		/*
		 * gc_Track_Service_Event.setInfo("Filed Date: " +
		 * gc_Track_Service_Event.getEvent_Date());
		 */
		gc_Track_Service_Event.setInfo(gc_Track_Service_Event.getEvent_Date());

		gc_Track_Service_DAO.save(gc_Track_Service_Event);
	}

    /**
     * "First Agency Report Comments" event recorded
     *
     * @param protest_Info
     *            : protest for which the event is recorded
     * @throws Exception
     */
    public void set_Event_For_Acknowledgment_Package(
            Protest_Info protest_Info) throws Exception {
        GC_Track_Service_Event gc_Track_Service_Event = get_gc_track_service_event(
                protest_Info, "Acknowledgement Letter");
        gc_Track_Service_Event.setEvent_type_id(6);
        gc_Track_Service_Event.setEvent_description("Acknowledgement Letter Submitted");

        gc_Track_Service_Event.setInfo(gc_Track_Service_Event.getEvent_Date());
        gc_Track_Service_DAO.save(gc_Track_Service_Event);
    }

	/**
	 * "Protest Filed Date Updated" event recorded
	 * 
	 * @param protest_Info
	 *            : protest for which event is recorded
	 * @param newValue
	 * @throws Exception 
	 */
	public void set_Event_For_Protest_Filed_Date_Updated(
			Protest_Info protest_Info, String newValue) throws Exception {
		GC_Track_Service_Event gc_Track_Service_Event = get_gc_track_service_event(
				protest_Info, "Protest Filed Date Updated");

		gc_Track_Service_Event.setEvent_type_id(5);
		gc_Track_Service_Event
				.setEvent_description("Protest Filed Date Updated");
		/* gc_Track_Service_Event.setInfo("Filed Date/Time: " + newValue); */
		gc_Track_Service_Event.setInfo(newValue);

		gc_Track_Service_DAO.save(gc_Track_Service_Event);
	}

	public List<GC_Track_Service_Call_Response> make_Changes_On_EPDS_Based_GC_Track_Updates(
			List<GC_track_case> list_Of_GC_Track_Update) {

		return null;
	}

	public List<GC_Track_Service_Event> get_List_Of_GC_Track_Service_Events() {
		List<GC_Track_Service_Event> list_Of_GC_Track_Service_Event = gc_Track_Service_DAO
				.get_list_of_GC_Track_webService_event();

		return list_Of_GC_Track_Service_Event;
	}

	/**
	 * when a case creation is completed in gc-track, gc-track updates EPDS
	 * about it. In general, b-number and attorney info for a case is updated
	 * through this channel
	 * 
	 * @param gc_track_case
	 *            : protest data sent by gc-track
	 * @param response
	 *            : data sent back to gc-track
	 * @throws Exception
	 */
	public void notify_case_created_in_gc_track(GC_track_case gc_track_case,
			GC_Track_Service_Call_Response response) throws Exception {
		updateEpdsBasedOnGC_track_case(gc_track_case, response, true, true);
		removeNewCaseGcEvent(gc_track_case);
	}

	/**
	 * GC_Track_Service_Event record with even-type = 'New Protest' will be
	 * deleted
	 * 
	 * @param gc_track_case
	 *            : protest data sent by gc-track
	 * @throws Exception
	 */
	public void removeNewCaseGcEvent(GC_track_case gc_track_case)
			throws Exception {
		GC_Track_Service_Event gc_Track_Service_Event = gc_Track_Service_DAO
				.get_GC_Track_Service_Event_For_New_Case(gc_track_case
						.getA_no());

		if (gc_Track_Service_Event != null) {
			gc_Track_Service_DAO.delete(gc_Track_Service_Event);
		}
	}

	/**
	 * @param a_No
	 *            : aNumber of a case
	 * @param response
	 *            : response data sent to gc-track
	 * @return:All gc-track events that need to updated to gc-track are
	 *             retrieved
	 * @throws Exception
	 */
	public List<EPDS_event> get_list_of_EPDS_event(String a_No,
			GC_Track_Service_Call_Response response) throws Exception {


		Protest_Info protest_Info = protest_Info_DAO.getProtestByA_no(a_No);

		if (protest_Info == null) {
			response.setMessage("Protest Case for provided a_no: " + a_No
					+ " doesn't exist. Please try again with correct A#.");

			response.setRetry("N");
			throw new EntityNotFoundException();
		}


		List<GC_Track_Service_Event> list_of_GC_track_service_event = gc_Track_Service_DAO
				.get_list_of_gc_track_service_event_based_on_a_no(a_No);

		if (list_of_GC_track_service_event == null) {
			response.setMessage("No events found for protest case with a_no = "
					+ a_No);
			response.setIsSuccess(true);
			response.setData(new ArrayList<EPDS_event>());

			return null;
		}

		List<EPDS_event> list_of_epds_event = new ArrayList<EPDS_event>();
		EPDS_event epds_event;

		for (GC_Track_Service_Event each_GC_Track_Service_Event : list_of_GC_track_service_event) {
			epds_event = new EPDS_event();
			epds_event.setEvent_Id(each_GC_Track_Service_Event.getEvent_Id() + "");
			epds_event.setEvent_type(each_GC_Track_Service_Event
					.getEvent_Type());
			epds_event.setEvent_type_id(each_GC_Track_Service_Event
					.getEvent_type_id());

            epds_event.setEvent_date(each_GC_Track_Service_Event.getEvent_Date());
			
			epds_event.setInfo(each_GC_Track_Service_Event.getInfo());

			populaterepInfoAndOrgInfo(each_GC_Track_Service_Event,epds_event);

			list_of_epds_event.add(epds_event);
		}

		return list_of_epds_event;
	}

	/**
	 * @param epds_event
	 */
	public void populaterepInfoAndOrgInfo(GC_Track_Service_Event gc_track_service_event, EPDS_event epds_event) {

		if (gc_track_service_event.getRepresentative_User_Id() != null){

			try {

				User_Info userInfo = user_info_DAO.getUser_Info_By_User_Id(gc_track_service_event.getRepresentative_User_Id());


				if (userInfo != null){

					epds_event.setRepInfo(populateRepInfo(userInfo));

					if (userInfo.getRole_id() == 1) {//protester/intervenor
						User_Protest_Role_Bridge uprb = user_info_DAO.getUser_Protest_Role_Bridge(gc_track_service_event.getA_No(),gc_track_service_event.getRepresentative_User_Id());

						if (uprb != null && (uprb.getRole_Id() == 2  || uprb.getRole_Id() == 9 ) ){//primary or secondary intervenor
							epds_event.setOrgInfo(populateOrgInfo(uprb.getIntervenor_Company_Name(), uprb.getIntervenor_Company_Address()));
						}
					}else if (userInfo.getRole_id() == 5 || userInfo.getRole_id() == 6) {//Agency Rep or Agency POC

						OrgInfo orgInfo = new OrgInfo();

						orgInfo.setId(userInfo.getFirm_id());
						orgInfo.setOrgName(userInfo.getFirm_Name());

						epds_event.setOrgInfo(orgInfo);

					}
				}




			} catch (Exception e) {

				System.out.println("There was problem populating repInfo and orgInfo " + gc_track_service_event.getA_No());
				e.printStackTrace();
			}
		}
	}

	/**
	 * GC_Track_Service_Event record with provided eventId is deleted
	 * 
	 * @param event_id
	 *            : unique id of gc_track_service_event table
	 * @param response
	 * @throws Exception
	 */
	public void deleteEvent(String event_id, GC_Track_Service_Call_Response response) throws Exception {
		GC_Track_Service_Event gc_Track_Service_Event = gc_Track_Service_DAO.getGC_Track_Service_Event_based_on_event_id(Integer.valueOf(event_id));
		//gc_Track_Service_Event.setEvent_Id(Integer.valueOf(event_id));

		if (gc_Track_Service_Event == null) {
			response.setMessage("Event Id : " + event_id
					+ " doesn't exist. Please try again with a different eventId.");

			response.setRetry("N");
			throw new EntityNotFoundException();
		}


		gc_Track_Service_DAO.delete(gc_Track_Service_Event);
	}

	/**
	 * It gc-track changes on a case to EPDS. It comes into play for
	 * notify-case-created / push-case-data / push-multiple-case-data
	 * 
	 * @param gc_track_case
	 *            : case attributes that are shared by both EPDs and gc-track
	 * @param response
	 *            : response data sent back to EPDS
	 * @param isSingleCaseUpdate
	 *            : this is to distinguish whether this method is called during
	 *            single case push request or multiple case push request by
	 *            gc-track
	 * @param isItCaseCreatedNotification
	 * @throws Exception
	 */
	@Transactional
	synchronized public void updateEpdsBasedOnGC_track_case(GC_track_case gc_track_case,
			GC_Track_Service_Call_Response response,
			boolean isSingleCaseUpdate, boolean isItCaseCreatedNotification)
			throws Exception {
		
		
		if (gc_track_case.getB_no() != null) {
			int len = (gc_track_case.getB_no().indexOf('.') >= 0) ? 10 : 8;
			if (gc_track_case.getB_no().length() < len) {
				response.setMessage(gc_track_case.getB_no() + " B# has to be at least 6 digits. Please try again by passing correct B#");
				response.setRetry("N");
				throw new IllegalArgumentException();
			}
		}

		Protest_Info updated_protest_info = GC_track_util
				.getUpdatedProtest_info(gc_track_case, user_info_DAO,
						protest_Info_DAO, file_info_DAO, gc_Track_Service_DAO,
						response, isItCaseCreatedNotification);

		protest_Info_DAO.updateProtest_Info(updated_protest_info);

		if (isSingleCaseUpdate) {
			Gc_protest_info gc_protest_info = getGc_protest_info(
					updated_protest_info.getA_No(), response);

			response.setData(gc_protest_info);
		}
		// this is needed because sometime the user do bulk push manually before pushing the case that 
		//was newly filed in GC Track(not using notify new case created service ) .. we need to clear new cases as 
		removeNewCaseGcEvent(gc_track_case); 

	}

	/**
	 * When a push-case-data button is clicked in gc-track, any changes to a
	 * case will be updated in EPDS
	 * 
	 * @param gc_track_case
	 *            : case attributes that are shared by both EPDs and gc-track
	 * @param response
	 *            : response data sent back to EPDS
	 * @param isSingleCaseUpdate
	 *            : this is to distinguish whether this method is called during
	 *            single case push request or multiple case push request by
	 *            gc-track
	 * @throws Exception
	 */
	public void update(GC_track_case gc_track_case,
			GC_Track_Service_Call_Response response, boolean isSingleCaseUpdate)
			throws Exception {
		updateEpdsBasedOnGC_track_case(gc_track_case, response,
				isSingleCaseUpdate, false);

	}

	/**
	 * It deletes all the gc-track event records in EPDS for a case
	 * 
	 * @param a_no
	 *            : case identifier
	 * @param response
	 *            : response data sent to gc-track
	 * @throws Exception
	 */
	public void clearCaseEvents(String a_no,
			GC_Track_Service_Call_Response response) throws Exception {

		Protest_Info protest_Info = protest_Info_DAO.getProtestByA_no(a_no);

		if (protest_Info == null) {
			response.setMessage("Protest Case for provided a_no: " + a_no
					+ " doesn't exist. Please try again with correct A#.");

			response.setRetry("N");
			throw new EntityNotFoundException();
		}


		List<GC_Track_Service_Event> list_of_GC_track_service_event = gc_Track_Service_DAO
				.get_list_of_gc_track_service_event_based_on_a_no(a_no);

		int errorCount = 0;
		String previousErrorMessage = "";
		String currentErrorMessage = "";
		if (list_of_GC_track_service_event != null) {
			for (GC_Track_Service_Event eachGC_Track_Service_Event : list_of_GC_track_service_event) {
				try {
					gc_Track_Service_DAO.delete(eachGC_Track_Service_Event);
				} catch (Exception e) {
					previousErrorMessage = response.getMessage();
					currentErrorMessage = "Event '"
							+ eachGC_Track_Service_Event.getEvent_description()
							+ "' with event id = "
							+ eachGC_Track_Service_Event.getEvent_Id()
							+ " couldn't be deleted.";
					if (errorCount == 0) {
						response.setMessage(currentErrorMessage);
					} else {
						response.setMessage(previousErrorMessage + "; "
								+ currentErrorMessage);
					}

					errorCount++;
				}
			}
		}

	}

	/**
	 * @param a_no
	 *            : case identifier
	 * @param response
	 *            : response data sent to gc-track
	 * @return: data needed by gc-track to file a new case
	 * @throws Exception
	 */
	public Gc_protest_info getGc_protest_info(String a_no,
			GC_Track_Service_Call_Response response) throws Exception {
		ProtestInfoService protestInfoService = (ProtestInfoService) SpringApplicationContext
				.getBean("protestInfoService");
		Agency_Info_DAO agency_info_dao = (Agency_Info_DAO) SpringApplicationContext
				.getBean("agency_Info_DAO");

		Protest_Info protest_Info = protestInfoService
				.getProtest_Info_ByA_No(a_no);

		if (protest_Info == null) {
			response.setMessage("Protest Case for provided a_no: " + a_no
					+ " doesn't exist. Please try again with correct A#.");

			response.setRetry("N");
			throw new EntityNotFoundException();
		}

		List<String> child_b_numbers = getChild_b_numbers(protestInfoService,protest_Info.getA_No());

		protest_Info.setSubmission_Date(protest_Info.getSubmission_Date());
		protest_Info.setAgency_Name(agency_info_dao.getAgencyName(protest_Info.getAgency_Info_Id()));
				protest_Info.setAgency_Name(agency_info_dao.getAgencyName(protest_Info.getAgency_Info_Id()));

		Gc_protest_info gc_protest_info = new Gc_protest_info();

		gc_protest_info.setA_no(protest_Info.getA_No());
		gc_protest_info.setAgency_name(protest_Info.getAgency_Name());
		gc_protest_info.setB_no(Util.stripBDashPrefix(protest_Info.getB_No()));
		gc_protest_info.setCase_status(protest_Info.getCase_Status());
		gc_protest_info.setCase_type(protest_Info.getCase_Type());
		gc_protest_info.setComments(protest_Info.getComments());
		gc_protest_info.setDue_date(Date_Util
				.getDateWithTimeSetToEndTimeOfTheDay(protest_Info.getDue_Date()));
		gc_protest_info.setFiled_date(Date_Util.convertToSpecifiedFormat(protest_Info.getSubmission_Date(),"MMM dd yyyy HH:mm:ss z", "MM/dd/yyyy HH:mm:ss z"));
		gc_protest_info.setParent_a_no(protest_Info.getParent_A_No());
		gc_protest_info.setProtester(protest_Info.getCompany_Name());
		gc_protest_info.setProtester_city(protest_Info.getCompany_City());
		gc_protest_info.setProtester_country(protest_Info.getCompany_Country());
		gc_protest_info.setProtester_state(protest_Info.getCompany_State());
		gc_protest_info.setProtester_status(protest_Info.getCompany_Status());
		gc_protest_info.setProtester_street(protest_Info.getCompany_address1() + System.lineSeparator() + protest_Info.getCompany_address2());
		gc_protest_info.setProtester_zipcode(protest_Info.getCompany_Zipcode());
		gc_protest_info.setSolicitation_no(protest_Info.getSolicitation_No());
		gc_protest_info.setAttorney_name(protest_Info.getAttorney_Name());
		gc_protest_info.setChild_b_numbers(child_b_numbers);


		populatePartiesAndOrgInfo(gc_protest_info,a_no);

		return gc_protest_info;
	}

	/**
	 * @param protestInfoService
	 *            : helper spring bean object for getting list of child protests
	 *            for a case
	 * @param a_no
	 *            : case identifier
	 * @return: list of child b-numbers to a case
	 */
	private List<String> getChild_b_numbers(
			ProtestInfoService protestInfoService, String a_no) {
		List<String> list_of_child_b_no = new ArrayList<String>();

		List<Protest_Info> list_of_childProtest_info = protestInfoService
				.getList_of_child_protest_info(a_no);

		String child_b_no;
		if (list_of_childProtest_info != null
				&& list_of_childProtest_info.size() > 0) {
			for (Protest_Info eachProtest_info : list_of_childProtest_info) {
				child_b_no = eachProtest_info.getB_No();

				if (child_b_no != null && !child_b_no.equalsIgnoreCase("")) {
					list_of_child_b_no.add(Util.stripBDashPrefix(child_b_no));
				}
			}

		}

		return list_of_child_b_no;
	}

	/**
	 * batch update of gc-track changes on multiple cases to EPDS is carried out
	 * 
	 * @param list_of_gc_track_case
	 *            : a list of gc-track cases
	 * @param response
	 *            : response data sent to gc-track
	 * @throws Exception
	 */
	public void updateEpdsBasedOnListOf_gc_track_case(
			List<GC_track_case> list_of_gc_track_case,
			GC_Track_Service_Call_Response response) throws Exception {
		
		BatchUpdateLogInfo batchUpdateLogInfo = new BatchUpdateLogInfo();
		String responseForAllCases = "";

		int numberOfProcessedCases = 0;
		int numberOfFailedUpdates = 0;
		int numberOfSuccessfulUpdates = 0;
		int numberOfUnchangedCases = 0;
		Map<String, String> aNoAndBNoToChangeMade = new HashMap<String, String>();
		Map<String, String> aNoAndBNoToException = new HashMap<String, String>();

		for (GC_track_case each_gc_track_case : list_of_gc_track_case) {
			numberOfProcessedCases++;
			try {
				GC_track_util.changeMade = "";

				updateEpdsBasedOnGC_track_case(each_gc_track_case, response,
						false, false);

				if (GC_track_util.changeMade != null
						&& !GC_track_util.changeMade.equalsIgnoreCase("")) {
					aNoAndBNoToChangeMade.put(getANoAndBNo(each_gc_track_case),
							GC_track_util.changeMade);
					numberOfSuccessfulUpdates++;
				} else {
					numberOfUnchangedCases++;
				}

				if (response.getMessage() != null
						&& !response.getMessage().equalsIgnoreCase("")) {
					responseForAllCases += "Response for case with a_no = "
							+ each_gc_track_case.getA_no() + " : "
							+ response.getMessage() + "; ";
				}

			} catch (Exception e) {
				aNoAndBNoToException.put(getANoAndBNo(each_gc_track_case),
					(response.getMessage() != null && !response.getMessage().equalsIgnoreCase("") )
						? response.getMessage() : e.getMessage());
				// once added, clear message to not pollute next iteration
				response.setMessage(null);
				numberOfFailedUpdates++;
			}
		}

		batchUpdateLogInfo.setaNoToChangeMade(aNoAndBNoToChangeMade);
		batchUpdateLogInfo.setaNoToException(aNoAndBNoToException);
		batchUpdateLogInfo.setNumberOfFailedUpdates(numberOfFailedUpdates);
		batchUpdateLogInfo.setNumberOfProcessedCases(numberOfProcessedCases);
		batchUpdateLogInfo
				.setNumberOfSuccessfulUpdates(numberOfSuccessfulUpdates);
		batchUpdateLogInfo.setNumberOfUnchangedCases(numberOfUnchangedCases);

		response.setData(batchUpdateLogInfo);

		if (numberOfFailedUpdates > 0){
			response.setRetry("N");
		}
		response.setMessage(responseForAllCases);
	}

	private String getANoAndBNo(GC_track_case each_gc_track_case) {
		String aNo = each_gc_track_case.getA_no();
		String bNo = each_gc_track_case.getB_no();

		String returnValue;
		if (bNo == null) {
			returnValue = aNo;
		} else {
			returnValue = aNo + "/" + bNo;
		}

		return returnValue;
	}

	/**
	 * @param jsonArray_of_multipleCaseData
	 *            : list of cases sent by gc-track
	 * @return: list of gc_track_case objects (conversion of
	 *          jsonArray_of_multipleCaseData)
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws org.json.simple.parser.ParseException
	 */
	public List<GC_track_case> convertTo_list_of_gc_track_case(
			JSONArray jsonArray_of_multipleCaseData)
			throws JsonGenerationException, JsonMappingException, IOException,
			org.json.simple.parser.ParseException {
		List<GC_track_case> list_of_gc_track_case = new ArrayList<GC_track_case>();
		GC_track_case gc_track_case;
		JSONObject jsonObject;
		Gson gson = new Gson();

		ObjectMapper objectMappper = new ObjectMapper();
		String jsonRepresentation;
		for (int i = 0; i < jsonArray_of_multipleCaseData.size(); i++) {
			System.out.println(jsonArray_of_multipleCaseData.get(i));
			jsonRepresentation = objectMappper
					.writeValueAsString(jsonArray_of_multipleCaseData.get(i));

			jsonObject = (JSONObject) (new JSONParser())
					.parse(jsonRepresentation);
			gc_track_case = gson.fromJson(jsonObject.toJSONString(),
					GC_track_case.class);
			gc_track_case.setB_no(Util.getBNumberWithBDashPrefix(gc_track_case
					.getB_no()));

			list_of_gc_track_case.add(gc_track_case);
		}

		return list_of_gc_track_case;
	}

	/**
	 * removes gc-test-case as identifed by identifier count
	 * 
	 * @param count
	 *            : identifier of a gc-test-case
	 * @throws Exception
	 */
	public void removeGcTestData(int count) throws Exception {
		String a_no = null;
		if (count == 0) {
			a_no = "A-GC111";
		} else if (count == 1) {
			a_no = "A-GC222";
		} else if (count == 2) {
			a_no = "A-GC333";
		} else if (count == 3) {
			a_no = "A-GC444";
		} else if (count == 4) {
			a_no = "A-GC555";
		}

		// remove user_protest_role_bridge
		protest_Info_DAO.removeAll_user_protest_role_bridge_recordsByA_no(a_no);

		// remove protest_info
		Protest_Info protest_info = protest_Info_DAO.getProtestByA_no(a_no);
		protest_Info_DAO.delete(protest_info);

		// remove file_info
		file_info_DAO.removeAll_file_info_records(a_no);

		// remove gc events
		gc_Track_Service_DAO.deleteEventsByA_no(a_no);

	}
}
