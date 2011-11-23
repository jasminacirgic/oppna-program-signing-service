/**
 * 
 */
package se.vgregion.web.dto;

import se.vgregion.ticket.Ticket;

/**
 * @author Anders Asplund
 *
 */
public class TicketDto {
    private String ticketAsString;

    private TicketDto(Ticket ticket) {
        this.ticketAsString = ticket.getDue() + ticket.getSignatureAsBase64();
    }

    public static TicketDto createDto(Ticket ticket) {
        return new TicketDto(ticket);
    }

}
