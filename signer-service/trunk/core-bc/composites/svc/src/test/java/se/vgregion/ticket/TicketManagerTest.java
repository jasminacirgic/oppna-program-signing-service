package se.vgregion.ticket;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import se.vgregion.web.security.services.ServiceIdService;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author Patrik Bergström
 */
@RunWith(MockitoJUnitRunner.class)
public class TicketManagerTest {

    @Mock
    private ServiceIdService serviceIdService;

    //The signature is created at Ticket creation time.
    @InjectMocks
    private TicketManager ticketManager = TicketManager.getInstance();

    @Before
    public void setup() {
        //we assume the service-id always exists
        when(serviceIdService.containsServiceId(anyString())).thenReturn(true);
    }

    @Test
    public void testSignAndVerifySuccess() throws TicketException {
        Ticket ticket = ticketManager.solveTicket("asdf");

        TicketManager.TicketVerifyResponse verifyResponse = ticketManager.verifyTicket(ticket);
        assertTrue(verifyResponse.verifyOk());

        //create another one in between
        Ticket ticket2 = ticketManager.solveTicket("asdf33");

        //verify again to ensure nothing gets screwed up when repeated
        verifyResponse = ticketManager.verifyTicket(ticket);
        assertTrue(verifyResponse.verifyOk());

        //and verify the second ticket too
        verifyResponse = ticketManager.verifyTicket(ticket2);
        assertTrue(verifyResponse.verifyOk());
    }

    @Test
    public void testSignAndVerifyFail() throws TicketException {
        Ticket ticket = ticketManager.solveTicket("asdf");

        //Manipulating the due date
        Long due = (Long) ReflectionTestUtils.getField(ticket, "due");
        due++;
        ReflectionTestUtils.setField(ticket, "due", due);

        //The signature does not verify the changed due date
        TicketManager.TicketVerifyResponse verifyResponse = ticketManager.verifyTicket(ticket);

        assertFalse(verifyResponse.verifyOk());
    }
}
