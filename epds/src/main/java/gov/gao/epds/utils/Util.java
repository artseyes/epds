package gov.gao.epds.utils;

import gov.gao.epds.dao.Agency_Info_DAO;
import gov.gao.epds.dto.SubmitNewDocDTO;
import gov.gao.epds.dto.User_info_dto;
import gov.gao.epds.persistence.entity.Agency_Info;
import gov.gao.epds.persistence.entity.Protest_Info;
import gov.gao.epds.persistence.entity.Tier_1_Agency;
import gov.gao.epds.persistence.entity.Tier_2_Agency;
import gov.gao.epds.persistence.entity.User_Info;
import gov.gao.epds.persistence.entity.User_Protest_Role_Bridge;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

public class Util {

	/**
	 * @Todo : need to add a check if this A# is already assigned
	 * @return
	 */
	public static String getA_Num() {
		String possibleChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random rnd = new Random();
		StringBuilder sb = new StringBuilder(5);

		for (int i = 0; i < 5; i++) {
			sb.append(possibleChars.charAt(rnd.nextInt(possibleChars.length())));
		}

		return "A-" + sb.toString();
	}
	
	
	
	public List<Integer> converStrListToIntList(List<String> strList){
		
		return strList
				.stream()
				.map(Integer::parseInt).collect(Collectors.toList());
	}
	
	public static List<String> converIntListToStringList(List<Integer> intList){
		
		return intList
				.stream()
				.map(Object::toString).collect(Collectors.toList());
	}
	public boolean isGAOUserWithFullAccess(boolean isViewOnly, User_Info user_Info){
		
		boolean isGAOUserSupervisorOrAttorney = (user_Info.getRole_id().equals(8) || user_Info.getRole_id().equals(3));
		boolean isGAOUserWithFullAccess = (user_Info.getRole_id().equals(7) || (isGAOUserSupervisorOrAttorney && !isViewOnly));
		
		return isGAOUserWithFullAccess;
	}

	public static <T> List<T> getDesiredClassObjectList(List<?> list,
			Class<T> entityClass) {
		if (list == null)
			return new ArrayList<T>();

		List<T> toBeReturnedList = new ArrayList<T>();

		for (Object eachObj : list) {
			
			for (Object eachObj2 : (Object[]) eachObj) {
				
				if (null != eachObj2){
					if (eachObj2.getClass().equals(entityClass)) {
						toBeReturnedList.add(entityClass.cast(eachObj2));
					}
				}
				
			}
		}

		return toBeReturnedList;
	}

	public static String getAgencyName(int agency_Info_Id,
			Agency_Info_DAO agency_Info_DAO) {
		Map<Integer, Tier_1_Agency> tier_1_AgencyMap = getTier_1_AgencyMap(agency_Info_DAO);
		Map<Integer, Tier_2_Agency> tier_2_AgencyMap = getTier_2_AgencyMap(agency_Info_DAO);
		Map<Integer, Agency_Info> agency_InfoMap = getAgency_InfoMap(agency_Info_DAO);

		String agencyName = "";
		try {
			Agency_Info agency_Info = agency_InfoMap.get(agency_Info_Id);
			if (agency_Info.getTier().equals("1")) {
				agencyName = tier_1_AgencyMap.get(agency_Info.getAgency_Id())
						.getAgency_Name();
			} else {
				Tier_2_Agency tier_2_Agency = tier_2_AgencyMap.get(agency_Info
						.getAgency_Id());
				Tier_1_Agency tier_1_Agency = tier_1_AgencyMap
						.get(tier_2_Agency.getTier_1_Agency_Id());
				agencyName = tier_1_Agency.getAgency_Name() + "/"
						+ tier_2_Agency.getAgency_Name();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return agencyName;
	}

	private static Map<Integer, Agency_Info> getAgency_InfoMap(
			Agency_Info_DAO agency_Info_DAO) {
		Map<Integer, Agency_Info> agency_InfoMap = GlobalFields.agency_InfoMap;
		if (agency_InfoMap == null) {
			agency_InfoMap = agency_Info_DAO.getAgency_InfoMap();
			GlobalFields.agency_InfoMap = agency_InfoMap;
		}

		return agency_InfoMap;
	}

	private static Map<Integer, Tier_2_Agency> getTier_2_AgencyMap(
			Agency_Info_DAO agency_Info_DAO) {
		Map<Integer, Tier_2_Agency> tier_2_AgencyMap = GlobalFields.tier_2_AgencyMap;
		if (tier_2_AgencyMap == null) {
			tier_2_AgencyMap = agency_Info_DAO.getTier_2_AgencyMap();
			GlobalFields.tier_2_AgencyMap = tier_2_AgencyMap;
		}

		return tier_2_AgencyMap;
	}

	private static Map<Integer, Tier_1_Agency> getTier_1_AgencyMap(
			Agency_Info_DAO agency_Info_DAO) {
		Map<Integer, Tier_1_Agency> tier_1_AgencyMap = GlobalFields.tier_1_AgencyMap;
		if (tier_1_AgencyMap == null) {
			tier_1_AgencyMap = agency_Info_DAO.getTier_1_AgencyMap();
			GlobalFields.tier_1_AgencyMap = tier_1_AgencyMap;
		}

		return tier_1_AgencyMap;
	}

	public static String getAddress(SubmitNewDocDTO submitNewDocDTO) {

		if (submitNewDocDTO.getAddress1() == null
				|| submitNewDocDTO.getAddress1().equalsIgnoreCase("")) {
			submitNewDocDTO.setAddress1("");
		}

		if (submitNewDocDTO.getAddress2() != null
				&& !submitNewDocDTO.getAddress2().equalsIgnoreCase("")) {
			String address = submitNewDocDTO.getAddress1() + "\n"
					+ submitNewDocDTO.getAddress2();
			submitNewDocDTO.setAddress1(address);
		}

		return submitNewDocDTO.getAddress1() + "\n" + submitNewDocDTO.getCity()
				+ ", " + submitNewDocDTO.getState() + ", "
				+ submitNewDocDTO.getZipCode() + "\n"
				+ submitNewDocDTO.getCountry();
	}

	public static User_Info getPopulatedUserInfo(User_info_dto user_info_dto) {
		User_Info user_info = new User_Info();
		user_info.setAddress1(user_info_dto.getAddress1());
		user_info.setAddress2(user_info_dto.getAddress2());
		user_info.setCity(user_info_dto.getCity());
		user_info.setCountry(user_info_dto.getCountry());
		user_info.setEmail(user_info_dto.getEmail());
		user_info.setFax_No(user_info_dto.getFaxNo());
		user_info.setFirm_id(user_info_dto.getFirm_id());
		user_info.setFirm_Name(user_info_dto.getNameOfFirm());
		user_info.setFirst_Name(user_info_dto.getFirstName());
		user_info.setLast_Name(user_info_dto.getLastName());
		user_info.setMiddle_initial(user_info_dto.getMiddle_initial());
		user_info.setPhone_No(user_info_dto.getPhoneNo());
		user_info.setPrefix(user_info_dto.getPrefix());
		user_info.setRole_id(user_info_dto.getEpds_role_id());
		user_info.setState(user_info_dto.getState());
		user_info.setSuffix(user_info_dto.getSuffix());
		user_info.setUser_Id(user_info_dto.getUser_id() + "");
		user_info.setZip_Code(user_info_dto.getZipCode());

		return user_info;
	}

	
	public static User_Info getPopulatedUserInfoDTOFromUserInfo(User_Info user_info, User_info_dto user_info_dto) {
		
		
		user_info_dto.setAddress1(user_info.getAddress1());
		user_info_dto.setAddress2(user_info.getAddress2());
		user_info_dto.setZipCode(user_info.getZip_Code());
		user_info_dto.setCity(user_info.getCity());
		user_info_dto.setCountry(user_info.getCountry());
		user_info_dto.setState(user_info.getState());
		
		user_info_dto.setEmail(user_info.getEmail());
		user_info_dto.setFaxNo(user_info.getFax_No());
		user_info_dto.setFirm_id(user_info.getFirm_id());
		user_info_dto.setNameOfFirm(user_info.getFirm_Name());
		user_info_dto.setFirstName(user_info.getFirst_Name());
		user_info_dto.setLastName(user_info.getLast_Name());
		user_info_dto.setMiddle_initial(user_info.getMiddle_initial());
		user_info_dto.setPhoneNo(user_info.getPhone_No());
		user_info_dto.setPrefix(user_info.getPrefix());
		user_info_dto.setSuffix(user_info.getSuffix());
		user_info_dto.setUser_id(Integer.valueOf(user_info.getUser_Id()));
		user_info_dto.setEpds_role_id(user_info.getRole_id());

		return user_info;
	}
	public static String getStackTraceMessage(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		e.printStackTrace(pw);

		return pw.toString();
	}

	public static String getBNumberWithBDashPrefix(String b_No) {
		try {
			String prefix = b_No.substring(0, 2);

			if (!prefix.equalsIgnoreCase("B-")) {
				b_No = "B-" + b_No;
			}
		} catch (Exception e) {

		}

		return b_No;
	}

	public static String stripBDashPrefix(String b_No) {
		try {
			String firstTwoPrefix = b_No.substring(0, 2);
			if (firstTwoPrefix.equalsIgnoreCase("B-")) {
				b_No = b_No.substring(2, b_No.length());
			}
		} catch (Exception e) {

		}

		return b_No;
	}

	public static String addSemicolonSeparatedText(String mainText,
			String toBeAddedText) {
		if (mainText == null)
			mainText = "";

		if (mainText.equals("")) {
			mainText += toBeAddedText;
		} else {
			mainText += "; " + toBeAddedText;
		}

		return mainText;
	}

	public static <T> Object convertToObject(String responseInJson,
			boolean isListType, Class<T> t) throws JsonParseException,
			JsonMappingException, IOException {
		Object obj = null;

		ObjectMapper mapper = new ObjectMapper();
		if (isListType) {
			obj = mapper.readValue(responseInJson,
					new TypeReference<List<T>>() {
					});
		} else {
			obj = mapper.readValue(responseInJson, t);
		}

		return obj;
	}

	public static String getRemoteIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public static boolean checkIfElementIsAlreadyInList(String element,
			List<String> listOfElements) {
		boolean returnValue = false;

		for (String eachEmail : listOfElements) {
			if (element.equalsIgnoreCase(eachEmail)) {
				returnValue = true;
				break;
			}
		}

		return returnValue;

	}

	public static String getRoleForDocumentSubmission(String userProtestRole) {
		String roleForDocumentSubmission;

		switch (userProtestRole) {
		case "PROTESTER":
		case "SECONDARY PROTESTER":
			roleForDocumentSubmission = "PROTESTER";
			break;
		case "INTERVENOR":
		case "SECONDARY INTERVENOR":
			roleForDocumentSubmission = "INTERVENOR";
			break;
		case "GAO ATTORNEY":
		case "GAO SUPERVISOR":
		case "GAO ADMIN":
		case "GAO":
			roleForDocumentSubmission = "GAO";
			break;
		case "AGENCY ATTORNEY":
		case "AGENCY ADMIN":
		case "AGENCY":
			roleForDocumentSubmission = "AGENCY";
			break;
		default:
			roleForDocumentSubmission = "PROTESTER";
		}

		return roleForDocumentSubmission;
	}

	public static void setIsUserConsolidated(Protest_Info protestInfo,
			User_Protest_Role_Bridge userProtestRoleBridge) {
		if (userProtestRoleBridge.getConsolidated_A_No() == null
				|| userProtestRoleBridge.getConsolidated_A_No().equals("")) {
			protestInfo.setIsUserConsolidated("N");
		} else {
			protestInfo.setIsUserConsolidated("Y");
		}
	}

	public static void setCompanyNameUserIsRepresentingTo(
			Protest_Info protestInfo,
			User_Protest_Role_Bridge userProtestRoleBridge,
			String eachUserRoleDesc) {
		if (eachUserRoleDesc.equalsIgnoreCase("PROTESTER")
				|| eachUserRoleDesc.equalsIgnoreCase("SECONDARY PROTESTER")) {
			protestInfo.setCompanyNameUserRepresentingTo(protestInfo
					.getCompany_Name());
		} else if (eachUserRoleDesc.equalsIgnoreCase("INTERVENOR")
				|| eachUserRoleDesc.equalsIgnoreCase("SECONDARY INTERVENOR")) {
			protestInfo.setCompanyNameUserRepresentingTo(userProtestRoleBridge
					.getIntervenor_Company_Name());
		}else if (eachUserRoleDesc.equalsIgnoreCase("AGENCY ADMIN")
				|| eachUserRoleDesc.equalsIgnoreCase("AGENCY ATTORNEY")) {
			protestInfo.setCompanyNameUserRepresentingTo(userProtestRoleBridge
					.getIntervenor_Company_Name());
		}

	}

	public static void setRoleAndRoleIdForNonUserIdBasedRole(
			Protest_Info protestInfo, Integer role_id) {
		switch (role_id) {
		case 5:
			protestInfo.setRoleId(5);
			protestInfo.setRole("AGENCY ADMIN");
			break;
		case 7:
			protestInfo.setRoleId(7);
			protestInfo.setRole("GAO ADMIN");
			break;
		case 8:
			protestInfo.setRoleId(8);
			protestInfo.setRole("GAO SUPERVISOR");
		case 3:
			protestInfo.setRoleId(3);
			protestInfo.setRole("GAO ATTORNEY");
		}
	}

	public static void setOtherTransientAttributestBasedOnUserProtestRoleBridge(
			Protest_Info mainProtestInfo,
			User_Protest_Role_Bridge userProtestRoleBridge, User_Info userInfo) {
		
		if (userProtestRoleBridge != null) {
			mainProtestInfo.setRoleId(userProtestRoleBridge.getRole_Id());
			mainProtestInfo.setRole(userProtestRoleBridge.getRoleDesc());
			mainProtestInfo.setIsUserAdmittedToPO(userProtestRoleBridge.getPo()
					.trim());
			setIsUserConsolidated(mainProtestInfo, userProtestRoleBridge);
			setCompanyNameUserIsRepresentingTo(mainProtestInfo,
					userProtestRoleBridge, userProtestRoleBridge.getRoleDesc());
			
		} else {
			setRoleAndRoleIdForNonUserIdBasedRole(mainProtestInfo,
					userInfo.getRole_id());
			mainProtestInfo.setIsUserAdmittedToPO("Y");
			mainProtestInfo.setIsUserConsolidated("N");
		}
		
		//this is mainly helpful for User roles that doesn'y have UPRB records 
		setEmailPreference(mainProtestInfo, userProtestRoleBridge,userInfo);
	}

	private static void setEmailPreference(Protest_Info mainProtestInfo,
			User_Protest_Role_Bridge userProtestRoleBridge, User_Info userInfo) {
		
		
		String emailPreference = "";
		
		
		if (userInfo.getRole_id().equals(5) 
				|| userInfo.getRole_id().equals(8)
				|| userInfo.getRole_id().equals(7)){
			
			
			if ((null != userInfo.getGlobalEmailPref() && "Y".equalsIgnoreCase(userInfo.getGlobalEmailPref())) 
					|| userInfo.getRole_id().equals(7)){
				String[] arrayOfANos = userInfo.getaNumNotifications() != null ? userInfo.getaNumNotifications().split(";") : new String [0];

				Set<String> setOfAnos = new HashSet<String>(Arrays.asList(arrayOfANos));

				if (!setOfAnos.isEmpty() && setOfAnos.contains(mainProtestInfo.getA_No())){
					emailPreference = "Y";
					
				}else{
					emailPreference = "N";
				}
			}else if (userInfo.getCds_preferences() != null 
					&& !userInfo.getCds_preferences().equalsIgnoreCase("")) {
				
				String[] arrayOfANos = userInfo.getCds_preferences().split(";");

				Set<String> setOfAnos = new HashSet<String>(Arrays.asList(arrayOfANos));
				
				if (setOfAnos.contains(mainProtestInfo.getA_No())){
					emailPreference = "N";
				}
			}
				
				
		}
		
		
		if (userProtestRoleBridge !=null){
			emailPreference = userProtestRoleBridge
					.getCasedocket_email_preferences();
		}
		
		if (emailPreference == null || emailPreference.equalsIgnoreCase("")) {
			emailPreference = "Y";
		}
		

		mainProtestInfo.setCasedocket_email_preferences(emailPreference);

	}
	
	
	public static <T>List<List<T>> partitionList( final List<T> ls, final int iParts )
	{
	    final List<List<T>> lsParts = new ArrayList<List<T>>();
	    final int iChunkSize = ls.size() / iParts;
	    int iLeftOver = ls.size() % iParts;
	    int iTake = iChunkSize;

	    for( int i = 0, iT = ls.size(); i < iT; i += iTake )
	    {
	        if( iLeftOver > 0 )
	        {
	            iLeftOver--;

	            iTake = iChunkSize + 1;
	        }
	        else
	        {
	            iTake = iChunkSize;
	        }

	        lsParts.add( new ArrayList<T>( ls.subList( i, Math.min( iT, i + iTake ) ) ) );
	    }

	    return lsParts;
	}

}
