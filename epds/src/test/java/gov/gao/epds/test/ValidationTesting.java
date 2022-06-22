package gov.gao.epds.test;

import java.util.List;

import gov.gao.epds.dto.FileUploadDTO;
import gov.gao.epds.dto.InputValidationError;
import gov.gao.epds.dto.LoginDTO;
import gov.gao.epds.dto.ProtestInfoFormDTO;
import gov.gao.epds.dto.SubmitNewDocDTO;
import gov.gao.epds.dto.User_info_dto;
import gov.gao.epds.rest.auth.services.AuthUtil;

/**
 * @author MHussaini
 *
 */
public class ValidationTesting {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		User_info_dto user = new User_info_dto();
		
		
		user.setPrefix("Mr.");
		user.setFirstName("Mohammed-Amer-Hussaini");
		user.setLastName("Hussaini12-123!@");
		user.setGroupNo(3);
		user.setRole("Protester");
		user.setAccount_status_id(4);
		user.setAddress1("562 Lynn Ct Unit C ");
		user.setAddress2("Apt# c Suite # 214");
		user.setAnswer1("This is a test");
		user.setSeqQue1Id("9");
		user.setSeqQue2Id("12");
		user.setAuth_role_id(2);
		user.setAuth_token("");
		user.setCity("Glebard Street");
		user.setCountry("United States of America");
		user.setDesc("Description");
		user.setEmail("case.tracking@gsa.gov");
		user.setEpds_role_id(8);
		user.setZipCode("5000000001_123ABC()");
		user.setPhoneNo("(630) 290-6087");
		
		LoginDTO loginDTO = new LoginDTO();
		
		loginDTO.setEmail("case.tracking@gsa.gov");
		List<InputValidationError> constraintViolations = AuthUtil.validateDTO(user);
		
		/*for (InputValidationError cv : constraintViolations) {
		      System.out.println(String.format(
		          "Error here! property: [%s], value: [%s], message: [%s]",
		          cv.getFieldName(), cv.getInvalidValue(), cv.getMessage()));
		}*/
		
		ProtestInfoFormDTO protest = new ProtestInfoFormDTO();
		protest.setA_No("B-2236456.12");
		protest.setComments("This is a test           /n/r /n /n /n/n /n/n /n");
		
		constraintViolations = AuthUtil.validateDTO(protest);
		
		SubmitNewDocDTO submitNewDocDTO = new SubmitNewDocDTO();
		submitNewDocDTO.setComments("This is a test    4/25/2021       \n \n \n \n\n\n \n \n \n");
		
		constraintViolations = AuthUtil.validateDTO(submitNewDocDTO);
		
		for (InputValidationError cv : constraintViolations) {
		      System.out.println(String.format(
		          "Error here! property: [%s], value: [%s], message: [%s]",
		          cv.getFieldName(), cv.getInvalidValue(), cv.getMessage()));
		}
		
		
		
		/*FileUploadDTO dto = new FileUploadDTO();
		
		dto.setFileIdentifierCode("A");
		dto.setFlowFilename("word (3).doc");
		
		constraintViolations = AuthUtil.validateDTO(dto);
		
		for (InputValidationError cv : constraintViolations) {
		      System.out.println(String.format(
		          "Error here! property: [%s], value: [%s], message: [%s]",
		          cv.getFieldName(), cv.getInvalidValue(), cv.getMessage()));
		}*/
		
		
	}

	

}
