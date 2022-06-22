package gov.gao.epds.exception;

public class InvalidFileException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8305309537326297229L;

	public InvalidFileException(String message) {
        super(message);
    }

    public InvalidFileException(String message, Throwable throwable) {
        super(message, throwable);
    }

}