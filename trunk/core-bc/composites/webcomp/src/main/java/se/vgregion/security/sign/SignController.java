package se.vgregion.security.sign;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SignatureException;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttributes;

import se.vgregion.domain.security.pkiclient.ELegType;
import se.vgregion.domain.security.pkiclient.ELegTypeRepository;
import se.vgregion.web.BrowserType;
import se.vgregion.web.security.services.SignatureService;

@Controller
@RequestMapping("sign/*")
@SessionAttributes({ "browserType", "clientTypes" })
public class SignController {
    @Autowired
    private SignatureService signatureService;

    @Autowired
    private ELegTypeRepository eLegTypes;

    @ModelAttribute("clientTypes")
    public Collection<ELegType> getClientTypes() {
        return Collections.unmodifiableCollection(eLegTypes.findAll());
    }

    @ModelAttribute("browserType")
    public BrowserType getBrowserType(HttpServletRequest request) {
        return BrowserType.getBrowser(request);
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

        ELegType eLegType = eLegTypes.find(clientType);
        String pkiPostBackUrl = buildPkiPostBackUrl(submitUri);
        SignForm signData = new SignForm(clientType, tbs, pkiPostBackUrl);
        model.addAttribute("signData", signData);
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

    @RequestMapping(value = "/saveSignature", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.MOVED_TEMPORARILY)
    public void postback(HttpServletResponse response,
            @RequestParam(value = "signature", required = false) String signature) {
        response.setHeader("Location", "http://www.google.se");
    }

    private String buildPkiPostBackUrl(String submitUri) {
        StringBuilder pkiPostUrl = new StringBuilder();
        pkiPostUrl.append("verify?submitUri=");
        pkiPostUrl.append(submitUri);

        return pkiPostUrl.toString();
    }
}
