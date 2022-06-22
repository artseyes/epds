package gov.gao.epds.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author MHussaini
 *
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class Recaptcha {
	
	@JsonIgnore
    private String recaptchaResponse;

    public void setRecaptchaResponse(String response) {
        this.recaptchaResponse = response;
    }

    public String getRecaptchaResponse() {
        return recaptchaResponse;
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Recaptcha [recaptchaResponse=");
		builder.append(recaptchaResponse);
		builder.append("]");
		return builder.toString();
	}
    
    
    
}
