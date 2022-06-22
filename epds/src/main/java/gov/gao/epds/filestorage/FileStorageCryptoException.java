package gov.gao.epds.filestorage;
/**
 * @author MHussaini
 *
 */
public class FileStorageCryptoException extends Exception {
 
    /**
	 * 
	 */
	private static final long serialVersionUID = -5624014659008414245L;

	public FileStorageCryptoException() {
    }
 
    public FileStorageCryptoException(String message, Throwable throwable) {
        super(message, throwable);
    }
}