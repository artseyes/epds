/**
 * 
 */
package gov.gao.epds.dto;

/**
 * @author MHussaini
 *
 */
public class FileStorage {

	private String inputFilePath;
	private String encryptedFilePath;
	private String decryptedFilePath;

	private Boolean isOutputFileNeedsTobeCreated;
	
	

	/**
	 * @return the inputFilePath
	 */
	public String getInputFilePath() {
		return inputFilePath;
	}

	/**
	 * @param inputFilePath the inputFilePath to set
	 */
	public void setInputFilePath(String inputFilePath) {
		this.inputFilePath = inputFilePath;
	}
	
	
	/**
	 * @return the encryptedFilePath
	 */
	public String getEncryptedFilePath() {
		return encryptedFilePath;
	}

	/**
	 * @param encryptedFilePath the encryptedFilePath to set
	 */
	public void setEncryptedFilePath(String encryptedFilePath) {
		this.encryptedFilePath = encryptedFilePath;
	}

	/**
	 * @return the decryptedFilePath
	 */
	public String getDecryptedFilePath() {
		return decryptedFilePath;
	}

	/**
	 * @param decryptedFilePath the decryptedFilePath to set
	 */
	public void setDecryptedFilePath(String decryptedFilePath) {
		this.decryptedFilePath = decryptedFilePath;
	}

	public Boolean getIsOutputFileNeedsTobeCreated() {
		return isOutputFileNeedsTobeCreated;
	}
	
	public void setIsOutputFileNeedsTobeCreated(Boolean isOutputFileNeedsTobeCreated) {
		this.isOutputFileNeedsTobeCreated = isOutputFileNeedsTobeCreated;
	}
	
	

	
}
