package se.vgregion.security.sign;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
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
import se.vgregion.signera.signature._1.*;
import se.vgregion.ticket.Ticket;
import se.vgregion.ticket.TicketException;
import se.vgregion.ticket.TicketManager;
import se.vgregion.web.dto.TicketDto;
import se.vgregion.web.security.services.SignatureData;
import se.vgregion.web.security.services.SignatureService;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * RESTful web service implementation of {@link AbstractSignController}.
 * <p/>
 * OBS!! Not fully implemented yet!
 *
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 * @author Patrik Bergström - <a href="http://www.knowit.se">Know IT</a>
 */
@Path("/")
@Produces("application/json")
public class RestSignController extends AbstractSignController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestSignController.class);
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

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
    public SignatureVerificationResponse verifySignature(SignatureVerificationRequest signatureVerificationRequest)
            throws CMSException, IOException, ParserConfigurationException, SAXException, MarshalException,
            XMLSignatureException {

        SignatureVerificationResponse response = new SignatureVerificationResponse();
        response.setCertificateInfos(new CertificateInfos());

        boolean verified = false;
        String message = null;

        SignatureFormat format = signatureVerificationRequest.getSignatureFormat();
        if (SignatureFormat.XMLDIGSIG.equals(format)) {
            String signature = new String(Base64.decode(signatureVerificationRequest.getSignature()));

            InputStream is = new ByteArrayInputStream(signature.getBytes());

            Document document = createDocument(is, true);

            XMLSignature xmlSignature = XMLSignatureFactory.getInstance().unmarshalXMLSignature(new DOMStructure(document));

            List contentList = xmlSignature.getKeyInfo().getContent();

            for (Object content : contentList) {
                try {
                    // We assume "content" is of type DOMX509Data but we don't use the class since it is in an internal
                    // package which is not allowed on all platforms (since they have different security settings).
                    // This is why we were forced to use reflection here.
                    Method getContentMethod = content.getClass().getMethod("getContent");
                    Object list = getContentMethod.invoke(content);
                    List certificateList = (List) list;
                    for (Object certificateObject : certificateList) {
                        if (certificateObject instanceof X509Certificate) {
                            X509Certificate cert = (X509Certificate) certificateObject;
                            CertificateInfo ci = new CertificateInfo();
                            ci.setSubjectDn(cert.getSubjectDN().getName());
                            ci.setValidTo(simpleDateFormat.format(cert.getNotAfter()));
                            response.getCertificateInfos().getCertificateInfo().add(ci);
                        }
                    }
                } catch (NoSuchMethodException e) {
                    LOGGER.warn("Unable to read certificate from signature");
                } catch (InvocationTargetException e) {
                    LOGGER.warn("Unable to read certificate from signature");
                } catch (IllegalAccessException e) {
                    LOGGER.warn("Unable to read certificate from signature");
                } 
            }

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
            String encodedSignedData = new String((byte[]) cmsSignedData.getSignedContent().getContent());

            //Fetch information about the issuers
            List<String> certInfos = new ArrayList<String>();
            Collection certificates = cmsSignedData.getCertificates().getMatches(null);
            for (Object certificate : certificates) {
                X509CertificateHolder holder = (X509CertificateHolder) certificate;
                certInfos.add(holder.getSubject().toString());
                CertificateInfo ci = new CertificateInfo();
                ci.setSubjectDn(holder.getSubject().toString());
                ci.setValidTo(simpleDateFormat.format(holder.getNotAfter()));
                response.getCertificateInfos().getCertificateInfo().add(ci);
            }

            //Fetch timestamp
            Date signingDate = findTimestamp(cmsSignedData);
            String dateString = simpleDateFormat.format(signingDate);
            response.setSignatureDate(dateString);

            //Create the SignatureData to be verified
            SignatureData signData = new SignatureData();
            signData.setEncodedTbs(encodedSignedData);
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

        response.setStatus(verified ? SignatureStatus.SUCCESS : SignatureStatus.FAILURE);
        if (message != null) {
            response.setMessage(message);
        }

        return response;
    }

    private Document createDocument(InputStream is, boolean namespaceAware)
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(namespaceAware);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        return documentBuilder.parse(is);
    }

    private Date findTimestamp(CMSSignedData cmsSignedData) {
        Iterator iterator = cmsSignedData.getSignerInfos().getSigners().iterator();

        while (iterator.hasNext()) {

            SignerInformation signerInformation = (SignerInformation) iterator.next();
            AttributeTable signedAttrTable = signerInformation.getSignedAttributes();
            if (signedAttrTable == null) {
                continue;
            }

            ASN1EncodableVector v = signedAttrTable.getAll(CMSAttributes.signingTime);
            switch (v.size()) {
                case 0:
                    continue;
                case 1: {
                    Attribute t = (Attribute) v.get(0);
                    ASN1Set attrValues = t.getAttrValues();
                    if (attrValues.size() != 1) {
                        continue;
                    }

                    //found it
                    try {
                        return ((ASN1UTCTime) attrValues.getObjectAt(0).getDERObject()).getDate();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
                default:
                    continue;
            }
        }

        //no timestamp found
        return null;
    }

    private SignatureData createSignatureDataFromXmlDigSig(String signature) throws SignatureException {

        //Start with setting some fields that are known directly
        SignatureData signatureData = new SignatureData();
        signatureData.setClientType(new ELegType("test", "test", PkiClient.NEXUS_PERSONAL_4X));
        signatureData.setSignature(new String(Base64.encode(signature.getBytes())));

        //For the rest we need to parse the XML
        try {
            Document document = createDocument(new ByteArrayInputStream(signature.getBytes()), false);

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
