package gov.gao.epds.dto;

import java.io.Serializable;

import javax.validation.constraints.Pattern;

public class RenamFile extends DTOValidator implements Serializable {

	
	private static final long serialVersionUID = 1207134059903374076L;

	@Pattern(regexp =INTEGER_PATTERN, message="Invalid input format!")
	private String fileId;
	@Pattern(regexp = COMMENTS_PATTERN, message="Invalid input format!")
	private String fileName;
	
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}
