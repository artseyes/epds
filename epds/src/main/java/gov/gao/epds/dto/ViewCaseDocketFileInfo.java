package gov.gao.epds.dto;

import java.io.Serializable;

import javax.validation.constraints.Pattern;


/**
 * @author MHussaini
 *
 */
public class ViewCaseDocketFileInfo extends DTOValidator implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8387405839488865957L;
	
	@Pattern(regexp = PROTEST_ID_PATTERN, message="Invalid input format!")
	private String protestId;
	private String submissionDate;
	private Integer doc_Type_Id;
	
	@Pattern(regexp = COMMENTS_PATTERN, message="Invalid input format!")
	private String fileAlert;
	
	public String getProtestId() {
		return protestId;
	}
	
	public void setProtestId(String protestId) {
		this.protestId = protestId;
	}
	
	public String getSubmissionDate() {
		return submissionDate;
	}
	
	public void setSubmissionDate(String submissionDate) {
		this.submissionDate = submissionDate;
	}
	
	public Integer getDoc_Type_Id() {
		return doc_Type_Id;
	}
	public void setDoc_Type_Id(int doc_Type_Id) {
		this.doc_Type_Id = doc_Type_Id;
	}
	public String getFileAlert() {
		return fileAlert;
	}
	public void setFileAlert(String fileAlert) {
		this.fileAlert = fileAlert;
	}
}
