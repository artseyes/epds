/**
 * 
 */
package gov.gao.epds.auth.dto;

/**
 * @author MHussaini
 *
 *To make all the evrything accepted ".*"
 *
 *\w stands for "word character", usually [A-Za-z0-9_]. Notice the inclusion of the underscore and digits.
 */
public abstract class DTOValidator {

	protected static final String IPADDRESS_PATTERN = 
			"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	
	protected static final String SECQID_PATTERN = "^[0-9]{1,3}$";
	
	public static final String AGENCYID_PATTERN = "^[0-9a-zA-z ]{1,20}$";
	
	protected static final String ROLE_PATTERN = "^[a-zA-Z ]{1,255}$";
	
	protected static final String GROUP_NO_PATTERN = "^[0-7 ]{1}$";
	
	protected static final String PREFIX_SUFFIX_PATTERN = "^[a-zA-Z0-9. ]{0,10}$";
	
	protected static final String FIRSTNAME_LASTNAME_PATTERN = "^[a-zA-Z0-9 \\-]{0,25}$";
	
	protected static final String MIDDLE_INITIAL_PATTERN = "^[a-zA-Z0-9 ]{1}$";
	
	protected static final String USER_ID_PATTERN = "^[0-9 ]{1,10}$";;
	
	public static final String NAME_OF_FIRM_PATTERN = "[\\w!\"\\#&'()*+, \\-./:;<=>?@\\[ \\\\\\]_]+";
	
	protected static final String AUTH_ROLE_ID_PATTERN = "^[0-3 ]{1}$";
	
	protected static final String EPDS_ROLE_ID_PATTERN = "^[0-9 ]{1}*$";
	
	protected static final String ACCOUNT_STATUS_ID_PATTERN = "^[0-8 ]{1}*$";
	
	protected static final String GAO_ID_PATTERN = "^[0-9 ]{1,10}*$";
	
	public static final String INTEGER_PATTERN = "^[0-9 ]*$";
	
	public static final String ALPHA_PATTERN = "(?i)[a-zA-Z0-9 ]+";
	
	public static final String ATTACHMENT_TYPE_PATTERN = "^[a-zA-Z0-9-\\._ ]*$";
	
	public static final String TYPE_OF_DOCUMENT_PATTERN = "^$|[\\w!\"\\&'*+, \\-./:;<=>?\\[ \\\\\\]_]+";

	public static final String IS_DOC_CONFIDENTIAL_PATTERN = "^[a-zA-Z ]{1,3}$";
	
	public static final String PROTEST_ID_PATTERN = "^[a-zA-Z0-9 -/\\.]{0,25}$";
	
	public static final String COMMENTS_PATTERN = "^$|[\\w!#&'()*+, -/\\.:;<=>?@ (?s).*[\n\r].*]+"; /*".*";*/
	
	public static final String SOLICITATION_PATTERN = "^[\\w-()/]{0,25}$";
	
	protected static final String ZIPCODE_PATTERN = "[\\w-()]+";
	
	protected static final String COUNTRY_PATTERN = "^$|[\\w!\"\\#&'()*+, \\-./:;<=>?@\\[ \\\\\\]_]+";
	
	protected static final String ADDRESS_PATTERN = "^$|[\\w!\"\\#&'()*+, \\-./:;<=>?@\\[ \\\\\\]_]+";
	
	protected static final String CITY_PATTERN = "^$|[\\w!\"\\#&'()*+, \\-./:;<=>?@\\[ \\\\\\]_]+";
	
	protected static final String STATE_PATTERN = "^$|[\\w!\"\\#&'()*+, \\-./:;<=>?@\\[ \\\\\\]_]+";
	
	protected static final String CONTACT_NUM_PATTERN = "^$|[\\w!\"\\#&'()*+, \\-./:;<=>?@\\[ \\\\\\]_]+";;
	
	public static final String FILE_NAME_PATTERN = "[\\w#()-.& ]+";
	
	public static final String FILE_IDENTIFIER_PATTERN = "^[AP]{1}+$";;
	
	
	public static final String EMAIL_PATTERN ="^[a-zA-Z0-9\\.'_+-]+@([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4})$";
	
}
