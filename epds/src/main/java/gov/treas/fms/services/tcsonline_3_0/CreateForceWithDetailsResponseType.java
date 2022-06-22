
package gov.treas.fms.services.tcsonline_3_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CreateForceWithDetailsResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CreateForceWithDetailsResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="createForceWithDetailsResponse" type="{http://fms.treas.gov/services/tcsonline_3_0}CreateForceWithDetailsResponse" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreateForceWithDetailsResponseType", propOrder = {
    "createForceWithDetailsResponse"
})
public class CreateForceWithDetailsResponseType {

    protected CreateForceWithDetailsResponse createForceWithDetailsResponse;

    /**
     * Gets the value of the createForceWithDetailsResponse property.
     * 
     * @return
     *     possible object is
     *     {@link CreateForceWithDetailsResponse }
     *     
     */
    public CreateForceWithDetailsResponse getCreateForceWithDetailsResponse() {
        return createForceWithDetailsResponse;
    }

    /**
     * Sets the value of the createForceWithDetailsResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link CreateForceWithDetailsResponse }
     *     
     */
    public void setCreateForceWithDetailsResponse(CreateForceWithDetailsResponse value) {
        this.createForceWithDetailsResponse = value;
    }

}
