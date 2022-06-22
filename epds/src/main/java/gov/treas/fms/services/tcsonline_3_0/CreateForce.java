
package gov.treas.fms.services.tcsonline_3_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CreateForce complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CreateForce"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="createForceRequest" type="{http://fms.treas.gov/services/tcsonline_3_0}CreateForceRequest"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreateForce", propOrder = {
    "createForceRequest"
})
public class CreateForce {

    @XmlElement(required = true)
    protected CreateForceRequest createForceRequest;

    /**
     * Gets the value of the createForceRequest property.
     * 
     * @return
     *     possible object is
     *     {@link CreateForceRequest }
     *     
     */
    public CreateForceRequest getCreateForceRequest() {
        return createForceRequest;
    }

    /**
     * Sets the value of the createForceRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link CreateForceRequest }
     *     
     */
    public void setCreateForceRequest(CreateForceRequest value) {
        this.createForceRequest = value;
    }

}
