
package gov.treas.fms.services.tcsonline_3_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CustomCollectionFields complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CustomCollectionFields"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="custom_field_1" type="{http://fms.treas.gov/services/common}CustomFieldValue" minOccurs="0"/&gt;
 *         &lt;element name="custom_field_2" type="{http://fms.treas.gov/services/common}CustomFieldValue" minOccurs="0"/&gt;
 *         &lt;element name="custom_field_3" type="{http://fms.treas.gov/services/common}CustomFieldValue" minOccurs="0"/&gt;
 *         &lt;element name="custom_field_4" type="{http://fms.treas.gov/services/common}CustomFieldValue" minOccurs="0"/&gt;
 *         &lt;element name="custom_field_5" type="{http://fms.treas.gov/services/common}CustomFieldValue" minOccurs="0"/&gt;
 *         &lt;element name="custom_field_6" type="{http://fms.treas.gov/services/common}CustomFieldValue" minOccurs="0"/&gt;
 *         &lt;element name="custom_field_7" type="{http://fms.treas.gov/services/common}CustomFieldValue" minOccurs="0"/&gt;
 *         &lt;element name="custom_field_8" type="{http://fms.treas.gov/services/common}CustomFieldValue" minOccurs="0"/&gt;
 *         &lt;element name="custom_field_9" type="{http://fms.treas.gov/services/common}CustomFieldValue" minOccurs="0"/&gt;
 *         &lt;element name="custom_field_10" type="{http://fms.treas.gov/services/common}CustomFieldValue" minOccurs="0"/&gt;
 *         &lt;element name="custom_field_11" type="{http://fms.treas.gov/services/common}CustomFieldValue" minOccurs="0"/&gt;
 *         &lt;element name="custom_field_12" type="{http://fms.treas.gov/services/common}CustomFieldValue" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomCollectionFields", propOrder = {
    "customField1",
    "customField2",
    "customField3",
    "customField4",
    "customField5",
    "customField6",
    "customField7",
    "customField8",
    "customField9",
    "customField10",
    "customField11",
    "customField12"
})
public class CustomCollectionFields {

    @XmlElement(name = "custom_field_1")
    protected String customField1;
    @XmlElement(name = "custom_field_2")
    protected String customField2;
    @XmlElement(name = "custom_field_3")
    protected String customField3;
    @XmlElement(name = "custom_field_4")
    protected String customField4;
    @XmlElement(name = "custom_field_5")
    protected String customField5;
    @XmlElement(name = "custom_field_6")
    protected String customField6;
    @XmlElement(name = "custom_field_7")
    protected String customField7;
    @XmlElement(name = "custom_field_8")
    protected String customField8;
    @XmlElement(name = "custom_field_9")
    protected String customField9;
    @XmlElement(name = "custom_field_10")
    protected String customField10;
    @XmlElement(name = "custom_field_11")
    protected String customField11;
    @XmlElement(name = "custom_field_12")
    protected String customField12;

    /**
     * Gets the value of the customField1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomField1() {
        return customField1;
    }

    /**
     * Sets the value of the customField1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomField1(String value) {
        this.customField1 = value;
    }

    /**
     * Gets the value of the customField2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomField2() {
        return customField2;
    }

    /**
     * Sets the value of the customField2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomField2(String value) {
        this.customField2 = value;
    }

    /**
     * Gets the value of the customField3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomField3() {
        return customField3;
    }

    /**
     * Sets the value of the customField3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomField3(String value) {
        this.customField3 = value;
    }

    /**
     * Gets the value of the customField4 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomField4() {
        return customField4;
    }

    /**
     * Sets the value of the customField4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomField4(String value) {
        this.customField4 = value;
    }

    /**
     * Gets the value of the customField5 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomField5() {
        return customField5;
    }

    /**
     * Sets the value of the customField5 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomField5(String value) {
        this.customField5 = value;
    }

    /**
     * Gets the value of the customField6 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomField6() {
        return customField6;
    }

    /**
     * Sets the value of the customField6 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomField6(String value) {
        this.customField6 = value;
    }

    /**
     * Gets the value of the customField7 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomField7() {
        return customField7;
    }

    /**
     * Sets the value of the customField7 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomField7(String value) {
        this.customField7 = value;
    }

    /**
     * Gets the value of the customField8 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomField8() {
        return customField8;
    }

    /**
     * Sets the value of the customField8 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomField8(String value) {
        this.customField8 = value;
    }

    /**
     * Gets the value of the customField9 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomField9() {
        return customField9;
    }

    /**
     * Sets the value of the customField9 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomField9(String value) {
        this.customField9 = value;
    }

    /**
     * Gets the value of the customField10 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomField10() {
        return customField10;
    }

    /**
     * Sets the value of the customField10 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomField10(String value) {
        this.customField10 = value;
    }

    /**
     * Gets the value of the customField11 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomField11() {
        return customField11;
    }

    /**
     * Sets the value of the customField11 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomField11(String value) {
        this.customField11 = value;
    }

    /**
     * Gets the value of the customField12 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomField12() {
        return customField12;
    }

    /**
     * Sets the value of the customField12 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomField12(String value) {
        this.customField12 = value;
    }

}
