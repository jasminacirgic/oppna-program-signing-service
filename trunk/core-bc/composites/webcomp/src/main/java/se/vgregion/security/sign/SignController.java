package se.vgregion.security.sign;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SignatureException;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import se.vgregion.domain.security.pkiclient.ELegType;
import se.vgregion.domain.security.pkiclient.ELegTypeRepository;
import se.vgregion.web.exceptions.IllegalWebArgumentException;
import se.vgregion.web.security.services.SignatureData;
import se.vgregion.web.security.services.SignatureService;

@Controller
@SessionAttributes("signData")
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
    public String prepareSignNoClientType(HttpServletRequest request, Model model,
            @RequestParam("tbs") String tbs, @RequestParam(value = "submitUri", required = false) String submitUri)
            throws IOException {
        model.addAttribute("tbs", tbs);
        model.addAttribute("submitUri", submitUri);
        return "clientTypeSelection";
    }

    @RequestMapping(value = "/prepare", method = RequestMethod.POST, params = "clientType")
    public String prepareSign(HttpServletRequest request, @RequestParam("tbs") String tbs,
            @RequestParam("clientType") String clientType,
            @RequestParam(value = "submitUri", required = false) String submitUri, Model model,
            HttpServletRequest req) throws IOException, URISyntaxException {

        URI uri = null;
        ELegType eLegType = getElegType(clientType);

        String encodedTbs = signatureService.encodeTbs(tbs);
        String nonce = signatureService.generateNonce();

        if (!StringUtils.isBlank(submitUri)) {
            uri = new URI(submitUri);
        }

        SignatureData signData = new SignatureData(encodedTbs, encodedTbs, uri, nonce, getPkiPostBackUrl(req),
                eLegType.getPkiClient());

        model.addAttribute("signData", signData);

        return eLegType.getPkiClient().toString();
    }

    private ELegType getElegType(String clientType) {
        ELegType eLegType = eLegTypes.find(clientType);
        if (eLegType == null) {
            throw new IllegalWebArgumentException();
        }
        return eLegType;
    }

    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    public String postback(@RequestParam("signature") String signature,
            @ModelAttribute("signData") SignatureData signData, SessionStatus sessionStatus)
            throws SignatureException {

        signData.setSignature(signature);
        signatureService.validate(signData);
        String redirectLocation = signatureService.save(signData);

        sessionStatus.setComplete();
        if (redirectLocation != null) {
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
