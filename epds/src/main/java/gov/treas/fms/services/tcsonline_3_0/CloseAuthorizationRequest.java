
package gov.treas.fms.services.tcsonline_3_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CloseAuthorizationRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CloseAuthorizationRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="auth_paygov_tracking_id" type="{http://fms.treas.gov/services/tcsonline_3_0}PaygovTrackingIDType"/&gt;
 *         &lt;element name="payment_type" type="{http://fms.treas.gov/services/tcsonline_3_0}CloseAuthorizationPaymentType"/&gt;
 *         &lt;element name="tcs_app_id" type="{http://fms.treas.gov/services/common}TCSApplicationID"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CloseAuthorizationRequest", propOrder = {
    "authPaygovTrackingId",
    "paymentType",
    "tcsAppId"
})
public class CloseAuthorizationRequest {

    @XmlElement(name = "auth_paygov_tracking_id", required = true)
    protected String authPaygovTrackingId;
    @XmlElement(name = "payment_type", required = true)
    @XmlSchemaType(name = "string")
    protected CloseAuthorizationPaymentType paymentType;
    @XmlElement(name = "tcs_app_id", required = true)
    protected String tcsAppId;

    /**
     * Gets the value of the authPaygovTrackingId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthPaygovTrackingId() {
        return authPaygovTrackingId;
    }

    /**
     * Sets the value of the authPaygovTrackingId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthPaygovTrackingId(String value) {
        this.authPaygovTrackingId = value;
    }

    /**
     * Gets the value of the paymentType property.
     * 
     * @return
     *     possible object is
     *     {@link CloseAuthorizationPaymentType }
     *     
     */
    public CloseAuthorizationPaymentType getPaymentType() {
        return paymentType;
    }

    /**
     * Sets the value of the paymentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link CloseAuthorizationPaymentType }
     *     
     */
    public void setPaymentType(CloseAuthorizationPaymentType value) {
        this.paymentType = value;
    }

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

}
