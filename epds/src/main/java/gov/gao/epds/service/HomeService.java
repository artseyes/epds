package gov.gao.epds.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.gao.epds.dao.Agency_Info_DAO;
import gov.gao.epds.dao.File_Info_DAO;
import gov.gao.epds.dao.Protest_Info_DAO;
import gov.gao.epds.dao.User_Info_DAO;
import gov.gao.epds.dto.AgencyRepInfo;
import gov.gao.epds.dto.DashboardDto;
import gov.gao.epds.enums.UserRoles;
import static gov.gao.epds.enums.UserRoles.*;
import gov.gao.epds.persistence.entity.Doc_Info;
import gov.gao.epds.persistence.entity.GAO_User;
import gov.gao.epds.persistence.entity.Invited_User;
import gov.gao.epds.persistence.entity.Protest_Info;
import gov.gao.epds.persistence.entity.User_Info;
import gov.gao.epds.session.EpdsSession;
import gov.gao.epds.utils.Date_Util;
import gov.gao.epds.utils.GlobalParams;
import gov.gao.epds.utils.Protest_info_util;

@Service
public class HomeService {
	@Autowired
	private Protest_Info_DAO protest_Info_DAO;
	@Autowired
	private User_Info_DAO user_Info_DAO;
	@Autowired
	private Agency_Info_DAO agency_Info_DAO;
	@Autowired
	private File_Info_DAO file_Info_DAO;
	
	@Autowired
	private ProtestInfoService protestInfoService;
	
	@Autowired
	private DashboardService dashboardService;
	
	@Autowired
	private CaseDocketSheetService caseDocketSheetService;

	public List<Protest_Info> getProtestInfoList(User_Info user_Info,
			String protestTableType, String user_Role, DashboardDto dashboardDto) throws Exception {
		List<Protest_Info> protest_InfoList = null;
		String user_Id = user_Info.getUser_Id();
		Integer firm_Id = user_Info.getFirm_id();
		UserRoles role = UserRoles.getByCode(user_Info.getRole_id());
		if (dashboardDto.getCaseStatusList() == null && role == PROTESTER){
			dashboardDto.setCaseStatusList("OPEN,CLOSED");
		}
		
		if (role == GAO_SUPERVISOR && dashboardDto.getAttorneyGroupIds()  == null){
			List<Integer> groupList = new ArrayList<Integer>();
			groupList.add(user_Info.getGroup_No());
			dashboardDto.setAttorneyGroupIds(groupList);
		}
		protest_InfoList = protest_Info_DAO.getProtestInfoList(user_Id,
				protestTableType, user_Role, firm_Id,dashboardDto);

		/*if (protest_InfoList != null) {
			fillUpEachProtestInfoWithAgencyName(protest_InfoList);
			addSuffixToEachProtestIfNeeded(protest_InfoList);
		}*/

		return protest_InfoList;
	}

	
	private void addSuffixToEachProtestIfNeeded(
			List<Protest_Info> protest_InfoList) {
		for (Protest_Info eachProtest_Info : protest_InfoList) {
			addSuffixToProtestIfNeeded(eachProtest_Info);
		}
	}

	private void addSuffixToProtestIfNeeded(Protest_Info protestInfo) {
		String typeOfProtest = protestInfo.getCase_Type();

		if (typeOfProtest == null) {
			// should not ever be null, but had a bug in gactrack that was setting it, so log if happens and avoid a NPE
			System.err.println("Case Type is NULL for A#: " + protestInfo.getA_No());
			return;
		}

		if (typeOfProtest.equalsIgnoreCase("RECONSIDERATION")) {
			protestInfo.setCompany_Name((protestInfo.getCompany_Name() + "-RECON"));
		}
		if (protestInfo.getB_No() != null) {
			if (typeOfProtest.equalsIgnoreCase("RECONSIDERATION")) {
				protestInfo.setB_No(protestInfo.getB_No() + "-RECON");
				protestInfo.setCompany_Name((protestInfo.getCompany_Name() + "-RECON"));
			} else if (typeOfProtest.equalsIgnoreCase("ENTITLEMENT")) {
				protestInfo.setB_No(protestInfo.getB_No() + "-ENT");
				protestInfo.setCompany_Name((protestInfo.getCompany_Name() + "-ENT"));
			} else if (typeOfProtest.equalsIgnoreCase("COST-CLAIM") || typeOfProtest.toUpperCase(Locale.ENGLISH).contains("COST")) {
				protestInfo.setB_No(protestInfo.getB_No() + "-COST");
				protestInfo.setCompany_Name((protestInfo.getCompany_Name() + "-COST"));
			}
		}

	}

	/**
	 * @param protest_InfoMap
	 *            : map of a-number to all protest_infos accessible to the user
	 * @param protest_InfoList
	 *            : list of cases that user has access to
	 * @param user_Role
	 *            : global role of the user (there is slight difference between
	 *            global and case-specific role)
	 * @return
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public List<Protest_Info> assignParentAndChildRelation(
			Map<String, Protest_Info> protest_InfoMap,
			List<Protest_Info> protest_InfoList, String user_Role)
			throws JsonGenerationException, JsonMappingException, IOException {

		assignChildProtestOrSupplementalProtestIfAny(protest_InfoMap,
				protest_InfoList);
		assignConsolidatedProtestsToEachProtest_InfoWhichIsConslidated(protest_InfoList);

		List<Protest_Info> finalProtest_InfoList = removeChildProtestFromList(protest_InfoList);

		if (user_Role.equalsIgnoreCase("PROTESTER")) {
			makeProtesterFiledProtestParentIfProtestIsJoined(finalProtest_InfoList);
		}

		sortChildrenProtest_InfoList(finalProtest_InfoList);

		return finalProtest_InfoList;
	}

	/**
	 * if protest_info is parent, it will be populated with list of
	 * 
	 * @param protest_InfoList
	 *            : contains non-child protest_info as root node elements
	 */
	private void assignConsolidatedProtestsToEachProtest_InfoWhichIsConslidated(
			List<Protest_Info> protest_InfoList) {
		List<Protest_Info> whole_ListOf_ConsolidatedCaseInfo = null;

		for (Protest_Info eachProtest_Info : protest_InfoList) {
			if (eachProtest_Info.getChildren_Protest_InfoList().size() > 0) {
				whole_ListOf_ConsolidatedCaseInfo = new ArrayList<Protest_Info>();
				whole_ListOf_ConsolidatedCaseInfo.add(eachProtest_Info);

				for (Protest_Info eachProtest_Info2 : eachProtest_Info
						.getChildren_Protest_InfoList()) {
					whole_ListOf_ConsolidatedCaseInfo.add(eachProtest_Info2);
				}

				assignConsolidatedProtestInfosToProtestInfoAsRequired(
						whole_ListOf_ConsolidatedCaseInfo, eachProtest_Info);
				for (Protest_Info eachProtest_Info2 : eachProtest_Info
						.getChildren_Protest_InfoList()) {
					assignConsolidatedProtestInfosToProtestInfoAsRequired(
							whole_ListOf_ConsolidatedCaseInfo,
							eachProtest_Info2);
				}
			}
		}
	}

	/**
	 * case example: a is parent of b and c.
	 * 
	 * @param whole_ListOf_ConsolidatedCaseInfo
	 *            : this list contains all the protest_info that are
	 *            consolidated. In above case example, this list will contain a,
	 *            b and c
	 * @param protest_Info
	 *            : protest_info to which other (not including itself)
	 *            consolidated protest_info/s need to assigned.
	 *            a.listOf_ConsolidatedProtest_Info contains b and c.
	 */
	private void assignConsolidatedProtestInfosToProtestInfoAsRequired(
			List<Protest_Info> whole_ListOf_ConsolidatedCaseInfo,
			Protest_Info protest_Info) {
		List<Protest_Info> listOf_ConsolidatedCaseInfo_ForThisProtest_Info = new ArrayList<Protest_Info>();

		Protest_Info consolidated_Protest_Info = null;
		for (Protest_Info each : whole_ListOf_ConsolidatedCaseInfo) {
			if (!each.getA_No().equals(protest_Info.getA_No())) {
				consolidated_Protest_Info = new Protest_Info(each);
				listOf_ConsolidatedCaseInfo_ForThisProtest_Info
						.add(consolidated_Protest_Info);
			}
		}

		protest_Info
				.setListOf_ConsolidatedProtest_Info(listOf_ConsolidatedCaseInfo_ForThisProtest_Info);
	}

	
	public List<Invited_User> getInvitedSecondaryProtester(String userId) {
		List <Invited_User> invitedUsers = user_Info_DAO.getInvitedSecondaryProtester(userId);
		//need to test it
		populateConsolidatedBnumbers(invitedUsers);
		return invitedUsers;
		
		
	}
	private void sortChildrenProtest_InfoList(
			List<Protest_Info> finalProtest_InfoList) {
		for (Protest_Info eachProtest_Info : finalProtest_InfoList) {
			Collections.sort(eachProtest_Info.getChildren_Protest_InfoList(),
					Protest_Info.B_No_Comparator_AlphabeticAscendingOrder);
		}
	}

	/**
	 * @param nonChildpPotestInfoList
	 *            : each list member is non-child. If any protest_info is a
	 *            parent protest_info then it became parent by virtue of cases
	 *            being added
	 */
	private void makeProtesterFiledProtestParentIfProtestIsJoined(
			List<Protest_Info> nonChildpPotestInfoList) {
		List<Protest_Info> whole_ListOf_ConsolidatedCaseInfo = null;
		List<Protest_Info> consolidatedProtestInfoListWithEachElementAsProtesterFiledProtest = new ArrayList<Protest_Info>();
		Protest_Info eachProtest_Info = null;
		Protest_Info parent_Protest_Info = null;

		for (int i = 0; i < nonChildpPotestInfoList.size(); i++) {
			eachProtest_Info = nonChildpPotestInfoList.get(i);

			if (eachProtest_Info.getChildren_Protest_InfoList().size() > 0) {
				whole_ListOf_ConsolidatedCaseInfo = eachProtest_Info
						.getChildren_Protest_InfoList();
				eachProtest_Info
						.setChildren_Protest_InfoList(new ArrayList<Protest_Info>());
				whole_ListOf_ConsolidatedCaseInfo.add(eachProtest_Info);

				parent_Protest_Info = getParent_Protest_Info(whole_ListOf_ConsolidatedCaseInfo);

				if (parent_Protest_Info.getChildren_Protest_InfoList().size() > 0) {
					nonChildpPotestInfoList.remove(i--);
					consolidatedProtestInfoListWithEachElementAsProtesterFiledProtest
							.add(parent_Protest_Info);
				}

			}
		}

		nonChildpPotestInfoList
				.addAll(consolidatedProtestInfoListWithEachElementAsProtesterFiledProtest);
	}

	/**
	 * @param whole_ListOf_ConsolidatedCaseInfo
	 * @return
	 */
	private Protest_Info getParent_Protest_Info(
			List<Protest_Info> whole_ListOf_ConsolidatedCaseInfo) {
		Collections.sort(whole_ListOf_ConsolidatedCaseInfo,
				Protest_Info.B_No_Comparator_AlphabeticAscendingOrder);
		Protest_Info parent_Protest_Info = null;

		for (int i = 0; i < whole_ListOf_ConsolidatedCaseInfo.size(); i++) {
			parent_Protest_Info = whole_ListOf_ConsolidatedCaseInfo.get(i);

			if ((parent_Protest_Info.getIsUserConsolidated() != null && parent_Protest_Info
					.getIsUserConsolidated().equalsIgnoreCase("N"))
					&& (parent_Protest_Info.getRole() != null && parent_Protest_Info
							.getRole().equalsIgnoreCase("PROTESTER"))) {

				for (int j = 0; j < whole_ListOf_ConsolidatedCaseInfo.size(); j++) {
					if (i != j) {
						parent_Protest_Info.getChildren_Protest_InfoList().add(
								whole_ListOf_ConsolidatedCaseInfo.get(j));
					}
				}

				break;
			}
		}

		if (parent_Protest_Info.getChildren_Protest_InfoList().size() == 0) {
			Protest_Info child_Protest_Info = null;
			parent_Protest_Info = whole_ListOf_ConsolidatedCaseInfo
					.get(whole_ListOf_ConsolidatedCaseInfo.size() - 1);

			for (int i = 0; i < whole_ListOf_ConsolidatedCaseInfo.size() - 1; i++) {
				child_Protest_Info = whole_ListOf_ConsolidatedCaseInfo.get(i);
				parent_Protest_Info.getChildren_Protest_InfoList().add(
						child_Protest_Info);
			}
		}

		return parent_Protest_Info;
	}

	/**
	 * @param protest_InfoList
	 *            : this list contains both parent and child protest_info as
	 *            root node element
	 * @return : final list of protest_info that doesn't have child protest_info
	 *         as the root node element (
	 */
	private List<Protest_Info> removeChildProtestFromList(
			List<Protest_Info> protest_InfoList) {
		
		List<Protest_Info> finalProtest_InfoList = new ArrayList<Protest_Info>();
		for (Protest_Info eachProtest_Info : protest_InfoList) {
			if (eachProtest_Info.getParent_A_No() == null
					|| eachProtest_Info.getParent_A_No().equals("")) {
				finalProtest_InfoList.add(eachProtest_Info);
			}
		}

		// assignSupplementalB_NosIfAny(finalProtest_InfoList);
		return finalProtest_InfoList;
	}

	/**
	 * @param protest_InfoMap
	 *            : map of a-number to protest_info. Helps to extract parent
	 *            protest_info for a child protest_info.
	 * @param protest_InfoList
	 *            : list of all protests that user can see. For each protest in
	 *            an iteration, 1. find parent protest_info if any 2. If parent
	 *            protest_info is found, this protest in the iteration will be
	 *            assigned as one of the children protest_info 3. If the protest
	 *            is supplemental, handle differently as shown below
	 */
	private void assignChildProtestOrSupplementalProtestIfAny(
			Map<String, Protest_Info> protest_InfoMap,
			List<Protest_Info> protest_InfoList) {
	
		for (Protest_Info eachProtest_Info : protest_InfoList) {
			
			if (eachProtest_Info.getParent_A_No() != null
					&& !eachProtest_Info.getParent_A_No().equals("")) {
				Protest_Info parentProtest_Info = protest_InfoMap
						.get(eachProtest_Info.getParent_A_No());

				if (parentProtest_Info != null) {
					
					if (eachProtest_Info.getCase_Type().equalsIgnoreCase("Supplemental") 
							/*|| protestInfoService.checkIfThisProtestIsSupplemental(parentProtest_Info, eachProtest_Info)*/) {
						
						Protest_info_util.populateSupplementalNumbersForEachCaseInDashboard(
								eachProtest_Info, parentProtest_Info);
					} else {
						//GE - 1414 Check parent and child A_NO
						if(parentProtest_Info.getA_No().equals(eachProtest_Info.getA_No())) {
							parentProtest_Info.setParent_A_No(null);
						} else {
							parentProtest_Info.getChildren_Protest_InfoList().add(
									eachProtest_Info);
						}
					}
				}
			}
		}
	}

	

	public List<Protest_Info> fillUpEachProtestInfoWithAgencyName(
			List<Protest_Info> protest_InfoList) throws Exception {
		String agencyName = "";
		List<Protest_Info> new_Protest_InfoList = new ArrayList<Protest_Info>();

		if (protest_InfoList == null)
			return null;

		for (Protest_Info eachProtest : protest_InfoList) {
			agencyName = agency_Info_DAO.getAgencyName(eachProtest.getAgency_Info_Id());
			eachProtest.setAgency_Name(agencyName);
			new_Protest_InfoList.add(eachProtest);
		}

		return new_Protest_InfoList;
	}

	

	

	

	

	public Map<String, Protest_Info> getProtestInfoMap(
			List<Protest_Info> protest_InfoList) throws Exception {
		if (protest_InfoList == null) {
			return null;
		}

		Map<String, Protest_Info> protestInfoMap = new HashMap<String, Protest_Info>();
		for (Protest_Info eachProtestInfo : protest_InfoList) {
			
			if (eachProtestInfo.getSubmissionDateTime() == null){
				eachProtestInfo.setSubmissionDateTime(Date_Util.getESTTimeStampInLong(eachProtestInfo.getSubmission_Date()));	
				protest_Info_DAO.updateProtest_Info(eachProtestInfo);
			}
			
			
			
			eachProtestInfo.setAgency_Name(agency_Info_DAO.getAgencyName(eachProtestInfo.getAgency_Info_Id()));
			addSuffixToProtestIfNeeded(eachProtestInfo);
			protestInfoMap.put(eachProtestInfo.getA_No(), eachProtestInfo);
		}

		return protestInfoMap;
	}

	

	

	public Integer getGroupId(User_Info user_Info) {
		int roleId = 0;

		if (user_Info.getRole_id().equals(GAO_SUPERVISOR.getCode())) {
			List<GAO_User> gao_UserList = user_Info_DAO
					.getGAO_UserList(user_Info.getUser_Id());
			if (gao_UserList != null) {

				roleId = gao_UserList.get(0).getGroup_No();
			}
		}

		return roleId;
	}

	

	public void populateConsolidatedBnumbers(List<Invited_User> invited_Users){
		
		for (Invited_User eachInvitedUser : invited_Users ){
			List<Protest_Info> listOfConsolidatedProtestInfo = protest_Info_DAO.getConsolidatedProtestInfoListWithParentAndChildSupplementalProtests(eachInvitedUser.getA_No());
			
			for (Protest_Info  eachProtestInfo : listOfConsolidatedProtestInfo){
				
				if (!eachInvitedUser.getA_No().equalsIgnoreCase(eachProtestInfo.getA_No())){
			
					if (eachInvitedUser.getConsolidateBNumbers() == null) {
						eachInvitedUser.setConsolidateBNumbers("; " + eachProtestInfo.getB_No());
					} else {
						eachInvitedUser.setConsolidateBNumbers(eachInvitedUser.getConsolidateBNumbers() + "; " + eachProtestInfo.getB_No());
					}
				}
			}
		}
			
	}
	public List<Invited_User> getInviteAcceptedSecondaryProtester(String userId) {
		return user_Info_DAO.getInviteAcceptedSecondaryProtester(userId);
	}


	public String getProtestTableType(String protestTableType, User_Info user_Info) {
		UserRoles role = UserRoles.getByCode(user_Info.getRole_id());

		if ((protestTableType == null
				|| protestTableType.equalsIgnoreCase("undefined")) && role == GAO_ADMIN){
			protestTableType = "unassigned";
		}else if (role != GAO_ADMIN){
			
			switch (role) {
				case GAO_SUPERVISOR:
					protestTableType = "assigned";
					break;
				case AGENCY_ADMIN:
					protestTableType = "allAgencyCases";
					break;
				default:
					protestTableType = "userIdBased";
					break;
			}
		}

		return protestTableType;
	}

	@SuppressWarnings("unchecked")
	public void setWholeDocInfoList(HttpServletRequest request) {
		List<Doc_Info> wholeDocInfoList = (List<Doc_Info>) GlobalParams.globalParam
				.get("whole_Doc_Info_List");
		if (wholeDocInfoList == null) {
			wholeDocInfoList = file_Info_DAO.getDoc_InfoList();
			GlobalParams.globalParam.put("whole_Doc_Info_List",
					wholeDocInfoList);
		}
	}

	
	
	public void populateAgencyRepsForProtestInfoList(List<Protest_Info> protest_InfoList) throws Exception {
		List<AgencyRepInfo> agencyRepInfos;
		AgencyRepInfo agencyRepInfo;
		List<User_Info> agencyReps;
		List<Integer> agencyInfoIds;
		
		for (Protest_Info eachProtestInfo : protest_InfoList){
			agencyRepInfos = new ArrayList<AgencyRepInfo>();
			agencyInfoIds = dashboardService.getListOfAssociatedAgencyIdsByAgencyInfoId(eachProtestInfo.getAgency_Info_Id(),true);
			agencyReps = user_Info_DAO.getAgencyRepUserInfoListByANum(eachProtestInfo.getA_No());
			
			if (agencyReps != null & !agencyReps.isEmpty()){
				
				for (User_Info eachAgencyRepInfo : agencyReps){
					
					if (agencyInfoIds.contains(eachAgencyRepInfo.getFirm_id())){
						agencyRepInfo = new AgencyRepInfo();
						agencyRepInfo.setLastName(eachAgencyRepInfo.getLast_Name());
						agencyRepInfo.setFirstName(eachAgencyRepInfo.getFirst_Name());
						agencyRepInfos.add(agencyRepInfo);
					}
					
				}
				
				eachProtestInfo.setAgencyRepInfos(agencyRepInfos);
			}
			
			
		}
		
	}

}
