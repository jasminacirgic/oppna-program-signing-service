package se.vgregion.security.sign;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SignatureException;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import se.vgregion.domain.security.pkiclient.ELegType;
import se.vgregion.domain.security.pkiclient.ELegTypeRepository;
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

    @RequestMapping(value = "/prepare", method = RequestMethod.POST, params = "clientType")
    public String prepareSign(HttpServletRequest request, @RequestParam("tbs") String tbs,
            @RequestParam("clientType") String clientType, Model model,
            @RequestParam(value = "submitUri", required = false) String submitUri) throws IOException {

        SignForm signForm = new SignForm(tbs, submitUri, "nonce");
        model.addAttribute("signData", signForm);

        ELegType eLegType = eLegTypes.find(clientType);
        return eLegType.getPkiClient().toString();
    }

    @RequestMapping(value = "/prepare", method = RequestMethod.POST)
    public String prepareSignNoClientType(HttpServletRequest request, Model model,
            @RequestParam("tbs") String tbs, @RequestParam(value = "submitUri", required = false) String submitUri)
            throws IOException {
        model.addAttribute("tbs", tbs);
        model.addAttribute("submitUri", submitUri);
        return "clientTypeSelection";
    }

    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    public String postback(@RequestParam(value = "SignedData", required = false) String signedData,
            @ModelAttribute("signData") SignForm signData, HttpServletRequest req) throws URISyntaxException,
            SignatureException {

        // System.out.println(Collections.list(req.getParameterNames()));

        String redirectLocation = signatureService.save(signData.getTbs(), new URI(signData.getSubmitUri()),
                signedData);
        if (redirectLocation != null) {
            return "redirect:" + redirectLocation;
        }
        return "verified";
    }
}
