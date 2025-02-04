package gov.gao.edps.webservice;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import gov.gao.epds.gctrack.Attorney;
import gov.gao.epds.gctrack.GC_track_case;
import gov.gao.epds.gctrack.Gc_Track_Update2;

public class GCTrackServicesTest {

	public static final String CIPHER_ALGORITHM = "AES";
	public static final String KEY_ALGORITHM = "AES";
	private static final String gctracAuthPassword =  "CBCA_app_pwd123";
	private static final String gctracDecryptPassword = "CBCA_app_Decrypt";

	public static final String PASSWORD_HASH_ALGORITHM = "SHA-256";
	public static final String baseUrl = "http://localhost:8080/epds/rest/gc-track-services";

	public static void main(String[] args) throws Exception {
		test_get_epdsUpdate();
//		test_get_protest_info("A-TCK92.3");
//		test_get_list_of_epds_event("A-3ZTT2");
//		test_delete_event();
//		test_clear_case_events();
//		test_notify_new_case_created();
//		test_get_new_case();
//		test_push_case_data();
//		test_get_new_case();
//		test_push_multiple_case_data();
//		test_get_new_case();

		// testWebServiceCallWithAuthentication();
		// testSimpleWebServiceCall();
		// testWebServiceCallWithDataPush();

		/*System.out.println(Protest_info_util.getNonDecimalPart("12kld"));*/

	}

	private static void test_push_multiple_case_data() throws Exception {
		ResteasyClient client = new ResteasyClientBuilder().build();

		ResteasyWebTarget target = client
				.target(baseUrl + "/push-multiple-case-data/");

		String encryptedPassword = encrypt(gctracAuthPassword);

		JSONArray gc_track_casesInJsonArray = getListOfGc_track_caseInJsonArray();

		Response response = target
				.request()
				.header("Authorization", encryptedPassword)
				.post(Entity.entity(gc_track_casesInJsonArray,
						"application/json"));

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus());
		}

		System.out.println(response.readEntity(String.class));
	}

	@SuppressWarnings("unchecked")
	private static JSONArray getListOfGc_track_caseInJsonArray()
			throws JsonGenerationException, JsonMappingException, IOException,
			ParseException {
		List<GC_track_case> list_of_gc_track_case = new ArrayList<GC_track_case>();
		GC_track_case gc_track_case;
		List<String> child_b_numbers;

		gc_track_case = new GC_track_case();
		gc_track_case.setA_no("A-3ZTT2");
		gc_track_case.setAttorney_id(new Long(1234));
		gc_track_case.setB_no("B-899984.5");
		gc_track_case.setCase_status(new Long(1));
		gc_track_case.setCase_type(new Long(2010));

		child_b_numbers = new ArrayList<String>();
//		gc_track_case.setChild_b_numbers(child_b_numbers);

		list_of_gc_track_case.add(gc_track_case);

//		gc_track_case = new GC_track_case();
//		gc_track_case.setA_no("A-PULYJ");
//		gc_track_case.setAttorney_id(new Long(1110));
//		gc_track_case.setB_no("B-PULYJ");
//		gc_track_case.setCase_status(new Long(1));
//		gc_track_case.setCase_type(new Long(2010));
//
//		child_b_numbers = new ArrayList<String>();
//		gc_track_case.setChild_b_numbers(child_b_numbers);
//
//		list_of_gc_track_case.add(gc_track_case);

		JSONArray ja = new JSONArray();
		JSONObject jsonObject;
		ObjectMapper objMapper = new ObjectMapper();
		String jsonRepresentation;
		for (GC_track_case each_gc_track_case : list_of_gc_track_case) {
			jsonRepresentation = objMapper
					.writeValueAsString(each_gc_track_case);
			jsonObject = (JSONObject) (new JSONParser())
					.parse(jsonRepresentation);
			ja.add(jsonObject);
		}

		return ja;
	}

	private static void test_push_case_data() throws Exception {
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client
				.target( baseUrl +"/push-case-data/");


		String encryptedPassword = encrypt(gctracAuthPassword);

		GC_track_case gc_track_case = new GC_track_case();
		/*
		 * gc_track_case.setA_no("A-895OI"); gc_track_case.setAttorney_id(new
		 * Long(1110)); gc_track_case.setB_no("B-11112");
		 * gc_track_case.setCase_status(new Long(1));
		 * gc_track_case.setCase_type(new Long(2010));
		 */
		// Evan wesser attorney ID  = 3036
		// Brent Burris test account ID 1234
		gc_track_case.setA_no("A-3ZTT2");
		gc_track_case.setAttorney_id(new Long(1234));
		gc_track_case.setB_no("B-899984.4");
		gc_track_case.setCase_status(new Long(1));
		gc_track_case.setCase_type(new Long(2010));

		// We are asuming that when child is pushed it will also send parent B# based on what Alex and Kirill
		//	gc_track_case.setParent_b_no("B-925575.1");
		List<String> child_b_numbers = new ArrayList<String>();

//		child_b_numbers.add("B-899994.2");
//		child_b_numbers.add("B-899994.3");
//		child_b_numbers.add("B-457140.4");


		gc_track_case.setChild_b_numbers(child_b_numbers);

		Response response = target.request()
				.header("Authorization", encryptedPassword)
				.post(Entity.entity(gc_track_case, "application/json"));

		if (response.getStatus() != 200) {

			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus());
		}

		System.out.println(response.readEntity(String.class));
	}

	private static void test_get_new_case() throws Exception {
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client
				.target (baseUrl +"/get-case/A-3ZTT2");

		String encryptedPassword = encrypt(gctracAuthPassword);

		Response response = target.request()
				.header("Authorization", encryptedPassword).get();

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus());
		}

		System.out.println(response.readEntity(String.class));

	}

	private static void test_notify_new_case_created() throws Exception {
		ResteasyClient client = new ResteasyClientBuilder().build();
		/*
		 * ResteasyWebTarget target = client .target(
		 * "http://localhost:8080/EPDS/rest/gc-track-services/notify-case-created/"
		 * );
		 */
		ResteasyWebTarget target = client
				.target(baseUrl + "/notify-case-created/");

		String encryptedPassword = encrypt(gctracAuthPassword);

		GC_track_case gc_track_case = new GC_track_case();
		gc_track_case.setA_no("A-3ZTT2");
		gc_track_case.setAttorney_id(new Long(1234));
		gc_track_case.setB_no("B-899984.3");
		gc_track_case.setCase_status(new Long(2));
		gc_track_case.setCase_type(new Long(2010));
		gc_track_case.setDue_date("09/20/2021");

		Response response = target.request()
				.header("Authorization", encryptedPassword)
				.put(Entity.entity(gc_track_case, "application/json"));

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus());
		}

		System.out.println(response.readEntity(String.class));
	}

	private static void test_clear_case_events() throws Exception {
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client
				.target(baseUrl + "/clear-case-events/A-CNAJT");
		/*
		 * ResteasyWebTarget target = client .target(
		 * "https://epdstest.edc.usda.gov/EPDS/rest/gc-track-services/clear-case-events/A-ALJFJ"
		 * );
		 */

		String encryptedPassword = encrypt(gctracAuthPassword);

		Response response = target.request()
				.header("Authorization", encryptedPassword).delete();

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus());
		}

		System.out.println(response.readEntity(String.class));

	}

	private static void test_delete_event() throws Exception {
		ResteasyClient client = new ResteasyClientBuilder().build();
		/*
		 * ResteasyWebTarget target = client .target(
		 * "http://localhost:8080/EPDS/rest/gc-track-services/delete-event/event-id"
		 * );
		 */
		ResteasyWebTarget target = client
				.target(baseUrl + "/delete-event/382805");

		String encryptedPassword = encrypt(gctracAuthPassword);

		Response response = target.request()
				.header("Authorization", encryptedPassword).delete();

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus());
		}

		System.out.println(response.readEntity(String.class));
	}

	private static void test_get_list_of_epds_event(String aNum) throws Exception {
		System.out.println("------------------------------------------------------------------");
		System.out.println("-------------------GET LIST OF EDS EVENTS BY ANUM-------------------------------------");
		System.out.println("------------------------------------------------------------------");

		ResteasyClient client = new ResteasyClientBuilder().build();
		/*
		 * ResteasyWebTarget target = client .target(
		 * "http://localhost:8080/EPDS/rest/gc-track-services/get-events/A-ALJFJ"
		 * );
		 */
		ResteasyWebTarget target = client
				.target(baseUrl + "/get-events/" + aNum);

		String encryptedPassword = encrypt(gctracAuthPassword);

		Response response = target.request()
				.header("Authorization", encryptedPassword).get();

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus());
		}

		System.out.println(response.readEntity(String.class));

		System.out.println("------------------------------------------------------------------");
	}

	private static void test_get_protest_info(String aNum) throws Exception {
		System.out.println("------------------------------------------------------------------");
		System.out.println("-------------------GET PROTEST INFO-------------------------------------");
		System.out.println("------------------------------------------------------------------");

		ResteasyClient client = new ResteasyClientBuilder().build();

		ResteasyWebTarget target = client
				.target(baseUrl + "/get-case/" + aNum);

		String encryptedPassword = encrypt(gctracAuthPassword);

		Response response = target.request()
				.header("Authorization", encryptedPassword).get();

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus());
		}

		System.out.println(response.readEntity(String.class));

		System.out.println("------------------------------------------------------------------");
	}

	private static void test_get_epdsUpdate() throws Exception {
		System.out.println("------------------------------------------------------------------");
		System.out.println("-------------------GET EDS UPDATE-------------------------------------");
		System.out.println("------------------------------------------------------------------");

		ResteasyClient client = new ResteasyClientBuilder().build();

		ResteasyWebTarget target = client .target(
				baseUrl + "/get-epds-update");

		/*ResteasyWebTarget target = client
				.target("https://epdstest.edc.usda.gov/EPDS/rest/gc-track-services/get-epds-update");*/

		String encryptedPassword = encrypt(gctracAuthPassword);

		Response response = target.request()
				.header("Authorization", encryptedPassword).get();

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus());
		}

		System.out.println(response.readEntity(String.class));

		System.out.println("------------------------------------------------------------------");

	}

	private static void testWebServiceCallWithAuthentication() throws Exception {
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client
				.target(baseUrl + "/new-cases");
		/*
		 * ResteasyWebTarget target = client .target(
		 * "https://epdstest.edc.usda.gov/EPDS/rest/gc-track-services/new-cases"
		 * );
		 */

		String encryptedPassword = encrypt(gctracAuthPassword);

		Response response = target.request()
				.header("Authorization", encryptedPassword).get();

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus());
		}

		System.out.println(response.readEntity(String.class));

	}

	public static String encrypt(String data) throws Exception {
		Key key = generateKey(gctracDecryptPassword);
		Cipher c = Cipher.getInstance("AES");
		c.init(Cipher.ENCRYPT_MODE, key);
		byte[] encVal = c.doFinal(data.getBytes());
		return Base64.getEncoder().encodeToString(encVal);
	}

	/**
	 * Generate a new encryption key.
	 */
	private static Key generateKey(String key) throws Exception {
		return new SecretKeySpec(key.getBytes(), "AES");
	}


	private static void testWebServiceCallWithDataPush() throws Exception {
		Gc_Track_Update2 gc_Track_Update = new Gc_Track_Update2();

		gc_Track_Update.setaNumber("A-J8B39");
		gc_Track_Update.setbNumber("B-11111");

		Attorney attorney = new Attorney();
		attorney.setFirstName("Cherie");
		attorney.setLastName("Owen");
		attorney.setId(2146);

		gc_Track_Update.setAttorney(attorney);

		ResteasyClient client = new ResteasyClientBuilder().build();
		/*
		 * ResteasyWebTarget target = client
		 * .target("http://localhost:8080/EPDS/rest/gc-track-services/push-update"
		 * );
		 */

		ResteasyWebTarget target = client
				.target(baseUrl + "/push-update");

		String encryptedPassword = encrypt(gctracAuthPassword);

		/*
		 * Gc_Track_Data gc_Track_Data = new Gc_Track_Data();
		 * gc_Track_Data.setId(3); gc_Track_Data.setA_No("A-qwery");
		 * gc_Track_Data.setAttorney_First_Name("Besling");
		 * gc_Track_Data.setB_No("B-JK1KJ");
		 */

		/*
		 * Student st = new Student(); st.setId(1); st.setName("Scott");
		 */

		Response response = target.request()
				.header("Authorization", encryptedPassword)
				.post(Entity.entity(gc_Track_Update, "application/json"));

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus());
		}

		System.out.println(response.readEntity(String.class));

	}

	public static void testSimpleWebServiceCall() {
		ResteasyClient client = new ResteasyClientBuilder().build();

		ResteasyWebTarget target = client
				.target("http://localhost:8080/EPDS/rest/gc-track-services/new-cases");
		/*
		 * ResteasyWebTarget target = client .target(
		 * "https://epdstest.edc.usda.gov/EPDS/rest/gc-track-services/new-cases"
		 * );
		 */

		Response response = target.request().get();

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus());
		}

		System.out.println(response.readEntity(String.class));
	}
}
