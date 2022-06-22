package gov.gao.epds.dto;


public class UploadedFileIdentifier {

	private String fileIdentifierCode;
	private String filePath;
	
	
	
	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return filePath;
	}
	/**
	 * @param filePath the filePath to set
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	/**
	 * @return the fileIdentifierCode
	 */
	public String getFileIdentifierCode() {
		return fileIdentifierCode;
	}
	/**
	 * @param fileIdentifierCode the fileIdentifierCode to set
	 */
	public void setFileIdentifierCode(String fileIdentifierCode) {
		this.fileIdentifierCode = fileIdentifierCode;
	}
	
	
}
