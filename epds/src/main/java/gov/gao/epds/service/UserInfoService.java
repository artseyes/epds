package gov.gao.epds.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import gov.gao.epds.dao.Agency_Info_DAO;
import gov.gao.epds.dao.File_Info_DAO;
import gov.gao.epds.dao.Protest_Info_DAO;
import gov.gao.epds.dao.User_Info_DAO;
import gov.gao.epds.dto.CompanyInfo;
import gov.gao.epds.dto.EmailNotification;
import gov.gao.epds.dto.LoginDTO;
import gov.gao.epds.dto.User_info_dto;
import gov.gao.epds.enums.UserRoles;
import static gov.gao.epds.enums.UserRoles.*;
import gov.gao.epds.persistence.entity.Agency_Info;
import gov.gao.epds.persistence.entity.File_Info;
import gov.gao.epds.persistence.entity.GAO_User;
import gov.gao.epds.persistence.entity.Invited_User;
import gov.gao.epds.persistence.entity.Protest_Info;
import gov.gao.epds.persistence.entity.Tier_1_Agency;
import gov.gao.epds.persistence.entity.Tier_2_Agency;
import gov.gao.epds.persistence.entity.User_Info;
import gov.gao.epds.persistence.entity.User_Protest_Role_Bridge;
import gov.gao.epds.rest.auth.services.AuthUtil;
import gov.gao.epds.session.EpdsSession;
import gov.gao.epds.utils.GlobalParams;
import gov.gao.epds.utils.Util;

@Service
public class UserInfoService {
	@Autowired
	private Protest_Info_DAO protest_Info_DAO;
	@Autowired
	private User_Info_DAO user_Info_DAO;
	@Autowired
	private Agency_Info_DAO agency_Info_DAO;
	@Autowired
	private File_Info_DAO file_Info_DAO;
	
	
	@Autowired
	private DashboardService dashboardService;
	
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	RestTemplate restTemplate;


	@Autowired
	AuthUtil authUtil;
	

	public User_Info getUserInfoByUsername(String username) throws Exception {
		return user_Info_DAO.getUser_Info_By_User_Id(username);
	}

	public User_Info saveProfileInfo(User_info_dto user_info_dto)
			throws Exception {

		return user_Info_DAO.saveProfileInfo(user_info_dto);
	}

	public <T> T save(T t) {
		return user_Info_DAO.save(t);
	}

	public User_Info updatePersonalInfo(User_info_dto user_Info_DTO)
			throws Exception {

		return user_Info_DAO.updateBasicInfo(user_Info_DTO);
	}
	
	
	public List<User_Info> getSecondaryAgencyUserInfoList(Protest_Info protest_Info)
			throws Exception {

		List<Integer> agencyInfoIds = dashboardService.getListOfAssociatedAgencyIdsByAgencyInfoId(protest_Info.getAgency_Info_Id(), true);
		return user_Info_DAO.getSecondaryAgencyUser_Info_List(protest_Info.getA_No(), agencyInfoIds);
	}
	
	

	@Transactional
	public void updateEmailAddress(String oldEmail, String newEmail,User_Info user_Info)  throws Exception{
 		List<Invited_User> invitedUserInfoList = new ArrayList<Invited_User>();
		List<Protest_Info> listOfProtestInfoByEmail = null;
		


		invitedUserInfoList = user_Info_DAO.getInvitedUserInfoListByEmail(oldEmail);
		
		for (Invited_User each : invitedUserInfoList){
			each.setInvitee_Email(newEmail);
			user_Info_DAO.update(each);
		}
		
		listOfProtestInfoByEmail = protest_Info_DAO.getListOfProtestInfoByEmail(oldEmail);
		
		for (Protest_Info eachProtestInfo : listOfProtestInfoByEmail){
			eachProtestInfo.setRepresentative_Email(newEmail);
			protest_Info_DAO.updateProtest_Info(eachProtestInfo);
		}
		
		user_Info.setEmail(newEmail.toLowerCase(Locale.ENGLISH));
		user_Info_DAO.update(user_Info);

		return;
	}
	
	
	@Transactional
	public void updateUserId(String newUserId, String email) throws Exception {
		List<Invited_User> invitedUserInfoList = new ArrayList<Invited_User>();
		List<File_Info> listOfFileInfo = null;
		List<User_Info> listOfUserInfo = null;
		List<User_Protest_Role_Bridge> uprbs = null;
		
		
		listOfUserInfo = user_Info_DAO.getListOfUserInfoByEmail(email);
		if (listOfUserInfo != null && listOfUserInfo.size() <= 1){
			return;
		}
		
		for (User_Info eachUserInfo : listOfUserInfo){
			
			
			if (eachUserInfo.getUser_Id().equalsIgnoreCase(newUserId)){
				continue;
			}
			
			String oldUserId = eachUserInfo.getUser_Id();
			invitedUserInfoList = user_Info_DAO.getListOfInvitedUsersbyUserId(oldUserId);
			listOfFileInfo = file_Info_DAO.getListOfFileInfoBySubimtterUserId(oldUserId);
			uprbs = user_Info_DAO.getUser_Protest_Role_Bridge_List_BasedOnUser_Id(oldUserId);

			// update invited user table
			if (null != invitedUserInfoList && !invitedUserInfoList.isEmpty()) {

				for (Invited_User each : invitedUserInfoList) {

					if (each.getInvitee_Id().equalsIgnoreCase(oldUserId)) {
						each.setInvitee_Id(newUserId);
					}

					if (each.getInviter_Id().equalsIgnoreCase(oldUserId)) {
						each.setInviter_Id(newUserId);
					}
					user_Info_DAO.update(each);
				}
			}

			// update file info table

			if (null != listOfFileInfo && !listOfFileInfo.isEmpty()) {

				for (File_Info eachFileInfo : listOfFileInfo) {

					// this is not required just kept it in place
					if (eachFileInfo.getSubmitter_User_Id().equalsIgnoreCase(oldUserId)) {
						eachFileInfo.setSubmitter_User_Id(newUserId);
					}

					file_Info_DAO.update(eachFileInfo);
				}
			}

			// update user protest role bridge
			if (null != uprbs && !uprbs.isEmpty()) {

				for (User_Protest_Role_Bridge eachUprb : uprbs) {

					// this is not required just kept it in place
					if (eachUprb.getUser_Id().equalsIgnoreCase(oldUserId)) {
						eachUprb.setUser_Id(newUserId);
					}

					user_Info_DAO.update(eachUprb);
				}
			}
			
			
			user_Info_DAO.delete(eachUserInfo);
		}

		
	

		return;
	}
	
	private List<User_Info> get_Invited_User_Info_List(String a_No,
			String inviter_Type, String intervenorCompanyName, String intervenorCompanyAddr) {
		List<User_Info> invitedOrAlreadyMemberUserInfoList = new ArrayList<User_Info>();

		List<User_Info> secondary_User_InfoList = new ArrayList<User_Info>();
		List<User_Info> unapprovedSecondaryUserInfoList =new ArrayList<User_Info>();
		if (inviter_Type.equals("protester")
				|| inviter_Type.equalsIgnoreCase("intervenor")) {
			Integer secondaryRoleId = findSecondaryRoleIdBasedOnInviterType(inviter_Type);

			if (inviter_Type.equals("protester") || intervenorCompanyName == null){
				secondary_User_InfoList = user_Info_DAO
						.getSecondary_User_Info_List(a_No, secondaryRoleId);
				
				unapprovedSecondaryUserInfoList = user_Info_DAO
						.getUnapprovedSecondaryUserInfoList(a_No, inviter_Type);
				
			}else if (inviter_Type.equals("intervenor")){
				secondary_User_InfoList = user_Info_DAO
						.getSecondary_User_Info_ListByIntervenorCompanyName(a_No, secondaryRoleId, intervenorCompanyName);
				
				unapprovedSecondaryUserInfoList = user_Info_DAO
						.getUnapprovedSecondaryUserInfoListByCompanyName(a_No, inviter_Type, intervenorCompanyName);
				
			}
			
			
			secondary_User_InfoList.addAll(unapprovedSecondaryUserInfoList);
			invitedOrAlreadyMemberUserInfoList = secondary_User_InfoList;

			
		} else {
			List<User_Info> agency_AttorneyList = user_Info_DAO
					.getAgencyRepUserInfoListByANum(a_No);
			invitedOrAlreadyMemberUserInfoList = agency_AttorneyList;
		}

		return invitedOrAlreadyMemberUserInfoList;
	}

	
	public List<String> getListOfAllAgencyPOCEmailAddressesByUserId(String userId) throws Exception{
		
		User_Info  user_Info = user_Info_DAO.getUser_Info_By_User_Id(userId);
		List<String> allAgencyPocEmailAddresses = getListAgencyPocEmailAddressesByFirmId(user_Info.getFirm_id());
		
		return allAgencyPocEmailAddresses;
	}

	/**
	 * @param firmId
	 * @return
	 * @throws Exception
	 */
	public List<String> getListAgencyPocEmailAddressesByFirmId(Integer firmId) throws Exception {
		List<String> allAgencyPocEmailAddresses = new ArrayList<String>();
		List<User_Info> userInfos = getListOfAgencyPOCUserInfoByFirmId(firmId);// come back to this I think we can just query userInfo table with roleId and firmId and we dont really need to query agencyInfo to get userIds
		for (User_Info eachUserInfo : userInfos){
			allAgencyPocEmailAddresses.add(eachUserInfo.getEmail());
		}
		return allAgencyPocEmailAddresses;
	}

	/**
	 * @param firmId
	 * @return
	 * @throws Exception
	 */
	public List<User_Info> getListOfAgencyPOCUserInfoByFirmId(Integer firmId) throws Exception {
		List<Integer> agencyInfoIds = dashboardService.getListOfAssociatedAgencyIdsByAgencyInfoId(firmId,false);
		List<User_Info> userInfos = user_Info_DAO.getListOfAgencyUserInfoByAgencyInfoIdsAndRoleId(agencyInfoIds, 5);
		return userInfos;
	}
	
	
	public void updateAgencyUserInfo(
			HttpServletRequest request, User_info_dto userInfoDTO) throws Exception {

		Integer agencyInfoId = getAgencyId(userInfoDTO.getTier1_agency_id(), userInfoDTO.getTier2_agency_id());
		
		if (agencyInfoId == null){
			return;
		}
		
		String toAgencyName = getAgencyNamebyAgencyInfoId(agencyInfoId);
		User_Info agencyUserInfo = user_Info_DAO.getUser_Info_ByEmail(userInfoDTO.getEmail());

		if (agencyUserInfo.getRole_id().equals(AGENCY_ADMIN.getCode()) && userInfoDTO.getEpds_role_id().equals(AGENCY_ATTORNEY.getCode()) ) {
			removeAgencyPOC(agencyUserInfo);
		} else if (agencyUserInfo.getRole_id().equals(AGENCY_ATTORNEY.getCode()) && userInfoDTO.getEpds_role_id().equals(AGENCY_ADMIN.getCode()) ) {
			addAgencyPOC(userInfoDTO.getUser_id().toString(), userInfoDTO.getFirm_id());
		} else if (!agencyUserInfo.getFirm_id().equals(userInfoDTO.getFirm_id()) && userInfoDTO.getEpds_role_id().equals(AGENCY_ADMIN.getCode()) && agencyUserInfo.getRole_id().equals(AGENCY_ADMIN.getCode()) ) {
			removeAgencyPOC(agencyUserInfo);
			addAgencyPOC(userInfoDTO.getUser_id().toString(), userInfoDTO.getFirm_id());
		}

		if (userInfoDTO.getTypeOfUpdate().equals("roleUpdate")) {
			agencyUserInfo.setRole_id(userInfoDTO.getEpds_role_id());
		}
		agencyUserInfo.setFirm_id(agencyInfoId);
		agencyUserInfo.setFirm_Name(toAgencyName);
		
		userInfoDTO.setFirm_id(agencyInfoId);
		userInfoDTO.setNameOfFirm(toAgencyName);

		user_Info_DAO.update(agencyUserInfo);

		String jsonResponse = authUtil.getAuthJSONResponse("epds-auth-util-api",null, userInfoDTO, request);

		return;
	}
	
	public void updateAgencyName(Integer firmId,String agencyNameCorrection) throws Exception {
		String RESTURL = "http://localhost:8080/epds-auth/util/";// temp URL
		
		List<User_Info> agencyUserInfo = user_Info_DAO.getListOfAlUsersByAgencyFirmName(firmId);
		Agency_Info agency_Info  = agency_Info_DAO.getAgency_Info(firmId);
		
		if (agency_Info.getTier().equalsIgnoreCase("1")){
			Tier_1_Agency tier1Agency = agency_Info_DAO.getAgencyTier1ByAgencyId(agency_Info.getAgency_Id());
			tier1Agency.setAgency_Name(agencyNameCorrection);
			
			agency_Info_DAO.update(tier1Agency);
            
		}else if (agency_Info.getTier().equalsIgnoreCase("2")){
			Tier_2_Agency tier2Agency = agency_Info_DAO.getAgencyTier2ByAgencyId(agency_Info.getAgency_Id());
			tier2Agency.setAgency_Name(agencyNameCorrection);
			agency_Info_DAO.update(tier2Agency);
		}
		
		if (agencyUserInfo != null){
			
			for (User_Info user_Info : agencyUserInfo) {
				user_Info.setFirm_Name(agencyNameCorrection);
				user_Info_DAO.update(user_Info);
			}
		}
	
		
		/*restTemplate.put(RESTURL, agencyUserInfo);*/
		
		
		return;
	}
	
	private Integer findSecondaryRoleIdBasedOnInviterType(String inviter_Type) {
		Integer role_id = 0;

		if (inviter_Type.equalsIgnoreCase("protester")) {
			role_id = 4;
		} else if (inviter_Type.equalsIgnoreCase("intervenor")) {
			role_id = 9;
		}

		return role_id;
	}

	public LoginDTO getEmail(LoginDTO loginDTO){
        
        try {
        	byte[] base64decodedBytes = Base64.getDecoder().decode(loginDTO.getEmail());
            loginDTO.setEmail(new String(base64decodedBytes, "utf-8"));
            
         }catch(UnsupportedEncodingException e){
        	 
         }
        
        return loginDTO;
        
	}

	/**
	 * Things to do :
	 * 1) Refactor the code : We dont need three if loops over here. Do the same way we did it for assign user role below 
	 * 2) Implement the logic to add secondary representatives to all the cases when it is joined.
	 * 
	 * 	Pseudo Code 
	 *  Since, we have company name and company Address of Intervenor, 
	 *  we can just query the User Protest Role Bridge by company name, company address  and a_No  and consolidateA_No should be null to check primary intervenor 
	 *  if the user protest role bridge exists for the primary then add the secondary User to that a_No
	 *  This will take care of the situation where if intervenor was approved before joining the case then they will be consolidated user to other cases
	 *  but if the request to intervene was approved after joining the cases then they are added to all the case with full access. 
	 *  
	 * @param a_No
	 * @param secondary_User_Id
	 * @param response
	 * @throws Exception
	 */
	public void acceptOrRejectInvitation(String a_No, String secondary_User_Id,
			String response) throws Exception {
		
		
		Invited_User invited_User = getSecondaryUser(a_No, secondary_User_Id,
				"INVITED");

		
		String status = "ACCEPTED";
		
		if (!"reject".equalsIgnoreCase(response)) {
			List<Protest_Info> listOfConsolidatedCases = protest_Info_DAO.getConsolidatedProtestInfoListWithParentAndChildSupplementalProtests(a_No);
			
			Protest_Info currentProtestInfo = listOfConsolidatedCases.get(0);
			User_Protest_Role_Bridge intervenorUPRB = null;
			
			if (invited_User.getInviter_Type().equalsIgnoreCase("intervenor")){
				
				intervenorUPRB = user_Info_DAO.
							getIntervenorUPRBByIntervenorCompanyNameAndIntervenorCompanyAddress(a_No,invited_User.getCompany_name(),invited_User.getCompany_Address());
			}
			
			for (Protest_Info eachProtestInfo : listOfConsolidatedCases){
				
				
				if (invited_User.getInviter_Type().equalsIgnoreCase("intervenor")){

					if (intervenorUPRB != null){
					List<User_Protest_Role_Bridge> listOfIntervenorUPRB = user_Info_DAO.getUser_Protest_Role_Bridge_List_BasedOnUser_IdAndProtestId(intervenorUPRB.getUser_Id(), eachProtestInfo.getA_No());
					
					if (listOfIntervenorUPRB != null){
						
						for (User_Protest_Role_Bridge eachUPRB: listOfIntervenorUPRB){
							
							User_Protest_Role_Bridge intervenorSecondaryUserProtestRoledBridge = new User_Protest_Role_Bridge();
							
							intervenorSecondaryUserProtestRoledBridge.setA_No(eachUPRB.getA_No());
							intervenorSecondaryUserProtestRoledBridge.setPo("N");
							intervenorSecondaryUserProtestRoledBridge.setUser_Id(secondary_User_Id);
							
							populateUserProtestRoleBridgeBasedOnInviterType(
									intervenorSecondaryUserProtestRoledBridge, invited_User,
									invited_User.getCompany_name(),
									invited_User.getCompany_Address());
							
							intervenorSecondaryUserProtestRoledBridge.setConsolidated_A_No(eachUPRB.getConsolidated_A_No());
							
							user_Info_DAO.save(intervenorSecondaryUserProtestRoledBridge);
							}
						}	
					}
					
				}else{
				
				User_Protest_Role_Bridge secondaryUserProtestRoledBridge = new User_Protest_Role_Bridge();
				
				secondaryUserProtestRoledBridge.setA_No(eachProtestInfo.getA_No());
				secondaryUserProtestRoledBridge.setPo("N");
				secondaryUserProtestRoledBridge.setUser_Id(secondary_User_Id);
				populateUserProtestRoleBridgeBasedOnInviterType(
						secondaryUserProtestRoledBridge, invited_User,
						invited_User.getCompany_name(),
						invited_User.getCompany_Address());
				if (eachProtestInfo.getA_No().equalsIgnoreCase(a_No) 
						&& !invited_User.getInviter_Type().equalsIgnoreCase("intervenor")){
					
					user_Info_DAO.save(secondaryUserProtestRoledBridge);
				}else if (eachProtestInfo.getParent_A_No() != null 
						&& !eachProtestInfo.getA_No().equalsIgnoreCase(a_No) 
						&& !invited_User.getInviter_Type().equalsIgnoreCase("intervenor")){
					secondaryUserProtestRoledBridge.setConsolidated_A_No(eachProtestInfo.getParent_A_No());
					user_Info_DAO.save(secondaryUserProtestRoledBridge);
				}else if (eachProtestInfo.getParent_A_No() == null 
						&& !eachProtestInfo.getA_No().equalsIgnoreCase(a_No) 
						&& !invited_User.getInviter_Type().equalsIgnoreCase("intervenor")){
					secondaryUserProtestRoledBridge.setConsolidated_A_No(currentProtestInfo.getA_No());
					user_Info_DAO.save(secondaryUserProtestRoledBridge);
				}
				
			}
		}
			/*Protest_Info protest_Info = getProtest_Info(a_No);

			User_Protest_Role_Bridge secondaryUserProtestRoledBridge = new User_Protest_Role_Bridge();
			secondaryUserProtestRoledBridge.setA_No(a_No);
			secondaryUserProtestRoledBridge.setPo(protest_Info.getPo());
			secondaryUserProtestRoledBridge.setUser_Id(secondary_User_Id);
			populateUserProtestRoleBridgeBasedOnInviterType(
					secondaryUserProtestRoledBridge, invited_User,
					invited_User.getCompany_name(),
					invited_User.getCompany_Address());

			user_Info_DAO.save(secondaryUserProtestRoledBridge);*/
		} else {
			status = "REJECTED";
		}

		invited_User.setStatus(status);
		user_Info_DAO.update(invited_User);
	}

	private void populateUserProtestRoleBridgeBasedOnInviterType(
			User_Protest_Role_Bridge secondaryUserProtestRoleBridge,
			Invited_User invited_User, String intervenorCompanyName,
			String intervenorCompanyAddress) {
		String inviterType = invited_User.getInviter_Type();
		/*String caseA_no = invited_User.getA_No();
		String inviterUserId = invited_User.getInviter_Id(); // reminder: it might make sense to rename Invited_User table Invite
*/
		if (inviterType.equalsIgnoreCase("protester")) {
			secondaryUserProtestRoleBridge.setRole_Id(4);
		} else if (inviterType.equalsIgnoreCase("intervenor")) {
			secondaryUserProtestRoleBridge.setRole_Id(9);
			secondaryUserProtestRoleBridge
					.setIntervenor_Company_Name(intervenorCompanyName);
			secondaryUserProtestRoleBridge
					.setIntervenor_Company_Address(intervenorCompanyAddress);
			/*
			 * User_Protest_Role_Bridge userProtestRoleBridgeOfPrimaryIntervenor
			 * = user_Info_DAO.getUser_Protest_Role_Bridge(caseA_no,
			 * inviterUserId);
			 * secondaryUserProtestRoleBridge.setIntervenor_Company_Name
			 * (userProtestRoleBridgeOfPrimaryIntervenor
			 * .getIntervenor_Company_Name());
			 * secondaryUserProtestRoleBridge.setIntervenor_Company_Address
			 * (userProtestRoleBridgeOfPrimaryIntervenor
			 * .getIntervenor_Company_Address());
			 */
		}

	}

	

	public void inviteSecondaryUser(String a_No, String primary_Id,
			String secondary_Id, String secondary_Email, String inviter_Type,
			String intervenorCompanyName, String intervenorCompanyAddr) {
		Invited_User invited_User = new Invited_User();
		invited_User.setA_No(a_No);
		invited_User.setInviter_Id(primary_Id);
		invited_User.setInvitee_Id(secondary_Id);
		invited_User.setStatus("INVITED");
		invited_User.setInvitee_Email(secondary_Email);
		invited_User.setInviter_Type(inviter_Type);

		if (inviter_Type.equalsIgnoreCase("intervenor")) {
			invited_User.setCompany_name(intervenorCompanyName);
			invited_User.setCompany_Address(intervenorCompanyAddr);
		}

		user_Info_DAO.save(invited_User);
	}

	public Invited_User getSecondaryUser(String invite_A_No,
			String secondary_Id, String status) {
		return user_Info_DAO.getSecondaryProtester(invite_A_No, secondary_Id,
				status);
	}

	/**
	 * find out if it is 'same email'/'not existing email'/'limit
	 * crossed'/'already invited'/'valid'
	 * 
	 * Amer : Need to add validation based on secondary agency 
	 * 
	 * Currently the logic is based on the assumption there is only one agency.
	 * Since now we can potentially have two agencies
	 * In the invited_User_InfoList when the inviter type is agency we get all the list of users with role Id 6
	 * I will need to separate that list in two one for primary agency and other for secondary agency then check for the limit by using the firm name from invitee_User_Info
	 * 
	 */
	
	/**
	 * @param a_No : The epds control number associated with this case
	 * @param invitee_Email : Email address of the user that being invited
	 * @param inviterUserInfo : User Info of the user who is inviting
	 * @param inviter_Type : The type of invitation it can be protester, intervenor or agency-attorney or SA(secondary Agency)
	 * @param invitee_User_Info : User Info of the user being invited
	 * @param intervenorCompanyAddr 
	 * @param intervenorCompanyName 
	 * @return
	 * @throws Exception 
	 */
	public String getValidation(String a_No, String invitee_Email,
			User_Info inviterUserInfo, String inviter_Type,User_Info invitee_User_Info, String intervenorCompanyName, String intervenorCompanyAddr) throws Exception {
		
		// check if it is same email of no email
		if (!invitee_Email.equals("")) {
			UserRoles role = UserRoles.getByCode(inviterUserInfo.getRole_id());
			if (invitee_Email.equals(inviterUserInfo.getEmail()) && role != AGENCY_ADMIN)
				return "sameEmail";
			if (!user_Info_DAO.isEmailValid(invitee_Email))
				return "noSuchEmail";
		}

		List<User_Info> invited_User_InfoList = get_Invited_User_Info_List(
				a_No, inviter_Type,intervenorCompanyName,intervenorCompanyAddr);

		//Agency Rep always belongs to same agency
	 
		if (!inviter_Type.equalsIgnoreCase("protester")
				&& !inviter_Type.equalsIgnoreCase("intervenor")){
		 
		/* invited_User_InfoList = new ArrayList<User_Info>();*/
		 invited_User_InfoList = this.getListOfAgencyReps(invited_User_InfoList, invitee_User_Info);
		 
		}
		
		int totalLimit = 0;
		
		if (inviter_Type.equalsIgnoreCase("protester")
				|| inviter_Type.equalsIgnoreCase("intervenor")) {
			totalLimit = 9;
		} else {
			totalLimit = 10;

			try {
				Protest_Info protestInfo = protest_Info_DAO.getProtestByA_no(a_No);
				
				List<Integer> agencyInfoIdsBasedOnProtestAgencyId = dashboardService.getListOfAssociatedAgencyIdsByAgencyInfoId(protestInfo.getAgency_Info_Id(),true);
				
				if (invitee_User_Info.getFirm_id() == null){
					return "invalidAgency";
					
				}else if ((inviter_Type.equalsIgnoreCase("agency-attorney") 
						&& (!agencyInfoIdsBasedOnProtestAgencyId.contains(invitee_User_Info.getFirm_id())))
											 ||
						(inviter_Type.equalsIgnoreCase("secondary-agency")  
						&& !invited_User_InfoList.isEmpty()
						&& (!dashboardService.getListOfAssociatedAgencyIdsByAgencyInfoId(invitee_User_Info.getFirm_id(),false).contains(invited_User_InfoList.get(0).getFirm_id()) )
						)) {
					
					return "notFromSameAgency";
				}else if ((inviter_Type.equalsIgnoreCase("secondary-agency") 
						&& (agencyInfoIdsBasedOnProtestAgencyId.contains(invitee_User_Info.getFirm_id())))) {
					
					return "notASecondaryAgency";
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (invited_User_InfoList.size() >= totalLimit)
			return "limitCrossed";

		for (User_Info eachUser_Info : invited_User_InfoList) {
			if (eachUser_Info.getEmail().equals(invitee_Email))
				return "alreadyInvited";
		}

		//check this user has already been invited or has access to this protest through consolidates cases
		List<Protest_Info> listOfConsolidatedProtestInfo = protest_Info_DAO.getConsolidatedProtestInfoListWithParentAndChildSupplementalProtests(a_No);
		List<User_Protest_Role_Bridge> userProtestRoleBridgeByUsedIdandANum = new ArrayList<User_Protest_Role_Bridge>();
		for (Protest_Info eachProtestInfo : listOfConsolidatedProtestInfo){
			Invited_User checkIfthiUserAlreadyHasAccessToThisProtest = user_Info_DAO.getSecondaryProtesterByANumberUserIdAndStatus(eachProtestInfo.getA_No(),invitee_User_Info.getUser_Id());
			userProtestRoleBridgeByUsedIdandANum = user_Info_DAO.getUser_Protest_Role_Bridge_List_BasedOnUser_IdAndProtestId(invitee_User_Info.getUser_Id(), eachProtestInfo.getA_No());

			// Needs to be done this way so a Agency POC/Admin is able to be added to a case as an Attorney even though
			// they already have access. Side effect: A PLCG user can invite a Primary protester/intervenor.
			if (checkIfthiUserAlreadyHasAccessToThisProtest != null &&
				(userProtestRoleBridgeByUsedIdandANum != null && userProtestRoleBridgeByUsedIdandANum.size() > 0) ) {
				return "hasAccess";
			}
		}
		
		return "valid";
	}

	
	private List<User_Info> getListOfAgencyReps(List<User_Info> invited_User_InfoList, User_Info invitee_User_Info) throws Exception{
		List<User_Info> agencyList = new LinkedList<User_Info>();
		
		List<Integer> agencyInfoIds = dashboardService.getListOfAssociatedAgencyIdsByAgencyInfoId(invitee_User_Info.getFirm_id(),false);
		for (User_Info user_Info : invited_User_InfoList) {
			UserRoles role = UserRoles.getByCode(user_Info.getRole_id());
			if ((role == AGENCY_ADMIN || role == AGENCY_ATTORNEY)
					&& agencyInfoIds.contains(user_Info.getFirm_id())){
				agencyList.add(user_Info);
			}
		}
		return agencyList;
	}
	
	
	@Transactional
	public void removeAccessOfSecondaryUser(String a_No,
			String secondary_User_Id) throws Exception {

		List<Protest_Info> listOfConsolidatedCases = protest_Info_DAO.getConsolidatedProtestInfoListWithParentAndChildSupplementalProtests(a_No);
		
		
		
		for (Protest_Info eachProtestInfo : listOfConsolidatedCases) {
			
			Invited_User invitedUser = null;
			
			User_Protest_Role_Bridge secondary_User_Protest_Role_Bridge = user_Info_DAO
					.getUser_Protest_Role_BridgeWithoutAgencyPOCsByANumAndUserId(eachProtestInfo.getA_No(), secondary_User_Id);
			if (secondary_User_Protest_Role_Bridge != null){
				user_Info_DAO.delete(secondary_User_Protest_Role_Bridge);	
			}else {
				invitedUser = user_Info_DAO.getSecondaryProtester(eachProtestInfo.getA_No(),secondary_User_Id,"INVITED");
				
			}
			
			
			if (invitedUser == null){
				invitedUser = user_Info_DAO.getSecondaryProtester(eachProtestInfo.getA_No(),secondary_User_Id,"ACCEPTED");
				if (invitedUser != null){
					
					invitedUser.setStatus("REMOVED");
					user_Info_DAO.update(invitedUser);
				}
			}else{
				user_Info_DAO.delete(invitedUser);
			}
			}
			
		
		}

	@Transactional
	public List<File_Info> revokeIntervenorAccess(String a_No, List<File_Info> fileInfoList, CompanyInfo dto) throws Exception {
		List<Protest_Info> listOfConsolidatedCases = protest_Info_DAO.getConsolidatedProtestInfoListWithParentAndChildSupplementalProtests(a_No);
		List<File_Info> intervenorFileInfoList;
		File_Info fileInfo = null;
		
		if (dto.getIntervenorFileId() != null){
			fileInfo = file_Info_DAO.getFile_Info(String.valueOf(dto.getIntervenorFileId()));
		}else if (dto.getCompanyName() != null){
			intervenorFileInfoList = file_Info_DAO.getFile_Info_List_BasedOn_A_No_And_CompanyName(a_No, dto.getCompanyName());
			
			
			if (null != intervenorFileInfoList && intervenorFileInfoList.size() > 0){
				Optional<File_Info> matchingObject = intervenorFileInfoList.stream().
					    filter(fInfo -> fInfo.getDoc_Type_Id() == 56).
					    findFirst();
				fileInfo = matchingObject.get();
			}
			
		}
		
		
		
		
		for (Protest_Info eachProtestInfo : listOfConsolidatedCases){
			Invited_User invitedUser = new Invited_User();
		
			List<User_Protest_Role_Bridge> intervenorUprb = user_Info_DAO.
					getUprb_BasedOnIntervenorInfo(eachProtestInfo.getA_No(), fileInfo.getCompany_Name(),fileInfo.getCompany_Address());
			
			if (intervenorUprb == null || intervenorUprb.isEmpty()){
				intervenorUprb = user_Info_DAO.
						getUser_Protest_Role_Bridge_List_BasedOnUser_IdAndProtestId(fileInfo.getSubmitter_User_Id(), eachProtestInfo.getA_No());
			}
			
			User_Protest_Role_Bridge uprb;
			UserRoles role;
			
			if (intervenorUprb != null){
			
				for (int i=0 ;i< intervenorUprb.size(); i++){
					uprb = intervenorUprb.get(i);
					
					if (uprb == null){
						continue;
					}
					role = UserRoles.getByCode(uprb.getRole_Id());
					if (role == INTERVENOR || role == SECONDARY_INTERVENOR){
					
						user_Info_DAO.delete(uprb);
						
						invitedUser = user_Info_DAO.getSecondaryProtester(eachProtestInfo.getA_No(),uprb.getUser_Id(),"ACCEPTED");
							if (invitedUser != null){
								
								invitedUser.setStatus("REMOVED");
								user_Info_DAO.update(invitedUser);
							}
						}
					}
					
					
				}
			}
			
			
		
		if (fileInfoList != null){
			for (int j=0; j <fileInfoList.size();j++ ){
				if (fileInfoList.get(j).getFile_Id() == fileInfo.getFile_Id()){
					fileInfoList.remove(j--);
				}
			}
		}
		
		
		fileInfo.setIs_Intervene_Approved("N");
		fileInfo.setCase_access_request_status("N");
		
		file_Info_DAO.update(fileInfo);
		
		fileInfoList.add(fileInfo);
		
		
		return fileInfoList;
		
		
		
	}
		
		
	
	
	public File_Info updateApprovedStatus(String file_Id, String response,
			String accessType) throws Exception {
		File_Info file_Info = file_Info_DAO.getFile_Info(file_Id);

		if (response.equalsIgnoreCase("Y")) {
			boolean isUserIntervenor = false;

			if (accessType.equalsIgnoreCase("intervene")) {
				// come back to fix redundancy
				file_Info.setIs_Intervene_Approved("Y");
				isUserIntervenor = true;
			}

			protest_Info_DAO
					.approveRequestToAccess(file_Info, isUserIntervenor);
			file_Info.setCase_access_request_status("A");
		} else {
			if (accessType.equalsIgnoreCase("intervene")) {
				// come back to fix redundancy
				file_Info.setIs_Intervene_Approved("N");
			}
			file_Info.setCase_access_request_status("D");
		}

		file_Info_DAO.updateFile_Info(file_Info);

		return file_Info;
	}

	
	/*
	 *
	 * */	
	public void assignRole(String a_No, User_Info userInfo, String assignType,
			String intervenorCompanyName, String intervenorCompanyAddr)
			throws Exception {
		String userId = userInfo.getUser_Id();
		List<Protest_Info> listOfConsolidatedProtestInfo = protest_Info_DAO.getConsolidatedProtestInfoListWithParentAndChildSupplementalProtests(a_No);
		Protest_Info currentProtestInfo = listOfConsolidatedProtestInfo.get(0);

		for (Protest_Info eachProtestInfo : listOfConsolidatedProtestInfo){
			User_Protest_Role_Bridge user_Protest_Role_Bridge = new User_Protest_Role_Bridge();

			user_Protest_Role_Bridge.setA_No(eachProtestInfo.getA_No());
			user_Protest_Role_Bridge
					.setPo("N");

			if (assignType.equals("agency-attorney")) {
				user_Protest_Role_Bridge.setRole_Id(6);
				
				//by default when agency POC's are added to the case their email preferences will be set to N
				/*if (userInfo.getRole_id().equals(5)){
					user_Protest_Role_Bridge.setCasedocket_email_preferences("N");	
				}*/

				user_Protest_Role_Bridge.setIntervenor_Company_Name(userInfo.getFirm_Name());
				
			} else if (assignType.equals("primary-protester")) {
				user_Protest_Role_Bridge.setRole_Id(1);
				
				
				if (eachProtestInfo.getA_No().equalsIgnoreCase(a_No)){
					eachProtestInfo.setRepresentative_Email(userInfo.getEmail());
					protest_Info_DAO.updateProtest_Info(eachProtestInfo);
				}
				
			} else if (assignType.equals("primary-intervenor")) {
				user_Protest_Role_Bridge.setRole_Id(2);
				user_Protest_Role_Bridge
						.setIntervenor_Company_Name(intervenorCompanyName);
				user_Protest_Role_Bridge
						.setIntervenor_Company_Address(intervenorCompanyAddr);
			}

			user_Protest_Role_Bridge.setUser_Id(userId);
			
			if (!eachProtestInfo.getA_No().equalsIgnoreCase(a_No) 
					&& !assignType.equalsIgnoreCase("primary-intervenor") 
					&& !assignType.equalsIgnoreCase("agency-attorney") 
					&& eachProtestInfo.getParent_A_No() != null){
				user_Protest_Role_Bridge.setConsolidated_A_No(eachProtestInfo.getParent_A_No());
				}else if (!eachProtestInfo.getA_No().equalsIgnoreCase(a_No) 
						&& !assignType.equalsIgnoreCase("primary-intervenor") 
						&& !assignType.equalsIgnoreCase("agency-attorney") 
						&& eachProtestInfo.getParent_A_No() == null){
					user_Protest_Role_Bridge.setConsolidated_A_No(currentProtestInfo.getParent_A_No());
				}
			
			user_Info_DAO.save_Entity(user_Protest_Role_Bridge);
		
		}
		
		
	}
	

	public User_Info getUser_InfoByEmail(String email) {
		return user_Info_DAO.getUser_Info_ByEmail(email);
	}

	public List<User_Info> getUser_Info_List_AssociatedWithAgency(
			String tier1_Agency_Id, String tier2_Agency_Id) throws Exception {
		int agency_Info_Id = protest_Info_DAO.getAgency_Info_Id(
				tier1_Agency_Id, tier2_Agency_Id);
		/*Agency_Info agency_Info = agency_Info_DAO.getAgency_Info(Integer
				.valueOf(agency_Info_Id));

		if (agency_Info.getUser_Id() != null) {
			String[] agency_User_Ids = agency_Info.getUser_Id().split(";");

			List<User_Info> user_InfoList = user_Info_DAO
					.getUser_Info_List_ByArrayOfUser_Id(agency_User_Ids);

			return user_InfoList;
		}*/

		return getAgencyPOCUserInfos(agency_Info_Id);
	}

	public void deleteUser(String user_Id){
		
		List<User_Protest_Role_Bridge> user_Protest_Role_Bridge_List  = null;
		try {
		user_Protest_Role_Bridge_List = user_Info_DAO
				.getUser_Protest_Role_Bridge_List_BasedOnUser_Id(user_Id);

		if (user_Protest_Role_Bridge_List != null){
			for (User_Protest_Role_Bridge each_User_Protest_Role_Bridge : user_Protest_Role_Bridge_List) {
				user_Info_DAO.delete(each_User_Protest_Role_Bridge);
			}
		}
		User_Info user_Info = user_Info_DAO.getUser_Info_By_User_Id(user_Id);
		user_Info_DAO.delete(user_Info);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public void deleteGAO_User(String user_Id) throws Exception {
		List<GAO_User> gao_UserList = user_Info_DAO.getGAO_UserList(user_Id);

		for (GAO_User eachGAO_User : gao_UserList) {
			user_Info_DAO.delete(eachGAO_User);
		}

	}

	public void editGAO_User(User_info_dto user_info_dto) throws Exception {
		GAO_User gao_User = user_Info_DAO.getGAO_UserList(user_info_dto.getUser_id() + "").get(0);

		
		if (!gao_User.getGroup_No().equals(user_info_dto.getGroupNo())){

			List<Protest_Info> protestInfoList =  protest_Info_DAO.getProtestInfoListByUserId(String.valueOf(user_info_dto.getUser_id()));	
			
			if (null != protestInfoList && !protestInfoList.isEmpty()){
				
				for (Protest_Info  protestInfo : protestInfoList){
					protestInfo.setAttorney_Group_Id(user_info_dto.getGroupNo());
					protestInfo.setAttorney_Name(user_info_dto.getLastName() + "," + user_info_dto.getFirstName());
					protest_Info_DAO.updateProtest_Info(protestInfo);
				}
			}
		}
		
		gao_User.setGroup_No(user_info_dto.getGroupNo());
		
		gao_User.setType(user_info_dto.getRole().trim());
		
		gao_User.setTitle(user_info_dto.getTitle());

		//To update GAOID
		if(!user_info_dto.getGaoId().equals(gao_User.getId())){
			user_Info_DAO.updateGAOId(gao_User.getId(), user_info_dto.getGaoId());
			gao_User.setId(user_info_dto.getGaoId());
		}

		user_Info_DAO.update(gao_User);

	}
	
	public void updateUserInfo(User_Info user_info) throws Exception {
		user_Info_DAO.update(user_info);
	}
	public void removeAgencyPOC(User_Info user_Info) throws Exception {
		String pocUserIds;
		Agency_Info agencyInfo = agency_Info_DAO.getAgency_Info(user_Info.getFirm_id());

		pocUserIds = agencyInfo.getUser_Id();
		
		if (pocUserIds != null){
			
			List<String> userids = new ArrayList<String>(Arrays.asList(pocUserIds.split(";")));
			
			if (userids != null && !userids.isEmpty()&& userids.size() >0){
			
				for (int i=0; i <userids.size();i++) {
					String eachPOCUserId = userids.get(i);
					
					if (eachPOCUserId.equalsIgnoreCase(user_Info.getUser_Id())){
						userids.remove(i--);
					}
				}
				
				if(null != userids){
					pocUserIds = StringUtils.join(userids, ";");
					/*pocUserIds.replaceAll(user_Info.getUser_Id(), "");*/
					agencyInfo.setUser_Id(pocUserIds);	
				}
				
				
				agency_Info_DAO.updateAgencyInfo(agencyInfo);
			}
		}
		
		
	}

	public List<User_Info> getAgencyPOCUserInfos(Integer agency_id) {
		
		List<User_Info> listOfUserInfo = user_Info_DAO.getListOfAgencyUserInfoByAgencyInfoIdsAndRoleId(
				Collections.singletonList(agency_id), 5);

		if (listOfUserInfo != null && listOfUserInfo.size() > 0) {
			return listOfUserInfo;
		}

		return (new ArrayList<User_Info>());
		/*
		 * String email = ""; if (listOfUserInfo != null &&
		 * listOfUserInfo.size() > 0) { for (User_Info eachUser_info :
		 * listOfUserInfo) { email += eachUser_info.getEmail() + ";"; }
		 * 
		 * email = email.substring(0, email.length() - 1); }
		 * 
		 * return email;
		 */
	}

	public String getPLCGEmail() {
		User_Info user_info = user_Info_DAO.getPLCGEmail();

		return user_info.getEmail();
	}

	public Integer getAgencyId(String tier1AgencyId, String tier2AgencyId)
			throws Exception {
		Integer agencyId = null;

		if (tier1AgencyId != null) {
			agencyId = protest_Info_DAO.getAgency_Info_Id(tier1AgencyId,
					tier2AgencyId);
		}

		return agencyId;
	}

	public void addAgencyPOC(String userId, Integer agencyInfoId)
			throws Exception {
		
		String pocUserIds;
		Agency_Info agencyInfo = agency_Info_DAO.getAgency_Info(agencyInfoId);

		pocUserIds = agencyInfo.getUser_Id();
		if (pocUserIds == null
				|| pocUserIds.equalsIgnoreCase("")) {
			pocUserIds = userId;
		} else {
			pocUserIds += ";" + userId;
		}
		agencyInfo.setUser_Id(pocUserIds);
		
		agency_Info_DAO.updateAgencyInfo(agencyInfo);

	}
	public User_Info getUserProfileInfo(String userId,
			HttpServletRequest request) throws Exception {
		
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request,
				"user_Info");

		if (user_Info == null) {
			user_Info = user_Info_DAO.getUserProfileInfo(userId);
			EpdsSession.setAttribute(request, "user_Info", user_Info);
		}

		return user_Info;
	}

	

	public String getUserRole(HttpServletRequest req, User_Info userInfo)
			throws Exception {
		String user_role = (String) EpdsSession.getAttribute(req, "user_Role");

		if (user_role == null) {
			UserRoles role = UserRoles.getByCode(userInfo.getRole_id());
			switch (role) {
				case GAO_ATTORNEY:
					user_role = "GAO ATTORNEY";
					break;
				case AGENCY_ADMIN:
					user_role = "AGENCY ADMIN";
					break;
				case AGENCY_ATTORNEY:
					user_role = "AGENCY ATTORNEY";
					break;
				case GAO_ADMIN:
					user_role = "GAO ADMIN";
					break;
				case GAO_SUPERVISOR:
					user_role = "GAO SUPERVISOR";
					break;
//				case PROTESTER:
//				case INTERVENOR:
//				case SECONDARY_PROTESTER:
//				case SECONDARY_INTERVENOR:
				default:
					user_role = "PROTESTER";
					break;

			}

			EpdsSession.setAttribute(req, "user_Role", user_role);
		}

		return user_role;
	}

	public String getAgencyName(int agencyId) {
		String agencyName = Util.getAgencyName(agencyId, agency_Info_DAO);

		return agencyName;
	}
	
	public String getAgencyNamebyAgencyInfoId(int agencyId) throws Exception {

		return agency_Info_DAO.getAgencyName(agencyId);
	}

	public List<User_Info> getGAOAdminUserInfos() {
		List<User_Info> listOfGAOAdminUserInfos = user_Info_DAO
				.getListOfEPDSUserByRoleId(7);

		return listOfGAOAdminUserInfos;
	}

	public boolean checkIfGAOIdExists(Integer gaoId) {
		return user_Info_DAO.checkIfGAOIdExists(gaoId);
	}


	public List<User_Info> getListOfSupervisorandAgencyPOCUserInfoList() {
		
		return user_Info_DAO.getListOfSupervisorandAgencyPOCUserInfoList();
	}
	
	/**
	 * @param eachUserInfo
	 */
	public void updateAgencyPOCAndSupervisorEmailPreferences(User_Info eachUserInfo) {
		
		if (eachUserInfo.getCds_preferences() != null 
				&& !eachUserInfo.getCds_preferences().equalsIgnoreCase("")){
			
			String[] arrayOfANos = eachUserInfo.getCds_preferences().split(";");
			
			Set<String> setOfAnos = new HashSet<String>(Arrays.asList(arrayOfANos));
			
			if (!setOfAnos.isEmpty()){
				
				for (Iterator<String> i = setOfAnos.iterator(); i.hasNext();) {
					
					String aNum = i.next();
					
					Protest_Info protest_Info;
					try {
						protest_Info = protest_Info_DAO.getProtestByA_no(aNum);
						
						
						if (null != protest_Info && protest_Info.getPublic_decision_date() != null) {
							int numberOFDaysSincePublicDecisionWasIssued = Days.
													daysBetween(new DateTime(protest_Info.getPublic_decision_date()).toLocalDate(), 
																new DateTime().toLocalDate()).getDays();
							
							if (numberOFDaysSincePublicDecisionWasIssued >= 60){
								i.remove();
							}
							
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
				if (setOfAnos.size() > 0 && !setOfAnos.isEmpty()){
				
					eachUserInfo.setCds_preferences(StringUtils.join(setOfAnos,";"));
				}else{
					eachUserInfo.setCds_preferences(null);
				}
				
				try {
					updateUserInfo(eachUserInfo);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void notifyAllSystemUsers(EmailNotification emailNotification) throws IOException {
		List<String> emailAddresses = new ArrayList<String>();
		List<String> testEmailAddresses = new ArrayList<String>();
		int totalLists = 0;
			//EDS PRE-PROD CBCA 0011 or DEV CBCAEDS 0005
		// if (GlobalParams.IP.toString().contains("10.102.108.136") || GlobalParams.IP.toString().contains("10.102.107.139"))
		if (GlobalParams.IP.toString().contains("GCOH0W-1G1KMR2/159.142.147.116")) {
			// for pre-prod, only send to CBCA, but appending the list that it would have sent to
			this.getListAuthUsersEmail(testEmailAddresses);
			emailNotification.setEmailBody(emailNotification.getEmailBody() + testEmailAddresses.toString());

//			emailAddresses.add("wessere@cbca.gov");
//			emailAddresses.add("goldsteine@cbca.gov");
			emailAddresses.add("arthur.hawkins@gsa.gov");
			//PROD CBCA --0012
		} else if (GlobalParams.IP.toString().contains("159.142.165.49")) {
			this.getListAuthUsersEmail(emailAddresses);
//			List<User_Info> listOfAllRegisteredSystemUsers = user_Info_DAO.getListOfAllSystemUsers();
//			for (User_Info eachUserInfo : listOfAllRegisteredSystemUsers){
//				 emailAddresses.add(eachUserInfo.getEmail());
//			 }
		} else {
			this.getListAuthUsersEmail(testEmailAddresses);
			emailNotification.setEmailBody(emailNotification.getEmailBody() + testEmailAddresses.toString());

			emailAddresses.add("arthur.hawkins@gsa.gov");
			emailAddresses.add("charles.otoupalik@usda.gov");
		}
		
		if (emailAddresses.size() > 20){
			totalLists = emailAddresses.size() / 25 ;
			List<List<String>> listOfEmailAddressList = Util.partitionList(emailAddresses,totalLists + 1);
			for (List<String> eachChunk : listOfEmailAddressList){
				emailService.notifyAllSystemUsers(emailNotification,eachChunk);
			}
		}else{
			 emailService.notifyAllSystemUsers(emailNotification,emailAddresses);
		}
	}

	public String checkIfAuthUserExists(String email) throws IOException {
		String response = authUtil.getForObject("checkEmailExistsURI", email, String.class);
		String message = AuthUtil.getJsonNode(response, "message").asText();
		return message;
	}

	private void getListAuthUsersEmail(List<String> emailAddresses) throws IOException {
		String response = authUtil.getForObject("getUserInfoURI", "all", String.class);
		ArrayNode arrayNode = (ArrayNode) AuthUtil.getJsonNode(response, "data");
		for (JsonNode eachUserInfo : arrayNode){
			emailAddresses.add(eachUserInfo.get("email").asText());
		}
	}
}
