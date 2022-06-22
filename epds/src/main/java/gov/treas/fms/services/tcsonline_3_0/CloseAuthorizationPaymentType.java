
package gov.treas.fms.services.tcsonline_3_0;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CloseAuthorizationPaymentType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CloseAuthorizationPaymentType"&gt;
 *   &lt;restriction base="{http://fms.treas.gov/services/common}TCSStringASCII2"&gt;
 *     &lt;enumeration value="PAYPAL"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "CloseAuthorizationPaymentType")
@XmlEnum
public enum CloseAuthorizationPaymentType {

    PAYPAL;

    public String value() {
        return name();
    }

    public static CloseAuthorizationPaymentType fromValue(String v) {
        return valueOf(v);
    }

}
