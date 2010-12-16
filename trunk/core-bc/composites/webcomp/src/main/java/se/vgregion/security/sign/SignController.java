package se.vgregion.security.sign;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.SignatureException;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import se.vgregion.domain.security.pkiclient.ELegType;
import se.vgregion.domain.security.pkiclient.ELegTypeRepository;
import se.vgregion.web.security.services.SignatureData;
import se.vgregion.web.security.services.SignatureService;

@Controller
public class SignController {
    @Autowired
    private SignatureService signatureService;

    @Autowired
    private ELegTypeRepository eLegTypes;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(ELegType.class, new ELegTypeEditor(eLegTypes));
    }

    @ModelAttribute("clientTypes")
    public Collection<ELegType> getClientTypes() {
        return Collections.unmodifiableCollection(eLegTypes.findAll());
    }

    private String getPkiPostBackUrl(HttpServletRequest req) {
        StringBuilder pkiPostUrl = new StringBuilder();
        String verifyUrl = "http" + (req.isSecure() ? "s" : "") + "://" + req.getServerName() + ":"
                + req.getServerPort() + req.getContextPath() + "/sign/verify";
        pkiPostUrl.append(verifyUrl);

        return pkiPostUrl.toString();
    }

    @RequestMapping(value = "/prepare", method = RequestMethod.POST, params = { "tbs", "submitUri" })
    public String prepareSignNoClientType(Model model, @ModelAttribute SignatureData signData,
            HttpServletRequest req) throws IOException {
        model.addAttribute("signData", signData);
        return "clientTypeSelection";
    }

    @RequestMapping(value = "/prepare", method = RequestMethod.POST, params = { "tbs", "submitUri", "clientType" })
    public String prepareSign(HttpServletRequest request, @ModelAttribute SignatureData signData, Model model,
            HttpServletRequest req) throws IOException, URISyntaxException {

        signData.setNonce(signatureService.generateNonce());
        signData.setEncodedTbs(signatureService.encodeTbs(signData.getTbs()));
        model.addAttribute("postbackUrl", getPkiPostBackUrl(req));
        model.addAttribute("signData", signData);
        return signData.getClientType().getPkiClient().toString();
    }

    @RequestMapping(value = "/verify", method = RequestMethod.POST, params = { "encodedTbs", "submitUri",
            "clientType", "signature" })
    public String postback(@ModelAttribute SignatureData signData, HttpServletRequest req)
            throws SignatureException {
        signatureService.verifySignature(signData);
        String redirectLocation = signatureService.save(signData);
        if (redirectLocation != null) {
            return "redirect:" + redirectLocation;
        }
        return "verified";
    }
}
