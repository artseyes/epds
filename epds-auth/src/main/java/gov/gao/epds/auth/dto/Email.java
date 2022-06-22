package gov.gao.epds.auth.dto;

public class Email extends DTOValidator{
	private String temPwd;
	private String emailBody;
	private Integer numOfDaysLeft;
	private AccountActivity accountActivity;
	
	private String agencyRepFirstName;
	private String agencyRepLastName;

	/**
	 * @return the temPwd
	 */
	public String getTemPwd() {
		return temPwd;
	}

	/**
	 * @param temPwd
	 *            the temPwd to set
	 */
	public void setTemPwd(String temPwd) {
		this.temPwd = temPwd;
	}

	/**
	 * @return the emailBody
	 */
	public String getEmailBody() {
		return emailBody;
	}

	/**
	 * @param emailBody the emailBody to set
	 */
	public void setEmailBody(String emailBody) {
		this.emailBody = emailBody;
	}

	
	/**
	 * @return the accountActivity
	 */
	public AccountActivity getAccountActivity() {
		return accountActivity;
	}

	/**
	 * @param accountActivity the accountActivity to set
	 */
	public void setAccountActivity(AccountActivity accountActivity) {
		this.accountActivity = accountActivity;
	}

	public String getAgencyRepLastName() {
		return agencyRepLastName;
	}

	public void setAgencyRepLastName(String agencyRepLastName) {
		this.agencyRepLastName = agencyRepLastName;
	}

	public String getAgencyRepFirstName() {
		return agencyRepFirstName;
	}

	public void setAgencyRepFirstName(String agencyRepFirstName) {
		this.agencyRepFirstName = agencyRepFirstName;
	}

	public Integer getNumOfDaysLeft() {
		return numOfDaysLeft;
	}

	public void setNumOfDaysLeft(Integer numOfDaysLeft) {
		this.numOfDaysLeft = numOfDaysLeft;
	}
}
