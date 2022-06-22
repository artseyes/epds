package gov.gao.epds.service.recaptcha;

import gov.gao.epds.utils.GlobalParams;
import gov.gao.epds.utils.PropertyFileEncrypter;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author MHussaini
 *
 */
@Service
public class RecaptchaServiceImpl implements RecaptchaService {

    private static class RecaptchaResponse {
        @JsonProperty("success")
        private boolean success;
        @JsonProperty("error-codes")
        private Collection<String> errorCodes;

        @Override
        public String toString() {
            return "RecaptchaResponse{" +
                    "success=" + success +
                    ", errorCodes=" + errorCodes +
                    '}';
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RecaptchaServiceImpl.class);
    
    private final RestTemplate restTemplate;

    private String recaptchaUrl = PropertyFileEncrypter.decrypt(GlobalParams.prop
			.getProperty("recaptchaUrl"));

    private String recaptchaSecretKey = PropertyFileEncrypter.decrypt(GlobalParams.prop
			.getProperty("recaptchaSiteKey"));

    @Autowired
    public RecaptchaServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean isResponseValid(String remoteIp, String response) {
    	
    	
    	if (LOGGER.isDebugEnabled()){
    		LOGGER.debug("Validating captcha response for remoteIp={}, response={}", remoteIp, response);
    	}
    	
        RecaptchaResponse recaptchaResponse;
        try {
            recaptchaResponse = restTemplate.postForEntity(
                    recaptchaUrl, createBody(recaptchaSecretKey, remoteIp, response), RecaptchaResponse.class).getBody();
        } catch (RestClientException e) {
        	e.printStackTrace();
        	
            throw new RecaptchaServiceException("Recaptcha API not available exception", e);
        }
        if (recaptchaResponse.success) {
            return true;
        } else {
            LOGGER.debug("Unsuccessful recaptchaResponse={}", recaptchaResponse);
            return false;
        }
    }

    private MultiValueMap<String, String> createBody(String secret, String remoteIp, String response) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("secret", secret);
        form.add("remoteip", remoteIp);
        form.add("response", response);
        return form;
    }

}
