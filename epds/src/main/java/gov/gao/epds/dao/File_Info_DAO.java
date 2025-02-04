package gov.gao.epds.dao;

import gov.gao.epds.dto.SubmitNewDocDTO;
import gov.gao.epds.dto.UploadedFileIdentifier;
import gov.gao.epds.persistence.DataAccess;
import gov.gao.epds.persistence.entity.Doc_Info;
import gov.gao.epds.persistence.entity.File_Alert;
import gov.gao.epds.persistence.entity.File_Info;
import gov.gao.epds.persistence.entity.Protest_File_Bridge;
import gov.gao.epds.utils.Date_Util;
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
public class File_Info_DAO {

	@Autowired
	private DataAccess access;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Transactional
	public List<File_Info> getFile_InfoListBasedOnSubmissionDateAndDocumentType(
			String submissionDate, int doc_Type_Id) throws Exception {
		String query = "from File_Info where submission_Date = :sub_Date and doc_Type_Id = :doc_Id";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("sub_Date", submissionDate);
		paramMap.put("doc_Id", doc_Type_Id);

		List resultList = access.queryWithParams(query, paramMap);

		return (List<File_Info>) resultList;
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Transactional
	public List<File_Info> getFile_InfoListBasedOnSubmissionDateAndDocumentTypeAndANum(
			String submissionDate, int doc_Type_Id,String aNum) throws Exception {
		String query = "from File_Info where originalSubmissionDate = :sub_Date and doc_Type_Id = :doc_Id and a_No = :a_No";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("sub_Date", submissionDate);
		paramMap.put("doc_Id", doc_Type_Id);
		paramMap.put("a_No", aNum);
		

		List resultList = access.queryWithParams(query, paramMap);

		return (List<File_Info>) resultList;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<File_Info> getFileEntitybyProtestId(String a_No)
			throws Exception {
		String query = "from File_Info where a_No =:a_No order by file_Id, doc_Type_Id";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a_No", a_No);

		@SuppressWarnings("rawtypes")
		List resultList = access.queryWithParams(query, map);

		return (List<File_Info>) resultList;
	}

	@Transactional
	public String getFileAlert(int file_Id, String user_Id) {
		String query = "from File_Alert where file_Id = :file_Id and user_Id =:user_Id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("file_Id", file_Id);
		map.put("user_Id", user_Id);

		List<?> list;
		try {
			list = access.queryWithParams(query, map);
		} catch (Exception e) {
			e.printStackTrace();
			return "N";
		}

		if (!list.isEmpty()) {
			if (((File_Alert) list.get(0)).getViewed().equals("N")) {
				return "Y";
			} else {
				return "N";
			}
		}

		return "N";
	}

	@Transactional
	public List<Integer> saveFilePathToDB(List<UploadedFileIdentifier> filePathList,
			SubmitNewDocDTO submitNewDocDTO,
			List<String> toBeAlerted_User_IdList) throws Exception {
		List<Integer> file_Info_IdList = new ArrayList<Integer>();

		String submitter_Role = submitNewDocDTO.getUser_Role();

		if (submitter_Role == null) {
			if (submitNewDocDTO.getTypeofdocument().equalsIgnoreCase(
					"Request to Intervene")) {
				submitter_Role = "INTERVENOR";
			} else {
				submitter_Role = "PROTESTER";
			}

		} else if (submitter_Role.equalsIgnoreCase("GAO ADMIN")
				|| submitter_Role.equalsIgnoreCase("GAO SUPERVISOR")
				|| submitter_Role.equalsIgnoreCase("GAO")
				|| submitter_Role.equalsIgnoreCase("GAO ATTORNEY")) {
			submitter_Role = "CBCA";
		} else if (submitter_Role.equalsIgnoreCase("AGENCY ATTORNEY")
				|| submitter_Role.equalsIgnoreCase("AGENCY ADMIN")) {
			submitter_Role = "AGENCY";
		} else if (submitter_Role.equalsIgnoreCase("SECONDARY PROTESTER")) {
			submitter_Role = "PROTESTER";
		} else if (submitter_Role.equalsIgnoreCase("SECONDARY INTERVENOR")) {
			submitter_Role = "INTERVENOR";
		}

		if ("PROTESTER".equalsIgnoreCase(submitter_Role)){
			submitter_Role = "FILER";
		}
		if ("INTERVENOR".equalsIgnoreCase(submitter_Role)){
			submitter_Role = "GranteeOrThirdParty";
		}

		for (UploadedFileIdentifier eachFileIdentifier : filePathList) {
			File_Info file_Info = new File_Info();
			file_Info.setFiller(submitNewDocDTO.getDocDescFiller());
			file_Info
					.setIs_Confidential(submitNewDocDTO.getIsDocConfidential());

			/*if (file_Info.getPo() == null) {
				submitNewDocDTO.setIsDocConfidential("Y");
			}*/

			file_Info.setPo(submitNewDocDTO.getIsDocConfidential());
			file_Info.setA_No(submitNewDocDTO.getProtestId());
			file_Info.setFile_Path(eachFileIdentifier.getFilePath());
			file_Info.setFile_identifier(eachFileIdentifier.getFileIdentifierCode());
			file_Info.setDoc_Type_Id(submitNewDocDTO.getDocId());
			file_Info.setDocTypeName(submitNewDocDTO.getTypeofdocument());
			file_Info.setSubmission_Date(submitNewDocDTO.getSubmissionDate());
			file_Info.setOriginalSubmissionDate(submitNewDocDTO.getSubmissionDate());
			if (submitNewDocDTO.getTypeofdocument().equalsIgnoreCase(
					"Minute Entry")
					|| submitNewDocDTO.getTypeofdocument().equalsIgnoreCase(
							"Request of Grantee or Third Party Request Approved") || submitNewDocDTO.getAttorney_note() != null ) {
				file_Info.setAttorney_Note(submitNewDocDTO.getAttorney_note()
						+ ":::" + Date_Util.getCurrentDate());
			} else {
				file_Info.setComments(submitNewDocDTO.getComments());
			}
			file_Info.setSubmitter_User_Id(submitNewDocDTO.getUser_Id());
			file_Info.setCompany_Name(submitNewDocDTO.getCompany_Name());
			file_Info.setCompany_Address(Util.getAddress(submitNewDocDTO));
			file_Info.setSubmitter_Role(submitter_Role);
			file_Info.setIs_Intervene_Approved(submitNewDocDTO
					.getIsInterveneApproved());
			file_Info.setCase_access_request_status(submitNewDocDTO
					.getCaseAccessRequestStatus());
			file_Info.setAlready_viewed_by(submitNewDocDTO.getUser_Id());

			file_Info = access.save(file_Info);
			/*updateFile_AlertTables(file_Info, toBeAlerted_User_IdList);*/

			file_Info_IdList.add(file_Info.getFile_Id());
		}

		return file_Info_IdList;
	}

	private void updateFile_AlertTables(File_Info file_Info,
			List<String> user_IdList) throws Exception {
		
		if (user_IdList == null)
			
			return;

		List<File_Alert> fileAlertList = new ArrayList<File_Alert>();
		
		for (String eachUser_Id : user_IdList) {
			
			File_Alert file_Alert = new File_Alert();
			
			file_Alert.setFile_Id(file_Info.getFile_Id());
			file_Alert.setUser_Id(eachUser_Id);
			file_Alert.setViewed("N");
			
			fileAlertList.add(file_Alert);
		}

		access.save(fileAlertList);
	}

	
	@Transactional
	public Map<Integer, Doc_Info> getDocId_To_DocType_Map() throws Exception {
		String query = "from Doc_Info";
		Map<String, Object> map = new HashMap<String, Object>();
		List<?> list = access.queryWithParams(query, map);

		@SuppressWarnings("unchecked")
		List<Doc_Info> doc_Info_List = (List<Doc_Info>) list;

		Map<Integer, Doc_Info> docId_To_Doc_Info_Map = new HashMap<Integer, Doc_Info>();
		for (Doc_Info eachDoc_Info : doc_Info_List) {
			docId_To_Doc_Info_Map.put(eachDoc_Info.getDoc_Type_Id(),
					eachDoc_Info);
		}

		return docId_To_Doc_Info_Map;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Doc_Info> getDoc_InfoList(String role) throws Exception {
		String query = "from Doc_Info where role =:role";

		Map<String, Object> map = new HashMap<String, Object>();
		// TEMPORARY
		if (role.equalsIgnoreCase("GAO ADMIN")) {
			query = "from Doc_Info";
		} else {
			map.put("role", role.toUpperCase(Locale.ENGLISH));
		}

		List<Doc_Info> doc_InfoList = (List<Doc_Info>) access.queryWithParams(
				query, map);

		return doc_InfoList;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public int getDocIdByTypeOfDoc(String typeOfDoc) throws Exception {
		String query = "from Doc_Info where doc_Type_Desc =:docType";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("docType", typeOfDoc);

		List<Doc_Info> resultList = (List<Doc_Info>) access.queryWithParams(
				query, map);
		int docId =  resultList.get(0).getDoc_Type_Id();

		return docId;
	}
	
	
	@SuppressWarnings("unchecked")
	@Transactional
	public String getTypeOfDocByDocTypeId(int docTypeId) throws Exception {
		String query = "from Doc_Info where doc_Type_Id =:docType";
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("docType", docTypeId);

		List<Doc_Info> resultList = (List<Doc_Info>) access.queryWithParams(
				query, map);
		
		String docTypeDesc = resultList.get(0).getDoc_Type_Desc();

		return docTypeDesc;
	}

	@Transactional
	public void indicateAsViewed(List<File_Info> file_InfoList, String user_Id) {
		String query = "from File_Alert where file_Id = :file_Id and user_Id = :user_Id";
		File_Alert file_Alert = new File_Alert();
		for (File_Info file_Info : file_InfoList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("file_Id", file_Info.getFile_Id());
			map.put("user_Id", user_Id);
			try {
				file_Alert = (File_Alert) (access.queryWithParams(query, map))
						.get(0);
				access.delete(file_Alert);
			} catch (Exception e) {
				System.out.println("Problem deleting...");
				System.out.println(e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public File_Info getFile_Info(String file_Id) {
		String query = "from File_Info where file_Id = :file_Id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("file_Id", Integer.valueOf(file_Id));

		return ((List<File_Info>) access.queryWithParams(query, map)).get(0);
	}

	@Transactional
	public void markIntervenorApproved(File_Info file_Info) throws Exception {
		file_Info.setIs_Intervene_Approved("Y");
		access.update(file_Info);
	}

	@Transactional
	public <T> void deleteFile_Info(T t) throws Exception {
		access.delete(t);
	}

	@Transactional
	public void changeProtectiveOrder(String a_No, String po) throws Exception {
		List<File_Info> file_InfoList = getFileEntitybyProtestId(a_No);

		for (File_Info eachFile_Info : file_InfoList) {
			eachFile_Info.setPo(po.toUpperCase(Locale.ENGLISH));
			access.update(eachFile_Info);
		}
	}

	@Transactional
	public void updateFile_Info(File_Info file_Info) throws Exception {
		access.update(file_Info);

	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Doc_Info> getDoc_InfoList() {
		String query = "from Doc_Info";
		Map<String, Object> map = new HashMap<String, Object>();

		List<Doc_Info> doc_InfoList = (List<Doc_Info>) access.queryWithParams(
				query, map);

		return doc_InfoList;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<File_Alert> getFile_AlertList(String file_Id) {
		String query = "from File_Alert where file_Id = :file_Id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("file_Id", Integer.valueOf(file_Id));

		List<?> list = access.queryWithParams(query, map);

		if (list != null) {
			return (List<File_Alert>) list;
		}

		return null;
	}

	@Transactional
	@SuppressWarnings("unchecked")
	public List<File_Info> getFile_Info_List_BasedOn_A_No_And_CompanyName(
			String a_No, String company_Name) {
		String query = "from File_Info where a_No = :a_No and company_Name = :company_Name";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("a_No", a_No);
		paramMap.put("company_Name", company_Name);

		List<?> resultList = access.queryWithParams(query, paramMap);

		return (List<File_Info>) resultList;
	}

	@Transactional
	public void associateFilesWithOtherOtherCases(
			List<Integer> file_Info_IdList, List<String> otherCasesAnumberList) {
		Protest_File_Bridge protest_File_Bridge = null;
		List<Protest_File_Bridge> protestFileBridgeList = new ArrayList<Protest_File_Bridge>();

		for (String eachA_No : otherCasesAnumberList) {
			for (Integer eachFile_Info_Id : file_Info_IdList) {
				protest_File_Bridge = new Protest_File_Bridge();
				protest_File_Bridge.setA_No(eachA_No);
				protest_File_Bridge.setFile_Id(eachFile_Info_Id);

				protestFileBridgeList.add(protest_File_Bridge);
			}
		}

		try {
			if (protestFileBridgeList.size() > 0) {
				access.save(protestFileBridgeList);
			}
		} catch (Exception e) {
			// come back later
			e.printStackTrace();
		}
	}

	@Transactional
	public List<File_Info> getfile_InfoList_FromOtherConsolidatedCases(
			String a_No) {
		String query = "from File_Info a, Protest_File_Bridge b where b.a_No =:a_No and a.file_Id = b.file_Id order by a.file_Id, a.doc_Type_Id";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a_No", a_No);

		@SuppressWarnings("rawtypes")
		List resultList = access.queryWithParams(query, map);

		List<File_Info> file_InfoList = Util.getDesiredClassObjectList(
				resultList, File_Info.class);

		return file_InfoList;
	}

	@Transactional
	public void removeAll_file_info_records(String a_no) throws Exception {
		List<File_Info> listOf_file_info = getListOf_file_info_basedOnA_no(a_no);

		if (listOf_file_info == null)
			return;

		for (File_Info eachFile_Info : listOf_file_info) {
			deleteFile_Info(eachFile_Info);
		}

	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<File_Info> getListOf_file_info_basedOnA_no(String a_no) {
		String query = "from File_Info where a_No = :a_No";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("a_No", a_no);

		List<?> resultList = access.queryWithParams(query, paramMap);

		if (resultList != null && resultList.size() > 0) {
			return (List<File_Info>) resultList;
		}

		return null;
	}

	@Transactional
	public void removeFile_Alert_records_forGivenListOf_file_Info(
			List<File_Info> list_of_file_info) throws Exception {
		List<File_Alert> list_of_file_alert = getList_of_file_alert_forGivenListOfFile_Info(list_of_file_info);

		if (list_of_file_alert == null || list_of_file_alert.size() == 0)
			return;

		for (File_Alert eachFile_Alert : list_of_file_alert) {
			deleteFile_Info(eachFile_Alert);
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<File_Alert> getList_of_file_alert_forGivenListOfFile_Info(
			List<File_Info> list_of_file_info) {
		List<Integer> file_ids = getFile_ids(list_of_file_info);

		String query = "from File_Alert where file_Id in (:file_ids)";

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("file_ids", file_ids);

		List<?> resultList = access.queryWithParams(query, paramMap);

		if (resultList != null && resultList.size() > 0) {
			return (List<File_Alert>) resultList;
		}

		return null;
	}

	private List<Integer> getFile_ids(List<File_Info> list_of_file_info) {
		List<Integer> file_ids = new ArrayList<Integer>();

		if (list_of_file_info != null) {
			for (File_Info eachFile_info : list_of_file_info) {
				file_ids.add(eachFile_info.getFile_Id());
			}
		}

		return file_ids;
	}

	@Transactional
	public void remove_all_protest_file_bridge_records_forGivenA_no(
			String a_number) throws Exception {
		List<Protest_File_Bridge> list_of_protest_file_bridge = get_list_of_protest_file_bridge_forGivenA_no(a_number);

		if (list_of_protest_file_bridge == null
				|| list_of_protest_file_bridge.size() == 0)
			return;

		for (Protest_File_Bridge eachProtest_file_bridge : list_of_protest_file_bridge) {
			deleteFile_Info(eachProtest_file_bridge);
		}

	}

	@Transactional
	@SuppressWarnings("unchecked")
	private List<Protest_File_Bridge> get_list_of_protest_file_bridge_forGivenA_no(
			String a_number) {
		String query = "from Protest_File_Bridge where a_No = :a_No";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("a_No", a_number);

		List<?> resultList = access.queryWithParams(query, paramMap);

		if (resultList != null && resultList.size() > 0) {
			return (List<Protest_File_Bridge>) resultList;
		}

		return null;
	}

	@Transactional
	public void remove_all_protest_file_bridge_records_forGivenListOf_file_Info(
			List<File_Info> list_of_file_info) throws Exception {
		List<Protest_File_Bridge> list_of_protest_file_bridge = getList_of_protest_file_bridge_forGivenListOfFile_Info(list_of_file_info);

		if (list_of_protest_file_bridge == null
				|| list_of_protest_file_bridge.size() == 0)
			return;

		for (Protest_File_Bridge eachProtest_file_bridge : list_of_protest_file_bridge) {
			deleteFile_Info(eachProtest_file_bridge);
		}

	}

	@Transactional
	@SuppressWarnings("unchecked")
	private List<Protest_File_Bridge> getList_of_protest_file_bridge_forGivenListOfFile_Info(
			List<File_Info> list_of_file_info) {
		List<Integer> file_ids = getFile_ids(list_of_file_info);

		String query = "from Protest_File_Bridge where file_Id in (:file_ids)";

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("file_ids", file_ids);

		List<?> resultList = access.queryWithParams(query, paramMap);

		if (resultList != null && resultList.size() > 0) {
			return (List<Protest_File_Bridge>) resultList;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<File_Info> getFileInfoByDocIdAndANo(int docTypeId, String a_No) {
		String query = "from File_Info where doc_Type_Id = :doc_Type_Id and a_No = :a_No";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("a_No", a_No);
		paramMap.put("doc_Type_Id", docTypeId);

		List<?> resultList = access.queryWithParams(query, paramMap);

		if (resultList != null && resultList.size() > 0) {
			return (List<File_Info>) resultList;
		}

		return null;
	}

	@Transactional
	public <T> List<T> save(List<T> list) throws Exception {
		List<T> listOfT = access.save(list);
		return listOfT;
	}

	@Transactional
	public <T> void update(List<T> file_InfoList) throws Exception {
		for (T each : file_InfoList) {
			access.update(each);
		}
	}

	
	@Transactional
	public <T> void update(T fileInfo) throws Exception {
		access.update(fileInfo);
	}
	@SuppressWarnings("unchecked")
	@Transactional
	public List<File_Info> getListOfDocsUserHasSubmittedToMakeRequestToAccess(
			String userId) {
		String query = "from File_Info where submitter_User_Id = :submitterUserId and doc_Type_Id = 79";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("submitterUserId", userId);

		List<?> resultList = access.queryWithParams(query, paramMap);

		if (resultList != null && resultList.size() > 0) {
			return (List<File_Info>) resultList;
		}

		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<File_Info> getListOfFileInfoBySubimtterUserId(
			String userId) {
		String query = "from File_Info where submitter_User_Id = :submitterUserId";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("submitterUserId", userId);

		List<?> resultList = access.queryWithParams(query, paramMap);

		if (resultList != null && resultList.size() > 0) {
			return (List<File_Info>) resultList;
		}

		return null;
	}
}
