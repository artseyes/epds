package gov.gao.epds.security;

public class PreAuthenticationException extends RuntimeException {

	private static final long serialVersionUID = 1495226812060851555L;

	public PreAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

}