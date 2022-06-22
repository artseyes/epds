package gov.gao.epds.persistence.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class File_Alert implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4226516757949584779L;

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int file_Alert_Id;

	@Column
	String user_Id;
	@Column
	String viewed;
	@Column
	int file_Id;

	public int getFile_Alert_Id() {
		return file_Alert_Id;
	}

	public void setFile_Alert_Id(int file_Alert_Id) {
		this.file_Alert_Id = file_Alert_Id;
	}

	public String getUser_Id() {
		return user_Id;
	}

	public void setUser_Id(String user_Id) {
		this.user_Id = user_Id;
	}

	public String getViewed() {
		return viewed;
	}

	public void setViewed(String viewed) {
		this.viewed = viewed;
	}

	public int getFile_Id() {
		return file_Id;
	}

	public void setFile_Id(int file_Id) {
		this.file_Id = file_Id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + file_Alert_Id;
		result = prime * result + file_Id;
		result = prime * result + ((user_Id == null) ? 0 : user_Id.hashCode());
		result = prime * result + ((viewed == null) ? 0 : viewed.hashCode());
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
		if (!(obj instanceof File_Alert))
			return false;
		File_Alert other = (File_Alert) obj;
		if (file_Alert_Id != other.file_Alert_Id)
			return false;
		if (file_Id != other.file_Id)
			return false;
		if (user_Id == null) {
			if (other.user_Id != null)
				return false;
		} else if (!user_Id.equals(other.user_Id))
			return false;
		if (viewed == null) {
			if (other.viewed != null)
				return false;
		} else if (!viewed.equals(other.viewed))
			return false;
		return true;
	}
	
	

}
