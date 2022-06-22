package gov.gao.epds.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.gao.epds.dao.Agency_Info_DAO;
import gov.gao.epds.dao.File_Info_DAO;
import gov.gao.epds.dao.Protest_Info_DAO;
import gov.gao.epds.dao.User_Info_DAO;
import gov.gao.epds.dto.ProtestInfoFormDTO;
import gov.gao.epds.dto.SubmitNewDocDTO;
import gov.gao.epds.dto.UploadedFileIdentifier;
import gov.gao.epds.enums.UserRoles;
import static gov.gao.epds.enums.UserRoles.*;
import gov.gao.epds.gctrack.GC_Track_Service_Call_Response;
import gov.gao.epds.persistence.entity.File_Info;
import gov.gao.epds.persistence.entity.Protest_Dm_Info;
import gov.gao.epds.persistence.entity.Protest_Info;
import gov.gao.epds.persistence.entity.Tier_2_Agency;
import gov.gao.epds.persistence.entity.User_Info;
import gov.gao.epds.persistence.entity.User_Protest_Role_Bridge;
import gov.gao.epds.session.EpdsSession;
import gov.gao.epds.utils.Date_Util;
import gov.gao.epds.utils.EPDS_FileUtils;
import gov.gao.epds.utils.GlobalParams;
import gov.gao.epds.utils.Protest_info_util;
import gov.gao.epds.utils.ZipFile_Util;

@Service
public class ProtestInfoService {

	@Autowired
	private Protest_Info_DAO protest_Info_DAO;
	@Autowired
	private File_Info_DAO file_Info_DAO;
	@Autowired
	private Agency_Info_DAO agency_Info_DAO;
	@Autowired
	private User_Info_DAO user_Info_DAO;
	@Autowired
	private EmailService emailService;
	@Autowired
	private GC_Service gc_service;
	@Autowired
	private CaseDocketSheetService caseDocketService;

	@Autowired
	private UserInfoService userInfoService;

	@Autowired
	private DashboardService dashboardService;

	/**
	 * @param protestInfoFormDTO
	 * @param loggedInUserInfo
	 * @param user_Role
	 * @param agency_tracking_id
	 * @param payDotGovTrackingId
	 * @return
	 * @throws Exception
	 */
	public Protest_Info registerProtestInfo(
			ProtestInfoFormDTO protestInfoFormDTO, User_Info loggedInUserInfo,
			String user_Role, String agency_tracking_id,String payDotGovTrackingId,User_Info protesterRepUserInfo) throws Exception {
		Protest_Info protest_Info = new Protest_Info();

		protest_Info.setSubmission_Date(Date_Util.getCurrentDate());
		protest_Info.setSubmissionDateTime(Date_Util.getESTTimeStampInLong(protest_Info.getSubmission_Date()));
		protest_Info.setDue_Date(Date_Util.getDueDate(protest_Info.getSubmission_Date(),100));

		protest_Info.setSolicitation_No(protestInfoFormDTO
				.getSolicitationNumber());
		protest_Info.setA_No(protestInfoFormDTO.getA_No());

		int agency_Info_Id = protest_Info_DAO.getAgency_Info_Id(
				protestInfoFormDTO.getAgency_tier_1(),
				protestInfoFormDTO.getAgency_tier_2());

		protest_Info.setCompany_City(protestInfoFormDTO.getCompany_city());
		protest_Info
				.setCompany_Country(protestInfoFormDTO.getCompany_country());
		protest_Info.setCompany_Name(protestInfoFormDTO.getCompany_name());
		protest_Info.setCompany_State(protestInfoFormDTO.getCompany_state());
		protest_Info.setCompany_Status(protestInfoFormDTO.getCompany_status());
		protest_Info.setCompany_Street(protestInfoFormDTO.getCompany_street());
		protest_Info.setCompany_address1(protestInfoFormDTO.getCompany_address1());
		protest_Info.setCompany_address2(protestInfoFormDTO.getCompany_address2());
		protest_Info
				.setCompany_Zipcode(protestInfoFormDTO.getCompany_zipcode());
		protest_Info.setPo("N");
		protest_Info.setCase_Status("OPEN");
		protest_Info.setAgency_Info_Id(agency_Info_Id);

		protest_Info.setRepresentative_City(protestInfoFormDTO.getCity());
		protest_Info.setRepresentative_Country(protestInfoFormDTO.getCountry());
		protest_Info.setRepresentative_Email(protestInfoFormDTO.getEmail());
		protest_Info
				.setRepresentative_Fax_No(protestInfoFormDTO.getFaxnumber());
		protest_Info.setRepresentative_First_Name(protestInfoFormDTO
				.getFirstname());
		protest_Info.setRepresentative_Last_Name(protestInfoFormDTO
				.getLastname());
		protest_Info.setRepresentative_Phone_No(protestInfoFormDTO
				.getPhonenumber());
		protest_Info.setRepresentative_State(protestInfoFormDTO.getState());
		protest_Info.setRepresentative_Street(protestInfoFormDTO.getStreet());

		protest_Info.setRepresentative_address1(protestInfoFormDTO
				.getAddress1());
		protest_Info.setRepresentative_address2(protestInfoFormDTO
				.getAddress2());
		protest_Info.setRepresentative_Zipcode(protestInfoFormDTO.getZipcode());
		protest_Info.setComments(protestInfoFormDTO.getComments());
		protest_Info.setB_No(protestInfoFormDTO.getB_no());
		protest_Info.setCase_Type("PROTEST");
		protest_Info.setAgency_tracking_id(agency_tracking_id);
		protest_Info.setPay_dot_gov_id(payDotGovTrackingId);

//		if (user_Role.equals("GAO ADMIN")) {
		    protest_Info.setTransaction_Status("PAID");
//		} else {
//		    protest_Info.setTransaction_Status("UNPAID");
//		}

		protest_Info_DAO.save_Entity(protest_Info);

		GlobalParams.aNumbersSet.remove(protest_Info.getA_No());

		if (user_Role.equals("GAO ADMIN")
			&& protestInfoFormDTO
			.getEmail().toLowerCase(Locale.ENGLISH)
			.equalsIgnoreCase(loggedInUserInfo.getEmail().toLowerCase(Locale.ENGLISH))) {

			user_Info_DAO.add_User_Protest_Role_Bridge_Entity(protest_Info, loggedInUserInfo.getUser_Id(),7);

			//PLCG is filing the protest on behalf of the protester.
		}else if (user_Role.equals("GAO ADMIN")
				&& !protestInfoFormDTO.getEmail().toLowerCase(Locale.ENGLISH)
				.equalsIgnoreCase(loggedInUserInfo.getEmail().toLowerCase(Locale.ENGLISH))){

			user_Info_DAO.add_User_Protest_Role_Bridge_Entity(protest_Info, protesterRepUserInfo.getUser_Id(),1);

		}else if (!user_Role.equals("GAO ADMIN")){//protester filing the protest
			user_Info_DAO.add_User_Protest_Role_Bridge_Entity(protest_Info, loggedInUserInfo.getUser_Id(),1);
		}


		return protest_Info;
	}

	public List<Tier_2_Agency> getAgencyTier2(int tier1) throws Exception {
		return agency_Info_DAO.getAgencyTier2(tier1);
	}

	public Protest_Info getProtestInfoByBNum(String bnumber) throws Exception {
		return protest_Info_DAO.getProtestInfoByBNum(bnumber);
	}

	public String getAgencyName(int agency_Info_Id) throws Exception {
		return agency_Info_DAO.getAgencyName(agency_Info_Id);
	}

	public String getResponseForRequestToIntervene(String userId, String a_No)
			throws Exception {
		return protest_Info_DAO.getResponseForRequestToIntervene(userId, a_No);
	}

	
	public Protest_Info getPendingOrDeniedRequestToInterveneProtestInfo(String userId, String userRole, Protest_Info protest_Info)
			throws Exception {
		
		if (userRole.equalsIgnoreCase("PROTESTER") || userRole.equalsIgnoreCase("AGENCY ATTORNEY")) {
			return protest_Info_DAO.getPendingOrDeniedRequestToInterveneProtestInfo(userId,userRole, protest_Info);
		}
		
		return protest_Info;
		
	}




	@SuppressWarnings("unchecked")
	public void saveFilesToDB(SubmitNewDocDTO submitNewDocDTO,
			HttpServletRequest request) throws Exception {

		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request, "user_Info");

		List<UploadedFileIdentifier> filePathList = (List<UploadedFileIdentifier>) EpdsSession.getAttribute(request, "filePathList");
		EpdsSession.removeAttribute(request, "filePathList");

		String typeOfDocment = submitNewDocDTO.getTypeofdocument();

		if (filePathList == null) {
			filePathList = new ArrayList<UploadedFileIdentifier>();
		}
		UploadedFileIdentifier upfi = null;
		switch (typeOfDocment.toLowerCase(Locale.ENGLISH)) {
		case "minute entry":
		case "request to intervene approved":
		case "notice of appearance acknowledged":
			upfi = new UploadedFileIdentifier();
			upfi.setFileIdentifierCode("P");
			upfi.setFilePath("C:\\");
			filePathList.add(upfi);
			break;
		case "zip":
			upfi = new UploadedFileIdentifier();
			upfi.setFileIdentifierCode("P");
			upfi.setFilePath(submitNewDocDTO.getFilepath());
			filePathList.add(upfi);

			break;
		}


		populateFilePathListForNonFileUploadDocIds(filePathList,submitNewDocDTO);

		if (dashboardService.findIfAlwaysProtected(submitNewDocDTO.getDocId())){
			submitNewDocDTO.setIsDocConfidential("Y");
		}
		
		File_Info fileInfoToCheckIfAlwaysVisible = new File_Info();
		
		fileInfoToCheckIfAlwaysVisible.setDoc_Type_Id(submitNewDocDTO.getDocId());
		fileInfoToCheckIfAlwaysVisible.setFiller(submitNewDocDTO.getDocDescFiller());
		if (dashboardService.findIfAlwaysVisible(fileInfoToCheckIfAlwaysVisible)){
			submitNewDocDTO.setIsDocConfidential("N");
		}

		Protest_Info protestInfo = protest_Info_DAO.getProtestByA_no(submitNewDocDTO.getProtestId());
		submitNewDocDTO.setPo(protestInfo.getPo());
		
		List<Integer> file_Info_IdList = file_Info_DAO.saveFilePathToDB(
				filePathList, submitNewDocDTO, null);

		if (file_Info_IdList != null && file_Info_IdList.size() > 0){
			makeChangesForExceptionalDocumentTypes(submitNewDocDTO.getTypeofdocument(),
					request, submitNewDocDTO.getUser_Id(),submitNewDocDTO.getDocId(),submitNewDocDTO.getProtestId());

			// If a new supplemental case is created in makeChangesForExceptionalDocumentTypes, needed to be added to
			// the consolidated_A_No_List. Wasn't being used above so moved down to here.
			List<String> consolidated_A_No_List = getConsolidated_A_No_List(request,submitNewDocDTO.getProtestId());

			Integer [] withdrawalConfirmationDocIds = {122,131,140,112};
			if (submitNewDocDTO.getTypeofdocument().toUpperCase(Locale.ENGLISH).contains("PUBLIC DECISION")
					|| submitNewDocDTO.getTypeofdocument().toUpperCase(Locale.ENGLISH).contains("PROTECTED DECISION")  || ArrayUtils.contains(withdrawalConfirmationDocIds, submitNewDocDTO.getDocId() )){
				changeCaseStatusToClosed(consolidated_A_No_List,submitNewDocDTO.getTypeofdocument());
			}
			consolidated_A_No_List.remove(submitNewDocDTO.getProtestId());

			if (!submitNewDocDTO.getTypeofdocument().toUpperCase(Locale.ENGLISH).contains("ZIP")
					&& !submitNewDocDTO.getTypeofdocument().equalsIgnoreCase(
							"protest")) {
				file_Info_DAO.associateFilesWithOtherOtherCases(file_Info_IdList,
						consolidated_A_No_List);
			}

			setTypeOfDocumentIfDescriptionFillerNotNull(submitNewDocDTO);

			gc_service.set_Event_For_Special_Doc_Types(
					user_Info, submitNewDocDTO.getDocId(),submitNewDocDTO.getProtestId());

			if (!submitNewDocDTO.getTypeofdocument().equalsIgnoreCase("Protest")
					&& !submitNewDocDTO.getTypeofdocument().toUpperCase(Locale.ENGLISH).contains("ZIP")){
				sendEmailNotificationsToAllPartiesAssociatedWithThisCase(submitNewDocDTO,request);
			}
		}


	}
	
	public void updateDmInfo (Protest_Dm_Info dmInfo) throws Exception{
		protest_Info_DAO.updateDmInfo(dmInfo);
	}
	

	private void populateFilePathListForNonFileUploadDocIds(List<UploadedFileIdentifier> filePathList, SubmitNewDocDTO submitNewDocDTO) {

	Boolean isUploadNotRequired  = EPDS_FileUtils.checkIfThisDocDoesNotRequireFileUpload(submitNewDocDTO.getDocId());
	UploadedFileIdentifier upfi = null;
		if (isUploadNotRequired){

			upfi = new UploadedFileIdentifier();
			upfi.setFileIdentifierCode("P");
			upfi.setFilePath("C:\\");
			filePathList.add(upfi);
		}

	}


	private void changeCaseStatusToClosed(List<String> consolidated_A_No_List, String typeOfDoc) throws Exception{

		for (String aNo : consolidated_A_No_List) {
			Protest_Info protest_Info = protest_Info_DAO.getProtestByA_no(aNo);
			protest_Info.setCase_Status("CLOSED");

			if (typeOfDoc.toUpperCase(Locale.ENGLISH).contains("PUBLIC DECISION")){
				 protest_Info.setPublic_decision_date(new Date());
			}
			protest_Info_DAO.updateProtest_Info(protest_Info);
		}
	}
	private void setTypeOfDocumentIfDescriptionFillerNotNull(
			SubmitNewDocDTO submitNewDocDTO) {
		if (submitNewDocDTO.getDocDescFiller() != null
				&& !submitNewDocDTO.getDocDescFiller().equalsIgnoreCase("")) {
			submitNewDocDTO.setTypeofdocument(submitNewDocDTO
					.getTypeofdocument().replace("____", "").trim()
					+ " " + submitNewDocDTO.getDocDescFiller());
		}

	}

	public List<Integer> saveFileToDB_for_gc_test(String typeOfFiling,
			String a_no, String user_id, String company_name,
			String company_address) throws Exception {
		List<UploadedFileIdentifier> filePathList = new ArrayList<UploadedFileIdentifier>();

		UploadedFileIdentifier upfi = new UploadedFileIdentifier();
		upfi.setFileIdentifierCode("P");
		upfi.setFilePath("gc_testPath");
		filePathList.add(upfi);

		SubmitNewDocDTO submitNewDocDTO = new SubmitNewDocDTO();
		if (typeOfFiling.equalsIgnoreCase("protest")) {
			submitNewDocDTO.setDocId(1);
			submitNewDocDTO.setTypeofdocument("Protest");
			submitNewDocDTO.setUser_Role("PROTESTER");
		} else if (typeOfFiling.equalsIgnoreCase("intervene")) {
			submitNewDocDTO.setDocId(56);
			submitNewDocDTO.setTypeofdocument("Request to Intervene");
			submitNewDocDTO.setUser_Role("INTERVENOR");
		} else if (typeOfFiling.equalsIgnoreCase("agency report")) {
			submitNewDocDTO.setDocId(74);
			submitNewDocDTO
					.setTypeofdocument("Agency Report - Legal Memorandum & Contracting Officer's Statement");
			submitNewDocDTO.setUser_Role("AGENCY");
		} else if (typeOfFiling.equalsIgnoreCase("agency report comment")) {
			submitNewDocDTO.setDocId(14);
			submitNewDocDTO.setTypeofdocument("Comments");
			submitNewDocDTO.setUser_Role("PROTESTER");
		} else if (typeOfFiling.equalsIgnoreCase("notice of appearance")) {
			submitNewDocDTO.setDocId(109);
			submitNewDocDTO.setTypeofdocument("Notice of Appearance");
			submitNewDocDTO.setDocDescFiller("Appearance");
			submitNewDocDTO.setUser_Role("GAO");
		}

		submitNewDocDTO.setIsDocConfidential("N");
		submitNewDocDTO.setProtestId(a_no);
		submitNewDocDTO.setSubmissionDate(Date_Util.getCurrentDate());
		submitNewDocDTO.setUser_Id(user_id);
		submitNewDocDTO.setCompany_Name(company_name);
		submitNewDocDTO.setCompany_Address(company_address);

		return file_Info_DAO.saveFilePathToDB(filePathList, submitNewDocDTO,
				null);
	}


	private List<String> getConsolidated_A_No_List(HttpServletRequest request,String a_No) {

		List<Protest_Info> consolidatedProtestInfoList = new ArrayList<Protest_Info>();
		List<String> consolidated_A_No_List = new ArrayList<String>();

		for (Protest_Info eachChildProtestInfo : protest_Info_DAO.getConsolidatedProtestInfoListWithParentAndChildSupplementalProtests(a_No)) {
			consolidated_A_No_List.add(eachChildProtestInfo.getA_No());
			consolidatedProtestInfoList.add(eachChildProtestInfo);
		}


		return consolidated_A_No_List;
	}


	private List<String> getAssociated_UserId_List(String userId,
			List<String> consolidated_A_No_List) throws Exception {
		List<String> user_IdList = new ArrayList<String>();

		for (String eachA_No : consolidated_A_No_List) {
			List<User_Protest_Role_Bridge> user_Protest_Role_BridegeList = user_Info_DAO
					.getUser_Protest_Role_Bridge_List_BasedOnProtestId(eachA_No);

			for (User_Protest_Role_Bridge each_User_Protest_Role_Bridge : user_Protest_Role_BridegeList) {
				user_IdList.add(each_User_Protest_Role_Bridge.getUser_Id());
			}

			user_IdList.remove(userId);
		}

		return user_IdList;
	}


	public List<Protest_Info> getListOfAllCasesWithUnpaidTransactionStatus() throws Exception {
		return protest_Info_DAO.getListOfAllCasesWithUnpaidTransactionStatus();
	}

	public List<Protest_Dm_Info> getListOfAllVerifiedCasesOlderThan10Days() throws Exception {
		return protest_Info_DAO.getListOfAllVerifiedCasesOlderThan10Days();
	}

	public String getNewAnumForSecondaryProtest(String a_No) {
		int suffix = protest_Info_DAO.getSuffixForSecondaryProtestbyAnum(Protest_info_util.getNonDecimalPart(a_No));
		String[] parentANum = a_No.split("\\.");
		String aNum = parentANum[0] + "." + suffix;
		return aNum;
	}

	public Protest_Info registerOtherProtestInfo(String userId,
			Protest_Info parent_ProtestInfo, String typeOfProtest,
			String agency_tracking_id/*,Integer filerId, String user_Role*/) throws Exception {
		Protest_Info otherProtestInfo = new Protest_Info();

		int suffixForBNum = 0;

		String parentANum = getNewAnumForSecondaryProtest(parent_ProtestInfo.getA_No());

		String[] parentBNum = new String [2];
		if (parent_ProtestInfo.getB_No() != null){
			suffixForBNum = protest_Info_DAO.getSuffixForSecondaryProtestbyBNum(Protest_info_util
					.getNonDecimalPart(parent_ProtestInfo.getB_No()));
			parentBNum = parent_ProtestInfo.getB_No().split("\\.");
		}
		// is this else ever called?
		else {
//			suffixForBNum = suffix;
			parentBNum[0] = " ";
		}

		otherProtestInfo.setA_No(parentANum);

		if (!typeOfProtest.equalsIgnoreCase("RECONSIDERATION")){
            otherProtestInfo.setB_No(parentBNum[0] + "." + suffixForBNum);
			otherProtestInfo.setAttorney_Name(parent_ProtestInfo.getAttorney_Name());
			otherProtestInfo.setAttorney_Group_Id(parent_ProtestInfo.getAttorney_Group_Id());

		}
		otherProtestInfo.setSubmission_Date(Date_Util.getCurrentDate());
		otherProtestInfo.setSubmissionDateTime(Date_Util.getESTTimeStampInLong(otherProtestInfo.getSubmission_Date()));
		otherProtestInfo.setDue_Date(Date_Util.getDueDate(otherProtestInfo
				.getSubmission_Date(),100));
		otherProtestInfo.setSolicitation_No(parent_ProtestInfo
				.getSolicitation_No());
		otherProtestInfo.setCompany_City(parent_ProtestInfo.getCompany_City());
		otherProtestInfo.setCompany_Country(parent_ProtestInfo
				.getCompany_Country());

//		if (user_Role.toUpperCase(Locale.ENGLISH).contains("AGENCY")){
//			filerId = 3;
//		}

		/*if (typeOfProtest.toUpperCase(Locale.ENGLISH).contains("RECON")){
			otherProtestInfo.setCompany_Name(getCompanyCompanyName(filerId, parent_ProtestInfo) + " " + "-RECON");
		}else if (typeOfProtest.toUpperCase(Locale.ENGLISH).contains("ENT")){
			otherProtestInfo.setCompany_Name(getCompanyCompanyName(filerId, parent_ProtestInfo) + " " + "-ENT");
		}else if (typeOfProtest.toUpperCase(Locale.ENGLISH).contains("COST")){
			otherProtestInfo.setCompany_Name(getCompanyCompanyName(filerId, parent_ProtestInfo) + " " + "-COST");
		}else{
			otherProtestInfo.setCompany_Name(getCompanyCompanyName(filerId, parent_ProtestInfo));
		}
		*/

//		otherProtestInfo.setCompany_Name(getCompanyName(filerId, parent_ProtestInfo));
		otherProtestInfo.setCompany_Name(parent_ProtestInfo.getCompany_Name());

		otherProtestInfo
				.setCompany_State(parent_ProtestInfo.getCompany_State());
		otherProtestInfo.setCompany_Status(parent_ProtestInfo
				.getCompany_Status());
		otherProtestInfo.setCompany_Street(parent_ProtestInfo
				.getCompany_Street());

		otherProtestInfo.setCompany_address1(parent_ProtestInfo.getCompany_address1());
		otherProtestInfo.setCompany_address2(parent_ProtestInfo.getCompany_address2());

		otherProtestInfo.setCompany_Zipcode(parent_ProtestInfo
				.getCompany_Zipcode());
		otherProtestInfo.setPo(parent_ProtestInfo.getPo());
		otherProtestInfo.setCase_Status("OPEN");
		otherProtestInfo.setAgency_Info_Id(parent_ProtestInfo
				.getAgency_Info_Id());
		otherProtestInfo.setRepresentative_City(parent_ProtestInfo
				.getRepresentative_City());
		otherProtestInfo.setRepresentative_Country(parent_ProtestInfo
				.getRepresentative_Country());
		otherProtestInfo.setRepresentative_Email(parent_ProtestInfo
				.getRepresentative_Email());
		otherProtestInfo.setRepresentative_Fax_No(parent_ProtestInfo
				.getRepresentative_Fax_No());
		otherProtestInfo.setRepresentative_First_Name(parent_ProtestInfo
				.getRepresentative_First_Name());
		otherProtestInfo.setRepresentative_Last_Name(parent_ProtestInfo
				.getRepresentative_Last_Name());
		otherProtestInfo.setRepresentative_Phone_No(parent_ProtestInfo
				.getRepresentative_Phone_No());
		otherProtestInfo.setRepresentative_State(parent_ProtestInfo
				.getRepresentative_State());
		otherProtestInfo.setRepresentative_Street(parent_ProtestInfo
				.getRepresentative_Street());
		otherProtestInfo.setRepresentative_address1(parent_ProtestInfo
				.getRepresentative_address1());
		otherProtestInfo.setRepresentative_address2(parent_ProtestInfo
				.getRepresentative_address2());
		otherProtestInfo.setRepresentative_Zipcode(parent_ProtestInfo
				.getRepresentative_Zipcode());
		otherProtestInfo.setAgency_tracking_id(agency_tracking_id);
		otherProtestInfo.setCase_Type(typeOfProtest.toUpperCase(Locale.ENGLISH));

		if (typeOfProtest.toUpperCase(Locale.ENGLISH).contains("SUPPLEMENTAL")) {

			otherProtestInfo.setParent_A_No(parent_ProtestInfo.getA_No());
		}

		user_Info_DAO.allowAllUsersOfCaseATheAccessToCaseB(parent_ProtestInfo.getA_No(), otherProtestInfo.getA_No(),typeOfProtest);

		protest_Info_DAO.save_Entity(otherProtestInfo);

		GlobalParams.aNumbersSet.remove(otherProtestInfo.getA_No());

		return otherProtestInfo;
	}

	public String getCompanyName (Integer filerId,Protest_Info parentProtestInfo, String userId, ProtestInfoFormDTO protestInfoFormDTO) throws Exception{
		String companyName = parentProtestInfo.getCompany_Name();
		switch (filerId) {
		case 2:	// intervenor
			if (protestInfoFormDTO.getCompany_name() == null || protestInfoFormDTO.getCompany_name().isEmpty()) {
				companyName = user_Info_DAO.getIntervenorCompanyName(parentProtestInfo.getA_No(), userId);
			} else {
				companyName = protestInfoFormDTO.getCompany_name();
			}
			break;
		case 3: // agency
			if (protestInfoFormDTO.getCompany_name() == null || protestInfoFormDTO.getCompany_name().isEmpty()) {
				companyName = agency_Info_DAO.getAgencyName(parentProtestInfo.getAgency_Info_Id());
			} else {
				companyName = protestInfoFormDTO.getCompany_name();
			}
			break;
		default:
			break;
		}
		return companyName;
	}

//	public String getCompanyName (String user_Role, Protest_Info parentProtestInfo) throws Exception{
//		String companyName;
//		if (user_Role.toUpperCase(Locale.ENGLISH).contains("AGENCY")) {
//			companyName = agency_Info_DAO.getAgencyName(parentProtestInfo.getAgency_Info_Id());
//		} else {
//			companyName = parentProtestInfo.getCompany_Name();
//		}
//		return companyName;
//	}

	public File_Info getFileInfoByFileId(int fileId) throws Exception {
		return protest_Info_DAO.getFileInfoByFileId(fileId);
	}

	public List<Protest_Info> getConsolidatedProtestInfoListWithParentAndChildSupplementalProtests(String aNo) throws Exception {
		return protest_Info_DAO.getConsolidatedProtestInfoListWithParentAndChildSupplementalProtests(aNo);
	}

	public void makeChangesForExceptionalDocumentTypes(String typeOfDoc,
			HttpServletRequest request, String user_Id, int docTypeId, String aNum) throws Exception {
		Protest_Info protest_Info = getProtest_Info_ByA_No(aNum);
		
		User_Info userInfo = user_Info_DAO.getUser_Info_By_User_Id(user_Id);
		UserRoles role = UserRoles.getByCode(userInfo.getRole_id());
		boolean canSubmitReqToInterve = role == GAO_ADMIN || role == PROTESTER;

		if (typeOfDoc.toLowerCase(Locale.ENGLISH)
				.contains("Supplemental Protest".toLowerCase(Locale.ENGLISH)) && canSubmitReqToInterve) {
			protest_Info = registerOtherProtestInfo(user_Id, protest_Info,
					"Supplemental", ""/*,0,""*/);
			gc_service.set_Event_For_New_Protest_Info(protest_Info);
		} else if (typeOfDoc
				.equalsIgnoreCase("Acknowledgment Package with Protective Order")
				|| typeOfDoc.equalsIgnoreCase("Notice of Protective Order") || dashboardService.findIfThisDocIdTriggerProtectiveOrder(docTypeId)) {

			List<Protest_Info> protest_Infos = protest_Info_DAO.getConsolidatedProtestInfoList(protest_Info.getA_No());

			for (Protest_Info eachProtestInfo : protest_Infos ){
				protest_Info_DAO.changeProtectiveOrder(eachProtestInfo, "Y");
			}

			//file_Info_DAO.changeProtectiveOrder(protest_Info.getA_No(), "Y");
			// user_Info_DAO.changeProtectiveOrder(protest_Info.getA_No(), "Y");
		}
	}

	public void admitToPO(String userId, String shouldAdmit, String a_No)
			throws Exception {


		List<Protest_Info> listOfConsolidatedCases = protest_Info_DAO.getConsolidatedProtestInfoList(a_No);

		for (Protest_Info eachProtest:  listOfConsolidatedCases){
			user_Info_DAO.admitToPO(userId, shouldAdmit, eachProtest.getA_No());
		}


	}

	public List<Protest_Info> getProtest_InfoListWithUnassignedB_No() {
		return protest_Info_DAO.getProtest_InfoListWithUnassignedB_No();
	}

	/**
	 *
	 * @param protest_Info
	 * @param request
	 * @param typeOfUpdate
	 * @throws Exception
	 */
	public void sendNotificationToAgencyAdminAndProtester(
			Protest_Info protest_Info, HttpServletRequest request, String typeOfUpdate) throws Exception {
		List<String> listOfEmailAddresses = new ArrayList<String>();
		List<User_Info> agency_Admin_User_Info_List = userInfoService.getListOfAgencyPOCUserInfoByFirmId(protest_Info
				.getAgency_Info_Id());

		if (agency_Admin_User_Info_List != null && !agency_Admin_User_Info_List.isEmpty()) {

			listOfEmailAddresses = new ArrayList<String>();

			for (User_Info each_Agency_Admin_User_Info : agency_Admin_User_Info_List) {
				if (each_Agency_Admin_User_Info == null) {
					continue;
				}
				listOfEmailAddresses.add(each_Agency_Admin_User_Info
						.getEmail());
				user_Info_DAO.assignProtest_To_Agency_Admin(protest_Info,
						each_Agency_Admin_User_Info.getUser_Id());

			}
		}

		if (listOfEmailAddresses != null && listOfEmailAddresses.size() > 0){
			emailService.sendNoticeForFileNewProtest(request,protest_Info,listOfEmailAddresses);
		}

		// no need to send payment confirmation for CBCA
//		if (typeOfUpdate.equalsIgnoreCase("newProtest")){
//			emailService.sendPaymentConfirmationToProtester(request,protest_Info);
//		}

	}

	public void sendNotificationToPartiesAssociatedWithTheCase(
			Protest_Info protest_Info, HttpServletRequest request) {
			List<String> toMailAddresses = new ArrayList<String>();

			try {
				populateListOfEmails(toMailAddresses,protest_Info);

				if (null != toMailAddresses && toMailAddresses.size() > 0){
					emailService.sendNoticeForOtherCaseTypes(request, protest_Info, toMailAddresses);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

	}

	/*
	 * ENTITLEMENT :  (sent to protester & agency representatives from the underlying case)
	 * RECONSIDERATION :  (sent to all parties from the underlying case and its associated cases)
	 */
	private void populateListOfEmails(List<String> toMailAddresses,Protest_Info protest_Info){

		List<Protest_Info> listOfConsolidatedCases = protest_Info_DAO.getConsolidatedProtestInfoList(protest_Info.getA_No());

		try {
			if (protest_Info.getCase_Type().toUpperCase(Locale.ENGLISH).contains("ENT")
					|| protest_Info.getCase_Type().toUpperCase(Locale.ENGLISH).contains("COST")){
				listOfConsolidatedCases = new ArrayList<Protest_Info>();
				listOfConsolidatedCases.add(protest_Info);
			}
			for (Protest_Info eachProtest : listOfConsolidatedCases){
				List<User_Protest_Role_Bridge> uprb = user_Info_DAO.getUser_Protest_Role_Bridge_List_BasedOnProtestId(eachProtest.getA_No());

				User_Protest_Role_Bridge eachUPRB = null;
				UserRoles role;
				for (int i= 0 ; i < uprb.size() ; i++){
					eachUPRB = uprb.get(i);
					role = UserRoles.getByCode(eachUPRB.getRole_Id());

					if ((protest_Info.getCase_Type().toUpperCase(Locale.ENGLISH).contains("ENT")
							|| protest_Info.getCase_Type().toUpperCase(Locale.ENGLISH).contains("COST"))
							&& (role == PROTESTER || role == SECONDARY_PROTESTER  || role == AGENCY_ATTORNEY)){
						toMailAddresses.add(user_Info_DAO.getUser_Info_By_User_Id(eachUPRB.getUser_Id()).getEmail());
					}else if (protest_Info.getCase_Type().toUpperCase(Locale.ENGLISH).contains("RECON")){
						toMailAddresses.add(user_Info_DAO.getUser_Info_By_User_Id(eachUPRB.getUser_Id()).getEmail());
					}

				}
			}

			//this step is to remove Duplicates if any
			Set<String> emailAddressSet = new HashSet<String>(toMailAddresses);
			toMailAddresses = new ArrayList<String>(emailAddressSet);

			return;
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	private List<String> getAgency_Admin_Id_List(String emailListInString) {
		List<String> agency_Email_List = new ArrayList<String>();

		String[] agency_Email_ArrayList = emailListInString.split(";");
		for (String each_Email : agency_Email_ArrayList) {
			agency_Email_List.add(each_Email.trim());
		}

		return agency_Email_List;
	}



	public Protest_Info getProtest_Info_ByA_No(String a_No) throws Exception {
		return protest_Info_DAO.getProtestByA_no(a_No);
	}

	List<Protest_Info> getList_of_child_protest_info(String a_no) {
		return protest_Info_DAO.get_list_of_child_protest_info(a_no);
	}


	/*
	 * Since when the cases are joined there can be multiple different B numbers
	 * associated with a case. We need to create Map object where the key will
	 * be ProtestInfo entity and value will be listOfToEmailAddresses.
	 *
	 * 1) Since in the consolidated protest info we will not get parent protest
	 * info we first calculate the the user protest role bridge Iterate over the
	 * returned list and can calculate list of email addresses
	 * user_Info_DAO.getUser_Info_By_User_Id(each.getUser_Id()).getEmail()
	 */
	public void sendEmailNotificationsToAllPartiesAssociatedWithThisCase(SubmitNewDocDTO submitNewDocDTO,HttpServletRequest request) {


		List<Protest_Info> protestInfoList = new ArrayList<Protest_Info>();

		//List<User_Info> agencyPocUserInfoList = null;
		List<User_Info> superVisorUserInfoList = null;
		List<User_Info> plcgUserInfoList = user_Info_DAO.getListOfUsersByRoleId(7);
		List<String> lisofOtherUsersThatNeedsToNotified;

		List<User_Protest_Role_Bridge> listOfUserProtestRoleBridge = null;

		List<String> listOfToMailAddresses = null;
		Map<Protest_Info, List<String>> mapOfProtestInfoAndEmailAddresses = new HashMap<Protest_Info, List<String>>();
		Map<String, String> mapOfBNumberToDocketEntryNumber = new HashMap<String, String>();

		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request,
				"user_Info");
		try {

		protestInfoList = protest_Info_DAO.getConsolidatedProtestInfoList(submitNewDocDTO.getProtestId());


		if (protestInfoList != null && protestInfoList.size() > 0) {
			Protest_Info eachConsolidatedProtestInfo;
			Protest_Info mainProtestInfo = protestInfoList.get(0);
			Integer indexOfMainProtestInfo = 0;

			if (mainProtestInfo.getParent_A_No() != null){

				mainProtestInfo = protest_Info_DAO.getProtestByA_no(mainProtestInfo.getParent_A_No());
				indexOfMainProtestInfo = protestInfoList.indexOf(mainProtestInfo);
				protestInfoList.get(indexOfMainProtestInfo);

			}

			for (int i = 0; i < protestInfoList.size(); i++) {

				eachConsolidatedProtestInfo = protestInfoList.get(i);
				lisofOtherUsersThatNeedsToNotified = new ArrayList<String>();
				if (eachConsolidatedProtestInfo.getAttorney_Group_Id() != null) {

					superVisorUserInfoList = user_Info_DAO
							.getSupervisorInfoByGroupId(Long.valueOf(eachConsolidatedProtestInfo.getAttorney_Group_Id()));

					populateListOfOtherUserThatNeedsToBeNotified(superVisorUserInfoList, eachConsolidatedProtestInfo,lisofOtherUsersThatNeedsToNotified);

				}

				populateListOfOtherUserThatNeedsToBeNotified(plcgUserInfoList, eachConsolidatedProtestInfo,lisofOtherUsersThatNeedsToNotified);

				if (!eachConsolidatedProtestInfo.getCase_Type().equalsIgnoreCase("SUPPLEMENTAL")) {

					listOfUserProtestRoleBridge = new ArrayList<User_Protest_Role_Bridge>();
					listOfToMailAddresses = new ArrayList<String>();

					listOfUserProtestRoleBridge = user_Info_DAO
							.getUserProtestRoleBridgeListBasedOnProtestIdAndEmailPreferences(eachConsolidatedProtestInfo
									.getA_No());

					for (User_Protest_Role_Bridge each : listOfUserProtestRoleBridge) {
						if (each.getRole_Id() != AGENCY_ADMIN.getCode()){
							listOfToMailAddresses.add(user_Info_DAO.getUser_Info_By_User_Id(each.getUser_Id()).getEmail());
						}
					}

					listOfToMailAddresses.addAll(lisofOtherUsersThatNeedsToNotified);


					Set<String> toMailAddressSet = new HashSet<String>(listOfToMailAddresses);

					mapOfProtestInfoAndEmailAddresses.put(eachConsolidatedProtestInfo,new ArrayList<String>(toMailAddressSet));

					List<File_Info> fileInfoList = dashboardService
							.getFinalFileInfoList(eachConsolidatedProtestInfo, user_Info.getUser_Id());

					Set<String> dupe = new HashSet<String>();
					fileInfoList.removeIf(fileInfo->!dupe.add(fileInfo.getOriginalSubmissionDate()));

					ZipFile_Util.sortFileInfoListBySubmissionDateAndFileId(fileInfoList);

					this.commentsOrNotesAdded(submitNewDocDTO,fileInfoList);
					mapOfBNumberToDocketEntryNumber.put(eachConsolidatedProtestInfo.getB_No(),Integer.toString(fileInfoList.size()));

				}

			}

			if (submitNewDocDTO.getUser_Role() == null) {
				submitNewDocDTO.setUser_Role(user_Info_DAO.getUserRoleByRoleId(user_Info.getRole_id()).getRole_Desc());
			}
			emailService.sendMailtoAllPartiesAssocatedWithThisCase(mapOfProtestInfoAndEmailAddresses, mapOfBNumberToDocketEntryNumber, submitNewDocDTO,request);
		}

		}catch (Exception e){
			System.out.println("we are not able to send email");
		}

	}


	/**
	 * @param userInfoList
	 * @param mainProtestInfo
	 * @param listOfToMailAddresses
	 */
	private void populateListOfOtherUserThatNeedsToBeNotified(List<User_Info> userInfoList,
			Protest_Info mainProtestInfo, List<String> listOfToMailAddresses) {

		if (userInfoList == null){
			return;
		}

		for (User_Info eachUser : userInfoList){
			if ((null != eachUser.getGlobalEmailPref() && "Y".equalsIgnoreCase(eachUser.getGlobalEmailPref()))
					|| eachUser.getRole_id().equals(GAO_ADMIN.getCode())){
				String[] arrayOfANos = eachUser.getaNumNotifications() != null ? eachUser.getaNumNotifications().split(";") : new String [0];

				Set<String> setOfAnos = new HashSet<String>(Arrays.asList(arrayOfANos));

				if (!setOfAnos.isEmpty() && setOfAnos.contains(mainProtestInfo.getA_No())){

					listOfToMailAddresses.add(eachUser.getEmail());
				}
			}else if (eachUser.getCds_preferences() != null
					&& !eachUser.getCds_preferences().equalsIgnoreCase("")) {

				String[] arrayOfANos = eachUser.getCds_preferences().split(";");

				Set<String> setOfAnos = new HashSet<String>(Arrays.asList(arrayOfANos));

				if (!setOfAnos.contains(mainProtestInfo.getA_No())){

					listOfToMailAddresses.add(eachUser.getEmail());
				}

			}else{
				listOfToMailAddresses.add(eachUser.getEmail());
			}
		}
	}


	private void commentsOrNotesAdded(SubmitNewDocDTO submitNewDocDTO, List<File_Info> fileInfoList){

			if (submitNewDocDTO.getTypeofdocument().equalsIgnoreCase("Minute Entry")){
				submitNewDocDTO.setTypeofdocument("comments added");
				submitNewDocDTO.setComments(submitNewDocDTO.getAttorney_note());
				submitNewDocDTO.setDocketEntryTitle("Minute Entry");
			}

			if (submitNewDocDTO.getTypeofdocument()
					.equalsIgnoreCase("comments added")) {
				for (File_Info eachFileInfo : fileInfoList) {

					if (eachFileInfo.getOriginalSubmissionDate().equalsIgnoreCase(submitNewDocDTO.getSubmissionDate())) {
						submitNewDocDTO.setDocketEntryNumber(String.valueOf(fileInfoList.indexOf(eachFileInfo) + 1));
					}
				}
			}
		}

	public List<File_Info> getDupsRemovedFileInfoList(
			List<File_Info> file_InfoList) {
		List<File_Info> newFileInfoList = new ArrayList<File_Info>();
		for (File_Info each : file_InfoList) {
			if (!newFileInfoList.contains(each)) {
				newFileInfoList.add(each);
			}
		}

		return newFileInfoList;
	}

	public String getAgencyTrackingId(HttpServletRequest request) {

		String a_no = request.getParameter("a_No");
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request,
				"user_Info");
		String user_id = user_Info.getUser_Id();

		String agency_tracking_id = a_no + "_" + user_id;

		if (agency_tracking_id.length() > 21)
			agency_tracking_id = agency_tracking_id.substring(0, 21).toString();

		return agency_tracking_id;
	}

	public boolean findIfUserAlreadyHasAccessToCase(String userId, String bNum) {
		List<User_Protest_Role_Bridge> user_protest_role_bridge_list = protest_Info_DAO
				.getUser_protest_role_bridge_list(userId, bNum);

		if (user_protest_role_bridge_list != null
				&& user_protest_role_bridge_list.size() > 0) {
			return true;
		}

		return false;
	}

	public List<Protest_Info> getListOfProtestInfoAgencyRepCanRequestToAccess(
			String bNo, Integer firm_id, String userId, Map<String, Protest_Info> protestInfoMap) throws Exception {
		List<Protest_Info> protestInfoList = protest_Info_DAO
				.getProtestInfoListByBNumberWildSearchAndFirmId(bNo, firm_id);
		List<File_Info> fileInfoList = file_Info_DAO
				.getListOfDocsUserHasSubmittedToMakeRequestToAccess(userId);

		if (null != fileInfoList){
			filterOutProtestsThatUserAlreadyMadeRequestTo(fileInfoList,
					protestInfoList);
			}
		if (null != protestInfoMap){
			protestInfoList = filterOutProtestsThatUserHasAlreadyHasAccessTo(protestInfoList,protestInfoMap);
		}

		if(null != protestInfoList && protestInfoList.size() == 1) {
			protestInfoList = addAlltheAssociatedConsolidatedProtests(protestInfoList);
		}

		return protestInfoList;
	}

	public List<Protest_Info> getListOfProtestInfointervenorCanRequestToAccess(
			String bNo, String userId,Map<String,Protest_Info> protestInfoMap) {
		List<Protest_Info> protestInfoList = protest_Info_DAO
				.getProtestInfoListForIntervenorAccessByBNumberWildSearch(bNo);
		List<File_Info> fileInfoList = file_Info_DAO
				.getListOfDocsUserHasSubmittedToMakeRequestToAccess(userId);

		if (null != fileInfoList){
		filterOutProtestsThatUserAlreadyMadeRequestTo(fileInfoList,
				protestInfoList);
		}

		if (null != protestInfoMap){
			protestInfoList = filterOutProtestsThatUserHasAlreadyHasAccessTo(protestInfoList,protestInfoMap);
		}

		if(null != protestInfoList && protestInfoList.size() == 1) {
			protestInfoList = addAlltheAssociatedConsolidatedProtests(protestInfoList);
		}
		return protestInfoList;
	}

	public List<Protest_Info> addAlltheAssociatedConsolidatedProtests(List<Protest_Info> protestInfoList){

		Set<String> uniqueResultSet = new HashSet<String>();
		List<Protest_Info> finalConsolidatedProtestInfoList = new ArrayList<Protest_Info>();

		if (null != protestInfoList && !protestInfoList.isEmpty()){

			for (Protest_Info eachProtestInfo : protestInfoList){
				List<Protest_Info> eachConsolidateProtestInfoList = protest_Info_DAO.getConsolidatedProtestInfoListWithParentAndChildSupplementalProtests(eachProtestInfo.getA_No());
				//eachConsolidateProtestInfoList.remove(0); although there will be extra but the set iteration will take care of all dupes
				finalConsolidatedProtestInfoList.addAll(eachConsolidateProtestInfoList);
			}

			protestInfoList.addAll(finalConsolidatedProtestInfoList);

			protestInfoList = protestInfoList.stream()
	            .filter(e -> uniqueResultSet.add(e.getA_No()))
	            .collect(Collectors.toList());
			
			return protestInfoList;

		}

		return protestInfoList;

	}


	public List<?> getUniqueList(List<?> listOfObjects, Object entityClass){

		List<Object> result = new ArrayList<Object>();
		Set<String> uniqueResultSet = new HashSet<String>();

		if (null != listOfObjects && !listOfObjects.isEmpty()){

			for( Object item : listOfObjects ) {

				if (entityClass instanceof User_Info){
					User_Info userInfo  = (User_Info) item;
					if(uniqueResultSet.add(userInfo.getUser_Id())) {
				        result.add(userInfo);
				    }
				}else if (entityClass instanceof Protest_Info){
					Protest_Info protest_Info  = (Protest_Info) item;
					if(uniqueResultSet.add(protest_Info.getA_No())) {
				        result.add(protest_Info);
				    }
				}

			}

		}

		return result;

	}


	private List<Protest_Info> filterOutProtestsThatUserHasAlreadyHasAccessTo (List<Protest_Info> protestInfoList, Map<String,Protest_Info> protestInfoMap){

		List<Protest_Info> finalProtestInfoList = new ArrayList<Protest_Info>();
		if (protestInfoList != null){
			
			for(Protest_Info eachProtestInfo : protestInfoList){

				Protest_Info exisitngProtestInfo = protestInfoMap.get(eachProtestInfo.getA_No());
			    if(exisitngProtestInfo != null && null != exisitngProtestInfo
			    		.getCaseAccessRequestStatus() && 
			    		!exisitngProtestInfo
			    		.getCaseAccessRequestStatus().equalsIgnoreCase("D")){
			    	finalProtestInfoList.add(eachProtestInfo);
			    }
			}
			protestInfoList.removeAll(finalProtestInfoList);
		}

		return protestInfoList;
	}

	private void filterOutProtestsThatUserAlreadyMadeRequestTo(
			List<File_Info> fileInfoList, List<Protest_Info> protestInfoList) {
		Protest_Info protestInfo;
		if (null != protestInfoList){
			for (File_Info eachFileInfo : fileInfoList) {
				for (int i = 0; i < protestInfoList.size(); i++) {
					protestInfo = protestInfoList.get(i);
					if (eachFileInfo.getA_No().equalsIgnoreCase(
							protestInfo.getA_No())) {
						protestInfoList.remove(i);
					}
				}
			}
		}
	}



	public void addRequestToAccessCaseApprovedDocumentEntry(
			HttpServletRequest request, String company_Name, String accessType,
			String agency_Name, User_Info repUserInfo, User_Info loggedInUserInfo, Protest_Info protest_Info) throws Exception {
		SubmitNewDocDTO submitNewDocDTO = getSubmitNewDocDtoForRequestToAccessCaseApproved(company_Name, accessType, agency_Name,repUserInfo,loggedInUserInfo,protest_Info);
		saveFilesToDB(submitNewDocDTO, request);
	}

	private SubmitNewDocDTO getSubmitNewDocDtoForRequestToAccessCaseApproved(String companyName, String accessType,
			String agencyName, User_Info repUserInfo, User_Info loggedInUserInfo, Protest_Info protest_Info) {
		
		
		String user_Id = loggedInUserInfo.getUser_Id();

		SubmitNewDocDTO submitNewDocDTO = new SubmitNewDocDTO();

		submitNewDocDTO.setDocDescFiller("");
		submitNewDocDTO.setIsDocConfidential("N");
		submitNewDocDTO.setProtestId(protest_Info.getA_No());
		submitNewDocDTO.setSubmissionDate(Date_Util.getCurrentDate());
		submitNewDocDTO.setUser_Id(user_Id);
		submitNewDocDTO.setCompany_Name(companyName);
		submitNewDocDTO.setCompany_Address("");
		submitNewDocDTO.setUser_Role("GAO");

		if (accessType.equalsIgnoreCase("intervene")) {
			submitNewDocDTO.setDocId(159);
			submitNewDocDTO.setTypeofdocument("Request to Intervene Approved");
			submitNewDocDTO
					.setAttorney_note(
							
							"'Request to Intervene' approved for "
							+ repUserInfo.getFirst_Name() +"  " + repUserInfo.getLast_Name()
							+ " from "
							+ (companyName != null? companyName : ""));
		} else if (accessType.equalsIgnoreCase("agency-rep-access")) {
			submitNewDocDTO.setDocId(162);
			submitNewDocDTO
					.setTypeofdocument("Notice of Appearance Acknowledged");
			submitNewDocDTO.setAttorney_note(
					
					"Notice of Appearance for Agency rep "
					+ repUserInfo.getFirst_Name() +"  " + repUserInfo.getLast_Name()
					+ " from "
					+ (agencyName!= null ? agencyName : "" + " acknowledged"));
		}

		return submitNewDocDTO;
	}


	public Boolean checkIfThisProtestIsSupplemental(Protest_Info protestInfo, Protest_Info supplementalProtestInfo){
		return Protest_info_util.checkIfThisProtestIsSupplemental(protestInfo, supplementalProtestInfo);
	}

	public void notifyAgencyRepOrIntervenorAboutAccessDenied(HttpServletRequest request,String accessType, File_Info fileInfo,Protest_Info protestInfo) throws Exception {

		try {
		User_Info user_Info = user_Info_DAO.getUser_Info_By_User_Id(fileInfo.getSubmitter_User_Id());
		emailService.notifyAgencyRepOrIntervenorAboutAccessDenied(request,accessType,protestInfo,user_Info.getEmail());
		}catch(Exception e){
			System.out.println("Case Access request denied Email was not sent");
			System.out.println();
			e.printStackTrace();
		}


	}

    public void updatePayDotGovResults(String a_Num, String agencyId, String trackingId, String transactionStatus, HttpServletRequest request)
            throws Exception {
        Protest_Info protest_Info;
        protest_Info = getProtest_Info_ByA_No(a_Num);

        try {

        	if (protest_Info != null){
        	    GC_Track_Service_Call_Response response = new GC_Track_Service_Call_Response();
        	    if (gc_service.get_list_of_EPDS_event(a_Num, response) == null) {
        	        // only update GCTrack and protest if not already added
        	        gc_service.set_Event_For_New_Protest_Info(protest_Info);

                    protest_Info.setPay_dot_gov_id(trackingId);
                    protest_Info.setAgency_tracking_id(agencyId);
                    if(null != transactionStatus && transactionStatus.equalsIgnoreCase("Success")){
                        protest_Info.setTransaction_Status("PAID");

                    } else {
                        protest_Info.setTransaction_Status(transactionStatus == null ? "UNPAID": transactionStatus);
                    }
                    protest_Info_DAO.updateProtest_Info(protest_Info);

                    if(null != transactionStatus && transactionStatus.equalsIgnoreCase("Success")){
                    	sendNotificationToAgencyAdminAndProtester(protest_Info,request,"newProtest");
                    }
        	    }

            }
        }catch (Exception e){
        	System.out.println("There was a problem updating protest Info");
        	e.printStackTrace();
        }

    }

	public List<Protest_Info> getProtestInfoByUniqueCaseStatus() {
		
		return protest_Info_DAO.getProtestInfoByUniqueCaseStatus();
	}

	public void validateAccess(String typeOfProtest, User_Info loggedInUserInfo, Protest_Info protestInfoByA_no,
			User_Protest_Role_Bridge uprb) throws IllegalAccessError {

		if (protestInfoByA_no.getCase_Status()
				.toUpperCase(Locale.ENGLISH).contains("OPEN")){
			throw new IllegalAccessError("Request Cannot be completed!! ");
		}

		UserRoles role = UserRoles.getByCode(loggedInUserInfo.getRole_id());
		if (role == GAO_ADMIN)
			return;
		
		if (typeOfProtest.toUpperCase(Locale.ENGLISH).contains("RECON")
						|| typeOfProtest.toUpperCase(Locale.ENGLISH).contains("ENT")
						|| typeOfProtest.toUpperCase(Locale.ENGLISH).contains("COST")){
			
			
			//other than PLCG and AGENCY Admin uprb has to exist to be able to register these protests
			if(role != AGENCY_ADMIN && uprb == null){
				throw new IllegalAccessError("Unauthorized!! ");
			}
			
			//ENT and cost's other than GAO admin only protester can file
			if (role != PROTESTER &&( typeOfProtest.toUpperCase(Locale.ENGLISH).contains("ENT")
					|| typeOfProtest.toUpperCase(Locale.ENGLISH).contains("COST"))){
				throw new IllegalAccessError("Unauthorized!! ");
			}
		}
	}
}
