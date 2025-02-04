package gov.gao.epds.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import gov.gao.epds.dao.Agency_Info_DAO;
import gov.gao.epds.dao.File_Info_DAO;
import gov.gao.epds.dao.GC_Track_Service_DAO;
import gov.gao.epds.dao.Protest_Info_DAO;
import gov.gao.epds.dao.User_Info_DAO;
import gov.gao.epds.dto.AdvanceSearchDTO;
import gov.gao.epds.dto.CaseCompletionStatus;
import gov.gao.epds.dto.RemoveCaseDto;
import gov.gao.epds.enums.UserRoles;
import static gov.gao.epds.enums.UserRoles.*;
import gov.gao.epds.filestorage.SFTP;
import gov.gao.epds.persistence.entity.Agency_Info;
import gov.gao.epds.persistence.entity.Doc_Info;
import gov.gao.epds.persistence.entity.File_Info;
import gov.gao.epds.persistence.entity.GAO_User;
import gov.gao.epds.persistence.entity.Invited_User;
import gov.gao.epds.persistence.entity.Protest_Dm_Info;
import gov.gao.epds.persistence.entity.Protest_Info;
import gov.gao.epds.persistence.entity.Tier_1_Agency;
import gov.gao.epds.persistence.entity.Tier_2_Agency;
import gov.gao.epds.persistence.entity.User_Info;
import gov.gao.epds.persistence.entity.User_Protest_Role_Bridge;
import gov.gao.epds.utils.EPDS_FileUtils;
import gov.gao.epds.utils.GlobalFields;
import gov.gao.epds.utils.Protest_info_util;
import gov.gao.epds.utils.Util;

@Service
@Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
public class DashboardService {

	private final static Logger logger = LoggerFactory.getLogger(DashboardService.class);
	private List<String> listOfPartyMemberUserIds;

	@Autowired
	private Protest_Info_DAO protest_Info_DAO;
	@Autowired
	private File_Info_DAO file_Info_DAO;
	@Autowired
	private User_Info_DAO user_Info_DAO;
	@Autowired
	private Agency_Info_DAO agency_Info_DAO;
	@Autowired
	private ProtestInfoService protestInfoService;
	@Autowired
	private GC_Track_Service_DAO gc_Track_Service_DAO;

	public List<Tier_1_Agency> getTier_1_Agency_List() throws Exception {
		return agency_Info_DAO.getTier_1_Agency_List();
	}

	public User_Info getAttorneyInfo(String a_No) {
		return user_Info_DAO.getAttorneyInfo(a_No);
	}

	/**
	 * Override the company name for "AGENCY" submitted document by always
	 * getting the company name from agency userInfo firm Id. This will make
	 * sure the the current agency name will be shown in case docket sheet for
	 * agency submitted documents.
	 * 
	 * @param fileInfoList
	 * @throws Exception
	 */
	public void populateAgencyNameForAgencyInFileInfoList(List<File_Info> fileInfoList) throws Exception {

		for (File_Info file_Info : fileInfoList) {

			if (file_Info.getSubmitter_Role().equalsIgnoreCase("AGENCY")) {
				User_Info submitterUserInfo = user_Info_DAO.getUser_Info_By_User_Id(file_Info.getSubmitter_User_Id());

				if (submitterUserInfo != null && submitterUserInfo.getFirm_id() != null){
					file_Info.setCompany_Name(agency_Info_DAO.getAgencyName(submitterUserInfo.getFirm_id()));
				}
				
			}
		}

	}

	public List<File_Info> getFileInfoList(Protest_Info protest_Info, String user_Id, Boolean isFileVisiblityNeeded)
			throws Exception {
		List<File_Info> file_InfoList = file_Info_DAO.getFileEntitybyProtestId(protest_Info.getA_No());

		List<File_Info> file_InfoList_FromOtherConsolidatedCases = file_Info_DAO
				.getfile_InfoList_FromOtherConsolidatedCases(protest_Info.getA_No());

		if (file_InfoList_FromOtherConsolidatedCases != null && !file_InfoList_FromOtherConsolidatedCases.isEmpty()) {
			file_InfoList.addAll(file_InfoList_FromOtherConsolidatedCases);
		}

		/* file_InfoList = getDupsRemovedFileInfoList(file_InfoList); */

		file_InfoList = populateFileInfoTransientAttributes(protest_Info, user_Id, isFileVisiblityNeeded,
				file_InfoList);

		// addSupplementalPrefixIfNeeded(file_InfoList);

		return file_InfoList;
	}

	/**
	 * @param protest_Info
	 * @param user_Id
	 * @param isFileVisiblityNeeded
	 * @param file_InfoList
	 * @return
	 * @throws Exception
	 */
	public List<File_Info> populateFileInfoTransientAttributes(Protest_Info protest_Info, String user_Id,
			Boolean isFileVisiblityNeeded, List<File_Info> file_InfoList) throws Exception {
		file_InfoList = getFileInfoListWith_DocumentType_And_SubmitterRole_And_AlertStatus(file_InfoList, user_Id);
		if (isFileVisiblityNeeded) {
			file_InfoList = getFileInfoListWithVisibility(file_InfoList, protest_Info, user_Id);
		}

		file_InfoList = getFileInfoList_WithAttorneyNoteAndSubmittedDate(file_InfoList);
		return file_InfoList;
	}

	public List<File_Info> getFinalFileInfoList(Protest_Info protest_Info, String user_Id) throws Exception {

		List<File_Info> file_InfoList = file_Info_DAO.getFileEntitybyProtestId(protest_Info.getA_No());

		List<File_Info> file_InfoList_FromOtherConsolidatedCases = file_Info_DAO
				.getfile_InfoList_FromOtherConsolidatedCases(protest_Info.getA_No());

		if (file_InfoList_FromOtherConsolidatedCases != null) {
			file_InfoList.addAll(file_InfoList_FromOtherConsolidatedCases);
		}

		return file_InfoList;
	}

	private List<File_Info> getFileInfoList_WithAttorneyNoteAndSubmittedDate(List<File_Info> file_InfoList) {
		List<File_Info> newFile_InfoList = new ArrayList<File_Info>();
		String attorney_Note = "";
		String[] attorney_NoteChunks;
		String transient_Note = "";
		String transient_Attorney_Note_Date = "";
		String transient_Date = "";
		String transient_Time = "";
		
		
		for (File_Info eachFile_Info : file_InfoList) {
			String comments = eachFile_Info.getComments();
			String[] comments_NoteChunks;
			String transient_Comments = "";
			String transient_Comments_Date="";
			attorney_Note = eachFile_Info.getAttorney_Note();
			if (attorney_Note != null && !attorney_Note.equalsIgnoreCase("null") && !attorney_Note.equals("")) {
				attorney_NoteChunks = attorney_Note.split(":::");
				if (attorney_NoteChunks != null && attorney_NoteChunks.length > 1) {
					transient_Note = attorney_NoteChunks[0];
					transient_Attorney_Note_Date = attorney_NoteChunks[1];
				}
			}
			
			if (null != comments && !comments.equalsIgnoreCase("null") && !comments.equals("")) {
				comments_NoteChunks = comments.split(":::");
				transient_Comments = comments_NoteChunks[0];
				if (comments_NoteChunks != null && comments_NoteChunks.length > 1) {
					transient_Comments_Date = comments_NoteChunks[1];
				}
			}

			transient_Date = eachFile_Info.getSubmission_Date().substring(0, 12);
			transient_Time = eachFile_Info.getSubmission_Date().substring(12,
					eachFile_Info.getSubmission_Date().length());

			eachFile_Info.setTransient_Note(transient_Note);
			eachFile_Info.setTransient_Comments(transient_Comments);
			eachFile_Info.setTransient_Comments_Date(transient_Comments_Date);
			eachFile_Info.setTransient_Attorney_Note_Date(transient_Attorney_Note_Date);
			eachFile_Info.setTransient_Date(transient_Date);
			eachFile_Info.setTransient_Time(transient_Time);

			newFile_InfoList.add(eachFile_Info);

			transient_Note = "";
			transient_Date = "";
		}

		return newFile_InfoList;
	}

	public List<Integer> getListOfAssociatedAgencyIdsByAgencyInfoId(Integer agencyInfoId, boolean isProtestLevel)
			throws Exception {
		Set<String> agencyInfoIds = new HashSet<String>();
		
		
		if (agencyInfoId != null){
			
			List<?> tier1AndTier2AgencyList;
			Agency_Info agencyInfoById = agency_Info_DAO.getAgency_Info(agencyInfoId);
			List<Agency_Info> listOfAgencyInfo;
			Tier_1_Agency tier1Agency;
			Tier_2_Agency tier2Agency;
			List<Tier_1_Agency> tier1AgencyInfoList;
			List<Tier_2_Agency> tier2AgencyInfoList;

			if (null != agencyInfoById && agencyInfoById.getTier().equals("2")) {

				tier1AndTier2AgencyList = agency_Info_DAO.getTier1AndTier2AgencyList(agencyInfoById.getAgency_Id());
				tier1AgencyInfoList = Util.getDesiredClassObjectList(tier1AndTier2AgencyList, Tier_1_Agency.class);
				tier1Agency = tier1AgencyInfoList.get(0);
				tier2AgencyInfoList = Util.getDesiredClassObjectList(tier1AndTier2AgencyList, Tier_2_Agency.class);
				tier2Agency = tier2AgencyInfoList.get(0);
				listOfAgencyInfo = agency_Info_DAO.getListOfAgencyInfoIdsByTier1AgencyId(tier2Agency.getTier_1_Agency_Id());
				
				if ("ALL".equalsIgnoreCase(tier1Agency.getTier2AgencyAccessFlag())
						|| "ALL".equalsIgnoreCase(agencyInfoById.getIs_Equals_To())) {

					for (Agency_Info eachAgencyInfo : listOfAgencyInfo) {
						agencyInfoIds.add(String.valueOf(eachAgencyInfo.getAgency_Info_Id()));
					}
				} 
				
				if (agencyInfoById.getIs_Equals_To() != null) {
					
					if (agencyInfoIds.size() > 0){
						agencyInfoIds = Stream.concat(agencyInfoIds.stream(),
															Arrays.asList(agencyInfoById.getIs_Equals_To().split(",")).stream())
																.distinct().collect(Collectors.toSet());
					}else{
						agencyInfoIds = new HashSet<String>(Arrays.asList(agencyInfoById.getIs_Equals_To().split(",")));	
					}
					

					if (isProtestLevel) {
						for (Agency_Info eachAgencyInfo : listOfAgencyInfo) {
							if ("ALL".equalsIgnoreCase(eachAgencyInfo.getIs_Equals_To())) {
								agencyInfoIds.add(String.valueOf(eachAgencyInfo.getAgency_Info_Id()));
							}

						}
					}

				}

			}else if(agencyInfoById.getIs_Equals_To() != null){
				agencyInfoIds = new HashSet<String>(Arrays.asList(agencyInfoById.getIs_Equals_To().split(",")));
			}

			agencyInfoIds.add(String.valueOf(agencyInfoId));

			removeAgencyIdList(agencyInfoIds, agencyInfoById.getIs_Not_Equals_To());
		}
		

		return agencyInfoIds.stream().map(Integer::parseInt).collect(Collectors.toList());

	}

	public void removeAgencyIdList(Set<String> agencyInfoIdsList, String removeIds) {

		if (removeIds != null) {
			Set<String> removeAgencyIdList = new HashSet<String>(Arrays.asList(removeIds.split(",")));
			agencyInfoIdsList.removeAll(removeAgencyIdList);
		}

	}

	private List<File_Info> getFileInfoListWithVisibility(List<File_Info> file_InfoList, Protest_Info protest_info,
			String user_Id) {
		List<File_Info> newFile_InfoList = new ArrayList<File_Info>();
		String is_Visible = "";

		listOfPartyMemberUserIds = null;

		for (File_Info file_Info : file_InfoList) {
			is_Visible = findVisibility(file_Info, protest_info, user_Id);
			file_Info.setIs_Visible(is_Visible);

			newFile_InfoList.add(file_Info);
		}

		return newFile_InfoList;
	}
	
	public String checkIfThisFileIsVisible (File_Info file_Info, Protest_Info protest_info, String user_Id){
		return findVisibility(file_Info, protest_info, user_Id);
	}

	private String findVisibility(File_Info file_Info, Protest_Info protest_info, String user_Id) {
		String isVisible = "Y";

		if (findIfAlwaysVisible(file_Info)) {
			isVisible = "Y";
		} else if (findIfUserIsFromAgencyOrGAO(protest_info.getRole())) {
			isVisible = "Y";
		} else if (user_Id.equalsIgnoreCase(file_Info.getSubmitter_User_Id())) {
			isVisible = "Y";
		} else if ("Y".equalsIgnoreCase(protest_info.getPo()) && null != protest_info.getPo()) {

			if ("Y".equalsIgnoreCase(protest_info.getIsUserAdmittedToPO())
					&& null != protest_info.getIsUserAdmittedToPO()) {
				isVisible = "Y";
			} else {
				isVisible = "Y";
			}
		} else if ("Y".equalsIgnoreCase(file_Info.getIs_Confidential()) && null != file_Info.getIs_Confidential()) {
			if (findIfUserBelongsToFilerParty(protest_info, user_Id, file_Info.getSubmitter_User_Id())) {
				isVisible = "Y";
			} else {
				isVisible = "Y";
			}
		}

		return isVisible;
	}

	/**
	 * 
	 * @param protest_info
	 * @param userIdOfViewer
	 * @param userIdOfFiler
	 * @return
	 */
	private boolean findIfUserBelongsToFilerParty(Protest_Info protest_info, String userIdOfViewer,
			String userIdOfFiler) {
		fillupListOfPartyMemberUserIdsIfRequired(protest_info, userIdOfViewer);

		/*
		 * return Util.checkIfElementIsAlreadyInList(userIdOfFiler,
		 * listOfPartyMemberUserIds);
		 */

		// Amer : I think we dont really need to pass userIdOfFiler the filer
		// instead we can pass userIdOfViewer.. this will resolve the issue This
		// is not happening, when primary representative is changed
		// I think when the document is marked as confidential what matters is
		// current user id is in listOfPartyMemberUserIds
		return Util.checkIfElementIsAlreadyInList(userIdOfFiler, listOfPartyMemberUserIds);
	}

	private void fillupListOfPartyMemberUserIdsIfRequired(Protest_Info protest_info, String user_id) {
		if (listOfPartyMemberUserIds != null)
			return;

		List<User_Protest_Role_Bridge> userProtestRoleBridgeList = user_Info_DAO
				.getUserProtestRoleBridgeListBasedOnVendorUserRole(user_id, protest_info);

		listOfPartyMemberUserIds = new ArrayList<String>();
		if (userProtestRoleBridgeList != null) {
			for (User_Protest_Role_Bridge eachUserProtestRoleBridge : userProtestRoleBridgeList) {
				listOfPartyMemberUserIds.add(eachUserProtestRoleBridge.getUser_Id());
			}
		}

	}

	private boolean findIfUserIsFromAgencyOrGAO(String role) {
		boolean isUserFromAgencyOrGAO = false;

		if ("AGENCY ATTORNEY".equalsIgnoreCase(role) || "AGENCY ADMIN".equalsIgnoreCase(role)
				|| "GAO ATTORNEY".equalsIgnoreCase(role) || "GAO SUPERVISOR".equalsIgnoreCase(role)
				|| "GAO ADMIN".equalsIgnoreCase(role)) {
			isUserFromAgencyOrGAO = true;
		}

		return isUserFromAgencyOrGAO;
	}

	public boolean findIfAlwaysVisible(File_Info file_Info) {
		return findAllTimeVisibilityByDocId(file_Info);

	}

	public boolean findIfAlwaysProtected(Integer docId) {

		Integer[] listOfDocIds = { 120, 110, 138, 129, 55, 84, 93, 102, 71, 156, 40, 168, 29, 18, 220, 219, 218, 217 };
		
	

		return ArrayUtils.contains(listOfDocIds, docId);

	}

	/**
	 * @Todo: It is expected that this service is used to remove cases which were mis-filings and not valid ongoing/closed cases.
	 * and since records in ProtestDmInfo is inserted when the case is completed and zip file is created..I dont think we will delete the case at that point.
	 * come back to this
	 * @param removeCaseDto
	 * @throws Exception
	 */
	public void removeCase(RemoveCaseDto removeCaseDto) throws Exception {

		Protest_Info protest_info = protest_Info_DAO.getProtestByA_no(removeCaseDto.getaNum());
		protest_info.setReasonForDeletion(removeCaseDto.getReasonForDeletion());
		
		protest_Info_DAO.updateProtest_Info(protest_info);//storing reason for deletion for audit logging purpose

		// remove user_protest_role_bridge
		protest_Info_DAO.removeAll_user_protest_role_bridge_recordsByA_no(removeCaseDto.getaNum());

		// remove gc events
		gc_Track_Service_DAO.deleteEventsByA_no(removeCaseDto.getaNum());

		// remove all protest_file_bridge records
		file_Info_DAO.remove_all_protest_file_bridge_records_forGivenA_no(removeCaseDto.getaNum());
		List<File_Info> list_of_file_info = file_Info_DAO.getListOf_file_info_basedOnA_no(removeCaseDto.getaNum());

		// remove all file_alert records
		if (list_of_file_info != null && list_of_file_info.size() > 0) {
			
			deleteFiles(list_of_file_info);
			
			if (list_of_file_info != null && list_of_file_info.size() > 0) {
				file_Info_DAO.remove_all_protest_file_bridge_records_forGivenListOf_file_Info(list_of_file_info);
			}

			// remove file_info
			file_Info_DAO.removeAll_file_info_records(removeCaseDto.getaNum());
		}

		// remove all invited user records by Anum
		removeInvitedUsers(removeCaseDto.getaNum());
		try {
		// remove protest_info
		protest_Info_DAO.delete(protest_info);
		}catch(Exception ex){
			ex.printStackTrace();
			System.out.println("not able to delete" + protest_info.getA_No());
		}
		

	}

	/**
	 * @param list_of_file_info
	 */
	private void deleteFiles(List<File_Info> list_of_file_info) {
		for (File_Info eachFileInfo : list_of_file_info) {
			
			if (!eachFileInfo.getFile_Path().equalsIgnoreCase("C:\\")) {
				SFTP.removeFile(new File(eachFileInfo.getFile_Path()));
			}

		}
	}

	public void removeInvitedUsers (String a_number) throws Exception{
		
		List<Invited_User> listOfInvitedUsers = user_Info_DAO.getListOfInvitedUsersByANum(a_number);
		
		if (null != listOfInvitedUsers && !listOfInvitedUsers.isEmpty()){
			for (Invited_User eachInvitedUsr :  listOfInvitedUsers){
				user_Info_DAO.delete(eachInvitedUsr);
			}
		}
	}
	public boolean findIfThisDocIdTriggerProtectiveOrder(Integer docId) {

		Integer[] listOfDocIds = { 115, 125, 134, 105, 103 };

		return ArrayUtils.contains(listOfDocIds, docId);

	}

	private boolean findAllTimeVisibilityByDocId(File_Info file_Info) {
		boolean isAllTimeVisbile = false;

		String docDescFiller = file_Info.getFiller();
		Integer[] listOfDocIds = { 2,3,4,5,12,17,19,20,21,22,27,28,30,31,32,33,38,39,
                41,42,43,44,46,53,54,56,57,58,59,67,70,82,83,91,92,100,101,103,104,
                105,106,107,108,111,112,114,115,116,117,118,121,122,124,125,126,127,
                130,131,133,134,135,136,139,140,142,143,144,145,147,154,155,157,158,159,
                160,161,162,111,130,121,139,46,56,147,172,235,236,237,238 };
		
		isAllTimeVisbile = ArrayUtils.contains(listOfDocIds, file_Info.getDoc_Type_Id());
		
		
		
		if (null != docDescFiller && "Appearance".equalsIgnoreCase(docDescFiller) && file_Info.getDoc_Type_Id() == 79) {
			isAllTimeVisbile = true;
		}
		

		return isAllTimeVisbile;
	}

	private List<File_Info> getFileInfoListWith_DocumentType_And_SubmitterRole_And_AlertStatus(
			List<File_Info> file_InfoList, String user_Id) throws Exception {
		List<File_Info> newFile_InfoList = new ArrayList<File_Info>();
		String docTypeName = "";
		String fileAlert = "N";
		String submitterRole = "";

		if (file_InfoList == null)
			return newFile_InfoList;

		Map<Integer, Doc_Info> docId_To_Doc_Info_Map = getDocId_To_Doc_Info_Map();

		for (File_Info eachFile_Info : file_InfoList) {
			docTypeName = docId_To_Doc_Info_Map.get((eachFile_Info.getDoc_Type_Id())).getDoc_Type_Desc();

			submitterRole = eachFile_Info.getSubmitter_Role();

			if (submitterRole == null || submitterRole.equals("")) {
				submitterRole = docId_To_Doc_Info_Map.get((eachFile_Info.getDoc_Type_Id())).getRole();
			}
			eachFile_Info.setDocTypeName(docTypeName);

			fileAlert = EPDS_FileUtils.getFileAlert(eachFile_Info, user_Id);

			eachFile_Info.setFileAlert(fileAlert);
			eachFile_Info.setSubmitter_Role(submitterRole);

			if (eachFile_Info.getPo() != null) {
				eachFile_Info.setPo(eachFile_Info.getPo().trim());
			}

			newFile_InfoList.add(eachFile_Info);
		}

		return newFile_InfoList;
	}

	public Map<Integer, Doc_Info> getDocId_To_Doc_Info_Map() throws Exception {
		Map<Integer, Doc_Info> docId_To_Doc_Info_Map = GlobalFields.docId_To_Doc_Info_Map;

		if (docId_To_Doc_Info_Map == null) {
			docId_To_Doc_Info_Map = file_Info_DAO.getDocId_To_DocType_Map();
			GlobalFields.docId_To_Doc_Info_Map = docId_To_Doc_Info_Map;
		}

		return docId_To_Doc_Info_Map;
	}

	public Protest_Info getProtestInfo(String a_No, String user_Role) throws Exception {
		Protest_Info protest_Info = protest_Info_DAO.get_protest_info_by_a_no(a_No, user_Role);

		String agency_Name = agency_Info_DAO.getAgencyName(protest_Info.getAgency_Info_Id());
		protest_Info.setAgency_Name(agency_Name);

		return protest_Info;
	}

	public String getAgencyName(int agency_Info_Id) throws Exception {
		return agency_Info_DAO.getAgencyName(agency_Info_Id);
	}

	public List<File_Info> getFileInfoByProtestId(String a_No) throws Exception {
		return file_Info_DAO.getFileEntitybyProtestId(a_No);
	}

	public List<String> getIntervenorCompanyNameList(String a_No) {
		return user_Info_DAO.getIntervenorCompanyNameList(a_No);
	}

	public void changeProtestAttribute(String a_No, String newValue, String typeOfChange) throws Exception {
		Protest_Info protest_Info = protest_Info_DAO.getProtestByA_no(a_No);

		if (typeOfChange.equalsIgnoreCase("b Number")) {
			protest_Info.setB_No(newValue);
		} else if (typeOfChange.equalsIgnoreCase("case Status")) {
			protest_Info.setCase_Status(newValue);
		} else {
			protest_Info.setCase_Type(newValue);
		}

		protest_Info_DAO.updateProtest_Info(protest_Info);
	}

	public List<Protest_Info> getSearchResultsBasedOnAdvancedSearch(AdvanceSearchDTO advancedSearchDTO)
			throws Exception {

		List<Protest_Info> searchResultList = new ArrayList<Protest_Info>();
		List<Integer> listOfAgencyInfoIds = new ArrayList<Integer>();
		
		if (advancedSearchDTO.getPartyInfo() != null && !advancedSearchDTO.getPartyInfo().equalsIgnoreCase("")){
			List<User_Info> listOfUserInfo = user_Info_DAO.getListOfUserInfoByPartyInformation(advancedSearchDTO.getPartyInfo());
			
			 if(null != listOfUserInfo && listOfUserInfo.size() > 0){
				 String result = listOfUserInfo.stream().map(User_Info::getUser_Id)
	                     .collect(Collectors.joining(","));
				 advancedSearchDTO.setPartyUserIds(result + (advancedSearchDTO.getAttorneyId() != null ? "," + advancedSearchDTO.getAttorneyId() : ""));
				 advancedSearchDTO.setAttorneyId("");//adding this so it passes all the attorneyId != null conditions in the advanceSearcQuery
			 }else if (advancedSearchDTO.getAttorneyId() != null){
				 advancedSearchDTO.setPartyUserIds(advancedSearchDTO.getAttorneyId());
			 }
		}else if (advancedSearchDTO.getAttorneyId() != null){
			 advancedSearchDTO.setPartyUserIds(advancedSearchDTO.getAttorneyId());
		 }
		

		// retrive list of agency Info id's
		// later on when we do multiple agency select we need to changes advance
		// search DTO from string to string []
		retrieveListOfAgencyInfoIds(advancedSearchDTO, listOfAgencyInfoIds);


		searchResultList = protest_Info_DAO.getProtestInfoBasedForAdvanceSearch(advancedSearchDTO);

		if (searchResultList != null) {

			// this is for UI display only
			if (advancedSearchDTO.getCase_Status() != null
					&& "120DAYS".equalsIgnoreCase(advancedSearchDTO.getCase_Status())) {

				changeCaseStatusToPublicDecisionPlus60DaysOrReadyToComplete(searchResultList,
						"PUBLIC DECISION +120 DAYS");
			} else if (advancedSearchDTO.getCase_Status() != null
					&& "RTC".equalsIgnoreCase(advancedSearchDTO.getCase_Status())) {

				changeCaseStatusToPublicDecisionPlus60DaysOrReadyToComplete(searchResultList, "READY TO COMPLETE");

			}

			if (advancedSearchDTO.getStartSubmission_Date() != null || advancedSearchDTO.getEndSubmission_Date() != null
					|| advancedSearchDTO.getStartDue_Date() != null || advancedSearchDTO.getEndDue_Date() != null) {

				List<Protest_Info> filteredProtestInfoListByDates = new ArrayList<Protest_Info>();
				getProtestInfoListFilteredByDates(filteredProtestInfoListByDates, advancedSearchDTO, searchResultList);

				return protestInfoService.addAlltheAssociatedConsolidatedProtests(filteredProtestInfoListByDates);
			}

		}

		return protestInfoService.addAlltheAssociatedConsolidatedProtests(searchResultList);
	}

	/**
	 * @param searchResultList
	 * @param caseStatus
	 */
	private void changeCaseStatusToPublicDecisionPlus60DaysOrReadyToComplete(List<Protest_Info> searchResultList,
			String caseStatus) {
		for (Protest_Info eachProtestInfo : searchResultList) {
			eachProtestInfo.setCase_Status(caseStatus);
		}
	}

	/**
	 * @param advancedSearchDTO
	 * @param listOfAgencyInfoIds
	 * @throws Exception
	 * @throws NumberFormatException
	 */
	private void retrieveListOfAgencyInfoIds(AdvanceSearchDTO advancedSearchDTO, List<Integer> listOfAgencyInfoIds)
			throws Exception, NumberFormatException {
		if (null != advancedSearchDTO.getTier1AgencyId()
				&& !"0".equalsIgnoreCase(advancedSearchDTO.getTier1AgencyId())) {

			if("0".equalsIgnoreCase(advancedSearchDTO.getTier2AgencyId())){
			
				List<Tier_2_Agency> tier2List = protestInfoService
						.getAgencyTier2(Integer.valueOf(advancedSearchDTO.getTier1AgencyId()));
				
				if (null != tier2List && tier2List.size() > 0) {

					for (Tier_2_Agency tier_2_Agency : tier2List) {
						
						listOfAgencyInfoIds.add(protest_Info_DAO.getAgency_Info_Id(advancedSearchDTO.getTier1AgencyId(),
								Integer.toString(tier_2_Agency.getAgency_Id())));
					}

				} 
			}else {

				listOfAgencyInfoIds.add(protest_Info_DAO.getAgency_Info_Id(advancedSearchDTO.getTier1AgencyId(),
						advancedSearchDTO.getTier2AgencyId()));

			}
			
			
		}
		
		advancedSearchDTO.setListOfAgencyInfoIds(listOfAgencyInfoIds);
	}

	private void getProtestInfoListFilteredByDates(List<Protest_Info> filteredProtestInfoListByDates,
			AdvanceSearchDTO advancedSearchDTO, List<Protest_Info> searchResultList) {

		String submissionDatePattern = "MMM dd yyyy HH:mm:ss z";
		String dueDatePattern = "MM/dd/yyyy";

		for (Protest_Info eachProtestInfo : searchResultList) {

			DateTime dueDate_dateTime = DateTime.parse(eachProtestInfo.getDue_Date(),
					DateTimeFormat.forPattern(dueDatePattern));
			DateTime submissionDate_dateTime = DateTime.parse(eachProtestInfo.getSubmission_Date(),
					DateTimeFormat.forPattern(submissionDatePattern));
			boolean isAfterStartSubmissionDate = true;
			boolean isBeforeEndSubmissionDate = true;
			boolean isAfterStartDueDate = true;
			boolean isBeforeEndDueDate = true;

			if (advancedSearchDTO.getStartSubmission_Date() != null) {
				isAfterStartSubmissionDate = submissionDate_dateTime.isAfter(DateTime
						.parse(advancedSearchDTO.getStartSubmission_Date(), DateTimeFormat.forPattern("MM/dd/yyyy")));
			}
			if (advancedSearchDTO.getEndSubmission_Date() != null) {
				isBeforeEndSubmissionDate = submissionDate_dateTime.isBefore(DateTime
						.parse(advancedSearchDTO.getEndSubmission_Date(), DateTimeFormat.forPattern("MM/dd/yyyy")));
			}
			if (advancedSearchDTO.getStartDue_Date() != null) {
				isAfterStartDueDate = dueDate_dateTime.isAfter(
						DateTime.parse(advancedSearchDTO.getStartDue_Date(), DateTimeFormat.forPattern("MM/dd/yyyy")));
			}
			if (advancedSearchDTO.getEndDue_Date() != null) {
				isBeforeEndDueDate = dueDate_dateTime.isBefore(
						DateTime.parse(advancedSearchDTO.getEndDue_Date(), DateTimeFormat.forPattern("MM/dd/yyyy")));
			}

			if (isAfterStartDueDate && isBeforeEndDueDate && isAfterStartSubmissionDate && isBeforeEndSubmissionDate) {
				filteredProtestInfoListByDates.add(eachProtestInfo);
			}

		}

	}

	public Protest_Info getProtestInfoBasedOnUserIdAndANo(User_Info userInfo, String aNo)
			throws Exception {

		List<Protest_Info> listOfConsolidatedCases = protest_Info_DAO
				.getConsolidatedProtestInfoListWithParentAndChildSupplementalProtests(aNo);

		Protest_Info protestInfo = getProtestInfoWithAllTransientFieldsPopulated(userInfo, aNo,
				listOfConsolidatedCases);

		
		if (protestInfo.getA_No() == null){
			protestInfo.setViewOnly(true);
		}

		return protestInfo;
	}

	private Protest_Info getProtestInfoWithAllTransientFieldsPopulated(User_Info userInfo, String aNo,
			List<Protest_Info> listOfConsolidatedCases) throws Exception {

		Protest_Info mainProtestInfo = new Protest_Info();
		User_Protest_Role_Bridge userProtestRoleBridge = new User_Protest_Role_Bridge();

		if (listOfConsolidatedCases != null && listOfConsolidatedCases.size() > 0) {

			mainProtestInfo = listOfConsolidatedCases.get(0);
			listOfConsolidatedCases.remove(0);
			
			userProtestRoleBridge = user_Info_DAO.getUserProtestRoleBridgeWithRoleDesc(aNo, userInfo.getUser_Id());

			
			if (userInfo.getRole_id().equals(AGENCY_ADMIN.getCode()) ) {
				
				List<Integer> agencyInfoIds = getListOfAssociatedAgencyIdsByAgencyInfoId(mainProtestInfo.getAgency_Info_Id(),true);
				
				if (!agencyInfoIds.contains(userInfo.getFirm_id()) && (checkIfUPRBExists(mainProtestInfo, userInfo) == null)){
					
					return  new Protest_Info(); //blank protest Info
				}
				
			}
			Protest_Info eachProtestInfo;

			for (int i = 0; i < listOfConsolidatedCases.size(); i++) {
				eachProtestInfo = listOfConsolidatedCases.get(i);

				/*
				 * eachProtestInfo.getCase_Type().equalsIgnoreCase(
				 * "SUPPLEMENTAL") ||
				 */
				if ((eachProtestInfo.getCase_Type().equalsIgnoreCase("SUPPLEMENTAL")
						/*||
						protestInfoService.checkIfThisProtestIsSupplemental(mainProtestInfo, eachProtestInfo)*/) 
						&& eachProtestInfo.getParent_A_No().equalsIgnoreCase(mainProtestInfo.getA_No())) {

					Protest_info_util.populateSupplementalNumbersForEachCaseInDashboard(eachProtestInfo, mainProtestInfo);
					listOfConsolidatedCases.remove(i--);
				} else if (eachProtestInfo.getCase_Type().equalsIgnoreCase("SUPPLEMENTAL")) {
					listOfConsolidatedCases.remove(i--);
				}
			}
		}

		mainProtestInfo.setListOf_ConsolidatedProtest_Info(listOfConsolidatedCases);
		Util.setOtherTransientAttributestBasedOnUserProtestRoleBridge(mainProtestInfo, userProtestRoleBridge, userInfo);
		mainProtestInfo.setViewOnly(findIfUserCanViewOnly(mainProtestInfo, userInfo));
		mainProtestInfo.setAgency_Name(agency_Info_DAO.getAgencyName(mainProtestInfo.getAgency_Info_Id()));

		setTransientAttrBasedOnDMInfo(mainProtestInfo, userInfo);

		return mainProtestInfo;
	}

	private void setTransientAttrBasedOnDMInfo(Protest_Info mainProtestInfo, User_Info userInfo) {
		try {
			Protest_Dm_Info dmInfo = protest_Info_DAO.getDmInfo(mainProtestInfo.getA_No());
			CaseCompletionStatus ccs = new CaseCompletionStatus();
			if (dmInfo != null) {
				ccs.setIsZipCreated(true);

				if (dmInfo.getGc_Track_Dm_No() == null) {
					ccs.setIsDmEntered(false);
				}

				if (dmInfo.getGc_Track_Dm_No() != null) {
					ccs.setIsDmEntered(true);
					ccs.setDmNumber(dmInfo.getGc_Track_Dm_No());
				}

				if (dmInfo.getVerified_By() == null && dmInfo.getGc_Track_Dm_No() != null
						&& !dmInfo.getDm_no_entered_By().equalsIgnoreCase(userInfo.getUser_Id())) {
					ccs.setIsDmVerfied(false);
				}
				if (dmInfo.getVerified_By() != null) {
					ccs.setIsDmVerfied(true);
					ccs.setCaseCompleted(dmInfo.getDate_verified());
				}
			} else {
				ccs.setIsZipCreated(false);
			}
			mainProtestInfo.setCaseCompletionStatus(ccs);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception occured when trying to retrieve DM INFO ", e);
		}
	}

	private boolean findIfUserCanViewOnly(Protest_Info mainProtestInfo, User_Info userInfo) {
		
		boolean isViewOnly = true;
        UserRoles role = UserRoles.getByCode(userInfo.getRole_id());

		if (role == GAO_ADMIN || role == AGENCY_ADMIN){
			isViewOnly = false;
		} else if (role == GAO_SUPERVISOR) {
			GAO_User gaoUser = user_Info_DAO.getGAO_User_BasedOnEPDSUserId(userInfo.getUser_Id());
			if (gaoUser.getGroup_No().equals(mainProtestInfo.getAttorney_Group_Id())) {
				isViewOnly = false;
			}
		} else{
			
			User_Protest_Role_Bridge uprb = user_Info_DAO.getUser_Protest_Role_Bridge(mainProtestInfo.getA_No(),userInfo.getUser_Id());
			
			if (uprb != null) {
				
				isViewOnly = false;
				
				if (role != GAO_ATTORNEY && !mainProtestInfo.getIsUserConsolidated().equalsIgnoreCase("N")){
					isViewOnly = true;
				}
			}
		}

		return isViewOnly;
	}
	
	
	public User_Protest_Role_Bridge checkIfUPRBExists(Protest_Info protest_Info, User_Info userInfo){
		
		User_Protest_Role_Bridge uprb = user_Info_DAO.getUser_Protest_Role_Bridge(protest_Info.getA_No(),userInfo.getUser_Id());
		
		
		return uprb;
	}

    public void validateAccess(User_Info userInfo, Protest_Info protestInfo) throws IllegalAccessError, Exception {
	    validateAccess(userInfo, protestInfo, null);
    }

	public void validateAccess(User_Info userInfo, Protest_Info protestInfo, File_Info file_Info) throws IllegalAccessError, Exception {
		UserRoles role = UserRoles.getByCode(userInfo.getRole_id());

		switch (role) {
			case PROTESTER:
			case INTERVENOR:
			case SECONDARY_PROTESTER:
			case SECONDARY_INTERVENOR:
			case AGENCY_ATTORNEY:
				if (checkIfUPRBExists(protestInfo, userInfo) == null) {
					if (file_Info != null && (((role == INTERVENOR || role == PROTESTER) && file_Info.getDoc_Type_Id() == 160) || (role == AGENCY_ATTORNEY && file_Info.getDoc_Type_Id() == 161)) ) {
						// allow INTERVENOR/PROTESTER and AGENCY_ATTORNEY to access their rejection notice
						break;
					} else {
						throw new IllegalAccessError("Unauthorized!!");
					}
				}
				break;
			case AGENCY_ADMIN:
				List<Integer> agencyInfoIds = getListOfAssociatedAgencyIdsByAgencyInfoId(protestInfo.getAgency_Info_Id(),
						true);

				if (!agencyInfoIds.contains(userInfo.getFirm_id()) && (checkIfUPRBExists(protestInfo, userInfo) == null)) {

					throw new IllegalAccessError("Unauthorized!!");
				}
				break;
			case GAO_ADMIN:
			case GAO_SUPERVISOR:
			case GAO_ATTORNEY:
				break;
			default:
				throw new IllegalAccessError("Unauthorized!!");
		}
	}
}
