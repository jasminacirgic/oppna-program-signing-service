package se.vgregion.ticket;

import java.util.TimerTask;

/**
 * @author Anders Asplund
 * 
 * Timer task which could be scheduled to clean up old tickets.
 * 
 */
public class TicketCleanup extends TimerTask {
    @Override
    public void run() {
        TicketManager.INSTANCE.removeOldTickets();
    }

}
