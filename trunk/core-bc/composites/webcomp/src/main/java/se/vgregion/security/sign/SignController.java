package se.vgregion.security.sign;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SignatureException;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import se.vgregion.domain.security.pkiclient.ELegType;
import se.vgregion.domain.security.pkiclient.ELegTypeRepository;
import se.vgregion.domain.web.BrowserType;
import se.vgregion.web.security.services.SignatureService;

@Controller
public class SignController {
    @Autowired
    private SignatureService signatureService;

    @Autowired
    private ELegTypeRepository eLegTypes;

    @ModelAttribute("clientTypes")
    public Collection<ELegType> getClientTypes() {
        return Collections.unmodifiableCollection(eLegTypes.findAll());
    }

    @RequestMapping(value = "/prepare", method = RequestMethod.POST)
    public String prepareSign(@RequestParam("tbs") String tbs,
            @RequestParam(value = "submitUri") String submitUri,
            @RequestParam(value = "clientType", required = false) String clientType, Model model,
            HttpServletRequest request) throws IOException {

        if (clientType == null) {
            model.addAttribute("tbs", tbs);
            model.addAttribute("submitUri", submitUri);
            return "clientTypeSelection";
        }

        String userAgent = request.getHeader("User-Agent");
        model.addAttribute("browserType", BrowserType.fromUserAgent(userAgent));

        String pkiPostBackUrl = buildPkiPostBackUrl(submitUri, request);
        System.out.println(pkiPostBackUrl);
        SignForm signForm = new SignForm(clientType, tbs, pkiPostBackUrl);
        model.addAttribute("signData", signForm);

        ELegType eLegType = eLegTypes.find(clientType);
        return eLegType.getPkiClientName();
    }

    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    public String postback(@RequestParam(value = "SignedData", required = false) String signedData,
            @RequestParam(value = "submitUri", required = false) String submitUri) throws URISyntaxException,
            SignatureException {

        byte[] pkcs7 = Base64.decodeBase64(signedData);
        String redirectLocation = signatureService.save(new URI(submitUri), pkcs7);
        if (redirectLocation != null) {
            return "redirect:" + redirectLocation;
        }
        return "verified";
    }

    private String buildPkiPostBackUrl(String submitUri, HttpServletRequest req) {
        StringBuilder pkiPostUrl = new StringBuilder();
        pkiPostUrl.append("http://" + req.getLocalName() + ":" + req.getLocalPort() + "/sign/verify?submitUri=");
        pkiPostUrl.append(submitUri);

        return pkiPostUrl.toString();
    }
}
