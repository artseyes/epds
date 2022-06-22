package gov.gao.epds.auth.test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.EnglishSequenceData;
import org.passay.IllegalSequenceRule;
import org.passay.LengthRule;

import gov.gao.epds.auth.utils.Util;

public class JavaCodeTester {

	public static void main(String[] args) {
		/*testTimeStamp();*/
		
		// password must be between 12 and 24 chars long
				LengthRule lengthRule = new LengthRule(12, 24);

				// don't allow whitespace
				org.passay.WhitespaceRule whitespaceRule = new org.passay.WhitespaceRule();

				List<CharacterRule> rules = Arrays.asList(
						// at least one upper-case character
						new CharacterRule(EnglishCharacterData.UpperCase, 1),

						// at least one lower-case character
						new CharacterRule(EnglishCharacterData.LowerCase, 1),

						// at least one digit character
						new CharacterRule(EnglishCharacterData.Digit, 1),

						//at lease one special character from list of chars
						new CharacterRule(new org.passay.CharacterData() {
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

				// group all rules together in a List
				List<org.passay.Rule> ruleList = new ArrayList<org.passay.Rule>();
				ruleList.add(lengthRule);
				ruleList.add(whitespaceRule);
				ruleList.add(charRule);
				ruleList.add(alphaSeqRule);
				ruleList.add(numSeqRule);
				ruleList.add(qwertySeqRule);
				ruleList.add(repeatRule);
				

	}

	private static void testTimeStamp() {
		Calendar calendar1 = Calendar.getInstance();
		Timestamp timestamp1 = new Timestamp(calendar1.getTimeInMillis());
		System.out.println("Timestamp1: " + timestamp1);

		Calendar calendar2 = Calendar.getInstance();
		calendar2.add(Calendar.DAY_OF_MONTH, 5);
		Timestamp timestamp2 = new Timestamp(calendar2.getTimeInMillis());
		System.out.println("Timestamp2: " + timestamp2);

		Date date1 = new Date(timestamp1.getTime());
		Date date2 = new Date(timestamp2.getTime());

		/*System.out.println("Number of days between: "
				+ daysBetween(date1, date2));*/

		System.out.println(getNumOfDaysInBetween(timestamp1, timestamp2));
	}

	private static int getNumOfDaysInBetween(Timestamp timestamp1,
			Timestamp timestamp2) {
		return (int) ((timestamp2.getTime() - timestamp1.getTime()) / (1000 * 60 * 60 * 24));
	}

	public static int daysBetween(Date d1, Date d2) {
		return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
	}

}
