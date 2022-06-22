/**
 * 
 */
package gov.gao.epds.dto;

import java.io.Serializable;

import javax.validation.constraints.Pattern;

/**
 * @author MHussaini
 *
 */
public class UpdateTypeOfDoc extends DTOValidator implements Serializable {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -4210379345585466650L;
	
	private Integer new_DocumentType_Id;
	
	@Pattern(regexp =INTEGER_PATTERN, message="Invalid input format!")
	private String file_Id;
	
	public Integer getNew_DocumentType_Id() {
		return new_DocumentType_Id;
	}
	public void setNew_DocumentType_Id(Integer new_DocumentType_Id) {
		this.new_DocumentType_Id = new_DocumentType_Id;
	}
	public String getFile_Id() {
		return file_Id;
	}
	public void setFile_Id(String file_Id) {
		this.file_Id = file_Id;
	}

}
