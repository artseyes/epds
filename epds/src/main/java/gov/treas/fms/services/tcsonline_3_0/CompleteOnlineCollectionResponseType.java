
package gov.treas.fms.services.tcsonline_3_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CompleteOnlineCollectionResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CompleteOnlineCollectionResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="completeOnlineCollectionResponse" type="{http://fms.treas.gov/services/tcsonline_3_0}CompleteOnlineCollectionResponse" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CompleteOnlineCollectionResponseType", propOrder = {
    "completeOnlineCollectionResponse"
})
public class CompleteOnlineCollectionResponseType {

    protected CompleteOnlineCollectionResponse completeOnlineCollectionResponse;

    /**
     * Gets the value of the completeOnlineCollectionResponse property.
     * 
     * @return
     *     possible object is
     *     {@link CompleteOnlineCollectionResponse }
     *     
     */
    public CompleteOnlineCollectionResponse getCompleteOnlineCollectionResponse() {
        return completeOnlineCollectionResponse;
    }

    /**
     * Sets the value of the completeOnlineCollectionResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompleteOnlineCollectionResponse }
     *     
     */
    public void setCompleteOnlineCollectionResponse(CompleteOnlineCollectionResponse value) {
        this.completeOnlineCollectionResponse = value;
    }

}
