
package gov.treas.fms.services.tcsonline_3_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetDetailsResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetDetailsResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="getDetailsResponse" type="{http://fms.treas.gov/services/tcsonline_3_0}GetDetailsResponse" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetDetailsResponseType", propOrder = {
    "getDetailsResponse"
})
public class GetDetailsResponseType {

    protected GetDetailsResponse getDetailsResponse;

    /**
     * Gets the value of the getDetailsResponse property.
     * 
     * @return
     *     possible object is
     *     {@link GetDetailsResponse }
     *     
     */
    public GetDetailsResponse getGetDetailsResponse() {
        return getDetailsResponse;
    }

    /**
     * Sets the value of the getDetailsResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetDetailsResponse }
     *     
     */
    public void setGetDetailsResponse(GetDetailsResponse value) {
        this.getDetailsResponse = value;
    }

}
