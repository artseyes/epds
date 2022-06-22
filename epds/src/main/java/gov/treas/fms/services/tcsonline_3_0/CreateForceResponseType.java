
package gov.treas.fms.services.tcsonline_3_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CreateForceResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CreateForceResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="createForceResponse" type="{http://fms.treas.gov/services/tcsonline_3_0}CreateForceResponse" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreateForceResponseType", propOrder = {
    "createForceResponse"
})
public class CreateForceResponseType {

    protected CreateForceResponse createForceResponse;

    /**
     * Gets the value of the createForceResponse property.
     * 
     * @return
     *     possible object is
     *     {@link CreateForceResponse }
     *     
     */
    public CreateForceResponse getCreateForceResponse() {
        return createForceResponse;
    }

    /**
     * Sets the value of the createForceResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link CreateForceResponse }
     *     
     */
    public void setCreateForceResponse(CreateForceResponse value) {
        this.createForceResponse = value;
    }

}
