/**
 * 
 */
package se.vgregion.web.dto;

import java.util.Date;

import se.vgregion.ticket.Ticket;

/**
 * @author Anders Asplund
 *
 */
public class TicketDto {
    private Date due;
    private String ticketNumber;

    private TicketDto(String ticketNumber, Date due) {
        this.due = new Date(due.getTime());
        this.ticketNumber = ticketNumber;
    }
    public Date getDue() {
        return due;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public static TicketDto createDto(Ticket ticket) {
        return new TicketDto(ticket.getTicketNumber(), new Date(ticket.getDue()));
    }

}
