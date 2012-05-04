package se.vgregion.web.signaturestorage;

/**
 * Should be thrown when an attempt to store a signature fails.
 * 
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 */
public class SignatureStoreageException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new {@link SignatureStoreageException}.
     */
    public SignatureStoreageException() {
        super();
    }

    /**
     * Constructs a new {@link SignatureStoreageException}.
     * 
     * @param message
     *            the detail message. The detail message is saved for later retrieval by the
     *            {@link Throwable#getMessage()} method.
     */
    public SignatureStoreageException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@link SignatureStoreageException}.
     * 
     * @param cause
     *            the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method). (A
     *            null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public SignatureStoreageException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new {@link SignatureStoreageException}.
     * 
     * @param message
     *            the detail message. The detail message is saved for later retrieval by the
     *            {@link Throwable#getMessage()} method.
     * @param cause
     *            the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method). (A
     *            null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public SignatureStoreageException(String message, Throwable cause) {
        super(message, cause);
    }
}
