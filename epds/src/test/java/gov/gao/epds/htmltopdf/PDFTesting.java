package gov.gao.epds.htmltopdf;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
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

import gov.gao.epds.utils.templates.HtmlToPDF;
import gov.gao.epds.utils.templates.TemplateUtils;


/**
 * 
 */

/**
 * @author MHussaini
 *
 */
public class PDFTesting {

	public static final String CSS = "table.myTable th { border-top: 5px solid green; } table.myTable td {border-bottom:20pt solid black;} "
			+ " table.myTable td { font-size: 10pt; border-color: gray; border-bottom : 10px }";
	public static final String HTML = "<table class='myTable table-bordered' aria-label='Test table one'>"
			+ "<thead><tr><th scope='col'>Customer Name</th><th scope='col'>Customer's Address</th> </tr></thead>"
			+ "<tbody><tr><td> XYZ </td><td> Bhubaneswar </td></tr>"
			+ "<tr><td> MNP </td><td> Cuttack </td></tr></tbody>"
			+ "</table><table class='anotherTable table-bordered' aria-label='Test table two'>"
			+ "<thead><tr><th scope='col'>Customer Name</th><th scope='col'>Customer's Address</th> </tr></thead>"
			+ "<tbody><tr><td> XYZ </td><td> Bhubaneswar </td></tr>"
			+ "<tr><td> MNP </td><td> Cuttack </td></tr></tbody>" + "</table>";

	public static void main(String[] args) throws IOException, DocumentException {

		String baseDir = "C:/AmersFiles/Gitworkspace/epds/src/main/webapp";

		Map<String, String> mapOfFileMeta = TemplateUtils.getMapOfFileMetaData("103");
		// imgFilePath = baseDir + mapOfFileMeta.get("imagePath");
		String htmlFilePath = baseDir + mapOfFileMeta.get("html");
		String content = HtmlToPDF.getTheContentOfHtmlFile(htmlFilePath);
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

		new PDFTesting().createPdf(content, pdfFilePath, cssFilePath);
		System.out.println("Current working directory : " + mapOfFileMeta.toString());

	}

	/**
	 * @param file
	 * @throws IOException
	 * @throws DocumentException
	 */
	public void createPdf(String htmlContent, String pdfFilePath, String cssFilePath)
			throws IOException, DocumentException {
		Document document = new Document();
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
		document.open();

		CSSResolver cssResolver = new StyleAttrCSSResolver();
		CssFile cssFile = XMLWorkerHelper
				.getCSS(/* new FileInputStream(cssFilePath) */new ByteArrayInputStream(CSS.getBytes()));
		cssResolver.addCss(cssFile);

		// HTML
		HtmlPipelineContext htmlContext = new HtmlPipelineContext(null);
		htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());

		// Pipelines
		PdfWriterPipeline pdf = new PdfWriterPipeline(document, writer);
		HtmlPipeline html = new HtmlPipeline(htmlContext, pdf);
		CssResolverPipeline css = new CssResolverPipeline(cssResolver, html);

		// XML Worker
		XMLWorker worker = new XMLWorker(css, true);
		XMLParser p = new XMLParser(worker);
		p.parse(new ByteArrayInputStream(htmlContent.getBytes()));
		document.close();
	}
}
