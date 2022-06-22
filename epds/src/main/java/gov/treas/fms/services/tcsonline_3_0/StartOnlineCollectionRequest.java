
package gov.treas.fms.services.tcsonline_3_0;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StartOnlineCollectionRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StartOnlineCollectionRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="tcs_app_id" type="{http://fms.treas.gov/services/common}TCSApplicationID"/&gt;
 *         &lt;element name="agency_tracking_id" type="{http://fms.treas.gov/services/common}AgencyTrackingID"/&gt;
 *         &lt;element name="transaction_type" type="{http://fms.treas.gov/services/tcsonline_3_0}TransactionType"/&gt;
 *         &lt;element name="transaction_amount" type="{http://fms.treas.gov/services/common}TCSDollarAmount"/&gt;
 *         &lt;element name="language" type="{http://fms.treas.gov/services/tcsonline_3_0}Language"/&gt;
 *         &lt;element name="url_success" type="{http://fms.treas.gov/services/tcsonline_3_0}TCSOUrl"/&gt;
 *         &lt;element name="url_cancel" type="{http://fms.treas.gov/services/tcsonline_3_0}TCSOUrl"/&gt;
 *         &lt;element name="account_holder_name" type="{http://fms.treas.gov/services/common}AccountHolderName" minOccurs="0"/&gt;
 *         &lt;group ref="{http://fms.treas.gov/services/tcsonline_3_0}billingAddressGroup"/&gt;
 *         &lt;element name="email_address" type="{http://fms.treas.gov/services/common}TCSEmail" minOccurs="0"/&gt;
 *         &lt;element name="custom_fields" type="{http://fms.treas.gov/services/tcsonline_3_0}CustomCollectionFields" minOccurs="0"/&gt;
 *         &lt;element name="classification" type="{http://fms.treas.gov/services/tcsonline_3_0}classification" minOccurs="0"/&gt;
 *         &lt;element name="payment_type" type="{http://fms.treas.gov/services/tcsonline_3_0}PaymentType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StartOnlineCollectionRequest", propOrder = {
    "tcsAppId",
    "agencyTrackingId",
    "transactionType",
    "transactionAmount",
    "language",
    "urlSuccess",
    "urlCancel",
    "accountHolderName",
    "billingAddress",
    "billingAddress2",
    "billingCity",
    "billingState",
    "billingZip",
    "billingCountry",
    "emailAddress",
    "customFields",
    "classification",
    "paymentType"
})
public class StartOnlineCollectionRequest {

    @XmlElement(name = "tcs_app_id", required = true)
    protected String tcsAppId;
    @XmlElement(name = "agency_tracking_id", required = true)
    protected String agencyTrackingId;
    @XmlElement(name = "transaction_type", required = true)
    @XmlSchemaType(name = "string")
    protected TransactionType transactionType;
    @XmlElement(name = "transaction_amount", required = true)
    protected BigDecimal transactionAmount;
    @XmlElement(required = true)
    protected String language;
    @XmlElement(name = "url_success", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String urlSuccess;
    @XmlElement(name = "url_cancel", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String urlCancel;
    @XmlElement(name = "account_holder_name")
    protected String accountHolderName;
    @XmlElement(name = "billing_address")
    protected String billingAddress;
    @XmlElement(name = "billing_address2")
    protected String billingAddress2;
    @XmlElement(name = "billing_city")
    protected String billingCity;
    @XmlElement(name = "billing_state")
    protected String billingState;
    @XmlElement(name = "billing_zip")
    protected String billingZip;
    @XmlElement(name = "billing_country")
    protected String billingCountry;
    @XmlElement(name = "email_address")
    protected String emailAddress;
    @XmlElement(name = "custom_fields")
    protected CustomCollectionFields customFields;
    protected Classification classification;
    @XmlElement(name = "payment_type")
    @XmlSchemaType(name = "string")
    protected PaymentType paymentType;

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
     * Gets the value of the transactionType property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionType }
     *     
     */
    public TransactionType getTransactionType() {
        return transactionType;
    }

    /**
     * Sets the value of the transactionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionType }
     *     
     */
    public void setTransactionType(TransactionType value) {
        this.transactionType = value;
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
     * Gets the value of the language property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the value of the language property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLanguage(String value) {
        this.language = value;
    }

    /**
     * Gets the value of the urlSuccess property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrlSuccess() {
        return urlSuccess;
    }

    /**
     * Sets the value of the urlSuccess property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrlSuccess(String value) {
        this.urlSuccess = value;
    }

    /**
     * Gets the value of the urlCancel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrlCancel() {
        return urlCancel;
    }

    /**
     * Sets the value of the urlCancel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrlCancel(String value) {
        this.urlCancel = value;
    }

    /**
     * Gets the value of the accountHolderName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountHolderName() {
        return accountHolderName;
    }

    /**
     * Sets the value of the accountHolderName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccountHolderName(String value) {
        this.accountHolderName = value;
    }

    /**
     * Gets the value of the billingAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillingAddress() {
        return billingAddress;
    }

    /**
     * Sets the value of the billingAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillingAddress(String value) {
        this.billingAddress = value;
    }

    /**
     * Gets the value of the billingAddress2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillingAddress2() {
        return billingAddress2;
    }

    /**
     * Sets the value of the billingAddress2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillingAddress2(String value) {
        this.billingAddress2 = value;
    }

    /**
     * Gets the value of the billingCity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillingCity() {
        return billingCity;
    }

    /**
     * Sets the value of the billingCity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillingCity(String value) {
        this.billingCity = value;
    }

    /**
     * Gets the value of the billingState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillingState() {
        return billingState;
    }

    /**
     * Sets the value of the billingState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillingState(String value) {
        this.billingState = value;
    }

    /**
     * Gets the value of the billingZip property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillingZip() {
        return billingZip;
    }

    /**
     * Sets the value of the billingZip property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillingZip(String value) {
        this.billingZip = value;
    }

    /**
     * Gets the value of the billingCountry property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillingCountry() {
        return billingCountry;
    }

    /**
     * Sets the value of the billingCountry property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillingCountry(String value) {
        this.billingCountry = value;
    }

    /**
     * Gets the value of the emailAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets the value of the emailAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmailAddress(String value) {
        this.emailAddress = value;
    }

    /**
     * Gets the value of the customFields property.
     * 
     * @return
     *     possible object is
     *     {@link CustomCollectionFields }
     *     
     */
    public CustomCollectionFields getCustomFields() {
        return customFields;
    }

    /**
     * Sets the value of the customFields property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomCollectionFields }
     *     
     */
    public void setCustomFields(CustomCollectionFields value) {
        this.customFields = value;
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

}
