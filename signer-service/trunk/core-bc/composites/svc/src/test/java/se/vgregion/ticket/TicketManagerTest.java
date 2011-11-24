package se.vgregion.ticket;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Patrik Bergström
 */
public class TicketManagerTest {

    @Test
    public void testSignAndVerifySuccess() {
        //The signature is created at Ticket creation time.
        TicketManager tm = TicketManager.getInstance();

        Ticket ticket = tm.solveTicket("asdf");

        boolean valid = tm.verifyTicket(ticket);

        assertTrue(valid);
    }
    
    @Test
    public void testSignAndVerifyFail() {
        TicketManager tm = TicketManager.getInstance();
        Ticket ticket = tm.solveTicket("asdf");

        //Manipulating the due date
        Long due = (Long) ReflectionTestUtils.getField(ticket, "due");
        due++;
        ReflectionTestUtils.setField(ticket, "due", due);

        //The signature does not verify the changed due date
        boolean valid = tm.verifyTicket(ticket);

        assertFalse(valid);
    }
}
