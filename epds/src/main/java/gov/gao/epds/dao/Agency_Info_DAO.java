package gov.gao.epds.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.gao.epds.persistence.DataAccess;
import gov.gao.epds.persistence.entity.Agency_Info;
import gov.gao.epds.persistence.entity.Protest_Info;
import gov.gao.epds.persistence.entity.Tier_1_Agency;
import gov.gao.epds.persistence.entity.Tier_2_Agency;
import gov.gao.epds.utils.Util;

@Repository
@Transactional
public class Agency_Info_DAO {

	@Autowired
	private DataAccess access;

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Tier_1_Agency> getTier_1_Agency_List() throws Exception {

		String query = "from Tier_1_Agency order by agency_Name";

		Map<String, Object> map = new HashMap<String, Object>();

		@SuppressWarnings("rawtypes")
		List resultList = access.queryWithParams(query, map);

		return resultList;
	}

	@Transactional
	public String getAgencyName(int agency_Info_Id) throws Exception {
		Agency_Info agency_Info = getAgency_Info(agency_Info_Id);

		String query;
		Map<String, Object> map;
		List<?> resultList;
		String agency_Name = "";
		map = new HashMap<String, Object>();
		if (agency_Info.getTier().equals("1")) {
			query = "from Tier_1_Agency where agency_Id = :agency_Id";
			map.put("agency_Id", agency_Info.getAgency_Id());
			resultList = access.queryWithParams(query, map);
			agency_Name = ((Tier_1_Agency) resultList.get(0)).getAgency_Name();
		} else {
			query = "from Tier_1_Agency a, Tier_2_Agency b where b.agency_Id = :agency_Id and a.agency_Id = b.tier_1_Agency_Id";
			map.put("agency_Id", agency_Info.getAgency_Id());
			resultList = access.queryWithParams(query, map);
			agency_Name = getAgencyNameForBothTier(resultList);
			// agency_Name = ((Tier_2_Agency)
			// resultList.get(0)).getAgency_Name();
		}

		return agency_Name;
	}

	@Transactional
	public Agency_Info getAgency_Info(int agency_Info_Id) {
		String query = "from Agency_Info where agency_Info_Id = :agency_Info_Id";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("agency_Info_Id", agency_Info_Id);

		List<?> resultList = access.queryWithParams(query, map);
		Agency_Info agency_Info = (Agency_Info) resultList.get(0);

		return agency_Info;
	}
	
	@Transactional
	public void updateAgencyInfo(Agency_Info agency_Info) throws Exception {
		
		access.update(agency_Info);

	}
	
	@Transactional
	public <T> void update(T t) throws Exception {
		
		access.update(t);

	}

	private String getAgencyNameForBothTier(
			@SuppressWarnings("rawtypes") List resultList) {
		String agencyName = "";
		for (Object eachObj : resultList) {
			for (Object eachObj2 : ((Object[]) eachObj)) {
				if (eachObj2 instanceof Tier_1_Agency) {
					agencyName = ((Tier_1_Agency) eachObj2).getAgency_Name()
							+ "/";
				} else {
					agencyName = agencyName
							+ ((Tier_2_Agency) eachObj2).getAgency_Name();
				}
			}

		}
		return agencyName;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Tier_2_Agency> getAgencyTier2(int tier1) throws Exception {
		String query = "from Tier_2_Agency where Tier_1_Agency_id=:Tier_1_Agency_id order by agency_Name";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("Tier_1_Agency_id", tier1);
		@SuppressWarnings("rawtypes")
		List resultList = access.queryWithParams(query, map);

		return resultList;
	}
	
	@Transactional
	public Tier_2_Agency getAgencyTier2ByAgencyId(int agencyId) throws Exception {
		String query = "from Tier_2_Agency where agency_Id=:agencyId";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("agencyId", agencyId);
		@SuppressWarnings("rawtypes")
		List resultList = access.queryWithParams(query, map);

		return (resultList != null ? (Tier_2_Agency) resultList.get(0) : null);
	}
	
	
	@Transactional
	public List<?>  getTier1AndTier2AgencyList(int agencyId) throws Exception {
		
		String query = "from Tier_1_Agency a , Tier_2_Agency b where a.agency_Id = b.tier_1_Agency_Id and b.agency_Id=:agencyId";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("agencyId", agencyId);
		@SuppressWarnings("rawtypes")
		List resultList = access.queryWithParams(query, map);
		
		return resultList;
	}
	
	/**
	 * Returns list of agencyInfo records for both tier 1 and tier 2 based on the tier1AgencyId and the first entry is always tier1 agencyInfo
	 * @param tier1AgencyId -- Tier 1 agencyId
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public List<Agency_Info> getListOfAgencyInfoIdsByTier1AgencyId(int tier1AgencyId) throws Exception {
		String query = "from Agency_Info a, Tier_2_Agency b where a.agency_Id = b.agency_Id "
				+ " and ((a.tier ='2' and b.tier_1_Agency_Id =:tier1AgencyId) "
				+ " or (a.tier ='1' and a.agency_Id =:tier1AgencyId)) order by a.tier";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tier1AgencyId", tier1AgencyId);
		@SuppressWarnings("rawtypes")
		List resultList = access.queryWithParams(query, map);
		
		List<Agency_Info> agencyInfoList = Util.getDesiredClassObjectList(
				resultList, Agency_Info.class);

		return (agencyInfoList != null ? agencyInfoList : null);
	}
	
	@Transactional
	public Tier_1_Agency getAgencyTier1ByAgencyId(int agencyId) throws Exception {
		String query = "from Tier_1_Agency where agency_Id=:agencyId";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("agencyId", agencyId);
		@SuppressWarnings("rawtypes")
		List resultList = access.queryWithParams(query, map);

		return (resultList != null ? (Tier_1_Agency) resultList.get(0) : null);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Map<Integer, Agency_Info> getAgency_InfoMap() {
		String query = "from Agency_Info";
		List<Agency_Info> tier_1_AgencyList = (List<Agency_Info>) access
				.queryWithOnlyQuery(query);

		Map<Integer, Agency_Info> agency_InfoMap = new HashMap<Integer, Agency_Info>();
		for (Agency_Info eachAgency_Info : tier_1_AgencyList) {
			agency_InfoMap.put(eachAgency_Info.getAgency_Info_Id(),
					eachAgency_Info);
		}

		return agency_InfoMap;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Map<Integer, Tier_2_Agency> getTier_2_AgencyMap() {
		String query = "from Tier_2_Agency";
		List<Tier_2_Agency> tier_1_AgencyList = (List<Tier_2_Agency>) access
				.queryWithOnlyQuery(query);

		Map<Integer, Tier_2_Agency> tier_2_AgencyMap = new HashMap<Integer, Tier_2_Agency>();
		for (Tier_2_Agency eachTier_2_Agency : tier_1_AgencyList) {
			tier_2_AgencyMap.put(eachTier_2_Agency.getAgency_Id(),
					eachTier_2_Agency);
		}

		return tier_2_AgencyMap;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Map<Integer, Tier_1_Agency> getTier_1_AgencyMap() {
		String query = "from Tier_1_Agency";
		List<Tier_1_Agency> tier_1_AgencyList = (List<Tier_1_Agency>) access
				.queryWithOnlyQuery(query);

		Map<Integer, Tier_1_Agency> tier_1_AgencyMap = new HashMap<Integer, Tier_1_Agency>();
		for (Tier_1_Agency eachTier_1_Agency : tier_1_AgencyList) {
			tier_1_AgencyMap.put(eachTier_1_Agency.getAgency_Id(),
					eachTier_1_Agency);
		}

		return tier_1_AgencyMap;
	}

}
