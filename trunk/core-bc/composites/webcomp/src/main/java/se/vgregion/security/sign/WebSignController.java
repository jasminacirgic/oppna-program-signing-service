package se.vgregion.security.sign;

import java.security.SignatureException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import se.vgregion.dao.domain.patterns.repository.Repository;
import se.vgregion.domain.security.pkiclient.ELegType;
import se.vgregion.web.security.services.SignatureData;
import se.vgregion.web.security.services.SignatureService;

/**
 * Web implementation of {@link AbstractSignController}. This implementation is used for standard web access to the
 * signer service. To access the signer service as RESTfull WebService use {@link RestSignController}.
 * 
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 * 
 * @see RestSignController
 */
@Controller
public class WebSignController extends AbstractSignController {

    /**
     * Constructs an instance of WebSignController.
     * 
     * @param signatureService
     *            a signatureService
     * @param eLegTypes
     *            a repository of e-legitimations
     */
    @Autowired
    public WebSignController(SignatureService signatureService, Repository<ELegType, String> eLegTypes) {
        super(signatureService, eLegTypes);
    }

    /**
     * Setup an {@link java.beans.PropertyEditor.PropertyEditor} to handle conversion of a {@link String}
     * representing an {@link ELegType} to ELegType.
     * 
     * @param binder
     *            WebDataBinder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(ELegType.class, new ELegTypeEditor(geteLegTypes()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.security.sign.AbstractSignController#getClientTypes()
     */
    @Override
    @ModelAttribute("clientTypes")
    public Collection<ELegType> getClientTypes() {
        return super.getClientTypes();
    }

    /**
     * If a pki client type is missing in the request provide a list to select from to the client.
     * 
     * @param model
     *            the model
     * @param signData
     *            signature data
     * @return name of the view which displays a list of pki clients
     */
    @RequestMapping(value = "/prepare", method = RequestMethod.POST, params = { "tbs", "submitUri" })
    public String prepareSignNoClientType(@ModelAttribute SignatureData signData, Model model) {
        model.addAttribute("signData", signData);
        return "clientTypeSelection";
    }

    /**
     * Preparation for signing, ie. encode <code>tbs</code> - To Be Signed and generate <code>nonce</code>. The
     * returned string should be the name of a pki client and should mapped to a view with the same name.
     * 
     * @param signData
     *            data used during signing
     * @param model
     *            the model
     * @param req
     *            the HttpServletRequest
     * @return the name of the pki client
     * 
     * @throws SignatureException
     *             if preparation fails
     */
    @RequestMapping(value = "/prepare", method = RequestMethod.POST, params = { "tbs", "submitUri", "clientType" })
    public String prepareSign(@ModelAttribute SignatureData signData, Model model, HttpServletRequest req)
            throws SignatureException {
        model.addAttribute("postbackUrl", getPkiPostBackUrl(req));
        model.addAttribute("signData", signData);
        return super.prepareSign(signData);
    }

    /**
     * Verifies and submits the signature to submitUri.
     * 
     * @param signData
     *            data used during verification
     * @return name of view to show to the client
     * @throws SignatureException
     *             if validation or submission fails
     */
    @RequestMapping(value = "/verify", method = RequestMethod.POST, params = { "tbs", "submitUri", "clientType",
            "signature" })
    public String verifyAndSaveSignature(@ModelAttribute SignatureData signData) throws SignatureException {
        super.verifySignature(signData);
        String redirectLocation = getSignatureService().save(signData);
        if (!StringUtils.isBlank(redirectLocation)) {
            return "redirect:" + redirectLocation;
        }
        return "verified";
    }

    private String getPkiPostBackUrl(HttpServletRequest req) {
        StringBuilder pkiPostUrl = new StringBuilder();
        String verifyUrl = "http" + (req.isSecure() ? "s" : "") + "://" + req.getServerName() + ":"
                + req.getServerPort() + req.getContextPath() + "/sign/verify";
        pkiPostUrl.append(verifyUrl);

        return pkiPostUrl.toString();
    }
}
