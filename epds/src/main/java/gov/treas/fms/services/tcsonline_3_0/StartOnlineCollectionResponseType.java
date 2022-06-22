
package gov.treas.fms.services.tcsonline_3_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StartOnlineCollectionResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StartOnlineCollectionResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="startOnlineCollectionResponse" type="{http://fms.treas.gov/services/tcsonline_3_0}StartOnlineCollectionResponse" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StartOnlineCollectionResponseType", propOrder = {
    "startOnlineCollectionResponse"
})
public class StartOnlineCollectionResponseType {

    protected StartOnlineCollectionResponse startOnlineCollectionResponse;

    /**
     * Gets the value of the startOnlineCollectionResponse property.
     * 
     * @return
     *     possible object is
     *     {@link StartOnlineCollectionResponse }
     *     
     */
    public StartOnlineCollectionResponse getStartOnlineCollectionResponse() {
        return startOnlineCollectionResponse;
    }

    /**
     * Sets the value of the startOnlineCollectionResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link StartOnlineCollectionResponse }
     *     
     */
    public void setStartOnlineCollectionResponse(StartOnlineCollectionResponse value) {
        this.startOnlineCollectionResponse = value;
    }

}
