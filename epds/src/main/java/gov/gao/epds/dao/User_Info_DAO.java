package gov.gao.epds.dao;

import gov.gao.epds.dto.CompanyInfo;
import gov.gao.epds.dto.SubmitNewDocDTO;
import gov.gao.epds.dto.User_info_dto;
import gov.gao.epds.enums.UserRoles;
import static gov.gao.epds.enums.UserRoles.*;
import gov.gao.epds.persistence.DataAccess;
import gov.gao.epds.persistence.entity.Agency_Info;
import gov.gao.epds.persistence.entity.GAO_User;
import gov.gao.epds.persistence.entity.Invited_User;
import gov.gao.epds.persistence.entity.Protest_Info;
import gov.gao.epds.persistence.entity.User_Info;
import gov.gao.epds.persistence.entity.User_Protest_Role_Bridge;
import gov.gao.epds.persistence.entity.User_Role;
import gov.gao.epds.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class User_Info_DAO {

	@Autowired
	private DataAccess access;

	@Transactional
	public User_Info getUserProfileInfo(String userId) throws Exception {

		String query = "from User_Info where user_Id = :user_Id";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user_Id", userId);

		@SuppressWarnings("rawtypes")
		List resultList = access.queryWithParams(query, map);

		return resultList.isEmpty() ? null : (User_Info) resultList.get(0);
	}
	
	@Transactional
	public List<User_Info> getListOfUserInfoByEmail(String email) throws Exception {

		String query = "from User_Info where email = :email";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("email", email);

		@SuppressWarnings("rawtypes")
		List resultList = access.queryWithParams(query, map);

		return resultList.isEmpty() ? null : (List<User_Info>) resultList;
	}

	@Transactional
	public User_Info getUser_Info_By_User_Id(String user_Id) throws Exception {
		String query = "from User_Info where user_Id=:user_Id";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user_Id", user_Id);
		@SuppressWarnings("rawtypes")
		List resultList = access.queryWithParams(query, map);
		return resultList.isEmpty() ? null : (User_Info) resultList.get(0);
	}
	
	
	@Transactional
	public User_Info getUserInfoByEmailAndNotEqualsToUserId(String email, String notEqualToUserId) throws Exception {

		String query = "from User_Info where user_Id = :user_Id and email = :email";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("email", email);
		map.put("user_Id", notEqualToUserId);

		@SuppressWarnings("rawtypes")
		List resultList = access.queryWithParams(query, map);

		return resultList.isEmpty() ? null : (User_Info) resultList.get(0);
	}

	@Transactional
	public User_Info getAttorneyInfo(String a_No) {
		String query = "from User_Info a, User_Protest_Role_Bridge b where a.user_Id = b.user_Id and b.a_No = :a_No and b.role_Id = 3 and b.consolidated_A_No is null";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a_No", a_No);

		List<?> list;
		User_Info attorneyInfo = null;
		try {
			list = access.queryWithParams(query, map);
			attorneyInfo = (Util.getDesiredClassObjectList(list,
					User_Info.class)).get(0);
		} catch (Exception e) {
			// e.printStackTrace();
			list = null;
		}

		return attorneyInfo;
	}

	@Transactional
	public User_Info saveProfileInfo(User_info_dto user_info_dto)
			throws Exception {
		User_Info user_Info = Util.getPopulatedUserInfo(user_info_dto);

		user_Info = access.save(user_Info);
		return user_Info;
	}

	@Transactional
	public User_Info updateBasicInfo(User_info_dto user_info_dto)
			throws Exception {
		User_Info user_Info = Util.getPopulatedUserInfo(user_info_dto);
		user_Info = access.update(user_Info);

		return user_Info;
	}

	@Transactional
	public List<User_Role> getUser_RoleList_BasedOnUserId(String user_Id)
			throws Exception {
		String query = "from User_Protest_Role_Bridge a, User_Role b where a.user_Id = :user_Id and a.role_Id = b.role_Id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user_Id", user_Id);

		List<?> list = access.queryWithParams(query, map);

		if (list == null) {
			return new ArrayList<User_Role>();
		}

		return Util.getDesiredClassObjectList(list, User_Role.class);
	}
	
	
	@Transactional
    public User_Role getUserRoleByRoleId(Integer roleId)
            throws Exception {
        String query = "from User_Role where role_Id = :roleId";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("roleId", roleId);

        List<?> list = access.queryWithParams(query, map);

       

        return (list != null ? (User_Role) list.get(0) : null);
    }

	@Transactional
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<User_Protest_Role_Bridge> getUser_Protest_Role_Bridge_List_BasedOnProtestId(
			String a_No) throws Exception {
		String query = "from User_Protest_Role_Bridge where a_No = :a_No";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a_No", a_No);

		List list = access.queryWithParams(query, map);
		return (List<User_Protest_Role_Bridge>) list;
	}

    @Transactional
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<User_Protest_Role_Bridge> getUser_Protest_Role_Bridge_List_NonConsolidated(
            String a_No) throws Exception {
        String query = "from User_Protest_Role_Bridge where a_No = :a_No and consolidated_A_No is null";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("a_No", a_No);

        List list = access.queryWithParams(query, map);
        return (List<User_Protest_Role_Bridge>) list;
    }

	@Transactional
	public Boolean checkIfThisAgencyRepUserProtestRoleBridgeExists(
			String a_No, String userId,Integer roleId) throws Exception {
		Boolean uprbExists = false;
		String query = "from User_Protest_Role_Bridge where a_No = :a_No and role_Id = :roleId and user_Id = :user_Id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a_No", a_No);
		map.put("roleId", roleId);
		map.put("user_Id", userId);

		List<?> resultList = access.queryWithParams(query, map);
		
		if (resultList != null && resultList.size() > 0){
			uprbExists = true;
		}
		
		return uprbExists;
	}
	
	@Transactional
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<User_Protest_Role_Bridge> getAgencyUserProtestRoleBridge(
			String a_No) throws Exception {
		String query = "from User_Protest_Role_Bridge where a_No = :a_No and role_Id in (5,6)";
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a_No", a_No);

		List list = access.queryWithParams(query, map);
		return (List<User_Protest_Role_Bridge>) list;
	}
	
	@Transactional
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<User_Protest_Role_Bridge> getUserProtestRoleBridgeListBasedOnProtestIdAndEmailPreferences(
			String a_No) throws Exception {

		String query = "from User_Protest_Role_Bridge where (casedocket_email_preferences is null or casedocket_email_preferences != 'N') and (a_No = :a_No)";
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("a_No", a_No);

		/*
		 * if (isThisAConsolidatedProtest){ query +=
		 * " and CONSOLIDATED_A_NO = :consolidated_aNo  or  a_No = :a_No";
		 * map.put("consolidated_aNo", a_No); map.put("a_No", a_No); }else{
		 * query += " and a_No = :a_No "; map.put("a_No", a_No); }
		 */

		List list = access.queryWithParams(query, map);
		return (List<User_Protest_Role_Bridge>) list;
	}

	@Transactional
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<User_Protest_Role_Bridge> getUser_Protest_Role_Bridge_List_BasedOnProtestIdAndRole(
			String a_No, String roleId) throws Exception {
		String query = "from User_Protest_Role_Bridge where a_No = :a_No and role_Id = :role_Id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a_No", a_No);
		map.put("role_Id", roleId);

		List list = access.queryWithParams(query, map);
		return (List<User_Protest_Role_Bridge>) list;
	}

	@Transactional
	public void insertIntoUser_Protest_Role_Bridge(
			SubmitNewDocDTO submitNewDocDTO) throws Exception {
		User_Protest_Role_Bridge user_Protest_Role_Bridge = new User_Protest_Role_Bridge();
		user_Protest_Role_Bridge.setA_No(submitNewDocDTO.getProtestId());
		user_Protest_Role_Bridge.setUser_Id(submitNewDocDTO.getUser_Id());
		user_Protest_Role_Bridge.setPo("N");
		user_Protest_Role_Bridge.setRole_Id(2);
		access.save(user_Protest_Role_Bridge);
	}

	@Transactional
	public List<User_Info> getUser_InfoListByAnum(String a_No) throws Exception {
		String query = "from User_Info a, User_Protest_Role_Bridge b, User_Role c where a.user_Id = b.user_Id and b.a_No = :a_No and b.role_Id = c.role_Id and b.role_Id != '5' "
				+ "and b.consolidated_A_No is null";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a_No", a_No);

		List<?> resultList = access.queryWithParams(query, map);

		List<User_Info> user_InfoList = Util.getDesiredClassObjectList(
				resultList, User_Info.class);
		List<User_Role> user_RoleList = Util.getDesiredClassObjectList(
				resultList, User_Role.class);
		List<User_Protest_Role_Bridge> user_Protest_Role_BridgeList = Util
				.getDesiredClassObjectList(resultList,
						User_Protest_Role_Bridge.class);

		user_InfoList = fillUpUser_Role_AndIntervenorCompanyInfo(user_InfoList,
				user_RoleList, user_Protest_Role_BridgeList);

		return user_InfoList;
	}

	private List<User_Info> fillUpUser_Role_AndIntervenorCompanyInfo(
			List<User_Info> user_InfoList, List<User_Role> user_RoleList,
			List<User_Protest_Role_Bridge> user_Protest_Role_BridgeList) {
		List<User_Info> newUser_InfoList = new ArrayList<User_Info>();

		User_Info user_Info = null;
		for (int i = 0; i < user_InfoList.size(); i++) {
            user_Info = user_InfoList.get(i);

			// changing a non-transient value, this will prevent DB update
			access.getSessionFactory().getCurrentSession().evict(user_Info);

			user_Info.setRole_id(user_RoleList.get(i).getRole_Id());
			user_Info.setRole(user_RoleList.get(i).getRole_Desc());
			user_Info.setPo(user_Protest_Role_BridgeList.get(i).getPo());

			if (user_RoleList.get(i).getRole_Desc().equals("INTERVENOR")
					|| user_RoleList.get(i).getRole_Desc()
							.equals("SECONDARY INTERVENOR")) {
				CompanyInfo companyInfo = new CompanyInfo();
				
				companyInfo.setCompanyName(user_Protest_Role_BridgeList
								.get(i).getIntervenor_Company_Name());
				
				companyInfo.setCompanyAddress(user_Protest_Role_BridgeList
						.get(i).getIntervenor_Company_Address());
				user_Info.setIntervenorCompanyInfo(companyInfo);
				user_Info
						.setIntervenor_Company_Name(user_Protest_Role_BridgeList
								.get(i).getIntervenor_Company_Name());
				user_Info
						.setIntervenor_Company_Address(user_Protest_Role_BridgeList
								.get(i).getIntervenor_Company_Address());
			}

			newUser_InfoList.add(user_Info);
		}

		return newUser_InfoList;
	}

	@Transactional
	public List<User_Info> getSecondary_User_Info_List(String a_No,
			Integer secondaryRoleId) {
		String query = "from User_Protest_Role_Bridge a, User_Info b where a.a_No = :a_No and a.user_Id = b.user_Id and a.role_Id = :role_id";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("a_No", a_No);
		paramMap.put("role_id", secondaryRoleId);

		List<?> resultList = access.queryWithParams(query, paramMap);

		return Util.getDesiredClassObjectList(resultList, User_Info.class);
	}
	
	
	@Transactional
	public List<User_Info> getSecondaryAgencyUser_Info_List(String a_No,
			List<Integer> agencyInfoIds) {
		String query = "from User_Protest_Role_Bridge a, User_Info b where a.a_No = :a_No and a.user_Id = b.user_Id and a.role_Id IN (5,6) and b.firm_id NOT IN (:firmIds)";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("a_No", a_No);
		paramMap.put("firmIds", agencyInfoIds);

		List<?> resultList = access.queryWithParams(query, paramMap);

		return Util.getDesiredClassObjectList(resultList, User_Info.class);
	}
	
	
	@Transactional
	public List<User_Info> getSecondary_User_Info_ListByIntervenorCompanyName(String a_No,
			Integer secondaryRoleId,String intervenorCompanyName) {
		String query = "from User_Protest_Role_Bridge a, User_Info b where a.a_No = :a_No and a.user_Id = b.user_Id and a.role_Id = :role_id and a.intervenor_Company_Name = :companyName";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("a_No", a_No);
		paramMap.put("role_id", secondaryRoleId);
		paramMap.put("companyName", intervenorCompanyName);

		List<?> resultList = access.queryWithParams(query, paramMap);

		return Util.getDesiredClassObjectList(resultList, User_Info.class);
	}

	@Transactional
	public void addSecondaryProtester(String a_No, String secondary_User_Id,
			String pO) {
		User_Protest_Role_Bridge user_Protest_Role_Bridge = new User_Protest_Role_Bridge();
		user_Protest_Role_Bridge.setA_No(a_No);
		user_Protest_Role_Bridge.setPo(pO);
		user_Protest_Role_Bridge.setRole_Id(4);
		user_Protest_Role_Bridge.setUser_Id(secondary_User_Id);

		access.save(user_Protest_Role_Bridge);
	}

	@Transactional
	public List<User_Info> getIntervenorList(String a_No) {
		String query = "from User_Info a, User_Protest_Role_Bridge b where a.user_Id = b.user_Id and b.a_No = :a_No and b.role_Id = 2";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("a_No", a_No);

		List<?> resultList = access.queryWithParams(query, paramMap);
		List<User_Info> userInfoList = Util.getDesiredClassObjectList(
				resultList, User_Info.class);

		return userInfoList;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public String getUserIdByEmail(String secondary_Email) {
		String query = "from User_Info where email = :email";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("email", secondary_Email);

		List<?> resultList = access.queryWithParams(query, paramMap);
		String user_Id = ((List<User_Info>) resultList).get(0).getUser_Id();

		return user_Id;
	}

	@Transactional
	@SuppressWarnings("unchecked")
	public Invited_User getSecondaryProtester(String invite_A_No,
			String secondary_Id, String status) {
		String query = "from Invited_User where a_No = :a_No and invitee_Id = :secondary_Id and status = :status";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("a_No", invite_A_No);
		paramMap.put("secondary_Id", secondary_Id);
		paramMap.put("status", status);

		List<?> resultList = access.queryWithParams(query, paramMap);

		if (resultList.size() > 0) {
			return ((List<Invited_User>) resultList).get(0);
		} else {
			return null;
		}

	}

	@Transactional
	@SuppressWarnings("unchecked")
	public Invited_User getSecondaryProtesterByANumberUserIdAndStatus(String invite_A_No,
			String secondary_Id) {
		String query = "from Invited_User where a_No = :a_No and invitee_Id = :secondary_Id and (status = 'ACCEPTED' or status = 'INVITED')";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("a_No", invite_A_No);
		paramMap.put("secondary_Id", secondary_Id);

		List<?> resultList = access.queryWithParams(query, paramMap);

		if (resultList.size() > 0) {
			return ((List<Invited_User>) resultList).get(0);
		} else {
			return null;
		}

	}
	@Transactional
	public void respondSecondaryProtesterInvite(Invited_User invited_User,
			String response) throws Exception {
		String status = "SECONDARY-ACCEPTED";
		if (response.equalsIgnoreCase("reject")) {
			status = "SECONDARY-REJECTED";
		}

		invited_User.setStatus(status);
		access.update(invited_User);
	}

	@Transactional
	public boolean isEmailValid(String secondary_Email) {
		
		String query = "from User_Info where email = :email";
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("email", secondary_Email);

		List<?> resultList = access.queryWithParams(query, paramMap);

		if (resultList.size() > 0)
			
			return true;

		return false;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Invited_User> getUnapprovedSecondary_ProtesterList(String a_No) {
		String query = "from Invited_User where (a_No = :a_No)  and (status = 'ACCEPTED' or status = 'INVITED')";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("a_No", a_No);

		List<?> resultList = access.queryWithParams(query, paramMap);
		return (List<Invited_User>) resultList;
	}

	@Transactional
	public List<Invited_User> getInvitedSecondaryProtester(String userId) {
		String query = "from Invited_User a, User_Info b, Protest_Info c where a.invitee_Id = :secondary_Id and a.inviter_Id = b.user_Id and a.a_No = c.a_No and a.status = :status";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("secondary_Id", userId);
		map.put("status", "INVITED");

		List<?> resultList = access.queryWithParams(query, map);
		List<Invited_User> secondary_ProtesterList = Util
				.getDesiredClassObjectList(resultList, Invited_User.class);
		List<User_Info> primary_User_InfoList = Util.getDesiredClassObjectList(
				resultList, User_Info.class);
		List<Protest_Info> protest_InfoList = Util.getDesiredClassObjectList(
				resultList, Protest_Info.class);

		secondary_ProtesterList = fillupSecondaryProtesterListWithAdditionalInfo(
				secondary_ProtesterList, primary_User_InfoList,
				protest_InfoList);

		return secondary_ProtesterList;
	}

	private List<Invited_User> fillupSecondaryProtesterListWithAdditionalInfo(
			List<Invited_User> secondary_ProtesterList,
			List<User_Info> user_InfoList, List<Protest_Info> protest_InfoList) {
		List<Invited_User> newSecondary_ProtesterList = new ArrayList<Invited_User>();

		Invited_User invited_User = null;
		for (int i = 0; i < secondary_ProtesterList.size(); i++) {
			invited_User = secondary_ProtesterList.get(i);
			invited_User.setInviterOrInviteeName(user_InfoList.get(i)
					.getFirst_Name()
					+ " "
					+ user_InfoList.get(i).getLast_Name());
			invited_User.setB_No(protest_InfoList.get(i).getB_No());
			invited_User.setProtesterCompanyName(protest_InfoList.get(i)
					.getCompany_Name());
			invited_User
					.setInviterFirmName(user_InfoList.get(i).getFirm_Name());

			newSecondary_ProtesterList.add(invited_User);
		}

		return secondary_ProtesterList;
	}

	@Transactional
	public List<Invited_User> getInviteAcceptedSecondaryProtester(
			String inviter_Id) {
		String query = "from Invited_User a, User_Info b, Protest_Info c where a.inviter_Id = :inviter_Id and a.invitee_Id = b.user_Id and a.a_No = c.a_No and a.status = 'SECONDARY-ACCEPTED'";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("inviter_Id", inviter_Id);

		List<?> resultList = access.queryWithParams(query, map);
		List<Invited_User> secondary_ProtesterList = Util
				.getDesiredClassObjectList(resultList, Invited_User.class);
		List<User_Info> secondary_User_InfoList = Util
				.getDesiredClassObjectList(resultList, User_Info.class);
		List<Protest_Info> protest_InfoList = Util.getDesiredClassObjectList(
				resultList, Protest_Info.class);

		secondary_ProtesterList = fillupSecondaryProtesterListWithAdditionalInfo(
				secondary_ProtesterList, secondary_User_InfoList,
				protest_InfoList);

		return secondary_ProtesterList;
	}
	
	@Transactional
	public List<Invited_User> getListOfInvitedUsersByANum(
			String aNum) {
		String query = "from Invited_User where a_No = :aNum";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("aNum", aNum);

		List<?> resultList = access.queryWithParams(query, map);

		return (List<Invited_User>) resultList;
	}
	
	
	@Transactional
	public List<Invited_User> getListOfInvitedUsersbyUserId(
			String userId) {
		String query = "from Invited_User where inviter_Id = :inviter_Id or invitee_Id = :inviter_Id";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("inviter_Id", userId);

		List<?> resultList = access.queryWithParams(query, map);
		List<Invited_User> invitedUserList = (List<Invited_User>) resultList;
		

		return invitedUserList;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<String> getIntervenorCompanyNameList(String a_No) {
		String query = "from User_Protest_Role_Bridge where a_No = :a_No and role_Id = 2";

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("a_No", a_No);

		List<?> resultList = access.queryWithParams(query, paramMap);
		List<User_Protest_Role_Bridge> user_Protest_Role_BridgeList = (List<User_Protest_Role_Bridge>) resultList;

		List<String> intervenorCompanyNameList = new ArrayList<String>();
		for (User_Protest_Role_Bridge user_Protest_Role_Bridge : user_Protest_Role_BridgeList) {
			intervenorCompanyNameList.add(user_Protest_Role_Bridge
					.getIntervenor_Company_Name());
		}

		return intervenorCompanyNameList;
	}

	@Transactional
	public void changeProtectiveOrder(String a_No, String po) throws Exception {
		List<User_Protest_Role_Bridge> user_Protest_Role_BridgeList = getUser_Protest_Role_Bridge_List_BasedOnProtestId(a_No);

		for (User_Protest_Role_Bridge each : user_Protest_Role_BridgeList) {
			each.setPo(po.toUpperCase(Locale.ENGLISH));
			access.update(each);
		}
	}

	@Transactional
	public void admitToPO(String user_Id, String shouldAdmit, String a_No)
			throws Exception {
		List<User_Protest_Role_Bridge> user_Protest_Role_Bridge = getUser_Protest_Role_BridgeList(
				a_No, user_Id);

		if (user_Protest_Role_Bridge != null){
			
			for (User_Protest_Role_Bridge eachUprb : user_Protest_Role_Bridge){
				eachUprb.setPo(shouldAdmit);
				access.update(eachUprb);	
			}	
		}
		
		
	}

	@Transactional
	public void admitToPO_BasedOnPartyType(String partyType,
			String shouldAdmit, String a_No) throws Exception {
		List<User_Protest_Role_Bridge> user_Protest_Role_BridgeList = getUser_Protest_Role_Bridge_List_BasedOnProtestIdAndRole(
				a_No, partyType);

		for (User_Protest_Role_Bridge each : user_Protest_Role_BridgeList) {
			each.setPo(shouldAdmit);
			access.update(each);
		}

	}

	@SuppressWarnings("unchecked")
	@Transactional
	public User_Protest_Role_Bridge getUser_Protest_Role_Bridge(String a_No,
			String user_Id) {
		String query = "from User_Protest_Role_Bridge where a_No = :a_No and user_Id = :user_Id";

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("a_No", a_No);
		paramMap.put("user_Id", user_Id);

		List<?> resultList = access.queryWithParams(query, paramMap);
		List<User_Protest_Role_Bridge> user_Protest_Role_BridgeList = (List<User_Protest_Role_Bridge>) resultList;

		return (!user_Protest_Role_BridgeList.isEmpty() ? user_Protest_Role_BridgeList.get(0) : null);
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<User_Protest_Role_Bridge> getUser_Protest_Role_BridgeList(String a_No,
			String user_Id) {
		
		String query = "from User_Protest_Role_Bridge where a_No = :a_No and user_Id = :user_Id";

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("a_No", a_No);
		paramMap.put("user_Id", user_Id);

		List<?> resultList = access.queryWithParams(query, paramMap);
		List<User_Protest_Role_Bridge> user_Protest_Role_BridgeList = (List<User_Protest_Role_Bridge>) resultList;

		return (!user_Protest_Role_BridgeList.isEmpty() ? user_Protest_Role_BridgeList : null);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public User_Protest_Role_Bridge getUser_Protest_Role_BridgeWithoutAgencyPOCsByANumAndUserId(String a_No,
			String user_Id) {
		
		String query = "from User_Protest_Role_Bridge where a_No = :a_No and user_Id = :user_Id and role_Id != '5'";

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("a_No", a_No);
		paramMap.put("user_Id", user_Id);

		List<?> resultList = access.queryWithParams(query, paramMap);
		List<User_Protest_Role_Bridge> user_Protest_Role_BridgeList = (List<User_Protest_Role_Bridge>) resultList;

		return (!user_Protest_Role_BridgeList.isEmpty() ? user_Protest_Role_BridgeList.get(0) : null);
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<User_Protest_Role_Bridge> getUser_Protest_Role_Bridge_BasedOn_A_No_And_IntervenorName(
			String a_No, String intervenor_Company_Name) {
		String query = "from User_Protest_Role_Bridge where a_No = :a_No and intervenor_Company_Name = :intervenor_Company_Name";

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("a_No", a_No);
		paramMap.put("intervenor_Company_Name", intervenor_Company_Name);

		List<?> resultList = access.queryWithParams(query, paramMap);
		List<User_Protest_Role_Bridge> user_Protest_Role_BridgeList = (List<User_Protest_Role_Bridge>) resultList;

		return user_Protest_Role_BridgeList;
	}

	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<User_Protest_Role_Bridge> getUprb_BasedOnIntervenorInfo(
			String a_No, String intervenor_Company_Name, String intervenorCompanyAddress) {
		String query = "from User_Protest_Role_Bridge "
				+ " where a_No = :a_No and intervenor_Company_Name = :intervenor_Company_Name"
				+ " and intervenor_Company_Address = :intervenorCompanyAddr";

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("a_No", a_No);
		paramMap.put("intervenor_Company_Name", intervenor_Company_Name);
		paramMap.put("intervenorCompanyAddr", intervenorCompanyAddress);

		List<?> resultList = access.queryWithParams(query, paramMap);
		List<User_Protest_Role_Bridge> user_Protest_Role_BridgeList = (List<User_Protest_Role_Bridge>) resultList;

		return user_Protest_Role_BridgeList;
	}
	
	@Transactional
	public void deleteInvited_User(Invited_User invited_User) throws Exception {
		access.delete(invited_User);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public String getIntervenorCompanyName(String a_No, String user_Id) {
		String query = "from User_Protest_Role_Bridge where a_No = :a_No and user_Id = :user_Id";

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("a_No", a_No);
		paramMap.put("user_Id", user_Id);

		List<?> resultList = access.queryWithParams(query, paramMap);
		List<User_Protest_Role_Bridge> user_Protest_Role_BridgeList = (List<User_Protest_Role_Bridge>) resultList;

		return user_Protest_Role_BridgeList.get(0).getIntervenor_Company_Name();
	}

	@Transactional
	public <T> void save_Entity(T t) {
		access.save(t);
	}
 
	@Transactional
	public void allowAllUsersOfCaseATheAccessToCaseB(String aNumberOfCaseA,
			String aNumberOfCaseB,String typeOfProtest) throws Exception {
		List<User_Protest_Role_Bridge> userProtestRoleBridgeRecordsOfA = getUser_Protest_Role_Bridge_List_BasedOnProtestId(aNumberOfCaseA);

		List<User_Protest_Role_Bridge> userProtestRoleBridgeList = new ArrayList<User_Protest_Role_Bridge>();

		UserRoles role;
		for (User_Protest_Role_Bridge eachUserProtestRoleBridgeOfA : userProtestRoleBridgeRecordsOfA) {
		
		    boolean doBridge = false;
			role = UserRoles.getByCode(eachUserProtestRoleBridgeOfA.getRole_Id());
		    if (!typeOfProtest.toUpperCase(Locale.ENGLISH).contains("RECON") 
                    && !typeOfProtest.toUpperCase(Locale.ENGLISH).contains("ENT") 
                    && !typeOfProtest.toUpperCase(Locale.ENGLISH).contains("COST")){
		        doBridge = true;
		    } else if ((typeOfProtest.toUpperCase(Locale.ENGLISH).contains("ENT") || typeOfProtest.toUpperCase(Locale.ENGLISH).contains("COST"))
                    && role != INTERVENOR && role != SECONDARY_INTERVENOR ){
		        doBridge = true;
            } else if ((typeOfProtest.equalsIgnoreCase("RECONSIDERATION") && role != GAO_ATTORNEY)){
                doBridge = true;
			}

		    if (doBridge)
		    {
                User_Protest_Role_Bridge user_Protest_Role_Bridge = new User_Protest_Role_Bridge(
                        eachUserProtestRoleBridgeOfA);
                user_Protest_Role_Bridge.setA_No(aNumberOfCaseB);
    
                userProtestRoleBridgeList.add(user_Protest_Role_Bridge);
		    }
		}

		access.save(userProtestRoleBridgeList);
	}

	@Transactional
	public void add_User_Protest_Role_Bridge_Entity(Protest_Info protest_Info,
			String userId, int role_id) {
		User_Protest_Role_Bridge user_Protest_Role_Bridge = new User_Protest_Role_Bridge();
		user_Protest_Role_Bridge.setA_No(protest_Info.getA_No());
		user_Protest_Role_Bridge.setUser_Id(userId);
		user_Protest_Role_Bridge.setPo("N");
		user_Protest_Role_Bridge.setRole_Id(role_id);

		access.save(user_Protest_Role_Bridge);
	}

	@Transactional
	public List<User_Info> getAgencyRepUserInfoListByANum(String a_No) {
		String query = "from User_Protest_Role_Bridge a, User_Info b where a.user_Id = b.user_Id and a.a_No = :a_No and a.role_Id = 6";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("a_No", a_No);

		List<?> resultList = access.queryWithParams(query, paramMap);
		List<User_Info> agency_Attorney_List = Util.getDesiredClassObjectList(
				resultList, User_Info.class);

		return agency_Attorney_List;
	}
	
	@Transactional
	public List<User_Info> getAgencyAttorneyListByCompanyName(String a_No,String companyName) {
		
		String query = "from User_Protest_Role_Bridge a, User_Info b where a.user_Id = b.user_Id "
				+ " and a.a_No = :a_No and a.role_Id = 6 "
				+ " and a.intervenor_Company_Name =:companyName";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("a_No", a_No);
		paramMap.put("companyName", companyName);

		List<?> resultList = access.queryWithParams(query, paramMap);
		List<User_Info> agency_Attorney_List = Util.getDesiredClassObjectList(
				resultList, User_Info.class);

		return agency_Attorney_List;
	}
	
	
	/**
	 * @param partyInformation - email,phone number, first name, last name, firmName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<User_Info> getListOfUserInfoByPartyInformation(String partyInformation) {
		
		String query = "from User_Info where "
				+ "(UPPER(email) LIKE UPPER(:partyInfo)) "
				+ "OR ( (UPPER(first_Name)||' '|| UPPER(last_Name)) LIKE UPPER(:partyInfo)) "
				+ "OR (UPPER(phone_No) LIKE UPPER(:partyInfo))"
				+ "OR (UPPER(firm_Name) LIKE UPPER(:partyInfo))";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		paramMap.put("partyInfo", "%" + partyInformation + "%");

		List<?> resultList = access.queryWithParams(query, paramMap);
		

		return resultList != null ? ( List<User_Info>) resultList : null;
	}

	@Transactional
	public List<User_Info> getUnapprovedSecondaryUserInfoList(String a_No,
			String inviter_Type) {
		String query = "from Invited_User a, User_Info b where a.invitee_Id = b.user_Id and a.a_No = :a_No and a.inviter_Type = :inviter_Type and a.status = 'INVITED'";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("a_No", a_No);
		paramMap.put("inviter_Type", inviter_Type);

		List<?> resultList = access.queryWithParams(query, paramMap);
		List<User_Info> unapproved_Secondary_Protester_User_Info_List = Util
				.getDesiredClassObjectList(resultList, User_Info.class);

		return unapproved_Secondary_Protester_User_Info_List;
	}
	
	@Transactional
	public List<User_Info> getUnapprovedSecondaryUserInfoListByCompanyName(String a_No,
			String inviter_Type,String companyName) {
		String query = "from Invited_User a, User_Info b where a.invitee_Id = b.user_Id and a.a_No = :a_No and a.inviter_Type = :inviter_Type and a.status = 'INVITED' and a.company_name = :companyName";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("a_No", a_No);
		paramMap.put("inviter_Type", inviter_Type);
		paramMap.put("companyName", companyName);

		List<?> resultList = access.queryWithParams(query, paramMap);
		List<User_Info> unapproved_Secondary_Protester_User_Info_List = Util
				.getDesiredClassObjectList(resultList, User_Info.class);

		return unapproved_Secondary_Protester_User_Info_List;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public User_Info getUser_Info_ByEmail(String email) {
		String query = "from User_Info where email = :email";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("email", email);

		List<?> resultList = access.queryWithParams(query, paramMap);

		if (resultList == null || resultList.size() == 0)
			return null;
		else
			return ((List<User_Info>) resultList).get(0);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public String getAgency_Admin_User_Id_List_InString(int agency_Info_Id) {
		String query = "from Agency_Info where agency_Info_Id = :agency_Info_Id";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("agency_Info_Id", agency_Info_Id);

		List<?> resultList = access.queryWithParams(query, paramMap);

		if (resultList == null || resultList.size() == 0)
			return "";
		else
			return ((List<Agency_Info>) resultList).get(0).getUser_Id();
	}

	@Transactional
	public List<User_Info> getUserInfoListByListOfUserIds(
			List<String> userIdList) throws Exception {
		List<User_Info> userInfoList = new ArrayList<User_Info>();

		User_Info user_Info = null;
		for (String each_User_Id : userIdList) {
			user_Info = getUser_Info_By_User_Id(each_User_Id);
			userInfoList.add(user_Info);
		}

		return userInfoList;
	}

	@Transactional
	public void assignProtest_To_Agency_Admin(Protest_Info protest_Info,
			String user_Id) {
		User_Protest_Role_Bridge user_Protest_Role_Bridge = new User_Protest_Role_Bridge();
		user_Protest_Role_Bridge.setA_No(protest_Info.getA_No());
		user_Protest_Role_Bridge.setUser_Id(user_Id);
		user_Protest_Role_Bridge.setPo(protest_Info.getPo());
		user_Protest_Role_Bridge.setRole_Id(5);

		access.save(user_Protest_Role_Bridge);

	}

	@Transactional
	public <T> void update(T t) throws Exception {
		access.update(t);
	}

	@Transactional
	public void updateGAOId(Integer id, Integer new_Id) throws Exception {

		String query = "Update GAO_USER set id= :1 where id = :2";

		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, new_Id);
		map.put(2, id);

		access.executeNativeUpdateSQLWithSimpleParams(query, map);
	}

	@Transactional
	public List<User_Info> getListOfGAOUserConsistOfAttorneyAndSupervisor() {
		String query = "from GAO_User a, User_Info b where a.user_Id = b.user_Id and (UPPER(a.type) IN ('ATTORNEY','SUPERVISOR'))";
		Map<String, Object> paramMap = new HashMap<String, Object>();

		List<?> resultList = access.queryWithParams(query, paramMap);

		if (resultList == null || resultList.size() == 0)
			return null;
		else {
			List<GAO_User> gao_User_List = Util.getDesiredClassObjectList(
					resultList, GAO_User.class);
			List<User_Info> gao_User_Info_List = Util
					.getDesiredClassObjectList(resultList, User_Info.class);

			for (int i = 0; i < gao_User_Info_List.size(); i++) {
				

				if (gao_User_List.get(i).getGroup_No() != null){
					
					gao_User_Info_List.get(i).setGroup_No(
							gao_User_List.get(i).getGroup_No());
				}
				
			}

			return gao_User_Info_List;
		}
	}

	@Transactional
	public List<User_Info> getListOfAllGAOUsers() {
		String query = "from GAO_User a, User_Info b where a.user_Id = b.user_Id";
		Map<String, Object> paramMap = new HashMap<String, Object>();

		List<?> resultList = access.queryWithParams(query, paramMap);

		if (resultList == null || resultList.size() == 0)
			return null;
		else {
			List<GAO_User> gao_User_List = Util.getDesiredClassObjectList(
					resultList, GAO_User.class);
			
			List<User_Info> gao_User_Info_List = Util
					.getDesiredClassObjectList(resultList, User_Info.class);

			for (int i = 0; i < gao_User_Info_List.size(); i++) {
				if (gao_User_List.get(i).getGroup_No() == null){
					gao_User_Info_List.get(i).setGroup_No(0);
				}else{
					gao_User_Info_List.get(i).setGroup_No(
							gao_User_List.get(i).getGroup_No());
				}
				
				gao_User_Info_List.get(i).setTitle(gao_User_List.get(i).getTitle());
				gao_User_Info_List.get(i).setGao_user_id(gao_User_List.get(i).getId());
				
			}

			return gao_User_Info_List;
		}
	}
	
	@Transactional
	public List<User_Info> getListOfAlUsersByAgencyFirmName(Integer firmId) {
		String query = "from User_Info where firm_id = :firm_id";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("firm_id",firmId);
		
		
		List<?> resultList = access.queryWithParams(query, paramMap);

		if (resultList == null || resultList.size() == 0)
			return null;
		else {
			List<User_Info> agencyUserInfoList = Util
					.getDesiredClassObjectList(resultList, User_Info.class);

			return agencyUserInfoList;
		}
	}
	
	@Transactional
	public List<User_Info> getListOfUsersByRoleId(Integer roleId) {
		String query = "from User_Info where role_id = :role_id";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("role_id",roleId);
		
		
		List<?> resultList = access.queryWithParams(query, paramMap);

		if (resultList == null || resultList.size() == 0)
			return null;
		else {
			@SuppressWarnings("unchecked")
			List<User_Info> userInfoList = (List<User_Info>) resultList;

			return userInfoList;
		}
	}
	
	@Transactional
	public List<User_Info> getListOfAllSystemUsers() {
		String query = "from User_Info";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		List<?> resultList = access.queryWithParams(query, paramMap);

		if (resultList == null || resultList.size() == 0)
			return null;
		else {
			List<User_Info> userInfoList = (List<User_Info>) resultList;

			return userInfoList;
		}
	}
	@SuppressWarnings("unchecked")
	@Transactional
	public User_Protest_Role_Bridge getAttorneyUser_Protest_Role_Bridge(
			String a_No) {
		String query = "from User_Protest_Role_Bridge where a_No = :a_No and role_Id = 3 and consolidated_A_No is null";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("a_No", a_No);

		List<?> resultList = access.queryWithParams(query, paramMap);

		if (resultList == null || resultList.size() == 0)
			return null;
		else {
			return ((List<User_Protest_Role_Bridge>) resultList).get(0);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<User_Protest_Role_Bridge> getAttorneyUser_Protest_Role_BridgeConsolidatedProtests(
			String a_No) {
		String query = "from User_Protest_Role_Bridge where role_Id = 3 and (consolidated_A_No =:consolidated_A_No or a_No = :a_No)";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("a_No", a_No);
		paramMap.put("consolidated_A_No", a_No);

		List<?> resultList = access.queryWithParams(query, paramMap);

		if (resultList == null || resultList.size() == 0)
			return null;
		else {
			return ((List<User_Protest_Role_Bridge>) resultList);
		}
	}
	
	
	@Transactional
	public Boolean checkIfUserProtestRoleBridgeExists(User_Protest_Role_Bridge uprb) {
		
		Boolean isExists = false;
		
		String query = "from User_Protest_Role_Bridge where a_No =:a_No and user_Id =:userid"
				+ " and po = :po and role_Id =:role_Id and intervenor_Company_Name =:intervenor_Company_Name"
				+ " and intervenor_Company_Address =:intervenor_Company_Address "
				+ " and consolidated_A_No =:consolidated_A_No and casedocket_email_preferences  =:casedocket_email_preferences";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("a_No", uprb.getA_No());
		paramMap.put("userid", uprb.getUser_Id());
		paramMap.put("po", uprb.getPo());
		paramMap.put("role_Id", uprb.getRole_Id());
		paramMap.put("intervenor_Company_Name", uprb.getIntervenor_Company_Name());
		paramMap.put("intervenor_Company_Address", uprb.getIntervenor_Company_Address());
		paramMap.put("consolidated_A_No", uprb.getConsolidated_A_No());
		paramMap.put("casedocket_email_preferences", uprb.getCasedocket_email_preferences());

		List<?> resultList = access.queryWithParams(query, paramMap);

		if (null != resultList && resultList.size() > 0){
			isExists = true;
		}
		
		return isExists;
	}

	@Transactional
	public <T> void delete(T t) throws Exception {
		access.delete(t);
	}

	@Transactional
	public <T> T save(T t) {
		return access.save(t);
	}

	@Transactional
	public List<User_Info> getUser_Info_List_ByArrayOfUser_Id(
			String[] agency_User_Ids) throws Exception {
		List<User_Info> user_Info_List = new ArrayList<User_Info>();

		User_Info user_Info = null;
		for (String eachUser_Id : agency_User_Ids) {
			user_Info = getUser_Info_By_User_Id(eachUser_Id.trim());
			
			if (null != user_Info){
				user_Info_List.add(user_Info);
			}
			
		}

		return user_Info_List;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<User_Protest_Role_Bridge> getUser_Protest_Role_Bridge_List_BasedOnUser_Id(
			String user_Id) {
		String query = "from User_Protest_Role_Bridge where user_Id = :user_Id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user_Id", user_Id);

		List<?> list = access.queryWithParams(query, map);

		if (list != null && list.size() > 0) {
			return (List<User_Protest_Role_Bridge>) list;
		}

		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<User_Protest_Role_Bridge> getUser_Protest_Role_Bridge_List_BasedOnUser_IdAndProtestId(
			String user_Id,String a_No) {
		String query = "from User_Protest_Role_Bridge where user_Id = :user_Id and a_No =:a_No";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user_Id", user_Id);
		map.put("a_No", a_No);

		List<?> list = access.queryWithParams(query, map);

		if (list != null && list.size() > 0) {
			return (List<User_Protest_Role_Bridge>) list;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<GAO_User> getGAO_UserList(String user_Id) {
		String query = "from GAO_User where user_Id = :user_Id";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("user_Id", user_Id);

		List<?> resultList = access.queryWithParams(query, paramMap);

		if (resultList != null && resultList.size() > 0) {
			return (List<GAO_User>) resultList;
		}

		return null;
	}

	@Transactional
	@SuppressWarnings("unchecked")
	public GAO_User getGAO_User_BasedOnGAOUserId(Integer id) {
		String query = "from GAO_User where id = :id";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", id);

		List<?> resultList = null;
		try {
			resultList = access.queryWithParams(query, paramMap);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (resultList != null && resultList.size() > 0) {
			return ((List<GAO_User>) resultList).get(0);
		}

		return null;
	}

	@Transactional
	@SuppressWarnings("unchecked")
	public GAO_User getGAO_User_BasedOnEPDSUserId(String user_Id) {
		String query = "from GAO_User where user_Id = :user_Id";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("user_Id", user_Id);

		List<?> resultList = null;
		try {
			resultList = access.queryWithParams(query, paramMap);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (resultList != null && resultList.size() > 0) {
			return ((List<GAO_User>) resultList).get(0);
		}

		return null;
	}

	@Transactional
	public List<User_Info> getSupervisorInfoByGroupId(Long attorneyGroupId) {
		String query = "from User_Info a, GAO_User b where b.group_No = :attorneyGroupId and a.user_Id = b.user_Id and a.role_id = 8";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("attorneyGroupId", Integer.valueOf(attorneyGroupId.intValue()));

		List<?> resultList = access.queryWithParams(query, map);
		List<User_Info> user_info_list = Util.getDesiredClassObjectList(
				resultList, User_Info.class);
		List<GAO_User> gao_user_list = Util.getDesiredClassObjectList(
				resultList, GAO_User.class);

		if (user_info_list != null && user_info_list.size() > 0) {
			User_Info attorney_info = user_info_list.get(0);
			GAO_User gao_user = gao_user_list.get(0);

			attorney_info.setGroup_No(gao_user.getGroup_No());

			return user_info_list;
		}

		return null;
	}

	@Transactional
	public User_Info getAttorney_info(Long attorney_id) {
		String query = "from User_Info a, GAO_User b where b.id = :id and a.user_Id = b.user_Id";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", Integer.valueOf(attorney_id.intValue()));

		List<?> resultList = access.queryWithParams(query, map);
		List<User_Info> user_info_list = Util.getDesiredClassObjectList(
				resultList, User_Info.class);
		List<GAO_User> gao_user_list = Util.getDesiredClassObjectList(
				resultList, GAO_User.class);

		if (user_info_list != null && user_info_list.size() > 0) {
			User_Info attorney_info = user_info_list.get(0);
			GAO_User gao_user = gao_user_list.get(0);

			attorney_info.setGroup_No(gao_user.getGroup_No());
			attorney_info
					.setGao_user_id(Integer.valueOf(attorney_id.intValue()));

			return user_info_list.get(0);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<User_Info> getListOfAgencyUserInfoByAgencyInfoIdsAndRoleId(List<Integer> agencyInfoIds,
			Integer role_id) {
		String query = "from User_Info where firm_id IN (:firmIds) and role_id = :role_id";

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("firmIds", agencyInfoIds);
		paramMap.put("role_id", role_id);

		List<?> resultList = access.queryWithParams(query, paramMap);

		if (resultList != null && resultList.size() > 0) {
			return (List<User_Info>) resultList;
		}

		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<User_Info> getListOfAgencyUserInfo(List<Integer> agencyInfoIds) {
		String query = "from User_Info where firm_id IN (:firmIds) and role_id IN (5,6)";

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("firmIds", agencyInfoIds);

		List<?> resultList = access.queryWithParams(query, paramMap);

		if (resultList != null && resultList.size() > 0) {
			return (List<User_Info>) resultList;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public User_Info getPLCGEmail() {
		String query = "from User_Info where first_Name = 'PLCG' and firm_name = 'GAO'and role_id = 7";

		Map<String, Object> paramMap = new HashMap<String, Object>();

		List<?> resultList = access.queryWithParams(query, paramMap);

		if (resultList != null && resultList.size() > 0) {
			return ((List<User_Info>) resultList).get(0);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<User_Info> getListOfEPDSUserByRoleId(Integer roleId) {
		String query = "from User_Info where role_id = :roleId";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("roleId", roleId);

		List<?> resultList = access.queryWithParams(query, paramMap);
		List<User_Info> epdsUsers = (List<User_Info>) resultList;

		return epdsUsers;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<User_Protest_Role_Bridge> getUserProtestRoleBridgeListBasedOnVendorUserRole(
			String user_id, Protest_Info protest_info) {
		String query = "from User_Protest_Role_Bridge where a_No = :a_no";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("a_no", protest_info.getA_No());

		if ("PROTESTER".equalsIgnoreCase(protest_info.getRole())
				|| 
				"SECONDARY PROTESTER".equalsIgnoreCase(protest_info.getRole())) {
			query += " and (role_Id = 1 or role_Id = 4)";
		} else if ("INTERVENOR".equalsIgnoreCase(protest_info.getRole())
				|| "SECONDARY INTERVENOR".equalsIgnoreCase(protest_info.getRole())) {
			User_Protest_Role_Bridge intervenorUserProtestRoleBridge = getUser_Protest_Role_Bridge(
					protest_info.getA_No(), user_id);
			String intervenorCompanyName = intervenorUserProtestRoleBridge
					.getIntervenor_Company_Name();

			if (intervenorCompanyName == null) {
				return null;
			}

			query += " and (role_Id = 2 or role_Id = 9) and intervenor_Company_Name = :intervenorCompanyName";
			paramMap.put("intervenorCompanyName", intervenorCompanyName);
		} else {
			return null;
		}

		if (protest_info.getIsUserConsolidated().equalsIgnoreCase("N")) {
			query += " and consolidated_A_No is null";
		} else {
			query += " and consolidated_A_No is not null";
		}

		List<?> list = access.queryWithParams(query, paramMap);

		if (list != null && list.size() > 0) {
			return (List<User_Protest_Role_Bridge>) list;
		}

		return null;
	}

	@Transactional
	public void removeUserProtestRoleBridgeRecordsForACase(String a_No)
			throws Exception {
		List<User_Protest_Role_Bridge> userProtestRoleBridgeRecords = getUser_Protest_Role_Bridge_List_BasedOnProtestId(a_No);

		if (userProtestRoleBridgeRecords != null
				&& userProtestRoleBridgeRecords.size() > 0) {
			for (User_Protest_Role_Bridge eachUserProtestRoleBridge : userProtestRoleBridgeRecords) {
				access.delete(eachUserProtestRoleBridge);
			}
		}

	}

	@Transactional
	public User_Info getUserInfoForACaseByRoleId(String aNo, Integer roleId) {
		String query = "from User_Info a, User_Protest_Role_Bridge b where b.a_No = :aNo and a.user_Id = b.user_Id and b.role_Id = :roleId";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("aNo", aNo);
		map.put("roleId", roleId);

		List<?> resultList = access.queryWithParams(query, map);

		List<User_Info> user_InfoList = Util.getDesiredClassObjectList(
				resultList, User_Info.class);

		User_Info userInfo = null;
		if (user_InfoList != null && user_InfoList.size() > 0) {
			userInfo = user_InfoList.get(0);
		}

		return userInfo;
	}

	@Transactional
	public User_Protest_Role_Bridge getUserProtestRoleBridgeWithRoleDesc(
			String aNo, String userId) {
		String query = "from User_Protest_Role_Bridge a, User_Role b where a.a_No = :a_No and a.user_Id = :user_Id and a.role_Id = b.role_Id";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("a_No", aNo);
		paramMap.put("user_Id", userId);

		List<?> resultList = access.queryWithParams(query, paramMap);
		List<User_Role> userRoleList = Util.getDesiredClassObjectList(
				resultList, User_Role.class);
		List<User_Protest_Role_Bridge> userProtestRoleBridgeList = Util
				.getDesiredClassObjectList(resultList,
						User_Protest_Role_Bridge.class);

		User_Protest_Role_Bridge userProtestRoleBridge = null;
		User_Role userRole;
		if (userProtestRoleBridgeList.size() > 0) {
			userProtestRoleBridge = userProtestRoleBridgeList.get(0);
			userRole = userRoleList.get(0);
			userProtestRoleBridge.setRoleDesc(userRole.getRole_Desc());
		}

		return userProtestRoleBridge;
	}

	public User_Protest_Role_Bridge getIntervenorUPRBByIntervenorCompanyNameAndIntervenorCompanyAddress(String a_No,
			String company_name, String company_Address) {
		
		String query = "from User_Protest_Role_Bridge where a_No = :a_No and role_Id = 2 and intervenor_Company_Name = :companyName and intervenor_Company_Address = :companyAddress";

		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		paramMap.put("a_No", a_No);
		paramMap.put("companyName", company_name);
		paramMap.put("companyAddress", company_Address);

		List<?> resultList = access.queryWithParams(query, paramMap);
		User_Protest_Role_Bridge user_Protest_Role_BridgeList = new User_Protest_Role_Bridge();
		if (null != resultList && resultList.size() > 0){
			user_Protest_Role_BridgeList = (User_Protest_Role_Bridge) resultList.get(0);
		}


		return user_Protest_Role_BridgeList;
	}

	public boolean checkIfGAOIdExists(Integer gaoId) {
		boolean isExists = false;

		String query = "from GAO_User where id = :id";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", gaoId);

		List<?> resultList = access.queryWithParams(query, paramMap);

		if (null != resultList && resultList.size() > 0) {
			isExists = true;
		}

		return isExists;
	}

	@SuppressWarnings("unchecked")
	public List<Invited_User> getInvitedUserInfoListByEmail(String oldEmail) {

		String query = "from Invited_User where invitee_Email = :email";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("email", oldEmail);

		List<?> resultList = access.queryWithParams(query, paramMap);

		if (null != resultList && resultList.size() > 0) {
			return ((List<Invited_User>) resultList);
		} else {
			return new ArrayList<Invited_User>();
		}

	
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<User_Info> getListOfSupervisorandAgencyPOCUserInfoList() {
		
		String query = "from User_Info where role_id in (5,8) ";
		Map<String, Object> paramMap = new HashMap<String, Object>();

		List<?> resultList = access.queryWithParams(query, paramMap);

		if (null != resultList && resultList.size() > 0) {
			return ((List<User_Info>) resultList);
		} else {
			return new ArrayList<User_Info>();
		}
	}
}
