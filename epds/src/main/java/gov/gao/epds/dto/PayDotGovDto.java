/**
 * 
 */
package gov.gao.epds.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author MHussaini
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayDotGovDto {

	private String agencyTrackingId;
	private String aNo;
	private String paydotgovToken;
	private String payDotgovTransactionStatus;
	private String payDotGovTrackingId;
	
	
	public String getAgencyTrackingId() {
		return agencyTrackingId;
	}
	public void setAgencyTrackingId(String agencyTrackingId) {
		this.agencyTrackingId = agencyTrackingId;
	}
	public String getaNo() {
		return aNo;
	}
	public void setaNo(String aNo) {
		this.aNo = aNo;
	}
	public String getPaydotgovToken() {
		return paydotgovToken;
	}
	public void setPaydotgovToken(String paydotgovToken) {
		this.paydotgovToken = paydotgovToken;
	}
	public String getPayDotgovTransactionStatus() {
		return payDotgovTransactionStatus;
	}
	public void setPayDotgovTransactionStatus(String payDotgovTransactionStatus) {
		this.payDotgovTransactionStatus = payDotgovTransactionStatus;
	}
	public String getPayDotGovTrackingId() {
		return payDotGovTrackingId;
	}
	public void setPayDotGovTrackingId(String payDotGovTrackingId) {
		this.payDotGovTrackingId = payDotGovTrackingId;
	}
	
}
