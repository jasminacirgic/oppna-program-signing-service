package se.vgregion.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown to indicate that a method has been passed an illegal or inappropriate argument. If thrown from a servlet
 * or web service the response status code is set to BAD REQUEST(400).
 * 
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 * 
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Ogiltigt argument")
public class IllegalWebArgumentException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new {@link IllegalWebArgumentException}.
     */
    public IllegalWebArgumentException() {
        super();
    }

    /**
     * Constructs a new {@link IllegalWebArgumentException}.
     * 
     * @param message
     *            the detail message. The detail message is saved for later retrieval by the
     *            {@link Throwable#getMessage()} method.
     */
    public IllegalWebArgumentException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@link IllegalWebArgumentException}.
     * 
     * @param cause
     *            the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method). (A
     *            null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public IllegalWebArgumentException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new {@link IllegalWebArgumentException}.
     * 
     * @param message
     *            the detail message. The detail message is saved for later retrieval by the
     *            {@link Throwable#getMessage()} method.
     * @param cause
     *            the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method). (A
     *            null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public IllegalWebArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}