/**
 * 
 */
package gov.gao.epds.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author MHussaini
 *
 */
public class EditFileInfo extends DTOValidator implements Serializable {

	
	private static final long serialVersionUID = -3219281841199514443L;

	@NotNull
	@Pattern(regexp = INTEGER_PATTERN, message="Invalid input format!")
	private String file_Id;
	@NotNull
	/*@Pattern(regexp = COMMENTS_PATTERN, message="Invalid input format!")*/
	private String newValue;
	
	private String aNum;
	
	
	
	public String getFile_Id() {
		return file_Id;
	}
	public void setFile_Id(String file_Id) {
		this.file_Id = file_Id;
	}
	public String getNewValue() {
		return newValue;
	}
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
	
	/**
	 * @return the aNum
	 */
	public String getaNum() {
		return aNum;
	}
	/**
	 * @param aNum the aNum to set
	 */
	public void setaNum(String aNum) {
		this.aNum = aNum;
	}
	
	
	
}
