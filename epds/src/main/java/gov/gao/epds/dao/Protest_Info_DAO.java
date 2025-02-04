package gov.gao.epds.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import gov.gao.epds.utils.ZipFile_Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.gao.epds.dto.AdvanceSearchDTO;
import gov.gao.epds.dto.DashboardDto;
import gov.gao.epds.persistence.DataAccess;
import gov.gao.epds.persistence.entity.Agency_Info;
import gov.gao.epds.persistence.entity.File_Info;
import gov.gao.epds.persistence.entity.Protest_Dm_Info;
import gov.gao.epds.persistence.entity.Protest_Info;
import gov.gao.epds.persistence.entity.Tier_1_Agency;
import gov.gao.epds.persistence.entity.User_Info;
import gov.gao.epds.persistence.entity.User_Protest_Role_Bridge;
import gov.gao.epds.persistence.entity.User_Role;
import gov.gao.epds.service.DashboardService;
import gov.gao.epds.utils.Util;

@Repository
public class Protest_Info_DAO {

	@Autowired
	private DataAccess access;
	@Autowired
	private File_Info_DAO fileInfoDAO;

	@Autowired
	private User_Info_DAO user_Info_DAO;

	@Autowired
	private DashboardService dashboardService;

	private final static Logger logger = LoggerFactory.getLogger(Protest_Info_DAO.class);

	@Transactional
	public int getSuffixForSecondaryProtestbyAnum(String a_No) {
		/*
		 * String query = "from Protest_Info where a_No like :a_No"; Map<String,
		 * Object> paramMap = new HashMap<String, Object>();
		 * paramMap.put("a_No", a_No + "%");
		 */
		int decimalIncrement = 0;
		int currentIncrement = 0;
		String highestANumSuffix = null;

		DetachedCriteria criteria = DetachedCriteria.forClass(Protest_Info.class);
		criteria.add(Restrictions.like("a_No", a_No, MatchMode.START));

		@SuppressWarnings("unchecked")
		List<Protest_Info> resultList = criteria.getExecutableCriteria(access.getSessionFactory().getCurrentSession())
				.list();

		if (resultList != null && !resultList.isEmpty()) {
			for (Protest_Info result : resultList) {
				currentIncrement = result.getA_No().contains(".") ? Integer.parseInt(result.getA_No().substring(result.getA_No().indexOf(".") + 1)) : 0;
				if (currentIncrement > decimalIncrement) {
					decimalIncrement = currentIncrement;
				}
			}
			decimalIncrement += 1;
		}
		return decimalIncrement;
	}

	@Transactional
	public int getSuffixForSecondaryProtestbyBNum(String b_No) {
		int decimalIncrement = 0;
		int currentIncrement = 0;
		String highestbNumSuffix = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(Protest_Info.class);
		criteria.add(Restrictions.like("b_No", b_No, MatchMode.START));

		@SuppressWarnings("unchecked")
		List<Protest_Info> resultList = criteria.getExecutableCriteria(access.getSessionFactory().getCurrentSession())
				.list();

		if (resultList != null && !resultList.isEmpty()) {
			for (Protest_Info result : resultList) {
				currentIncrement = Integer.parseInt(result.getB_No().substring(result.getB_No().indexOf(".") + 1));
				if (currentIncrement > decimalIncrement) {
					decimalIncrement = currentIncrement;
				}
			}
			decimalIncrement += 1;
		}
		return decimalIncrement;
	}

	@Transactional
	public int getAgency_Info_Id(String agency_tier_1, String agency_tier_2) throws Exception {
		String query = "";
		Map<String, Object> map = new HashMap<String, Object>();
		if (agency_tier_2 != null && !agency_tier_2.equals("null") && !agency_tier_2.equals("0")
				&& !agency_tier_2.equalsIgnoreCase("undefined")) {
			query = "from Agency_Info where tier = 2 and agency_Id = :agency_Id";
			map.put("agency_Id", Integer.valueOf(agency_tier_2));
		} else {
			query = "from Agency_Info where tier = 1 and agency_Id = :agency_Id";
			map.put("agency_Id", Integer.valueOf(agency_tier_1));
		}

		List<?> resultList = access.queryWithParams(query, map);

		int agency_Info_Id = resultList.isEmpty() ? 0 : ((Agency_Info) resultList.get(0)).getAgency_Info_Id();

		return agency_Info_Id;
	}

	// getting protester details using userId
	@Transactional
	public Protest_Info getProtestInfo(int user_profile_id) throws Exception {

		String query = "from Protest_Info where userid = :userid";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userid", user_profile_id);
		List<?> resultList = access.queryWithParams(query, map);
		return resultList.isEmpty() ? null : (Protest_Info) resultList.get(0);
	}

	// getting User Information by using userId
	@Transactional
	public User_Info getUserInfoById(String userId) throws Exception {

		String query = "from User_Info where user_id = :user_id";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user_id", userId);
		List<?> resultList = access.queryWithParams(query, map);
		return resultList.isEmpty() ? null : (User_Info) resultList.get(0);
	}

	private List<User_Info> getProtestor_Info_List(List<?> resultList) {
		List<User_Info> usrInfoList = new ArrayList<User_Info>();
		if (resultList == null)
			return usrInfoList;
		for (Object each : resultList) {
			Object[] each2 = (Object[]) each;
			for (Object each3 : each2) {
				if (each3 instanceof User_Info) {
					usrInfoList.add((User_Info) each3);
				}
			}
		}

		return usrInfoList;
	}

	@Transactional
	public List<File_Info> getFileEntitybyProtestId(String a_No) throws Exception {
		String query = "from File_Info where a_No =:a_No order by file_Id, doc_Type_Id";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a_No", a_No);

		@SuppressWarnings("unchecked")
		List<File_Info> resultList = (List<File_Info>) access.queryWithParams(query, map);
		return resultList;
	}

	// getting all tier1 values
	@Transactional
	public List<Tier_1_Agency> getTier_1_Agency_List() throws Exception {

		// logger.info("====getAgencyTier1===");
		String query = "from Tier_1_Agency";
		// String query =
		// "from Tier_1_Agency as t1 LEFT JOIN FETCH t1.tier_2_Agency_List";

		Map<String, Object> map = new HashMap<String, Object>();

		List resultList = access.queryWithParams(query, map);

		return resultList;
	}

	// getting Tier 1 object using tier1 id
	@Transactional
	public Tier_1_Agency getAgencyTier1byTierId(int Tier_1_Agency) throws Exception {

		String query = "from Tier_1_Agency where Tier_1_Agency_id=:Tier_1_Agency_id ";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("Tier_1_Agency_id", Tier_1_Agency);
		List resultList = access.queryWithParams(query, map);

		return resultList.isEmpty() ? null : (Tier_1_Agency) resultList.get(0);

	}

	// geting protest information by protestid and userid
	@Transactional
	public Protest_Info get_protest_info_by_a_no(String a_No, String user_Role) throws Exception {
		String query = "";
		Map<String, Object> map = new HashMap<String, Object>();
		if (user_Role.equalsIgnoreCase("GAO ADMIN")) {
			query = "from Protest_Info a, User_Protest_Role_Bridge b, User_Role c where a.a_No =:a_No and a.a_No = b.a_No and b.role_Id = 1 and b.role_Id = c.role_Id and b.consolidated_A_No is null";
			map.put("a_No", a_No);
		} else {
			query = "from Protest_Info a, User_Protest_Role_Bridge b, User_Role c where a.a_No =:a_No and a.a_No = b.a_No and b.role_Id = 3 and b.role_Id = c.role_Id";
			map.put("a_No", a_No);
		}

		List<?> resultList = access.queryWithParams(query, map);

		List<Protest_Info> protestInfoList = Util.getDesiredClassObjectList(resultList, Protest_Info.class);
		List<User_Role> user_RoleList = Util.getDesiredClassObjectList(resultList, User_Role.class);
		List<User_Protest_Role_Bridge> user_Protest_Role_BridgeList = Util.getDesiredClassObjectList(resultList,
				User_Protest_Role_Bridge.class);

		protestInfoList = fillupUserSpecificAttributes(protestInfoList, user_RoleList, user_Protest_Role_BridgeList,
				"userIdBased", user_Role);

		return protestInfoList.get(0);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<User_Protest_Role_Bridge> getUser_Protest_Role_BridgeList(String user_Id) throws Exception {

		String query = "from User_Protest_Role_Bridge where user_Id = :userId";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", user_Id);
		List<?> resultList = access.queryWithParams(query, map);
		return resultList.isEmpty() ? null : (List<User_Protest_Role_Bridge>) resultList;
	}

	// // getting List of all protests Protested by userId
	@Transactional
	public List<Protest_Info> getProtestInfoListOld(String user_Id, String protestTableType, String user_Role,
			Integer agencyId) throws Exception {
		String query = "";
		Map<String, Object> map = new HashMap<String, Object>();
		if (protestTableType.equalsIgnoreCase("userIdBased")) {
			query = "from Protest_Info a, User_Protest_Role_Bridge b, User_Role c "
					+ " where b.user_Id = :userId and a.a_No = b.a_No and b.role_Id = c.role_Id";
			map.put("userId", user_Id);
		} else if (protestTableType.equalsIgnoreCase("unassigned")) {
			query = "from Protest_Info a, User_Protest_Role_Bridge b, User_Role c "
					+ " where a.a_No = b.a_No and b.role_Id = c.role_Id and b.role_Id = 1 and a.attorney_Name is null and b.consolidated_A_No is null";
		} else if (protestTableType.equalsIgnoreCase("assigned")) {
			query = "from Protest_Info a, User_Protest_Role_Bridge b, User_Role c "
					+ " where a.a_No = b.a_No and b.role_Id = c.role_Id and b.role_Id = 3 and a.b_No is not null and b.consolidated_A_No is null";
		} else if (protestTableType.equalsIgnoreCase("allAgencyCases")) {
			query = "from Protest_Info a, User_Protest_Role_Bridge b, User_Role c "
					+ " where a.agency_Info_Id = :agencyId and a.a_No = b.a_No and b.role_Id = c.role_Id and b.consolidated_A_No is null";
			map.put("agencyId", agencyId);
		} else {
			return null;
		}

		List<?> resultList = access.queryWithParams(query, map);

		List<Protest_Info> protestInfoList = Util.getDesiredClassObjectList(resultList, Protest_Info.class);
		List<User_Role> user_RoleList = Util.getDesiredClassObjectList(resultList, User_Role.class);
		List<User_Protest_Role_Bridge> user_Protest_Role_BridgeList = Util.getDesiredClassObjectList(resultList,
				User_Protest_Role_Bridge.class);

		if (protestTableType.equalsIgnoreCase("userIdBased")) {
			removeDups(protestInfoList, user_Protest_Role_BridgeList, user_RoleList);
		}

		protestInfoList = fillupUserSpecificAttributes(protestInfoList, user_RoleList, user_Protest_Role_BridgeList,
				protestTableType, user_Role);

		return protestInfoList.isEmpty() ? null : protestInfoList;
	}

	// getting List of all protests Protested by userId
	@SuppressWarnings("unchecked")
	@Transactional
	public List<Protest_Info> getProtestInfoList(String user_Id, String protestTableType, String user_Role,
			Integer agencyId, DashboardDto dashboardDto) throws Exception {
		String query = "";
		String alreadyAvailAnumsQuery = " AND ( a_No NOT IN (:alreadyAvailAnums))";
		String paymentStatus = " and (Transaction_Status = 'PAID' or Transaction_Status is null)";
		String orderByClause = "  and SUBMISSIONDATETIME > (:threeYearsFromNow)  order by SUBMISSIONDATETIME desc ";
		LocalDate threeYearsFromNow = LocalDate.now().minusYears(3);
		LocalDate fourMonthsFromNow = LocalDate.now().minusDays(120);
		Map<String, Object> map = new HashMap<String, Object>();
		
		
		
		if (protestTableType.equalsIgnoreCase("userIdBased")) {
			alreadyAvailAnumsQuery = alreadyAvailAnumsQuery.replaceAll("a_No", "a.a_No");
			//orderByClause = orderByClause.replaceAll("SUBMISSIONDATETIME", "a.SUBMISSIONDATETIME");
			query = "from Protest_Info a, User_Protest_Role_Bridge b, User_Role c where b.user_Id = :userId and a.a_No = b.a_No and b.role_Id = c.role_Id " + paymentStatus;
			
			
		
			if (user_Role.equalsIgnoreCase("AGENCY ADMIN")){
				query += "  and b.role_Id = '6' ";
			}
			map.put("userId", user_Id);
		} else if (protestTableType.equalsIgnoreCase("unassigned")) {
			query = "from Protest_Info where (attorney_Name is null or b_No is null)" + paymentStatus;
		} else if (protestTableType.equalsIgnoreCase("assigned")) {
			query = "from Protest_Info where attorney_Name is not null and b_No is not null" + paymentStatus;
		} else if (protestTableType.equalsIgnoreCase("allAgencyCases")) {
			query = "from Protest_Info where agency_Info_Id IN (:agencyInfoIds)" + paymentStatus;
			map.put("agencyInfoIds", dashboardService.getListOfAssociatedAgencyIdsByAgencyInfoId(agencyId,false));
		} else {
			return null;
		}
		map.put("threeYearsFromNow", threeYearsFromNow.toDate().getTime());
		//map.put("fourMonthsFromNow", fourMonthsFromNow.toDate().getTime());
		if (dashboardDto.getCaseStatusList() != null){
			query += " AND (case_Status IN (:caseStatus))";
			map.put("caseStatus", Arrays.asList(dashboardDto.getCaseStatusList().split(",")));
		}
		
		if (null != dashboardDto.getAttorneyGroupIds() && dashboardDto.getAttorneyGroupIds().size() > 0){
			query += " AND (attorney_Group_Id IN (:attorney_Group_Id))";
			map.put("attorney_Group_Id", dashboardDto.getAttorneyGroupIds());
		}
		
		if (dashboardDto.getAlreadyAvailableANums() != null){
			
			query = getAlreadyAvailableANumQuery(dashboardDto, query, alreadyAvailAnumsQuery, map);
			
		}
		
		
		query += orderByClause;
		List<?> resultList = access.queryWithParamsWithLimit(query, map,dashboardDto.getEndLimit(),dashboardDto.getStartLimit());

		
		List<Protest_Info> protestInfoList = null;
		List<User_Role> user_RoleList = null;
		List<User_Protest_Role_Bridge> user_Protest_Role_BridgeList = null;

		if (protestTableType.equalsIgnoreCase("userIdBased")) {
			protestInfoList = Util.getDesiredClassObjectList(resultList, Protest_Info.class);
			user_RoleList = Util.getDesiredClassObjectList(resultList, User_Role.class);
			user_Protest_Role_BridgeList = Util.getDesiredClassObjectList(resultList, User_Protest_Role_Bridge.class);
			removeDups(protestInfoList, user_Protest_Role_BridgeList, user_RoleList);
		} else {
			protestInfoList = (List<Protest_Info>) resultList;
		}

		
		if (protestTableType.equalsIgnoreCase("allAgencyCases")) {
			List<String> availANums = new ArrayList<String>();

			if (null != dashboardDto.getAlreadyAvailableANums() && dashboardDto.getAlreadyAvailableANums().size() > 0) {
				availANums = dashboardDto.getAlreadyAvailableANums();
			}

			if (protestInfoList != null && protestInfoList.size() > 0) {
				availANums.addAll(protestInfoList.stream().map(Protest_Info::getA_No).collect(Collectors.toList()));
				dashboardDto.setAlreadyAvailableANums(availANums);

			}

			List<Protest_Info> secondaryAgencyProtestInfoList = getProtestInfoList(user_Id, "userIdBased", user_Role,
					agencyId, dashboardDto);
			if (secondaryAgencyProtestInfoList != null) {
				protestInfoList.addAll(secondaryAgencyProtestInfoList);
			}
		}
		
		
		protestInfoList = fillupUserSpecificAttributes(protestInfoList, user_RoleList, user_Protest_Role_BridgeList,
				protestTableType, user_Role);

		if (user_Role.equalsIgnoreCase("PROTESTER") || user_Role.equalsIgnoreCase("AGENCY ATTORNEY")) {
			List<Protest_Info> pendingOrDeniedRequestToInterveneProtestInfoList = getPendingOrDeniedRequestToInterveneProtestInfoList(
					user_Id, user_Role,dashboardDto);

			protestInfoList.addAll(pendingOrDeniedRequestToInterveneProtestInfoList);
		}

		/*if (protestTableType.equalsIgnoreCase("userIdBased")){
			protestInfoList = getConsolidatedCasesForAllProtests(protestInfoList);
		}*/
		
		protestInfoList = getConsolidatedCasesForAllProtests(protestInfoList);
		
		
		
		return protestInfoList.isEmpty() ? null : protestInfoList;
	}

	
	
	/**
	 * @param dashboardDto
	 * @param query
	 * @param alreadyAvailAnumsQuery
	 * @param map
	 * @return
	 */
	public String getAlreadyAvailableANumQuery(DashboardDto dashboardDto, String query, String alreadyAvailAnumsQuery,
			Map<String, Object> map) {
		int totalLists;
		int sizeOfList = dashboardDto.getAlreadyAvailableANums().size();
		if (sizeOfList >300){
			totalLists = dashboardDto.getAlreadyAvailableANums().size() /100;
			List<List<String>> alreadyAvailableAnums = Util.partitionList(dashboardDto.getAlreadyAvailableANums(),totalLists + 1);
			int count = 0;
			for (List<String> eachList : alreadyAvailableAnums){
				query += "  " +  alreadyAvailAnumsQuery.replace("(:alreadyAvailAnums)", "(:alreadyAvailAnums" + count + ")");
				map.put("alreadyAvailAnums" + count, eachList);
				count++;
			}
		}else{
			query +=  alreadyAvailAnumsQuery;
			map.put("alreadyAvailAnums", dashboardDto.getAlreadyAvailableANums());
		}
		
		
		
		return query;
	}

	/**
	 * Get the protestInfoList based on userId and get all the consolidated
	 * cases for each protest then check if the parent protest caseAccess
	 * request is pending or denied then apply the same to all the children
	 * protests.
	 *
	 * This step is added to make sure 1) All the consolidated protests are
	 * displayed. 1.a Add Pending Or Denied status for Request to intervene and
	 * Notice Of Appearance to all consolidated cases.
	 *
	 *
	 * @param protestInfoList
	 * @return filteredProtestInfoList which has all the unique protestInfo
	 *         values are there
	 */
	public List<Protest_Info> getConsolidatedCasesForAllProtests(List<Protest_Info> protestInfoList) {

		List<Protest_Info> finalConsolidatedProtestInfoList = new ArrayList<Protest_Info>();

		Protest_Info eachProtestInfo = null;

		for (int i = 0; i < protestInfoList.size(); i++) {
			eachProtestInfo = protestInfoList.get(i);
			List<Protest_Info> eachConsolidateProtestInfoList = getConsolidatedProtestInfoListWithParentAndChildSupplementalProtests(
					eachProtestInfo.getA_No());
			eachConsolidateProtestInfoList.remove(0);

			for (int j = 0; j < eachConsolidateProtestInfoList.size(); j++) {
				// if the parent protest has pending or denied status then set
				// the same status for all its children
				if (eachProtestInfo.getCaseAccessRequestStatus() != null
						&& (eachProtestInfo.getCaseAccessRequestStatus().equalsIgnoreCase("P")
								|| eachProtestInfo.getCaseAccessRequestStatus().equalsIgnoreCase("D"))) {
					eachConsolidateProtestInfoList.get(j)
							.setCaseAccessRequestType(eachProtestInfo.getCaseAccessRequestType());
					eachConsolidateProtestInfoList.get(j)
							.setCaseAccessRequestStatus(eachProtestInfo.getCaseAccessRequestStatus());
					eachConsolidateProtestInfoList.get(j)
							.setDeniedIndicatingDocTypeId(eachProtestInfo.getDeniedIndicatingDocTypeId());
					eachConsolidateProtestInfoList.get(j).setDeniedDate(eachProtestInfo.getDeniedDate());
				}
			}

			finalConsolidatedProtestInfoList.addAll(eachConsolidateProtestInfoList);
		}
		protestInfoList.addAll(finalConsolidatedProtestInfoList);

		HashSet<Object> dupe = new HashSet<>();
		protestInfoList.removeIf(protestInfo->!dupe.add(protestInfo.getA_No()));

		return protestInfoList;

	}

	@Transactional
	public List<Protest_Info> getPendingOrDeniedRequestToInterveneProtestInfoList(String user_Id, String user_Role, DashboardDto dashboardDto) {
		String query = "from File_Info a, Protest_Info b where a.submitter_User_Id = :submitterUserId and (a.case_access_request_status = 'P' or a.case_access_request_status = 'D') and a.a_No = b.a_No";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("submitterUserId", user_Id);
		String alreadyAvailAnumsQuery = " AND ( a.a_No NOT IN (:alreadyAvailAnums))";
		
		
		if (dashboardDto.getAlreadyAvailableANums() != null){
			query = getAlreadyAvailableANumQuery(dashboardDto, query, alreadyAvailAnumsQuery, map);
		}
		List<Protest_Info> protestInfoList = populateCaseAccessAttrs(user_Id, user_Role, query, map);

		return protestInfoList;
	}

	/**
	 * @param user_Id
	 * @param user_Role
	 * @param query
	 * @param map
	 * @return
	 */
	public List<Protest_Info> populateCaseAccessAttrs(String user_Id, String user_Role, String query,
			Map<String, Object> map) {
		List<?> resultList = access.queryWithParams(query, map);
		List<Protest_Info> protestInfoList = Util.getDesiredClassObjectList(resultList, Protest_Info.class);
		List<File_Info> fileInfoList = Util.getDesiredClassObjectList(resultList, File_Info.class);
		
		File_Info fileInfo = null;
		Protest_Info protestInfo = null;
		int numberOfDaysOld = 0;
		int denialToAccessDocTypeId = 0;
		if (user_Role.equalsIgnoreCase("PROTESTER")) {
			denialToAccessDocTypeId = 160;
		} else if (user_Role.equalsIgnoreCase("AGENCY ATTORNEY")) {
			denialToAccessDocTypeId = 161;
		}
		for (int i = 0; i < fileInfoList.size(); i++) {
			fileInfo = fileInfoList.get(i);
			protestInfo = protestInfoList.get(i);
			

			// this is added to make sure that if request to intervene was
			// submitted and it was denied and later on if the same user is
			// added to the same case as a primary or secondary rep then he
			// should be able to access it.
			List<User_Protest_Role_Bridge> uprb = user_Info_DAO
					.getUser_Protest_Role_Bridge_List_BasedOnUser_IdAndProtestId(user_Id, protestInfo.getA_No());

			if (null != uprb && uprb.size() > 0) {
				continue;
			}

			protestInfo.setCaseAccessRequestStatus(fileInfo.getCase_access_request_status());

			if (fileInfo.getDoc_Type_Id() == 160 || fileInfo.getDoc_Type_Id() == 56) {
				protestInfo.setCaseAccessRequestType("Request to Intervene");
			} else if (fileInfo.getDoc_Type_Id() == 161 || fileInfo.getDoc_Type_Id() == 79) {
				protestInfo.setCaseAccessRequestType("Notice of Appearance");
			}

			if (fileInfo.getCase_access_request_status().equalsIgnoreCase("D")) {
				List<File_Info> declinedRequestToAccessIndicatingFileInfoList = fileInfoDAO
						.getFileInfoByDocIdAndANo(denialToAccessDocTypeId, protestInfo.getA_No());

				if (declinedRequestToAccessIndicatingFileInfoList != null
						&& declinedRequestToAccessIndicatingFileInfoList.size() > 0) {
				    // If multiple denials, return most recent
					// sort first, found case where latest was not last in list
					ZipFile_Util.sortFileInfoListBySubmissionDateAndFileId(declinedRequestToAccessIndicatingFileInfoList);
					File_Info declinedRequestToAccess = declinedRequestToAccessIndicatingFileInfoList.get(declinedRequestToAccessIndicatingFileInfoList.size()-1);

					protestInfo.setDeniedIndicatingDocTypeId(denialToAccessDocTypeId);
					protestInfo.setDeniedDate(declinedRequestToAccess.getOriginalSubmissionDate());

					DateTime submissionDate_dateTime = DateTime.parse(
							declinedRequestToAccess.getOriginalSubmissionDate().trim(),
							DateTimeFormat.forPattern("MMM dd yyyy HH:mm:ss z"));

					DateTime currentTime = new DateTime();

					numberOfDaysOld = Days
							.daysBetween(new LocalDate(submissionDate_dateTime), new LocalDate(currentTime)).getDays();

					/*
					 * numberOfDaysOld = Date_Util.getNumberOfDaysOld(
					 * declinedRequestToAccess.getSubmission_Date()
					 * ,"MMM dd yyyy HH:mm:ss z");
					 */
					protestInfo.setDeniedFileId(declinedRequestToAccess.getFile_Id());

					if (numberOfDaysOld > 15) {
						protestInfoList.remove(i);
						fileInfoList.remove(i);
						i--;
					}
				}
			}
		}
		return protestInfoList;
	}

	
	@Transactional
	public Protest_Info getPendingOrDeniedRequestToInterveneProtestInfo(String user_Id, String user_Role, Protest_Info protest_Info) {
		String query = "from File_Info a, Protest_Info b where a.submitter_User_Id = :submitterUserId and (a.case_access_request_status = 'P' or a.case_access_request_status = 'D') "
				+ "and a.a_No = b.a_No and a.a_No = :aNum";
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("submitterUserId", user_Id);
		map.put("aNum", protest_Info.getA_No());
		
		
		
		List<Protest_Info> protestInfoList = populateCaseAccessAttrs(user_Id, user_Role, query, map);
		
		if (protestInfoList != null && protestInfoList.size() > 0){
			
			protest_Info.setCaseAccessRequestType(protestInfoList.get(0).getCaseAccessRequestType());
			protest_Info.setCaseAccessRequestStatus(protestInfoList.get(0).getCaseAccessRequestStatus());
			protest_Info.setDeniedDate(protestInfoList.get(0).getDeniedDate());
			protest_Info.setDeniedIndicatingDocTypeId(protestInfoList.get(0).getDeniedIndicatingDocTypeId());
			protest_Info.setDeniedFileId(protestInfoList.get(0).getDeniedFileId());
		}

		return protest_Info;
	}
	private void removeDups(List<Protest_Info> protestInfoList,
			List<User_Protest_Role_Bridge> user_Protest_Role_BridgeList, List<User_Role> user_RoleList) {
		User_Protest_Role_Bridge user_Protest_Role_Bridge = null;
		Protest_Info eachProtest_Info = null;
		for (int i = 0; i < protestInfoList.size(); i++) {
			user_Protest_Role_Bridge = user_Protest_Role_BridgeList.get(i);
			if (user_Protest_Role_Bridge.getConsolidated_A_No() != null
					&& !user_Protest_Role_Bridge.getA_No().equals("")) {
				for (int j = 0; j < protestInfoList.size(); j++) {
					eachProtest_Info = protestInfoList.get(j);
					if (user_Protest_Role_Bridge.getA_No().equals(eachProtest_Info.getA_No()) && i != j) {
						protestInfoList.remove(i);
						user_Protest_Role_BridgeList.remove(i);
						user_RoleList.remove(i);
						i--;
						break;
					}
				}
			}
		}

	}

	// fills up user role, isUserAdmittedToPO, isUserConsolidated and
	// companyNameUserIsRepresentingTo
	private List<Protest_Info> fillupUserSpecificAttributes(List<Protest_Info> protestInfoList,
			List<User_Role> user_RoleList, List<User_Protest_Role_Bridge> user_Protest_Role_BridgeList,
			String protestTableType, String user_Role) {
		List<Protest_Info> newProtest_InfoList = new ArrayList<Protest_Info>();
		Protest_Info eachProtestInfo = null;
		User_Protest_Role_Bridge eachUserProtestRoleBridge = null;
		String eachUserRoleDesc = null;
		String emailPreference;

		for (int i = 0; i < protestInfoList.size(); i++) {
			eachProtestInfo = protestInfoList.get(i);

			if (protestTableType.equalsIgnoreCase("userIdBased")) {
				eachUserRoleDesc = user_RoleList.get(i).getRole_Desc();
				eachUserProtestRoleBridge = user_Protest_Role_BridgeList.get(i);

				eachProtestInfo.setRole(eachUserRoleDesc);
				eachProtestInfo.setIsUserAdmittedToPO(eachUserProtestRoleBridge.getPo().trim());
				Util.setIsUserConsolidated(eachProtestInfo, eachUserProtestRoleBridge);

				Util.setCompanyNameUserIsRepresentingTo(eachProtestInfo, eachUserProtestRoleBridge, eachUserRoleDesc);

				emailPreference = eachUserProtestRoleBridge.getCasedocket_email_preferences();
				if (emailPreference == null || emailPreference.equalsIgnoreCase("")) {
					emailPreference = "Y";
				}

				eachProtestInfo.setCasedocket_email_preferences(emailPreference);
			} else {
				eachProtestInfo.setRole(user_Role);
				eachProtestInfo.setIsUserAdmittedToPO("N");
			}
			/*
			 * List<Protest_Info> listOfConsolidatedProtests =
			 * getProtestInfoList(eachProtestInfo.getA_No());
			 * listOfConsolidatedProtests.remove(0);
			 * eachProtestInfo.setChildren_Protest_InfoList(
			 * listOfConsolidatedProtests);
			 */
			newProtest_InfoList.add(eachProtestInfo);
		}

		return newProtest_InfoList;
	}

	// getting all protests with same user Id
	@Transactional
	public List<Protest_Info> getProtestInfoListByUserId(String user_Id) throws Exception {

		String query = "from Protest_Info a, User_Protest_Role_Bridge b "
				+ " where b.user_Id = :userId and a.a_No = b.a_No  ORDER BY a.submission_Date ASC ";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", user_Id);
		// map.put("submission_Date", );
		List<?> resultList = access.queryWithParams(query, map);

		List<Protest_Info> projectInfoList = Util.getDesiredClassObjectList(resultList, Protest_Info.class);
		return projectInfoList.isEmpty() ? null : projectInfoList;
	}

	@Transactional
	public boolean checkIfA_NoBelongsToSameUser(String userId, String a_No) throws Exception {

		String query = "from User_Protest_Role_Bridge " + " where A_NO =:a_No and USER_ID =:userId and ROLE_ID = 1";

		Map<String, Object> map = new HashMap<String, Object>();

		map.put("a_No", a_No);
		map.put("userId", userId);
		List<?> resultList = access.queryWithParams(query, map);

		return resultList.size() >= 1 ? true : false;
	}

	@Transactional
	public boolean checkIfRequestIsAcceptedForThisUser(String userId, String a_No) throws Exception {

		String query = "from User_Protest_Role_Bridge " + " where A_NO =:a_No and USER_ID =:userId and ROLE_ID = 2";

		Map<String, Object> map = new HashMap<String, Object>();

		map.put("a_No", a_No);
		map.put("userId", userId);
		List<?> resultList = access.queryWithParams(query, map);

		return resultList.size() >= 1 ? true : false;

	}

	@Transactional
	public boolean checkIfRequestIsAlreadySentByThisUser(String userId, String a_No) throws Exception {

		String query = "from File_Info " + " where a_No =:a_No and submitter_User_Id =:userId";

		Map<String, Object> map = new HashMap<String, Object>();

		map.put("a_No", a_No);
		map.put("userId", userId);
		List<?> resultList = access.queryWithParams(query, map);
		if (resultList.size() > 0) {
			@SuppressWarnings("unchecked")
			List<File_Info> file_InfoList = (List<File_Info>) resultList;
			File_Info file_Info = file_InfoList.get(0);
			String is_Intervene_Approved = file_Info.getIs_Intervene_Approved();
			if (is_Intervene_Approved != null && is_Intervene_Approved.equals("Y")) {
				return true;
			}
		}

		return false;

	}

	@Transactional
	public String getResponseForRequestToIntervene(String userId, String a_No) throws Exception {
		boolean checkForSameUser = checkIfA_NoBelongsToSameUser(userId, a_No);
		boolean checkIfAccepted = checkIfRequestIsAcceptedForThisUser(userId, a_No);
		boolean checkIfRequestIsSubmitted = checkIfRequestIsAlreadySentByThisUser(userId, a_No);
		if (checkForSameUser) {
			return "same user";
		} else if (checkIfRequestIsSubmitted) {
			return "request submitted";
		} else if (checkIfAccepted) {
			return "request accepted";
		}

		return "new request";
	}

	// getting Protest by passing B number
	@Transactional
	public Protest_Info getProtestInfoByBNum(String b_No) throws Exception {

		String query = "from Protest_Info where B_NO = :b_id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("b_id", b_No);
		List<?> resultList = access.queryWithParams(query, map);

		return resultList.isEmpty() ? null : (Protest_Info) resultList.get(0);
	}

	// getting Protest Info by passing A number
	@Transactional
	public Protest_Info getProtestByA_no(String a_No) throws Exception {
		String query = "from Protest_Info where a_No = :a_id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a_id", a_No);
		List<?> resultList = access.queryWithParams(query, map);

		return resultList.isEmpty() ? null : (Protest_Info) resultList.get(0);
	}

	/**
	 * @param a_No
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<Protest_Info> getListOfSupplementalProtestbyLastParentANum(String a_No) throws Exception {
		String query = "from Protest_Info where LAST_PARENT_A_NO = :a_id order by b_No asc";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a_id", a_No);
		List<?> resultList = access.queryWithParams(query, map);

		return (null == resultList) ? new ArrayList<Protest_Info>() : (List<Protest_Info>) resultList;
	}

	// getting Protest Info by passing A number
	@Transactional
	public Protest_Info getProtestByAnoPrefix(String a_No) throws Exception {
		String query = "from Protest_Info where a_No = :a_id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a_id", a_No);
		List<?> resultList = access.queryWithParams(query, map);

		return resultList.isEmpty() ? null : (Protest_Info) resultList.get(0);
	}

	// get gc track docket management info by A#
	@Transactional
	public Protest_Dm_Info getDmInfo(String a_No) throws Exception {
		String query = "from Protest_Dm_Info where a_No=:a_id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a_id", a_No);
		List<?> resultList = access.queryWithParams(query, map);

		return resultList.isEmpty() ? null : (Protest_Dm_Info) resultList.get(0);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Protest_Dm_Info> getDmInfoByPendingVerificationStatus(String a_No) throws Exception {
		String query = "from Protest_Dm_Info where a_No=:a_id and verified_By is null ";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a_id", a_No);
		List<?> resultList = access.queryWithParams(query, map);

		return (List<Protest_Dm_Info>) (resultList.isEmpty() ? null : resultList);
	}

	@Transactional
	public List<Protest_Info> getListOfReadyToCompleteCases() throws Exception {
		String query = "from Protest_Dm_Info a, Protest_Info b where a.a_No = b.a_No and a.verified_By is null";
		Map<String, Object> map = new HashMap<String, Object>();

		List<?> resultList = access.queryWithParams(query, map);

		List<Protest_Info> protestInfoList = Util.getDesiredClassObjectList(resultList, Protest_Info.class);

		return protestInfoList.isEmpty() ? null : protestInfoList;
	}
	
	/**
	 * @return All unpaid transactions 15 days ago
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<Protest_Info> getListOfAllCasesWithUnpaidTransactionStatus() throws Exception {
		String query = "from Protest_Info where Transaction_Status = 'UNPAID' and SUBMISSIONDATETIME <= (:fifteenDaysAgo)";
		LocalDate fifteenDaysAgo = LocalDate.now().minusDays(15);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("fifteenDaysAgo", fifteenDaysAgo.toDate().getTime());
		
		List<?> resultList = access.queryWithParams(query, map);

		return (null == resultList) ? new ArrayList<Protest_Info>() : (List<Protest_Info>) resultList;
	}
	
	
	
	/**
	 * @return All verified cases which are 45 days older
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<Protest_Dm_Info> getListOfAllVerifiedCasesOlderThan10Days() throws Exception {
		String query = "from Protest_Dm_Info where date_verified <= CONVERT(DATETIME, CONVERT(DATE, getdate()-10)) and dir_Del is null";
		Map<String, Object> map = new HashMap<String, Object>();
		
		List<?> resultList = access.queryWithParams(query, map);

		return (null == resultList) ? new ArrayList<Protest_Dm_Info>() : (List<Protest_Dm_Info>) resultList;
	}

	// get gc track docket management info by A#
	@Transactional
	public Protest_Dm_Info getDmInfoById(Integer dmNumber) throws Exception {
		String query = "from Protest_Dm_Info where GC_TRACK_DM_NO = :id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", dmNumber);
		List<?> resultList = access.queryWithParams(query, map);

		return resultList.isEmpty() ? null : (Protest_Dm_Info) resultList.get(0);
	}

	@Transactional
	public File_Info getFileInfoByFileId(int fileId) throws Exception {

		String query = "from File_Info where file_Id =:fileId";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("fileId", fileId);

		List<?> resultList = access.queryWithParams(query, map);

		return resultList.isEmpty() ? null : (File_Info) resultList.get(0);
	}

	// getting User_Info by user name
	@Transactional
	public User_Info getUserInfoByUsername(String username) throws Exception {
		String query = "from User_Info where userId=:userId";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", username);
		// map.put("protestid", protestId);
		// map.put("submissiondate", "0");
		List<?> resultList = access.queryWithParams(query, map);
		return resultList.isEmpty() ? null : (User_Info) resultList.get(0);
	}

	@Transactional
	public void approveRequestToAccess(File_Info file_Info, boolean isUserIntervenor) {

		List<Protest_Info> listOfConsolidatedCases = getConsolidatedProtestInfoListWithParentAndChildSupplementalProtests(
				file_Info.getA_No());

		for (Protest_Info eachProtestInfo : listOfConsolidatedCases) {

			User_Protest_Role_Bridge uprb = new User_Protest_Role_Bridge();
			uprb.setA_No(eachProtestInfo.getA_No());
			uprb.setPo("N");
			uprb.setUser_Id(file_Info.getSubmitter_User_Id());
			if (isUserIntervenor) {
				uprb.setRole_Id(2);
				uprb.setIntervenor_Company_Name(file_Info.getCompany_Name());
				uprb.setIntervenor_Company_Address(file_Info.getCompany_Address());
			} else {
				uprb.setRole_Id(6);
			}

			access.save(uprb);

		}

		/*
		 * User_Protest_Role_Bridge uprb = new User_Protest_Role_Bridge();
		 * uprb.setA_No(file_Info.getA_No()); uprb.setPo("N");
		 * uprb.setUser_Id(file_Info.getSubmitter_User_Id()); if
		 * (isUserIntervenor) { uprb.setRole_Id(2);
		 * uprb.setIntervenor_Company_Name(file_Info.getCompany_Name());
		 * uprb.setIntervenor_Company_Address(file_Info.getCompany_Address()); }
		 * else { uprb.setRole_Id(6); }
		 *
		 * access.save(uprb);
		 */
	}

	@Transactional
	public List<Protest_Info> getSupplementalProtest_Info_List(String user_Id) {
		String query = "from Protest_Info a, User_Protest_Role_Bridge b, User_Role c "
				+ "where b.user_Id = :userId and a.a_No = b.a_No and b.role_Id = c.role_Id "
				+ " and (b.role_Id = 1 or b.role_Id = 2 or b.role_Id = 4) and case_Type =:case_Type";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", user_Id);
		map.put("case_Type", "Supplemental Protest");

		List<?> resultList = access.queryWithParams(query, map);

		List<Protest_Info> protestInfoList = Util.getDesiredClassObjectList(resultList, Protest_Info.class);
		/*
		 * List<User_Role> user_RoleList = Util.getDesiredClassObjectList(
		 * resultList, User_Role.class);
		 */

		// protestInfoList = fillupWithRole(protestInfoList, user_RoleList);

		return protestInfoList.isEmpty() ? null : protestInfoList;
	}

	@Transactional
	public void changeProtectiveOrder(Protest_Info protest_Info, String po) throws Exception {
		protest_Info.setPo(po.toUpperCase(Locale.ENGLISH));
		access.update(protest_Info);
	}

	/*
	 * @SuppressWarnings("unchecked")
	 *
	 * @Transactional public static List<Protest_Info>
	 * getProtest_InfoListWithUnassignedB_No() { String query =
	 * "from Protest_Info where b_No is null"; DataAccess access = new
	 * DataAccess(); List<Protest_Info> protest_InfoList = (List<Protest_Info>)
	 * access .queryWithParams(query, new HashMap<String, Object>()); return
	 * protest_InfoList; }
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Protest_Info> getProtest_InfoListWithUnassignedB_No() {
		String query = "from Protest_Info where b_No is null or attorney_Name is null";
		List<Protest_Info> protest_InfoList = (List<Protest_Info>) access.queryWithParams(query,
				new HashMap<String, Object>());
		return protest_InfoList;
	}

	@Transactional
	public <T> void save_Entity(T t) {
		access.save(t);
	}

	@Transactional
	public void changeB_Number(String a_No, String new_B_No) throws Exception {
		Protest_Info protest_Info = new Protest_Info();
		protest_Info.setA_No(a_No);
		protest_Info.setB_No(new_B_No);
		access.update(protest_Info);
	}

	@Transactional
	public void updateProtest_Info(Protest_Info protest_Info) throws Exception {
		access.update(protest_Info);
	}

	@Transactional
	public void updateDmInfo(Protest_Dm_Info dmInfo) throws Exception {
		access.update(dmInfo);
	}

	@Transactional
	public void addDmInfo(Protest_Dm_Info dmInfo) throws Exception {
		access.save(dmInfo);
	}

	@Transactional
	public void deleteDmInfo(Protest_Dm_Info dmInfo) throws Exception {
		access.delete(dmInfo);
	}

	@Transactional
	public List<Protest_Info> getProtestInfoBasedForAdvanceSearch(AdvanceSearchDTO advancedSearchDTO) {

		String query = " ";
		Map<String, Object> map = new HashMap<String, Object>();

		if (advancedSearchDTO.getAttorneyId() == null && advancedSearchDTO.getPartyUserIds() == null &&  advancedSearchDTO.getIntervenorCompName() == null) {
			query = "from Protest_Info a LEFT OUTER JOIN Protest_Dm_Info b ON a.a_No = b.a_No where";
		} else if (advancedSearchDTO.getAttorneyId() != null || advancedSearchDTO.getPartyUserIds() != null ||  advancedSearchDTO.getIntervenorCompName() != null) {

			query = "FROM   Protest_Info  a JOIN   User_Protest_Role_Bridge  b  ON a.a_No = b.a_No "
					+ " LEFT  JOIN User_Role c ON c.role_Id = b.role_Id "
					+ " LEFT  JOIN  Protest_Dm_Info d ON a.a_No = d.a_No  WHERE ";
					
		}

		// set true by audit report to filter only primary A#'s (Those without .#'s)
		if (advancedSearchDTO.isOnlyPrimaryANos()) {
			query += " a.a_No not like :isPrimaryANo and ";
			map.put("isPrimaryANo", "%.%");
		}

		if (advancedSearchDTO.getAttorneyId() != null || advancedSearchDTO.getPartyUserIds() != null){
			query += " b.user_Id IN (:userIds) and b.consolidated_A_No is null and ";
			map.put("userIds", Arrays.asList(advancedSearchDTO.getPartyUserIds().split(",")));
		}
		
		
		if (advancedSearchDTO.getIntervenorCompName() != null){
			query += "  (UPPER(b.intervenor_Company_Name) LIKE UPPER(:intervenorCompName))  and ";
			map.put("intervenorCompName", "%" + advancedSearchDTO.getIntervenorCompName() + "%");
			advancedSearchDTO.setAttorneyId("");
			
		}
		

//		map.put("b_id", "%" + advancedSearchDTO.getB_No() + "%");
//
//
//		if (advancedSearchDTO.getB_No() != null && !advancedSearchDTO.getB_No().trim().equalsIgnoreCase("\"")
//				&& !advancedSearchDTO.getB_No().trim().equalsIgnoreCase("B-")) {
//			query += "  (UPPER(a.b_No) LIKE UPPER(:b_id))";
//			map.put("b_id", "%" + advancedSearchDTO.getB_No() + "%");
//
//		} else if (advancedSearchDTO.getB_No() == null || advancedSearchDTO.getB_No().trim().equalsIgnoreCase("B-")) {
//			query += "  ( (UPPER(a.b_No) LIKE UPPER(:b_id) ) OR (a.b_No is null) )";
//			map.put("b_id", "%" + "B-" + "%");
//
//		}

		if (advancedSearchDTO.getA_No() != null && !"\"".equalsIgnoreCase(advancedSearchDTO.getA_No())) {

			query += " (UPPER(a.a_No) LIKE UPPER(:a_id))";
			map.put("a_id", "%" + advancedSearchDTO.getA_No() + "%");

		} else {

			query += " (UPPER(a.a_No) LIKE UPPER(:a_id))";
			map.put("a_id", "%" + "A-" + "%");

		}
		if (advancedSearchDTO.getB_No() != null && advancedSearchDTO.getB_No().length() > 0){
			query += " AND (UPPER(a.b_No) LIKE UPPER(:b_id))";
			map.put("b_id", "%" + advancedSearchDTO.getB_No() + "%");
		}

		if (advancedSearchDTO.getCompany_Name() != null
				&& !"\"".equalsIgnoreCase(advancedSearchDTO.getCompany_Name())) {

			query += " AND (UPPER(a.company_Name) LIKE UPPER(:protesterName))";
			map.put("protesterName", "%" + advancedSearchDTO.getCompany_Name() + "%");
		}

		if (advancedSearchDTO.getSolicitation_No() != null
				&& !"\"".equalsIgnoreCase(advancedSearchDTO.getSolicitation_No())) {

			query += "AND (UPPER(a.solicitation_No) LIKE UPPER(:sol_No))";
			map.put("sol_No", "%" + advancedSearchDTO.getSolicitation_No() + "%");
		}

		if (advancedSearchDTO.getLawGroup() != null) {

			query += "AND (a.attorney_Group_Id = :lawGroup)";
			map.put("lawGroup", Integer.valueOf(advancedSearchDTO.getLawGroup()));
		}

		
		/*
		 * public decision +60 days
		 *
		 * retrieve all the records which are not availble in Protest DM Info
		 * and case status is closed and 60 days has been passed since the
		 * public decision was issued.
		 *
		 */
		if ((advancedSearchDTO.getCase_Status() != null && advancedSearchDTO.getAttorneyId() == null)
				&& "120DAYS".equalsIgnoreCase(advancedSearchDTO.getCase_Status())
				&& !"RTC".equalsIgnoreCase(advancedSearchDTO.getCase_Status())) {

			query += "  AND a.public_decision_date <= CONVERT(DATETIME, CONVERT(DATE, getdate()-120)) ";
			query += "  AND  b.gc_Track_Dm_No IS null";
			query += "  AND UPPER(a.case_Status) LIKE UPPER('CLOSED')";

			/*
			 * ready to complete After public decision is issued and is pending
			 * verification we only want to retrieve records
			 */
		} else if ((advancedSearchDTO.getCase_Status() != null && advancedSearchDTO.getAttorneyId() == null)
				&& !"120DAYS".equalsIgnoreCase(advancedSearchDTO.getCase_Status())
				&& "RTC".equalsIgnoreCase(advancedSearchDTO.getCase_Status())) {
			query += "  AND (UPPER(a.case_Status) LIKE UPPER('CLOSED'))";

			query += "  AND  b.gc_Track_Dm_No IS NOT null  AND b.verified_By is null";

		} else if ((advancedSearchDTO.getCase_Status() != null && advancedSearchDTO.getAttorneyId() != null)
				&& "120DAYS".equalsIgnoreCase(advancedSearchDTO.getCase_Status())
				&& !"RTC".equalsIgnoreCase(advancedSearchDTO.getCase_Status())) {
/* query += "  AND (a.public_decision_date <= CONVERT(DATETIME, CONVERT(DATE, getdate()-60)) "; 20240305 */
			query += "  AND a.public_decision_date <= CONVERT(DATETIME, CONVERT(DATE, getdate()-120)) ";
			query += "  AND  d.gc_Track_Dm_No IS null";
			query += "  AND (UPPER(a.case_Status) LIKE UPPER('CLOSED'))";

			/*
			 * When querying using attorney id the we protest dm info is
			 * referred by d
			 */
		} else if ((advancedSearchDTO.getCase_Status() != null && advancedSearchDTO.getAttorneyId() != null)
				&& !"120DAYS".equalsIgnoreCase(advancedSearchDTO.getCase_Status())
				&& "RTC".equalsIgnoreCase(advancedSearchDTO.getCase_Status())) {
			query += "  AND (UPPER(a.case_Status) LIKE UPPER('CLOSED'))";

			query += "  AND  d.gc_Track_Dm_No IS NOT null  AND d.verified_By is null";

		} else if (advancedSearchDTO.getCase_Status() != null
				&& !"120DAYS".equalsIgnoreCase(advancedSearchDTO.getCase_Status())
				&& !"RTC".equalsIgnoreCase(advancedSearchDTO.getCase_Status())) {

			query += "  AND (UPPER(a.case_Status) LIKE UPPER(:caseStatus))";

			map.put("caseStatus", "%" + advancedSearchDTO.getCase_Status() + "%");
		}

		if (advancedSearchDTO.getCase_Type() != null) {
			query += "AND (UPPER(a.case_Type) LIKE UPPER(:caseType))";
			map.put("caseType", "%" + advancedSearchDTO.getCase_Type() + "%");
		}

		if (advancedSearchDTO.getListOfAgencyInfoIds().size() > 0) {
			query += " AND (a.agency_Info_Id IN (:agencyInfoIds))";
			map.put("agencyInfoIds", advancedSearchDTO.getListOfAgencyInfoIds());
		}

		List<?> resultList = access.queryWithParams(query, map);

		List<Protest_Info> protestInfoList = null;
		List<User_Role> user_RoleList = null;
		List<User_Protest_Role_Bridge> user_Protest_Role_BridgeList = null;

		protestInfoList = Util.getDesiredClassObjectList(resultList, Protest_Info.class);
		if (advancedSearchDTO.getAttorneyId() != null) {

			user_RoleList = Util.getDesiredClassObjectList(resultList, User_Role.class);
			user_Protest_Role_BridgeList = Util.getDesiredClassObjectList(resultList, User_Protest_Role_Bridge.class);
			removeDups(protestInfoList, user_Protest_Role_BridgeList, user_RoleList);

			protestInfoList = fillupUserSpecificAttributes(protestInfoList, user_RoleList, user_Protest_Role_BridgeList,
					"", "GAO");
		}

		if (protestInfoList != null && protestInfoList.size() > 0 && !resultList.isEmpty()) {
			return protestInfoList;
		} else {
			return new ArrayList<Protest_Info>();
		}

	}

	/*
	 * @Transactional public List<Protest_Info>
	 * getProtestInfoByAttorneyUserId(String user_Id, String protestTableType,
	 * String user_Role, AdvancedSearchDTO advancedSearchDTO) throws Exception {
	 * String query = ""; Map<String, Object> map = new HashMap<String,
	 * Object>();
	 *
	 * query =
	 * "FROM   Protest_Info  a JOIN   User_Protest_Role_Bridge  b  ON a.a_No = b.a_No "
	 * + " LEFT  JOIN User_Role c ON c.role_Id = b.role_Id " +
	 * " LEFT  JOIN  Protest_Dm_Info d ON a.a_No = d.a_No  " +
	 * " WHERE  b.user_Id = :userId and b.consolidated_A_No is null ";
	 *
	 * map.put("userId", user_Id); map.put("b_id", "%" +
	 * advancedSearchDTO.getB_No() + "%");
	 *
	 * if ( advancedSearchDTO.getB_No() !=null &&
	 * !advancedSearchDTO.getB_No().trim().equalsIgnoreCase("\"") &&
	 * !advancedSearchDTO.getB_No().trim().equalsIgnoreCase("B-")) { query +=
	 * " and  (UPPER(a.b_No) LIKE UPPER(:b_id))"; map.put("b_id", "%" +
	 * advancedSearchDTO.getB_No() + "%");
	 *
	 * } else if (advancedSearchDTO.getB_No() == null ||
	 * advancedSearchDTO.getB_No().trim().equalsIgnoreCase("B-")) { query +=
	 * " and ( (UPPER(a.b_No) LIKE UPPER(:b_id)))"; map.put("b_id", "%" + "B-" +
	 * "%"); }
	 *
	 *
	 * if (advancedSearchDTO.getA_No() != null &&
	 * !advancedSearchDTO.getA_No().equalsIgnoreCase("\"")) { query +=
	 * " and (upper(a.a_No) LIKE upper(:a_id))"; map.put("a_id", "%" +
	 * advancedSearchDTO.getA_No() + "%"); } else { query +=
	 * " and (upper(a.a_No) LIKE upper(:a_id))"; map.put("a_id", "%" + "A-" +
	 * "%"); }
	 *
	 * if (advancedSearchDTO.getCompany_Name() != null &&
	 * !"\"".equalsIgnoreCase(advancedSearchDTO.getCompany_Name())) {
	 *
	 * query += " AND (UPPER(a.company_Name) LIKE UPPER(:protesterName))";
	 * map.put("protesterName", "%" + advancedSearchDTO.getCompany_Name() +
	 * "%"); }
	 *
	 * if (advancedSearchDTO.getSolicitation_No() != null &&
	 * !"\"".equalsIgnoreCase(advancedSearchDTO.getSolicitation_No())) {
	 *
	 * query += "AND (UPPER(a.solicitation_No) LIKE UPPER(:sol_No))";
	 * map.put("sol_No", "%" + advancedSearchDTO.getSolicitation_No() + "%"); }
	 *
	 * if (advancedSearchDTO.getLawGroup() != null) {
	 *
	 * query += "AND (a.attorney_Group_Id = :lawGroup)";
	 * map.put("lawGroup",Integer.valueOf(advancedSearchDTO.getLawGroup())); }
	 *
	 * public decision +60 days
	 *
	 * retrieve all the records which are not availble in Protest DM Info and
	 * case status is closed and 60 days has been passed since the public
	 * decision was issued.
	 *
	 *
	 * if (advancedSearchDTO.getCase_Status() != null &&
	 * "60DAYS".equalsIgnoreCase(advancedSearchDTO.getCase_Status()) &&
	 * !"RTC".equalsIgnoreCase(advancedSearchDTO.getCase_Status())) {
	 *
	 * query += "  AND (a.public_decision_date <= trunc(getdate() - 60,'DD')) ";
	 * query += "  AND  b.id IS null"; query +=
	 * "  AND (UPPER(a.case_Status) LIKE UPPER('CLOSED'))";
	 *
	 *
	 * ready to complete After public decision is issued and is pending
	 * verification we only want to retrieve records }else if
	 * (advancedSearchDTO.getCase_Status() != null &&
	 * !"60DAYS".equalsIgnoreCase(advancedSearchDTO.getCase_Status()) &&
	 * "RTC".equalsIgnoreCase(advancedSearchDTO.getCase_Status())) { query +=
	 * "  AND (UPPER(a.case_Status) LIKE UPPER('CLOSED'))";
	 *
	 * query += "  AND  b.id IS NOT null  AND b.verified_By is null";
	 *
	 * }else if (advancedSearchDTO.getCase_Status() != null &&
	 * !"60DAYS".equalsIgnoreCase(advancedSearchDTO.getCase_Status()) &&
	 * !"RTC".equalsIgnoreCase(advancedSearchDTO.getCase_Status())) {
	 *
	 * query += "  AND (UPPER(a.case_Status) LIKE UPPER(:caseStatus))";
	 *
	 * map.put("caseStatus", "%" + advancedSearchDTO.getCase_Status() + "%"); }
	 *
	 * if (advancedSearchDTO.getCase_Type() != null) { query +=
	 * "AND (UPPER(a.case_Type) LIKE UPPER(:caseType))"; map.put("caseType", "%"
	 * + advancedSearchDTO.getCase_Type() + "%"); }
	 *
	 *
	 * if (advancedSearchDTO.getListOfAgencyInfoIds().size() > 0) { query +=
	 * " AND (a.agency_Info_Id IN (:agencyInfoIds))"; map.put("agencyInfoIds",
	 * advancedSearchDTO.getListOfAgencyInfoIds()); }
	 *
	 *
	 * List<?> resultList = access.queryWithParams(query, map);
	 *
	 * List<Protest_Info> protestInfoList = Util.getDesiredClassObjectList(
	 * resultList, Protest_Info.class);
	 *
	 * List<User_Role> user_RoleList = Util.getDesiredClassObjectList(
	 * resultList, User_Role.class); List<User_Protest_Role_Bridge>
	 * user_Protest_Role_BridgeList = Util
	 * .getDesiredClassObjectList(resultList, User_Protest_Role_Bridge.class);
	 *
	 * if (protestTableType.equalsIgnoreCase("userIdBased")) { protestInfoList =
	 * Util.getDesiredClassObjectList(resultList, Protest_Info.class);
	 * user_RoleList = Util.getDesiredClassObjectList(resultList,
	 * User_Role.class); user_Protest_Role_BridgeList =
	 * Util.getDesiredClassObjectList( resultList,
	 * User_Protest_Role_Bridge.class); removeDups(protestInfoList,
	 * user_Protest_Role_BridgeList, user_RoleList); }
	 *
	 *
	 * protestInfoList = fillupUserSpecificAttributes(protestInfoList,
	 * user_RoleList, user_Protest_Role_BridgeList, protestTableType,
	 * user_Role);
	 *
	 * return protestInfoList.isEmpty() ? null : protestInfoList; }
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Protest_Info> getProtestInfoListByANumPrefix(String a_No) {

		String query = "from Protest_Info where UPPER(A_NO) LIKE UPPER(:a_id)";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a_id", "%" + a_No + "%");
		List<?> resultList = access.queryWithParams(query, map);

		return (List<Protest_Info>) (resultList.isEmpty() ? null : resultList);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Protest_Info> getProtest_Info_List_BasedOnB_NoList(List<String> b_NoList) {
		List<Protest_Info> final_Protest_Info_List = new ArrayList<Protest_Info>();

		if (b_NoList != null) {
			for (String b_No : b_NoList) {
				String query = "from Protest_Info where b_No=:b_No";
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("b_No", b_No);

				List<?> resultList = access.queryWithParams(query, paramMap);
				List<Protest_Info> protest_Info_List = (List<Protest_Info>) resultList;

				final_Protest_Info_List.addAll(protest_Info_List);
			}
		}

		return final_Protest_Info_List;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Protest_Info> get_list_of_child_protest_info(String parent_a_no) {
		String query = "from Protest_Info where parent_a_no = :parent_a_no";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("parent_a_no", parent_a_no);

		List<?> resultList = access.queryWithParams(query, map);

		if (resultList != null && resultList.size() > 0) {
			return (List<Protest_Info>) resultList;
		}

		return new ArrayList<Protest_Info>();
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Protest_Info> get_list_of_supplementalProtest(String parent_a_no) {

		String query = "from Protest_Info where parent_a_no = :parent_a_no and case_type = 'SUPPLEMENTAL'";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("parent_a_no", parent_a_no);

		List<?> resultList = access.queryWithParams(query, map);

		if (resultList != null && resultList.size() > 0) {
			return (List<Protest_Info>) resultList;
		}

		return new ArrayList<Protest_Info>();
	}

	@Transactional
	public <T> void delete(T t) throws Exception {
		if (t != null) {
			access.delete(t);
		}

	}

	@Transactional
	public void removeAll_user_protest_role_bridge_recordsByA_no(String a_no) throws Exception {
		List<User_Protest_Role_Bridge> listOf_user_protest_role_bridge = getUser_Protest_Role_BridgeList_byA_no(a_no);

		if (listOf_user_protest_role_bridge == null)
			return;

		for (User_Protest_Role_Bridge eachUser_Protest_Role_Bridge : listOf_user_protest_role_bridge) {
			delete(eachUser_Protest_Role_Bridge);
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<User_Protest_Role_Bridge> getUser_Protest_Role_BridgeList_byA_no(String a_no) {
		String query = "from User_Protest_Role_Bridge where a_No = :a_No";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a_No", a_no);
		List<?> resultList = access.queryWithParams(query, map);
		return resultList.isEmpty() ? null : (List<User_Protest_Role_Bridge>) resultList;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<User_Protest_Role_Bridge> getUser_protest_role_bridge_list(String userId, String bNum) {
		String query = "from User_Protest_Role_Bridge a, Protest_Info b where a.user_Id = :user_Id and b.b_No = :b_No and a.a_No = b.a_No";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user_Id", userId);
		map.put("b_No", bNum);

		List<?> resultList = access.queryWithParams(query, map);

		return resultList.isEmpty() ? null : (List<User_Protest_Role_Bridge>) resultList;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Protest_Info> getProtestInfoListByBNumberWildSearchAndFirmId(String bNumber, Integer firmId) throws Exception {
		String query;
		Map<String, Object> map = new HashMap<String, Object>();

		if (bNumber != null  
				&& !bNumber.equalsIgnoreCase("B-")
				&& !bNumber.equalsIgnoreCase("")) {
			query = "from Protest_Info where UPPER(b_No) LIKE UPPER(:b_no) and agency_Info_Id IN (:agencyInfoIds) and case_Status = 'OPEN'";
			map.put("b_no", "%" + bNumber + "%");
		} else {
			query = "from Protest_Info where agency_Info_Id IN (:agencyInfoIds) and case_Status = 'OPEN'";
		}

		map.put("agencyInfoIds", dashboardService.getListOfAssociatedAgencyIdsByAgencyInfoId(firmId,false));
		List<?> resultList = access.queryWithParams(query, map);

		return (resultList == null || resultList.isEmpty()) ? null : (List<Protest_Info>) resultList;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Protest_Info> getProtestInfoListForIntervenorAccessByBNumberWildSearch(String bNumber) {
		String query = "";
		Map<String, Object> map = new HashMap<String, Object>();

		if (bNumber != null) {
			query = "from Protest_Info where UPPER(b_No) LIKE UPPER(:b_no) and case_Status = 'OPEN'";
			map.put("b_no", "%" + bNumber + "%");
		}

		List<?> resultList = access.queryWithParams(query, map);

		return (resultList == null || resultList.isEmpty()) ? null : (List<Protest_Info>) resultList;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Protest_Info> getConsolidatedProtestInfoList(String aNo) {
		List<Protest_Info> protestInfoList = null;

		String query = "from Protest_Info where a_No = :a_No or parent_A_No = :a_No";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a_No", aNo);

		List<?> resultList = access.queryWithParams(query, map);

		if (resultList != null && resultList.size() > 0) {
			protestInfoList = (List<Protest_Info>) resultList;

			reArrangeConsolidatedProtestInfoList(protestInfoList, aNo);

			String parentAno = protestInfoList.get(0).getParent_A_No();

			if (parentAno != null) {
				if (!aNo.equalsIgnoreCase(parentAno) && parentAno != null && !parentAno.equalsIgnoreCase("")) {
					/* protestInfoList = getProtestInfoList(parentAno); */
					protestInfoList.addAll(getConsolidatedProtestInfoList(parentAno));

				}
			}

			// Extra step to make sure there are no dupes
			Set<Protest_Info> protestInfoSet = new HashSet<Protest_Info>(protestInfoList);

			protestInfoList = new ArrayList<Protest_Info>(protestInfoSet);

			reArrangeConsolidatedProtestInfoList(protestInfoList, aNo);

		}

		/*
		 * if (protestInfo.getParent_A_No() != null){
		 *
		 * try { Protest_Info mainProtestInfo =
		 * protest_Info_DAO.getProtestByA_no(listOfConsolidatedCases.get(0).
		 * getParent_A_No()); listOfConsolidatedCases.add(mainProtestInfo); }
		 * catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } }
		 */
		return protestInfoList;
	}

	@Transactional
	public List<Protest_Info> getConsolidatedProtestInfoListWithParentAndChildSupplementalProtests(String aNo) {

		List<Protest_Info> protestInfoList = getConsolidatedProtestInfoList(aNo);
		Set<Protest_Info> protest_InfoSet = new HashSet<Protest_Info>();

		
		
		if (null != protestInfoList && !protestInfoList.isEmpty()) {

			for (Protest_Info eachProtestInfo : protestInfoList) {

				String eachProtestANum = eachProtestInfo.getA_No();

				if (null != eachProtestANum && !aNo.equalsIgnoreCase(eachProtestANum)
						&& !eachProtestANum.equalsIgnoreCase("")
						&& !eachProtestInfo.getCase_Type().equalsIgnoreCase("Supplemental")) {
					/* protestInfoList = getProtestInfoList(parentAno); */
					protest_InfoSet.addAll(getConsolidatedProtestInfoList(eachProtestInfo.getA_No()));

				} else {
					protest_InfoSet.add(eachProtestInfo);
				}

				/*
				 * if (parentAno != null) { if (!aNo.equalsIgnoreCase(parentAno)
				 * && parentAno != null && !parentAno.equalsIgnoreCase("")) {
				 * protestInfoList = getProtestInfoList(parentAno);
				 * protest_InfoSet.addAll(getConsolidatedProtestInfoList(
				 * parentAno));
				 *
				 * } }else{ protest_InfoSet.add(eachProtestInfo); }
				 */

			}

		}

		protestInfoList = new ArrayList<Protest_Info>(protest_InfoSet);

		reArrangeConsolidatedProtestInfoList(protestInfoList, aNo);
		return protestInfoList;
	}

	public void reArrangeConsolidatedProtestInfoList(List<Protest_Info> protestInfoList, String aNo) {

		// put main protestInfo on demand at first index
		Protest_Info protestInfo;
		for (int i = 0; i < protestInfoList.size() && protestInfoList.size() > 1; i++) {
			protestInfo = protestInfoList.get(i);
			if (protestInfo.getA_No().equalsIgnoreCase(aNo)) {
				protestInfoList.remove(i);
				protestInfoList.add(0, protestInfo);
			}
		}

	}

	@SuppressWarnings("unchecked")
	public List<Protest_Info> getListOfProtestInfoByEmail(String oldEmail) {

		String query = "from Protest_Info where UPPER(rep_Email) LIKE UPPER(:email)";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("email", oldEmail);
		List<?> resultList = access.queryWithParams(query, map);

		if (null != resultList && resultList.size() > 0) {
			return ((List<Protest_Info>) resultList);
		} else {
			return new ArrayList<Protest_Info>();
		}
	}

	public List<Protest_Info> getProtestInfoByUniqueCaseStatus() {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(Protest_Info.class);
		criteria.setProjection(Projections.distinct(Projections.property("case_Status")));
		@SuppressWarnings("unchecked")
		List<Protest_Info> resultList = criteria.
										getExecutableCriteria(
												access.getSessionFactory().getCurrentSession())
												.list();
		
		return resultList;
	}

}
