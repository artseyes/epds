package gov.gao.epds.gctrack;

import gov.gao.epds.dao.Agency_Info_DAO;
import gov.gao.epds.dao.User_Info_DAO;
import gov.gao.epds.dto.ProtestInfoFormDTO;
import gov.gao.epds.persistence.entity.Protest_Info;
import gov.gao.epds.persistence.entity.User_Info;
import gov.gao.epds.service.GC_Service;
import gov.gao.epds.service.ProtestInfoService;
import gov.gao.epds.service.UserInfoService;
import gov.gao.epds.utils.Date_Util;
import gov.gao.epds.utils.GC_track_util;
import gov.gao.epds.utils.SpringApplicationContext;
import gov.gao.epds.utils.Util;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class GC_test_dataHandler {

	/*
	 * public static void main(String[] args) { addGC_webservice_data();
	 * removeGC_webservice_data(); }
	 */

/*	@RequestMapping(value = "/remove-gc-data/{numberOfCases}", method = RequestMethod.GET, produces = "application/json")
	private static void removeGC_webservice_data(
			@PathVariable("numberOfCases") String numberOfCases,
			HttpServletRequest request) throws Exception {
		System.out.println("Request Servlet Path: " + request.getServletPath());
		System.out
				.println("Real Path: "
						+ request
								.getServletContext()
								.getRealPath(
										"/resources/GAO_Templates/css/ACKNOWLEDGMENTLETTERSWITHOUTPROTECTIVEORDER.css"));

		System.out.println("testing");

		
		 * GC_Service gc_Service = (GC_Service) SpringApplicationContext
		 * .getBean("GC_Service"); int numOfCases =
		 * Integer.valueOf(numberOfCases);
		 * 
		 * for (int i = 0; i < numOfCases; i++) {
		 * gc_Service.removeGcTestData(i); }
		 
	}

	@RequestMapping(value = "/add-gc-data/{numberOfCases}", method = RequestMethod.GET, produces = "application/json")
	public void addGC_webservice_data(
			@PathVariable("numberOfCases") String numberOfCases)
			throws Exception {
		GC_Service gc_Service = (GC_Service) SpringApplicationContext
				.getBean("GC_Service");
		int numOfCases = Integer.valueOf(numberOfCases);

		List<Protest_Info> list_of_protest_info = fillupProtest_info_tables(
				gc_Service, numOfCases);  events triggered: new protests 

		fillupFile_info_tables(list_of_protest_info, gc_Service); 
																 * events
																 * triggered:
																 * agency report
																 * filed; agency
																 * report
																 * comments
																 * filed; notice
																 * of appearance
																 * filed
																 
		fillupUser_protest_role_tables(list_of_protest_info, gc_Service); 
																		 * events
																		 * triggered
																		 * :
																		 * INTERVENOR
																		 * assigned
																		 * ;
																		 * agency
																		 * representatives
																		 * assigned
																		 

	}

	private static List<Protest_Info> fillupProtest_info_tables(
			GC_Service gc_Service, int numOfCases) throws Exception {
		ProtestInfoService protestInfoService = (ProtestInfoService) SpringApplicationContext
				.getBean("protestInfoService");
		Agency_Info_DAO agency_info_dao = (Agency_Info_DAO) SpringApplicationContext
				.getBean("agency_Info_DAO");
		User_Info_DAO user_info_dao = (User_Info_DAO) SpringApplicationContext
				.getBean("user_Info_DAO");

		ProtestInfoFormDTO protestInfoFormDto = new ProtestInfoFormDTO();
		Protest_Info protest_Info;
		List<Protest_Info> list_of_protest_info = new ArrayList<Protest_Info>();

		for (int i = 0; i < numOfCases; i++) {
			fillupProtestInfoFormDto(i, protestInfoFormDto);
			protest_Info = protestInfoService.registerProtestInfo(
					protestInfoFormDto, null, "", "","",null);//come back and update params

			if (i == 0 || i == 1) {
				gc_Service.set_Event_For_New_Protest_Info(protest_Info);
			} else {
				GC_track_util.updateCaseAttorney(
						protestInfoFormDto.getAttorney_id(),
						protest_Info.getA_No(), user_info_dao,
						new GC_Track_Service_Call_Response(), protest_Info);
			}

			protest_Info.setAgency_Name(Util.getAgencyName(
					protest_Info.getAgency_Info_Id(), agency_info_dao));

			list_of_protest_info.add(protest_Info);
		}

		return list_of_protest_info;
	}

	private static void fillupProtestInfoFormDto(int i,
			ProtestInfoFormDTO protestInfoFormDto) {
		if (i == 0) {
			protestInfoFormDto.setSolicitationNumber("S-GC111");
			protestInfoFormDto.setA_No("A-GC111");
			protestInfoFormDto.setAgency_tier_1("4");
			protestInfoFormDto.setAgency_tier_2("1");
			// protestInfoFormDto.setB_no("B-GC1111");
			protestInfoFormDto.setCompany_city("Fort Collins");
			protestInfoFormDto.setCompany_country("United States");
			protestInfoFormDto.setCompany_name("AEROSOL MONITORING & ANALYSIS");
			protestInfoFormDto.setCompany_state("Colorado");
			protestInfoFormDto.setCompany_status("large");
			protestInfoFormDto.setCompany_street("300 RIDGE STREET");
			protestInfoFormDto.setCompany_zipcode("80526");
			protestInfoFormDto.setCity("Fort Collins");
			protestInfoFormDto.setCountry("USA");
			protestInfoFormDto.setEmail("rAdhikari@cbca.gov");
			protestInfoFormDto.setFaxnumber("(985)-956-1385");
			protestInfoFormDto.setFirstname("Rosh");
			protestInfoFormDto.setLastname("Adhikari");
			protestInfoFormDto.setPhonenumber("(985)-956-1385");
			protestInfoFormDto.setState("CO");
			protestInfoFormDto.setStreet("200 Street");
			protestInfoFormDto.setZipcode("80526");
			protestInfoFormDto.setComments("");
			// protestInfoFormDto.setAttorney_id(new Long(1917));
		} else if (i == 1) {
			protestInfoFormDto.setSolicitationNumber("S-GC222");
			protestInfoFormDto.setA_No("A-GC222");
			protestInfoFormDto.setAgency_tier_1("4");
			protestInfoFormDto.setAgency_tier_2("2");
			// protestInfoFormDto.setB_no("B-GC222");
			protestInfoFormDto.setCompany_city("Fort Collins");
			protestInfoFormDto.setCompany_country("United States");
			protestInfoFormDto.setCompany_name("APEXX, INC.");
			protestInfoFormDto.setCompany_state("Colorado");
			protestInfoFormDto.setCompany_status("large");
			protestInfoFormDto.setCompany_street("300 RIDGE STREET");
			protestInfoFormDto.setCompany_zipcode("80526");
			protestInfoFormDto.setCity("Fort Collins");
			protestInfoFormDto.setCountry("USA");
			protestInfoFormDto.setEmail("rAdhikari@cbca.gov");
			protestInfoFormDto.setFaxnumber("(985)-956-1385");
			protestInfoFormDto.setFirstname("Rosh");
			protestInfoFormDto.setLastname("Adhikari");
			protestInfoFormDto.setPhonenumber("(985)-956-1385");
			protestInfoFormDto.setState("CO");
			protestInfoFormDto.setStreet("200 Street");
			protestInfoFormDto.setZipcode("80526");
			protestInfoFormDto.setComments("");
			// protestInfoFormDto.setAttorney_id(new Long(2067));
		} else if (i == 2) {
			protestInfoFormDto.setSolicitationNumber("S-GC333");
			protestInfoFormDto.setA_No("A-GC333");
			protestInfoFormDto.setAgency_tier_1("4");
			protestInfoFormDto.setAgency_tier_2("2");
			protestInfoFormDto.setB_no("B-GC333");
			protestInfoFormDto.setCompany_city("Fort Collins");
			protestInfoFormDto.setCompany_country("United States");
			protestInfoFormDto.setCompany_name("PETROGEN INC");
			protestInfoFormDto.setCompany_state("Colorado");
			protestInfoFormDto.setCompany_status("large");
			protestInfoFormDto.setCompany_street("300 RIDGE STREET");
			protestInfoFormDto.setCompany_zipcode("80526");
			protestInfoFormDto.setCity("Fort Collins");
			protestInfoFormDto.setCountry("USA");
			protestInfoFormDto.setEmail("rAdhikari@cbca.gov");
			protestInfoFormDto.setFaxnumber("(985)-956-1385");
			protestInfoFormDto.setFirstname("Rosh");
			protestInfoFormDto.setLastname("Adhikari");
			protestInfoFormDto.setPhonenumber("(985)-956-1385");
			protestInfoFormDto.setState("CO");
			protestInfoFormDto.setStreet("200 Street");
			protestInfoFormDto.setZipcode("80526");
			protestInfoFormDto.setComments("");
			protestInfoFormDto.setAttorney_id(new Long(2209));
		} else if (i == 3) {
			protestInfoFormDto.setSolicitationNumber("S-GC444");
			protestInfoFormDto.setA_No("A-GC444");
			protestInfoFormDto.setAgency_tier_1("4");
			protestInfoFormDto.setAgency_tier_2("3");
			protestInfoFormDto.setB_no("B-GC444");
			protestInfoFormDto.setCompany_city("Fort Collins");
			protestInfoFormDto.setCompany_country("United States");
			protestInfoFormDto.setCompany_name("DELEX SYSTEMS");
			protestInfoFormDto.setCompany_state("Colorado");
			protestInfoFormDto.setCompany_status("large");
			protestInfoFormDto.setCompany_street("300 RIDGE STREET");
			protestInfoFormDto.setCompany_zipcode("80526");
			protestInfoFormDto.setCity("Fort Collins");
			protestInfoFormDto.setCountry("USA");
			protestInfoFormDto.setEmail("rAdhikari@cbca.gov");
			protestInfoFormDto.setFaxnumber("(985)-956-1385");
			protestInfoFormDto.setFirstname("Rosh");
			protestInfoFormDto.setLastname("Adhikari");
			protestInfoFormDto.setPhonenumber("(985)-956-1385");
			protestInfoFormDto.setState("CO");
			protestInfoFormDto.setStreet("200 Street");
			protestInfoFormDto.setZipcode("80526");
			protestInfoFormDto.setComments("");
			protestInfoFormDto.setAttorney_id(new Long(2329));
		} else if (i == 4) {
			protestInfoFormDto.setSolicitationNumber("S-GC555");
			protestInfoFormDto.setA_No("A-GC555");
			protestInfoFormDto.setAgency_tier_1("4");
			protestInfoFormDto.setAgency_tier_2("4");
			protestInfoFormDto.setB_no("B-GC555");
			protestInfoFormDto.setCompany_city("Fort Collins");
			protestInfoFormDto.setCompany_country("United States");
			protestInfoFormDto.setCompany_name("PETROGEN INC.2");
			protestInfoFormDto.setCompany_state("Colorado");
			protestInfoFormDto.setCompany_status("large");
			protestInfoFormDto.setCompany_street("300 RIDGE STREET");
			protestInfoFormDto.setCompany_zipcode("80526");
			protestInfoFormDto.setCity("Fort Collins");
			protestInfoFormDto.setCountry("USA");
			protestInfoFormDto.setEmail("rAdhikari@cbca.gov");
			protestInfoFormDto.setFaxnumber("(985)-956-1385");
			protestInfoFormDto.setFirstname("Rosh");
			protestInfoFormDto.setLastname("Adhikari");
			protestInfoFormDto.setPhonenumber("(985)-956-1385");
			protestInfoFormDto.setState("CO");
			protestInfoFormDto.setStreet("200 Street");
			protestInfoFormDto.setZipcode("80526");
			protestInfoFormDto.setComments("");
			protestInfoFormDto.setAttorney_id(new Long(2667));
		}
	}

	// agency representative assigned;
	private static void fillupUser_protest_role_tables(
			List<Protest_Info> list_of_protest_info, GC_Service gc_Service)
			throws Exception {
		UserInfoService userInfoService = (UserInfoService) SpringApplicationContext
				.getBean("userInfoService");
		User_Info attorney_info;

		for (int i = 2; i < list_of_protest_info.size(); i++) {
			
			 * if (i == 0) { attorney_info =
			 * userInfoService.getUserInfoByUsername("jScott");
			 * userInfoService.assignRole("A-GC111", "jScott",
			 * "agency-attorney");
			 * gc_Service.set_Agency_Representative_Assigned_Notice(
			 * list_of_protest_info.get(i), attorney_info); } else if (i == 1) {
			 * attorney_info = userInfoService
			 * .getUserInfoByUsername("gthomas");
			 * userInfoService.assignRole("A-GC111", "gthomas",
			 * "agency-attorney");
			 * gc_Service.set_Agency_Representative_Assigned_Notice(
			 * list_of_protest_info.get(i), attorney_info); } else
			 

			if (i == 2) {
				attorney_info = userInfoService.getUserInfoByUsername("aBurke");
				userInfoService.assignRole("A-GC111", attorney_info,
						"agency-attorney", null, null);
				gc_Service.set_Agency_Representative_Assigned_Notice(
						list_of_protest_info.get(i), attorney_info);

			} else if (i == 3) {
				attorney_info = userInfoService.getUserInfoByUsername("uBolt");
				userInfoService.assignRole("A-GC111", attorney_info,
						"agency-attorney", null, null);
				gc_Service.set_Agency_Representative_Assigned_Notice(
						list_of_protest_info.get(i), attorney_info);
			} else if (i == 4) {
				attorney_info = userInfoService.getUserInfoByUsername("bScott");
				userInfoService.assignRole("A-GC111",attorney_info,
						"agency-attorney", null, null);
				gc_Service.set_Agency_Representative_Assigned_Notice(
						list_of_protest_info.get(i), attorney_info);
			}

		}

	}

	private static void fillupFile_info_tables(
			List<Protest_Info> list_of_protest_info, GC_Service gc_Service)
			throws Exception {
		ProtestInfoService protestInfoService = (ProtestInfoService) SpringApplicationContext
				.getBean("protestInfoService");
		UserInfoService userInfoService = (UserInfoService) SpringApplicationContext
				.getBean("userInfoService");

		List<String> listOfIntervenerCompany = getListOfCompany();

		// List<Integer> listOfProtestFileId;
		List<Integer> listOfIntervenorFileId;
		// List<Integer> listOfAgencyReportFileId;
		// List<Integer> listOfReportCommentFileId;
		// List<Integer> listOfNOAFileId;

		for (int i = 0; i < list_of_protest_info.size(); i++) {
			// adding protest file
			protestInfoService.saveFileToDB_for_gc_test("Protest",
					list_of_protest_info.get(i).getA_No(), "radhikari",
					list_of_protest_info.get(i).getCompany_Name(),
					list_of_protest_info.get(i).getCompany_Street());

			if (i == 0 || i == 1)
				continue;

			// adding INTERVENOR file
			listOfIntervenorFileId = protestInfoService
					.saveFileToDB_for_gc_test("intervene", list_of_protest_info
							.get(i).getA_No(), "radhikari",
							listOfIntervenerCompany.get(i),
							listOfIntervenerCompany.get(i) + "'s address");
			userInfoService.updateApprovedStatus(listOfIntervenorFileId.get(0)
					+ "", "Y", "intervene");
			gc_Service
					.set_Intervener_Approved_Event(list_of_protest_info.get(i),
							listOfIntervenerCompany.get(i));

			// adding agency report file
			protestInfoService.saveFileToDB_for_gc_test("agency report",
					list_of_protest_info.get(i).getA_No(), "agency",
					list_of_protest_info.get(i).getCompany_Name(),
					list_of_protest_info.get(i).getCompany_Street());
			gc_Service
					.set_Event_For_Agency_Report_Filed_For_First_Time(list_of_protest_info
							.get(i));

			// adding agency report comment file
			protestInfoService.saveFileToDB_for_gc_test(
					"agency report comment", list_of_protest_info.get(i)
							.getA_No(), "radhikari", list_of_protest_info
							.get(i).getCompany_Name(), list_of_protest_info
							.get(i).getCompany_Street());
			gc_Service
					.set_Event_For_Agency_Report_Comments_Filed_For_First_Time(list_of_protest_info
							.get(i));

			gc_Service.set_Event_For_Protest_Filed_Date_Updated(
					list_of_protest_info.get(i), Date_Util
							.getCurrentDatePlusOrMinusSpecifiedInDays(-4,
									"MM/dd/yyyy HH:mm:ss z"));

			
			 * // adding notice of appearance file
			 * protestInfoService.saveFileToDB_for_gc_test
			 * ("notice of appearance", list_of_protest_info.get(i).getA_No(),
			 * "GAO", "GAO", "GAO Address"); gc_Service
			 * .set_event_for_notice_of_appearance_filed_for_first_time
			 * (list_of_protest_info .get(i));
			 
		}

	}

	private static List<String> getListOfCompany() {
		List<String> listOfCompany = new ArrayList<String>();
		listOfCompany.add("AirScan");
		listOfCompany.add("Academi");
		listOfCompany.add("DynCorp");
		listOfCompany.add("MPRI, Inc.");
		listOfCompany.add("Raytheon");

		return listOfCompany;
	}*/

}
