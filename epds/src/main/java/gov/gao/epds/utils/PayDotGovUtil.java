/**
 * 
 */
package gov.gao.epds.utils;

import static gov.gao.epds.utils.Params.payDotGovCertRelativePath;
import static gov.gao.epds.utils.Params.payDotGovFailURL;
import static gov.gao.epds.utils.Params.payDotGovPaymentUrl;
import static gov.gao.epds.utils.Params.payDotGovSuccessURL;
import static gov.gao.epds.utils.Params.payDotGovTcsAppId;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.gao.epds.service.ProtestInfoService;
import gov.gao.epds.session.EpdsSession;
import gov.treas.fms.services.tcsonline_3_0.CompleteOnlineCollectionRequest;
import gov.treas.fms.services.tcsonline_3_0.CompleteOnlineCollectionResponse;
import gov.treas.fms.services.tcsonline_3_0.GetDetailsRequest;
import gov.treas.fms.services.tcsonline_3_0.GetDetailsResponse;
import gov.treas.fms.services.tcsonline_3_0.StartOnlineCollectionRequest;
import gov.treas.fms.services.tcsonline_3_0.StartOnlineCollectionResponse;
import gov.treas.fms.services.tcsonline_3_0.TCSOnlineService30;
import gov.treas.fms.services.tcsonline_3_0.TCSOnlineServiceV30;
/*import gov.treas.fms.services.tcsonline.CompleteOnlineCollectionRequest;
import gov.treas.fms.services.tcsonline.CompleteOnlineCollectionResponse;
import gov.treas.fms.services.tcsonline.GetDetailsRequest;
import gov.treas.fms.services.tcsonline.GetDetailsResponse2;
import gov.treas.fms.services.tcsonline.StartOnlineCollectionRequest;
import gov.treas.fms.services.tcsonline.StartOnlineCollectionResponse;
import gov.treas.fms.services.tcsonline.TCSOnlineServiceV22;
import gov.treas.fms.services.tcsonline.TCSOnlineServiceV22_Service;
import gov.treas.fms.services.tcsonline.TCSServiceFault_Exception;
import gov.treas.fms.services.tcsonline.TransactionType;
import gov.treas.fms.services.tcsonline_3_0.TCSOnlineService30;
import gov.treas.fms.services.tcsonline_3_0.TCSOnlineServiceV30;*/
import gov.treas.fms.services.tcsonline_3_0.TCSServiceFault_Exception;
import gov.treas.fms.services.tcsonline_3_0.TransactionType;


/**
 * @author MHussaini
 *
 */
public class PayDotGovUtil {

	
	private final static Logger logger = LoggerFactory
			.getLogger(PayDotGovUtil.class);
	/**
	 * @param request
	 * @return
	 * @throws TCSServiceFault_Exception
	 */
	public static StartOnlineCollectionResponse startOnlineCollectionRequest(HttpServletRequest request, ProtestInfoService protestInfoService)
			throws TCSServiceFault_Exception {
		
		
		StartOnlineCollectionRequest startOnlineCollectionRequest = new StartOnlineCollectionRequest();

		startOnlineCollectionRequest.setTcsAppId(payDotGovTcsAppId);
		startOnlineCollectionRequest
				.setTransactionType(TransactionType.SALE);
		setPayDotGovAmount(startOnlineCollectionRequest, request, protestInfoService);
		startOnlineCollectionRequest.setLanguage("en");
		startOnlineCollectionRequest
				.setUrlSuccess(payDotGovSuccessURL);
		startOnlineCollectionRequest.setUrlCancel(payDotGovFailURL);


		TCSOnlineServiceV30 tCSOnlineService = getTcsOnlineServiceV30();


		StartOnlineCollectionResponse startOnlineCollectionResponse;
		startOnlineCollectionResponse = tCSOnlineService
				.startOnlineCollection(startOnlineCollectionRequest);
		return startOnlineCollectionResponse;
	}

	/**
	 * @param startOnlineCollectionRequest
	 */
	private static void setPayDotGovAmount(StartOnlineCollectionRequest startOnlineCollectionRequest,HttpServletRequest request, ProtestInfoService protestInfoService) {
		
			Boolean isTestPayDotGovResponse = (Boolean) request.getAttribute("testPayDotGov");
		
			if (null != isTestPayDotGovResponse && isTestPayDotGovResponse){
				startOnlineCollectionRequest.setAgencyTrackingId("testingPayDotGov");
				startOnlineCollectionRequest
						.setTransactionAmount(new BigDecimal(1));
			}else{
				startOnlineCollectionRequest.setAgencyTrackingId(protestInfoService.getAgencyTrackingId(request));
				startOnlineCollectionRequest
						.setTransactionAmount(new BigDecimal(350));
			}
		
		
	}

	
	/**
	 * @param map
	 * @param request
	 */
	public static void testPayDotGovConnection(ModelMap map, HttpServletRequest request,ProtestInfoService protestInfoService) {
		HttpsURLConnection payDotGovURL_connection = null;
		
			request.setAttribute("testPayDotGov", true);
		
		try {
			payDotGovURL_connection = Protest_info_util
					.getPayDotGovURL_connection(request.getServletContext()
							.getRealPath(payDotGovCertRelativePath));

			StartOnlineCollectionResponse startOnlineCollectionResponse = startOnlineCollectionRequest(request,protestInfoService);

			if (null != startOnlineCollectionResponse.getToken()){
				map.addAttribute("isSuccess",true);	
			}
			

		}catch (TCSServiceFault_Exception e) {
			
			e.printStackTrace();
			logger.error("Pay.gov return error code ---->" + e.getFaultInfo().getReturnCode());
			logger.error("Pay.gov return Detail ---->" + e.getFaultInfo().getReturnDetail());
			
			
			Map<String, Object> errorMap = new HashMap<String,Object>();
			errorMap.put("errorCode", e.getFaultInfo().getReturnCode());
			errorMap.put("errorDetail", e.getFaultInfo().getReturnDetail());
			map.addAttribute("error", errorMap);
		} catch (Exception e) {
			e.printStackTrace();
			map.addAttribute("error", "Exception Occured");
		} finally {
			if (payDotGovURL_connection != null) {
				
				payDotGovURL_connection.disconnect();
			}
		}
	}
	
	
	/**
	 * @param token
	 * @return
	 * @throws TCSServiceFault_Exception
	 */
	public static GetDetailsResponse checkTransactionStatus(String token) throws TCSServiceFault_Exception {
		TCSOnlineServiceV30 tCSOnlineService = getTcsOnlineServiceV30();

		CompleteOnlineCollectionRequest completeOnlineCollectionRequest = new CompleteOnlineCollectionRequest();
		completeOnlineCollectionRequest
				.setTcsAppId(payDotGovTcsAppId);
		completeOnlineCollectionRequest.setToken(token);

		CompleteOnlineCollectionResponse completeOnlineCollectionResponse = tCSOnlineService
				.completeOnlineCollection(completeOnlineCollectionRequest);
		String paygovTrackingId = completeOnlineCollectionResponse.getPaygovTrackingId();

		GetDetailsRequest getDetailsRequest = new GetDetailsRequest();
		getDetailsRequest.setPaygovTrackingId(paygovTrackingId);
		getDetailsRequest.setTcsAppId(payDotGovTcsAppId);

		GetDetailsResponse getDetailsResponse = tCSOnlineService
				.getDetails(getDetailsRequest);
		return getDetailsResponse;
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

	
	

	/**
	 * @param map
	 * @param request
	 * @throws TCSServiceFault_Exception
	 */
	public static void startOnlineCollectionRequest(ModelMap map, HttpServletRequest request,ProtestInfoService protestInfoService)
			throws TCSServiceFault_Exception {
		StartOnlineCollectionResponse startOnlineCollectionResponse = startOnlineCollectionRequest(request,protestInfoService);

		EpdsSession.setAttribute(request, "payDotGovToken",
				startOnlineCollectionResponse.getToken());

		map.addAttribute("payDotGovToken",
				startOnlineCollectionResponse.getToken());
		map.addAttribute("payDotGovAppId", payDotGovTcsAppId);
		map.addAttribute("payDotGovPaymentUrl", payDotGovPaymentUrl);
	}
	
	
	/**
	 * @param map
	 * @param request
	 * @param token
	 * @throws TCSServiceFault_Exception
	 * @throws JsonProcessingException 
	 */
	public static  void checkPayDotGovTransactionStatus(ModelMap map, HttpServletRequest request, String token)
			throws TCSServiceFault_Exception, JsonProcessingException {

		ObjectMapper om = new ObjectMapper();
		GetDetailsResponse getDetailsResponse = checkTransactionStatus(token);

		if (logger.isDebugEnabled()){
			logger.debug(om.writeValueAsString(getDetailsResponse));
		}
		
		String transactionStatus = getDetailsResponse.getTransactions()
				.getTransaction().get(0).getTransactionStatus();
		String payDotGovTrackingId = getDetailsResponse.getTransactions()
                .getTransaction().get(0).getPaygovTrackingId();
		String agency_tracking_id = getDetailsResponse.getTransactions()
                .getTransaction().get(0).getAgencyTrackingId();

		EpdsSession.setAttribute(request, "agency_tracking_id",
				agency_tracking_id);
		EpdsSession.setAttribute(request, "payDotGovTrackingId",
				payDotGovTrackingId);

		if (transactionStatus.equalsIgnoreCase("success")) {
			map.addAttribute("isPaymentSuccess", "Y");
		} else {
			map.addAttribute("isPaymentSuccess", "N");
		}
		
		map.addAttribute("payDotGovTrackingId", payDotGovTrackingId);
		map.addAttribute("transactionStatus", transactionStatus);
	
	}
	
}
