package gov.gao.epds.persistence.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

@Entity
@Table
@Audited
public class Protest_File_Bridge implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6252093446462492802L;
	@Id
	@Column
	@GenericGenerator(
	        name = "protestFileBridgeSeqGen", 
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
	        generator = "protestFileBridgeSeqGen"
	    )
	/*@Id
	@Column
	@GeneratedValue(strategy = GenerationType.SEQUENCE)*/
	private Integer id;
	@Column
	private String a_No;
	@Column
	private Integer file_Id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getA_No() {
		return a_No;
	}

	public void setA_No(String a_No) {
		this.a_No = a_No;
	}

	public Integer getFile_Id() {
		return file_Id;
	}

	public void setFile_Id(Integer file_Id) {
		this.file_Id = file_Id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a_No == null) ? 0 : a_No.hashCode());
		result = prime * result + ((file_Id == null) ? 0 : file_Id.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Protest_File_Bridge))
			return false;
		Protest_File_Bridge other = (Protest_File_Bridge) obj;
		if (a_No == null) {
			if (other.a_No != null)
				return false;
		} else if (!a_No.equals(other.a_No))
			return false;
		if (file_Id == null) {
			if (other.file_Id != null)
				return false;
		} else if (!file_Id.equals(other.file_Id))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	
	
}
