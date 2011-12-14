package se.vgregion.web.appx;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertStoreException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.cxf.jaxrs.client.WebClient;
import org.bouncycastle.cms.CMSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import se.vgregion.dao.domain.patterns.repository.Repository;
import se.vgregion.signera.signature._1.SignatureEnvelope;
import se.vgregion.signera.signature._1.SignatureFormat;
import se.vgregion.signera.signature._1.SignatureVerificationRequest;

@Controller
public class ClientXController {

    @Autowired
    private Repository<Signature, Integer> signaturesRepository;
    @Autowired
    private String signerServiceLocation;
    @Autowired
    private String submitUrl;
    @Autowired
    private WebClient client;// = WebClient.create("https://localhost:9443/service/solveTicket/");

    @Value("${service-id}")
    private String serviceId;
    @Value("${verify_signature.url}")
    private String verifySignatureUrl;

    @ModelAttribute("signatures")
    public Collection<Signature> getSignatures() {
        return signaturesRepository.findAll();
    }

    @ModelAttribute("signerServiceLocation")
    public String getSignerServiceLocation() {
        return signerServiceLocation;
    }

    @ModelAttribute("submitUrl")
    public String getSubmitUrl() {
        return submitUrl;
    }

    /**
     * Handler method called by Spring.
     *
     * @param req request
     * @param response response
     * @param envelope envelope
     * @throws IOException IOException
     */
    @RequestMapping(value = "/saveSignature", method = POST)
    public void postback(HttpServletRequest req, HttpServletResponse response,
            @RequestBody SignatureEnvelope envelope) throws IOException {
        StringBuilder relocateUrl = new StringBuilder("http://").append(req.getLocalName()).append(":7080")
                .append(req.getContextPath()).append(req.getServletPath());

        if (envelope.getErrorCode() > 0) {
            relocateUrl.append("/abort?errormessage=").append(
                    URLEncoder.encode(envelope.getErrorMessage(), "UTF-8"));
        } else {
            signaturesRepository.store(new Signature(envelope.getSignature().getBytes(),
                    envelope.getSignatureFormat()));
            relocateUrl.append("/showSignStatus");
        }
        String encodedRedirectURL = response.encodeRedirectURL(relocateUrl.toString());
        response.sendRedirect(encodedRedirectURL);
    }

    /**
     * Handler method called by Spring.
     *
     * @return a view
     * @throws CMSException CMSException
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws NoSuchProviderException NoSuchProviderException
     * @throws CertStoreException CertStoreException
     * @throws IOException IOException
     */
    @RequestMapping(value = "/showSignStatus", method = GET)
    public String status() throws CMSException, NoSuchAlgorithmException, NoSuchProviderException,
    CertStoreException, IOException {
        // for (Signature signature : getSignatures()) {
        // String s = signature.getDecoded();
        //
        // CMSSignedData signedData = new CMSSignedData(s.getBytes());
        // Collection<X509CertificateHolder> signerCerts = signedData.getCertificates().getMatches(null);
        // for (X509CertificateHolder cert : signerCerts) {
        // System.out.println(cert.getSubject());
        // }
        // }
        return "showSignatures";
    }

    @RequestMapping(value = "/verifySignature", method = POST)
    @ResponseBody
    public void verifySignature(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Content-Type", "text/xml");

        String encodedSignature = httpServletRequest.getParameter("signature");
        String format = httpServletRequest.getParameter("signatureFormat");

        if (encodedSignature == null || format == null) {
            try {
                PrintWriter writer = httpServletResponse.getWriter();
                writer.append("Illegal arguments");
                writer.close();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        SignatureVerificationRequest verificationRequest = new SignatureVerificationRequest();
        verificationRequest.setSignature(encodedSignature);
        verificationRequest.setSignatureFormat(SignatureFormat.fromValue(format));

        //Marshal the SignatureVerificationRequest
        String requestBody = createRequestBody(verificationRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> entity = new HttpEntity<String>(requestBody, headers);

        RestTemplate template = new RestTemplate();

        ResponseEntity<String> response = template.exchange(verifySignatureUrl, HttpMethod.POST, entity,
                String.class);

        String body = response.getBody();

        try {
            PrintWriter writer = httpServletResponse.getWriter();
            writer.append(body);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String createRequestBody(SignatureVerificationRequest verificationRequest) {
        ByteArrayOutputStream baos;
        try {
            JAXBContext jc = JAXBContext.newInstance(SignatureVerificationRequest.class);
            baos = new ByteArrayOutputStream();
            jc.createMarshaller().marshal(verificationRequest, baos);
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to marshal SignatureVerificationRequest");
        }
        return baos.toString();
    }

    /**
     * Handler method called by Spring.
     *
     * @param model the model
     * @return a view
     */
    @RequestMapping(value = "/", method = GET)
    public String signForm(Model model) {
        //Get ticket and add to model
        client.accept("application/json");
        String ticket = client.path("{serviceId}", serviceId).get(String.class);
        model.addAttribute("ticket", ticket);
        return "signForm";
    }

    /**
     * Handler method called by Spring.
     *
     * @param model the model
     * @param errorMessage errorMessage
     * @return a view
     */
    @RequestMapping(value = "/abort", method = GET)
    public String errorForm(Model model, @RequestParam("errormessage") String errorMessage) {
        model.addAttribute("errormessage", errorMessage);
        return "errorForm";
    }

    /**
     * Handler method called by Spring.
     *
     * @return a view
     */
    @RequestMapping(value = "/clean", method = POST)
    public String cleanSignatures() {
        Collection<Signature> signatures = getSignatures();
        for (Signature signature : signatures) {
            signatures.remove(signature);
        }
        return "showSignatures";
    }

    public static void main(String[] args) {
        final WebClient client = WebClient.create("https://localhost:9443/service/solveTicket/",
                "/Users/anders/src/vgr/oppna-program-signing-service/signer-service/trunk/reference-application/modules/web/src/main/resources/https.xml");
        client.accept("application/json");
        String ticket = client.path("{serviceId}", "someServiceId").get(String.class);
        System.out.println(ticket);
    }
}
