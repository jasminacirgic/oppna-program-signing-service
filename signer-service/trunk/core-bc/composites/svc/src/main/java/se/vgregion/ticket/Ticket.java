package se.vgregion.ticket;

import java.util.UUID;

import org.apache.commons.lang.builder.ToStringBuilder;


public final class Ticket {
    private static final Long MILLIS_IN_A_MINUTE = 1000L * 60;
    private static final Long KEEP_ALIVE = 5 * MILLIS_IN_A_MINUTE; // 5 minutes;

    private Long due;
    private String ticketNumber;

    private Ticket(Long due, String value) {
        this.due = due;
        this.ticketNumber = value;
    }

    public static Ticket solveTicket() {
        final Ticket ticket = new Ticket(System.currentTimeMillis() + KEEP_ALIVE, UUID.randomUUID().toString());
        return ticket;
    }

    public static boolean isValid(String ticketNumber) {
        final Long due = TicketManager.INSTANCE.getDue(ticketNumber);
        final long now = System.currentTimeMillis();
        return (!isNull(due) && due >= now);
    }

    public boolean isValid() {
        return Ticket.isValid(this.ticketNumber);
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public long getDue() {
        return due;
    }

    private static boolean isNull(Object o) {
        return o == null;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
