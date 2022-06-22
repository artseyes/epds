package gov.gao.epds.exception;

public class InvalidInputException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8305309537326297229L;

	public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String message, Throwable throwable) {
        super(message, throwable);
    }

}