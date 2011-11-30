package se.vgregion.ticket;

/**
 * @author Patrik Bergström
 */
public class TicketException extends Exception {

    public TicketException() {
    }

    public TicketException(String message) {
        super(message);
    }

    public TicketException(String message, Throwable cause) {
        super(message, cause);
    }

    public TicketException(Throwable cause) {
        super(cause);
    }
}
