package gov.gao.epds.auth.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gov.gao.epds.auth.dto.AccountActivity;
import gov.gao.epds.auth.dto.Email;
import gov.gao.epds.auth.dto.ServiceResponse;
import gov.gao.epds.auth.persistence.entity.User_info;
import gov.gao.epds.auth.utils.PolicyParam;
import gov.gao.epds.auth.utils.Util;


/*https://www.emailonacid.com/character-converter*/
@Service
public class EmailService {
	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

//	private final String plcgEmail = "protests@cbca.gov";
	private final String plcgEmail = "cbca.systemreport@gsa.gov";

	@Resource(mappedName="java:jboss/mail/Default")
    private Session mailSession;

	/**
	 *
	 * @param user_info
	 * @param temporaryPassword
	 * @return
	 */
	public ServiceResponse emailTemporaryPasswordToUser(User_info user_info,
			String temporaryPassword) {

		String email = user_info.getEmail();
		String activityType = "";
		switch (user_info.getAccount_status_id()) {
		case 1:
			activityType = "CREATED";
			break;
		case 4:
			activityType = "LOCKED";
					break;
		case 6:
			activityType = "DEACTIVATED";
			break;
		case 7:
			activityType = "RESET";
			break;
		default:
			activityType = "TEMPORARY PASSWORD";
			break;
		}
		ServiceResponse serviceResponse = new ServiceResponse();
		AccountActivity accountActivity = new AccountActivity();
		accountActivity.setActivityType(activityType.toLowerCase(Locale.ENGLISH));

		Email dto = new Email();

		dto.setAccountActivity(accountActivity);

		dto.setTemPwd(temporaryPassword);

		String emailBody = this.getTheContentOfEmailBody("temPwd.tpl.html",dto);
		dto.setEmailBody(emailBody);
		try {
			Message message = new MimeMessage(mailSession);
			Address fromAddress = new InternetAddress("cbca.systemreport@gsa.gov", "EDS");
			message.setFrom(fromAddress);
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
			message.setSubject("EDS:  " + "ACCOUNT " + (!("Temporary Password").equalsIgnoreCase(activityType) ? activityType : " RESET"));
			message.setText(dto.getEmailBody().toString());
			message.setContent(dto.getEmailBody(),"text/html");
			Transport.send(message);
			serviceResponse.setIsSuccess(true);
		} catch (Exception e) {
			logger.error("emailTemporaryPasswordToUser exception: " + Util.getStackTraceMessage(e));
			serviceResponse.setException(Util.getStackTraceMessage(e));
			serviceResponse.setIsSuccess(false);
		} finally {

		}

		return serviceResponse;
	}

	public ServiceResponse notifyAccountActivityToSysAdmins(String email, String accountActivityType) {

		ServiceResponse serviceResponse = new ServiceResponse();
		List<String> sysAdmsEmail = new ArrayList<String>();

		//sysAdmsEmail.add("EPDS_Security@ocio.usda.gov");
		//sysAdmsEmail.add("EPDS_Admins@ocio.usda.gov");
		sysAdmsEmail.add("Charles.Otoupalik@usda.gov");
		sysAdmsEmail.add("arthur.hawkins@gsa.gov");
		// add Cody Martin?
//		sysAdmsEmail.add("Beth.Dowsett@ocio.usda.gov");
//		sysAdmsEmail.add("Kenneth.Moten@ocio.usda.gov");
//		sysAdmsEmail.add("Ray.Ellis@ocio.usda.gov");
		//sysAdmsEmail.add("Mohammed.Hussaini@ocio.usda.gov");
//		sysAdmsEmail.add("OCIO-EAS-EPDS-AUDIT@usda.gov");

		Email dto = new Email();
		dto.setTemPwd("");
		AccountActivity accountActivity = new AccountActivity();
		accountActivity.setEmail(email);
		accountActivity.setTimeStamp(DateTime.now(DateTimeZone.forID("America/Los_Angeles")));
		accountActivity.setActivityType(accountActivityType);
		dto.setAccountActivity(accountActivity);
		String emailBody = this.getTheContentOfEmailBody("accountActivity.tpl.html",dto);
		dto.setEmailBody(emailBody);

		InternetAddress[] toMailAddresses = getListOfInternetEmailAddress(sysAdmsEmail);
		try {
			Message message = new MimeMessage(mailSession);
			Address fromAddress = new InternetAddress("cbca.systemreport@gsa.gov", "EPDS");
			message.setFrom(fromAddress);
			message.setRecipients(Message.RecipientType.BCC, toMailAddresses);
			message.setSubject("EDS:  Account Activity");
			message.setText(dto.getEmailBody().toString());
			message.setContent(dto.getEmailBody(),"text/html");
			Transport.send(message);
			serviceResponse.setIsSuccess(true);
		} catch (Exception e) {
			logger.error("notifyAccountActivityToSysAdmins exception: " + Util.getStackTraceMessage(e));
			serviceResponse.setException(Util.getStackTraceMessage(e));
			serviceResponse.setIsSuccess(false);
		} finally {

		}

		return serviceResponse;
	}

	public  ServiceResponse testEmail() {
		ServiceResponse serviceResponse = new ServiceResponse();

		/*Email dto = new Email();
		dto.setTemPwd("This is a test");
		String emailBody = getTheContentOfEmailBody("temPwd.tpl.html",dto);
		dto.setEmailBody(emailBody);

		try {
			Message message = new MimeMessage(mailSession);
			Address fromAddress = new InternetAddress("epdssystem@gmail.com", "EDS");
			message.setFrom(fromAddress);
			message.setRecipient(Message.RecipientType.BCC, new InternetAddress("amer_hussaini@yahoo.com"));
			message.setSubject("EDS:  Account Activity");
			message.setText("Text");
			message.setContent(dto.getEmailBody(),"text/html");
			Transport.send(message);
			serviceResponse.setIsSuccess(true);
		} catch (Exception e) {
			serviceResponse.setException(Util.getStackTraceMessage(e));
			serviceResponse.setIsSuccess(false);
		} finally {

		}
*/
		sendAccountDeletionWarningOrConfirmation("amer_hussaini@yahoo.com", 5, "warning");
		return serviceResponse;
	}


	public void sendAccountDeletionWarningOrConfirmation(String email, int numOfDaysLeft, String actionType) {

		Email dto = new Email();
		dto.setNumOfDaysLeft(numOfDaysLeft);
		String emailBody = "";
		
		AccountActivity accountActivity = new AccountActivity();
		accountActivity.setEmail(email);


		dto.setAccountActivity(accountActivity);
		
		if (actionType.equalsIgnoreCase("warning")){
			emailBody = getTheContentOfEmailBody("accountDeletionWarning.html",dto);
		}else{
			emailBody = getTheContentOfEmailBody("accountDeletionConfirmation.html",dto);
		}
		dto.setEmailBody(emailBody);

		try {
			Message message = new MimeMessage(mailSession);
			Address fromAddress = new InternetAddress("cbca.systemreport@gsa.gov", "EDS");
			message.setFrom(fromAddress);
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
			message.setSubject("EDS:  ACCOUNT DELETION  " + actionType.toUpperCase(Locale.ENGLISH));
			message.setText(dto.getEmailBody().toString());
			message.setContent(dto.getEmailBody(),"text/html");
			Transport.send(message);
		} catch (Exception e) {
			logger.error("sendAccountDeletionWarningOrConfirmation exception: " + Util.getStackTraceMessage(e));
			e.printStackTrace();
		} 

		return;
	}
	
	public boolean notifyLockStatusToAgencyAndMayBePLCG(List<String> agencyPOCEmails,
			User_info user_info) {
		if (agencyPOCEmails == null || agencyPOCEmails.size() == 0) {
			return false;
		}



		StringBuilder emailBody = new StringBuilder();
		emailBody.append("<html>")
		     .append("<head>")
		     .append("<meta charset=\"UTF-8\">")
		     .append("</head> ")
		     .append("<body>")
		     .append("<span><strong>***********Please do not reply to this message. Replies to this message are routed to an unmonitored mailbox.**************</strong></span>")
		     .append("	<p>This email is an automated notification,")
		     .append("	 which is unable to receive replies.")
		     .append("	  For assistance, please contact CBCA Clerk at ")
		     .append("	  (202) 606-8800 or clerk@cbca.gov.</p>")
		     .append("	<br/>")
		     .append("	<p>The following user's account has been locked. </p>	")
		     .append("	<table>")
		     .append("		<tbody>")
		     .append("			<tr>")
		     .append("				<td><strong>Name : </strong></td>")
		     .append("				<td><strong>" + user_info.getFirst_name() + "  " + user_info.getLast_name() + "</strong></td>")
		     .append("			</tr>")
		     .append("		</tbody>")
		     .append("	</table>")
		     .append("	<p>To unlock the account and have a temporary password sent to <strong>" + user_info.getFirst_name() + "  " + user_info.getLast_name() + "</strong>, an Agency POC must contact GAO at protests@cbca.gov or (202) 512-5436.</p>")
		     .append("  <br />" + GetFooterText())
             .append("</body>   ")
		     .append("</html>");

		Email dto = new Email();
		dto.setTemPwd("");
		dto.setAgencyRepFirstName(user_info.getFirst_name());
		dto.setAgencyRepLastName(user_info.getLast_name());


		try {
			boolean isUserAgencyRep = !Util.checkIfElementIsAlreadyInList(user_info.getEmail(), agencyPOCEmails);

			Message message = new MimeMessage(mailSession);
			Address fromAddress = new InternetAddress("cbca.systemreport@gsa.gov", "EDS");
			message.setFrom(fromAddress);
			populateToAndCCFieldForAgencyUser(message, isUserAgencyRep, agencyPOCEmails, user_info.getEmail());
			message.setSubject("EDS:  ACCOUNT LOCKED");
			message.setText(emailBody.toString());
			message.setContent(emailBody.toString(),"text/html");
			Transport.send(message);

			return true;
		} catch (Exception e) {
			return false;
		}

	}


	private void populateToAndCCFieldForAgencyUser(Message message,
			boolean isUserAgencyRep, List<String> agencyPOCEmails, String lockedUserEmail) throws MessagingException {

		List<String> ccEmailAddressList = new ArrayList<String>();
		List<String> toEmailAddressList = new ArrayList<String>();

		if(isUserAgencyRep){

			for(int i = 0; i<agencyPOCEmails.size(); i++){
				toEmailAddressList.add(agencyPOCEmails.get(i));
			}

		}else{

			for(int i =0; i<agencyPOCEmails.size(); i++){
				String agencyPOCEmail = agencyPOCEmails.get(i);
				if(!agencyPOCEmail.equalsIgnoreCase(lockedUserEmail)){
					ccEmailAddressList.add(agencyPOCEmail);
				}
			}
		}

		ccEmailAddressList.add(lockedUserEmail);
		ccEmailAddressList.add(plcgEmail);

		InternetAddress[] toMailAddresses = getListOfInternetEmailAddress(toEmailAddressList);
		InternetAddress[] ccMailAddresses = getListOfInternetEmailAddress(ccEmailAddressList);

		message.setRecipients(Message.RecipientType.TO, toMailAddresses);
		message.setRecipients(Message.RecipientType.CC, ccMailAddresses);

	}

	/**
	 * @param toEmailAddressList
	 * @return
	 */
	private InternetAddress[] getListOfInternetEmailAddress(List<String> toEmailAddressList) {


		InternetAddress[] toMailAddresses = new InternetAddress[toEmailAddressList.size()];

		for (int i = 0; i < toEmailAddressList.size(); i++){

			try {
				toMailAddresses[i] = new InternetAddress(toEmailAddressList.get(i));
			} catch (AddressException e) {
				e.printStackTrace();
			}
        }
		return toMailAddresses;
	}


	public boolean notifyLockStatusToGAOUserAndMaybeEASSupport(List<String> gaoAdminUsers, User_info user_info) {
		if (gaoAdminUsers == null || gaoAdminUsers.size() == 0) {
			return false;
		}

		boolean isUserNonGAOAdmin = !Util.checkIfElementIsAlreadyInList(user_info.getEmail(), gaoAdminUsers);

		Email dto = new Email();
		dto.setTemPwd("");
		dto.setAgencyRepFirstName(user_info.getFirst_name());
		dto.setAgencyRepLastName(user_info.getLast_name());

		try {
			Message message = new MimeMessage(mailSession);
			Address fromAddress = new InternetAddress("cbca.systemreport@gsa.gov", "EDS");
			message.setFrom(fromAddress);
			populateToAndCCFieldForGAOUser(message, isUserNonGAOAdmin, gaoAdminUsers, user_info.getEmail());
			message.setSubject("EDS:  ACCOUNT LOCKED");

			if(isUserNonGAOAdmin){
				message.setText("The following CBCA user's EDS account has been locked:"
						+ user_info.getFirst_name()
						+ " "
						+ user_info.getLast_name()
						+ ".  To unlock the account and have a temporary password resent to the individual whose account has been locked, please contact a member of CBCA IT shop for assistance."
				        + GetFooterText());
			}else{
				message.setText("The following CBCA user's EDS account has been locked:"
						+ user_info.getFirst_name()
						+ " "
						+ user_info.getLast_name()
						+ ".  To unlock the account and have a temporary password resent to the individual whose account has been locked, please contact a member of CBCA IT shop for assistance. "
						+ GetFooterText());
			}
			Transport.send(message);

			return true;
		} catch (Exception e) {
			return false;
		}
	}



	private void populateToAndCCFieldForGAOUser(Message message,
			boolean isUserNonGAOAdmin, List<String> gaoAdminUsers, String lockedUserEmail) throws MessagingException {


		List<String> ccEmailAddressList = new ArrayList<String>();
		List<String> toEmailAddressList = new ArrayList<String>();


		if(isUserNonGAOAdmin){
			String[] gaoAdminUsersInArray = new String[gaoAdminUsers.size()];
			gaoAdminUsers.toArray(gaoAdminUsersInArray);

			for(int i = 0; i<gaoAdminUsers.size(); i++){
				toEmailAddressList.add(gaoAdminUsers.get(i));
			}

		}else{
			toEmailAddressList.add(plcgEmail);
		}

		ccEmailAddressList.add(lockedUserEmail);


		InternetAddress[] toMailAddresses = getListOfInternetEmailAddress(toEmailAddressList);
		InternetAddress[] ccMailAddresses = getListOfInternetEmailAddress(ccEmailAddressList);

		message.setRecipients(Message.RecipientType.TO, toMailAddresses);
		message.setRecipients(Message.RecipientType.CC, ccMailAddresses);
	}


	public String getTheContentOfEmailBody(String templateName ,Email dto){

		String emailBody = "";
		InputStream inputStream = null;

		try {

			inputStream = Thread.currentThread()
					.getContextClassLoader().getResourceAsStream("emailTemplates/"+templateName);


			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

			StringBuffer buffer = new StringBuffer();
			String line;

			while((line = reader.readLine()) != null) {
			    buffer.append(line);
			}
			reader.close();

    		Map<String, Object> ReplacementMap = new HashMap<String, Object>();


    		try {
    			InetAddress IP = InetAddress.getLocalHost();

    			if (IP.toString().contains("159.142.165.49")) {
    				ReplacementMap.put("epds_base_url", "https://eds.cbca.gov");
    				ReplacementMap.put("non_Prod_Warn", "");
    			}else{
    				ReplacementMap.put("epds_base_url", "https://edstest.edc.usda.gov");
    				ReplacementMap.put("non_Prod_Warn", "<p><strong>MESSAGE GENERATED FROM NON PRODUCTION ENVIRONMENT</strong></p><br />");
    			}

    		}catch (Exception e) {
    			System.out.println("Problem getting current system IP");
    		}

    		if (templateName.equalsIgnoreCase("accountDeletionConfirmation.html")){
    			LocalDate accountExpiryTimeStamp = LocalDate.now();
    			
    			ReplacementMap.put("user_email_address", dto.getAccountActivity().getEmail());
    			ReplacementMap.put("account_expiry_timestamp", accountExpiryTimeStamp);
    		}else if (templateName.equalsIgnoreCase("accountDeletionWarning.html")){
    			LocalDate accountExpiryTimeStamp = LocalDate.now().plusDays(dto.getNumOfDaysLeft());
    			int numberOfDaysPassed = PolicyParam.numberOfDaysToRemoveAccount - dto.getNumOfDaysLeft();
    			ReplacementMap.put("user_email_address", dto.getAccountActivity().getEmail());
    			ReplacementMap.put("account_expiry_timestamp", accountExpiryTimeStamp);
    			ReplacementMap.put("number_Of_Days_Passed", numberOfDaysPassed);
    			ReplacementMap.put("number_Of_Days_To_Remove_Acct", PolicyParam.numberOfDaysToRemoveAccount);
    		}else if (templateName.equalsIgnoreCase("accountActivity.tpl.html")){
    			ReplacementMap.put("user_email", dto.getAccountActivity().getEmail());
    			ReplacementMap.put("time_Stamp", new Date());
    			ReplacementMap.put("activity_Type", dto.getAccountActivity().getActivityType());
    		}else if (templateName.equalsIgnoreCase("agency-account-locked.tpl.htm")){
    			ReplacementMap.put("agencyRepFirstName", dto.getAgencyRepFirstName());
    			ReplacementMap.put("agencyRepLastName", dto.getAgencyRepLastName());
    		}else{
    			ReplacementMap.put("temp_password", dto.getTemPwd());
    			if (!"Temporary Password".equalsIgnoreCase(dto.getAccountActivity().getActivityType())){
    				ReplacementMap.put("activity_type_message",dto.getAccountActivity().getActivityType() );
    			}else{
    				ReplacementMap.put("activity_type_message","reset");
    			}

    		}

    		ReplacementMap.put("Footer_Text", GetFooterText());

    		emailBody = buffer.toString();

    		for (Map.Entry<String, Object> entry : ReplacementMap.entrySet()) {

    		    emailBody = emailBody.replace(entry.getKey(), entry.getValue().toString());

    		}


		}catch (IOException e) {

			e.printStackTrace();
		} finally{
			IOUtils.closeQuietly(inputStream);
		}

		return emailBody;
	}

	public ServiceResponse sendUpdateAccountInfoAlertToOldAndNewEmailAddress(String newEmail, String old_email) {

		List<String> toMailAddressesList = new ArrayList<String>();
		toMailAddressesList.add(newEmail);
		toMailAddressesList.add(old_email);

		String emailBody  = "This is to notify you that you have recently updated your user name."
				+ " If you have not made this change, please contact CBCA at (202) 606-8800 or cbca.it@gsa.gov immediately."
		        + "<br /><br />"
				+ GetFooterText();

		ServiceResponse serviceResponse = new ServiceResponse();
		InternetAddress[] toMailAddresses = getListOfInternetEmailAddress(toMailAddressesList);
		try {
			Message message = new MimeMessage(mailSession);
			Address fromAddress = new InternetAddress("cbca.systemreport@gsa.gov", "EDS");
			message.setFrom(fromAddress);
			message.setRecipients(Message.RecipientType.BCC, toMailAddresses);
			message.setSubject("EDS:  Account Update");
			message.setText(emailBody);
			message.setContent(emailBody,"text/html");
			Transport.send(message);
			serviceResponse.setIsSuccess(true);
		} catch (Exception e) {
			logger.error("sendUpdateAccountInfoAlertToOldAndNewEmailAddress exception: " + Util.getStackTraceMessage(e));
			serviceResponse.setException(Util.getStackTraceMessage(e));
			serviceResponse.setIsSuccess(false);
		}


		return serviceResponse;

	}

   public String GetFooterText() {
        String footerText = "This electronic message contains information generated by the US Government solely "
                + "for the intended recipients. Any unauthorized interception of this message or the use or "
                + "disclosure of the information it contains may violate the law and subject the violator to civil "
                + "or criminal penalties. If you believe you have received this message in error, please notify the "
                + "sender and delete the email immediately. ";

        return footerText;
    }

   public static void main(String[] args) {
       new EmailService().testEmail();
   }



}
