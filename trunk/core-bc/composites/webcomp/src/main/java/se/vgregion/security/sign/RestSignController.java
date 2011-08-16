package se.vgregion.security.sign;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.security.SignatureException;
import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import se.vgregion.dao.domain.patterns.repository.Repository;
import se.vgregion.domain.security.pkiclient.ELegType;
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
@RequestMapping(headers="Content-Type=application/json")
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
    public RestSignController(SignatureService signatureService, Repository<ELegType, String> eLegTypes) {
        super(signatureService, eLegTypes);
    }

    /*
     * (non-Javadoc)
     * 
     * @see AbstractSignController#getClientTypes()
     */
    @Override
    // @RequestMapping(value = "/prepare", headers = "Accept=application/json", params = { "tbs" })
    public @ResponseBody
    Collection<ELegType> getClientTypes() {
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
    //    @RequestMapping(value = "/verify", method = POST, params = { "encodedTbs", "clientType", "signature"}, headers="Accept=application/json")
    public boolean verifySignature(@RequestBody SignatureData signData) throws SignatureException {
        return super.verifySignature(signData);
    }

    @RequestMapping(value = "/validate", method = POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void verify(@RequestBody SignatureData signatureData, HttpServletResponse resp) {
        //        resp.setStatus(HttpStatus.NO_CONTENT);
        System.out.println(signatureData);
    }
}
