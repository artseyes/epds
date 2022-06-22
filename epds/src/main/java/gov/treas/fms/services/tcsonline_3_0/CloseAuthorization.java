
package gov.treas.fms.services.tcsonline_3_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CloseAuthorization complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CloseAuthorization"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="closeAuthorizationRequest" type="{http://fms.treas.gov/services/tcsonline_3_0}CloseAuthorizationRequest"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CloseAuthorization", propOrder = {
    "closeAuthorizationRequest"
})
public class CloseAuthorization {

    @XmlElement(required = true)
    protected CloseAuthorizationRequest closeAuthorizationRequest;

    /**
     * Gets the value of the closeAuthorizationRequest property.
     * 
     * @return
     *     possible object is
     *     {@link CloseAuthorizationRequest }
     *     
     */
    public CloseAuthorizationRequest getCloseAuthorizationRequest() {
        return closeAuthorizationRequest;
    }

    /**
     * Sets the value of the closeAuthorizationRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link CloseAuthorizationRequest }
     *     
     */
    public void setCloseAuthorizationRequest(CloseAuthorizationRequest value) {
        this.closeAuthorizationRequest = value;
    }

}
