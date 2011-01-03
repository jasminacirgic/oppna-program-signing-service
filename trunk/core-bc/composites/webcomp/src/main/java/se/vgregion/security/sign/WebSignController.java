package se.vgregion.security.sign;

import java.io.IOException;
import java.security.SignatureException;
import java.util.Collection;

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
public class WebSignController extends AbstractSignController {

    @Autowired
    public WebSignController(SignatureService signatureService, ELegTypeRepository eLegTypes) {
        super(signatureService, eLegTypes);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(ELegType.class, new ELegTypeEditor(geteLegTypes()));
    }

    @Override
    @ModelAttribute("clientTypes")
    public Collection<ELegType> getClientTypes() {
        return super.getClientTypes();
    }

    @RequestMapping(value = "/prepare", method = RequestMethod.POST, params = { "tbs", "submitUri" })
    public String prepareSignNoClientType(Model model, @ModelAttribute SignatureData signData) throws IOException {
        model.addAttribute("signData", signData);
        return "clientTypeSelection";
    }

    @RequestMapping(value = "/prepare", method = RequestMethod.POST, params = { "tbs", "submitUri", "clientType" })
    public String prepareSign(@ModelAttribute SignatureData signData, Model model, HttpServletRequest req)
            throws SignatureException {
        model.addAttribute("postbackUrl", getPkiPostBackUrl(req));
        model.addAttribute("signData", signData);
        return super.prepareSign(signData);
    }

    @RequestMapping(value = "/verify", method = RequestMethod.POST, params = { "tbs", "submitUri", "clientType",
            "signature" })
    public String verifyAndSaveSignature(@ModelAttribute SignatureData signData) throws SignatureException {
        super.verifySignature(signData);
        String redirectLocation = getSignatureService().save(signData);
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
