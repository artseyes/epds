package gov.gao.epds.utils;

import gov.gao.epds.dao.File_Info_DAO;
import gov.gao.epds.dao.GC_Track_Service_DAO;
import gov.gao.epds.dao.Protest_Info_DAO;
import gov.gao.epds.dao.User_Info_DAO;
import gov.gao.epds.gctrack.GC_Track_Service_Call_Response;
import gov.gao.epds.gctrack.GC_track_case;
import gov.gao.epds.persistence.entity.GC_Track_Service_Event;
import gov.gao.epds.persistence.entity.Protest_Info;
import gov.gao.epds.persistence.entity.User_Info;
import gov.gao.epds.persistence.entity.User_Protest_Role_Bridge;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;


/**
 * This is helper class for GCTrackService to synch EPDS case with gc-track case
 * when gc-track case is pushed to EPDS
 * 
 * @author RAdhikari
 * 
 */
public class GC_track_util {
	public static String changeMade = "";

	/**
	 * @param gc_track_case
	 *            : case data sent by gc-track
	 * @param user_info_DAO
	 *            : data-access-object for user_info table (mainly)
	 * @param protest_Info_DAO
	 *            : data-access-object for protest_info table (mainly)
	 * @param file_info_DAO
	 *            : data-access-object for file_info table (mainly)
	 * @param gc_Track_Service_DAO
	 *            : data-access-object for gc_track_service_event table (mainly)
	 * @param response
	 *            : response data sent to gc-track
	 * @param isItCaseCreatedNotification
	 * @return: EPDS case after it is synched with gc-track
	 * @throws Exception
	 */
//	@Transactional
	public static Protest_Info getUpdatedProtest_info(
			GC_track_case gc_track_case, User_Info_DAO user_info_DAO,
			Protest_Info_DAO protest_Info_DAO, File_Info_DAO file_info_DAO,
			GC_Track_Service_DAO gc_Track_Service_DAO,
			GC_Track_Service_Call_Response response,
			boolean isItCaseCreatedNotification) throws Exception {

		Protest_Info protest_info = protest_Info_DAO
				.getProtestByA_no(gc_track_case.getA_no());

		if (protest_info == null) {
			response.setMessage("Protest Case for provided a_no: " + gc_track_case.getA_no()
					+ " doesn't exist. Please try again with different A#.");

			response.setRetry("N");
			throw new EntityNotFoundException();
		}

		if (protest_info != null) {
			if (gc_track_case.getAttorney_id() != 0) {
				updateCaseAttorney(gc_track_case.getAttorney_id(),
						gc_track_case.getA_no(), user_info_DAO, response,
						protest_info);

			}

			if (gc_track_case.getB_no() != null
					&& !gc_track_case.getB_no().equalsIgnoreCase(
							protest_info.getB_No())) {
				protest_info.setB_No(gc_track_case.getB_no());
				
				
				
				List<GC_Track_Service_Event> gcTrackEvents = gc_Track_Service_DAO.getListOf_gc_track_service_event_byA_no(protest_info.getA_No());
				

				if (gcTrackEvents != null  && gcTrackEvents.size() > 0) {
					for (GC_Track_Service_Event gc_Track_Service_Event : gcTrackEvents) {
						gc_Track_Service_Event.setB_No(gc_track_case.getB_no());
						gc_Track_Service_DAO.update(gc_Track_Service_Event);

					}
				}
				/*gc_Track_Service_DAO.changeBNumber(gc_track_case.getB_no(),
						protest_info.getB_No());*/
				changeMade = Util.addSemicolonSeparatedText(changeMade,
						"B-number changed");
			}

			if (!isItCaseCreatedNotification) {
				if (gc_track_case.getParent_b_no() == null
						|| gc_track_case.getParent_b_no().equalsIgnoreCase("")) {
					changeCase_type(protest_info, gc_track_case);

					handleCaseTypeDoesntExist(gc_track_case, response, protest_info);
				}
			} else if (!protest_info.getCase_Type().toUpperCase(Locale.ENGLISH).equals("SUPPLEMENTAL")){
				changeCase_type(protest_info, gc_track_case);

				handleCaseTypeDoesntExist(gc_track_case, response, protest_info);
			}

			// changeCase_status(protest_info, gc_track_case, changeMade);
			
			if (!protest_info.getCase_Type().equalsIgnoreCase("SUPPLEMENTAL")){
				
				joinOrUnjoinCasesBasedOnListOfChildBNumbers(protest_info,
						gc_track_case, protest_Info_DAO, file_info_DAO,
						user_info_DAO, gc_Track_Service_DAO, changeMade, response);
			}
			

			if (gc_track_case.getDue_date() != null
					&& !gc_track_case.getDue_date().equalsIgnoreCase(
							protest_info.getDue_Date())) {
				protest_info.setDue_Date(gc_track_case.getDue_date());
				changeMade = Util.addSemicolonSeparatedText(changeMade,
						"Due date changed");
			}
		} else {
			response.setMessage("Case not found");
		}

		return protest_info;
	}

	/**
	 * @param gc_track_case
	 * @param response
	 * @param protest_info
	 */
	public static void handleCaseTypeDoesntExist(GC_track_case gc_track_case, GC_Track_Service_Call_Response response,
			Protest_Info protest_info) {
		if (protest_info.getCase_Type() == null || "".equalsIgnoreCase(protest_info.getCase_Type())){
			response.setMessage("The case type " + gc_track_case.getCase_type() + " doesn't exist. Please correct the case type and try again. ");
			response.setRetry("N");
			throw new EntityNotFoundException();
		}
	}

	/**
	 * @param attorney_id
	 *            : gao user identifier
	 * @param a_no
	 *            : EPDS/gc-track case identifier
	 * @param user_info_DAO
	 *            : data-access-object for user_info table
	 * @param response
	 *            : response data sent to gc-track
	 * @param protest_info
	 *            : EPDS case
	 * @throws Exception
	 */
	synchronized public static void updateCaseAttorney(Long attorney_id, String a_no,
			User_Info_DAO user_info_DAO,
			GC_Track_Service_Call_Response response, Protest_Info protest_info)
			throws Exception {
		User_Info attorney_info = user_info_DAO.getAttorney_info(attorney_id);

		if (attorney_info == null) {
			response.setMessage("Attorney (" + attorney_id + ") not found in EDS");
//			response.setMessage("EPDS currently doesn't have record of an attorney that you are trying to add. Please add this attorney in EPDS and try again.");
			response.setRetry("N");
			throw new EntityNotFoundException();
		} else {
			User_Protest_Role_Bridge attorney_User_Protest_Role_Bridge = user_info_DAO
					.getAttorneyUser_Protest_Role_Bridge(a_no);

			if (attorney_User_Protest_Role_Bridge == null) {
				user_info_DAO.add_User_Protest_Role_Bridge_Entity(protest_info,
						attorney_info.getUser_Id(), 3);

				changeMade = Util.addSemicolonSeparatedText(changeMade,
						"Attorney assigned");
			} else if (!attorney_User_Protest_Role_Bridge.getUser_Id()
					.equalsIgnoreCase(attorney_info.getUser_Id())) {
				attorney_User_Protest_Role_Bridge.setUser_Id(attorney_info
						.getUser_Id());
				user_info_DAO.update(attorney_User_Protest_Role_Bridge);

				changeMade = Util.addSemicolonSeparatedText(changeMade,
						"Attorney changed");
			}

			String attorney_name = attorney_info.getLast_Name() + ", "
					+ attorney_info.getFirst_Name();
			protest_info.setAttorney_Name(attorney_name);
			protest_info.setAttorney_Group_Id(attorney_info.getGroup_No());
		}
	}

	/**
	 * join or unjoin cases based on child_b_numbers attribute in gc-track-case
	 * 
	 * @param protest_info
	 *            : EPDS case
	 * @param gc_track_case
	 *            : data shared by gc-track case and EPDS case
	 * @param protest_Info_DAO
	 *            :
	 * @param file_info_DAO
	 * @param user_info_DAO
	 * @param gc_Track_Service_DAO 
	 * @param changeMade
	 *            :
	 * @param response
	 * @throws Exception
	 */
//	@Transactional
	private static void joinOrUnjoinCasesBasedOnListOfChildBNumbers(
			Protest_Info protest_info, GC_track_case gc_track_case,
			Protest_Info_DAO protest_Info_DAO, File_Info_DAO file_info_DAO,
			User_Info_DAO user_info_DAO, GC_Track_Service_DAO gc_Track_Service_DAO, String changeMade,
			GC_Track_Service_Call_Response response) throws Exception {

		try {
			if (gc_track_case.getChild_b_numbers() != null) {
				addBDashPrefixToChildBNumbersIfRequired(gc_track_case);

				/*
				 * String[] gc_child_b_numbers_inArray = new
				 * String[gc_track_case .getChild_b_numbers().size()];
				 * gc_child_b_numbers_inArray =
				 * gc_track_case.getChild_b_numbers()
				 * .toArray(gc_child_b_numbers_inArray);
				 */

				List<Protest_Info> gc_list_of_child_protest_info = protest_Info_DAO
						.getProtest_Info_List_BasedOnB_NoList(gc_track_case
								.getChild_b_numbers());

				List<Protest_Info> epds_list_of_child_protest_info = protest_Info_DAO
						.get_list_of_child_protest_info(gc_track_case.getA_no());

				// separate out the cases to be joined and the cases to be
				// unjoined
				for (int i = 0; i < gc_list_of_child_protest_info.size(); i++) {
					Protest_Info each_gc_child_protest_info = gc_list_of_child_protest_info
							.get(i);
					for (int j = 0; j < epds_list_of_child_protest_info.size(); j++) {
						Protest_Info each_epds_child_protest_info = epds_list_of_child_protest_info
								.get(j);
						if (each_epds_child_protest_info.getA_No()
								.equalsIgnoreCase(
										each_gc_child_protest_info.getA_No())) {
							epds_list_of_child_protest_info.remove(j);
							j--;
							gc_list_of_child_protest_info.remove(i);
							i--;
							break;
						}

					}
				}

				List<Protest_Info> listOfToBeJoined_child_protest_info = gc_list_of_child_protest_info;
				List<Protest_Info> listOfToBeUnJoined_child_protest_info = epds_list_of_child_protest_info;
				
				removeCasesThatAreNotCreatedInGcTrackFromTheList(listOfToBeUnJoined_child_protest_info, gc_Track_Service_DAO);

				Protest_info_util.joinCases(protest_Info_DAO, user_info_DAO,
						listOfToBeJoined_child_protest_info, protest_info,file_info_DAO, true);
				Protest_info_util.unJoinCases(protest_Info_DAO, user_info_DAO,
						file_info_DAO, listOfToBeUnJoined_child_protest_info,
						protest_info, listOfToBeJoined_child_protest_info, true);

				if (listOfToBeJoined_child_protest_info.size() > 0) {
					changeMade = Util.addSemicolonSeparatedText(changeMade,
							"Case(s) joined");
				}
				if (listOfToBeUnJoined_child_protest_info.size() > 0) {
					changeMade = Util.addSemicolonSeparatedText(changeMade,
							"Case(s) unjoined");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error while joining/unjoining cases");

			throw e;
		}
	}

	private static void removeCasesThatAreNotCreatedInGcTrackFromTheList(
			List<Protest_Info> listOfToBeUnJoined_child_protest_info, GC_Track_Service_DAO gc_Track_Service_DAO) {
		
		Protest_Info eachCaseToBeUnjoined;
		
		for(int i=0; i < listOfToBeUnJoined_child_protest_info.size(); i++){
			eachCaseToBeUnjoined = listOfToBeUnJoined_child_protest_info.get(i);
			
			if(!isThisCaseCreatedInGcTrack(eachCaseToBeUnjoined, gc_Track_Service_DAO)){
				listOfToBeUnJoined_child_protest_info.remove(i--);
			}
		}
		
	}

	private static boolean isThisCaseCreatedInGcTrack(Protest_Info eachCaseToBeUnjoined, GC_Track_Service_DAO gc_Track_Service_DAO) {
		GC_Track_Service_Event gcEvent = gc_Track_Service_DAO.get_GC_Track_Service_Event_For_New_Case(eachCaseToBeUnjoined.getA_No());
		
		if(gcEvent != null) {
			return false;
		}else{
			return true;
		}
		
	}

	/**
	 * B-number for EPDS will have 'B-' prefix but gc-track doesn't have
	 * 
	 * @param gcTrackCase
	 *            : data shared between gc-track and EPDS
	 */
	private static void addBDashPrefixToChildBNumbersIfRequired(
			GC_track_case gcTrackCase) {
		List<String> newChildBnumbers = new ArrayList<String>();

		for (String eachBNumber : gcTrackCase.getChild_b_numbers()) {
			eachBNumber = Util.getBNumberWithBDashPrefix(eachBNumber);
			newChildBnumbers.add(eachBNumber);
		}

		gcTrackCase.setChild_b_numbers(newChildBnumbers);
	}

	/*private static void changeCase_status(Protest_Info protest_info,
			GC_track_case gc_track_case, String changeMade) {
		String case_status = getCase_status(gc_track_case.getCase_status());
		if (case_status != null
				&& !case_status.equals(protest_info.getCase_Status())) {
			protest_info.setCase_Status(case_status);
			changeMade = Util.addSemicolonSeparatedText(changeMade,
					"Case status changed");
		}

	}*/

	private static void changeCase_type(Protest_Info protest_info, GC_track_case gc_track_case) {
		String case_type = getCase_type(gc_track_case.getCase_type());
		if (!case_type.equals(protest_info.getCase_Type())) {
			protest_info.setCase_Type(case_type);
			changeMade = Util.addSemicolonSeparatedText(changeMade,
					"Case type changed");
		}

	}


	private static String getCase_type(Long case_type) {
		String case_type_desc = "";

		if (case_type == 2020) {
			case_type_desc = "COST-CLAIM";
		} else if (case_type == 2030) {
			case_type_desc = "ENTITLEMENT";
		} else if (case_type == 2040) {
			case_type_desc = "RECONSIDERATION";
		} else if (case_type == 2050) {
			case_type_desc = "AGENCY";
		} else {
			// default case type to make sure we don't accidentally miss a new gctrack 'other' case type
			// currently expected: 2010, 2110, and 2410
			case_type_desc = "PROTEST";
		}

		return case_type_desc;
	}

}
