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
import se.vgregion.signera.signature._1.SignatureVerificationRequest;
import se.vgregion.ticket.Ticket;
import se.vgregion.ticket.TicketException;
import se.vgregion.ticket.TicketManager;
import se.vgregion.web.dto.TicketDto;
import se.vgregion.web.security.services.SignatureData;
import se.vgregion.web.security.services.SignatureService;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    @Produces("text/plain")
    public String verifySignature(SignatureVerificationRequest signatureVerificationRequest) throws CMSException, IOException, ParserConfigurationException, SAXException, MarshalException, XMLSignatureException {

        SignatureFormat format = signatureVerificationRequest.getSignatureFormat();
        if (SignatureFormat.XMLDIGSIG.equals(format)) {
            String signature = signatureVerificationRequest.getSignature();

            InputStream is = new ByteArrayInputStream(signature.getBytes());

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(is);

            XMLSignature xmlSignature = XMLSignatureFactory.getInstance().unmarshalXMLSignature(new DOMStructure(document));
            xmlSignature.getKeyInfo().getContent();

            SignatureData signatureData = new SignatureData();
            signatureData.setClientType(new ELegType("test", "test", PkiClient.NEXUS_PERSONAL_4X));
            signatureData.setSignature(new String(Base64.encode(signature.getBytes())));
            signatureData.setNonce(getNonce(signature));
            signatureData.setTbs("TODO"); //TODO

            xmlSignature.getObjects().get(0);

            boolean verified = false;
            try {
                verified = super.verifySignature(signatureData);
            } catch (SignatureException e) {
                e.printStackTrace();
            }
            System.out.println(verified);
        } else if (SignatureFormat.CMS.equals(format)) {
            String signature = signatureVerificationRequest.getSignature();
            byte[] decoded = Base64.decode(signature);

            CMSSignedData cmsSignedData = new CMSSignedData(decoded);
            String signedData = new String((byte[]) cmsSignedData.getSignedContent().getContent());

            SignatureData signData = new SignatureData();
            signData.setEncodedTbs(signedData);
            signData.setSignature(signature);
            ELegType clientType = new ELegType("test", "test", PkiClient.NETMAKER_NETID_4);
            signData.setClientType(clientType);
            boolean verified = false;
            try {
                verified = super.verifySignature(signData);
            } catch (SignatureException e) {
                e.printStackTrace();
            }
            System.out.println(verified);

        }
        return "hej";
    }

    private String getNonce(String signature) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(false);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(new ByteArrayInputStream(signature.getBytes()));

        XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            XPathExpression expression = xpath.compile("/Signature/Object/bankIdSignedData/srvInfo/nonce/text()");
            String nonce = (String) expression.evaluate(document, XPathConstants.STRING);
            String decoded = new String(Base64.decode(nonce));
            return decoded;
        } catch (XPathExpressionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return "";
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
