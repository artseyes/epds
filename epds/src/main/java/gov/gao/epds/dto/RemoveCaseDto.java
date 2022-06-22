/**
 * 
 */
package gov.gao.epds.dto;

import javax.validation.constraints.Pattern;

/**
 * @author MHussaini
 *
 */
public class RemoveCaseDto extends DTOValidator {

	@Pattern(regexp = PROTEST_ID_PATTERN, message = "Invalid input format!")
	private String aNum;
	@Pattern(regexp = COMMENTS_PATTERN, message = "Invalid input format!")
	private String reasonForDeletion;

	/**
	 * @return the aNum
	 */
	public String getaNum() {
		return aNum;
	}

	/**
	 * @param aNum
	 *            the aNum to set
	 */
	public void setaNum(String aNum) {
		this.aNum = aNum;
	}

	/**
	 * @return the reasonForDeletion
	 */
	public String getReasonForDeletion() {
		return reasonForDeletion;
	}

	/**
	 * @param reasonForDeletion
	 *            the reasonForDeletion to set
	 */
	public void setReasonForDeletion(String reasonForDeletion) {
		this.reasonForDeletion = reasonForDeletion;
	}

}
