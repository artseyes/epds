package gov.gao.epds.auth.dao;

import gov.gao.epds.auth.persistence.DataAccess;
import gov.gao.epds.auth.persistence.entity.Account_status;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class Account_status_dao extends DataAccess {
	/*@Autowired
	private DataAccess access;*/

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Account_status> getListOfAccount_status() {
		String query = "from ACCOUNT_STATUS";
		Map<String, Object> map = new HashMap<String, Object>();

		List<?> resultList = queryWithParams(query, map);

		if (resultList != null && resultList.size() > 0) {
			return (List<Account_status>) resultList;
		}

		return null;
	}

	@Transactional
	public Account_status getAccount_statusBasedOnStatusId(Integer status_id) {
		String query = "from ACCOUNT_STATUS where status_id =:status_id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status_id", status_id);

		List<?> resultList = queryWithParams(query, map);

		if (resultList != null && resultList.size() > 0) {
			return (Account_status) resultList.get(0);
		}

		return null;
	}

	@Transactional
	public Account_status getAccount_statusBasedOnAcronym(String status_acronym) {
		String query = "from ACCOUNT_STATUS where status_acronym =:status_acronym";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status_acronym", status_acronym);

		List<?> resultList = queryWithParams(query, map);

		if (resultList != null && resultList.size() > 0) {
			return (Account_status) resultList.get(0);
		}

		return null;
	}

}
