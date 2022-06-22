
package gov.treas.fms.services.tcsonline_3_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CompleteOnlineCollection complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CompleteOnlineCollection"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="completeOnlineCollectionRequest" type="{http://fms.treas.gov/services/tcsonline_3_0}CompleteOnlineCollectionRequest"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CompleteOnlineCollection", propOrder = {
    "completeOnlineCollectionRequest"
})
public class CompleteOnlineCollection {

    @XmlElement(required = true)
    protected CompleteOnlineCollectionRequest completeOnlineCollectionRequest;

    /**
     * Gets the value of the completeOnlineCollectionRequest property.
     * 
     * @return
     *     possible object is
     *     {@link CompleteOnlineCollectionRequest }
     *     
     */
    public CompleteOnlineCollectionRequest getCompleteOnlineCollectionRequest() {
        return completeOnlineCollectionRequest;
    }

    /**
     * Sets the value of the completeOnlineCollectionRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompleteOnlineCollectionRequest }
     *     
     */
    public void setCompleteOnlineCollectionRequest(CompleteOnlineCollectionRequest value) {
        this.completeOnlineCollectionRequest = value;
    }

}
