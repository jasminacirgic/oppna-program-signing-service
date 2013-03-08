package se.vgregion.security.sign;

import com.logica.mbi.service.v1_0.CollectResponseType;
import com.logica.mbi.service.v1_0.ProgressStatusType;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import se.vgregion.dao.domain.patterns.repository.Repository;
import se.vgregion.domain.security.pkiclient.ELegType;
import se.vgregion.ticket.Ticket;
import se.vgregion.ticket.TicketException;
import se.vgregion.ticket.TicketManager;
import se.vgregion.web.dto.TicketDto;
import se.vgregion.web.security.services.SignatureData;
import se.vgregion.web.security.services.SignatureService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.SignatureException;
import java.util.*;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Web implementation of {@link AbstractSignController}. This implementation is used for standard web access to the
 * signer service. To access the signer service as RESTfull WebService use {@link RestSignController}.
 *
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 * @see RestSignController
 */
@Controller
public class WebSignController extends AbstractSignController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSignController.class);

    private Set<String> internalNetworks;

    /**
     * Constructs an instance of WebSignController.
     *
     * @param signatureService a signatureService
     * @param eLegTypes        a repository of e-legitimations
     * @param ticketManager    the {@link TicketManager} to use
     */
    @Autowired
    public WebSignController(SignatureService signatureService, Repository<ELegType, String> eLegTypes,
                             TicketManager ticketManager) {
        super(signatureService, eLegTypes, ticketManager);
    }

    @Autowired
    @Required
    public void setInternalNetworks(String internalNetworks) {
        if (internalNetworks.length() >= 3) { //minimum length of a network
            String[] internalIpsArray = internalNetworks.replaceAll(" ", "").split(",");
            this.internalNetworks = new HashSet<String>(Arrays.asList(internalIpsArray));
        } else {
            this.internalNetworks = new HashSet<String>();
        }
    }


    /**
     * Setup an {@link java.beans.PropertyEditor.PropertyEditor} to handle conversion of a {@link String}
     * representing an {@link ELegType} to ELegType.
     *
     * @param binder WebDataBinder
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
     * @param signData signature data
     * @param model    the model
     * @param req      the request
     * @return name of the view which displays a list of pki clients
     * @throws TicketException if the {@link Ticket} validation fails
     */
    @RequestMapping(value = "/prepare", method = POST, params = {"tbs", "submitUri"})
    public String prepareSignNoClientType(@ModelAttribute SignatureData signData, Model model,
                                          HttpServletRequest req) throws TicketException {
        LOGGER.info("Incoming sign request from {}", req.getRemoteHost());
        String ticket = signData.getTicket();

        assertPermission(req, ticket);

        model.addAttribute("ticket", signData.getTicket());
        model.addAttribute("signData", signData);
        return "clientTypeSelection";
    }

    private void validateInternalAccess(HttpServletRequest req) throws TicketException {
        String header = req.getHeader("x-forwarded-for");

        LOGGER.debug("No ticket provided. Validate request with x-forwarded-for header [" + header + "].");

        if (header == null) {
            throwTicketException(header);
        }

        boolean internal = false;
        for (String network : internalNetworks) {
            if (header.startsWith(network)) {
                internal = true;
            }
        }

        if (!internal) {
            throwTicketException(header);
        }
    }

    private void throwTicketException(String header) throws TicketException {
        LOGGER.warn("The x-forward-for header was " + header + " which is not allowed for access to the Signing Service"
                + " without a ticket.");
        throw new TicketException("A ticket is needed in order to proceed.");
    }

    private void validateTicket(Ticket ticket) throws TicketException {
        if (ticket == null) {
            throw new TicketException("No ticket was attached with the request.");
        }
        boolean valid = getTicketManager().verifyTicket(ticket);
        if (!valid) {
            throw new TicketException("Ticket is invalid. It is either too old or corrupt.");
        }
    }

    /**
     * Preparation for signing, ie. encode <code>tbs</code> - To Be Signed and generate <code>nonce</code>. The
     * returned string should be the name of a pki client and should mapped to a view with the same name.
     *
     * @param signData data used during signing
     * @param model    the model
     * @param req      the HttpServletRequest
     * @return the name of the pki client
     * @throws SignatureException if preparation fails
     */
    @RequestMapping(value = "/prepare", method = POST, params = {"tbs", "submitUri", "clientType"})
    public String prepareSign(@ModelAttribute SignatureData signData, Model model, HttpServletRequest req)
            throws SignatureException, TicketException {
        String ticket = signData.getTicket();
        assertPermission(req, ticket);
        model.addAttribute("postbackUrl", getPkiPostBackUrl(req));
        model.addAttribute("signData", signData);
        model.addAttribute("ticket", ticket);
        return super.prepareSign(signData);
    }

    /**
     * Verifies and submits the signature to submitUri.
     *
     * @param signData data used during verification
     * @return name of view to show to the client
     * @throws SignatureException if validation or submission fails
     */
    @RequestMapping(value = "/verify", method = POST, params = {"encodedTbs", "submitUri", "clientType",
            "signature"})
    public String verifyAndSaveSignature(@ModelAttribute SignatureData signData) throws SignatureException {
        super.verifySignature(signData);
        String redirectLocation = getSignatureService().save(signData);
        if (!StringUtils.isBlank(redirectLocation)) {
            LOGGER.debug(String.format("WebSignController.verifyAndSaveSignature(%s)\n", "redirect:"
                    + redirectLocation));
            return "redirect:" + redirectLocation;
        }
        return "verified";
    }

    @RequestMapping(value = "/signMobileBankId", method = POST, params = {"encodedTbs", "submitUri", "personalNumber"})
    public String signMobileBankId(@ModelAttribute SignatureData signData, HttpServletRequest request, Model model)
            throws SignatureException, TicketException {

        String ticket = signData.getTicket();

        assertPermission(request, ticket);

        String orderRef = getSignatureService().sendMobileSignRequest(signData);

        model.addAttribute("orderRef", orderRef);
        String userAgent = request.getHeader("User-Agent");

        boolean isMobileDevice;
        if (userAgent != null && userAgent.toLowerCase().matches("(.*ipad.*)|(.*iphone.*)|(.*android.*)|(.*mobile.*)")) {
            isMobileDevice = true;
        } else {
            isMobileDevice = false;
        }

        model.addAttribute("isMobileDevice", isMobileDevice);
        model.addAttribute("data", objectToString(signData));

        return "awaitResponse";
    }

    @RequestMapping(value = "/awaitResponse", method = POST, params = {"orderRef"})
    public void checkMobileBankIdResponse(@RequestParam("orderRef") String orderRef, @RequestParam("data") String data,
                                          Model model, HttpServletResponse response)
            throws SignatureException, InterruptedException, IOException {

        response.setContentType("application/json");

        SignatureData signData = (SignatureData) stringToObject(data);

        CollectResponseType collectResponse;

        try {
            collectResponse = getSignatureService().collectRequest(orderRef);

            if (collectResponse.getProgressStatus().equals(ProgressStatusType.COMPLETE)) {
                signData.setSignature(collectResponse.getSignature());
                String redirectLocation = getSignatureService().save(signData);
                String status = ProgressStatusType.COMPLETE.value();
                if (!StringUtils.isBlank(redirectLocation)) {
                    LOGGER.debug(String.format("WebSignController.verifyAndSaveSignature(%s)\n", "redirect:"
                            + redirectLocation));
                    writeToOutput(response, "{\"status\":\"" + status + "\", \"redirect\":\"" + redirectLocation + "\"}");
                    return;
                } else {
                    writeToOutput(response, "{\"status\":\"" + status + "\", \"message\":\"Signeringen är nu slutförd.\"}");
                    return;
                }
            }

            String message;
            String status = collectResponse.getProgressStatus().value();
            message = "{\"status\":\"" + status + "\"}";

            writeToOutput(response, message);
        } catch (SignatureException e) {
            if (e.getCause() != null) {
                writeToOutput(response, "\"status\":\"FAILURE\",\"message\":\"" + e.getCause().getMessage() + "\"}");
            } else {
                writeToOutput(response, "\"status\":\"FAILURE\",\"message\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    /**
     * Cancel the signing and informs the consumer.
     *
     * @param signData data used during verification
     * @param response the {@link HttpServletResponse}
     * @return name of view to show to the client
     * @throws SignatureException if validation or submission fails
     */
    @RequestMapping(value = "/cancel", method = POST)
    public String cancelSignature(@ModelAttribute SignatureData signData, HttpServletResponse response)
            throws SignatureException {
        String redirectLocation = getSignatureService().abort(signData);
        if (!StringUtils.isBlank(redirectLocation)) {
            LOGGER.debug(String.format("WebSignController.cancelSignature(%s)\n", redirectLocation));
            return "redirect:" + redirectLocation;
        }
        return "errorForm";
    }

    /**
     * Handles all exceptions so that no stacktraces is displayed on a web page. Logs the complete stacktrace in
     * the configured log.
     *
     * @param ex      the exception
     * @param request the httpServletRequest
     * @return a {@link ModelAndView} with an error message and the view to display
     */
    @ExceptionHandler({SignatureException.class, TicketException.class})
    public ModelAndView handleException(Exception ex, HttpServletRequest request) {
        ex.printStackTrace();
        LOGGER.error("Generic Error Handling", ex);
        ModelMap model = new ModelMap();
        model.addAttribute("class", ClassUtils.getShortName(ex.getClass()));
        model.addAttribute("message", ex.getMessage());
        return new ModelAndView("errorHandling", model);
    }

    String objectToString(SignatureData signData) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(signData);
            oos.close();
            baos.close();
            return new String(Base64.encodeBase64(baos.toByteArray()), "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    Object stringToObject(String string) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decodeBase64(string.getBytes("UTF-8")));
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object object = ois.readObject();
            ois.close();
            bais.close();
            return object;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private String getPkiPostBackUrl(HttpServletRequest req) {
        StringBuilder pkiPostUrl = new StringBuilder();
        String verifyUrl = "http" + (req.isSecure() ? "s" : "") + "://" + req.getServerName() + ":"
                + req.getServerPort() + req.getContextPath() + "/sign";
        pkiPostUrl.append(verifyUrl);

        return pkiPostUrl.toString();
    }

    private void assertPermission(HttpServletRequest req, String ticket) throws TicketException {
        if (ticket != null && ticket.length() > 0) {
            TicketDto ticketDto = new TicketDto(ticket);
            LOGGER.debug("Ticket used: " + ticketDto.toString());
            validateTicket(ticketDto.toTicket());
        } else {
            validateInternalAccess(req);
        }
    }

    private void writeToOutput(HttpServletResponse response, String value) {
        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            outputStream.write(value.getBytes("UTF-8"));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }
}
