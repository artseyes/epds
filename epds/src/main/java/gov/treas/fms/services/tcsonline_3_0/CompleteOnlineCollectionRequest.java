
package gov.treas.fms.services.tcsonline_3_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CompleteOnlineCollectionRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CompleteOnlineCollectionRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="tcs_app_id" type="{http://fms.treas.gov/services/common}TCSApplicationID"/&gt;
 *         &lt;element name="token" type="{http://fms.treas.gov/services/tcsonline_3_0}Token"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CompleteOnlineCollectionRequest", propOrder = {
    "tcsAppId",
    "token"
})
public class CompleteOnlineCollectionRequest {

    @XmlElement(name = "tcs_app_id", required = true)
    protected String tcsAppId;
    @XmlElement(required = true)
    protected String token;

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
     * Gets the value of the token property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the value of the token property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToken(String value) {
        this.token = value;
    }

}
