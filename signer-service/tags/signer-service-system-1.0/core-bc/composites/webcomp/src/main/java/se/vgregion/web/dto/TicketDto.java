/**
 *
 */
package se.vgregion.web.dto;

import org.apache.commons.codec.binary.Base64;
import se.vgregion.ticket.Ticket;
import se.vgregion.ticket.TicketException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A data transfer object class for converting between {@link String} and {@link Ticket}.
 *
 * @author Anders Asplund
 * @author Patrik Bergström
 */
public class TicketDto {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private static final String DELIMITER = "_";
    private String ticket;

    /**
     * Constructor.
     *
     * @param ticket a {@link Ticket}
     */
    public TicketDto(Ticket ticket) {
        String date = SDF.format(new Date(ticket.getDue()));
        String signature = ticket.getSignatureAsBase64();
        this.ticket = date + DELIMITER + signature;
    }

    /**
     * Constructor.
     *
     * @param ticket a {@link String} which can be converted into a {@link Ticket}
     */
    public TicketDto(String ticket) {
        this.ticket = ticket;
    }

    /**
     * Returns the instance as a {@link Ticket} object.
     *
     * @return the {@link Ticket}
     * @throws TicketException if the instance cannot be made into a {@link Ticket} object
     */
    public Ticket toTicket() throws TicketException {
        String datePart = null;
        try {
            int i = ticket.indexOf(DELIMITER);
            datePart = ticket.substring(0, i);
            String signaturePart = ticket.substring(i + 1, ticket.length());

            Date date = SDF.parse(datePart);

            return new Ticket(date.getTime(), Base64.decodeBase64(signaturePart));
        } catch (ParseException e) {
            throw new TicketException("The string " + datePart + " is not regarded as a valid date.");
        }
    }

    @Override
    public String toString() {
        int i = ticket.indexOf(DELIMITER);
        String date = ticket.substring(0, i);
        String signature = ticket.substring(i + 1, ticket.length());
        
        return date + DELIMITER + signature;
    }

}
