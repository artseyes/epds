package gov.gao.epds.zipfile;

import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.List;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import gov.gao.epds.persistence.entity.File_Info;

public class PrintReport {

	public static void main(String[] args) {

		String pdfFilename = "index.pdf";
		PrintReport printReport = new PrintReport();
		/*
		 * if (args.length < 1) { System.err.println("Usage: java "+
		 * printReport.getClass().getName()+ " PDF_Filename"); System.exit(1); }
		 */

		// file path
					String path = "C:/Users/mhussaini/GAO/zipfileTest/" + pdfFilename;
		/* pdfFilename = args[0].trim(); */
		printReport.createPDF(path,null);

	}

	private void createPDF(String path ,List<File_Info> fileInfoList) {

		Document doc = new Document();
		PdfWriter docWriter = null;

		try {

			// special font sizes
			Font bfBold12 = new Font(FontFamily.TIMES_ROMAN, 8, Font.BOLD, new BaseColor(0, 0, 0));
			Font bf12 = new Font(FontFamily.TIMES_ROMAN, 8);
			
			docWriter = PdfWriter.getInstance(doc, new FileOutputStream(path));

			// document header attributes
			doc.addAuthor("betterThanZero");
			doc.addCreationDate();
			doc.addProducer();
			doc.addCreator("MySampleCode.com");
			doc.addTitle("Report with Column Headings");
			doc.setPageSize(PageSize.LETTER);

			// open document
			doc.open();

			// create a paragraph
			Paragraph paragraph = new Paragraph("iText is a library that allows you to create and "
					+ "manipulate PDF documents. It enables developers looking to enhance web and other "
					+ "applications with dynamic PDF document generation and/or manipulation.");

			// specify column widths
			float[] columnWidths = { 1.5f, 3f, 2f, 2f, 4f, 4f };
			// create PDF table with the given widths
			PdfPTable table = new PdfPTable(columnWidths);
			// set table width a percentage of the page width
			table.setWidthPercentage(90f);

			// insert column headings
			insertCell(table, "Index", Element.ALIGN_LEFT, 1, bfBold12);
			insertCell(table, "Type Of Filing", Element.ALIGN_LEFT, 1, bfBold12);
			insertCell(table, "Filer", Element.ALIGN_LEFT, 1, bfBold12);
			insertCell(table, "Date", Element.ALIGN_LEFT, 1, bfBold12);
			insertCell(table, "Comments", Element.ALIGN_LEFT, 1, bfBold12);
			insertCell(table, "GAO Notes", Element.ALIGN_LEFT, 1, bfBold12);
			table.setHeaderRows(1);

			/*
			 * //insert an empty row insertCell(table, "", Element.ALIGN_LEFT,
			 * 4, bfBold12); //create section heading by cell merging
			 * insertCell(table, "New York Orders ...", Element.ALIGN_LEFT, 4,
			 * bfBold12);
			 */
			/* double orderTotal, total = 0; */

			// just some random data to fill
			for (int x = 1; x < 5; x++) {

				insertCell(table, String.valueOf(x), Element.ALIGN_CENTER, 1, bf12);
				insertCell(table, "Request for Case Transfer", Element.ALIGN_LEFT, 1, bf12);
				insertCell(table, "Protest \n ( XYZ Tester)", Element.ALIGN_CENTER, 1, bf12);
				insertCell(table, "1/11/2017", Element.ALIGN_CENTER, 1, bf12);
				insertCell(table,
						"A wonderful serenity has taken possession of my entire soul, like these sweet mornings of spring "
								+ "which I enjoy with my whole heart. I am alone, and feel the charm of existence in this "
								+ "spot, which was created for the bliss of souls like mine. I am so happy, my dear friend, "
								+ "so absorbed in the exquisite sense of mere tranquil existence, that I neglect my talents."
								+ " I should be incapable of drawing a single stroke at the present moment; and yet I feel that"
								+ " I never was a greater artist than now. When, while the lovely valley teems with vapour around me,"
								+ " and the meridian sun strikes the upper surface of the impenetrable foliage of my trees,"
								+ " and but a few stray gleams steal into the inner sanctuary,"
								+ " I throw myself down among the tall grass by the trickling stream; and, as I lie close to the earth,"
								+ " a thousand unknown plants are noticed by me: when I hear the buzz of the little world among the stalks,"
								+ " and grow familiar with the countless indescribable forms of the insects and flies,"
								+ " then I feel the presence of the Almighty, who formed us in his own image,"
								+ " and the breath of that universal love which bears and sustains us, as it "
								+ "floats around us in an eternity of bliss; and then, my friend, when darkness overspreads my eyes,"
								+ " and heaven and earth seem to dwell in my soul and absorb its power, like the form of a",
						Element.ALIGN_LEFT, 1, bf12);
				insertCell(table,
						"A wonderful serenity has taken possession of my entire soul, like these sweet mornings of spring "
								+ "which I enjoy with my whole heart. I am alone, and feel the charm of existence in this "
								+ "spot, which was created for the bliss of souls like mine. I am so happy, my dear friend, "
								+ "so absorbed in the exquisite sense of mere tranquil existence, that I neglect my talents."
								+ " I should be incapable of drawing a single stroke at the present moment; and yet I feel that"
								+ " I never was a greater artist than now. When, while the lovely valley teems with vapour around me,"
								+ " and the meridian sun strikes the upper surface of the impenetrable foliage of my trees,"
								+ " and but a few stray gleams steal into the inner sanctuary,"
								+ " I throw myself down among the tall grass by the trickling stream; and, as I lie close to the earth,"
								+ " a thousand unknown plants are noticed by me: when I hear the buzz of the little world among the stalks,"
								+ " and grow familiar with the countless indescribable forms of the insects and flies,"
								+ " then I feel the presence of the Almighty, who formed us in his own image,"
								+ " and the breath of that universal love which bears and sustains us, as it "
								+ "floats around us in an eternity of bliss; and then, my friend, when darkness overspreads my eyes,"
								+ " and heaven and earth seem to dwell in my soul and absorb its power, like the form of a",
						Element.ALIGN_LEFT, 1, bf12);

			}
			/*
			 * //merge the cells to create a footer for that section
			 * insertCell(table, "New York Total...", Element.ALIGN_RIGHT, 3,
			 * bfBold12); insertCell(table, df.format(total),
			 * Element.ALIGN_RIGHT, 1, bfBold12);
			 * 
			 * //repeat the same as above to display another location
			 * insertCell(table, "", Element.ALIGN_LEFT, 4, bfBold12);
			 * insertCell(table, "California Orders ...", Element.ALIGN_LEFT, 4,
			 * bfBold12); orderTotal = 0;
			 * 
			 * for(int x=1; x<7; x++){
			 * 
			 * insertCell(table, "20020" + x, Element.ALIGN_RIGHT, 1, bf12);
			 * insertCell(table, "XYZ00" + x, Element.ALIGN_LEFT, 1, bf12);
			 * insertCell(table, "This is Customer Number XYZ00" + x,
			 * Element.ALIGN_LEFT, 1, bf12);
			 * 
			 * orderTotal = Double.valueOf(df.format(Math.random() * 1000));
			 * total = total + orderTotal; insertCell(table,
			 * df.format(orderTotal), Element.ALIGN_RIGHT, 1, bf12);
			 * 
			 * } insertCell(table, "California Total...", Element.ALIGN_RIGHT,
			 * 3, bfBold12); insertCell(table, df.format(total),
			 * Element.ALIGN_RIGHT, 1, bfBold12);
			 */

			// add the PDF table to the paragraph
			paragraph.add(table);
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

	private void insertCell(PdfPTable table, String text, int align, int colspan, Font font) {

		// create a new cell with the specified Text and Font
		PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
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

}