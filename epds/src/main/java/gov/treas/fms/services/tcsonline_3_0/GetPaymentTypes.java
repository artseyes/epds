
package gov.treas.fms.services.tcsonline_3_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetPaymentTypes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetPaymentTypes"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="getPaymentTypesRequest" type="{http://fms.treas.gov/services/tcsonline_3_0}GetPaymentTypesRequest"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetPaymentTypes", propOrder = {
    "getPaymentTypesRequest"
})
public class GetPaymentTypes {

    @XmlElement(required = true)
    protected GetPaymentTypesRequest getPaymentTypesRequest;

    /**
     * Gets the value of the getPaymentTypesRequest property.
     * 
     * @return
     *     possible object is
     *     {@link GetPaymentTypesRequest }
     *     
     */
    public GetPaymentTypesRequest getGetPaymentTypesRequest() {
        return getPaymentTypesRequest;
    }

    /**
     * Sets the value of the getPaymentTypesRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetPaymentTypesRequest }
     *     
     */
    public void setGetPaymentTypesRequest(GetPaymentTypesRequest value) {
        this.getPaymentTypesRequest = value;
    }

}
