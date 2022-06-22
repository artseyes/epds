package gov.gao.epds.service.recaptcha;

/**
 * @author MHussaini
 *
 */
public interface RecaptchaService {

    boolean isResponseValid(String remoteIp, String response);

}
