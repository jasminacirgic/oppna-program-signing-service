package se.vgregion.security.sign;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import se.vgregion.dao.domain.patterns.repository.Repository;
import se.vgregion.domain.security.pkiclient.ELegType;
import se.vgregion.domain.security.pkiclient.PkiClient;
import se.vgregion.signera.signature._1.SignatureFormat;
import se.vgregion.signera.signature._1.SignatureStatus;
import se.vgregion.signera.signature._1.SignatureVerificationRequest;
import se.vgregion.signera.signature._1.SignatureVerificationResponse;
import se.vgregion.ticket.Ticket;
import se.vgregion.ticket.TicketException;
import se.vgregion.ticket.TicketManager;
import se.vgregion.web.dto.TicketDto;
import se.vgregion.web.security.services.SignatureData;
import se.vgregion.web.security.services.SignatureService;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.SignatureException;
import java.util.Collection;

/**
 * RESTful web service implementation of {@link AbstractSignController}.
 * <p/>
 * OBS!! Not fully implemented yet!
 *
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 */
@Path("/")
@Produces("text/plain")
public class RestSignController extends AbstractSignController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestSignController.class);

    /**
     * Constructs an instance of RestSignController.
     *
     * @param signatureService a signatureService
     * @param eLegTypes        a repository of e-legitimations
     * @param ticketManager    the {@link TicketManager} to use
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
//    @Override
    @POST
    @Path("/verifySignature")
    @Consumes("application/xml")
    @Produces("application/xml")
    public SignatureVerificationResponse verifySignature(SignatureVerificationRequest signatureVerificationRequest) throws CMSException, IOException, ParserConfigurationException, SAXException, MarshalException, XMLSignatureException {

        boolean verified = false;
        String message = null;

        SignatureFormat format = signatureVerificationRequest.getSignatureFormat();
        if (SignatureFormat.XMLDIGSIG.equals(format)) {
            String signature = new String(Base64.decode(signatureVerificationRequest.getSignature()));

//            InputStream is = new ByteArrayInputStream(signature.getBytes());

//            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
//            documentBuilderFactory.setNamespaceAware(true);
//            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
//            Document document = documentBuilder.parse(is);

//            XMLSignature xmlSignature = XMLSignatureFactory.getInstance().unmarshalXMLSignature(new DOMStructure(document));


            try {
                SignatureData signatureData = createSignatureDataFromXmlDigSig(signature);
                verified = super.verifySignature(signatureData);
            } catch (SignatureException e) {
                e.printStackTrace();
                message = e.getMessage();
            }
        } else if (SignatureFormat.CMS.equals(format)) {
            String signature = signatureVerificationRequest.getSignature();
            byte[] decoded = Base64.decode(signature);

            CMSSignedData cmsSignedData = new CMSSignedData(decoded);
            String signedData = new String((byte[]) cmsSignedData.getSignedContent().getContent());

            //Create the SignatureData to be verified
            SignatureData signData = new SignatureData();
            signData.setEncodedTbs(signedData);
            signData.setSignature(signature);
            ELegType clientType = new ELegType("test", "test", PkiClient.NETMAKER_NETID_4);
            signData.setClientType(clientType);

            try {
                //Verify
                verified = super.verifySignature(signData);
            } catch (SignatureException e) {
                e.printStackTrace();
                message = e.getMessage();
            }
        }

        SignatureVerificationResponse response = new SignatureVerificationResponse();
        response.setStatus(verified ? SignatureStatus.SUCCESS : SignatureStatus.FAILURE);
        if (message != null) {
            response.setMessage(message);
        }

        return response;
    }

    private SignatureData createSignatureDataFromXmlDigSig(String signature) throws SignatureException {

        //Start with setting some fields that are known directly
        SignatureData signatureData = new SignatureData();
        signatureData.setClientType(new ELegType("test", "test", PkiClient.NEXUS_PERSONAL_4X));
        signatureData.setSignature(new String(Base64.encode(signature.getBytes())));

        //For the rest we need to parse the XML
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(false);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new ByteArrayInputStream(signature.getBytes()));

            //nonce and tbs
            XPath xpath = XPathFactory.newInstance().newXPath();
            XPathExpression expression = xpath.compile("/Signature/Object/bankIdSignedData/srvInfo/nonce/text()");
            String encodedNonce = (String) expression.evaluate(document, XPathConstants.STRING);
            String decodedNonce = new String(Base64.decode(encodedNonce)); //this will be re-encoded again before it is sent
            signatureData.setNonce(decodedNonce);

            expression = xpath.compile("/Signature/Object/bankIdSignedData/usrVisibleData/text()");
            String encodedTbs = (String) expression.evaluate(document, XPathConstants.STRING);
            String decodedTbs = new String(Base64.decode(encodedTbs)); //this will be re-encoded again before it is sent

            signatureData.setEncodedTbs(encodedTbs);
            signatureData.setTbs(decodedTbs);

        } catch (XPathExpressionException e) {
            throw new SignatureException(e);
        } catch (ParserConfigurationException e) {
            throw new SignatureException(e);
        } catch (SAXException e) {
            throw new SignatureException(e);
        } catch (IOException e) {
            throw new SignatureException(e);
        }

        return signatureData;
    }

    /**
     * Method to request a new {@link Ticket}.
     *
     * @param serviceId the requester must have a serviceId in order to be authorized to receive a {@link Ticket}
     * @return the {@link Ticket} as a {@link String} in the HTTP response body
     */
    @GET
    @Path("/solveTicket/{serviceId}")
    public String solveTicket(@PathParam("serviceId") String serviceId) {
        LOGGER.info("Client with serviceId=" + serviceId + " requests a ticket.");
        Ticket ticket;
        try {
            ticket = getTicketManager().solveTicket(serviceId);
        } catch (TicketException e) {
            throw new WebApplicationException(e, Response.Status.FORBIDDEN);
        }
        return new TicketDto(ticket).toString();
    }


}
