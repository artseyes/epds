package gov.gao.epds.persistence.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table
@Audited
public class File_Info implements Comparable<File_Info>, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2712935625693737727L;

	@Id
	@Column
	@GenericGenerator(
	        name = "fileInfoSequenceGenerator", 
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
	        generator = "fileInfoSequenceGenerator"
	    )
	/*@Id
	@Column
	@GeneratedValue(strategy = GenerationType.SEQUENCE)*/
	private int file_Id;

	@Column
	private String po;

	@JsonIgnore
	@Column
	private String file_Path;

	@Column
	private int doc_Type_Id;

	@Column
	private String a_No;

	@Column
	private String submission_Date;
	
	@Column(name="ORG_SUBMISSION_DATE")
	private String originalSubmissionDate;

	@Column
	private String is_Confidential;

	@Column(length=300)
	private String comments;

	@Column
	private String submitter_User_Id;

	@Transient
	private String docTypeName;

	@Transient
	private String fileAlert;

	@Transient
	private String fileName;

	@Column
	private String company_Name;

	@Column
	private String is_Intervene_Approved;

	@Column
	private String case_access_request_status;
	
	@Column
	private String file_identifier;

	@Column
	private String company_Address;
	
	@Column
	private String isCommentEdited;

	@Column
	private String isAttorneyNoteEdited;

	@Column(length=300)
	private String attorney_Note;

	@Column
	private String filler;

	@Column
	private Timestamp time_stamp;

	@Column
	@JsonIgnore
	private String already_viewed_by;

	@Transient
	private String is_Visible;

	@Column
	private String submitter_Role;

	@Transient
	private String transient_Note;
	
	@Transient
	private String transient_Comments;

	@Transient
	private String transient_Date;

	@Transient
	private String transient_Time;

	@Transient
	private String transient_Attorney_Note_Date;
	
	@Transient
	private String transient_Comments_Date;

	@Transient
	private String indexNum;
	
	public File_Info() {
		super();
	}

	public File_Info(int file_Id, String submission_Date) {
		super();
		this.file_Id = file_Id;
		this.submission_Date = submission_Date;
	}

	public String getAlready_viewed_by() {
		return already_viewed_by;
	}

	public void setAlready_viewed_by(String already_viewed_by) {
		this.already_viewed_by = already_viewed_by;
	}

	public Timestamp getTime_stamp() {
		return time_stamp;
	}

	public void setTime_stamp(Timestamp time_stamp) {
		this.time_stamp = time_stamp;
	}

	public String getCase_access_request_status() {
		return case_access_request_status;
	}

	public void setCase_access_request_status(String case_access_request_status) {
		this.case_access_request_status = case_access_request_status;
	}

	public String getFiller() {
		return filler;
	}

	public void setFiller(String filler) {
		this.filler = filler;
	}

	public String getTransient_Attorney_Note_Date() {
		return transient_Attorney_Note_Date;
	}

	public void setTransient_Attorney_Note_Date(
			String transient_Attorney_Note_Date) {
		this.transient_Attorney_Note_Date = transient_Attorney_Note_Date;
	}

	public String getTransient_Time() {
		return transient_Time;
	}

	public void setTransient_Time(String transient_Time) {
		this.transient_Time = transient_Time;
	}

	public String getTransient_Note() {
		return transient_Note;
	}

	public void setTransient_Note(String transient_Note) {
		this.transient_Note = transient_Note;
	}

	public String getTransient_Date() {
		return transient_Date;
	}

	public void setTransient_Date(String transient_Date) {
		this.transient_Date = transient_Date;
	}

	public String getSubmitter_Role() {
		return submitter_Role;
	}

	public void setSubmitter_Role(String submitter_Role) {
		this.submitter_Role = submitter_Role;
	}

	public String getIs_Visible() {
		return is_Visible;
	}

	public void setIs_Visible(String is_Visible) {
		this.is_Visible = is_Visible.trim();
	}

	public String getAttorney_Note() {
		return attorney_Note;
	}

	public void setAttorney_Note(String attorney_Note) {
		this.attorney_Note = attorney_Note;
	}

	public String getCompany_Address() {
		return company_Address;
	}

	public void setCompany_Address(String company_Address) {
		this.company_Address = company_Address;
	}

	public String getCompany_Name() {
		return company_Name;
	}

	public void setCompany_Name(String company_Name) {
		this.company_Name = company_Name;
	}

	public String getIs_Intervene_Approved() {
		return is_Intervene_Approved;
	}

	public void setIs_Intervene_Approved(String is_Intervene_Approved) {
		this.is_Intervene_Approved = is_Intervene_Approved;
	}

	public String getSubmitter_User_Id() {
		return submitter_User_Id;
	}

	public void setSubmitter_User_Id(String submitter_User_Id) {
		this.submitter_User_Id = submitter_User_Id;
	}

	public String getFileAlert() {
		return fileAlert;
	}

	public void setFileAlert(String fileAlert) {
		this.fileAlert = fileAlert;
	}

	public String getDocTypeName() {
		return docTypeName;
	}

	public void setDocTypeName(String docTypeName) {
		this.docTypeName = docTypeName;
	}

	public int getFile_Id() {
		return file_Id;
	}

	public void setFile_Id(int file_Id) {
		this.file_Id = file_Id;
	}

	public String getPo() {
		return po;
	}

	public void setPo(String po) {
		this.po = po;
	}

	public String getFile_Path() {
		return file_Path;
	}

	public void setFile_Path(String file_Path) {
		this.file_Path = file_Path;
	}

	public int getDoc_Type_Id() {
		return doc_Type_Id;
	}

	public void setDoc_Type_Id(int doc_Type_Id) {
		this.doc_Type_Id = doc_Type_Id;
	}

	public String getA_No() {
		return a_No;
	}

	public void setA_No(String a_No) {
		this.a_No = a_No;
	}

	public String getSubmission_Date() {
		return submission_Date;
	}

	public void setSubmission_Date(String submission_Date) {
		this.submission_Date = submission_Date;
	}

	public String getIs_Confidential() {
		return is_Confidential;
	}

	public void setIs_Confidential(String is_Confidential) {
		this.is_Confidential = is_Confidential;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	

	
	@Override
	public int compareTo(File_Info file_Info2) {
		DateFormat format = new SimpleDateFormat("MMM dd yyyy HH:mm:ss z",
				Locale.ENGLISH);
		Date date1;
		Date date2;
		try {
			
			if (this.originalSubmissionDate != null){
				date1 = format.parse(this.originalSubmissionDate);
			}else{
				date1 = format.parse(this.submission_Date);	
			}
			
			
			if (file_Info2.getOriginalSubmissionDate() != null){
				date2 = format.parse(file_Info2.getOriginalSubmissionDate());	
			}else{
				date2 = format.parse(file_Info2.getSubmission_Date());
			}
			

			return date1.compareTo(date2);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return 0;
	}
	
	
	

	/**
	 * @return the file_identifier
	 */
	public String getFile_identifier() {
		return file_identifier;
	}

	/**
	 * @param file_identifier the file_identifier to set
	 */
	public void setFile_identifier(String file_identifier) {
		this.file_identifier = file_identifier;
	}

	public String getIndexNum() {
		return indexNum;
	}

	public void setIndexNum(String indexNum) {
		this.indexNum = indexNum;
	}


	/**
	 * @return the originalSubmissionDate
	 */
	public String getOriginalSubmissionDate() {
		return originalSubmissionDate;
	}

	/**
	 * @param originalSubmissionDate the originalSubmissionDate to set
	 */
	public void setOriginalSubmissionDate(String originalSubmissionDate) {
		this.originalSubmissionDate = originalSubmissionDate;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a_No == null) ? 0 : a_No.hashCode());
		result = prime * result + ((already_viewed_by == null) ? 0 : already_viewed_by.hashCode());
		result = prime * result + ((attorney_Note == null) ? 0 : attorney_Note.hashCode());
		result = prime * result + ((case_access_request_status == null) ? 0 : case_access_request_status.hashCode());
		result = prime * result + ((comments == null) ? 0 : comments.hashCode());
		result = prime * result + ((company_Address == null) ? 0 : company_Address.hashCode());
		result = prime * result + ((company_Name == null) ? 0 : company_Name.hashCode());
		result = prime * result + ((docTypeName == null) ? 0 : docTypeName.hashCode());
		result = prime * result + doc_Type_Id;
		result = prime * result + ((fileAlert == null) ? 0 : fileAlert.hashCode());
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + file_Id;
		result = prime * result + ((file_Path == null) ? 0 : file_Path.hashCode());
		result = prime * result + ((file_identifier == null) ? 0 : file_identifier.hashCode());
		result = prime * result + ((filler == null) ? 0 : filler.hashCode());
		result = prime * result + ((indexNum == null) ? 0 : indexNum.hashCode());
		result = prime * result + ((is_Confidential == null) ? 0 : is_Confidential.hashCode());
		result = prime * result + ((is_Intervene_Approved == null) ? 0 : is_Intervene_Approved.hashCode());
		result = prime * result + ((is_Visible == null) ? 0 : is_Visible.hashCode());
		result = prime * result + ((originalSubmissionDate == null) ? 0 : originalSubmissionDate.hashCode());
		result = prime * result + ((po == null) ? 0 : po.hashCode());
		result = prime * result + ((submission_Date == null) ? 0 : submission_Date.hashCode());
		result = prime * result + ((submitter_Role == null) ? 0 : submitter_Role.hashCode());
		result = prime * result + ((submitter_User_Id == null) ? 0 : submitter_User_Id.hashCode());
		result = prime * result + ((time_stamp == null) ? 0 : time_stamp.hashCode());
		result = prime * result
				+ ((transient_Attorney_Note_Date == null) ? 0 : transient_Attorney_Note_Date.hashCode());
		result = prime * result + ((transient_Date == null) ? 0 : transient_Date.hashCode());
		result = prime * result + ((transient_Note == null) ? 0 : transient_Note.hashCode());
		result = prime * result + ((transient_Time == null) ? 0 : transient_Time.hashCode());
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
		if (!(obj instanceof File_Info))
			return false;
		File_Info other = (File_Info) obj;
		if (a_No == null) {
			if (other.a_No != null)
				return false;
		} else if (!a_No.equals(other.a_No))
			return false;
		if (already_viewed_by == null) {
			if (other.already_viewed_by != null)
				return false;
		} else if (!already_viewed_by.equals(other.already_viewed_by))
			return false;
		if (attorney_Note == null) {
			if (other.attorney_Note != null)
				return false;
		} else if (!attorney_Note.equals(other.attorney_Note))
			return false;
		if (case_access_request_status == null) {
			if (other.case_access_request_status != null)
				return false;
		} else if (!case_access_request_status.equals(other.case_access_request_status))
			return false;
		if (comments == null) {
			if (other.comments != null)
				return false;
		} else if (!comments.equals(other.comments))
			return false;
		if (company_Address == null) {
			if (other.company_Address != null)
				return false;
		} else if (!company_Address.equals(other.company_Address))
			return false;
		if (company_Name == null) {
			if (other.company_Name != null)
				return false;
		} else if (!company_Name.equals(other.company_Name))
			return false;
		if (docTypeName == null) {
			if (other.docTypeName != null)
				return false;
		} else if (!docTypeName.equals(other.docTypeName))
			return false;
		if (doc_Type_Id != other.doc_Type_Id)
			return false;
		if (fileAlert == null) {
			if (other.fileAlert != null)
				return false;
		} else if (!fileAlert.equals(other.fileAlert))
			return false;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (file_Id != other.file_Id)
			return false;
		if (file_Path == null) {
			if (other.file_Path != null)
				return false;
		} else if (!file_Path.equals(other.file_Path))
			return false;
		if (file_identifier == null) {
			if (other.file_identifier != null)
				return false;
		} else if (!file_identifier.equals(other.file_identifier))
			return false;
		if (filler == null) {
			if (other.filler != null)
				return false;
		} else if (!filler.equals(other.filler))
			return false;
		if (indexNum == null) {
			if (other.indexNum != null)
				return false;
		} else if (!indexNum.equals(other.indexNum))
			return false;
		if (is_Confidential == null) {
			if (other.is_Confidential != null)
				return false;
		} else if (!is_Confidential.equals(other.is_Confidential))
			return false;
		if (is_Intervene_Approved == null) {
			if (other.is_Intervene_Approved != null)
				return false;
		} else if (!is_Intervene_Approved.equals(other.is_Intervene_Approved))
			return false;
		if (is_Visible == null) {
			if (other.is_Visible != null)
				return false;
		} else if (!is_Visible.equals(other.is_Visible))
			return false;
		if (originalSubmissionDate == null) {
			if (other.originalSubmissionDate != null)
				return false;
		} else if (!originalSubmissionDate.equals(other.originalSubmissionDate))
			return false;
		if (po == null) {
			if (other.po != null)
				return false;
		} else if (!po.equals(other.po))
			return false;
		if (submission_Date == null) {
			if (other.submission_Date != null)
				return false;
		} else if (!submission_Date.equals(other.submission_Date))
			return false;
		if (submitter_Role == null) {
			if (other.submitter_Role != null)
				return false;
		} else if (!submitter_Role.equals(other.submitter_Role))
			return false;
		if (submitter_User_Id == null) {
			if (other.submitter_User_Id != null)
				return false;
		} else if (!submitter_User_Id.equals(other.submitter_User_Id))
			return false;
		if (time_stamp == null) {
			if (other.time_stamp != null)
				return false;
		} else if (!time_stamp.equals(other.time_stamp))
			return false;
		if (transient_Attorney_Note_Date == null) {
			if (other.transient_Attorney_Note_Date != null)
				return false;
		} else if (!transient_Attorney_Note_Date.equals(other.transient_Attorney_Note_Date))
			return false;
		if (transient_Date == null) {
			if (other.transient_Date != null)
				return false;
		} else if (!transient_Date.equals(other.transient_Date))
			return false;
		if (transient_Note == null) {
			if (other.transient_Note != null)
				return false;
		} else if (!transient_Note.equals(other.transient_Note))
			return false;
		if (transient_Time == null) {
			if (other.transient_Time != null)
				return false;
		} else if (!transient_Time.equals(other.transient_Time))
			return false;
		return true;
	}

	/**
	 * @return the transient_Comments
	 */
	public String getTransient_Comments() {
		return transient_Comments;
	}

	/**
	 * @param transient_Comments the transient_Comments to set
	 */
	public void setTransient_Comments(String transient_Comments) {
		this.transient_Comments = transient_Comments;
	}

	/**
	 * @return the transient_Comments_Date
	 */
	public String getTransient_Comments_Date() {
		return transient_Comments_Date;
	}

	/**
	 * @param transient_Comments_Date the transient_Comments_Date to set
	 */
	public void setTransient_Comments_Date(String transient_Comments_Date) {
		this.transient_Comments_Date = transient_Comments_Date;
	}

	/**
	 * @return the isCommentEdited
	 */
	public String getIsCommentEdited() {
		return isCommentEdited;
	}

	/**
	 * @param isCommentEdited the isCommentEdited to set
	 */
	public void setIsCommentEdited(String isCommentEdited) {
		this.isCommentEdited = isCommentEdited;
	}

	/**
	 * @return the isAttorneyNoteEdited
	 */
	public String getIsAttorneyNoteEdited() {
		return isAttorneyNoteEdited;
	}

	/**
	 * @param isAttorneyNoteEdited the isAttorneyNoteEdited to set
	 */
	public void setIsAttorneyNoteEdited(String isAttorneyNoteEdited) {
		this.isAttorneyNoteEdited = isAttorneyNoteEdited;
	}
	

	
}
