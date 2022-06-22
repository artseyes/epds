
package gov.treas.fms.services.tcsonline_3_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetDetails"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="getDetailsRequest" type="{http://fms.treas.gov/services/tcsonline_3_0}GetDetailsRequest"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetDetails", propOrder = {
    "getDetailsRequest"
})
public class GetDetails {

    @XmlElement(required = true)
    protected GetDetailsRequest getDetailsRequest;

    /**
     * Gets the value of the getDetailsRequest property.
     * 
     * @return
     *     possible object is
     *     {@link GetDetailsRequest }
     *     
     */
    public GetDetailsRequest getGetDetailsRequest() {
        return getDetailsRequest;
    }

    /**
     * Sets the value of the getDetailsRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetDetailsRequest }
     *     
     */
    public void setGetDetailsRequest(GetDetailsRequest value) {
        this.getDetailsRequest = value;
    }

}
