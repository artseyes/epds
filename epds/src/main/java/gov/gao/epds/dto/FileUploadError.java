package gov.gao.epds.dto;

import java.io.Serializable;

public class FileUploadError implements Serializable{

	
	private static final long serialVersionUID = -7021388041712413675L;

	private String fileName;
	private String fileError;
	
	private String fileIdentifierCode;
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileError() {
		return fileError;
	}
	public void setFileError(String fileError) {
		this.fileError = fileError;
	}
	public String getFileIdentifierCode() {
		return fileIdentifierCode;
	}
	public void setFileIdentifierCode(String fileIdentifierCode) {
		this.fileIdentifierCode = fileIdentifierCode;
	}
}
