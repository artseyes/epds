/**
 * 
 *//*

package gov.gao.epds.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.ModelMap;

import gov.gao.epds.service.ProtestInfoService;
import gov.gao.epds.session.EpdsSession;
import gov.treas.fms.services.tcsonline.*;

*/
/**
 * @author MHussaini
 *
 *//*

public class PayDotGovProdUtil {

	
	
	*/
/**
	 * @param request
	 * @return
	 * @throws TCSServiceFault_Exception
	 *//*

	public static StartOnlineCollectionResponse startOnlineCollectionRequest(HttpServletRequest request, ProtestInfoService protestInfoService)
			throws TCSServiceFault_Exception {
		
		
		StartOnlineCollectionRequest startOnlineCollectionRequest = new StartOnlineCollectionRequest();

		startOnlineCollectionRequest.setTcsAppId(Params.payDotGovTcsAppId);
		startOnlineCollectionRequest
				.setTransactionType(TransactionType.SALE);
		setPayDotGovAmount(startOnlineCollectionRequest, request, protestInfoService);
		startOnlineCollectionRequest.setLanguage("en");
		startOnlineCollectionRequest
				.setUrlSuccess(Params.payDotGovSuccessURL);
		startOnlineCollectionRequest.setUrlCancel(Params.payDotGovFailURL);

		TCSOnlineService_Service tCSOnlineServiceImplService = new TCSOnlineService_Service();
		TCSOnlineService tCSOnlineService = tCSOnlineServiceImplService
				.getTCSOnlineServicePort();

		StartOnlineCollectionResponse startOnlineCollectionResponse;
		startOnlineCollectionResponse = tCSOnlineService
				.startOnlineCollection(startOnlineCollectionRequest);
		return startOnlineCollectionResponse;
	}

	*/
/**
	 * @param startOnlineCollectionRequest
	 *//*

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

	
	*/
/**
	 * @param map
	 * @param request
	 *//*

	public static void testPayDotGovConnection(ModelMap map, HttpServletRequest request,ProtestInfoService protestInfoService) {
		HttpsURLConnection payDotGovURL_connection = null;
		
			request.setAttribute("testPayDotGov", true);
		
		try {
			payDotGovURL_connection = Protest_info_util
					.getPayDotGovURL_connection(request.getServletContext()
							.getRealPath(Params.payDotGovCertRelativePath));

			StartOnlineCollectionResponse startOnlineCollectionResponse = startOnlineCollectionRequest(request,protestInfoService);

			if (null != startOnlineCollectionResponse.getToken()){
				map.addAttribute("isSuccess",true);	
			}
			

		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);

			e.printStackTrace(pw);
			map.addAttribute("error", sw.toString());
		} finally {
			if (payDotGovURL_connection != null) {
				
				payDotGovURL_connection.disconnect();
			}
		}
	}
	
	
	*/
/**
	 * @param token
	 * @return
	 * @throws TCSServiceFault_Exception
	 *//*

	public static GetDetailsResponse checkTransactionStatus(String token) throws TCSServiceFault_Exception {
		TCSOnlineService_Service tCSOnlineServiceImplService = new TCSOnlineService_Service();
		TCSOnlineService tCSOnlineService = tCSOnlineServiceImplService
				.getTCSOnlineServicePort();

		CompleteOnlineCollectionRequest completeOnlineCollectionRequest = new CompleteOnlineCollectionRequest();
		completeOnlineCollectionRequest
				.setTcsAppId(Params.payDotGovTcsAppId);
		completeOnlineCollectionRequest.setToken(token);

		CompleteOnlineCollectionResponse completeOnlineCollectionResponse = tCSOnlineService
				.completeOnlineCollection(completeOnlineCollectionRequest);
		String paygovTrackingId = completeOnlineCollectionResponse.getPaygovTrackingId();

		GetDetailsRequest getDetailsRequest = new GetDetailsRequest();
		getDetailsRequest.setPaygovTrackingId(paygovTrackingId);
		getDetailsRequest.setTcsAppId(Params.payDotGovTcsAppId);

		GetDetailsResponse getDetailsResponse = tCSOnlineService
				.getDetails(getDetailsRequest);
		return getDetailsResponse;
	}
	
	
	*/
/**
	 * @param map
	 * @param request
	 * @throws TCSServiceFault_Exception
	 *//*

	public static void startOnlineCollectionRequest(ModelMap map, HttpServletRequest request,ProtestInfoService protestInfoService)
			throws TCSServiceFault_Exception {
		StartOnlineCollectionResponse startOnlineCollectionResponse = startOnlineCollectionRequest(request,protestInfoService);

		EpdsSession.setAttribute(request, "payDotGovToken",
				startOnlineCollectionResponse.getToken());

		map.addAttribute("payDotGovToken",
				startOnlineCollectionResponse.getToken());
		map.addAttribute("payDotGovAppId",Params.payDotGovTcsAppId);
		map.addAttribute("payDotGovPaymentUrl",Params.payDotGovPaymentUrl);
	}
	
	
	*/
/**
	 * @param map
	 * @param request
	 * @param token
	 * @throws TCSServiceFault_Exception
	 *//*

	public static  void checkPayDotGovTransactionStatus(ModelMap map, HttpServletRequest request, String token)
			throws TCSServiceFault_Exception {
		GetDetailsResponse getDetailsResponse = checkTransactionStatus(token);
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
*/
