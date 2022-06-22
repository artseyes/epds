package gov.gao.epds.dto;

import java.io.Serializable;
import java.util.Date;

public class CaseCompletionStatus implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4720931798165990859L;
	
	private Boolean isZipCreated;
	private Boolean isDmEntered;
	private Boolean isDmVerfied;
	private Date caseCompleted;
	
	private Integer dmNumber;
	/**
	 * @return the isZipCreated
	 */
	public Boolean getIsZipCreated() {
		return isZipCreated;
	}
	/**
	 * @param isZipCreated the isZipCreated to set
	 */
	public void setIsZipCreated(Boolean isZipCreated) {
		this.isZipCreated = isZipCreated;
	}
	/**
	 * @return the isDmEntered
	 */
	public Boolean getIsDmEntered() {
		return isDmEntered;
	}
	/**
	 * @param isDmEntered the isDmEntered to set
	 */
	public void setIsDmEntered(Boolean isDmEntered) {
		this.isDmEntered = isDmEntered;
	}
	/**
	 * @return the isDmVerfied
	 */
	public Boolean getIsDmVerfied() {
		return isDmVerfied;
	}
	/**
	 * @param isDmVerfied the isDmVerfied to set
	 */
	public void setIsDmVerfied(Boolean isDmVerfied) {
		this.isDmVerfied = isDmVerfied;
	}
	/**
	 * @return the dmNumber
	 */
	public Integer getDmNumber() {
		return dmNumber;
	}
	/**
	 * @param dmNumber the dmNumber to set
	 */
	public void setDmNumber(Integer dmNumber) {
		this.dmNumber = dmNumber;
	}
	/**
	 * @return the caseCompleted
	 */
	public Date getCaseCompleted() {
		return caseCompleted;
	}
	/**
	 * @param caseCompleted the caseCompleted to set
	 */
	public void setCaseCompleted(Date caseCompleted) {
		this.caseCompleted = caseCompleted;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((caseCompleted == null) ? 0 : caseCompleted.hashCode());
		result = prime * result + ((dmNumber == null) ? 0 : dmNumber.hashCode());
		result = prime * result + ((isDmEntered == null) ? 0 : isDmEntered.hashCode());
		result = prime * result + ((isDmVerfied == null) ? 0 : isDmVerfied.hashCode());
		result = prime * result + ((isZipCreated == null) ? 0 : isZipCreated.hashCode());
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
		if (!(obj instanceof CaseCompletionStatus))
			return false;
		CaseCompletionStatus other = (CaseCompletionStatus) obj;
		if (caseCompleted == null) {
			if (other.caseCompleted != null)
				return false;
		} else if (!caseCompleted.equals(other.caseCompleted))
			return false;
		if (dmNumber == null) {
			if (other.dmNumber != null)
				return false;
		} else if (!dmNumber.equals(other.dmNumber))
			return false;
		if (isDmEntered == null) {
			if (other.isDmEntered != null)
				return false;
		} else if (!isDmEntered.equals(other.isDmEntered))
			return false;
		if (isDmVerfied == null) {
			if (other.isDmVerfied != null)
				return false;
		} else if (!isDmVerfied.equals(other.isDmVerfied))
			return false;
		if (isZipCreated == null) {
			if (other.isZipCreated != null)
				return false;
		} else if (!isZipCreated.equals(other.isZipCreated))
			return false;
		return true;
	}
	
	
	
}
