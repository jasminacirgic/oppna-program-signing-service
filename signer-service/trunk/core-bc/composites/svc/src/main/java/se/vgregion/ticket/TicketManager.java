package se.vgregion.ticket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton package private ticket manager, responsible to manage all solved tickets in a store. It keeps the
 * tickets in a concurrent map since both {@link Ticket} and {@link TicketCleanup} writes and reads to the store.
 * The class is an internal for the Ticket process thus its declared package private.
 * 
 * @author Anders Asplund
 * 
 * 
 */
enum TicketManager {
    INSTANCE; // Singleton
    private static final Logger LOG = LoggerFactory.getLogger(TicketManager.class);
    private static Map<String, Long> solvedTickets = new ConcurrentHashMap<String, Long>();

    void addTicket(Ticket ticket) {
        solvedTickets.put(ticket.getTicketNumber(), ticket.getDue());
        LOG.info("Added ticket to store: {}", ticket);
    }

    Long getDue(String ticketNumber) {
        return solvedTickets.get(ticketNumber);
    }

    void removeOldTickets() {
        LOG.info("Number of valid tickets in store: {}", solvedTickets.size());
        final Long now = System.currentTimeMillis();
        for (Map.Entry<String, Long> ticketEntry : solvedTickets.entrySet()) {
            if (ticketEntry.getValue() < now) {
                LOG.info("Removing ticket: {}", ticketEntry);
                solvedTickets.remove(ticketEntry.getKey());
            }
        }
        LOG.info("Number of valid tickets in store: {}", solvedTickets.size());
    }
}
