package crypto;

/**
 * This exception is thrown when there is any issue encrypting or decrypting data.
 * In the future, we can make error reporting more precise by fine tuning messages, and maybe create subclasses of this class.
 */
public class CryptoException extends Exception {

    public CryptoException() {
        super();
    }

    public CryptoException(String message) {
        super(message);
    }

    public CryptoException(String message, Throwable cause) {
        super(message, cause);
    }

    public CryptoException(Throwable cause) {
        super(cause);
    }
}
