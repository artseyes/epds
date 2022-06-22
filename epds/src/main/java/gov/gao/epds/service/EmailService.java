package gov.gao.epds.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
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
import javax.servlet.http.HttpServletRequest;

import org.jsoup.safety.Whitelist;
import org.springframework.stereotype.Service;

import gov.gao.epds.dto.EmailNotification;
import gov.gao.epds.dto.SubmitNewDocDTO;
import gov.gao.epds.persistence.entity.File_Info;
import gov.gao.epds.persistence.entity.Protest_Info;
import gov.gao.epds.utils.Date_Util;
import gov.gao.epds.utils.Util;
import gov.gao.epds.utils.Util_Test;
import gov.gao.epds.utils.templates.TemplateUtils;

@Service
public class EmailService {

	/*@Autowired
	private MailSender mailSender;*/
//	private final String PLCGEMAIL = "protests@cbca.gov";
	private final String PLCGEMAIL = "cbca.systemreport@gsa.gov";

	/* private static Logger logger = Logger.getLogger(EmailSevice.class); */

	@Resource(mappedName="java:jboss/mail/Default")
    private Session mailSession;

	//File New Protest
	public void sendNoticeForFileNewProtest(HttpServletRequest request, Protest_Info protestInfo, List<String> listOfAgencyPOCsEmailAddresses) {

		Message message = new MimeMessage(mailSession);
		String  emailBody = "";

		try {

			emailBody = this
					.getTheContentOfEmailBody(new SubmitNewDocDTO(), request,"newProtest",protestInfo);

			InternetAddress[] agencyPOCsEmails = new InternetAddress[listOfAgencyPOCsEmailAddresses.size()];

			for (int i = 0; i < listOfAgencyPOCsEmailAddresses.size(); i++)
	        {
				agencyPOCsEmails[i] = new InternetAddress(listOfAgencyPOCsEmailAddresses.get(i));
	        }
			message.setFrom(this.getFromAddress());
			message.setRecipients(Message.RecipientType.TO, agencyPOCsEmails);
			message.setRecipient(Message.RecipientType.CC, new InternetAddress(protestInfo.getRepresentative_Email()));
			message.setRecipient(Message.RecipientType.BCC, new InternetAddress(PLCGEMAIL, "EDS"));
			message.setSubject("EDS:  NOTICE OF NEW PROTEST");
			message.setText(emailBody.toString());
			message.setContent(emailBody,"text/html");

			Transport.send(message);
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}


	}


	//Filing Other case types Ex: Recon,Ent,Cost-Claim

	public void sendNoticeForOtherCaseTypes(HttpServletRequest request, Protest_Info protestInfo, List<String> listOfEmailAddress) {

			Message message = new MimeMessage(mailSession);
			String  emailBody = "";

			try {

				emailBody = this
						.getTheContentOfEmailBody(new SubmitNewDocDTO(), request,"otherProtest",protestInfo);

				InternetAddress[] toMailAddresses = new InternetAddress[listOfEmailAddress.size()];

				for (int i = 0; i < listOfEmailAddress.size(); i++)
		        {
					toMailAddresses[i] = new InternetAddress(listOfEmailAddress.get(i));
		        }
				message.setFrom(this.getFromAddress());
				message.setRecipients(Message.RecipientType.TO, toMailAddresses);
				message.setRecipient(Message.RecipientType.BCC, new InternetAddress(PLCGEMAIL));

				if (protestInfo.getCase_Type().toUpperCase(Locale.ENGLISH).contains("RECON")){
					message.setSubject("EDS:  NOTICE OF NEW REQUEST FOR RECONSIDERATION");
				}else if (protestInfo.getCase_Type().toUpperCase(Locale.ENGLISH).contains("ENT")){
					message.setSubject("EDS:  NOTICE OF NEW REQUEST FOR ENTITLEMENT RECOMMENDATION");
				}else if (protestInfo.getCase_Type().toUpperCase(Locale.ENGLISH).contains("COST")){
					message.setSubject("EDS:  NOTICE OF NEW REQUEST FOR REIMBURSEMENT OF COSTS RECOMMNEDATION");
				}

				message.setText(emailBody.toString());
				message.setContent(emailBody,"text/html");

				Transport.send(message);
			} catch (AddressException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			}


		}

	//Payment Confirmation
	void sendPaymentConfirmationToProtester(HttpServletRequest request, Protest_Info protestInfo)  {

		String  emailBody = "";
		Message message = new MimeMessage(mailSession);

		Address toAddress;
		try {

			emailBody = this
					.getTheContentOfEmailBody(new SubmitNewDocDTO(), request,"paymentConfirmation",protestInfo);

			toAddress = new InternetAddress(protestInfo.getRepresentative_Email());
			message.setFrom(this.getFromAddress());
			message.setRecipient(Message.RecipientType.TO,toAddress);
			message.setSubject("EDS:  Confirmation of Payment of Filing Fee");
			message.setText(emailBody.toString());
			message.setContent(emailBody,"text/html");

			Transport.send(message);
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}


	}



	void sendMailtoAllPartiesAssocatedWithThisCase(
			Map<Protest_Info, List<String>> mapOfProtestInfoAndEmailAddresses,
			Map<String,String> mapOfBNumberToDocketEntryNumber,SubmitNewDocDTO submitNewDocDTO, HttpServletRequest request){
		try {

		String  emailBody = "";
		for (Map.Entry<Protest_Info, List<String>> entry : mapOfProtestInfoAndEmailAddresses
				.entrySet()) {

			String bNumText = entry.getKey().getB_No() != null ? entry.getKey().getB_No() + "--" : "";
			String companyNameAndTypeOfDoc = entry.getKey().getCompany_Name() + "--" + submitNewDocDTO.getTypeofdocument();

			Message message = new MimeMessage(mailSession);
			ArrayList<InternetAddress> toMailAddresses = new ArrayList<InternetAddress>();

			for (int i = 0; i < entry.getValue().size(); i++){
            	toMailAddresses.add(new InternetAddress(entry.getValue().get(i)));
            }

			if (!submitNewDocDTO.getTypeofdocument().equalsIgnoreCase("comments added")){
				submitNewDocDTO.setDocketEntryNumber(mapOfBNumberToDocketEntryNumber.get(entry.getKey().getB_No()));
			}

			if (submitNewDocDTO.getTypeofdocument().equalsIgnoreCase("Request for Dismissal")){
                toMailAddresses.add(new InternetAddress(PLCGEMAIL));
            }

			emailBody = this.getTheContentOfEmailBody(submitNewDocDTO, request, "caseDocketFilings", entry.getKey());

			message.setFrom(this.getFromAddress());
			message.setRecipients(Message.RecipientType.BCC, toMailAddresses.toArray(new InternetAddress[toMailAddresses.size()]));



			if (submitNewDocDTO.getTypeofdocument().equalsIgnoreCase("comments added")){
				companyNameAndTypeOfDoc = companyNameAndTypeOfDoc + " " + "CHANGE TO DOCKET ENTRY";
			}


			message.setSubject("EDS :"
					+ bNumText
					+ companyNameAndTypeOfDoc);

			message.setText(emailBody.toString());
			message.setContent(emailBody,"text/html");
			Transport.send(message);

		}
		}catch (IOException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}

	}

	//Send Invitation To secondary Rep
		public void sendNotificationToSecondaryRep(HttpServletRequest request, Protest_Info protestInfo, String secondaryEmail) {

			String  emailBody = "";

			try {
				Message message = new MimeMessage(mailSession);
				SubmitNewDocDTO submitNewDocDTO = new SubmitNewDocDTO();
				submitNewDocDTO.setConsolidatedBNumber(protestInfo.getB_No());
				submitNewDocDTO.setConsolidatedANumber(protestInfo.getA_No());
				for (Protest_Info eachProtest_Info : protestInfo.getListOf_ConsolidatedProtest_Info()) {
					submitNewDocDTO.setConsolidatedBNumber(submitNewDocDTO
							.getConsolidatedBNumber() + "; "
							+ eachProtest_Info.getB_No());
					submitNewDocDTO.setConsolidatedANumber(submitNewDocDTO
							.getConsolidatedANumber() + "; "
							+ eachProtest_Info.getA_No());
				}

				emailBody = this
						.getTheContentOfEmailBody(submitNewDocDTO, request,"invitation",protestInfo);

				message.setFrom(this.getFromAddress());
				message.setRecipient(Message.RecipientType.TO, new InternetAddress(secondaryEmail));
				message.setSubject("EDS:  INVITATION TO JOIN CASE");
				message.setText(emailBody.toString());
				message.setContent(emailBody,"text/html");

				Transport.send(message);
			} catch (AddressException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			}


		}

	private String getTheContentOfEmailBody(
			SubmitNewDocDTO submitNewDocDTO, HttpServletRequest request, String templateName, Protest_Info protestInfo){

		String emailBody = "";

		try {
			File file = null;

			/*file = new File(
					request.getServletContext()
							.getRealPath(
									"/resources/emailTemplates/" + templateName +".tpl.html"));*/

			if (templateName.equalsIgnoreCase("newProtest")) {
				file = new File(request.getServletContext().getRealPath(
						"/resources/emailTemplates/newProtest.tpl.html"));
			} else if (templateName.equalsIgnoreCase("caseDocketFilings")) {
				file = new File(request.getServletContext().getRealPath(
						"/resources/emailTemplates/caseDocketFilings.tpl.html"));
			} else if (templateName.equalsIgnoreCase("paymentConfirmation")) {
				file = new File(
						request.getServletContext()
								.getRealPath(
										"/resources/emailTemplates/paymentConfirmation.tpl.html"));
			} else if (templateName.equalsIgnoreCase("otherProtest")) {
				file = new File(request.getServletContext().getRealPath(
						"/resources/emailTemplates/otherProtest.tpl.html"));
			} else if (templateName.equalsIgnoreCase("invitation")) {
				file = new File(request.getServletContext().getRealPath(
						"/resources/emailTemplates/invitation.tpl.htm"));
			}else if (templateName.equalsIgnoreCase("caseAccessDenied")) {
				file = new File(request.getServletContext().getRealPath(
						"/resources/emailTemplates/caseAccessDenied.tpl.html"));
			}

			if (submitNewDocDTO.getTypeofdocument() != null) {
				if (submitNewDocDTO.getTypeofdocument().equalsIgnoreCase(
						"comments added")) {
					file = new File(request.getServletContext().getRealPath(
							"/resources/emailTemplates/add-edit-notes.tpl.htm"));
				}
			}


    		BufferedReader reader = new BufferedReader(new FileReader(file));
    		StringBuffer buffer = new StringBuffer();
    		String line;
    		while((line = reader.readLine()) != null) {
    		    buffer.append(line);
    		}
    		reader.close();

    		emailBody = populateTemplateWithDynamicData (emailBody,buffer,submitNewDocDTO,protestInfo,templateName);

		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return emailBody;
	}


	private String populateTemplateWithDynamicData (String emailBody,StringBuffer buffer,SubmitNewDocDTO submitNewDocDTO,Protest_Info protest_Info,String templateName){

		Map<String, Object> ReplacementMap = new HashMap<String, Object>();


		try  {
		InetAddress IP = InetAddress.getLocalHost();

		if (IP.toString().contains("159.142.165.49")) {
			ReplacementMap.put("epds_base_url", "https://eds.cbca.gov");
			ReplacementMap.put("non_Prod_Warn", "");
		}else{
			ReplacementMap.put("epds_base_url", "https://epdstest.edc.usda.gov");
			ReplacementMap.put("non_Prod_Warn", "<p><strong>MESSAGE GENERATED FROM NON PRODUCTION ENVIRONMENT</strong></p><br />");
		}

		}catch (Exception e) {
			System.out.println("Problem getting current system IP");
		}

		if (templateName.equalsIgnoreCase("newProtest")
				|| templateName.equalsIgnoreCase("paymentConfirmation")
				|| templateName.equalsIgnoreCase("otherProtest")){

			ReplacementMap.put("protester_Name", protest_Info.getCompany_Name());
			ReplacementMap.put("sol_Num", protest_Info.getSolicitation_No());
			ReplacementMap.put("filing_Time", protest_Info.getSubmission_Date());
			ReplacementMap.put("caseDueDate", protest_Info.getDue_Date());
			
			try {
				ReplacementMap.put("agencyReportDueDate", Date_Util.agencyReportDueDate(protest_Info.getSubmission_Date(),protest_Info.getCase_Type()));
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (protest_Info.getB_No() != null){
				ReplacementMap.put("b_Num", protest_Info.getB_No());
			}else {
				ReplacementMap.put("b_Num", "TBD");
			}
			ReplacementMap.put("a_Num", protest_Info.getA_No());

			if (templateName.equalsIgnoreCase("paymentConfirmation")){
			    String payment_tracking_Num = "Filed by GAO";
			    if (protest_Info.getPay_dot_gov_id() != null) {
			        payment_tracking_Num = protest_Info.getPay_dot_gov_id();
                }
				ReplacementMap.put("payment_tracking_Num", payment_tracking_Num);
			}

		}else if (templateName.equalsIgnoreCase("caseDocketFilings")){
			
			
			if (submitNewDocDTO.getDocId() == 160){
				ReplacementMap.put("Docket_Entry_Title", "Denial Of Request To Intervene");
			}else if (submitNewDocDTO.getDocId() == 161){
				ReplacementMap.put("Docket_Entry_Title", "Denial Of Notice Of Appearance");
			}else{
				ReplacementMap.put("Docket_Entry_Title", submitNewDocDTO.getTypeofdocument());
			}
			ReplacementMap.put("Doc_Num", String.valueOf(submitNewDocDTO.getDocketEntryNumber()));
			ReplacementMap.put("Filing_Date", submitNewDocDTO.getSubmissionDate());
			
			ReplacementMap.put("a_Num", protest_Info.getA_No());


		}else if (templateName.equalsIgnoreCase("caseAccessDenied")){

			ReplacementMap.put("protester_Name", protest_Info.getCompany_Name());
			ReplacementMap.put("sol_Num", protest_Info.getSolicitation_No());
			ReplacementMap.put("Docket_Entry_Title", submitNewDocDTO.getTypeofdocument());
			ReplacementMap.put("b_Num", protest_Info.getB_No());

		}else if (templateName.equalsIgnoreCase("invitation")){
			ReplacementMap.put("protester_Name", protest_Info.getCompany_Name());
			ReplacementMap.put("sol_Num", protest_Info.getSolicitation_No());
			if (submitNewDocDTO.getConsolidatedBNumber() != null){
				ReplacementMap.put("b_Num", submitNewDocDTO.getConsolidatedBNumber());
			}else {
				ReplacementMap.put("b_Num", "TBD");
			}
			if (submitNewDocDTO.getConsolidatedANumber() != null){
				ReplacementMap.put("a_Num", submitNewDocDTO.getConsolidatedANumber());
			}else {
				ReplacementMap.put("a_Num", "TBD");
			}
		}

		if (protest_Info.getCase_Type() != null){
			if (protest_Info.getCase_Type().toUpperCase(Locale.ENGLISH).contains("RECON")){
				ReplacementMap.put("other_protest_para1","GAO is notifying you that the following request for reconsideration has been filed with our Office:");
			}else if (protest_Info.getCase_Type().toUpperCase(Locale.ENGLISH).contains("ENT")){
				ReplacementMap.put("other_protest_para1","GAO is notifying you that the following request for an entitlement recommendation has been filed with our Office:");
			}else if (protest_Info.getCase_Type().toUpperCase(Locale.ENGLISH).contains("COST")){
				ReplacementMap.put("other_protest_para1","GAO is notifying you that the following request for a reimbursement of costs recommendation has been filed with our Office:");
			}
		}

		if (submitNewDocDTO.getTypeofdocument() != null
				&& submitNewDocDTO.getTypeofdocument().equalsIgnoreCase("comments added")){
			String docEntryTitle = "";
			if (submitNewDocDTO.getDocDescFiller() != null){
				docEntryTitle = submitNewDocDTO.getDocketEntryTitle().split("_")[0] + submitNewDocDTO.getDocDescFiller();
			}else{
				docEntryTitle = submitNewDocDTO.getDocketEntryTitle().split("_")[0];
			}

			ReplacementMap.put("Doc_Num", String.valueOf(submitNewDocDTO.getDocketEntryNumber()));
			ReplacementMap.put("Filing_Date", Date_Util.getCurrentDate());
			ReplacementMap.put("Docket_Entry_Title", docEntryTitle);
			ReplacementMap.put("comments_notes", submitNewDocDTO.getComments());
			ReplacementMap.put("a_Num", protest_Info.getA_No());
		}

		if ( (submitNewDocDTO.getUser_Role() != null) && (templateName.equalsIgnoreCase("caseDocketFilings")
		        || templateName.equalsIgnoreCase("caseAccessDenied")
                || "comments added".equalsIgnoreCase(submitNewDocDTO.getTypeofdocument()))){
            String submitterRole = Util.getRoleForDocumentSubmission(submitNewDocDTO.getUser_Role());
            ReplacementMap.put("Doc_Filer", String.valueOf(submitterRole));
        } else {
            ReplacementMap.put("Doc_Filer", "");
        }

		ReplacementMap.put("Footer_Text", GetFooterText());

		emailBody = buffer.toString();
		for (Map.Entry<String, Object> entry : ReplacementMap.entrySet()) {
		    emailBody = emailBody.replace(entry.getKey(), entry.getValue().toString());
		}
		return emailBody;

	}

	public InternetAddress getFromAddress() throws UnsupportedEncodingException{

		return new InternetAddress("cbca.systemreport@gsa.gov","EDS");

	}


	public void notifyAgencyRepOrIntervenorAboutAccessDenied(HttpServletRequest request, String accessType, Protest_Info protestInfo, String toEmail) {

		Message message = new MimeMessage(mailSession);
		String  emailBody = "";

		try {
			SubmitNewDocDTO dto = new SubmitNewDocDTO();

			if (accessType.equalsIgnoreCase("agency-rep-access")){
				dto.setTypeofdocument("AGENCY NOTICE OF APPEARANCE NOT ACKNOWLEDGED ");
			}else if (accessType.equalsIgnoreCase("intervene")){
				dto.setTypeofdocument("REQUEST TO INTERVENE DENIED");
			}
			dto.setUser_Role("GAO");

			emailBody = this
					.getTheContentOfEmailBody(dto, request,"caseAccessDenied",protestInfo);

			message.setFrom(this.getFromAddress());
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));

			if (accessType.equalsIgnoreCase("agency-rep-access")){
				message.setSubject(protestInfo.getB_No() + "--" + "EDS: AGENCY NOTICE OF APPEARANCE NOT ACKNOWLEDGED");
			}else if (accessType.equalsIgnoreCase("intervene")){
				message.setSubject(protestInfo.getB_No() + "--" + "EDS:  REQUEST TO INTERVENE DENIED");
			}

			message.setText(emailBody.toString());
			message.setContent(emailBody,"text/html");

			Transport.send(message);
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	public String GetFooterText() {
	    String footerText = "This electronic message contains information generated by the US Government solely "
	            + "for the intended recipients. Any unauthorized interception of this message or the use or "
	            + "disclosure of the information it contains may violate the law and subject the violator to civil "
	            + "or criminal penalties. If you believe you have received this message in error, please notify the "
	            + "sender and delete the email immediately. ";

	    return footerText;
	}


	public void notifyAllSystemUsers(EmailNotification emailNotification, List<String> listOfEmailAddress) throws IOException {
		
		String htmlContent = TemplateUtils.cleanHtmlContentBeforeCreatingPdf(
				emailNotification.getEmailBody(),
				Whitelist.relaxed()
				.addAttributes(":all", "style")
				.preserveRelativeLinks(true).removeTags("code"));
		


		

		try {
			InternetAddress[] toMailAddresses = new InternetAddress[listOfEmailAddress.size()];

			for (int i = 0; i < listOfEmailAddress.size(); i++)
	        {
				toMailAddresses[i] = new InternetAddress(listOfEmailAddress.get(i));
	        }
			Message message = new MimeMessage(mailSession);
			message.setFrom(this.getFromAddress());
			message.setRecipients(Message.RecipientType.BCC, toMailAddresses);
			message.setSubject(emailNotification.getSubject().toUpperCase(Locale.ENGLISH));
			message.setText(htmlContent.toString());
			message.setContent(htmlContent,"text/html");

			Transport.send(message);
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	
		
	}
}
