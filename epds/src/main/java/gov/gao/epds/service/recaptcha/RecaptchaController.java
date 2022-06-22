package gov.gao.epds.service.recaptcha;

import gov.gao.epds.dto.Recaptcha;
import gov.gao.epds.utils.Util;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RecaptchaController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RecaptchaController.class);

	private final HttpServletRequest httpServletRequest;
    private final RecaptchaService recaptchaService;

    @Autowired
    public RecaptchaController(HttpServletRequest httpServletRequest, RecaptchaService recaptchaService) {
        this.httpServletRequest = httpServletRequest;
        this.recaptchaService = recaptchaService;
    }
		
    @RequestMapping(value = "/user/verify-captcha-response", method = RequestMethod.POST)
	public void verifyCaptchaResponse(ModelMap model,Recaptcha recaptcha) {
		 
    	System.out.println("recaptcha -------->" + recaptcha.toString());
    	
		try {
            if (recaptcha.getRecaptchaResponse() != null
                    && !recaptcha.getRecaptchaResponse().isEmpty()
                    && recaptchaService.isResponseValid(Util.getRemoteIp(httpServletRequest), recaptcha.getRecaptchaResponse())) {
            	model.addAttribute("isResponseValid", true);
            }else{
            	model.addAttribute("isResponseValid", false);
            }
        } catch (RecaptchaServiceException e) {
        	
        	e.printStackTrace();
        	LOGGER.error("Exception occurred when validating captcha response", e);
            model.addAttribute("data", "Exception occurred when validating captcha response");
        }
	}
	
}
