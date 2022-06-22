
package gov.treas.fms.services.tcsonline_3_0;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for CompleteOnlineCollectionWithDetailsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CompleteOnlineCollectionWithDetailsResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;group ref="{http://fms.treas.gov/services/tcsonline_3_0}commonHeaderGroup"/&gt;
 *         &lt;group ref="{http://fms.treas.gov/services/tcsonline_3_0}shippingAddressGroup"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CompleteOnlineCollectionWithDetailsResponse", propOrder = {
    "paygovTrackingId",
    "agencyTrackingId",
    "transactionAmount",
    "transactionType",
    "transactionDate",
    "paymentDate",
    "transactionStatus",
    "paymentType",
    "shippingAddress",
    "shippingAddress2",
    "shippingCity",
    "shippingState",
    "shippingZip",
    "shippingCountry",
    "addressReturnMessage"
})
public class CompleteOnlineCollectionWithDetailsResponse {

    @XmlElement(name = "paygov_tracking_id", required = true)
    protected String paygovTrackingId;
    @XmlElement(name = "agency_tracking_id", required = true)
    protected String agencyTrackingId;
    @XmlElement(name = "transaction_amount", required = true)
    protected BigDecimal transactionAmount;
    @XmlElement(name = "transaction_type", required = true)
    protected String transactionType;
    @XmlElement(name = "transaction_date", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar transactionDate;
    @XmlElement(name = "payment_date")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar paymentDate;
    @XmlElement(name = "transaction_status", required = true)
    protected String transactionStatus;
    @XmlElement(name = "payment_type", required = true)
    @XmlSchemaType(name = "string")
    protected PaymentType paymentType;
    @XmlElement(name = "shipping_address")
    protected String shippingAddress;
    @XmlElement(name = "shipping_address2")
    protected String shippingAddress2;
    @XmlElement(name = "shipping_city")
    protected String shippingCity;
    @XmlElement(name = "shipping_state")
    protected String shippingState;
    @XmlElement(name = "shipping_zip")
    protected String shippingZip;
    @XmlElement(name = "shipping_country")
    protected String shippingCountry;
    @XmlElement(name = "address_return_message")
    protected String addressReturnMessage;

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
     * Gets the value of the transactionType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionType() {
        return transactionType;
    }

    /**
     * Sets the value of the transactionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionType(String value) {
        this.transactionType = value;
    }

    /**
     * Gets the value of the transactionDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTransactionDate() {
        return transactionDate;
    }

    /**
     * Sets the value of the transactionDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTransactionDate(XMLGregorianCalendar value) {
        this.transactionDate = value;
    }

    /**
     * Gets the value of the paymentDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPaymentDate() {
        return paymentDate;
    }

    /**
     * Sets the value of the paymentDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPaymentDate(XMLGregorianCalendar value) {
        this.paymentDate = value;
    }

    /**
     * Gets the value of the transactionStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionStatus() {
        return transactionStatus;
    }

    /**
     * Sets the value of the transactionStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionStatus(String value) {
        this.transactionStatus = value;
    }

    /**
     * Gets the value of the paymentType property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentType }
     *     
     */
    public PaymentType getPaymentType() {
        return paymentType;
    }

    /**
     * Sets the value of the paymentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentType }
     *     
     */
    public void setPaymentType(PaymentType value) {
        this.paymentType = value;
    }

    /**
     * Gets the value of the shippingAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShippingAddress() {
        return shippingAddress;
    }

    /**
     * Sets the value of the shippingAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShippingAddress(String value) {
        this.shippingAddress = value;
    }

    /**
     * Gets the value of the shippingAddress2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShippingAddress2() {
        return shippingAddress2;
    }

    /**
     * Sets the value of the shippingAddress2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShippingAddress2(String value) {
        this.shippingAddress2 = value;
    }

    /**
     * Gets the value of the shippingCity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShippingCity() {
        return shippingCity;
    }

    /**
     * Sets the value of the shippingCity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShippingCity(String value) {
        this.shippingCity = value;
    }

    /**
     * Gets the value of the shippingState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShippingState() {
        return shippingState;
    }

    /**
     * Sets the value of the shippingState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShippingState(String value) {
        this.shippingState = value;
    }

    /**
     * Gets the value of the shippingZip property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShippingZip() {
        return shippingZip;
    }

    /**
     * Sets the value of the shippingZip property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShippingZip(String value) {
        this.shippingZip = value;
    }

    /**
     * Gets the value of the shippingCountry property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShippingCountry() {
        return shippingCountry;
    }

    /**
     * Sets the value of the shippingCountry property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShippingCountry(String value) {
        this.shippingCountry = value;
    }

    /**
     * Gets the value of the addressReturnMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddressReturnMessage() {
        return addressReturnMessage;
    }

    /**
     * Sets the value of the addressReturnMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddressReturnMessage(String value) {
        this.addressReturnMessage = value;
    }

}
