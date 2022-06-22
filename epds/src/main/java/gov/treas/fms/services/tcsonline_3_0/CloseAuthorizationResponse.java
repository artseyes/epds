
package gov.treas.fms.services.tcsonline_3_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CloseAuthorizationResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CloseAuthorizationResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="auth_paygov_tracking_id" type="{http://fms.treas.gov/services/common}paygov_tracking_id"/&gt;
 *         &lt;element name="auth_status" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CloseAuthorizationResponse", propOrder = {
    "authPaygovTrackingId",
    "authStatus"
})
public class CloseAuthorizationResponse {

    @XmlElement(name = "auth_paygov_tracking_id", required = true)
    protected String authPaygovTrackingId;
    @XmlElement(name = "auth_status", required = true)
    protected String authStatus;

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
     * Gets the value of the authStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthStatus() {
        return authStatus;
    }

    /**
     * Sets the value of the authStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthStatus(String value) {
        this.authStatus = value;
    }

}
