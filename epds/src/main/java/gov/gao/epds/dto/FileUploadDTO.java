/**
 * 
 */
package gov.gao.epds.dto;

import java.io.Serializable;

import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author MHussaini
 *
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class FileUploadDTO extends DTOValidator implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6809151476574766547L;
	private String a_No;
	private String attachmentType;
	private int flowChunkSize;
	private long flowTotalSize;
	private String flowIdentifier;
	

	
	/*flowChunkNumber	1
	flowChunkSize	1000000
	flowCurrentChunkSize	292577
	flowTotalSize	292577
	flowIdentifier	ldhk
	flowFilename	Building the Data Sources.docx
	flowRelativePath	Building the Data Sources.docx
	flowTotalChunks	1
	attachmentType	comments
	fileIdentifierCode	P
	a_No	A-KB5CP*/
	
	@Pattern(regexp=FILE_IDENTIFIER_PATTERN, message="Inavlid Indentifier Code")
	private String fileIdentifierCode;
	
	@Pattern(regexp=FILE_NAME_PATTERN, message="Inavlid File Name")
	private String flowFilename;
	
	
	public String getA_No() {
		return a_No;
	}
	public void setA_No(String a_No) {
		this.a_No = a_No;
	}
	public String getAttachmentType() {
		return attachmentType;
	}
	public void setAttachmentType(String attachmentType) {
		this.attachmentType = attachmentType;
	}
	
	public String getFlowFilename() {
		return flowFilename;
	}
	public void setFlowFilename(String flowFilename) {
		this.flowFilename = flowFilename;
	}
	public String getFileIdentifierCode() {
		return fileIdentifierCode;
	}
	public void setFileIdentifierCode(String fileIdentifierCode) {
		this.fileIdentifierCode = fileIdentifierCode;
	}
	public int getFlowChunkSize() {
		return flowChunkSize;
	}
	public void setFlowChunkSize(int flowChunkSize) {
		this.flowChunkSize = flowChunkSize;
	}
	public long getFlowTotalSize() {
		return flowTotalSize;
	}
	public void setFlowTotalSize(long flowTotalSize) {
		this.flowTotalSize = flowTotalSize;
	}
	public String getFlowIdentifier() {
		return flowIdentifier;
	}
	public void setFlowIdentifier(String flowIdentifier) {
		this.flowIdentifier = flowIdentifier;
	}
	
	
	
}
