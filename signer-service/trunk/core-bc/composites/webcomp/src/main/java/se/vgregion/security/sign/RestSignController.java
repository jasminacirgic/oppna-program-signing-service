package se.vgregion.security.sign;

import java.security.SignatureException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import se.vgregion.dao.domain.patterns.repository.Repository;
import se.vgregion.domain.security.pkiclient.ELegType;
import se.vgregion.ticket.Ticket;
import se.vgregion.ticket.TicketManager;
import se.vgregion.web.dto.TicketDto;
import se.vgregion.web.security.services.SignatureData;
import se.vgregion.web.security.services.SignatureService;

/**
 * RESTfull web service implementation of {@link AbstractSignController}.
 * 
 * OBS!! Not fully implemented yet!
 * 
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 * 
 */
@Controller
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

    @RequestMapping(value = "/solveTicket/{serviceId}")
    @ResponseBody
    public String solveTicket(@PathVariable String serviceId) {
        Ticket ticket = getTicketManager().solveTicket(serviceId);
        return TicketDto.createDto(ticket).toString();
    }


}
