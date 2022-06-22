package gov.gao.epds.service.recaptcha;

/**
 * @author MHussaini
 *
 */
public class RecaptchaServiceException extends RuntimeException {

	private static final long serialVersionUID = 1495226812060851555L;

	public RecaptchaServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
