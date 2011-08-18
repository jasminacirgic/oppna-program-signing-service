package se.vgregion.web.appx;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertStoreException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.cms.CMSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import se.vgregion.dao.domain.patterns.repository.Repository;
import se.vgregion.proxy.signera.signature.SignatureEnvelope;

@Controller
public class ClientXController {

    @Autowired
    private Repository<Signature, Integer> signatures;

    @ModelAttribute("signatures")
    public Collection<Signature> getSignatures() {
        return signatures.findAll();
    }

    @RequestMapping(value = "/saveSignature", method = POST)
    public void postback(HttpServletRequest req, HttpServletResponse response,
            @RequestBody SignatureEnvelope envelope) throws IOException {
        StringBuilder relocateUrl = new StringBuilder("http://").append(req.getLocalName()).append(":7080")
                .append(req.getContextPath()).append(req.getServletPath());

        if (envelope.getErrorCode() > 0) {
            relocateUrl.append("/abort?errormessage=").append(
                    URLEncoder.encode(envelope.getErrorMessage(), "UTF-8"));
        } else {
            signatures.store(new Signature(envelope.getSignature().getBytes(), envelope.getSignatureFormat()));
            relocateUrl.append("/showSignStatus");
        }
        String encodedRedirectURL = response.encodeRedirectURL(relocateUrl.toString());
        response.sendRedirect(encodedRedirectURL);
    }

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

    @RequestMapping(value = "/", method = GET)
    public String signForm() {
        return "signForm";
    }

    @RequestMapping(value = "/abort", method = GET)
    public String errorForm(Model model, @RequestParam("errormessage") String errorMessage) {
        model.addAttribute("errormessage", errorMessage);
        return "errorForm";
    }

    @RequestMapping(value = "/clean", method = POST)
    public String cleanSignatures() {
        Collection<Signature> signatures = getSignatures();
        for (Signature signature : signatures) {
            signatures.remove(signature);
        }
        return "showSignatures";
    }

}
