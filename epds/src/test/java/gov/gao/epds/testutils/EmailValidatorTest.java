/**
 * 
 */
package gov.gao.epds.testutils;

import org.apache.commons.validator.routines.EmailValidator;

/**
 * @author MHussaini
 *
 */
public class EmailValidatorTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EmailValidator validator = EmailValidator.getInstance();
		
		String email = "amer!##%%^&*Select*from$$justcheckinghowitworkslazyfoxis@gmail.com";
		
		System.out.println(validator.isValid(email));
		
		
		String regex = "^[a-zA-Z0-9_-]*$";
		
		String input = "B-123456.1";
		
		System.out.println(input.matches(regex));

	}

}
