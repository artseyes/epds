package gov.gao.epds.utils;

import gov.gao.epds.persistence.entity.Protest_Info;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

public class ExportToExcel 
{
	private static CellStyle cs = null;
	 private static CellStyle csBold = null;
	 private static CellStyle csTop = null;
	 private static CellStyle csRight = null;
	 private static CellStyle csBottom = null;
	 private static CellStyle csLeft = null;
	 private static CellStyle csTopLeft = null;
	 private static CellStyle csTopRight = null;
	 private static CellStyle csBottomLeft = null;
	 private static CellStyle csBottomRight = null;
	 private static HSSFCellStyle styleDateFormat = null;
	 private static HSSFCellStyle styleCurrencyFormat = null;
	 
    public static void main(String[] args) 
    {
    	
    }
    
    public static HSSFWorkbook getWorkBook(List<Protest_Info> listOfProtestInfo){
    

        //Blank workbook
        HSSFWorkbook workbook = new HSSFWorkbook(); 
        
        setCellStyles(workbook);
        //Create a blank sheet
        HSSFSheet sheet = workbook.createSheet("Pay.gov Report");
        
          
        //Set Column Widths
        sheet.setColumnWidth(0, 2500); 
        sheet.setColumnWidth(1, 2500);
        sheet.setColumnWidth(2, 6000);
        sheet.setColumnWidth(3, 10000);
        sheet.setColumnWidth(4, 3000);
        
      //Setup the Page margins - Left, Right, Top and Bottom
        sheet.setMargin(Sheet.LeftMargin, 0.25);
        sheet.setMargin(Sheet.RightMargin, 0.25);
        sheet.setMargin(Sheet.TopMargin, 0.75);
        sheet.setMargin(Sheet.BottomMargin, 0.75);
        
        //Setup the Header and Footer Margins
        sheet.setMargin(Sheet.HeaderMargin, 0.25);
        sheet.setMargin(Sheet.FooterMargin, 0.25);
        
        Row headerRow = sheet.createRow(0);
        Cell c;
        c = headerRow.createCell(0);
        c.setCellValue("A#");
        c.setCellStyle(csBold);
        c = headerRow.createCell(1);
        c.setCellValue("B#");
        c.setCellStyle(csBold);
        c = headerRow.createCell(2);
        c.setCellValue("PayDotGov Tracking Id");
        c.setCellStyle(csBold);
        c = headerRow.createCell(3);
        c.setCellValue("Transaction Date");
        c.setCellStyle(csBold);
        c = headerRow.createCell(4);
        c.setCellValue("Transaction Amount");
        c.setCellStyle(csBold);
        
        Map<String, Object[]> data = new TreeMap<String, Object[]>();
        Protest_Info eachProtestInfo;
        int count  = 1;
        String submissionDatePattern = "MMM dd yyyy HH:mm:ss z";
        for (int i = 0; i < listOfProtestInfo.size(); i++ ){
        	eachProtestInfo = listOfProtestInfo.get(i);
        	
        	if (eachProtestInfo.getCase_Type().equalsIgnoreCase("PROTEST")){
        		
        		DateTime dt = DateTime.parse(
    					eachProtestInfo.getSubmission_Date(),
    					DateTimeFormat.forPattern(submissionDatePattern));
        		DateTimeZone dtZone = DateTimeZone.forID("America/New_York");
        		DateTime dtus = dt.withZone(dtZone); //21-1-2015 09:15:55 PM - Correct!

        		Date dateInUS = dtus.toLocalDateTime().toDate();
        		
        		data.put(String.valueOf(count), new Object[] {
            		eachProtestInfo.getA_No(), 
            		eachProtestInfo.getB_No(), 
            		eachProtestInfo.getPay_dot_gov_id(),
            		dateInUS,
            		(eachProtestInfo.getPay_dot_gov_id() != null ? 350 : null)
            		}
            	);
        		
        		count++;
        	}
        	
        	
        }
        
          
        //Iterate over data and write to sheet
        Set<String> keyset = data.keySet();
        int rownum = 1;
        for (String key : keyset)
        {
            Row row = sheet.createRow(rownum++);
            Object [] objArr = data.get(key);
            int cellnum = 0;
            for (Object obj : objArr)
            {
               Cell cell = row.createCell(cellnum++);
               
               if (cellnum == 5 && obj instanceof Integer){
            	   cell.setCellValue((Integer)obj);
            	   cell.setCellStyle(styleCurrencyFormat);
               }if(obj instanceof String){
                    cell.setCellValue((String)obj);
               }else if(obj instanceof Integer){
                    cell.setCellValue((Integer)obj);
               }else if(obj instanceof Date){
            	   cell.setCellValue((Date)obj);
            	   cell.setCellStyle(styleDateFormat);
              }
            }
        }
        sheet.createFreezePane(0,1);
        
        
        Row row = sheet.createRow(sheet.getPhysicalNumberOfRows() + 5);
        
        Cell cell1 = row.createCell(5); 
        cell1.setCellValue("*The filing fees should always reflect $350.");
        
        
		return workbook;
    
    }

	public static HSSFWorkbook getAdvanceSearchWorkBook(List<Protest_Info> listOfProtestInfo){

		//Blank workbook
		HSSFWorkbook workbook = new HSSFWorkbook();

		setCellStyles(workbook);
		//Create a blank sheet
		HSSFSheet sheet = workbook.createSheet("Advance Search Results");

		//Set Column Widths
		sheet.setColumnWidth(0, 2500);
		sheet.setColumnWidth(1, 10000);
		sheet.setColumnWidth(2, 6000);
		sheet.setColumnWidth(3, 10000);
		sheet.setColumnWidth(4, 3000);
		sheet.setColumnWidth(5, 3000);
		sheet.setColumnWidth(6, 3000);
		sheet.setColumnWidth(7, 3000);
		sheet.setColumnWidth(8, 3000);
		sheet.setColumnWidth(9, 3000);

		//Setup the Page margins - Left, Right, Top and Bottom
		sheet.setMargin(Sheet.LeftMargin, 0.25);
		sheet.setMargin(Sheet.RightMargin, 0.25);
		sheet.setMargin(Sheet.TopMargin, 0.75);
		sheet.setMargin(Sheet.BottomMargin, 0.75);

		//Setup the Header and Footer Margins
		sheet.setMargin(Sheet.HeaderMargin, 0.25);
		sheet.setMargin(Sheet.FooterMargin, 0.25);

		Row headerRow = sheet.createRow(0);
		Cell c;
		c = headerRow.createCell(0);
		c.setCellValue("Case Number");
		c.setCellStyle(csBold);
		c = headerRow.createCell(1);
		c.setCellValue("Party");
		c.setCellStyle(csBold);
		c = headerRow.createCell(2);
		c.setCellValue("Filing Date");
		c.setCellStyle(csBold);
		c = headerRow.createCell(3);
		c.setCellValue("Agency");
		c.setCellStyle(csBold);
		c = headerRow.createCell(4);
		c.setCellValue("Contract Number");
		c.setCellStyle(csBold);
		c = headerRow.createCell(5);
		c.setCellValue("Due Date");
		c.setCellStyle(csBold);
		c = headerRow.createCell(6);
		c.setCellValue("Group #");
		c.setCellStyle(csBold);
		c = headerRow.createCell(7);
		c.setCellValue("Judge Name");
		c.setCellStyle(csBold);
		c = headerRow.createCell(8);
		c.setCellValue("Case Type");
		c.setCellStyle(csBold);
		c = headerRow.createCell(9);
		c.setCellValue("Case Status");
		c.setCellStyle(csBold);


		Map<String, Object[]> data = new TreeMap<String, Object[]>();
		Protest_Info eachProtestInfo;
		int count  = 1;
		String submissionDatePattern = "MMM dd yyyy HH:mm:ss z";
		for (int i = 0; i < listOfProtestInfo.size(); i++ ){
			eachProtestInfo = listOfProtestInfo.get(i);

				DateTime dt = DateTime.parse(
						eachProtestInfo.getSubmission_Date(),
						DateTimeFormat.forPattern(submissionDatePattern));
				DateTimeZone dtZone = DateTimeZone.forID("America/New_York");
				DateTime dtus = dt.withZone(dtZone); //21-1-2015 09:15:55 PM - Correct!

				Date dateInUS = dtus.toLocalDateTime().toDate();

				data.put(String.valueOf(count), new Object[] {
								eachProtestInfo.getB_No(),
								eachProtestInfo.getCompany_Name(),
								dateInUS,
								eachProtestInfo.getAgency_Name(),
								eachProtestInfo.getSolicitation_No(),
								eachProtestInfo.getDue_Date(),
								eachProtestInfo.getAttorney_Group_Id(),
								eachProtestInfo.getAttorney_Name(),
								eachProtestInfo.getCase_Type(),
								eachProtestInfo.getCase_Status()
						}
				);

				count++;
			}

		//Iterate over data and write to sheet
		Set<String> keyset = data.keySet();
		int rownum = 1;
		for (String key : keyset)
		{
			Row row = sheet.createRow(rownum++);
			Object [] objArr = data.get(key);
			int cellnum = 0;
			for (Object obj : objArr)
			{
				Cell cell = row.createCell(cellnum++);

				if (cellnum == 10 && obj instanceof Integer){
					cell.setCellValue((Integer)obj);
					cell.setCellStyle(styleCurrencyFormat);
				}if(obj instanceof String){
				cell.setCellValue((String)obj);
			}else if(obj instanceof Integer){
				cell.setCellValue((Integer)obj);
			}else if(obj instanceof Date){
				cell.setCellValue((Date)obj);
				cell.setCellStyle(styleDateFormat);
			}
			}
		}
		sheet.createFreezePane(0,1);


		Row row = sheet.createRow(sheet.getPhysicalNumberOfRows() + 10);

		Cell cell1 = row.createCell(10);


		return workbook;

	}
    private static void setCellStyles(Workbook wb) {

    	
    	
    	  //font size 10
    	  Font f = wb.createFont();
    	  f.setFontHeightInPoints((short) 10);

    	  //Simple style 
    	  cs = wb.createCellStyle();
    	  cs.setFont(f);

    	  //Bold Fond
    	  Font bold = wb.createFont();
		  bold.setBold(true);
//    	  bold.setBoldweight(Font.BOLDWEIGHT_BOLD);
    	  bold.setFontHeightInPoints((short) 10);

    	  //Bold style 
    	  csBold = wb.createCellStyle();
    	  csBold.setBorderBottom(BorderStyle.THIN);
    	  csBold.setBottomBorderColor(IndexedColors.BLACK.getIndex());
    	  csBold.setFont(bold);

    	  //Setup style for Top Border Line
    	  csTop = wb.createCellStyle();
    	  csTop.setBorderTop(BorderStyle.THIN);
    	  csTop.setTopBorderColor(IndexedColors.BLACK.getIndex());
    	  csTop.setFont(f);

    	  //Setup style for Right Border Line
    	  csRight = wb.createCellStyle();
    	  csRight.setBorderRight(BorderStyle.THIN);
    	  csRight.setRightBorderColor(IndexedColors.BLACK.getIndex());
    	  csRight.setFont(f);

    	  //Setup style for Bottom Border Line
    	  csBottom = wb.createCellStyle();
    	  csBottom.setBorderBottom(BorderStyle.THIN);
    	  csBottom.setBottomBorderColor(IndexedColors.BLACK.getIndex());
    	  csBottom.setFont(f);

    	  //Setup style for Left Border Line
    	  csLeft = wb.createCellStyle();
    	  csLeft.setBorderLeft(BorderStyle.THIN);
    	  csLeft.setLeftBorderColor(IndexedColors.BLACK.getIndex());
    	  csLeft.setFont(f);

    	  //Setup style for Top/Left corner cell Border Lines
    	  csTopLeft = wb.createCellStyle();
    	  csTopLeft.setBorderTop(BorderStyle.THIN);
    	  csTopLeft.setTopBorderColor(IndexedColors.BLACK.getIndex());
    	  csTopLeft.setBorderLeft(BorderStyle.THIN);
    	  csTopLeft.setLeftBorderColor(IndexedColors.BLACK.getIndex());
    	  csTopLeft.setFont(f);

    	  //Setup style for Top/Right corner cell Border Lines
    	  csTopRight = wb.createCellStyle();
    	  csTopRight.setBorderTop(BorderStyle.THIN);
    	  csTopRight.setTopBorderColor(IndexedColors.BLACK.getIndex());
    	  csTopRight.setBorderRight(BorderStyle.THIN);
    	  csTopRight.setRightBorderColor(IndexedColors.BLACK.getIndex());
    	  csTopRight.setFont(f);

    	  //Setup style for Bottom/Left corner cell Border Lines
    	  csBottomLeft = wb.createCellStyle();
    	  csBottomLeft.setBorderBottom(BorderStyle.THIN);
    	  csBottomLeft.setBottomBorderColor(IndexedColors.BLACK.getIndex());
    	  csBottomLeft.setBorderLeft(BorderStyle.THIN);
    	  csBottomLeft.setLeftBorderColor(IndexedColors.BLACK.getIndex());
    	  csBottomLeft.setFont(f);

    	  //Setup style for Bottom/Right corner cell Border Lines
    	  csBottomRight = wb.createCellStyle();
    	  csBottomRight.setBorderBottom(BorderStyle.THIN);
    	  csBottomRight.setBottomBorderColor(IndexedColors.BLACK.getIndex());
    	  csBottomRight.setBorderRight(BorderStyle.THIN);
    	  csBottomRight.setRightBorderColor(IndexedColors.BLACK.getIndex());
    	  csBottomRight.setFont(f);
    	  
    	  
    	  styleDateFormat = (HSSFCellStyle) wb.createCellStyle();
      	  styleDateFormat.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));
    	  
    	 /* CellStyle styleDateFormat = wb.createCellStyle();
    	  CreationHelper createHelper = wb.getCreationHelper();
    	  styleDateFormat.setDataFormat(
    	      createHelper.createDataFormat().getFormat("m/d/yy h:mm"));*/
    	 


          styleCurrencyFormat = (HSSFCellStyle) wb.createCellStyle();
          styleCurrencyFormat.setDataFormat((short)8);

    	 }
    
}


