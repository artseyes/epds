
package gov.treas.fms.services.tcsonline_3_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CreateForceWithDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CreateForceWithDetails"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="createForceWithDetailsRequest" type="{http://fms.treas.gov/services/tcsonline_3_0}CreateForceWithDetailsRequest"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreateForceWithDetails", propOrder = {
    "createForceWithDetailsRequest"
})
public class CreateForceWithDetails {

    @XmlElement(required = true)
    protected CreateForceWithDetailsRequest createForceWithDetailsRequest;

    /**
     * Gets the value of the createForceWithDetailsRequest property.
     * 
     * @return
     *     possible object is
     *     {@link CreateForceWithDetailsRequest }
     *     
     */
    public CreateForceWithDetailsRequest getCreateForceWithDetailsRequest() {
        return createForceWithDetailsRequest;
    }

    /**
     * Sets the value of the createForceWithDetailsRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link CreateForceWithDetailsRequest }
     *     
     */
    public void setCreateForceWithDetailsRequest(CreateForceWithDetailsRequest value) {
        this.createForceWithDetailsRequest = value;
    }

}
