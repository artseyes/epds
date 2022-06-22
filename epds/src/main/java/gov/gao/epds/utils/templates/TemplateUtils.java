package gov.gao.epds.utils.templates;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import gov.gao.epds.dto.TemplateDataDTO;
import gov.gao.epds.persistence.entity.Protest_Info;
import gov.gao.epds.persistence.entity.User_Info;
import gov.gao.epds.session.EpdsSession;

public class TemplateUtils{


	public static final String ALLOWED_HTML_TAGS = "p,span,b,i,u,div,br,a";;
	public static final String REMOVED_HTML_TAGS = "style,script,noscript,hr,input";;

	public static String getTheContentOfHtmlFile(HttpServletRequest request, TemplateDataDTO templateDataDTO)
			throws IOException {

		File input = new File(templateDataDTO.getFilePath());

		Document doc = Jsoup.parse(input, "UTF-8");

		Map<String,List<String>> htmlAttrKeyAndValue = new HashMap<String,List<String>>();
		List<String> classNames = getListOfClassNames();

		if (templateDataDTO.getProtestInfo() != null){

			for (String className : classNames) {
				populateDynamicDataInHtml(className, doc, templateDataDTO);
			}
		}


		Elements all = doc.select("*");
		for (Element el : all) {
		  for (Attribute attr : el.attributes()) {

			  List<String> attributeValues = new ArrayList<String>();
			    String attrKey = attr.getKey();
			    String attrValue = attr.getValue();

			  if (attrKey.startsWith("on")) {
			      el.removeAttr(attrKey);
			    }

		    attributeValues.add(attrValue);

		    if (htmlAttrKeyAndValue.get(attrKey) != null){
		    	htmlAttrKeyAndValue.get(attrKey).add(attrValue);
		    }else{
		    	htmlAttrKeyAndValue.put(attrKey,attributeValues);
		    }



		  }
		}


		EpdsSession.setAttribute(request, "templateAttributeMap", htmlAttrKeyAndValue);
		String output = Jsoup.clean(doc.toString(),
				"https://epdstest.edc.usda.gov/",
				templateDataDTO.getWhiteList(),
				new Document.OutputSettings().prettyPrint(true));

		return output;
	}

	public static String cleanHtmlContentBeforeCreatingPdf(String content,
			Whitelist whitelist) throws IOException {

		Document doc = Jsoup.parse(content, "UTF-8");


		// Remove all script and style elements and those of class "hidden".
		doc.select("script,.hidden,code,a,pre").remove();

		// Remove all style and event-handler attributes from all elements.
		Elements all = doc.select("*");
		for (Element el : all) {
		  for (Attribute attr : el.attributes()) {
		    String attrKey = attr.getKey();
		    if (attrKey.startsWith("on")) {
		      el.removeAttr(attrKey);
		    }
		  }
		}

		String output = Jsoup.clean(doc.toString(),
				"https://epdstest.edc.usda.gov/", whitelist,
				new Document.OutputSettings().prettyPrint(true));



		return output;
	}

	private static void populateDynamicDataInHtml(String className,
			Document doc, TemplateDataDTO templateDataDTO) {

		Elements elements = doc.getElementsByClass(className);
		for (Element each : elements) {
			each.text(returnCaseDataBasedOnHTMLClassName(className,
					templateDataDTO));
		}
	}

	private static String returnCaseDataBasedOnHTMLClassName(String className,
			TemplateDataDTO templateDataDTO) {
		String retVal = "";
		Protest_Info protestInfo = templateDataDTO.getProtestInfo();
		User_Info attorneyInfo = templateDataDTO.getAttorneyInfo();

		switch (className) {

		case "todaysDate": // come back
			DateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy");
			 java.util.Date date = new java.util.Date();
			retVal = dateFormat.format(date);
			break;
		case "bNumber":
			retVal = protestInfo.getB_No();
			break;
		case "protesterName":
			retVal = protestInfo.getCompany_Name();
			break;
		case "agencyName":
			retVal = protestInfo.getAgency_Name();
			break;
		case "solNumber":
			retVal = protestInfo.getSolicitation_No();
			break;
		case "reportDueDate": // come back
			retVal = "02/22/2016";
			break;
		case "decisionDueDate":
			retVal = protestInfo.getDue_Date();
			break;
		case "attorneyName":
			retVal = attorneyInfo.getFirst_Name() + " " + attorneyInfo.getLast_Name();
			break;
		case "attorneyPhoneNumber":
			retVal = attorneyInfo.getPhone_No();
			break;

		}

		return retVal;
	}


	private static List<String> getListOfClassNames() {
		List<String> listOfClassNames = new ArrayList<String>();
		listOfClassNames.add("todaysDate");
		listOfClassNames.add("bNumber");
		listOfClassNames.add("protesterName");
		listOfClassNames.add("agencyName");
		listOfClassNames.add("solNumber");
		listOfClassNames.add("reportDueDate");
		listOfClassNames.add("decisionDueDate");
		listOfClassNames.add("attorneyName");
		listOfClassNames.add("attorneyPhoneNumber");
		listOfClassNames.add("primaryProtesterRepName");
		listOfClassNames.add("primaryProtesterPhoneNumber");
		listOfClassNames.add("agencyRepName");
		listOfClassNames.add("agencyRepPhoneNumber");

		return listOfClassNames;
	}

	/*
	 * This method the file type to file path it is used to get the HTML file
	 * path for GUI and css file path for at the time of creating the PDF.
	 *
	 * 114 Acknowledgement Of Request For Reconsideration GAO 103
	 * Acknowledgement Package with Protective Order GAO 104 Acknowledgement
	 * Package without Protective Order GAO 133 Acknowledgement of Request for
	 * Costs Recommendation GAO 124 Acknowledgement of Request for Entitlement
	 * Recommendation GAO 135 Admission to Protective Order GAO 106 Admission to
	 * Protective Order GAO 116 Admission to Protective Order GAO 126 Admission
	 * to Protective Order GAO 107 Amended Admission to Protective Order GAO 127
	 * Amended Admission to Protective Order GAO 136 Amended Admission to
	 * Protective Order GAO 117 Amended Admission to Protective Order GAO 137
	 * Notice Of ____ GAO 128 Notice Of ____ GAO 119 Notice Of ____ GAO 109
	 * Notice Of ____ GAO 105 Notice of Protective Order GAO 125 Notice of
	 * Protective Order GAO 115 Notice of Protective Order GAO 134 Notice of
	 * Protective Order GAO 140 Withdrawal Confirmation GAO 112 Withdrawal
	 * Confirmation GAO 131 Withdrawal Confirmation GAO 122 Withdrawal
	 * Confirmation GAO
	 */
	public static Map<String, String> getMapOfFileMetaData(String docId) {

		Map<String, String> mapOfFileTypeToFilePath = new HashMap<String, String>();


		switch (docId) {

		case "103":
			mapOfFileTypeToFilePath
					.put("css", "/resources/GAO_Templates/css/ACKNOWLEDGMENTLETTERSWITHPROTECTIVEORDER.css");
			mapOfFileTypeToFilePath
					.put("html", "/resources/GAO_Templates/html/ACKNOWLEDGMENTLETTERSWITHPROTECTIVEORDER.htm");
			mapOfFileTypeToFilePath.put("fileName", "AcknowledgmentPackageWithProtectiveOrder");
			mapOfFileTypeToFilePath.put("multiple", "Y");
			mapOfFileTypeToFilePath.put("pages", "3;5;8");
			mapOfFileTypeToFilePath.put("title", "Acknowledgment Package With Protective Order");
			break;

		case "104":
			mapOfFileTypeToFilePath
					.put("css", "/resources/GAO_Templates/css/ACKNOWLEDGMENTLETTERSWITHOUTPROTECTIVEORDER.css");
			mapOfFileTypeToFilePath
					.put("html", "/resources/GAO_Templates/html/ACKNOWLEDGMENTLETTERSWITHOUTPROTECTIVEORDER.htm");
			mapOfFileTypeToFilePath.put("fileName", "AcknowledgmentPackageWithOutProtectiveOrder");
			mapOfFileTypeToFilePath.put("multiple", "Y");
			mapOfFileTypeToFilePath.put("pages", "2");
			mapOfFileTypeToFilePath.put("title", "Acknowledgment Package With Out Protective Order");
			break;

		case "135":
		case "106":
		case "116":
		case "126":
			mapOfFileTypeToFilePath.put("css", "/resources/GAO_Templates/css/ADMISSIONTOPROTECTIVEORDER.css");
			mapOfFileTypeToFilePath.put("html", "/resources/GAO_Templates/html/ADMISSIONTOPROTECTIVEORDER.htm");
			mapOfFileTypeToFilePath.put("fileName", "AdmissionToProtectiveOrder");
			mapOfFileTypeToFilePath.put("multiple", "N");
			mapOfFileTypeToFilePath.put("title", "Admission To Protective Order");
			break;

		case "107":
		case "127":
		case "136":
		case "117":
			mapOfFileTypeToFilePath.put("css", "/resources/GAO_Templates/css/AMENDEDADMISSIONTOPROTECTIVEORDER.css");
			mapOfFileTypeToFilePath.put("html", "/resources/GAO_Templates/html/AMENDEDADMISSIONTOPROTECTIVEORDER.htm");
			mapOfFileTypeToFilePath.put("fileName", "AmendedAdmissionToProtectiveOrder");
			mapOfFileTypeToFilePath.put("multiple", "N");
			mapOfFileTypeToFilePath.put("title", "Amended Admission To Protective Order");
			break;

		case "112":
		case "122":
		case "131":
		case "140":
			mapOfFileTypeToFilePath.put("css", "/resources/GAO_Templates/css/WITHDRAWALCONFIRMATION.css");
			mapOfFileTypeToFilePath.put("html", "/resources/GAO_Templates/html/WITHDRAWALCONFIRMATION.htm");
			mapOfFileTypeToFilePath.put("fileName", "WithdrawalConfirmation");
			mapOfFileTypeToFilePath.put("multiple", "N");
			mapOfFileTypeToFilePath.put("title", "Withdrawal Confirmation");
			break;

		case "105":
		case "115":
		case "125":
		case "134":
			mapOfFileTypeToFilePath.put("css", "/resources/GAO_Templates/css/PROTECTIVEORDERNOTICEAPPLICATION.css");
			mapOfFileTypeToFilePath.put("html", "/resources/GAO_Templates/html/PROTECTIVEORDERNOTICEAPPLICATION.htm");
			mapOfFileTypeToFilePath.put("fileName", "NoticeOfProtectiveOrder");
			mapOfFileTypeToFilePath.put("multiple", "Y");
			mapOfFileTypeToFilePath.put("pages", "2;5");
			mapOfFileTypeToFilePath.put("title", "Notice Of Protective Order");
			break;

		case "124":
			mapOfFileTypeToFilePath.put("css", "/resources/GAO_Templates/css/ENTITLEMENTACKNOWLEDGMENTOFREQUEST.css");
			mapOfFileTypeToFilePath.put("html", "/resources/GAO_Templates/html/ENTITLEMENTACKNOWLEDGMENTOFREQUEST.htm");
			mapOfFileTypeToFilePath.put("fileName", "AcknowledgmentRequestForEntitlement");
			mapOfFileTypeToFilePath.put("multiple", "Y");
			mapOfFileTypeToFilePath.put("pages", "2");
			mapOfFileTypeToFilePath.put("title", "Acknowledgment Request For Entitlement");
			break;

		case "114":
			mapOfFileTypeToFilePath
					.put("css", "/resources/GAO_Templates/css/ENTITLEMENTACKNOWLEDGMENTOFREQUEST.css");
			mapOfFileTypeToFilePath
					.put("html", "/resources/GAO_Templates/html/RECONACKNOWLEDGEMENT.htm");
			mapOfFileTypeToFilePath.put("fileName", "AcknowledgmentRequestForReconsideration");
			mapOfFileTypeToFilePath.put("multiple", "N");
			/*mapOfFileTypeToFilePath.put("pages", "2");*/
			mapOfFileTypeToFilePath.put("title", "Acknowledgment Request For Reconsideration");
			break;

		case "133":
			mapOfFileTypeToFilePath.put("css", "/resources/GAO_Templates/css/ACKNOWLEDGMENTOFCLAIMFORCOSTS.css");
			mapOfFileTypeToFilePath.put("html",  "/resources/GAO_Templates/html/ACKNOWLEDGMENTOFCLAIMFORCOSTS.htm");
			mapOfFileTypeToFilePath.put("fileName", "AcknowledgmentRequestForCostClaims");
			mapOfFileTypeToFilePath.put("multiple", "Y");
			mapOfFileTypeToFilePath.put("pages", "2");
			mapOfFileTypeToFilePath.put("title", "Acknowledgment Request For Cost Claims");
			break;

		case "137":
		case "128":
		case "119":
		case "109":
			mapOfFileTypeToFilePath.put("css", "/resources/GAO_Templates/css/NOTICE.css");
			mapOfFileTypeToFilePath.put("html", "/resources/GAO_Templates/html/NOTICE.htm");
			mapOfFileTypeToFilePath.put("fileName", "noticeOfBlank");
			mapOfFileTypeToFilePath.put("multiple", "N");
			mapOfFileTypeToFilePath.put("title", "Notice Of");
			break;


		case "160":
		case "161":
			mapOfFileTypeToFilePath.put("css", "/resources/GAO_Templates/css/NOTICE.css");
			mapOfFileTypeToFilePath.put("html", "/resources/GAO_Templates/html/DENIALNOTICE.htm");
			mapOfFileTypeToFilePath.put("fileName", "noticeOfBlank");
			mapOfFileTypeToFilePath.put("multiple", "N");
			mapOfFileTypeToFilePath.put("title", "Notice Of Denial");
			break;

		case "191":
        case "193":
        case "195":
        case "197":
            mapOfFileTypeToFilePath.put("css", "/resources/GAO_Templates/css/NOTICE.css");
            mapOfFileTypeToFilePath.put("html", "/resources/GAO_Templates/html/OTHERBLANK.htm");
            mapOfFileTypeToFilePath.put("fileName", "Blank");
            mapOfFileTypeToFilePath.put("multiple", "N");
			mapOfFileTypeToFilePath.put("title", "Other");
            break;

        case "217":
		case "218":
		case "219":
		case "220":
			mapOfFileTypeToFilePath.put("css", "/resources/GAO_Templates/css/NOTICE.css");
			mapOfFileTypeToFilePath.put("html", "/resources/GAO_Templates/html/PROPOSEDPUBLICVERSION.htm");
			mapOfFileTypeToFilePath.put("fileName", "ProposedPublicVersionBlank");
			mapOfFileTypeToFilePath.put("multiple", "N");
			mapOfFileTypeToFilePath.put("title", "Proposed Public Version");
			break;

		case "235":
		case "236":
		case "237":
		case "238":
			mapOfFileTypeToFilePath.put("css", "/resources/GAO_Templates/css/NOTICE.css");
			mapOfFileTypeToFilePath.put("html", "/resources/GAO_Templates/html/PUBLICNOTICE.htm");
			mapOfFileTypeToFilePath.put("fileName", "publicNoticeOfBlank");
			mapOfFileTypeToFilePath.put("multiple", "N");
			mapOfFileTypeToFilePath.put("title", "Public Notice Of");
			break;

		case "110":
		case "120":
		case "138":
		case "129":
			mapOfFileTypeToFilePath.put("css", "/resources/GAO_Templates/css/NOTICE.css");
			mapOfFileTypeToFilePath.put("html", "/resources/GAO_Templates/html/protectedDecision.htm");
			mapOfFileTypeToFilePath.put("fileName", "noticeOfIssuanceOfProtectedDecision");
			mapOfFileTypeToFilePath.put("multiple", "N");
			mapOfFileTypeToFilePath.put("title", "Notice Of Issuance Of Protected Decision");
			break;
		default:
			break;

		}

		mapOfFileTypeToFilePath.put("imagePath", "/resources/GAO_Templates/img/GAO/gaoTemplateHeader.png");
		mapOfFileTypeToFilePath.put("tempStoragePath", "/resources/GAO_Templates/temp_Pdf");

		return mapOfFileTypeToFilePath;
	}


	public static String getTemplateFilePathInProd(String path){
		String appTemplatePath = "D:" + File.separator + "UpFiles";
		return path.replace("/resources", appTemplatePath);
	}
}
