package gov.gao.epds.test;




import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.namespace.QName;

import gov.gao.epds.utils.Params;
import gov.treas.fms.services.tcsonline_3_0.CompleteOnlineCollectionRequest;
import gov.treas.fms.services.tcsonline_3_0.CompleteOnlineCollectionResponse;
import gov.treas.fms.services.tcsonline_3_0.GetDetailsRequest;
import gov.treas.fms.services.tcsonline_3_0.GetDetailsResponse;
import gov.treas.fms.services.tcsonline_3_0.StartOnlineCollectionRequest;
import gov.treas.fms.services.tcsonline_3_0.StartOnlineCollectionResponse;
import gov.treas.fms.services.tcsonline_3_0.TCSOnlineService30;
import gov.treas.fms.services.tcsonline_3_0.TCSOnlineServiceV30;
import gov.treas.fms.services.tcsonline_3_0.TransactionType;

/**
 * This Class is Used to Mock GC Track Webservice calls
 * @author MHussaini
 *
 */
public class ServiceTest {
	public static void main(String[] args) throws Exception {
		// testStartOnlineCollection();
		// testCompleteOnlineColleciton();
		testGetDetails();

		// System.out.println(new File(".").getCanonicalPath());
		// System.out.println(System.getenv("JAVA_HOME"));
	}

	private static HttpsURLConnection getPayDotGovConnection() throws Exception {
		String certPath = (new File(".").getCanonicalPath()) + File.separator
				+ "src" + File.separator + "main" + File.separator + "webapp"
				+ File.separator + "resources" + File.separator
				+ "qa.paygov.p12";

		KeyStore clientStore = KeyStore.getInstance("PKCS12");
		clientStore.load(new FileInputStream(certPath),
				"pay.govEPDS_GAO".toCharArray());

		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory
				.getDefaultAlgorithm());
		kmf.init(clientStore, "pay.govEPDS_GAO".toCharArray());
		KeyManager[] kms = kmf.getKeyManagers();

		KeyStore trustStore = KeyStore.getInstance("JKS");
		trustStore.load(new FileInputStream(
				"C:/Program Files/Java/jdk1.7.0_51/jre/lib/security/cacerts"),
				"changeit".toCharArray());

		TrustManagerFactory tmf = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(trustStore);
		TrustManager[] tms = tmf.getTrustManagers();

		SSLContext sslContext = null;
		sslContext = SSLContext.getInstance("TLS");
		sslContext.init(kms, tms, new SecureRandom());

		HttpsURLConnection.setDefaultSSLSocketFactory(sslContext
				.getSocketFactory());
		URL url = new URL(
				"https://qa.tcs.pay.gov/services/TCSOnlineService/2.0/?WSDL");

		HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();

		return urlConn;
	}

	private static void testGetDetails() throws Exception {
		HttpsURLConnection urlConn = getPayDotGovConnection();

		TCSOnlineServiceV30 tCSOnlineService = getTcsOnlineServiceV30();

		GetDetailsRequest getDetailsRequest = new GetDetailsRequest();
		getDetailsRequest.setAgencyTrackingId("A-DEOBF_mhussaini");
		getDetailsRequest.setTcsAppId("TCSGAOEPDS");

		try {
			GetDetailsResponse getDetailsResponse = tCSOnlineService
					.getDetails(getDetailsRequest);

			System.out.println("Agency Tracking Id: "
					+ getDetailsResponse.getTransactions()
					.getTransaction().get(0).getAgencyTrackingId());
			System.out.println("PaygocTracking Id: "
					+ getDetailsResponse.getTransactions()
					.getTransaction().get(0).getPaygovTrackingId());
			System.out.println("Transaction Status: "
					+ getDetailsResponse.getTransactions()
					.getTransaction().get(0).getTransactionStatus());
			System.out.println("Transaction Type: "
					+ getDetailsResponse.getTransactions()
					.getTransaction().get(0).getTransactionType());
			System.out.println("Payment Date: "
					+ getDetailsResponse.getTransactions()
					.getTransaction().get(0).getPaymentDate());
			System.out.println("Transaction Amount: "
					+ getDetailsResponse.getTransactions()
					.getTransaction().get(0).getTransactionAmount());
			System.out.println("Transaction Date: "
					+ getDetailsResponse.getTransactions()
					.getTransaction().get(0).getTransactionDate());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error occured.");
		} finally {
			urlConn.disconnect();
		}
	}

	
	private static TCSOnlineServiceV30 getTcsOnlineServiceV30() {


        URL url = null;
        try {
            url = new URL(Params.payDotGovURL);
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(TCSOnlineServiceV30.class.getName())
                    .log(java.util.logging.Level.INFO,
                            "Can not initialize the default wsdl from {0}", Params.payDotGovURL);
        }
        final QName  SERVICE = new QName("http://fms.treas.gov/services/tcsonline_3_0", "TCSOnlineService_3_0");

        TCSOnlineService30 tCSOnlineServiceImplService = new TCSOnlineService30(url,SERVICE);
		TCSOnlineServiceV30 tCSOnlineService = tCSOnlineServiceImplService
				.getTCSOnlineServicePort();
		
		return tCSOnlineService;
	}
	private static void testCompleteOnlineColleciton() throws Exception {
		HttpsURLConnection urlConn = getPayDotGovConnection();

		TCSOnlineServiceV30 tCSOnlineService = getTcsOnlineServiceV30();

		CompleteOnlineCollectionRequest completeOnlineCollectionRequest = new CompleteOnlineCollectionRequest();
		completeOnlineCollectionRequest.setTcsAppId("TCSGAOEPDS");
		completeOnlineCollectionRequest
				.setToken("53294d1e70c44501a0dfa141100be0d0");

		try {
			CompleteOnlineCollectionResponse completeOnlineCollectionResponse = tCSOnlineService
					.completeOnlineCollection(completeOnlineCollectionRequest);
			String paygovTrackingId = completeOnlineCollectionResponse
					.getPaygovTrackingId();

			System.out.println(paygovTrackingId);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error occured.");
		} finally {
			urlConn.disconnect();
		}

	}

	private static void testStartOnlineCollection() throws Exception {
		HttpsURLConnection urlConn = getPayDotGovConnection();

		TCSOnlineServiceV30 tCSOnlineService = getTcsOnlineServiceV30();

		StartOnlineCollectionRequest startOnlineRequest = new StartOnlineCollectionRequest();
		startOnlineRequest.setTcsAppId("TCSGAOEPDS");
		startOnlineRequest.setAgencyTrackingId("100120154");
		startOnlineRequest.setTransactionType(TransactionType.SALE);
		startOnlineRequest.setTransactionAmount(new BigDecimal(100));
		startOnlineRequest.setLanguage("en");
		startOnlineRequest.setUrlSuccess("https://epdstest.edc.usda.gov/");
		startOnlineRequest.setUrlCancel("http://www.google.com ");

		try {
			StartOnlineCollectionResponse startOnlineCollectionResponse = tCSOnlineService
					.startOnlineCollection(startOnlineRequest);
			String token = startOnlineCollectionResponse.getToken();

			System.out.println(token);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error occured.");
		} finally {
			urlConn.disconnect();
		}

	}

}
