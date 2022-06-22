package gov.gao.epds.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DataAccess {

	private static Logger logger = Logger.getLogger(DataAccess.class);

	@Autowired
	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private Session getCurrentSession() {
		return this.sessionFactory.getCurrentSession();
	}

	/**
	 * Use this method to update or insert a object using HQL
	 * 
	 * @param query
	 *            For insert / update query
	 * 
	 */
	public int executeUpdateSQL(String query) throws Exception {
		int result = 0;
		try {
			Session session = getCurrentSession();
			Query q = session.createSQLQuery(query);
			result = q.executeUpdate();
		} catch (Exception e) {
			logger.error("Error while executing save/update", e);
			throw new Exception(e);
		}
		return result;
	}

	/**
	 * Method to execute a prepared select HQL query. Return the query results
	 * as a List. If the query contains multiple results per row, the results
	 * are returned in an instance of Object[].
	 * <p>
	 * Example usage : <br>
	 * query = "select user u where u.id = :id"; <br>
	 * Map<String, Object> params = new HashMap<String, Object>(); <br>
	 * params.put("id", 10); <br>
	 * 
	 * @param query
	 *            - Select prepared query to be executed
	 * @param parameters
	 *            - Parameters to be passed to the query. Pass empty Map if no
	 *            parameters are present
	 * @param limit
	 *            - Max number of results to be fetched
	 * @return The list of requested entities
	 * @throws
	 * */
	@SuppressWarnings("rawtypes")
	public List<Object> queryWithParamsWithLimit(String query,
			Map<String, Object> parameters, int limit, int start)
			throws Exception {
		if (logger.isDebugEnabled()){
			logger.debug(query + " : params - " + parameters);	
		}
		Session session = getCurrentSession();
		List<Object> list = null;
		try {
			Query queryObj = session.createQuery(query).setMaxResults(limit)
					.setFirstResult(start);

			if (parameters != null && !parameters.isEmpty()) {
				for (String param : parameters.keySet()) {
					if (parameters.get(param) instanceof Collection) {
						queryObj.setParameterList(param,
								(Collection) parameters.get(param));
					} else {
						queryObj.setParameter(param, parameters.get(param));
					}
				}
			}
			System.out.println(queryObj.getQueryString());
			list = queryObj.list();

		} catch (Exception e) {
			logger.error("error in query() , ", e);
			throw new Exception(e);
		}
		return list;
	}

	@SuppressWarnings("rawtypes")
	public int queryCount(String query, Map<String, Object> parameters)
			throws Exception {
		if (logger.isDebugEnabled()){
			logger.debug(query + " : params - " + parameters);	
		}
		Session session = getCurrentSession();
		int count = 0;
		try {
			Query queryObj = session.createQuery(query);

			if (parameters != null && !parameters.isEmpty()) {
				for (String param : parameters.keySet()) {
					if (parameters.get(param) instanceof Collection) {
						queryObj.setParameterList(param,
								(Collection) parameters.get(param));
					} else {
						queryObj.setParameter(param, parameters.get(param));
					}
				}
			}

			count = ((Long) queryObj.uniqueResult()).intValue();

		} catch (Exception e) {
			logger.error("error in query() , ", e);
			throw new Exception(e);
		}
		return count;
	}

	/**
	 * Method to execute a prepared select HQL query. Return the query results
	 * as a List. If the query contains multiple results per row, the results
	 * are returned in an instance of Object[].
	 * <p>
	 * Example usage : <br>
	 * query = "select user u where u.id = :id"; <br>
	 * Map<String, Object> params = new HashMap<String, Object>(); <br>
	 * params.put("id", 10); <br>
	 * 
	 * @param query
	 *            - Select prepared query to be executed
	 * @param parameters
	 *            - Parameters to be passed to the query. Pass empty Map if no
	 *            parameters are present
	 * @return The list of requested entities
	 * 
	 * */
	public List<?> queryWithParams(String query, Map<String, Object> parameters) {
		if (logger.isDebugEnabled()){
			logger.debug(query + " : params - " + parameters);	
		}
		Session session = getCurrentSession();
		
		
		List<?> list = null;
		try {
			Query queryObj = session.createQuery(query);
			if (parameters != null && !parameters.isEmpty()) {
				for (String param : parameters.keySet()) {
					if (parameters.get(param) instanceof Collection) {
						queryObj.setParameterList(param,
								(Collection<?>) parameters.get(param));
					} else {
						queryObj.setParameter(param, parameters.get(param));
					}
				}
			}
			
			list = queryObj.list();

		} catch (Exception e) {
			logger.error("error in query() , ", e);
			// throw new Exception(e);
			e.printStackTrace();
		}
		return list;
	}

	public List<?> queryWithOnlyQuery(String query) {
		Session session = getCurrentSession();
		return session.createQuery(query).list();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Object> sqlqueryWithParams(String query,
			Map<String, Object> parameters) throws Exception {
		if (logger.isDebugEnabled()){
			logger.debug(query + " : params - " + parameters);	
		}
		Session session = getCurrentSession();
		List<Object> list = null;
		try {
			Query queryObj = session.createSQLQuery(query);

			if (parameters != null && !parameters.isEmpty()) {
				for (String param : parameters.keySet()) {
					if (parameters.get(param) instanceof Collection) {
						queryObj.setParameterList(param,
								(Collection) parameters.get(param));
					} else {
						queryObj.setParameter(param, parameters.get(param));
					}
				}
			}

			/*System.out.println(queryObj.getQueryString());*/

			list = queryObj.list();

		} catch (Exception e) {
			logger.error("error in query() , ", e);
			throw new Exception(e);
		}
		return list;
	}

	public void updateQuery(String queryString, Map<String, Object> map)
			throws Exception {

		Session session = getCurrentSession();
		try {
			Query query = session.createQuery(queryString);
			query.setProperties(map);
		} catch (Exception e) {
			logger.error("error in query() , ", e);
			throw new Exception(e);
		}

	}

	/**
	 * @param t
	 *            - Entity to be saved
	 * @return saved entity
	 * 
	 */
	public <T> T save(T t) {
		try {
			Session session = getCurrentSession();
			session.save(t);
		} catch (Exception e) {
			logger.error("Unable to save entity", e);
			// throw new Exception(e);
			e.printStackTrace();
		}
		return t;
	}

	/**
	 * @param list
	 *            - Entity list to be saved
	 * @return Saved entity list
	 * 
	 * */
	public <T> List<T> save(List<T> list) throws Exception {
		try {
			Session session = getCurrentSession();
			for (T entity : list) {
				session.save(entity);
			}
		} catch (Exception e) {
			logger.error("Unable to save entity", e);
			throw new Exception(e);
		}
		return list;
	}

	/**
	 * @param t
	 * @return saved/updated entity
	 * 
	 */
	public <T> T saveOrUpdate(T t) throws Exception {
		try {
			Session session = getCurrentSession();
			session.saveOrUpdate(t);
		} catch (Exception e) {
			logger.error("Unable to save/update entity", e);
			throw new Exception(e);
		}
		return t;
	}

	/**
	 * @param t
	 * @return update entity
	 * 
	 */
	public <T> T update(T t) throws Exception {
		try {
			Session session = getCurrentSession();
			session.update(t);
		} catch (Exception e) {
			logger.error("Unable to save/update entity", e);
			throw new Exception(e);
		}
		return t;
	}

	/**
	 * To delete an entity
	 * 
	 * @param t
	 *            - Entity to be deleted
	 * 
	 * */
	public <T> void delete(T t) throws Exception {
		try {
			Session session = getCurrentSession();
			session.delete(t);
		} catch (Exception e) {
			logger.error("Unable to save/update entity", e);
			throw new Exception(e);
		}
	}

	public void deleteById(Class<?> entityClass, String id) throws Exception {
		try {
			Session session = getCurrentSession();
			if (session.get(entityClass, id) != null) {
				session.delete(session.get(entityClass, id));
			}

		} catch (Exception e) {
			logger.error("Unable delete entity", e);
			throw new Exception(e);
		}
	}

	/**
	 * Load a entity by primary key
	 * 
	 * @param t
	 *            - entity class to be loaded
	 * @param identifier
	 *            - the primary key
	 * 
	 * */
	@SuppressWarnings("rawtypes")
	public Object loadById(Class t, String identifier) throws Exception {
		logger.info("Loading object by id:" + identifier);
		Session session = getCurrentSession();
		Object obj;
		try {
			obj = session.get(t, identifier);
		} catch (Exception e) {
			logger.error("Error while getting object by id", e);
			throw new Exception(e);
		}
		logger.info("Loaded object by id:" + identifier);
		return obj;
	}

	/**
	 * Load a entity by primary key
	 * 
	 * @param t
	 *            - entity class to be loaded
	 * @param identifier
	 *            - the primary key
	 * 
	 * */
	@SuppressWarnings("rawtypes")
	public Object loadById(Class t, int identifier) throws Exception {
		logger.info("Loading object by id:" + identifier);
		Session session = getCurrentSession();
		Object obj;
		try {
			obj = session.get(t, identifier);
		} catch (Exception e) {
			logger.error("Error while getting object by id", e);
			throw new Exception(e);
		}
		return obj;
	}

	/**
	 * Load a entity by primary key
	 * 
	 * @param t
	 *            - entity class to be loaded
	 * @param identifier
	 *            - the primary key
	 * 
	 * */
	public Object loadById(Class t, Long identifier) throws Exception {
		logger.info("Loading object by id:" + identifier);
		Session session = getCurrentSession();
		Object obj;
		try {
			obj = session.get(t, identifier);
		} catch (Exception e) {
			logger.error("Error while getting object by id", e);
			throw new Exception(e);
		}
		logger.info("Loaded object by id:" + identifier);
		return obj;
	}

	/**
	 * Method to execute any native sql. (select)
	 * <p>
	 * 
	 * @param query
	 *            - native select sql to be executed
	 * @return list For select query
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public List<Object> executeNativeSQL(String query,
			Map<Integer, Object> parameters) throws Exception {
		List<Object> list = new ArrayList<Object>();
		try {
			Session session = getCurrentSession();
			Query q = session.createSQLQuery(query);
			if (parameters != null) {
				for (int param : parameters.keySet()) {
					q.setParameter(param, parameters.get(param));
				}
			}
			list = q.list();
		} catch (Exception e) {
			logger.error("Error on select query", e);
			throw new Exception(e);
		}
		return list;
	}

	/**
	 * @param query
	 * @param parameters
	 *            For insert / update query with parameters
	 * 
	 */
	public void executeNativeUpdateSQLWithSimpleParams(String query,
			Map<Integer, Object> parameters) throws Exception {
		try {
			Session session = getCurrentSession();
			Query q = session.createSQLQuery(query.toString());
			if (parameters != null) {
				for (int param : parameters.keySet()) {
					q.setParameter(param, parameters.get(param));
				}
			}
			q.executeUpdate();
			session.flush();
		} catch (Exception e) {
			logger.error("Error on update / Save", e);
			throw new Exception(e);
		}
	}

}
