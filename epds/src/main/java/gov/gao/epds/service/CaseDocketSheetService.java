package gov.gao.epds.service;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.gao.epds.dao.Agency_Info_DAO;
import gov.gao.epds.dao.File_Info_DAO;
import gov.gao.epds.dao.GC_Track_Service_DAO;
import gov.gao.epds.dao.Protest_Info_DAO;
import gov.gao.epds.dao.User_Info_DAO;
import gov.gao.epds.dto.CompanyInfo;
import gov.gao.epds.dto.EditPartyInfo;
import gov.gao.epds.dto.SubmitNewDocDTO;
import gov.gao.epds.dto.TemplateDataDTO;
import gov.gao.epds.dto.UploadedFileIdentifier;
import gov.gao.epds.enums.UserRoles;
import static gov.gao.epds.enums.UserRoles.*;
import gov.gao.epds.filestorage.SFTP;
import gov.gao.epds.persistence.entity.Doc_Info;
import gov.gao.epds.persistence.entity.File_Info;
import gov.gao.epds.persistence.entity.GAO_User;
import gov.gao.epds.persistence.entity.GC_Track_Service_Event;
import gov.gao.epds.persistence.entity.Protest_Dm_Info;
import gov.gao.epds.persistence.entity.Protest_Info;
import gov.gao.epds.persistence.entity.Tier_1_Agency;
import gov.gao.epds.persistence.entity.User_Info;
import gov.gao.epds.persistence.entity.User_Protest_Role_Bridge;
import gov.gao.epds.session.EpdsSession;
import gov.gao.epds.utils.CaseDocketPDFUtil;
import gov.gao.epds.utils.Date_Util;
import gov.gao.epds.utils.GlobalParams;
import gov.gao.epds.utils.Protest_info_util;
import gov.gao.epds.utils.Util;
import gov.gao.epds.utils.ZipFile_Util;
import gov.gao.epds.utils.templates.TemplateUtils;


@Service
public class CaseDocketSheetService {
	private static final Logger logger = LoggerFactory.getLogger(CaseDocketSheetService.class);

	@Autowired
	private Protest_Info_DAO protest_Info_DAO;
	@Autowired
	private File_Info_DAO file_Info_DAO;
	@Autowired
	private User_Info_DAO user_Info_DAO;
	@Autowired
	private Agency_Info_DAO agency_Info_DAO;
	@Autowired
	private HomeService homeService;

	@Autowired
	private UserInfoService userInfoService;

	@Autowired
	private GC_Track_Service_DAO gcTrackServiceDAO;
	@Autowired
	private GC_Service gc_Service;
	@Autowired
	private DashboardService dashboardService;

	@Autowired
	private ProtestInfoService protestInfoService;


	@SuppressWarnings("unchecked")
	public List<Doc_Info> getDoc_InfoList(Protest_Info protestInfo)
			throws Exception {
		String userDocumentRole = getRoleForSubmitDocument(protestInfo
				.getRole());
		List<Doc_Info> wholeDoc_InfoList = (List<Doc_Info>) GlobalParams.globalParam
				.get("whole_Doc_Info_List");

		List<Doc_Info> doc_InfoList = getListOfDocInfoBasedOnDocumentRole(
				userDocumentRole, wholeDoc_InfoList,protestInfo);
		removeUnwantedDoc_Info(doc_InfoList, protestInfo.getB_No());


		return doc_InfoList;
	}

	/**
	 * @param request
	 * @param aNum
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public File downloadOfflineCds(HttpServletRequest request, String aNum) throws Exception, IOException {
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request,"user_Info");
		File inputFile;
		String tempPdfFilePath = GlobalParams.fileStorageBasePath;
		
		Protest_Info protestInfo = dashboardService.getProtestInfoBasedOnUserIdAndANo(user_Info, aNum);
		
		tempPdfFilePath +=  user_Info.getUser_Id()+ System.getProperty("file.separator") + "cds.pdf";
		inputFile = new File(tempPdfFilePath);
		if (!inputFile.exists()){
			if(!inputFile.getParentFile().exists()){
				inputFile.getParentFile().mkdirs();
				inputFile.createNewFile();
		    }
			
		}
		List<String> intervenorCompanyNameList = dashboardService.getIntervenorCompanyNameList(aNum);
		User_Info attorney_Info = dashboardService.getAttorneyInfo(aNum);
		List<File_Info> fileInfoList = dashboardService.getFileInfoList(protestInfo, user_Info.getUser_Id(),true);
		dashboardService.populateAgencyNameForAgencyInFileInfoList(fileInfoList);
		ZipFile_Util.sortFileInfoListBySubmissionDateAndFileId(fileInfoList);
		CaseDocketPDFUtil.createPDF(tempPdfFilePath, fileInfoList, this, protestInfo,
				intervenorCompanyNameList, attorney_Info,true, "Offline Case Docket");
		return inputFile;
	}
	
	
	private List<Doc_Info> getListOfDocInfoBasedOnDocumentRole(
			String userDocumentRole, List<Doc_Info> wholeDoc_InfoList, Protest_Info protestInfo) {
		List<Doc_Info> docInfoList = new ArrayList<Doc_Info>();

		if (userDocumentRole.equalsIgnoreCase("GAO ADMIN")) {
			docInfoList = wholeDoc_InfoList;
		} else {
		    Integer [] listOfDocIds = {9,64,151,176 // Notice of Filing of Related Judicial Proceeding (Prot/Int)
		                              ,7,62,149,174 // Request to Use Protected Material in a Related Proceeding (Prot/Int)
		                              ,221,222,223,224,228,230 // Request to Modify Protective Order  (Prot/Int)
		                              };
		    

		    Integer [] agencyCloseDocIds = {239,240,241}; //Response to GAO Sustain Recommendation;
			for (Doc_Info eachDocInfo : wholeDoc_InfoList) {

				if (!userDocumentRole.toUpperCase(Locale.ENGLISH).contains("GAO")
						&& protestInfo.getCase_Status().equalsIgnoreCase("CLOSED")){

					boolean isAgency = userDocumentRole.toUpperCase(Locale.ENGLISH).contains("AGENCY") && ArrayUtils.contains(agencyCloseDocIds, eachDocInfo.getDoc_Type_Id());
					
				    if ((eachDocInfo.getDoc_Type_Desc().equalsIgnoreCase("Objection to ____")
							|| eachDocInfo.getDoc_Type_Desc().equalsIgnoreCase("Proposed redactions to GAO Decision")
							|| eachDocInfo.getDoc_Type_Desc().equalsIgnoreCase("No Objection to ____")
							|| eachDocInfo.getDoc_Type_Desc().equalsIgnoreCase("Final Redacted Version of ____")
							|| ArrayUtils.contains(listOfDocIds, eachDocInfo.getDoc_Type_Id())
							|| isAgency)
                            && eachDocInfo.getCase_Type().contains(protestInfo.getCase_Type().substring(0,4))
							&& eachDocInfo.getRole().equalsIgnoreCase(userDocumentRole)) {
						docInfoList.add(eachDocInfo);
					}
				}else if (eachDocInfo.getRole().equalsIgnoreCase(userDocumentRole)) {
					docInfoList.add(eachDocInfo);
				}
			}
		}

		return docInfoList;
	}



	public String getTypeOfDocFromDocId(int docTypeId)
			throws Exception {
		String typeOfDoc = file_Info_DAO.getTypeOfDocByDocTypeId(docTypeId);


		return typeOfDoc;
	}

	private String getRoleForSubmitDocument(String protestSpecificRole) {
		String documentRole = "";

		switch (protestSpecificRole) {
		case "PROTESTER":
		case "SECONDARY PROTESTER":
			documentRole = "PROTESTER";
			break;
		case "INTERVENOR":
		case "SECONDARY INTERVENOR":
			documentRole = "INTERVENOR";
			break;
		case "AGENCY ATTORNEY":
		case "AGENCY ADMIN":
			documentRole = "AGENCY";
			break;
		case "GAO ATTORNEY":
		case "GAO SUPERVISOR":
			documentRole = "GAO";
			break;
		case "GAO ADMIN":
			documentRole = "GAO ADMIN";
		}

		return documentRole;
	}

	private void removeUnwantedDoc_Info(List<Doc_Info> doc_InfoList, String b_No) {
		Doc_Info each_doc_info;
		for (int i = 0; i < doc_InfoList.size(); i++) {
			each_doc_info = doc_InfoList.get(i);

			if (checkIfDocumentIsRemovable(each_doc_info, b_No)) {
				doc_InfoList.remove(i);
				i--;
			}
		}

	}

	private boolean checkIfDocumentIsRemovable(Doc_Info docInfo, String b_No) {
		boolean isDocumentRemovable = false;

		switch (docInfo.getDoc_Type_Desc().toLowerCase(Locale.ENGLISH)) {
		case "request to intervene":
		case "minute entry":
		case "zip":
		case "request to intervene approved":
		case "denial of request to intervene":
		case "denial of notice of appearance":
		case "notice of appearance acknowledged":
			isDocumentRemovable = true;
		}

		return isDocumentRemovable;
	}

	public Map<Integer, String> getDoc_InfoMap(List<Doc_Info> doc_InfoList) {
		Map<Integer, String> doc_InfoMap = new HashMap<Integer, String>();
		for (Doc_Info eachDoc_Info : doc_InfoList) {
			doc_InfoMap.put(eachDoc_Info.getDoc_Type_Id(),
					eachDoc_Info.getDoc_Type_Desc());
		}

		return doc_InfoMap;
	}

	public List<User_Info> getUserInfoListByANum(String a_No) throws Exception {
		return user_Info_DAO.getUser_InfoListByAnum(a_No);
	}

	@Transactional
	public void indicateAsViewed(List<File_Info> file_InfoList,
			String fileAlert, String user_Id) throws Exception {
		
		
		
		if (fileAlert.equalsIgnoreCase("Y")) {
			String alreadyViewedBy;
			Set<String> setOfAlreadyViewedUserIds = new HashSet<String>();
			
			for (File_Info eachFileInfo : file_InfoList) {
				alreadyViewedBy = eachFileInfo.getAlready_viewed_by();
				setOfAlreadyViewedUserIds.add(user_Id);
				
				if (alreadyViewedBy != null) {
					String[] arrayOfUserIds = alreadyViewedBy.split(";");
					setOfAlreadyViewedUserIds.addAll(Arrays.asList(arrayOfUserIds));
					
				}
				eachFileInfo.setAlready_viewed_by(StringUtils.join(setOfAlreadyViewedUserIds,";"));
			}

			file_Info_DAO.update(file_InfoList);
		}

		// file_Info_DAO.indicateAsViewed(file_InfoList, user_Id);
	}





	@Transactional
	public void addOrEditAttorneyNoteInFileInfoRecord(String file_Id,
			String note,String gaoNote,HttpServletRequest request) throws Exception {
		File_Info file_Info = file_Info_DAO.getFile_Info(file_Id);

		User_Info loggedInUserInfo = (User_Info) EpdsSession.getAttribute(request, "user_Info");
		
        Protest_Info protestInfo = dashboardService.getProtestInfoBasedOnUserIdAndANo(loggedInUserInfo,file_Info.getA_No());
		
		dashboardService.validateAccess(loggedInUserInfo, protestInfo);

        UserRoles role = UserRoles.getByCode(loggedInUserInfo.getRole_id());
		if ( role != GAO_ADMIN && role != GAO_SUPERVISOR && role != GAO_ATTORNEY){
			throw new IllegalAccessError("Unauthorized!!");
		}

		// guaranteed to be one of the above: GAO_ADMIN, GAO_SUPERVISOR, GAO_ATTORNEY
		// if view only, only GAO_ADMIN
		if (protestInfo.isViewOnly() && role != GAO_ADMIN){
			throw new IllegalAccessError("Unauthorized!!");
		}
		
		if (file_Info.getAttorney_Note() != null){
			file_Info.setIsAttorneyNoteEdited("Y");
		}
		file_Info.setAttorney_Note(note);

		if (logger.isDebugEnabled()){
			logger.debug("addOrEditAttorneyNoteInFileInfoRecord a_No={},fileId = {}",file_Info.getA_No(),file_Info.getFile_Id());
		}
		file_Info_DAO.updateFile_Info(file_Info);

		SubmitNewDocDTO submitNewDocDTO = new SubmitNewDocDTO();
		submitNewDocDTO.setComments(gaoNote);
		submitNewDocDTO.setSubmissionDate(file_Info.getOriginalSubmissionDate());
		submitNewDocDTO.setProtestId(file_Info.getA_No());
		submitNewDocDTO.setTypeofdocument("comments added");
		submitNewDocDTO.setDocketEntryTitle(file_Info_DAO.getTypeOfDocByDocTypeId(file_Info.getDoc_Type_Id()));
		submitNewDocDTO.setDocDescFiller(file_Info.getFiller());
		submitNewDocDTO.setUser_Role("GAO");
		protestInfoService.sendEmailNotificationsToAllPartiesAssociatedWithThisCase(submitNewDocDTO, request);
	}



	@Transactional
	public void changeComments(String file_Id, String new_Comments)
			throws Exception {
		File_Info file_Info = file_Info_DAO.getFile_Info(file_Id);
		file_Info.setComments(new_Comments);
		file_Info_DAO.updateFile_Info(file_Info);
	}

	@Transactional
	public void changeDocumentType(String file_Id, Integer new_DocumentType_Id)
			throws Exception {
		File_Info file_Info = file_Info_DAO.getFile_Info(file_Id);
		file_Info.setDoc_Type_Id(new_DocumentType_Id);
		file_Info_DAO.updateFile_Info(file_Info);
	}

	@Transactional
	public void deleteFile(String file_Id) throws Exception {
		File_Info file_Info = file_Info_DAO.getFile_Info(file_Id);
		file_Info_DAO.deleteFile_Info(file_Info);
	}

	@Transactional
	public void updateCaseDocketEmailPreferences(String a_No, User_Info user_Info,
			String val) throws Exception {

		boolean updateUserInfo  =false;

		UserRoles role = UserRoles.getByCode(user_Info.getRole_id());
		if (role == AGENCY_ADMIN || role == GAO_SUPERVISOR) {
			if (null != user_Info.getGlobalEmailPref() && "Y".equalsIgnoreCase(user_Info.getGlobalEmailPref())){
				updateAnumNotificationList(a_No, user_Info, val);
				updateUserInfo = true;
				
			}else if (user_Info.getCds_preferences() != null && !user_Info.getCds_preferences().equalsIgnoreCase("")) {

				String[] arrayOfANos = user_Info.getCds_preferences().split(";");

				Set<String> setOfAnos = new HashSet<String>(Arrays.asList(arrayOfANos));

				if (val.equalsIgnoreCase("Y") && setOfAnos.contains(a_No)){
					setOfAnos.remove(a_No);
				}else if (val.equalsIgnoreCase("N")){
					setOfAnos.add(a_No.trim());
				}


				if (setOfAnos.size() > 0 && !setOfAnos.isEmpty()){

					user_Info.setCds_preferences(StringUtils.join(setOfAnos,";"));
				}else{
					user_Info.setCds_preferences(null);
				}
				updateUserInfo = true;
			} else if (val.equalsIgnoreCase("N")){
				user_Info.setCds_preferences(a_No.trim());
				updateUserInfo = true;
				
			}

		}
		
		if (role == GAO_ADMIN){
			updateAnumNotificationList(a_No, user_Info, val);
			updateUserInfo = true;
		}
		
		if (updateUserInfo){
			userInfoService.updateUserInfo(user_Info);
		}

		User_Protest_Role_Bridge userProtestRoleBridge = user_Info_DAO
				.getUser_Protest_Role_Bridge(a_No, user_Info.getUser_Id());

		if (userProtestRoleBridge != null){

			userProtestRoleBridge.setCasedocket_email_preferences(val);
			user_Info_DAO.update(userProtestRoleBridge);
		}

	}

	/**
	 * @param a_No
	 * @param user_Info
	 * @param val
	 */
	private void updateAnumNotificationList(String a_No, User_Info user_Info, String val) {
		String[] arrayOfANos = user_Info.getaNumNotifications() != null ? user_Info.getaNumNotifications().split(";") : new String [0];

		Set<String> setOfAnos = new HashSet<String>(Arrays.asList(arrayOfANos));

		if (val.equalsIgnoreCase("Y") && !setOfAnos.contains(a_No)){
			setOfAnos.add(a_No);
		}else if (val.equalsIgnoreCase("N")){
			setOfAnos.remove(a_No.trim());
		}
		if (!setOfAnos.isEmpty()){
			user_Info.setaNumNotifications(StringUtils.join(setOfAnos,";"));
			
		}else{
			user_Info.setaNumNotifications(null);
		}
	}
	
	@Transactional
	public void updatePartyInfo(EditPartyInfo editPartyInfo)
			throws Exception {
		
		File_Info fileInfo = null;
		
		List<Protest_Info> listOfConsolidatedCases = protest_Info_DAO
                .getConsolidatedProtestInfoListWithParentAndChildSupplementalProtests(editPartyInfo.getaNum());


	    for (Protest_Info eachConsolidatedProtestInfo : listOfConsolidatedCases) {
	    	
	    	if ("protester".equalsIgnoreCase(editPartyInfo.getPartyType()) && eachConsolidatedProtestInfo.getA_No().equalsIgnoreCase(editPartyInfo.getaNum())){
	    		eachConsolidatedProtestInfo.setCompany_Name(editPartyInfo.getCompanyName());
	    		eachConsolidatedProtestInfo.setCompany_address1(editPartyInfo.getAddress1());
	    		eachConsolidatedProtestInfo.setCompany_address2(editPartyInfo.getAddress2());
	    		eachConsolidatedProtestInfo.setCompany_City(editPartyInfo.getCity());
	    		eachConsolidatedProtestInfo.setCompany_Country(editPartyInfo.getCountry());
	    		eachConsolidatedProtestInfo.setCompany_State(editPartyInfo.getState());
	    		eachConsolidatedProtestInfo.setCompany_Zipcode(editPartyInfo.getZipCode());
	    		protest_Info_DAO.updateProtest_Info(eachConsolidatedProtestInfo);
	    		break;
	        }
	        
	        if ("intervenor".equalsIgnoreCase(editPartyInfo.getPartyType())){

	            List<User_Protest_Role_Bridge> user_Protest_Role_Bridge_List = user_Info_DAO
	                    .getUser_Protest_Role_Bridge_BasedOn_A_No_And_IntervenorName(editPartyInfo.getaNum(), editPartyInfo.getOldCompanyName());
	            SubmitNewDocDTO submitNewDocDTO = new SubmitNewDocDTO();
	            
	            submitNewDocDTO.setAddress1(editPartyInfo.getAddress1());
	            submitNewDocDTO.setAddress2(editPartyInfo.getAddress2());
	            submitNewDocDTO.setCity(editPartyInfo.getCity());
	            submitNewDocDTO.setCountry(editPartyInfo.getCountry());
	            submitNewDocDTO.setState(editPartyInfo.getState());
	            submitNewDocDTO.setZipCode(editPartyInfo.getZipCode());

	            if (null != user_Protest_Role_Bridge_List
	                    && user_Protest_Role_Bridge_List.size() > 0) {
	            	
	                for (User_Protest_Role_Bridge eachUser_Protest_Role_Bridge : user_Protest_Role_Bridge_List) {
	                    eachUser_Protest_Role_Bridge
	                            .setIntervenor_Company_Name(editPartyInfo.getCompanyName());
	                    eachUser_Protest_Role_Bridge
	                    .setIntervenor_Company_Address(Util.getAddress(submitNewDocDTO));
	                    user_Info_DAO.update(eachUser_Protest_Role_Bridge);
	                }
	            }


	            if (editPartyInfo.getIntervenorFileId() != null){
	            	fileInfo = file_Info_DAO.getFile_Info(String.valueOf(editPartyInfo.getIntervenorFileId()));
	            	if (fileInfo != null){
	            		fileInfo.setCompany_Address(Util.getAddress(submitNewDocDTO));
	            	}
	            	
	            }
	        
	        }
	    }
        
		
	}

	@Transactional
	public void changeFile_Info(final String file_Id, String newValue,
			String typeOfChange, HttpServletRequest request) throws Exception {

		User_Info loggedInUserInfo = (User_Info) EpdsSession.getAttribute(request, "user_Info");
		File_Info file_Info = file_Info_DAO.getFile_Info(file_Id);
		Protest_Info protestInfo = dashboardService.getProtestInfoBasedOnUserIdAndANo(loggedInUserInfo,file_Info.getA_No());
		
		dashboardService.validateAccess(loggedInUserInfo, protestInfo);

		UserRoles role = UserRoles.getByCode(loggedInUserInfo.getRole_id());
		if ( role != GAO_ADMIN && role != GAO_SUPERVISOR && role != GAO_ATTORNEY){
			throw new IllegalAccessError("Unauthorized!!");
		}

		// guaranteed to be one of the above: GAO_ADMIN, GAO_SUPERVISOR, GAO_ATTORNEY
		// if view only, only GAO_ADMIN
		if (protestInfo.isViewOnly() && role != GAO_ADMIN){
			throw new IllegalAccessError("Unauthorized!!");
		}

		String user_Role = (String) EpdsSession.getAttribute(request, "user_Role");
		if ("delete".equalsIgnoreCase(typeOfChange) && user_Role.equalsIgnoreCase("GAO ADMIN")) {

			if (SFTP.removeFile(new File(file_Info.getFile_Path()))){
				
				List<File_Info> fileInfos = file_Info_DAO.getFile_InfoListBasedOnSubmissionDateAndDocumentType(file_Info.getSubmission_Date(), file_Info.getDoc_Type_Id());
				
				if (null != fileInfos && fileInfos.size() > 1){
					file_Info_DAO.deleteFile_Info(file_Info);
				}else{
					file_Info.setFile_Path("C:\\"); 
					//if this is the only entry and if it is deleted than we wil just update the file path to blank/invalid entry to preserve docket entry
					file_Info_DAO.updateFile_Info(file_Info);
				}
				
				
			} else {
			    System.out.println("Delete operation is failed." + file_Info.getFile_Path() );
			    throw new RuntimeException("Delete operation is failed.");
			}

		}else if ("replaceDoc".equalsIgnoreCase(typeOfChange) && user_Role.equalsIgnoreCase("GAO ADMIN")) {
			@SuppressWarnings("unchecked")
			List<UploadedFileIdentifier> filePathList = (List<UploadedFileIdentifier>) EpdsSession.getAttribute(request, "filePathList");
			EpdsSession.removeAttribute(request, "filePathList");

			final String path =  file_Info.getFile_Path();

			File file = null;

			if (null !=  path){
				 file = new File(file_Info.getFile_Path());
			}

    		if(null !=  path
    				&& file != null
    				&& SFTP.removeFile(file)){

    			file_Info.setFile_Path(filePathList.get(0).getFilePath());
    			file_Info_DAO.updateFile_Info(file_Info);
    			System.out.println(file.getName() + " is deleted!");

    		}else{

    			/*if(filePathList != null && filePathList.size() > 0){
    				//if delete operation was failed then the file which was uploaded should also be deleted
        			SFTP.removeDir(new File(filePathList.get(0).getFilePath()));
    			}*/

    			System.out.println("Delete operation is failed." + file_Info.getFile_Path() );
    			throw new RuntimeException("Delete operation is failed.");

    		}


		} else {
			List<File_Info> file_InfoList = file_Info_DAO
					.getFile_InfoListBasedOnSubmissionDateAndDocumentTypeAndANum(file_Info.getOriginalSubmissionDate(), file_Info.getDoc_Type_Id(), file_Info.getA_No());

			boolean protest_InfoChanged = false;
            boolean commentAdded = false;
			boolean createSupplemental = true;
			for (File_Info eachFile_Info : file_InfoList) {

				if ("markAsPrimary".equalsIgnoreCase(typeOfChange) && file_Id.equalsIgnoreCase(String.valueOf(eachFile_Info.getFile_Id())) ) {
					eachFile_Info.setFile_identifier("P");
					file_Info_DAO.updateFile_Info(eachFile_Info);
				}else if ("markAsPrimary".equalsIgnoreCase(typeOfChange) && !file_Id.equalsIgnoreCase(String.valueOf(eachFile_Info.getFile_Id())) ) {
					eachFile_Info.setFile_identifier("A");
					file_Info_DAO.updateFile_Info(eachFile_Info);
				}


				if ("type-of-document".equalsIgnoreCase(typeOfChange)) {
					String [] parts = newValue.split("&&&&");
					eachFile_Info.setDoc_Type_Id(Integer.valueOf(parts[0]));
					if (parts.length >1){
						eachFile_Info.setFiller(newValue.split("&&&&")[1]);
					}else{
						eachFile_Info.setFiller(null);
					}
					
					// Create Supplemental Protest on Updating Document Type to Supplemental Protest and Comments & Supplemental Protest
					// createSupplemental - When multiple files in a docket being updated, just create the supplemental on the first iteration
					if (createSupplemental && getDocTypeName(Integer.valueOf(parts[0])).toLowerCase(Locale.ENGLISH)
							.contains("Supplemental Protest".toLowerCase(Locale.ENGLISH))) {
						Protest_Info new_protest_Info = protestInfoService.registerOtherProtestInfo(loggedInUserInfo.getUser_Id(), protestInfo,
								"Supplemental", ""/*,0,""*/);
						gc_Service.set_Event_For_New_Protest_Info(new_protest_Info);

						List<File_Info> fileInfoList = file_Info_DAO.getFileEntitybyProtestId(protestInfo.getA_No());
						Boolean foundSupplementFile = false;

						List<Integer> file_Info_IdList = new ArrayList<>();

						List<String> consolidated_A_No_List = Arrays.asList(new_protest_Info.getA_No());

						for (File_Info eachFile : fileInfoList){
							if(!foundSupplementFile && (eachFile.getFile_Id() == eachFile_Info.getFile_Id())){
								foundSupplementFile = true;
							}

							if(foundSupplementFile){
								file_Info_IdList.add(eachFile.getFile_Id());
							}

						}
						file_Info_DAO.associateFilesWithOtherOtherCases(file_Info_IdList,
								consolidated_A_No_List);

						createSupplemental = false;
					}

					file_Info_DAO.updateFile_Info(eachFile_Info);
				} else if ("confidential".equalsIgnoreCase(typeOfChange)) {
					eachFile_Info.setIs_Confidential(newValue);
					file_Info_DAO.updateFile_Info(eachFile_Info);
				}else if ("comments".equalsIgnoreCase(typeOfChange)) {
					if (eachFile_Info.getComments() != null){
						eachFile_Info.setIsCommentEdited("Y");
					}
					String note = newValue + ":::" + Date_Util.getCurrentDate();
					eachFile_Info.setComments(note);
					file_Info_DAO.updateFile_Info(eachFile_Info);

					if (!commentAdded){
						commentAdded = true;
						SubmitNewDocDTO submitNewDocDTO = new SubmitNewDocDTO();
						submitNewDocDTO.setComments(newValue);
						submitNewDocDTO.setSubmissionDate(eachFile_Info.getOriginalSubmissionDate());
						submitNewDocDTO.setProtestId(eachFile_Info.getA_No());
						submitNewDocDTO.setTypeofdocument("comments added");
						submitNewDocDTO.setUser_Role("GAO");
						submitNewDocDTO.setDocketEntryTitle(file_Info_DAO.getTypeOfDocByDocTypeId(eachFile_Info.getDoc_Type_Id()));
						submitNewDocDTO.setDocDescFiller(eachFile_Info.getFiller());
						protestInfoService.sendEmailNotificationsToAllPartiesAssociatedWithThisCase(submitNewDocDTO, request);

					}
						/*
					 * } else if (typeOfChange.equalsIgnoreCase("delete")) {
					 * List<File_Alert> file_AlertList = file_Info_DAO
					 * .getFile_AlertList(file_Id); if (file_AlertList != null
					 * && file_AlertList.size() > 0) { for (File_Alert
					 * eachFile_Alert : file_AlertList) {
					 * file_Info_DAO.deleteFile_Info(eachFile_Alert); } }
					 * file_Info_DAO.deleteFile_Info(eachFile_Info);
					 */
				} else if ("submission Date".equalsIgnoreCase(typeOfChange)) {

					String dateInEasternTimezoneFormat = newValue;

					eachFile_Info.setSubmission_Date(dateInEasternTimezoneFormat);
					if (eachFile_Info.getDoc_Type_Id() == 1
							&& !protest_InfoChanged) {
						protest_InfoChanged = true;
						protestInfo.setSubmission_Date(dateInEasternTimezoneFormat);
						protestInfo.setDue_Date(Date_Util.getDueDate(dateInEasternTimezoneFormat,100));
						protestInfo.setSubmissionDateTime(Date_Util.getESTTimeStampInLong(protestInfo.getSubmission_Date()));
						protest_Info_DAO.updateProtest_Info(protestInfo);

						String date = Date_Util.convertToSpecifiedFormat(
								protestInfo.getSubmission_Date(),
								"MMM dd yyyy HH:mm:ss z",
								"MM/dd/yyyy HH:mm:ss z");
						gc_Service.set_Event_For_Protest_Filed_Date_Updated(
								protestInfo, date);

					}
					file_Info_DAO.updateFile_Info(eachFile_Info);
				}
			}
		}

	}


	public Protest_Info changeProtest_Info_Attribute(String a_No,
			String typeOfChange, String newValue, String newValue2,
			List<String> listOfBNumbers, String oldValue,
			String agencyTier_1_Id, String agencyTier_2_Id,
			HttpServletRequest request,Protest_Info protest_Info) throws Exception {

	    List<Protest_Info> listOfConsolidatedCases = protest_Info_DAO
                .getConsolidatedProtestInfoListWithParentAndChildSupplementalProtests(a_No);


	    for (Protest_Info eachConsolidatedProtestInfo : listOfConsolidatedCases) {
	        boolean updateProtestInfo = false;
	        if ("Protester Company Name".equalsIgnoreCase(typeOfChange)) {

	            if (eachConsolidatedProtestInfo .getA_No().equalsIgnoreCase(a_No)
	                    || eachConsolidatedProtestInfo.getCompany_Name().equalsIgnoreCase(protest_Info.getCompany_Name())) {
	                protest_Info.setCompany_Name(newValue);
	                eachConsolidatedProtestInfo.setCompany_Name(newValue);

	                changeAssociatedFile_Info_List(eachConsolidatedProtestInfo.getA_No(), newValue, oldValue);
	                updateProtestInfo = true;
	            }




	        }else if ("Solicitation Number".equalsIgnoreCase(typeOfChange)) {
	            protest_Info.setSolicitation_No(newValue);
	            eachConsolidatedProtestInfo.setSolicitation_No(newValue);

	            updateProtestInfo = true;

	        }else if ("Case Status".equalsIgnoreCase(typeOfChange)) {

	            protest_Info.setCase_Status(newValue);
	            eachConsolidatedProtestInfo.setCase_Status(newValue);
	            updateProtestInfo = true;

	        }else if ("INTERVENOR Company Name".equalsIgnoreCase(typeOfChange)) {
	            List<User_Protest_Role_Bridge> user_Protest_Role_Bridge_List = user_Info_DAO
	                    .getUser_Protest_Role_Bridge_BasedOn_A_No_And_IntervenorName(
	                            eachConsolidatedProtestInfo.getA_No(), oldValue);

	            if (null != user_Protest_Role_Bridge_List
	                    && user_Protest_Role_Bridge_List.size() > 0) {
	                for (User_Protest_Role_Bridge eachUser_Protest_Role_Bridge : user_Protest_Role_Bridge_List) {
	                    eachUser_Protest_Role_Bridge
	                            .setIntervenor_Company_Name(newValue);
	                    user_Info_DAO.update(eachUser_Protest_Role_Bridge);
	                }
	            }

	            changeAssociatedFile_Info_List(eachConsolidatedProtestInfo.getA_No(), newValue, oldValue);

	        } else if ("case Type".equalsIgnoreCase(typeOfChange)) {

	            if (!eachConsolidatedProtestInfo.getCase_Type().equalsIgnoreCase("SUPPLEMENTAL") 
	            		|| eachConsolidatedProtestInfo.getA_No().equalsIgnoreCase(protest_Info.getA_No())) {
	            	protest_Info.setCase_Type(newValue);
	                eachConsolidatedProtestInfo.setCase_Type(newValue);
	                updateProtestInfo = true;
	            }



	        }else if ("Protective Order".equalsIgnoreCase(typeOfChange)) {

	            if (!eachConsolidatedProtestInfo.getCase_Type().equalsIgnoreCase("SUPPLEMENTAL") 
	            		|| eachConsolidatedProtestInfo.getA_No().equalsIgnoreCase(protest_Info.getA_No())) {
	                protest_Info_DAO.changeProtectiveOrder(eachConsolidatedProtestInfo, newValue);
	                file_Info_DAO.changeProtectiveOrder(eachConsolidatedProtestInfo.getA_No(),
	                        newValue);
	                /*
	                 * user_Info_DAO.changeProtectiveOrder(protest_Info.getA_No(),
	                 * newValue);
	                 */
	            }


	        }else if ("Attorney Info".equalsIgnoreCase(typeOfChange)) {


	            String attorney_Name = this.getAttorney_Name(newValue,request);
	            updateProtestInfo = true;
	            protest_Info.setAttorney_Name(attorney_Name);
	            protest_Info.setAttorney_Group_Id(Integer.valueOf(newValue2));


	            eachConsolidatedProtestInfo.setAttorney_Name(attorney_Name);
	            eachConsolidatedProtestInfo.setAttorney_Group_Id(Integer.valueOf(newValue2));

	            User_Protest_Role_Bridge attorney_User_Protest_Role_Bridge = user_Info_DAO
	                    .getAttorneyUser_Protest_Role_Bridge(eachConsolidatedProtestInfo.getA_No());

	            if (attorney_User_Protest_Role_Bridge != null) {
	                attorney_User_Protest_Role_Bridge.setUser_Id(newValue);
	                user_Info_DAO.update(attorney_User_Protest_Role_Bridge);
	            } else {
	                user_Info_DAO.add_User_Protest_Role_Bridge_Entity(protest_Info,
	                        newValue, 3);
	            }

	            EpdsSession.setAttribute(request, "attorney_name", attorney_Name);

	        }


	       if (updateProtestInfo) {
	           protest_Info_DAO.updateProtest_Info(eachConsolidatedProtestInfo);
	       }

	    }

		/*Protest_Info protest_Info = protest_Info_DAO.getProtestByA_no(a_No);*/
		  if ("Agency Name".equalsIgnoreCase(typeOfChange)) {
			int agency_Info_Id = protest_Info_DAO.getAgency_Info_Id(
					agencyTier_1_Id, agencyTier_2_Id);

			updateAgencyInfo(listOfConsolidatedCases,agency_Info_Id,request);
			changeAssociatedFile_Info_List(a_No, newValue, oldValue);
		} else if ("B Number".equalsIgnoreCase(typeOfChange)) {
			protest_Info.setB_No(newValue);
			protest_Info_DAO.updateProtest_Info(protest_Info);

			List<GC_Track_Service_Event> gcTrackEvents = gcTrackServiceDAO.getListOf_gc_track_service_event_byA_no(protest_Info.getA_No());

			if (null != gcTrackEvents && gcTrackEvents.size() > 0 ){
				for (GC_Track_Service_Event gc_Track_Service_Event : gcTrackEvents) {
					gc_Track_Service_Event.setB_No(newValue);
					gcTrackServiceDAO.update(gc_Track_Service_Event);

				}
			}
			/*gcTrackServiceDAO.changeBNumber(newValue, protest_Info.getB_No());*/
		}   else if ("Company Status".equalsIgnoreCase(typeOfChange)) {
			protest_Info.setCompany_Status(newValue);
			protest_Info_DAO.updateProtest_Info(protest_Info);
		}  else if ("joinCases".equalsIgnoreCase(typeOfChange)) {
			List<Protest_Info> child_Protest_Info_List = protest_Info_DAO
					.getProtest_Info_List_BasedOnB_NoList(listOfBNumbers);

			Protest_info_util.joinCases(protest_Info_DAO, user_Info_DAO,
					child_Protest_Info_List, protest_Info,file_Info_DAO);
		} else if ("unJoinCases".equalsIgnoreCase(typeOfChange)) {
			List<Protest_Info> child_Protest_Info_List = protest_Info_DAO
					.getProtest_Info_List_BasedOnB_NoList(listOfBNumbers);
			List<Protest_Info> listOfCaseTobeJoined = new ArrayList<Protest_Info>();

			//need to come back over here and send list Of cases to be joined

			Protest_info_util.unJoinCases(protest_Info_DAO, user_Info_DAO,
					file_Info_DAO, child_Protest_Info_List, protest_Info,listOfCaseTobeJoined, false);

		}


		return protest_Info;
	}

	/**
	 * Updates agencyInfoId for each consolidated cases as well as removes all the agency reps from the old agency
	 * This is done from the case docket sheet header
	 * @param protestInfoList
	 * @param newAgencyInfoId
	 * @throws Exception
	 */
	private void updateAgencyInfo(List <Protest_Info> protestInfoList, int newAgencyInfoId,HttpServletRequest request) throws Exception {
		//List<User_Info> listOfAllOldAgencyUsers = user_Info_DAO.getListOfAgencyUserInfo(dashboardService.getListOfAssociatedAgencyIdsByAgencyInfoId(oldAgencyInfoId, true));
		List<User_Info> listOfAllNewAgencyUsers = user_Info_DAO.getListOfAgencyUserInfo(dashboardService.getListOfAssociatedAgencyIdsByAgencyInfoId(newAgencyInfoId, true));

		//list of all the protests
		if (protestInfoList != null){
			for (Protest_Info eachProtestInfo : protestInfoList){

				//get agency user protest role bridge for each protest
				List<User_Protest_Role_Bridge> agencyUserProtestRoleBridge = user_Info_DAO.getAgencyUserProtestRoleBridge(eachProtestInfo.getA_No());

				if (agencyUserProtestRoleBridge !=  null &&  null != listOfAllNewAgencyUsers && !listOfAllNewAgencyUsers.isEmpty()){

					for (User_Protest_Role_Bridge eachUPRB : agencyUserProtestRoleBridge){
						boolean isExists = false;
						
						for (User_Info eachAgencyUser : listOfAllNewAgencyUsers){
							
							//for each agency user check if user protest role bridge record exists? if yes break the loop
							isExists = eachAgencyUser.getUser_Id().equalsIgnoreCase(eachUPRB.getUser_Id());
							if (isExists){
								break;
							}
							
						}
						
						if (!isExists){
							user_Info_DAO.delete(eachUPRB);
						}
					}

				}

				eachProtestInfo.setAgency_Info_Id(newAgencyInfoId);
				protest_Info_DAO.updateProtest_Info(eachProtestInfo);
				try {
				if (!eachProtestInfo.getCase_Type().equalsIgnoreCase("SUPPLEMENTAL")){
					protestInfoService.sendNotificationToAgencyAdminAndProtester(eachProtestInfo, request, "agencyName");
				}

				}catch (Exception e){
					System.out.println("There was a problem sending email");
				}


			}
		}
		
	
		
		
		
		
/*		List <Protest_Info> protestInfoList = new ArrayList<Protest_Info>();
		protestInfoList.add(protest_Info);
		protestInfoList = protestInfoService.addAlltheAssociatedConsolidatedProtests(protestInfoList);*/


	}




	private String getAttorney_Name(String user_id, HttpServletRequest request) throws Exception {
		User_Info attorney_Info = user_Info_DAO
				.getUser_Info_By_User_Id(user_id);

		if (attorney_Info != null) {
			EpdsSession.setAttribute(request,
					"attorney_Info",attorney_Info);
			return attorney_Info.getLast_Name() + ", "
					+ attorney_Info.getFirst_Name();
		}


		return null;
	}

	private void changeAssociatedFile_Info_List(String a_No, String newValue,
			String oldValue) throws Exception {
		List<File_Info> file_InfoList = file_Info_DAO
				.getFile_Info_List_BasedOn_A_No_And_CompanyName(a_No, oldValue);
		for (File_Info eachFile_Info : file_InfoList) {
			eachFile_Info.setCompany_Name(newValue);
			file_Info_DAO.updateFile_Info(eachFile_Info);
		}
	}

	public List<User_Info> getListOfGAOUserConsistOfAttorneyAndSupervisor() {
		return user_Info_DAO.getListOfGAOUserConsistOfAttorneyAndSupervisor();
	}

	public List<User_Info> getListOfAllGAOUsers() {
		List<User_Info> listOfAllGAOUsers = user_Info_DAO.getListOfAllGAOUsers();

		if (listOfAllGAOUsers != null) {
			for (User_Info eachUserInfo : listOfAllGAOUsers){
				UserRoles role = UserRoles.getByCode(eachUserInfo.getRole_id());
				if (role == GAO_ADMIN){
					eachUserInfo.setRole("ADMIN");
				}else if (role == GAO_SUPERVISOR){
					eachUserInfo.setRole("SUPERVISOR");
				}else if (role == GAO_ATTORNEY){
					eachUserInfo.setRole("ATTORNEY");
				}
			}
		}

		return listOfAllGAOUsers;
	}

	public List<Tier_1_Agency> getTier_1_Agency_List() throws Exception {
		return agency_Info_DAO.getTier_1_Agency_List();
	}


	public String validateForJoining(String b_Num,String parentBNum){
		String response = "";
		try {
			Protest_Info parentBNumProtestInfo = protestInfoService.getProtestInfoByBNum(parentBNum);
			Protest_Info protest_Info_ToBeJoinedWith = protest_Info_DAO.getProtestInfoByBNum(b_Num);

			if (protest_Info_ToBeJoinedWith != null) {
				boolean join = protest_Info_ToBeJoinedWith.getParent_A_No() == null;
		
				if (parentBNumProtestInfo.getA_No().equalsIgnoreCase(protest_Info_ToBeJoinedWith.getA_No())) {
					response = "Same A#";
				} else if (join && parentBNumProtestInfo.getAgency_Info_Id() != protest_Info_ToBeJoinedWith
						.getAgency_Info_Id()) {
					response = "Not Same Agency";
				} else if (join && !parentBNumProtestInfo.getPo().equalsIgnoreCase(protest_Info_ToBeJoinedWith.getPo())) {
					response = "PO not equal";
				} else if (join && parentBNumProtestInfo.getAttorney_Name() != null
						&& protest_Info_ToBeJoinedWith.getAttorney_Name() != null
						&& !parentBNumProtestInfo.getAttorney_Name().equalsIgnoreCase(
								protest_Info_ToBeJoinedWith.getAttorney_Name())) {
					response = "Not Same Attorney";
				} else {
					response = "valid";
				}
			}else{
				response = "B# doesn't exist";
			}
		
		}catch (Exception e){
			logger.error("Exception occured when validating B# for join/unjoin cases", e);
			response = "invalid";
		}


		return response;
	}

	public List<File_Info> getFile_InfoListBasedOnSubmissionDateAndDocumentType(
			String submission_Date, int doc_Type_Id) throws Exception {
		return file_Info_DAO
				.getFile_InfoListBasedOnSubmissionDateAndDocumentType(
						submission_Date, doc_Type_Id);
	}

	public List<User_Info> getProtester_parties_list(
			List<User_Info> user_InfoList, String aNum) {
		List<User_Info> protester_parties_list = new ArrayList<User_Info>();
		List<User_Info> unapprovedSecondaryUserInfoList = new ArrayList<User_Info>();
		for (User_Info eachUser_Info : user_InfoList) {
			if (eachUser_Info.getRole().equals("SECONDARY PROTESTER")
					|| eachUser_Info.getRole().equals("PROTESTER")) {
				protester_parties_list.add(eachUser_Info);
			}
		}
		unapprovedSecondaryUserInfoList = user_Info_DAO.getUnapprovedSecondaryUserInfoList(aNum, "protester");
		if (unapprovedSecondaryUserInfoList !=null && !unapprovedSecondaryUserInfoList.isEmpty() && !protester_parties_list.isEmpty()){
			protester_parties_list.forEach(f -> f.setInvitationStatus("ACCEPTED"));
			unapprovedSecondaryUserInfoList.forEach(f -> {f.setInvitationStatus("INVITED");f.setRole("SECONDARY PROTESTER");});
			protester_parties_list = Stream.concat(protester_parties_list.stream(), unapprovedSecondaryUserInfoList.stream())
					.sorted(Comparator.comparing(User_Info::getInvitationStatus)) 
					.distinct().collect(Collectors.toList());
			
		}
		
		makeFirstElementThePrimaryRepresentative(protester_parties_list,
				"PROTESTER");

		return protester_parties_list;
	}

	private void makeFirstElementThePrimaryRepresentative(
			List<User_Info> userInfoList, String primaryRepRole) {

		if (userInfoList != null && userInfoList.size() > 0) {
			User_Info firstUserInfoInTheList = userInfoList.get(0);

			if (!firstUserInfoInTheList.getRole().equalsIgnoreCase(
					primaryRepRole)) {
				User_Info eachUserInfo;

				for (int i = 1; i < userInfoList.size(); i++) {
					eachUserInfo = userInfoList.get(i);
					if (eachUserInfo.getRole().equalsIgnoreCase(primaryRepRole)) {
						userInfoList.set(0, eachUserInfo);
						userInfoList.set(i, firstUserInfoInTheList);
						break;
					}
				}
			}
		}

	}

	public List<List<User_Info>> getListOfIntervenorList(
			List<User_Info> user_InfoList, String aNum) {
		List<User_Info> intervener_parties_list = new ArrayList<User_Info>();
		for (User_Info eachUser_Info : user_InfoList) {
			if (eachUser_Info.getRole().equals("INTERVENOR")
					|| eachUser_Info.getRole().equalsIgnoreCase(
							"SECONDARY INTERVENOR")) {
				eachUser_Info.setIntervenor_Company_Name(eachUser_Info
						.getIntervenor_Company_Name());
				eachUser_Info.setIntervenor_Company_Address(eachUser_Info
						.getIntervenor_Company_Address());
				eachUser_Info.setIntervenorCompanyDetail("INTERVENOR ("
						+ eachUser_Info.getIntervenor_Company_Name() + ")\n"
						+ eachUser_Info.getIntervenor_Company_Address());
				intervener_parties_list.add(eachUser_Info);
			}
		}

		List<List<User_Info>> listOfIntervenorParty = new ArrayList<List<User_Info>>();
		List<User_Info> intervenorParty;
		List<User_Info> unapprovedSecondaryUserInfoList;
		User_Info userInfo1 = new User_Info();
		User_Info userInfo2 = new User_Info();
		for (int i = 0; i < intervener_parties_list.size(); i++) {
			intervenorParty = new ArrayList<User_Info>();

			userInfo1 = intervener_parties_list.get(i);
			intervenorParty.add(userInfo1);

			unapprovedSecondaryUserInfoList = user_Info_DAO
					.getUnapprovedSecondaryUserInfoListByCompanyName(aNum, "intervenor", userInfo1.getIntervenor_Company_Name());

			for (int j = i + 1; j < intervener_parties_list.size(); j++) {
				userInfo2 = intervener_parties_list.get(j);
				if (userInfo1.getIntervenor_Company_Name() != null
						&& userInfo2.getIntervenor_Company_Name() != null) {
					if (userInfo1.getIntervenor_Company_Name()
							.equalsIgnoreCase(
									userInfo2.getIntervenor_Company_Name())) {
						intervenorParty.add(userInfo2);
						intervener_parties_list.remove(j);
						j--;
					}
				}
			}
			
			
			if (unapprovedSecondaryUserInfoList !=null && !unapprovedSecondaryUserInfoList.isEmpty() && !intervenorParty.isEmpty()){
				
			 final String compAddrs = userInfo1.getIntervenor_Company_Address();
			 final String compName = userInfo1.getIntervenor_Company_Name();
			 final String compDetail = userInfo1.getIntervenorCompanyDetail();
			 final CompanyInfo compInfo = userInfo1.getIntervenorCompanyInfo();
			 
			 
				intervenorParty.forEach(f -> 
				f.setInvitationStatus("ACCEPTED"));
				unapprovedSecondaryUserInfoList.forEach(f -> {
					                       f.setInvitationStatus("INVITED");
				                           f.setRole("SECONDARY INTERVENOR");
				                           f.setIntervenor_Company_Address(compAddrs);
				                           f.setIntervenor_Company_Name(compName);
				                           f.setIntervenorCompanyDetail(compDetail);
				                           f.setIntervenorCompanyInfo(compInfo);
				});
				intervenorParty = Stream.concat(intervenorParty.stream(), unapprovedSecondaryUserInfoList.stream())
						.sorted(Comparator.comparing(User_Info::getInvitationStatus)) 
						.distinct().collect(Collectors.toList());
			}
			
			makeFirstElementThePrimaryRepresentative(intervenorParty,
					"INTERVENOR");

			listOfIntervenorParty.add(intervenorParty);
		}

		return listOfIntervenorParty;
	}


	public List<User_Info> getPrimaryAgencyList(List<User_Info> user_InfoList,Protest_Info protest_Info,
			List<Integer> primaryAgencyInfoIds) {

		List<User_Info> primaryAgencyList = new ArrayList<User_Info>();

		for (User_Info eachUser_Info : user_InfoList) {

			if (eachUser_Info.getRole().equals("AGENCY ATTORNEY")
					&& primaryAgencyInfoIds.contains(eachUser_Info.getFirm_id())) {
				primaryAgencyList.add(eachUser_Info);
			}
		}

		return primaryAgencyList;
	}


	public List<User_Info> getSecondaryAgencyList(List<User_Info> user_InfoList,Protest_Info protest_Info,
			List<Integer> primaryAgencyInfoIds) {

		List<User_Info> secondaryAgencyList = new ArrayList<User_Info>();


		for (User_Info eachUser_Info : user_InfoList) {

			if (eachUser_Info.getRole().equals("AGENCY ATTORNEY")
					&& !primaryAgencyInfoIds.contains(eachUser_Info.getFirm_id())) {
				secondaryAgencyList.add(eachUser_Info);
			}
		}

		return secondaryAgencyList;
	}

	@SuppressWarnings("unchecked")
	public List<CompanyInfo> getOrphanIntervenorCompanyInfoList(
			HttpServletRequest request,
			List<List<User_Info>> listOfIntervenorParty) {
		List<File_Info> fileInfoList = (List<File_Info>) EpdsSession
				.getAttribute(request, "caseDocket_File_Info_List");


		List<CompanyInfo> listOfIntervenorCompanyDetail = getListOfIntervenorCompanyInfo(fileInfoList);
		EpdsSession.setAttribute(request, "listOfIntervenorCompanyDetail",listOfIntervenorCompanyDetail);
		List<CompanyInfo> listOfNonOrphanIntervenorCompanyInfo = getListOfNonOrphanIntervenorCompanyInfo(listOfIntervenorParty);
		List<CompanyInfo> listOfOrphanIntervenorCompanyInfo = getListOfOrphanIntervenorCompanyInfo(
				listOfIntervenorCompanyDetail,
				listOfNonOrphanIntervenorCompanyInfo);

		return listOfOrphanIntervenorCompanyInfo;
	}

	private List<CompanyInfo> getListOfOrphanIntervenorCompanyInfo(
			List<CompanyInfo> listOfIntervenorCompanyInfo,
			List<CompanyInfo> listOfNonOrphanIntervenorCompanyInfo) {
		List<CompanyInfo> listOfOrphanIntervenorCompanyInfo = new ArrayList<CompanyInfo>();

		if (listOfIntervenorCompanyInfo.size() > listOfNonOrphanIntervenorCompanyInfo
				.size()) {
			CompanyInfo eachIntervenorCompanyInfo;

			/*
			 * listOfIntervenorCompanyInfo will be transformed to
			 * listOfOrphanIntervenorCompanyInfo
			 */
			for (int i = 0; i < listOfIntervenorCompanyInfo.size(); i++) {
				eachIntervenorCompanyInfo = listOfIntervenorCompanyInfo.get(i);
				for (CompanyInfo eachNonOrphanIntervenorCompanyInfo : listOfNonOrphanIntervenorCompanyInfo) {
					if (eachNonOrphanIntervenorCompanyInfo.getCompanyDetail()
							.equalsIgnoreCase(
									eachIntervenorCompanyInfo
											.getCompanyDetail())) {
						listOfIntervenorCompanyInfo.remove(i--);
						break;
					}
				}

			}

			listOfOrphanIntervenorCompanyInfo = listOfIntervenorCompanyInfo;
		}

		return listOfOrphanIntervenorCompanyInfo;
	}

	private List<CompanyInfo> getListOfNonOrphanIntervenorCompanyInfo(
			List<List<User_Info>> listOfIntervenorParty) {
		List<CompanyInfo> listOfNonOrphanIntervenorCompanyInfo = new ArrayList<CompanyInfo>();
		CompanyInfo companyInfo;

		for (List<User_Info> eachIntevenorParty : listOfIntervenorParty) {
			companyInfo = new CompanyInfo();
			companyInfo.setCompanyName(eachIntevenorParty.get(0)
					.getIntervenor_Company_Name());
			companyInfo.setCompanyAddress(eachIntevenorParty.get(0)
					.getIntervenor_Company_Address());
			companyInfo.setCompanyDetail(eachIntevenorParty.get(0)
					.getIntervenorCompanyDetail());
			listOfNonOrphanIntervenorCompanyInfo.add(companyInfo);
		}

		return listOfNonOrphanIntervenorCompanyInfo;
	}

	private List<CompanyInfo> getListOfIntervenorCompanyInfo(
			List<File_Info> fileInfoList) {
		List<CompanyInfo> listOfIntervenorCompanyDetail = new ArrayList<CompanyInfo>();
		CompanyInfo companyInfo;
		String intervenorCompanyDetail;

		for (File_Info eachFileInfo : fileInfoList) {
			if (eachFileInfo.getIs_Intervene_Approved() != null
					&& eachFileInfo.getIs_Intervene_Approved()
							.equalsIgnoreCase("Y")) {
				intervenorCompanyDetail = "INTERVENOR ("
						+ eachFileInfo.getCompany_Name() + ")\n"
						+ eachFileInfo.getCompany_Address();

				companyInfo = new CompanyInfo();
				companyInfo.setCompanyName(eachFileInfo.getCompany_Name());
				companyInfo
						.setCompanyAddress(eachFileInfo.getCompany_Address());
				companyInfo.setCompanyDetail(intervenorCompanyDetail);
				companyInfo.setIntervenorFileId(eachFileInfo.getFile_Id());
				listOfIntervenorCompanyDetail.add(companyInfo);
			}
		}

		return listOfIntervenorCompanyDetail;
	}

	public void fillUpIntervenorCompanyInfo(HttpServletRequest request, List<List<User_Info>> listOfIntervenorParty) {

		@SuppressWarnings("unchecked")
		List<CompanyInfo> listOfIntervenorCompanyDetail = (List<CompanyInfo>) EpdsSession.getAttribute(request, "listOfIntervenorCompanyDetail");

		for (int i= 0; i < listOfIntervenorParty.size(); i++) {
			User_Info eachUserInfo;
			

			for (int j = 0; j < listOfIntervenorParty.get(i).size(); j++) {
				eachUserInfo = listOfIntervenorParty.get(i).get(j);
				for (int j2 = 0; j2 < listOfIntervenorCompanyDetail.size(); j2++) {

					if (eachUserInfo.getIntervenor_Company_Address()
							.equalsIgnoreCase(listOfIntervenorCompanyDetail.get(j2).getCompanyAddress())
							&& eachUserInfo.getIntervenor_Company_Name().equalsIgnoreCase(listOfIntervenorCompanyDetail.get(j2).getCompanyName())){
						listOfIntervenorParty.get(i).get(j).setIntervenorCompanyInfo(listOfIntervenorCompanyDetail.get(j2));
					}
					
					
					
				}


			}
			
		}
		
	}

	private User_Info getAttorneyUserInfo(String a_No) {
		User_Info userInfo = user_Info_DAO.getAttorneyInfo(a_No);
		return userInfo;
	}

	public String getDocTypeName(int doc_Type_Id) throws Exception {
		Map<Integer, Doc_Info> docId_To_Doc_Info_Map = dashboardService
				.getDocId_To_Doc_Info_Map();
		return docId_To_Doc_Info_Map.get(doc_Type_Id).getDoc_Type_Desc();
	}

	public TemplateDataDTO getTemplateDataDTO(HttpServletRequest request,
			String docId,String aNum) {
		
		
		User_Info userInfo = (User_Info) EpdsSession.getAttribute(request,"user_Info");

		Protest_Info protest_Info = null;
		GAO_User gao_User = null;
		TemplateDataDTO templateDataDTO = new TemplateDataDTO();
		List<Protest_Info> listOfConsolidatedProtestInfo;
		String consolidatedBNums = null;
		String consolidatedProtesterNames = null;
		try {
			

			if ( aNum != null ){
				//amer come back to this the consolidated protestInfo is calculated twice because in this method supp and consolidated protests are separated
				//in the ui it is replacing the submit new doc protestInfo need to fix this
				
				protest_Info = dashboardService.getProtestInfoBasedOnUserIdAndANo(userInfo, aNum);
				//protest_Info.setAgency_Name(agency_Info_DAO.getAgencyName(protest_Info.getAgency_Info_Id()));
				listOfConsolidatedProtestInfo = protest_Info_DAO
						.getConsolidatedProtestInfoListWithParentAndChildSupplementalProtests(protest_Info.getA_No());

				
				listOfConsolidatedProtestInfo.sort(Comparator.comparing(Protest_Info::getB_No, Comparator.nullsLast(Comparator.naturalOrder())));
				

				for (Protest_Info eachConsolidatedProtestInfo : listOfConsolidatedProtestInfo){


					if (eachConsolidatedProtestInfo.getB_No() != null
							&& !eachConsolidatedProtestInfo.getB_No().equalsIgnoreCase("")){
						if (consolidatedBNums != null){
							consolidatedBNums += ", " + eachConsolidatedProtestInfo.getB_No();
						}else{
							consolidatedBNums = eachConsolidatedProtestInfo.getB_No();
						}

					}

					if (eachConsolidatedProtestInfo.getCompany_Name() != null
							&& !eachConsolidatedProtestInfo.getCompany_Name().equalsIgnoreCase("")){

						if (consolidatedProtesterNames != null){

							if (!consolidatedProtesterNames.contains(eachConsolidatedProtestInfo.getCompany_Name())){
								consolidatedProtesterNames += ", " + eachConsolidatedProtestInfo.getCompany_Name();
							}

						}else{
							consolidatedProtesterNames = eachConsolidatedProtestInfo.getCompany_Name();
						}

					}


				}

				templateDataDTO.setConsolidateBNums(consolidatedBNums);
				templateDataDTO.setConsolidatedProtesterNames(consolidatedProtesterNames);



				User_Info attorneyUserInfo = getAttorneyUserInfo(protest_Info.getA_No());


				if (attorneyUserInfo != null){
					gao_User = user_Info_DAO.getGAO_User_BasedOnEPDSUserId(attorneyUserInfo.getUser_Id());
					attorneyUserInfo.setTitle(gao_User.getTitle());
					templateDataDTO.setAttorneyInfo(attorneyUserInfo);
				}

				templateDataDTO.setProtestInfo(protest_Info);

			}


			Map<String, String> mapOfFileMeta = TemplateUtils
					.getMapOfFileMetaData(docId);

			String filePath = request.getServletContext().getRealPath(
					mapOfFileMeta.get("html"));

			InetAddress IP = InetAddress.getLocalHost();

			if (IP.toString().contains("159.142.165.49")) {
				filePath = TemplateUtils.getTemplateFilePathInProd(mapOfFileMeta.get("html"));
			}
			Whitelist whiteList = Whitelist.relaxed().addTags("span", "p", "br")
					.addAttributes(":all", "style").preserveRelativeLinks(true);


			templateDataDTO.setFilePath(filePath);
			templateDataDTO.setWhiteList(whiteList);

			/*templateDataDTO.setPrimaryProtesterRepInfo(primaryProtesterRepInfo);*/
			/*User_Info primaryProtesterRepInfo = user_Info_DAO
			.getUserInfoForACaseByRoleId(protest_Info.getA_No(), 1);*/
			/*User_Info agencyRepInfo = user_Info_DAO.getUserInfoForACaseByRoleId(
			protest_Info.getA_No(), 6);*/

			/*templateDataDTO.setAgencyRepInfo(agencyRepInfo);*/

		} catch (Exception e) {
			System.out.println("No Protest_Info for you yet!!");
		}


		return templateDataDTO;
	}



	public Boolean checkIfDmNumberExists(Integer dmNumber) {
		Protest_Dm_Info dmInfo;
		Boolean flag = false;
		try {
			dmInfo = protest_Info_DAO.getDmInfoById(dmNumber);

			if (dmInfo != null){

				flag = true;
			}


		} catch (Exception e) {
			e.printStackTrace();
		}


		return flag;
	}
	public Integer verifyDMInfo(Protest_Info protestInfo, Integer dmNumber,User_Info user_Info) {
		Protest_Dm_Info dmInfo;
		Boolean flag = false;
		Integer dbDMNumber = 0;
		try {
			dmInfo = protest_Info_DAO.getDmInfo(protestInfo.getA_No());

			if (dmInfo != null){

				dbDMNumber = dmInfo.getGc_Track_Dm_No();
				flag = dbDMNumber.equals(dmNumber);

				if (flag){
					dmInfo.setVerified_By(user_Info.getUser_Id());
					dmInfo.setDate_verified(new Date());

					protest_Info_DAO.updateDmInfo(dmInfo);

					List<Protest_Info> listOfConsolidatedCases = protest_Info_DAO.getConsolidatedProtestInfoListWithParentAndChildSupplementalProtests(protestInfo.getA_No());
					for (Protest_Info eachConsolidatedProtestInfo : listOfConsolidatedCases) {
						eachConsolidatedProtestInfo.setCase_Status("COMPLETE");
						protest_Info_DAO.updateProtest_Info(eachConsolidatedProtestInfo);
					}

//					protestInfo.setCase_Status("COMPLETE");
//					protest_Info_DAO.updateProtest_Info(protestInfo);


				}
			}


		} catch (Exception e) {
			e.printStackTrace();
		}

		return dbDMNumber;
	}

	@Transactional
	public void assignDmInfo(Protest_Info protestInfo, Integer dmNumber,User_Info user_Info) {
		Protest_Dm_Info dmInfo;

		try {
			dmInfo = protest_Info_DAO.getDmInfo(protestInfo.getA_No());

			if (dmInfo != null){
				dmInfo.setA_No(protestInfo.getA_No());
				dmInfo.setGc_Track_Dm_No(dmNumber);
				dmInfo.setDm_no_entered_By(user_Info.getUser_Id());
				protest_Info_DAO.updateDmInfo(dmInfo);
			}else {

				dmInfo = new Protest_Dm_Info();
				dmInfo.setA_No(protestInfo.getA_No());
				dmInfo.setGc_Track_Dm_No(dmNumber);
				dmInfo.setCompleted_By(user_Info.getUser_Id());
				dmInfo.setDm_no_entered_By(user_Info.getUser_Id());
				protest_Info_DAO.addDmInfo(dmInfo);
			}


		} catch (Exception e) {
			e.printStackTrace();
		}


	}
	@Transactional
	public void addDmInfo(Protest_Info protestInfo,User_Info user_Info) {
		Protest_Dm_Info dmInfo;

		try {
			dmInfo = new Protest_Dm_Info();
			dmInfo.setA_No(protestInfo.getA_No());
			dmInfo.setCompleted_By(user_Info.getUser_Id());
			protest_Info_DAO.addDmInfo(dmInfo);


		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	public List<File_Info> getFile_InfoListBasedOnSubmissionDateAndDocumentTypeAndANum(String submissionDate,
			Integer doc_Type_Id, String protestId) throws Exception {
		// TODO Auto-generated method stub
		return file_Info_DAO.getFile_InfoListBasedOnSubmissionDateAndDocumentTypeAndANum(submissionDate,doc_Type_Id,protestId);
	}

	



}
