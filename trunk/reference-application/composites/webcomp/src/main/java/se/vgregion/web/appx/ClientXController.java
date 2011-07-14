package se.vgregion.web.appx;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertStoreException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.bouncycastle.cms.CMSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import se.vgregion.dao.domain.patterns.repository.Repository;
import se.vgregion.web.appx.Signature.SignatureFormat;

@Controller
public class ClientXController {

    private static final String XPATH_SIGNATURE = "//signatureenvelope/signature/text()";
    private static final String XPATH_SIGNATURE_FORMAT = "//signatureenvelope/signatureformat/text()";
    private static final String XPATH_ERROR_CODE = "//signatureenvelope/errorcode/text()";
    private static final String XPATH_ERROR_MESSAGE = "//signatureenvelope/errormessage/text()";

    @Autowired
    private Repository<Signature, Integer> signatures;
    @Autowired
    private DocumentBuilder docBuilder;
    @Autowired
    private XPath xPath;

    @ModelAttribute("signatures")
    public Collection<Signature> getSignatures() {
        return signatures.findAll();
    }

    @RequestMapping(value = "/saveSignature", method = RequestMethod.POST)
    public void postback(HttpServletRequest req, HttpServletResponse response, @RequestBody String envelope)
            throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
        String relocate = "http://" + req.getLocalName() + ":7080" + req.getContextPath() + req.getServletPath();
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(envelope.getBytes());
            Document envelopeDoc = docBuilder.parse(bais);
            int errorCode = ((Double)xPath.compile(XPATH_ERROR_CODE).evaluate(envelopeDoc, XPathConstants.NUMBER)).intValue();

            if(errorCode > 0) {
                String errorMsg = (String)xPath.compile(XPATH_ERROR_MESSAGE).evaluate(envelopeDoc, XPathConstants.STRING);
                relocate += "/abort?errormessage=" + URLEncoder.encode(errorMsg, "UTF-8");
            } else {
                String signature = (String)xPath.compile(XPATH_SIGNATURE).evaluate(envelopeDoc, XPathConstants.STRING);
                String signatureFormat = (String)xPath.compile(XPATH_SIGNATURE_FORMAT).evaluate(envelopeDoc, XPathConstants.STRING);
                signatures.store(new Signature(signature.getBytes(), SignatureFormat.valueOf(signatureFormat)));
                relocate += "/showSignStatus";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(bais != null) {
                bais.close();
            }
        }
        String encodedRedirectURL = response.encodeRedirectURL(relocate);
        System.out.println(encodedRedirectURL);
        response.sendRedirect(encodedRedirectURL);
    }

    @RequestMapping(value = "/showSignStatus", method = RequestMethod.GET)
    public String status() throws CMSException, NoSuchAlgorithmException, NoSuchProviderException,
    CertStoreException, IOException {
        //        for (Signature signature : getSignatures()) {
        //            String s = signature.getDecoded();
        //
        //            CMSSignedData signedData = new CMSSignedData(s.getBytes());
        //            Collection<X509CertificateHolder> signerCerts = signedData.getCertificates().getMatches(null);
        //            for (X509CertificateHolder cert : signerCerts) {
        //                System.out.println(cert.getSubject());
        //            }
        //        }
        return "showSignatures";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String signForm() {
        return "signForm";
    }

    @RequestMapping(value = "/abort", method = RequestMethod.GET)
    public String errorForm(Model model, @RequestParam("errormessage") String errorMessage) {
        model.addAttribute("errormessage", errorMessage);
        return "errorForm";
    }

    @RequestMapping(value = "/clean", method = RequestMethod.POST)
    public String cleanSignatures() {
        Collection<Signature> signatures = getSignatures();
        for (Signature signature : signatures) {
            signatures.remove(signature);
        }
        return "showSignatures";
    }

}
