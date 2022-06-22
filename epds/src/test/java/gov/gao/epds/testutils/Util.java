package gov.gao.epds.testutils;

import gov.gao.epds.dto.User_info_dto;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

/**
 * @author MHussaini
 * Utility class to populate data to test epds-auth
 */
public class Util {
	private static String baseEPDSRestAuthURL = "https://epdstest.edc.usda.gov/epds-auth/rest/";

	public static User_info_dto getUser_info_dto_forRegistrationOrUpdate(
			boolean isSecurityQuestionsAdded) {
		User_info_dto user_info_dto = new User_info_dto();

		/*user_info_dto.setAccount_status_id(1);
		user_info_dto.setCity("Fort Collins");
		user_info_dto.setCountry("USA");
		user_info_dto.setEmail("amer111@gmail.com");
		user_info_dto.setFax_no("1111111111");
		// user_info_dto.setFirm_id();
		user_info_dto.setFirm_name("Amer Protest Firm");
		user_info_dto.setFirst_name("Mohammed");
		user_info_dto.setLast_name("Hussaini");
		user_info_dto.setPassword("admin");
		user_info_dto.setPhone_no("1111111111");
		user_info_dto.setRole_id(1);
		user_info_dto.setSecQIdToAnswerMap(getSecQIdToAnswerMap());
		user_info_dto.setStreet("200 Harmony Road");
		user_info_dto.setZip_code("80526");
		user_info_dto.setState("CO");
		user_info_dto.setAuth_token("aeq142");*/

		if (isSecurityQuestionsAdded) {
			user_info_dto.setSecQIdToAnswerMap(getSecQIdToAnswerMap());
		}

		return user_info_dto;
	}

	private static Map<Integer, String> getSecQIdToAnswerMap() {
		Map<Integer, String> secQIdToAnswerMap = new HashMap<Integer, String>();
		secQIdToAnswerMap.put(1, "Amira");
		secQIdToAnswerMap.put(2, "Seattle");
		secQIdToAnswerMap.put(3, "Rakesh");
		secQIdToAnswerMap.put(4, "Devon Street");
		secQIdToAnswerMap.put(5, "March");

		return secQIdToAnswerMap;
	}

	public static String testResponseFromRestService(Object payLoad,
			String relativePath, String httpMethod) {

		Response response = null;

		try {

			ResteasyClient client = new ResteasyClientBuilder().build();

			ResteasyWebTarget target = client.target(baseEPDSRestAuthURL
					+ relativePath);

			if (httpMethod.equalsIgnoreCase("post")) {
				response = target.request().post(
						Entity.entity(payLoad, "application/json"));
			} else if (httpMethod.equalsIgnoreCase("put")) {
				response = target.request().put(
						Entity.entity(payLoad, "application/json"));
			} else if (httpMethod.equalsIgnoreCase("GET")) {
				response = target.request().get();
			}

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			System.out.println("Server response : \n");
			System.out.println(response.readEntity(String.class));
			response.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		

		return response.readEntity(String.class);
	}

	public static User_info_dto getUser_info_dtoForLogin() {
		User_info_dto user_info_dto = new User_info_dto();
		user_info_dto.setEmail("amer111@gmail.com");
		user_info_dto.setPassword("admin");
		user_info_dto.setClient_ip("199.12.31");

		return user_info_dto;
	}

}
