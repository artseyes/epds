package gov.gao.epds.persistence.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import gov.gao.epds.persistence.listener.AuditLogRevListener;

@Entity
@RevisionEntity(AuditLogRevListener.class)
@Table
public class Audit_Log implements Serializable {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 8333048464696603363L;
	
	private String userId;
	
	@Id
	@GenericGenerator(
	        name = "auditLogSeqGenerator", 
	        strategy = "enhanced-sequence",
	        parameters = {
	        	@org.hibernate.annotations.Parameter(name="prefer_sequence_per_entity", value="true"),	
	        		
	            @org.hibernate.annotations.Parameter(
	                name = "optimizer",
	                value = "pooled-lo"
	            ),
	            @org.hibernate.annotations.Parameter(
	                name = "initial_value", 
	                value = "1"
	            ),
	            @org.hibernate.annotations.Parameter(
	                name = "increment_size", 
	                value = "1"
	            )
	        }
	    )
	    @GeneratedValue(
	        strategy = GenerationType.SEQUENCE, 
	        generator = "auditLogSeqGenerator"
	    )
	@Column(name = "rev_Id")
	@RevisionNumber
	private Long revisionId;

	
	/*@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
    @RevisionNumber
    @Column(name = "rev_Id")
    private int revisionId;*/
	
	
	@Column(name = "entity_class_name")
	private String entityClassName;
	
	@Column(name = "modification_type")
	private String modificationType;
	
	@Column(name = "primary_key_value")
	private String primaryKeyValue;

	@Column(name = "rev_timestamp")
	@RevisionTimestamp
	private Date revisionTimeStamp;
	
	/**
	 * Stores the change set
	 */
	@Column(name = "change_set", columnDefinition="CLOB")
	@Lob
	@Basic(fetch=FetchType.LAZY)
	private String changeSet;
	
	
	public String getModificationType() {
		return modificationType;
	}
	public void setModificationType(String modificationType) {
		this.modificationType = modificationType;
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getEntityClassName() {
		return entityClassName;
	}
	public void setEntityClassName(String entityClassName) {
		this.entityClassName = entityClassName;
	}

	public String getPrimaryKeyValue() {
		return primaryKeyValue;
	}
	public void setPrimaryKeyValue(String primaryKeyValue) {
		this.primaryKeyValue = primaryKeyValue;
	}
	public String getChangeSet() {
		return changeSet;
	}
	public void setChangeSet(String changeSet) {
		this.changeSet = changeSet;
	}
	
	
	
    
}