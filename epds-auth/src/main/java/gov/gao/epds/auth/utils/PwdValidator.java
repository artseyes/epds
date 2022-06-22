package gov.gao.epds.auth.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.passay.EnglishCharacterData;
import org.passay.EnglishSequenceData;
import org.passay.IllegalRegexRule;
import org.passay.IllegalSequenceRule;

public class PwdValidator {

	public static List<String> validatePassword(String password,
			String password_history) {
		List<String> validationResultMessages = new ArrayList<String>();

		validationResultMessages = setInvalidMessageIfPasswordRepeated(
				password, password_history, validationResultMessages);

		List<org.passay.Rule> ruleList = getPasswordRules();

		org.passay.PasswordValidator validator = new org.passay.PasswordValidator(ruleList);
		org.passay.PasswordData passwordData = new org.passay.PasswordData(password);
		org.passay.RuleResult result = validator.validate(passwordData);
		if (result.isValid()) {
			
			if (validationResultMessages.size() <= 0){
				validationResultMessages.add("Valid password");
			}
		} else {
			validationResultMessages.add("Invalid password");
			validationResultMessages.addAll(validator.getMessages(result));
		}

		return validationResultMessages;
	}

	/**
	 * @return
	 */
	public static List<org.passay.Rule> getPasswordRules() {
		
		// password must be between 12 and 24 chars long
		org.passay.LengthRule lengthRule = new org.passay.LengthRule(12, 24);

		// don't allow whitespace
		org.passay.WhitespaceRule whitespaceRule = new org.passay.WhitespaceRule();

		List<org.passay.CharacterRule> rules = Arrays.asList(
				// at least one upper-case character
				new org.passay.CharacterRule(EnglishCharacterData.UpperCase, 1),

				// at least one lower-case character
				new org.passay.CharacterRule(EnglishCharacterData.LowerCase, 1),

				// at least one digit character
				new org.passay.CharacterRule(EnglishCharacterData.Digit, 1),

				//at lease one special character from list of chars
				new org.passay.CharacterRule(new org.passay.CharacterData() {
					@Override
					public String getErrorCode() {
						return "INVALID_SPECIAL_CHARS";
					}

					@Override
					public String getCharacters() {
						return Util.SPECIALCHARACTERS;
					}
				}, 1));
				
		// control allowed characters
		org.passay.CharacterCharacteristicsRule charRule = new org.passay.CharacterCharacteristicsRule();
		
		charRule.setNumberOfCharacteristics(4);
		charRule.setRules(rules);
		
		// don't allow alphabetical sequences
		IllegalSequenceRule alphaSeqRule = new IllegalSequenceRule(EnglishSequenceData.Alphabetical,3,true);

		// don't allow numerical sequences of length 3
		IllegalSequenceRule numSeqRule = new IllegalSequenceRule(EnglishSequenceData.Numerical,3,true);

		// don't allow qwerty sequences
		IllegalSequenceRule qwertySeqRule = new IllegalSequenceRule(EnglishSequenceData.USQwerty,3,true);

		// don't allow 4 repeat characters
		org.passay.RepeatCharacterRegexRule repeatRule = new org.passay.RepeatCharacterRegexRule(4);
		
		
        IllegalRegexRule illegalRegexRule = new IllegalRegexRule(
        	    "[^a-zA-Z0-9!#$\\*\\-%\\+=?:;~]");

		// group all rules together in a List
		List<org.passay.Rule> ruleList = new ArrayList<org.passay.Rule>();
		ruleList.add(lengthRule);
		ruleList.add(whitespaceRule);
		ruleList.add(charRule);
		ruleList.add(alphaSeqRule);
		ruleList.add(numSeqRule);
		ruleList.add(qwertySeqRule);
		ruleList.add(repeatRule);
		ruleList.add(illegalRegexRule);
		/*// password must be between 12 and 24 chars long
		LengthRule lengthRule = new LengthRule(12, 24);

		// don't allow whitespace
		WhitespaceRule whitespaceRule = new WhitespaceRule();

		// control allowed characters
		CharacterCharacteristicsRule charRule = new CharacterCharacteristicsRule();
		// require at least 1 digit in passwords
		charRule.getRules().add(new DigitCharacterRule(1));
		// require at least 1 non-alphanumeric char
		charRule.getRules().add(new NonAlphanumericCharacterRule(1));
		// require at least 1 upper case char
		charRule.getRules().add(new UppercaseCharacterRule(1));
		// require at least 1 lower case char
		charRule.getRules().add(new LowercaseCharacterRule(1));
		// require at least 4 of the previous rules be met
		charRule.setNumberOfCharacteristics(4);

		// don't allow alphabetical sequences
		AlphabeticalSequenceRule alphaSeqRule = new AlphabeticalSequenceRule(3,
				true);

		// don't allow numerical sequences of length 3
		NumericalSequenceRule numSeqRule = new NumericalSequenceRule(3, true);

		// don't allow qwerty sequences
		QwertySequenceRule qwertySeqRule = new QwertySequenceRule(3, true);

		// don't allow 4 repeat characters
		RepeatCharacterRegexRule repeatRule = new RepeatCharacterRegexRule(4);

		// group all rules together in a List
		List<Rule> ruleList = new ArrayList<Rule>();
		ruleList.add(lengthRule);
		ruleList.add(whitespaceRule);
		ruleList.add(charRule);
		ruleList.add(alphaSeqRule);
		ruleList.add(numSeqRule);
		ruleList.add(qwertySeqRule);
		ruleList.add(repeatRule);
		return ruleList;*/
		
		return ruleList;
	}

	
	/**
	 * When we are validating the password we encode the password in Base 64 but we dont hash the password 
	 * but in the password history we are storing the sha512 hashed password. Because of that when we get the password 
	 * during password validation we decode the base64Encoded password then generate sha512 hash and compare it with 
	 * the each password  in the password history. 
	 * 
	 * @param password
	 * @param password_history
	 * @param validationResultMessages
	 * @return
	 */
	private static List<String> setInvalidMessageIfPasswordRepeated(
			String password, String password_history,
			List<String> validationResultMessages) {
		
		
		if (password_history != null) {
			String[] oldPasswords = password_history.split(";");

			for (String eachOldPassword : oldPasswords) {
				
				if (eachOldPassword.equalsIgnoreCase(PasswordUtil.generateHash(password))) {
					validationResultMessages.add("Invalid password");
					validationResultMessages.add("Old password is not allowed");

					break;
				}
			}
		}

		return validationResultMessages;
	}
}
