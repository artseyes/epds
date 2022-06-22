package gov.gao.epds.utils.templates;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.itextpdf.text.*;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;

import gov.gao.epds.filestorage.FileStorageEncryption;
import gov.gao.epds.filestorage.SFTP;
import gov.gao.epds.persistence.entity.User_Info;
import gov.gao.epds.session.EpdsSession;
import gov.gao.epds.utils.EPDS_FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlToPDF {
	private final static Logger logger = LoggerFactory.getLogger(HtmlToPDF.class);

	// Temporary usage to test Creating temp PDF.
	public static final String DEST = "C:/Users/mhussaini/Gitworkspace/epds/src/main/webapp/resources/GAO_Templates/pdf";
	List<String> arrayOfGAOHeaderPageNumbers = new ArrayList<String>();
	public static String gaoHeaderPageNumber;
	public static String imgFilePath;
	public static Image image;

	public class HeaderFooter extends PdfPageEventHelper {

		@Override
		public void onOpenDocument(PdfWriter writer, com.itextpdf.text.Document document) {
			int gaoHeaderOffset = 10;
			try {
				image = Image.getInstance(imgFilePath);
				image.setAlignment(Image.ORIGINAL_PNG);
				image.scalePercent(60);
				image.setAbsolutePosition(36, PageSize.A4.getHeight() - image.getScaledHeight() - gaoHeaderOffset);
				image.setAlt("GAO Logo");
			} catch (BadElementException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void onEndPage(PdfWriter writer, com.itextpdf.text.Document document) {
			int currentPageNumber = writer.getCurrentPageNumber();
			PdfContentByte cb = null;
			try {
				cb = writer.getDirectContent();
				cb.beginMarkedContentSequence(PdfName.ARTIFACT);

				if (currentPageNumber == 1) {
					cb.addImage(image);
				} else {
					for (int i = 0; i < arrayOfGAOHeaderPageNumbers.size(); i++) {
						if (currentPageNumber == Integer.valueOf(arrayOfGAOHeaderPageNumbers.get(i))) {
							cb.addImage(image);
						}
					}
				}
				cb.endMarkedContentSequence();

			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// This is used to insert page numbers
			/*
			 * Phrase header = new Phrase(); Phrase footer = new
			 * Phrase(String.valueOf(currentPageNumber));
			 * ColumnText.showTextAligned( cb,
			 * com.itextpdf.text.Element.ALIGN_CENTER, header, (document.right()
			 * - document.left()) / 2 + document.leftMargin(), document.top() +
			 * 10, 0); ColumnText.showTextAligned( cb,
			 * com.itextpdf.text.Element.ALIGN_CENTER, footer, (document.right()
			 * - document.left()) / 2 + document.leftMargin(), document.bottom()
			 * - 10, 0);
			 */
		}

	}

	/*
	 * This method the file type to file path it is used to get the HTML file
	 * path for UI and css file path for at the time of creating the PDF.
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
	public static void main(String[] args) throws IOException, DocumentException {

		String baseDir = "C:/AmersFiles/Gitworkspace/epds/src/main/webapp";


		Map<String, String> mapOfFileMeta = TemplateUtils.getMapOfFileMetaData("103");
		imgFilePath = baseDir + mapOfFileMeta.get("imagePath");
		String imgPath = imgFilePath.substring(0, imgFilePath.indexOf("/img"));
		String htmlFilePath = baseDir + mapOfFileMeta.get("html");
		String content = getTheContentOfHtmlFile(htmlFilePath);
		String cssFilePath = baseDir + mapOfFileMeta.get("css");
		String storagePath = "C:/Users/mhussaini/GAO/pdfReportTesting";
		String fileName = mapOfFileMeta.get("fileName") + ".pdf";
		String pdfFilePath = storagePath + File.separator + fileName;

		File dir = new File(storagePath);

		if (!dir.exists())
			dir.mkdirs();
		/*
		 * createPdf(mapOfFileMeta.get("fileName"), content, cssFilePath,
		 * mapOfFileMeta);
		 */

		new HtmlToPDF().createPdf(content, cssFilePath, pdfFilePath, "", imgPath);
		System.out.println("Current working directory : " + mapOfFileMeta.toString());
	}

	public void createPdf(HttpServletRequest request, String attachmentType, String content,
			Map<String, String> mapOfFileMeta) throws IOException, DocumentException {
		imgFilePath = request.getServletContext().getRealPath(mapOfFileMeta.get("imagePath"));
		logger.info("createPdf meta imagePath:{}", mapOfFileMeta.get("imagePath"));
		logger.info("createPdf imgFilePath:{}", imgFilePath);
		String imgPath = imgFilePath.substring(0, imgFilePath.indexOf("\\img"));
		String cssFilePath = request.getServletContext().getRealPath(mapOfFileMeta.get("css"));
		String storagePath = EPDS_FileUtils.getStoragePath(request, /* attachmentType */mapOfFileMeta.get("fileName"));
		String fileName = mapOfFileMeta.get("fileName") + ".pdf";
		String filePath = storagePath + File.separator + fileName;
		File dir = new File(storagePath);
		Boolean isSuccess = false;

		if (mapOfFileMeta.get("multiple").equalsIgnoreCase("Y")) {
			fillArrayOfGAOHeaderPageNumbers(mapOfFileMeta);
		} else {
			gaoHeaderPageNumber = "0";
		}

		if (!dir.exists())
			dir.mkdirs();


		createPdf(content, cssFilePath, filePath, mapOfFileMeta.get("title"), imgPath);

		isSuccess = FileStorageEncryption.encryptFile(new File(filePath));
		if (isSuccess) {
			isSuccess = SFTP.uploadToServer(new File(filePath));
		}

		if (isSuccess) {
			// if encryption and uploading to remote server is true than delete
			// the local file and set the file path in session
			EPDS_FileUtils.secureDeleteFile(filePath);
			EPDS_FileUtils.setFilePathListInSession(filePath, request, "P", new HashMap<String, String>());

		} else {
			throw new RuntimeException(filePath + "  File was not uploaded to remote server");
		}

	}

	public void createTempPdf(HttpServletRequest request, String content, Map<String, String> mapOfFileMeta)
			throws IOException, DocumentException {

		imgFilePath = request.getServletContext().getRealPath(mapOfFileMeta.get("imagePath"));
		logger.info("createPdf meta imagePath:{}", mapOfFileMeta.get("imagePath"));
		logger.info("createPdf imgFilePath:{}", imgFilePath);
		String imgPath = imgFilePath.substring(0, imgFilePath.indexOf("\\img"));
		User_Info user_Info = (User_Info) EpdsSession.getAttribute(request, "user_Info");

		String uniqueId = /*RandomStringUtils.randomAlphanumeric(8) + */new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());

		String cssFilePath = request.getServletContext().getRealPath(mapOfFileMeta.get("css"));

		String storagePath = request.getServletContext().getRealPath(mapOfFileMeta.get("tempStoragePath")) + File.separator + user_Info.getUser_Id();

		String fileName = mapOfFileMeta.get("fileName") + ".pdf";
		String filePath = storagePath  + File.separator + uniqueId;

		File storagePathDir = new File(storagePath);
		File tempPdfFilePathDir = new File(filePath);
		if (mapOfFileMeta.get("multiple").equalsIgnoreCase("Y")) {
			fillArrayOfGAOHeaderPageNumbers(mapOfFileMeta);
		} else {
			gaoHeaderPageNumber = "0";
		}

		if (!storagePathDir.isDirectory())
			storagePathDir.mkdirs();

		if (!tempPdfFilePathDir.isDirectory())
			tempPdfFilePathDir.mkdirs();


		filePath = filePath+ File.separator + fileName;


		createPdf(content, cssFilePath, filePath, mapOfFileMeta.get("title"), imgPath);

		EpdsSession.setAttribute(request, "tempPdfFilePath", filePath);
	}

	/**https://stackoverflow.com/questions/29394735/can-br-tag-work-inside-td-with-xmlworker
	 * @param htmlContent
	 * @param cssFilePath
	 * @param pdfFilePath
	 * @throws FileNotFoundException
	 * @throws DocumentException
	 * @throws IOException
	 */
	private void createPdf(String htmlContent, String cssFilePath, String pdfFilePath, String title, String imgPath)
			throws FileNotFoundException, DocumentException, IOException {


		com.itextpdf.text.Document document = new com.itextpdf.text.Document(PageSize.A4, 36, 36, 18, 72);

		OutputStream fileOutputStream = new FileOutputStream(new File(pdfFilePath));
		/*
		 * When an XML parser encounters the closing tag </br> without having encountered the opening tag <br> first, it will throw an exception because your XML is invalid.
		*/
		htmlContent = htmlContent.replaceAll("(?i)<br[^>]*>", "<br />");
		htmlContent = htmlContent.replaceAll("<img (.*)\">", "<img $1\" />");

		// step 2
		PdfWriter writer = PdfWriter.getInstance(document, fileOutputStream);
		writer.setPdfVersion(PdfWriter.VERSION_1_7);
		writer.setTagged();
		writer.setViewerPreferences(PdfWriter.DisplayDocTitle);

		// Switching to inline image processing instead of adding it at an absolute position after
		// For 508 images, the graphic alt tags weren't being added and it had to be manually added as an artifact
		// so screen readers wouldn't see it
//		writer.setPageEvent(new HeaderFooter());

		// document header attributes
		document.addLanguage("en-US");
		document.addAuthor("EPDS");
		document.addCreationDate();
		document.addProducer();
		document.addCreator("epds.cbca.gov");
		document.addTitle(title);

		writer.createXmpMetadata();

		// step 3
		document.open();

//		document.add(Chunk.NEWLINE);
//		document.add(Chunk.NEWLINE);
//		document.add(Chunk.NEWLINE);

//		XMLWorkerFontProvider fontProvider = new XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS);
//		EpdsFontProvider fontProvider = new EpdsFontProvider();
//		fontProvider.getFont(BaseFont.TIMES_ROMAN, 8);
//		fontProvider.getFont(BaseFont.TIMES_ROMAN, 8, Font.BOLD);
////		fontProvider.register("resources/fonts/Cardo-Italic.ttf");
//		fontProvider.addFontSubstitute(BaseFont.HELVETICA, BaseFont.TIMES_ROMAN);
//		fontProvider.addFontSubstitute(BaseFont.HELVETICA_BOLD, BaseFont.TIMES_BOLD);
//		fontProvider.addFontSubstitute(BaseFont.ZAPFDINGBATS, BaseFont.SYMBOL);
////		fontProvider.addFontSubstitute(BaseFont.HELVETICA_BOLD, BaseFont.TIMES_BOLD);
////		fontProvider.defaultEncoding
//		fontProvider.defaultEncoding = BaseFont.IDENTITY_H;

//		InputStream htmlInputStream = new ByteArrayInputStream(htmlContent.getBytes());
//		InputStream cssInputStream = new FileInputStream(cssFilePath);
//		XMLWorkerHelper.getInstance( ).parseXHtml (
//				writer,
//				document,
//				htmlInputStream,
//				cssInputStream
////				fontProvider
//		);

		// step 4

		// CSS
		CSSResolver cssResolver = new StyleAttrCSSResolver();
		InputStream cssInputStream = new FileInputStream(cssFilePath);
		CssFile cssFile = XMLWorkerHelper.getCSS(cssInputStream);
		cssResolver.addCss(cssFile);

		// HTML
//		XMLWorkerFontProvider fontProvider = new XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS);
//		fontProvider.getFont(BaseFont.TIMES_ROMAN, 8);
//		fontProvider.getFont(BaseFont.TIMES_ROMAN, 8, Font.BOLD);
////		fontProvider.register("resources/fonts/Cardo-Italic.ttf");
//		fontProvider.addFontSubstitute(BaseFont.HELVETICA, BaseFont.TIMES_ROMAN);
//		fontProvider.addFontSubstitute(BaseFont.HELVETICA_BOLD, BaseFont.TIMES_BOLD);
////		fontProvider.addFontSubstitute(BaseFont.HELVETICA_BOLD, BaseFont.TIMES_BOLD);
//
//		CssAppliers cssAppliers = new CssAppliersImpl(fontProvider);
//		HtmlPipelineContext htmlContext = new HtmlPipelineContext(cssAppliers);
		HtmlPipelineContext htmlContext = new HtmlPipelineContext(null);
		htmlContext.setResourcesRootPath(imgPath);
		htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());

		// Pipelines
		PdfWriterPipeline pdf = new PdfWriterPipeline(document, writer);
		HtmlPipeline html = new HtmlPipeline(htmlContext, pdf);
		CssResolverPipeline css = new CssResolverPipeline(cssResolver, html);

		// XML Worker
		InputStream htmlInputStream = new ByteArrayInputStream(htmlContent.getBytes());
		XMLWorker worker = new XMLWorker(css, true);
		XMLParser p = new XMLParser(worker);
		p.parse(htmlInputStream);

		// step 5
		document.close();
		fileOutputStream.close();
	}

	/**
	 * @param mapOfFileMeta
	 */
	private void fillArrayOfGAOHeaderPageNumbers(Map<String, String> mapOfFileMeta) {
		gaoHeaderPageNumber = mapOfFileMeta.get("pages");
		String[] arrayOfPageNum = gaoHeaderPageNumber.split(";");
		arrayOfGAOHeaderPageNumbers = Arrays.asList(arrayOfPageNum);
	}

	/**
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static String getTheContentOfHtmlFile(String filePath) throws IOException {

		File input = new File(filePath);

		Document doc = Jsoup.parse(input, "UTF-8");

		List<String> classNames = getListOfClassNames();

		for (String className : classNames) {
			populateDynamicDataInHtml(className, doc);
		}

		Whitelist whitelist = new Whitelist();

		Cleaner cleaner = new Cleaner(whitelist);

		/*
		 * whitelist.addAttributes("a","accesskey","dir","lang","style",
		 * "tabindex" ,"title","href");
		 */

		cleaner.clean(doc);
		String output = Jsoup.clean(doc.toString(), "",
				Whitelist.relaxed().addTags("span", "p", "br").addAttributes(":all", "style"),
				new Document.OutputSettings().prettyPrint(true));

		return output;
	}

	/**
	 * @param className
	 * @param doc
	 */
	public static void populateDynamicDataInHtml(String className, Document doc) {

		Elements elements = doc.getElementsByClass(className);
		for (Element each : elements) {
			each.text(returnCaseDataBasedOnHTMLClassName(className));
		}
	}

	/**
	 * @param className
	 * @return
	 */
	public static String returnCaseDataBasedOnHTMLClassName(String className) {

		String retVal = "";

		switch (className) {

		case "todaysDate":

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date dateWithoutTime = null;
			try {
				dateWithoutTime = sdf.parse(sdf.format(new Date()));
			} catch (ParseException e) {
				e.printStackTrace();
			}

			retVal = "01/22/2016";
			break;

		case "bNumber":

			retVal = "B-123456";
			break;

		case "protesterName":

			retVal = "HTG";
			break;

		case "agencyName":

			retVal = "Department of Defense/Defense Logisitcs Agency";
			break;

		case "solNumber":

			retVal = "SPE7L4-15-T-8892";
			break;

		case "reportDueDate":

			retVal = "02/22/2016";
			break;

		case "decisionDueDate":

			retVal = "04/22/2016";

			break;
		case "attorneyName":

			retVal = "Ken Kilgour";
			break;

		case "attorneyPhoneNumber":
			retVal = "(202) 512-6969";
			break;

		default:
			break;
		}

		return retVal;
	}

	public static List<String> getListOfClassNames() {

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

		return listOfClassNames;
	}

}

//class EpdsFontProvider extends FontFactoryImp {
//	@Override
//	public Font getFont(String fontName, String encoding, boolean embedded, float size, int style, BaseColor color, boolean cached) {
//		// for now just passing through to super so can put a breakpoint
//		return super.getFont(fontName, encoding, embedded, size, style, color, cached);
//		// example of intercepting and overriding font
//		// LiberationSans – http://de.wikipedia.org/wiki/Liberation_(Schriftart) – http://scripts.sil.org/cms/scripts/page.php?item_id=OFL_web
////		if (style == Font.NORMAL)     return new Font(this.load("fonts/Liberation/LiberationSans-Regular.ttf"),    size, Font.NORMAL, color);
////		if (style == Font.BOLD)       return new Font(this.load("fonts/Liberation/LiberationSans-Bold.ttf"),       size, Font.NORMAL, color);
////		if (style == Font.BOLDITALIC) return new Font(this.load("fonts/Liberation/LiberationSans-BoldItalic.ttf"), size, Font.NORMAL, color);
////		if (style == Font.ITALIC)     return new Font(this.load("fonts/Liberation/LiberationSans-Italic.ttf"),     size, Font.NORMAL, color);
////		return new Font(this.load("fonts/Liberation/LiberationSans-Regular.ttf"), size, style, color);
//	}
//
//}
