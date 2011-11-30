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
 * @author Anders Asplund
 */
public class TicketDto {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private static final String delimiter = "_";
    private String ticket;

    public TicketDto(Ticket ticket) {
        String date = sdf.format(new Date(ticket.getDue()));
        String signature = ticket.getSignatureAsBase64();
        this.ticket = date + delimiter + signature;
    }

    public TicketDto(String ticket) {
        this.ticket = ticket;
    }

    public Ticket toTicket() throws TicketException {
        String datePart = null;
        try {
            int i = ticket.indexOf(delimiter);
            datePart = ticket.substring(0, i);
            String signaturePart = ticket.substring(i + 1, ticket.length());

            Date date = sdf.parse(datePart);

            return new Ticket(date.getTime(), Base64.decodeBase64(signaturePart));
        } catch (ParseException e) {
            throw new TicketException("The string " + datePart + " is not regarded as a valid date.");
        }
    }

    @Override
    public String toString() {
        int i = ticket.indexOf(delimiter);
        String date = ticket.substring(0, i);
        String signature = ticket.substring(i + 1, ticket.length());
        
        return date + delimiter + signature;
    }

}
