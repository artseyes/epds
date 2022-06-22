
package gov.treas.fms.services.tcsonline_3_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CompleteOnlineCollectionWithDetailsResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CompleteOnlineCollectionWithDetailsResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="completeOnlineCollectionWithDetailsResponse" type="{http://fms.treas.gov/services/tcsonline_3_0}CompleteOnlineCollectionWithDetailsResponse" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CompleteOnlineCollectionWithDetailsResponseType", propOrder = {
    "completeOnlineCollectionWithDetailsResponse"
})
public class CompleteOnlineCollectionWithDetailsResponseType {

    protected CompleteOnlineCollectionWithDetailsResponse completeOnlineCollectionWithDetailsResponse;

    /**
     * Gets the value of the completeOnlineCollectionWithDetailsResponse property.
     * 
     * @return
     *     possible object is
     *     {@link CompleteOnlineCollectionWithDetailsResponse }
     *     
     */
    public CompleteOnlineCollectionWithDetailsResponse getCompleteOnlineCollectionWithDetailsResponse() {
        return completeOnlineCollectionWithDetailsResponse;
    }

    /**
     * Sets the value of the completeOnlineCollectionWithDetailsResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompleteOnlineCollectionWithDetailsResponse }
     *     
     */
    public void setCompleteOnlineCollectionWithDetailsResponse(CompleteOnlineCollectionWithDetailsResponse value) {
        this.completeOnlineCollectionWithDetailsResponse = value;
    }

}
