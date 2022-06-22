package gov.gao.epds.dto;

import java.io.Serializable;

import javax.validation.constraints.Pattern;

public class UpdateComments extends DTOValidator implements Serializable{

	
	private static final long serialVersionUID = -7086571359503558893L;
	
	@Pattern(regexp = COMMENTS_PATTERN, message="Invalid input format!")
	private String new_Comments;
	
	@Pattern(regexp = INTEGER_PATTERN, message="Invalid input format!")
	private String file_Id;
	
	
	public String getNew_Comments() {
		return new_Comments;
	}
	public void setNew_Comments(String new_Comments) {
		this.new_Comments = new_Comments;
	}
	public String getFile_Id() {
		return file_Id;
	}
	public void setFile_Id(String file_Id) {
		this.file_Id = file_Id;
	}

}
