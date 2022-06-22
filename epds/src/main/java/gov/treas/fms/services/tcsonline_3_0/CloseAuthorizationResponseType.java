
package gov.treas.fms.services.tcsonline_3_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CloseAuthorizationResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CloseAuthorizationResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="closeAuthorizationResponse" type="{http://fms.treas.gov/services/tcsonline_3_0}CloseAuthorizationResponse" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CloseAuthorizationResponseType", propOrder = {
    "closeAuthorizationResponse"
})
public class CloseAuthorizationResponseType {

    protected CloseAuthorizationResponse closeAuthorizationResponse;

    /**
     * Gets the value of the closeAuthorizationResponse property.
     * 
     * @return
     *     possible object is
     *     {@link CloseAuthorizationResponse }
     *     
     */
    public CloseAuthorizationResponse getCloseAuthorizationResponse() {
        return closeAuthorizationResponse;
    }

    /**
     * Sets the value of the closeAuthorizationResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link CloseAuthorizationResponse }
     *     
     */
    public void setCloseAuthorizationResponse(CloseAuthorizationResponse value) {
        this.closeAuthorizationResponse = value;
    }

}
