package gov.gao.epds.utils;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.commons.lang.StringUtils;

import com.itextpdf.text.Font.FontFamily;

import gov.gao.epds.persistence.entity.File_Info;
import gov.gao.epds.persistence.entity.Protest_Info;
import gov.gao.epds.persistence.entity.User_Info;
import gov.gao.epds.service.CaseDocketSheetService;

public class CaseDocketPDFUtil {

	public static void main(String[] args) {

		String pdfFilename = "index.pdf";
		CaseDocketPDFUtil printReport = new CaseDocketPDFUtil();
		/*
		 * if (args.length < 1) { System.err.println("Usage: java "+
		 * printReport.getClass().getName()+ " PDF_Filename"); System.exit(1); }
		 */

		// file path
		String path = "C:/Users/mhussaini/GAO/zipfileTest/" + pdfFilename;
		/* pdfFilename = args[0].trim(); */
		/*printReport.createPDF(path, null, new CaseDocketSheetService(),null, null, null);*/

	}

	public static void createPDF(String path, List<File_Info> groupedBySubmissionDateFileInfoList,
			CaseDocketSheetService caseDocketSheetService,
			Protest_Info protest_Info, List<String> intervenorCompanyNameList, User_Info attorney_Info,boolean isCaseDocketSheet,
			String title) {

		
		Document doc = new Document();
		PdfWriter docWriter = null;
		/*List<File_Info> groupedBySubmissionDateFileInfoList = new ArrayList<>(fileInfoList);*/
		
		HashSet<Object> dupe =new HashSet<>();
		groupedBySubmissionDateFileInfoList.removeIf(fileInfo->!dupe.add(fileInfo.getOriginalSubmissionDate()));
		List<String> consolidatedProtests = new ArrayList<String>();
		
		if (protest_Info.getListOf_ConsolidatedProtest_Info() != null){
			
			for (Protest_Info eachProtestInfo : protest_Info.getListOf_ConsolidatedProtest_Info()){
				consolidatedProtests.add(eachProtestInfo.getCompany_Name() + ", " + eachProtestInfo.getB_No());
			}
			
		}
		
		
		try {

			// special font sizes -  since were using one of the Standard Type 1 fonts specified by adobe, don't need to embed
			Font bfBold12 = new Font(FontFamily.TIMES_ROMAN, 8, Font.BOLD, new BaseColor(0, 0, 0));
			Font bf12 = new Font(FontFamily.TIMES_ROMAN, 8);
//			Font bfBold12 = FontFactory.getFont(BaseFont.TIMES_ROMAN, BaseFont.WINANSI, BaseFont.EMBEDDED, 8, Font.BOLD, new BaseColor(0, 0, 0));
//			Font bf12 = FontFactory.getFont(BaseFont.TIMES_ROMAN, BaseFont.WINANSI, BaseFont.EMBEDDED, 8);
//			BaseFont bf = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.WINANSI, BaseFont.EMBEDDED);
//			Font bf12test = new Font(bf, 8, style, color);
//			Font bf12test = new Font(bf, 8);

			docWriter = PdfWriter.getInstance(doc, new FileOutputStream(path));
			docWriter.setPdfVersion(PdfWriter.VERSION_1_7);
			docWriter.setTagged();
			docWriter.setViewerPreferences(PdfWriter.DisplayDocTitle);

			// document header attributes
			doc.addLanguage("en-US");
			doc.addAuthor("EDS");
			doc.addCreationDate();
			doc.addProducer();
			doc.addCreator("eds.cbca.gov");
			doc.addTitle(title);
			doc.setPageSize(PageSize.LETTER);

			docWriter.createXmpMetadata();

			// open document
			doc.open();

			// create a paragraph
			Paragraph paragraph = new Paragraph();
			paragraph.setFont(bf12);

			// Main table
			PdfPTable mainTable = new PdfPTable(2);

			mainTable.setWidthPercentage(100.0f);

			// First table
			PdfPCell firstTableCell = new PdfPCell();

			firstTableCell.setBorder(PdfPCell.NO_BORDER);

			PdfPTable firstTable = new PdfPTable(2);

//			insertCell(firstTable, "Case Type99", Element.ALIGN_LEFT, 1, bfBold12, true);
//			insertCell(firstTable, protest_Info.getCase_Type().toUpperCase(Locale.ENGLISH), Element.ALIGN_LEFT, 1, bfBold12);
			
			insertCell(firstTable, "Case Type", Element.ALIGN_LEFT, 1, bfBold12, true);
			insertCell(firstTable, protest_Info.getCompany_Status().toUpperCase(Locale.ENGLISH), Element.ALIGN_LEFT, 1, bfBold12);
			
			insertCell(firstTable, "Filing Party", Element.ALIGN_LEFT, 1, bfBold12, true);
			insertCell(firstTable, protest_Info.getCompany_Name(), Element.ALIGN_LEFT, 1, bfBold12);
			
			insertCell(firstTable, "Case Number", Element.ALIGN_LEFT, 1, bfBold12, true);
			insertCell(firstTable, (protest_Info.getB_No() != null? protest_Info.getB_No() : "pending") + (protest_Info.getSupplemental_B_Nos() != null ? "; "+ protest_Info.getSupplemental_B_Nos() : ""), Element.ALIGN_LEFT, 1, bfBold12);
			
			insertCell(firstTable, "Agency", Element.ALIGN_LEFT, 1, bfBold12, true);
			insertCell(firstTable, protest_Info.getAgency_Name().toUpperCase(Locale.ENGLISH), Element.ALIGN_LEFT, 1, bfBold12);
			
			insertCell(firstTable, "Grantee/Third Party", Element.ALIGN_LEFT, 1, bfBold12, true);
			insertCell(firstTable, ((intervenorCompanyNameList != null ? StringUtils.join(intervenorCompanyNameList,";") : "")), Element.ALIGN_LEFT, 1, bfBold12);
			
			insertCell(firstTable, "Contract Number", Element.ALIGN_LEFT, 1, bfBold12, true);
			insertCell(firstTable, protest_Info.getSolicitation_No().toUpperCase(Locale.ENGLISH), Element.ALIGN_LEFT, 1, bfBold12);
			
			insertCell(firstTable, "Consolidated Cases", Element.ALIGN_LEFT, 1, bfBold12, true);
			insertCell(firstTable, StringUtils.join(consolidatedProtests,"; "), Element.ALIGN_LEFT, 1, bfBold12);

			firstTableCell.addElement(firstTable);

			mainTable.addCell(firstTableCell);

			// Second table
			PdfPCell secondTableCell = new PdfPCell();
			secondTableCell.setBorder(PdfPCell.NO_BORDER);
			PdfPTable secondTable = new PdfPTable(2);

			insertCell(secondTable, "Presiding Judge", Element.ALIGN_LEFT, 1, bfBold12, true);
			insertCell(secondTable,  (attorney_Info != null ? attorney_Info.getLast_Name() + ", " +attorney_Info.getFirst_Name() : "pending"), Element.ALIGN_LEFT, 1, bfBold12);
			
			insertCell(secondTable, "Chambers Email", Element.ALIGN_LEFT, 1, bfBold12, true);
			insertCell(secondTable, (attorney_Info != null ? attorney_Info.getEmail() : "pending"), Element.ALIGN_LEFT, 1, bfBold12);
			
			if (isCaseDocketSheet){
//				insertCell(secondTable, "Days Remaining", Element.ALIGN_LEFT, 1, bfBold12, true);
//				insertCell(secondTable,  Date_Util.getNumberOfDaysRemaining(protest_Info.getDue_Date())+"", Element.ALIGN_LEFT, 1, bfBold12);
				
				insertCell(secondTable, "Case Status", Element.ALIGN_LEFT, 1, bfBold12, true);
				insertCell(secondTable, (protest_Info.getCase_Status()), Element.ALIGN_LEFT, 1, bfBold12);
				
				insertCell(secondTable, "Protective Order Issued?", Element.ALIGN_LEFT, 1, bfBold12, true);
				insertCell(secondTable,  (protest_Info.getPo()), Element.ALIGN_LEFT, 1, bfBold12);
			}
			secondTableCell.addElement(secondTable);
			mainTable.addCell(secondTableCell);

			paragraph.add(mainTable);

		        
			// specify column widths
			float[] columnWidths = { 1.5f, 3f, 4f, 2f, 2f, 4f, 4f };
			// create PDF table with the given widths
			PdfPTable fileInfoTable = new PdfPTable(columnWidths);
			
			fileInfoTable.setSpacingBefore(5f);

			fileInfoTable.setSpacingAfter(10f);
			// set table width a percentage of the page width
			fileInfoTable.setWidthPercentage(90f);

			// insert column headings
			insertCell(fileInfoTable, "Index", Element.ALIGN_LEFT, 1, bfBold12);
			insertCell(fileInfoTable, "Type Of Filing", Element.ALIGN_LEFT, 1, bfBold12);
			insertCell(fileInfoTable, "Filer", Element.ALIGN_LEFT, 1, bfBold12);
			insertCell(fileInfoTable, "Protected", Element.ALIGN_LEFT, 1, bfBold12);
            insertCell(fileInfoTable, "Date", Element.ALIGN_LEFT, 1, bfBold12);
			insertCell(fileInfoTable, "Comments", Element.ALIGN_LEFT, 1, bfBold12);
			insertCell(fileInfoTable, "CBCA Notes", Element.ALIGN_LEFT, 1, bfBold12);
			
			fileInfoTable.setHeaderRows(1);

			File_Info eachFileInfo;
			for (int i = 0; i < groupedBySubmissionDateFileInfoList.size(); i++) {
				eachFileInfo = groupedBySubmissionDateFileInfoList.get(i);

				insertCell(fileInfoTable, String.valueOf(i + 1), Element.ALIGN_CENTER, 1, bf12);
				
				insertCell(fileInfoTable,
						(caseDocketSheetService.getTypeOfDocFromDocId(eachFileInfo.getDoc_Type_Id()).split("_"))[0]
								+ " " + (eachFileInfo.getFiller() != null ? eachFileInfo.getFiller() : ""),
						Element.ALIGN_LEFT, 1, bf12);
				
				insertCell(fileInfoTable, eachFileInfo.getSubmitter_Role() + "\n "
						+ (eachFileInfo.getCompany_Name() != null ? "( " + eachFileInfo.getCompany_Name() + " )" : ""),
						Element.ALIGN_CENTER, 1, bf12);
				
				insertCell(fileInfoTable, (eachFileInfo.getIs_Confidential() != null ? eachFileInfo.getIs_Confidential() : "N" ),
                        Element.ALIGN_CENTER, 1, bf12);

                insertCell(fileInfoTable, eachFileInfo.getSubmission_Date(), Element.ALIGN_CENTER, 1, bf12);
				
				insertCell(fileInfoTable, eachFileInfo.getComments(), Element.ALIGN_LEFT, 1, bf12);
				
				insertCell(
						fileInfoTable, ((eachFileInfo.getAttorney_Note() != null
								? (eachFileInfo.getAttorney_Note().split(":::"))[0] : "")),
						Element.ALIGN_LEFT, 1, bf12);

			}
			// add the PDF table to the paragraph
			paragraph.add(fileInfoTable);
			// add the paragraph to the document
			doc.add(paragraph);

		} catch (DocumentException dex) {
			dex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (doc != null) {
				// close the document
				doc.close();
			}
			if (docWriter != null) {
				// close the writer
				docWriter.close();
			}
		}
	}



	private static void insertCell(PdfPTable table, String text, int align, int colspan, Font font) {
		insertCell(table, text, align, colspan, font, false);
	}

	private static void insertCell(PdfPTable table, String text, int align, int colspan, Font font, boolean header) {

		if (text == null) {
			text = "";
		}
		// create a new cell with the specified Text and Font
		PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
		if (header)
			cell.setRole(PdfName.TH);

		// set the cell alignment
		cell.setHorizontalAlignment(align);
		// set the cell column span in case you want to merge two or more cells
		cell.setColspan(colspan);
		// in case there is no text and you wan to create an empty row
		if (text.trim().equalsIgnoreCase("")) {
			cell.setMinimumHeight(10f);
		}
		// add the call to the table
		table.addCell(cell);

	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	public static String getFormattedString(String str){
		 
		  if(str.lastIndexOf(".") != -1){
			  String nonDecimalPart = StringUtils.leftPad(str.substring(0,str.lastIndexOf(".")), 2,'0'),
					  decimalPart = str.substring(str.lastIndexOf(".") + 1);
			  
			  str = nonDecimalPart  + "." + getFormattedString(decimalPart);
		  }else if(str.length() == 1){
			  str = StringUtils.leftPad(str, 2,'0'); 
		  }
		  
		  
		return str;
	}

}
