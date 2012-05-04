package se.vgregion.ticket;

/**
 * Exception class which can be thrown when something associated with {@link Ticket}s fails.
 *
 * @author Patrik Bergström
 */
public class TicketException extends Exception {

    /**
     * Default constructor.
     */
    public TicketException() {
    }

    /**
     * Constructor.
     *
     * @param message message
     */
    public TicketException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param message message
     * @param cause cause
     */
    public TicketException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     *
     * @param cause cause
     */
    public TicketException(Throwable cause) {
        super(cause);
    }
}
