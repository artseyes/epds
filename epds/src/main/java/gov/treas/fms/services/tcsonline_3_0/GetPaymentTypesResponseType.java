
package gov.treas.fms.services.tcsonline_3_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetPaymentTypesResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetPaymentTypesResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="getPaymentTypesResponse" type="{http://fms.treas.gov/services/tcsonline_3_0}GetPaymentTypesResponse" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetPaymentTypesResponseType", propOrder = {
    "getPaymentTypesResponse"
})
public class GetPaymentTypesResponseType {

    protected GetPaymentTypesResponse getPaymentTypesResponse;

    /**
     * Gets the value of the getPaymentTypesResponse property.
     * 
     * @return
     *     possible object is
     *     {@link GetPaymentTypesResponse }
     *     
     */
    public GetPaymentTypesResponse getGetPaymentTypesResponse() {
        return getPaymentTypesResponse;
    }

    /**
     * Sets the value of the getPaymentTypesResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetPaymentTypesResponse }
     *     
     */
    public void setGetPaymentTypesResponse(GetPaymentTypesResponse value) {
        this.getPaymentTypesResponse = value;
    }

}
