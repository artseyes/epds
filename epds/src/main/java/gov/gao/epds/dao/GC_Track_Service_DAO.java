package gov.gao.epds.dao;

import gov.gao.epds.persistence.DataAccess;
import gov.gao.epds.persistence.entity.GC_Track_Service_Event;
import gov.gao.epds.persistence.entity.Protest_Info;
import gov.gao.epds.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class GC_Track_Service_DAO {

	@Autowired
	private DataAccess access;

	@SuppressWarnings("unchecked")
	@Transactional
	public List<GC_Track_Service_Event> get_list_of_GC_Track_webService_event() {
		String query = "from GC_Track_Service_Event where status =:status and event_Type !=:event_type";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", "New");
		map.put("event_type", "New Protest");

		List<?> resultList = access.queryWithParams(query, map);

		if (resultList != null && resultList.size() > 0) {
			return (List<GC_Track_Service_Event>) resultList;
		}

		return null;
	}


	@SuppressWarnings("unchecked")
	@Transactional
	public List<GC_Track_Service_Event> get_list_of_GC_Track_webService_eventByAnum(String aNum) {
		String query = "from GC_Track_Service_Event where status =:status and event_Type !=:event_type and a_No =:aNum";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", "New");
		map.put("event_type", "New Protest");
		map.put("aNum", aNum);

		List<?> resultList = access.queryWithParams(query, map);

		if (resultList != null && resultList.size() > 0) {
			return (List<GC_Track_Service_Event>) resultList;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public GC_Track_Service_Event getGC_Track_Service_Event_based_on_event_id(
			Integer event_id) {
		String query = "from GC_Track_Service_Event where event_Id =:event_Id";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("event_Id", event_id);

		List<?> resultList = access.queryWithParams(query, map);

		if (resultList != null && resultList.size() > 0) {
			return ((List<GC_Track_Service_Event>) resultList).get(0);
		}

		return null;
	}

	@Transactional
	public <T> void delete(T entity) throws Exception {
		access.delete(entity);
	}

	@Transactional
	public <T> void save(T t) {
		access.save(t);
	}
	
	@Transactional
	public <T> void update(T t) throws Exception {
		access.update(t);
	}

	@Transactional
	public List<Protest_Info> get_list_of_new_protest_Info() {
		String query = "from Protest_Info a, GC_Track_Service_Event b where b.status =:status and b.event_Type =:event_type and a.a_No = b.a_No";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", "New");
		map.put("event_type", "New Protest");

		List<?> resultList = access.queryWithParams(query, map);

		if (resultList != null && resultList.size() > 0) {
			List<Protest_Info> list_of_new_protest_info = Util
					.getDesiredClassObjectList(resultList, Protest_Info.class);

			return list_of_new_protest_info;
		}

		return null;
	}


	@Transactional
	public List<Protest_Info> get_new_protest_Info_byAnum(String aNum) {
		String query = "from Protest_Info a, GC_Track_Service_Event b where b.status =:status and b.event_Type =:event_type and a.a_No = b.a_No and a.a_No = :aNum";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", "New");
		map.put("event_type", "New Protest");
		map.put("aNum", aNum);

		List<?> resultList = access.queryWithParams(query, map);

		if (resultList != null && resultList.size() > 0) {
			List<Protest_Info> list_of_new_protest_info = Util
					.getDesiredClassObjectList(resultList, Protest_Info.class);

			return list_of_new_protest_info;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public GC_Track_Service_Event get_GC_Track_Service_Event_For_New_Case(
			String aNo) {
		String query = "from GC_Track_Service_Event where a_No =:a_No and event_Type = :event_Type";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a_No", aNo);
		map.put("event_Type", "New Protest");

		List<?> resultList = access.queryWithParams(query, map);

		if (resultList != null && resultList.size() > 0) {
			return ((List<GC_Track_Service_Event>) resultList).get(0);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<GC_Track_Service_Event> get_list_of_gc_track_service_event_based_on_a_no(
			String a_No) {
		String query = "from GC_Track_Service_Event where a_No =:a_No";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a_No", a_No);

		List<?> resultList = access.queryWithParams(query, map);

		if (resultList != null && resultList.size() > 0) {
			return ((List<GC_Track_Service_Event>) resultList);
		}

		return null;
	}

	@Transactional
	public void deleteEventsByA_no(String a_no) throws Exception {
		List<GC_Track_Service_Event> listOf_gc_track_service_event = getListOf_gc_track_service_event_byA_no(a_no);

		if (listOf_gc_track_service_event == null)
			return;

		for (GC_Track_Service_Event eachGC_track_service_event : listOf_gc_track_service_event) {
			access.delete(eachGC_track_service_event);
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<GC_Track_Service_Event> getListOf_gc_track_service_event_byA_no(
			String a_no) {
		String query = "from GC_Track_Service_Event where a_No =:a_No";

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a_No", a_no);

		List<?> resultList = access.queryWithParams(query, map);

		if (resultList != null && resultList.size() > 0) {
			return (List<GC_Track_Service_Event>) resultList;
		}

		return new ArrayList<GC_Track_Service_Event>();
	}

	@Transactional
	public int changeBNumber(String newB_no, String oldB_no) throws Exception {
		String query = "update gc_track_service_event set b_No = '" + newB_no
				+ "' where b_No = '" + oldB_no + "'";

		int numberOfUpdates = access.executeUpdateSQL(query);

		return numberOfUpdates;
	}
}
