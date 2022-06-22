
package gov.treas.fms.services.tcsonline_3_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CompleteOnlineCollectionWithDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CompleteOnlineCollectionWithDetails"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="completeOnlineCollectionWithDetailsRequest" type="{http://fms.treas.gov/services/tcsonline_3_0}CompleteOnlineCollectionWithDetailsRequest"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CompleteOnlineCollectionWithDetails", propOrder = {
    "completeOnlineCollectionWithDetailsRequest"
})
public class CompleteOnlineCollectionWithDetails {

    @XmlElement(required = true)
    protected CompleteOnlineCollectionWithDetailsRequest completeOnlineCollectionWithDetailsRequest;

    /**
     * Gets the value of the completeOnlineCollectionWithDetailsRequest property.
     * 
     * @return
     *     possible object is
     *     {@link CompleteOnlineCollectionWithDetailsRequest }
     *     
     */
    public CompleteOnlineCollectionWithDetailsRequest getCompleteOnlineCollectionWithDetailsRequest() {
        return completeOnlineCollectionWithDetailsRequest;
    }

    /**
     * Sets the value of the completeOnlineCollectionWithDetailsRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompleteOnlineCollectionWithDetailsRequest }
     *     
     */
    public void setCompleteOnlineCollectionWithDetailsRequest(CompleteOnlineCollectionWithDetailsRequest value) {
        this.completeOnlineCollectionWithDetailsRequest = value;
    }

}
