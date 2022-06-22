package gov.gao.epds.dto;

import java.io.Serializable;


/**
 * @author MHussaini
 *
 */
public class CompanyInfo extends DTOValidator implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5642292582414976894L;
	private String companyName;
	private String companyAddress;
	private String companyDetail;
	private Integer intervenorFileId;

	public Integer getIntervenorFileId() {
		return intervenorFileId;
	}

	public void setIntervenorFileId(Integer intervenorFileId) {
		this.intervenorFileId = intervenorFileId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyAddress() {
		return companyAddress;
	}

	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}

	public String getCompanyDetail() {
		return companyDetail;
	}

	public void setCompanyDetail(String companyDetail) {
		this.companyDetail = companyDetail;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((companyAddress == null) ? 0 : companyAddress.hashCode());
		result = prime * result + ((companyDetail == null) ? 0 : companyDetail.hashCode());
		result = prime * result + ((companyName == null) ? 0 : companyName.hashCode());
		result = prime * result + ((intervenorFileId == null) ? 0 : intervenorFileId.hashCode());
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
		if (!(obj instanceof CompanyInfo))
			return false;
		CompanyInfo other = (CompanyInfo) obj;
		if (companyAddress == null) {
			if (other.companyAddress != null)
				return false;
		} else if (!companyAddress.equals(other.companyAddress))
			return false;
		if (companyDetail == null) {
			if (other.companyDetail != null)
				return false;
		} else if (!companyDetail.equals(other.companyDetail))
			return false;
		if (companyName == null) {
			if (other.companyName != null)
				return false;
		} else if (!companyName.equals(other.companyName))
			return false;
		if (intervenorFileId == null) {
			if (other.intervenorFileId != null)
				return false;
		} else if (!intervenorFileId.equals(other.intervenorFileId))
			return false;
		return true;
	}

	

	

}
