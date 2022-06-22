package gov.gao.epds.utils;

public final class Params {



	public static final String PAY_DOT_GOV_WSDL_LOCATION = "https://qa.tcs.pay.gov/services/TCSOnlineService/2.2/?WSDL";
	/*static {

		if (GlobalParams.IP.toString().contains("199.134.74.73")){
		   PAY_DOT_GOV_WSDL_LOCATION = "https://tcs.pay.gov/services/TCSOnlineService/2.2/?WSDL";
		}else{
		   PAY_DOT_GOV_WSDL_LOCATION = "https://qa.tcs.pay.gov/services/TCSOnlineService/2.2/?WSDL";
		}
	}*/


	public final static String payDotGovSuccessURL = PropertyFileEncrypter.decrypt(GlobalParams.prop
			.getProperty("payDotGovSuccessURL"));
	public final static String payDotGovFailURL = PropertyFileEncrypter.decrypt(GlobalParams.prop
			.getProperty("payDotGovFailURL"));
	public final static String javaTrustStoreLocation = PropertyFileEncrypter.decrypt(GlobalParams.prop
			.getProperty("javaTrustStoreLocation"));
	public final static String payDotGovURL = PropertyFileEncrypter.decrypt(GlobalParams.prop
			.getProperty("payDotGovURL"));
	public final static String payDotGovCertPassword = PropertyFileEncrypter.decrypt(GlobalParams.prop
			.getProperty("payDotGovCertPassword"));
	public final static String payDotGovTcsAppId = PropertyFileEncrypter.decrypt(GlobalParams.prop
			.getProperty("payDotGovTcsAppId"));
	public final static String payDotGovCertRelativePath = PropertyFileEncrypter.decrypt(GlobalParams.prop
			.getProperty("payDotGovCertRelativePath"));
	public final static String removeCasePassword = PropertyFileEncrypter.decrypt(GlobalParams.prop
			.getProperty("removeCasePassword"));
	
	public final static String payDotGovPaymentUrl = PropertyFileEncrypter.decrypt(GlobalParams.prop
			.getProperty("payDotGovPaymentUrl"));

}
