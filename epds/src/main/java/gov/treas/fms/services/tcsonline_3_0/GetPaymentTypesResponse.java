
package gov.treas.fms.services.tcsonline_3_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetPaymentTypesResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetPaymentTypesResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="paymentTypes" type="{http://fms.treas.gov/services/tcsonline_3_0}paymentTypes"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetPaymentTypesResponse", propOrder = {
    "paymentTypes"
})
public class GetPaymentTypesResponse {

    @XmlElement(required = true)
    protected PaymentTypes paymentTypes;

    /**
     * Gets the value of the paymentTypes property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentTypes }
     *     
     */
    public PaymentTypes getPaymentTypes() {
        return paymentTypes;
    }

    /**
     * Sets the value of the paymentTypes property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentTypes }
     *     
     */
    public void setPaymentTypes(PaymentTypes value) {
        this.paymentTypes = value;
    }

}
