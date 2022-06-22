package gov.gao.epds.utils;

import gov.gao.epds.dto.User_info_auth;
import gov.gao.epds.dto.User_info_dto;
import gov.gao.epds.enums.UserRoles;
import static gov.gao.epds.enums.UserRoles.*;
import gov.gao.epds.persistence.entity.GAO_User;
import gov.gao.epds.persistence.entity.User_Info;
import gov.gao.epds.rest.auth.services.AuthUtil;
import gov.gao.epds.service.UserInfoService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class RegistrationUtil {
	
	@Autowired
	private static AuthUtil authUtil;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationUtil.class);
	public static void bulkUpload(String userType,
			UserInfoService userInfoService, HttpServletRequest request) throws JsonProcessingException,
			IOException, Exception {
		List<User_info_dto> listOfUserInfoDTO = getUserInfoListForBulkUpload(userType);

		int numberOfFailedCases = 0;
		int numberOfSuccessCases = 0;
		for (User_info_dto eachUserInfDTO : listOfUserInfoDTO) {
			try {
				registerUser(eachUserInfDTO, userInfoService, userType, false,request);
				numberOfSuccessCases++;
			} catch (Exception e) {
				numberOfFailedCases++;
			}
		}

		System.out.println("Number of total failed cases: "
				+ numberOfFailedCases);
		System.out.println("Number of total success cases: "
				+ numberOfSuccessCases);
	}

	private static List<User_info_dto> getUserInfoListForBulkUpload(
			String userType) {
		List<User_info_dto> listOfUserInfoDTO = new ArrayList<User_info_dto>();

		try {
			FileInputStream file = new FileInputStream(new File("C:/Users/mhussaini/GAO/updateGAOUsers.xlsxsjcnsd"));

			XSSFWorkbook workbook = new XSSFWorkbook(file);

			XSSFSheet sheet;
			if (userType.equalsIgnoreCase("GAO USER")) {
				sheet = workbook.getSheetAt(0);
			} else {
				sheet = workbook.getSheetAt(1);
			}

			Iterator<Row> rowIterator = sheet.iterator();
			rowIterator.next();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();

				User_info_dto userInfoDTO = new User_info_dto();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();

					setUserInfoDTOField(userInfoDTO, cell, userType);

					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_NUMERIC:
						System.out.print((int) cell.getNumericCellValue());
						break;
					case Cell.CELL_TYPE_STRING:
						System.out.print(cell.getStringCellValue() + "  ");
						break;
					}
				}

				if (userInfoDTO.getEmail() != null) {
					listOfUserInfoDTO.add(userInfoDTO);
				} else {
					break;
				}
			}
			file.close();
		} catch (Exception e) {
			
			LOGGER.error("Exception occurred when getUserInfoListForBulkUpload", e);
			e.printStackTrace();
		}

		return listOfUserInfoDTO;
	}

	private static void setUserInfoDTOField(User_info_dto userInfoDTO,
			Cell cell, String userType) {
		if (userType.equalsIgnoreCase("GAO USER")) {
			populateGaoUserField(userInfoDTO, cell);
			//userInfoDTO.getEmail().toLowerCase(Locale.ENGLISH);
		} else {
			populateAgencyPocField(userInfoDTO, cell);
		}
	}

	private static void populateAgencyPocField(User_info_dto userInfoDTO,
			Cell cell) {
		userInfoDTO.setAuth_role_id(2);
		userInfoDTO.setEpds_role_id(5);

		switch (cell.getColumnIndex()) {
		case 0:
			userInfoDTO.setEmail(cell.getStringCellValue());
			break;
		case 1:
			userInfoDTO.setFirstName(cell.getStringCellValue());
			break;
		case 2:
			userInfoDTO.setLastName(cell.getStringCellValue());
			break;
		case 3:
			userInfoDTO.setPhoneNo(cell.getStringCellValue());
			break;
		case 4:
			userInfoDTO.setFaxNo(cell.getStringCellValue());
			break;
		case 5:
			userInfoDTO.setAddress1(cell.getStringCellValue());
			break;
		case 6:
			userInfoDTO.setAddress2(cell.getStringCellValue());
			break;
		case 7:
			userInfoDTO.setCity(cell.getStringCellValue());
			break;
		case 8:
			userInfoDTO.setState(cell.getStringCellValue());
			break;
		case 9:
			userInfoDTO.setCountry(cell.getStringCellValue());
			break;
		case 10:
			userInfoDTO.setZipCode((int) cell.getNumericCellValue() + "");
			break;
		case 11:
			userInfoDTO.setMiddle_initial(cell.getStringCellValue());
			break;
		case 12:
			userInfoDTO.setPrefix(cell.getStringCellValue());
			break;
		case 13:
			userInfoDTO.setSuffix(cell.getStringCellValue());
			break;
		case 14:
			userInfoDTO.setFirm_id((int) cell.getNumericCellValue());
			break;
		}

	}

	/*private static void populateGaoUserField(User_info_dto userInfoDTO,
			Cell cell) {
		userInfoDTO.setAddress1("441 G St., NW");
		userInfoDTO.setAddress2("");
		userInfoDTO.setCity("District of Columbia");
		userInfoDTO.setState("Washington");
		userInfoDTO.setCountry("United States");
		userInfoDTO.setZipCode("20548");

		userInfoDTO.setAuth_role_id(3);
		userInfoDTO.setNameOfFirm("GAO");

		switch (cell.getColumnIndex()) {
		case 0:
			userInfoDTO.setEmail(cell.getStringCellValue());
			break;
		case 1:
			userInfoDTO.setTitle(cell.getStringCellValue());
			break;
		case 2:
			String role = cell.getStringCellValue();
			int epds_role_id = 3;
			if (role.equalsIgnoreCase("SUPERVISOR")) {
				epds_role_id = 8;
			} else if (role.equalsIgnoreCase("ADMIN")) {
				epds_role_id = 7;
			}
			userInfoDTO.setRole(role);
			userInfoDTO.setEpds_role_id(epds_role_id);
			break;
		case 3:
			userInfoDTO.setFirstName(cell.getStringCellValue());
			break;
		case 4:
			userInfoDTO.setLastName(cell.getStringCellValue());
			break;
		case 5:
			userInfoDTO.setPhoneNo(cell.getStringCellValue());
			break;
		case 6:
			userInfoDTO.setMiddle_initial(cell.getStringCellValue());
			break;
		case 7:
			userInfoDTO.setPrefix(cell.getStringCellValue());
			break;
		case 8:
			userInfoDTO.setSuffix(cell.getStringCellValue());
			break;
		case 9:
			userInfoDTO.setGroupNo((int) cell.getNumericCellValue());
			break;
		case 10:
			userInfoDTO.setGaoId((int) cell.getNumericCellValue());
		}
	}*/
	
	
	/*
	 * GAO sent the Excel sheet in the below format ...So I'm using this  order 
	*/
	
	private static void populateGaoUserField(User_info_dto userInfoDTO,
			Cell cell) {
		
		userInfoDTO.setAddress1("441 G St., NW");
		userInfoDTO.setAddress2("");
		userInfoDTO.setCity("District of Columbia");
		userInfoDTO.setState("Washington");
		userInfoDTO.setCountry("United States");
		userInfoDTO.setZipCode("20548");

		userInfoDTO.setAuth_role_id(3);
		userInfoDTO.setNameOfFirm("GAO");

		switch (cell.getColumnIndex()) {
		case 0:
			userInfoDTO.setEmail(cell.getStringCellValue().toLowerCase(Locale.ENGLISH));
			break;
		case 1:
			userInfoDTO.setTitle(cell.getStringCellValue());
			break;
		case 2:
			String role = cell.getStringCellValue();
			int epds_role_id = 3;
			if (role.equalsIgnoreCase("SUPERVISOR")) {
				epds_role_id = 8;
			} else if (role.equalsIgnoreCase("ADMIN")) {
				epds_role_id = 7;
			}
			userInfoDTO.setRole(role);
			userInfoDTO.setEpds_role_id(epds_role_id);
			break;
		case 3:
			userInfoDTO.setPrefix(cell.getStringCellValue());
			break;
		case 4:
			userInfoDTO.setFirstName(cell.getStringCellValue());
			
			break;
		case 5:
			userInfoDTO.setMiddle_initial(cell.getStringCellValue());
			
			break;
		case 6:
			userInfoDTO.setLastName(cell.getStringCellValue());
			break;
		case 7:
			userInfoDTO.setSuffix(cell.getStringCellValue());
			break;
		case 8:
			userInfoDTO.setPhoneNo(cell.getStringCellValue());
			break;
		case 9:
			/*userInfoDTO.setGroupNo((int) cell.getNumericCellValue());*/
			userInfoDTO.setGroupNo(getGroupValue(cell));
			break;
		case 10:
			userInfoDTO.setGaoId((int) cell.getNumericCellValue());
		}
	}

	
	
	public static Integer getGroupValue(Cell cell) {
		
		Integer groupNo = null;
		
		if (cell.getStringCellValue().toLowerCase(Locale.ENGLISH).contains("1")){
			
			groupNo= 1;
		}else if (cell.getStringCellValue().toLowerCase(Locale.ENGLISH).contains("2")){
			
			groupNo= 2;
		}else if (cell.getStringCellValue().toLowerCase(Locale.ENGLISH).contains("3")){
			
			groupNo= 3;
		}else if (cell.getStringCellValue().toLowerCase(Locale.ENGLISH).contains("4")){
			
			groupNo= 4;
		}else if (cell.getStringCellValue().toLowerCase(Locale.ENGLISH).contains("5")){
			
			groupNo= 5;
		}else if (cell.getStringCellValue().toLowerCase(Locale.ENGLISH).contains("6")){
			
			groupNo= 6;
		}else if (cell.getStringCellValue().toLowerCase(Locale.ENGLISH).contains("7")){
			
			groupNo= 7;
		}
		
		
		return groupNo;
	}
	public static void populateGaoUserField(User_info_dto userInfoDTO){
			
			userInfoDTO.setAddress1("441 G St., NW");
			//userInfoDTO.setAddress2("");
			userInfoDTO.setCity("District of Columbia");
			userInfoDTO.setState("Washington");
			userInfoDTO.setCountry("United States");
			userInfoDTO.setZipCode("20548");
			userInfoDTO.setAuth_role_id(3);
			userInfoDTO.setNameOfFirm("GAO");
			
			
			int epds_role_id = 0;
			if (userInfoDTO.getRole().equalsIgnoreCase("SUPERVISOR") 
					|| userInfoDTO.getRole().toUpperCase(Locale.ENGLISH).contains("SUPERVISOR")) {
				epds_role_id = 8;
			} else if (userInfoDTO.getRole().equalsIgnoreCase("ADMIN") 
					|| userInfoDTO.getRole().toUpperCase(Locale.ENGLISH).contains("ADMIN")) {
				epds_role_id = 7;
			}else if (userInfoDTO.getRole().equalsIgnoreCase("ATTORNEY") 
					|| userInfoDTO.getRole().toUpperCase(Locale.ENGLISH).contains("ATTORNEY")) {
				epds_role_id = 3;
			}
			userInfoDTO.setRole(userInfoDTO.getRole());
			userInfoDTO.setEpds_role_id(epds_role_id);
		}
	private static void testExcelRead() {
		try {
			FileInputStream file = new FileInputStream(
					new File(
							"C:\\Users\\radhikari\\GAO\\Authentication\\ExcelReadTestFile.xlsx"));

			// Create Workbook instance holding reference to .xlsx file
			XSSFWorkbook workbook = new XSSFWorkbook(file);

			// Get first/desired sheet from the workbook
			XSSFSheet sheet = workbook.getSheetAt(0);

			// Iterate through each rows one by one
			Iterator<Row> rowIterator = sheet.iterator();
			rowIterator.next();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				// For each row, iterate through all the columns
				Iterator<Cell> cellIterator = row.cellIterator();

				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					// Check the cell type and format accordingly
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_NUMERIC:
						System.out.print(cell.getNumericCellValue());
						break;
					case Cell.CELL_TYPE_STRING:
						System.out.print(cell.getStringCellValue() + "  ");
						break;
					}
				}
				System.out.println("");
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static User_Info registerUser(User_info_dto user_info_dto,
			UserInfoService userInfoService, String userType, boolean isGAOUser, HttpServletRequest request)
			throws JsonProcessingException, IOException, Exception {
		
		User_Info userInfoByEmail = userInfoService.getUser_InfoByEmail(user_info_dto.getEmail());
		UserRoles role = UserRoles.getByCode(user_info_dto.getEpds_role_id());

		user_info_dto.setAuth_role_id(AuthUtil.getEpdsAuthRoleId(role));
		
		/*AuthUtil.setEPDSRoleAndRoleId(user_info_dto);*/
		//Amer : maybe over here I can just use epdsAuthroleid == 2


		if (role == AGENCY_ATTORNEY || role == AGENCY_ADMIN) {
			if (user_info_dto.getFirm_id() == null) {
				Integer firm_id = userInfoService.getAgencyId(
						user_info_dto.getTier1_agency_id(),
						user_info_dto.getTier2_agency_id());
				user_info_dto.setFirm_id(firm_id);
			}

			if (GlobalParams.IP.equals(GlobalParams.PROD_IP_ADDR)
					&& (!user_info_dto.getEmail().endsWith(".gov") 
							&& !user_info_dto.getEmail().endsWith(".mil") 
					&& !user_info_dto.getEmail().endsWith(".edu") )){
				throw new IllegalArgumentException("Email Address should contains .gov or .mil");
			}
			
			if (user_info_dto.getNameOfFirm() == null) {
				String agencyName = userInfoService.getAgencyNamebyAgencyInfoId(user_info_dto
						.getFirm_id());
				
				if (agencyName ==  null || agencyName.equalsIgnoreCase("")){
					throw new IllegalArgumentException("Agency Name is not selected");
				}
				
				user_info_dto.setNameOfFirm(agencyName);
			}

		}else if (user_info_dto.getAuth_role_id() == 3){
			populateGaoUserField(user_info_dto);
		}

		
		AuthUtil authUtil = new AuthUtil(new RestTemplate());
		String jsonResponse = authUtil.getAuthJSONResponse("registerURI", "n",user_info_dto,request);

		boolean isSuccess = AuthUtil.getJsonNode(jsonResponse, "isSuccess")
				.asBoolean();

		User_Info userInfo = null;
		
		//Amer Notes: We need provide validation of the GAOId before completing the transaction because sometimes the user might get registered 
		// and send email but the GAO ID might be already there and the account will not be properly registered
		if (isSuccess) {
			JsonNode dataJsonNode = AuthUtil.getJsonNode(jsonResponse, "data");
			User_info_auth user_info_auth = (User_info_auth) AuthUtil
					.convertToPOJO(dataJsonNode, User_info_auth.class);

			user_info_dto.setUser_id(user_info_auth.getUser_id());
			userInfo = userInfoService.saveProfileInfo(user_info_dto);
			
			if (userInfoByEmail != null){
				userInfoService.updateUserId(userInfo.getUser_Id(),userInfo.getEmail());	
			}
			
		}

		if (userInfo != null && userType.equalsIgnoreCase("GAO USER")) {
			
			if (GlobalParams.IP.equals(GlobalParams.PROD_IP_ADDR)
					&& (!user_info_dto.getEmail().endsWith(".gov"))){
				throw new IllegalArgumentException("Email Address should contains .gov");
			}
			GAO_User gao_User = new GAO_User();
			gao_User.setGroup_No(user_info_dto.getGroupNo());
			gao_User.setId(user_info_dto.getGaoId());
			gao_User.setTitle(user_info_dto.getTitle());
			gao_User.setType(user_info_dto.getRole());
			gao_User.setUser_Id(userInfo.getUser_Id());

			userInfoService.save(gao_User);
		}

	
		try {
			if (role == AGENCY_ADMIN){
				userInfoService.addAgencyPOC(userInfo.getUser_Id(), userInfo.getFirm_id());
			}
		}catch (NullPointerException e){
			e.printStackTrace();
		}
		return userInfo;
	}

}
