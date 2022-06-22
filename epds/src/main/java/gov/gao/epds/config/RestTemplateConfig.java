package gov.gao.epds.config;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import gov.gao.epds.service.recaptcha.RecaptchaController;

/**
 * @author MHussaini
 *
 */
@Component
@Configuration
public class RestTemplateConfig {  // NO_UCD (unused code)
	private static final Logger LOGGER = LoggerFactory.getLogger(RecaptchaController.class);
	
    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory httpRequestFactory) {
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        
        return restTemplate;
    }

    @Bean
    public ClientHttpRequestFactory httpRequestFactory(HttpClient httpClient) {
        /*return new HttpComponentsClientHttpRequestFactory(httpClient);*/
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
       
        factory.setReadTimeout(10000);
        factory.setConnectTimeout(10000);

        return factory;
    }
    
    @Bean
    public HttpClient httpClient() {
    	
        return HttpClientBuilder.create().setRetryHandler((exception, executionCount, context) -> {
            if (executionCount > 3) {
                LOGGER.warn("Maximum tries reached for client http pool ");
                return false;
            }
            if (exception instanceof org.apache.http.NoHttpResponseException) {
                LOGGER.warn("No response from server on " + executionCount + " call");
                return true;
            }
            return false;
        }).build();
    }

}
