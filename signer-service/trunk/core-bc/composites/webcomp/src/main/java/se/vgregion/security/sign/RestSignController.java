package se.vgregion.security.sign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import se.vgregion.dao.domain.patterns.repository.Repository;
import se.vgregion.domain.security.pkiclient.ELegType;
import se.vgregion.ticket.Ticket;
import se.vgregion.ticket.TicketManager;
import se.vgregion.ticket.TicketException;
import se.vgregion.web.dto.TicketDto;
import se.vgregion.web.security.services.SignatureData;
import se.vgregion.web.security.services.SignatureService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.security.SignatureException;
import java.util.Collection;

/**
 * RESTfull web service implementation of {@link AbstractSignController}.
 * 
 * OBS!! Not fully implemented yet!
 * 
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 * 
 */
@Path("/")
@Produces("text/plain")
public class RestSignController extends AbstractSignController {

    /**
     * Constructs an instance of RestSignController.
     * 
     * @param signatureService
     *            a signatureService
     * @param eLegTypes
     *            a repository of e-legitimations
     */
    @Autowired
    public RestSignController(SignatureService signatureService, Repository<ELegType, String> eLegTypes,
                              TicketManager ticketManager) {
        super(signatureService, eLegTypes, ticketManager);
    }

    /*
     * (non-Javadoc)
     * 
     * @see AbstractSignController#getClientTypes()
     */
    @Override
    @ResponseBody
    public Collection<ELegType> getClientTypes() {
        return super.getClientTypes();
    }

    /*
     * (non-Javadoc)
     * 
     * @see AbstractSignController#prepareSign(SignatureData)
     */
    @Override
    public String prepareSign(@RequestBody SignatureData signData) throws SignatureException {
        return super.prepareSign(signData);
    }

    /*
     * (non-Javadoc)
     * 
     * @see AbstractSignController#verifySignature(SignatureData)
     */
    @Override
    public boolean verifySignature(@RequestBody SignatureData signData) throws SignatureException {
        return super.verifySignature(signData);
    }

    @GET
    @Path("/solveTicket/{serviceId}")
    public String solveTicket(@PathParam("serviceId") String serviceId) {
        Ticket ticket;
        try {
            ticket = getTicketManager().solveTicket(serviceId);
        } catch (TicketException e) {
            throw new WebApplicationException(e, Response.Status.FORBIDDEN);
        }
        return new TicketDto(ticket).toString();
    }


}
