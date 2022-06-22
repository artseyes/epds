
package gov.treas.fms.services.tcsonline_3_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetDetailsRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetDetailsRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="tcs_app_id" type="{http://fms.treas.gov/services/common}TCSApplicationID"/&gt;
 *         &lt;group ref="{http://fms.treas.gov/services/tcsonline_3_0}getDetailsSearchCriteria" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetDetailsRequest", propOrder = {
    "tcsAppId",
    "agencyTrackingId",
    "paygovTrackingId"
})
public class GetDetailsRequest {

    @XmlElement(name = "tcs_app_id", required = true)
    protected String tcsAppId;
    @XmlElement(name = "agency_tracking_id")
    protected String agencyTrackingId;
    @XmlElement(name = "paygov_tracking_id")
    protected String paygovTrackingId;

    /**
     * Gets the value of the tcsAppId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTcsAppId() {
        return tcsAppId;
    }

    /**
     * Sets the value of the tcsAppId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTcsAppId(String value) {
        this.tcsAppId = value;
    }

    /**
     * Gets the value of the agencyTrackingId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgencyTrackingId() {
        return agencyTrackingId;
    }

    /**
     * Sets the value of the agencyTrackingId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgencyTrackingId(String value) {
        this.agencyTrackingId = value;
    }

    /**
     * Gets the value of the paygovTrackingId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaygovTrackingId() {
        return paygovTrackingId;
    }

    /**
     * Sets the value of the paygovTrackingId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaygovTrackingId(String value) {
        this.paygovTrackingId = value;
    }

}
