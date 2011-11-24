/**
 * 
 */
package se.vgregion.web.dto;

import se.vgregion.ticket.Ticket;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Anders Asplund
 *
 */
public class TicketDto {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private String date;
    private String signature;

    private TicketDto(Ticket ticket) {
        this.date = sdf.format(new Date(ticket.getDue()));
        this.signature = ticket.getSignatureAsBase64();
    }

    public static TicketDto createDto(Ticket ticket) {
        return new TicketDto(ticket);
    }

    @Override
    public String toString() {
        return date + "_" + signature;
    }

}
