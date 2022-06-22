package gov.gao.epds.test;

import gov.gao.epds.persistence.entity.File_Info;
import gov.gao.epds.persistence.entity.User_Info;
import gov.gao.epds.utils.Date_Util;
import gov.gao.epds.utils.Util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Key;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.client.ClientProtocolException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.util.Base64;


import com.EPDS.US.Dog;
import com.google.gson.Gson;

@SuppressWarnings("deprecation")
public class Test {

	public static void main(String[] args) throws Exception {
		testDate();
		// test_epds_dueDate();
		// testAddSemicolonSeparatedText();
		// testPropertiesFileRead();
		// testPropertiesFileRead2();
		// testJSONArray();
		// testComparatorByDate();
		// testEncryptionAndDecryption();
		// testEPDS_WebService();
		// testRecursiveFunction();
		// testDog_ParentToChildren_Relation();
		// testStringToDateConversion();

	}

	private static void testDate() throws ParseException {
		// System.out.println(Date_Util.getCurrentDate("MM/dd/yyyy HH:mm:ss z"));
		/*
		 * try { System.out.println(Date_Util.convertToSpecifiedFormat(
		 * "Oct 06 2015 14:56:07 EDT", "MMM dd yyyy HH:mm:ss z",
		 * "MM/dd/yyyy HH:mm:ss z")); } catch (ParseException e) {
		 * e.printStackTrace(); }
		 */

		/*
		 * System.out.println(Date_Util.convertToSpecifiedDateFormatIfNotAlready(
		 * "Sep 07 2015 00:00:00 EDT", "MM/dd/yyyy HH:mm:ss z"));
		 */

		/*
		 * Calendar currentCalendar = Calendar.getInstance();
		 * System.out.println(currentCalendar.getTime());
		 * 
		 * currentCalendar.add(Calendar.DATE, -15);
		 * System.out.println(currentCalendar.getTime());
		 */

		System.out.println(Date_Util.getNumberOfDaysRemaining("01/20/2016"));

		//protest dueDate = 100 days
		//AR = Agency Report Due Date = 30 days
		System.out.println(Date_Util.getDueDate("Aug 01 2019 17:27:31 EDT", 30));

	}

	private static void testAddSemicolonSeparatedText() {
		String text = "hello";
		Util.addSemicolonSeparatedText(text, " programmer");

		System.out.println(text);
	}

	private static void test_epds_dueDate() throws ParseException {
		String testDate = Date_Util
				.convertToMMMDDYYYYHHMMSSZFormat("12/23/2015");

		System.out.println(testDate);

		// System.out.println(Date_Util.getCurrentDate(false));

		System.out.println("Due date: " + Date_Util.getDueDate(testDate,100));

		System.out.println(Date_Util.getNumberOfDaysRemaining("04/01/2016"));

	}

	private static void testPropertiesFileRead2() throws IOException {
		ClassPathResource resource = new ClassPathResource("/config.properties");
		Properties props = PropertiesLoaderUtils.loadProperties(resource);

		System.out.println(props.getProperty("successURL"));
	}

	private static void testPropertiesFileRead() throws IOException {
		Properties properties = new Properties();

		System.out.println(new File(".").getCanonicalPath());
		InputStream inputStream = new FileInputStream(
				"C:\\Users\\radhikari\\Workspace_GAO\\EPDS\\src\\main\\resources\\config.properties");

		properties.load(inputStream);
		System.out.println(properties.getProperty("successURL"));

	}

	@SuppressWarnings("unchecked")
	private static void testJSONArray() throws JsonGenerationException,
			JsonMappingException, IOException,
			org.json.simple.parser.ParseException {
		User_Info user_Info1 = new User_Info();
		user_Info1.setCity("kathmandu");

		User_Info user_Info2 = new User_Info();
		user_Info2.setCity("pokhara");

		ObjectMapper objMapper = new ObjectMapper();
		String output1 = objMapper.writeValueAsString(user_Info1);
		String output2 = objMapper.writeValueAsString(user_Info2);

		JSONObject json1 = (JSONObject) (new JSONParser()).parse(output1);
		JSONObject json2 = (JSONObject) (new JSONParser()).parse(output2);

		JSONArray ja = new JSONArray();
		ja.add(json1);
		ja.add(json2);

		Gson gson = new Gson();
		List<User_Info> user_info_list = new ArrayList<User_Info>();
		User_Info user_info;
		JSONObject jsonObject;

		for (int i = 0; i < ja.size(); i++) {
			jsonObject = (JSONObject) ja.get(i);
			user_info = gson.fromJson(jsonObject.toJSONString(),
					User_Info.class);
			System.out.println(user_info.getCity());
		}

		/*
		 * User_Info user_info1 = gson.fromJson(json1.toJSONString(),
		 * User_Info.class); User_Info user_info2 =
		 * gson.fromJson(json2.toJSONString(), User_Info.class);
		 * 
		 * System.out.println(user_info1.getCity());
		 * System.out.println(user_info2.getCity());
		 */

		/*
		 * System.out.println(json1); System.out.println(json2);
		 * System.out.println(ja);
		 */

		/*
		 * for (JSONObject eachJsonObject : (JSONObject[]) ja.toArray()) {
		 * System.out.println(eachJsonObject); }
		 */

	}

	private static void testComparatorByDate() {
		File_Info f1 = new File_Info(1, "Jun 29 2015 16:03:35 EDT");
		File_Info f2 = new File_Info(2, "Jun 11 2015 00:00:00 EDT");
		File_Info f3 = new File_Info(3, "Jul 09 2015 00:00:00 EDT");
		File_Info f4 = new File_Info(4, "Jul 13 2015 18:33:04 EDT");
		File_Info f5 = new File_Info(5, "Jul 14 2015 07:44:17 EDT");

		List<File_Info> file_Info_List = new ArrayList<File_Info>();
		file_Info_List.add(f3);
		file_Info_List.add(f2);
		file_Info_List.add(f5);
		file_Info_List.add(f1);
		file_Info_List.add(f4);

		Collections.sort(file_Info_List);

		for (File_Info eachFile_Info : file_Info_List) {
			System.out.println(eachFile_Info.getFile_Id());
		}

	}

	private static void testEncryptionAndDecryption() throws Exception {
		String encryptedValue = encrypt("pass");
		System.out.println("Encrypted value: " + encryptedValue);

		String decryptedValue = decrypt(encryptedValue);
		System.out.println("Decrypted value: " + decryptedValue);
	}

	private static String decrypt(String encryptedValue) throws Exception {
		String keyValue = "CBCA_app_Decrypt123";
		Key key = generateKey(keyValue);
		Cipher c = Cipher.getInstance("AES");
		c.init(Cipher.DECRYPT_MODE, key);
		byte[] decodedValue = Base64.getDecoder().decode(encryptedValue);
		byte[] decValue = c.doFinal(decodedValue);
		String decryptedValue = new String(decValue);
		return decryptedValue;
	}

	private static String encrypt(String password) throws Exception {
		String keyValue = "CBCA_app_Decrypt123";
		Key key = generateKey(keyValue);

		Cipher c = Cipher.getInstance("AES");
		c.init(Cipher.ENCRYPT_MODE, key);
		byte[] encVal = c.doFinal(password.getBytes());
		String encryptedValue = Base64.getEncoder().encodeToString(encVal);

		return encryptedValue;
	}

	private static Key generateKey(String keyword) {
		Key key = new SecretKeySpec(keyword.getBytes(), "AES");
		return key;
	}

	private static void testEPDS_WebService() {
		try {

			ClientRequest request = new ClientRequest(
					"https://epdstest.edc.usda.gov/EPDS/rest/gc-track-services/print");

			/*
			 * ClientRequest request = new ClientRequest(
			 * "http://localhost:8080/EPDS/rest/gc-track-services/print");
			 */
			request.accept("application/json");
			ClientResponse<String> response = request.get(String.class);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					new ByteArrayInputStream(response.getEntity().getBytes())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}

		} catch (ClientProtocolException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	private static void testRecursiveFunction() {
		List<Dog> finalDogList = getFinalDogList();

		List<String> dogIdList = new ArrayList<String>();

		Dog testDog = finalDogList.get(0).getChildrenDogList().get(1)
				.getChildrenDogList().get(0);
		Dog rootParentDog = getRootParentDog(testDog, finalDogList);

		populateDogIdList(rootParentDog, dogIdList);
		// populateDogIdList(finalDogList.get(0), dogIdList);

	}

	private static Dog getRootParentDog(Dog testDog, List<Dog> finalDogList) {
		if (testDog.getParentDogId() == null
				|| testDog.getParentDogId().equals(""))
			return testDog;

		for (Dog eachDog : finalDogList) {
			if (checkIfItIsRootParentDog(eachDog, testDog)) {
				return eachDog;
			}
		}

		return null;
	}

	private static boolean checkIfItIsRootParentDog(Dog eachDog, Dog testDog) {
		for (Dog eachDog2 : eachDog.getChildrenDogList()) {
			if (eachDog2.getId().equals(testDog.getId())
					|| eachDog2.getId().equals(testDog.getParentDogId()))
				return true;
		}

		return false;
	}

	private static void populateDogIdList(Dog dog, List<String> dogIdList) {
		dogIdList.add(dog.getId());
		for (Dog eachDog : dog.getChildrenDogList()) {
			populateDogIdList(eachDog, dogIdList);
		}
	}

	private static void testDog_ParentToChildren_Relation()
			throws JsonGenerationException, JsonMappingException, IOException {
		List<Dog> finalDogList = getFinalDogList();

		ObjectMapper objMapper = new ObjectMapper();
		System.out.println(objMapper.writeValueAsString(finalDogList));

	}

	private static List<Dog> getFinalDogList() {
		Dog a = new Dog("Brown", "", "", "1", "");
		Dog b = new Dog("Black", "", "", "2", "1");
		Dog c = new Dog("White", "", "", "3", "1");
		Dog d = new Dog("White", "", "", "4", "");
		Dog e = new Dog("Orange", "", "", "5", "3");

		List<Dog> wholeDogList = new ArrayList<Dog>();
		wholeDogList.add(a);
		wholeDogList.add(b);
		wholeDogList.add(c);
		wholeDogList.add(d);
		wholeDogList.add(e);

		Map<String, Dog> dogMap = getDogMap(wholeDogList);

		for (Dog eachDog : wholeDogList) {
			if (eachDog.getParentDogId() != null
					&& !eachDog.getParentDogId().equals("")) {
				Dog parentDog = dogMap.get(eachDog.getParentDogId());
				if (parentDog != null) {
					parentDog.getChildrenDogList().add(eachDog);
				}
			}
		}

		List<Dog> finalDogList = new ArrayList<Dog>();
		for (Dog eachDog : wholeDogList) {
			if (eachDog.getParentDogId() == null
					|| eachDog.getParentDogId().equals("")) {
				finalDogList.add(eachDog);
			}
		}
		return finalDogList;
	}

	private static Map<String, Dog> getDogMap(List<Dog> wholeDogList) {
		Map<String, Dog> dogMap = new HashMap<String, Dog>();

		for (Dog eachDog : wholeDogList) {
			if (eachDog.getId() != null && !eachDog.getId().equals("")) {
				dogMap.put(eachDog.getId(), eachDog);
			} else {
				eachDog.setId("");
			}

		}

		return dogMap;
	}

	private static void testStringToDateConversion() throws ParseException {
		String string = "Jun 29 2015 17:15:16 EDT";
		DateFormat format = new SimpleDateFormat("MMM dd yyyy HH:mm:ss z",
				Locale.ENGLISH);
		Date date = format.parse(string);
		System.out.println(date); // Sat Jan 02 00:00:00 GMT 2010

		System.out.println(new Date());
	}

}
