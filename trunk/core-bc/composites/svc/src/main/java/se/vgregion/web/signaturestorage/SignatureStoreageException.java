package se.vgregion.web.signaturestorage;

public class SignatureStoreageException extends Exception {

    private static final long serialVersionUID = 1L;

    public SignatureStoreageException() {
        super();
    }

    public SignatureStoreageException(String message) {
        super(message);
    }

    public SignatureStoreageException(Throwable cause) {
        super(cause);
    }

    public SignatureStoreageException(String message, Throwable cause) {
        super(message, cause);
    }
}
