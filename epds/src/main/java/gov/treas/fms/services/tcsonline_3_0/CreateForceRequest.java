
package gov.treas.fms.services.tcsonline_3_0;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CreateForceRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CreateForceRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice&gt;
 *           &lt;element name="paygov_tracking_id" type="{http://fms.treas.gov/services/common}paygov_tracking_id" minOccurs="0"/&gt;
 *           &lt;element name="agency_tracking_id" type="{http://fms.treas.gov/services/common}AgencyTrackingID" minOccurs="0"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="tcs_app_id" type="{http://fms.treas.gov/services/common}TCSApplicationID"/&gt;
 *         &lt;element name="transaction_amount" type="{http://fms.treas.gov/services/common}DollarAmount"/&gt;
 *         &lt;element name="classification" type="{http://fms.treas.gov/services/tcsonline_3_0}classification" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreateForceRequest", propOrder = {
    "paygovTrackingId",
    "agencyTrackingId",
    "tcsAppId",
    "transactionAmount",
    "classification"
})
public class CreateForceRequest {

    @XmlElement(name = "paygov_tracking_id")
    protected String paygovTrackingId;
    @XmlElement(name = "agency_tracking_id")
    protected String agencyTrackingId;
    @XmlElement(name = "tcs_app_id", required = true)
    protected String tcsAppId;
    @XmlElement(name = "transaction_amount", required = true)
    protected BigDecimal transactionAmount;
    protected Classification classification;

    /**
     * Gets the value of the paygovTrackingId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaygovTrackingId() {
        return paygovTrackingId;
    }

    /**
     * Sets the value of the paygovTrackingId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaygovTrackingId(String value) {
        this.paygovTrackingId = value;
    }

    /**
     * Gets the value of the agencyTrackingId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgencyTrackingId() {
        return agencyTrackingId;
    }

    /**
     * Sets the value of the agencyTrackingId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgencyTrackingId(String value) {
        this.agencyTrackingId = value;
    }

    /**
     * Gets the value of the tcsAppId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTcsAppId() {
        return tcsAppId;
    }

    /**
     * Sets the value of the tcsAppId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTcsAppId(String value) {
        this.tcsAppId = value;
    }

    /**
     * Gets the value of the transactionAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    /**
     * Sets the value of the transactionAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTransactionAmount(BigDecimal value) {
        this.transactionAmount = value;
    }

    /**
     * Gets the value of the classification property.
     * 
     * @return
     *     possible object is
     *     {@link Classification }
     *     
     */
    public Classification getClassification() {
        return classification;
    }

    /**
     * Sets the value of the classification property.
     * 
     * @param value
     *     allowed object is
     *     {@link Classification }
     *     
     */
    public void setClassification(Classification value) {
        this.classification = value;
    }

}
