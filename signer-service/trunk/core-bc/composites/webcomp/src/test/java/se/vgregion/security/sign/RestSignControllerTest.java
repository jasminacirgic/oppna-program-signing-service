package se.vgregion.security.sign;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.ServerWebApplicationException;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.vgregion.dao.domain.patterns.repository.Repository;
import se.vgregion.ticket.TicketManager;
import se.vgregion.web.security.services.SignatureService;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

/**
 * @author Patrik Bergström
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:application-context-test.xml"})
public class RestSignControllerTest {

    @Autowired
    private ApplicationContext ctx;
    @Autowired
    @Qualifier("client")
    private IRestSignController restSignControllerProxy;

    private RestSignController controller;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        controller = new RestSignController(Mockito.mock(SignatureService.class), mock(Repository.class),
                TicketManager.getInstance());

        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
        sf.setResourceClasses(RestSignController.class);
        sf.setResourceProvider(RestSignController.class, new SingletonResourceProvider(controller));
        sf.setAddress("http://localhost:9000/service");
        sf.create();
    }

    @Test
    @ExpectedException(ServerWebApplicationException.class)
    public void testSolveTicketWithWrongServiceId() throws Exception {

        try {
            String response = restSignControllerProxy.solveTicket("asdf");
            fail();
        } catch (ServerWebApplicationException ex) {
            assertEquals(Response.Status.FORBIDDEN.getStatusCode(), ex.getStatus());
            ex.printStackTrace();
            throw ex;
        }
    }

}
