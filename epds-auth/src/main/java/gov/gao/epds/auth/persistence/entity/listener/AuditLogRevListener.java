
package gov.gao.epds.auth.persistence.entity.listener;
import gov.gao.epds.auth.persistence.entity.Audit_Log;
import gov.gao.epds.auth.persistence.entity.User_info;
import gov.gao.epds.auth.utils.SpringApplicationContext;
import gov.gao.epds.tokenutils.TokenUtils;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.EntityTrackingRevisionListener;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.exception.AuditException;
import org.hibernate.envers.exception.NotAuditedException;
import org.hibernate.envers.query.AuditEntity;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author MHussaini
 *
 */
public class AuditLogRevListener
             implements EntityTrackingRevisionListener {
	
	@Context
	private HttpServletRequest httpRequest;
	
    @Override
    public void entityChanged(Class entityClass, String entityName,
                              Serializable entityId, RevisionType revisionType,
                              Object revisionEntity) {
    	
    	Diff diff = this.getChangeSet(entityClass, entityId);
         
        Audit_Log userRevEntity = (Audit_Log) revisionEntity;
        
        //String userId = (String) httpRequest.getHeader("userId");
    	
        try {
        	//Amer : Actually this is a rest app and there is not user id in  and just doing it this way is not might potentially give wrong results.
    		/*userRevEntity.setUserId(TokenUtils.decrypedTokenUserId);*/
    		userRevEntity.setEntityClassName(entityClass.getName());
			userRevEntity.setModificationType(revisionType.name());
			userRevEntity.setPrimaryKeyValue(entityId.toString());
			
			if (null != diff && diff.hasChanges()){
	            List<ValueChange> changeSet = diff.getChangesByType(ValueChange.class);
	            if (!changeSet.isEmpty() && changeSet.size() > 0)
	            userRevEntity.setChangeSet(diff.prettyPrint());
	        }else if (revisionType.name().equalsIgnoreCase("ADD")){
	        	userRevEntity.setChangeSet("New Entity Added");
	        }else if (revisionType.name().equalsIgnoreCase("DEL")){
	        	userRevEntity.setChangeSet("Entity Deleted");
	        }
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
    	
        
    }

    
    /**
	 * @param entityClass
	 * @param entityId
	 * @return
	 * @throws HibernateException
	 * @throws AuditException
	 * @throws NonUniqueResultException
	 * @throws NoResultException
	 * @throws IllegalArgumentException
	 * @throws NotAuditedException
	 * @throws IllegalStateException
	 */
	@SuppressWarnings("unchecked")
	private Diff getChangeSet(Class entityClass, Serializable entityId)
			throws HibernateException, AuditException, NonUniqueResultException, NoResultException,
			IllegalArgumentException, NotAuditedException, IllegalStateException {


		Diff diff = null;
		
		if (entityClass != null && entityId != null) {

			SessionFactory sessionFactory = (SessionFactory) SpringApplicationContext.getBean("sessionFactory");

			Session currentSession = sessionFactory.getCurrentSession();
			AuditReader auditReader = AuditReaderFactory.get(currentSession);
			Number prior_revision = null;
			Object newEntityObject = null;
			Object oldEntityObject = null;
			
			Number currentRevision = (Number) auditReader.createQuery().forRevisionsOfEntity(entityClass, false, true)
					.addProjection(AuditEntity.revisionNumber().max()).add(AuditEntity.id().eq(entityId))
					.getSingleResult();
			
			if (currentRevision != null){
				prior_revision = (Number) auditReader.createQuery().forRevisionsOfEntity(entityClass, false, true)
						.addProjection(AuditEntity.revisionNumber().max()).add(AuditEntity.id().eq(entityId))
						.add(AuditEntity.revisionNumber().lt(currentRevision)).getSingleResult();

				newEntityObject = auditReader.find(entityClass, entityId, currentRevision);	
			}

			

			if (prior_revision != null && newEntityObject != null) {

				oldEntityObject = auditReader.find(entityClass, entityId, prior_revision);
				Javers javers = JaversBuilder.javers().build();
				if (oldEntityObject != null){
					diff = javers.compare(oldEntityObject, newEntityObject);	
				}
				
			}

		}
		return diff;
	
	}
	
    @Override
    public void newRevision(Object revisionEntity) {
    	
    	
    }
   
}