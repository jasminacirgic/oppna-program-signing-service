package se.vgregion.security.sign;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

import se.vgregion.web.security.services.SignatureService;

@Controller
@RequestMapping("sign/*")
public class SignController {
    private static final Map<ClientType, String> CLIENT_TYPES = new HashMap<ClientType, String>();
    @Autowired
    private SignatureService signatureService;

    static {
        CLIENT_TYPES
                .put(new ClientType(
                        1,
                        "BankID",
                        "Danske Bank/Östgöta Enskilda Bank, Handelsbanken, Ikanobanken, Länsförsäkringar Bank, Skandiabanken, Sparbanken Finn, Sparbanken Gripen, Swedbank"),
                        "nexus_personal_4");
        CLIENT_TYPES.put(new ClientType(2, "Nordea", ""), "netmaker-netid_4");
        CLIENT_TYPES.put(new ClientType(3, "Telia", "ICA Banken, Posten, SEB, Skatteverket"), "nexus_personal_4");
    }

    @ModelAttribute("clientTypes")
    public Collection<ClientType> getClientTypes() {
        return CLIENT_TYPES.keySet();
    }

    @RequestMapping(value = "/prepare", method = RequestMethod.POST)
    public String prepareSign(@RequestParam("tbs") String tbs,
            @RequestParam(value = "submitUri") String submitUri,
            @RequestParam(value = "clientType", required = false) Integer clientType, Model model)
            throws IOException {

        if (clientType == null) {
            model.addAttribute("tbs", tbs);
            model.addAttribute("submitUri", submitUri);
            return "clientTypeSelection";
        }

        String pkiPostBackUrl = buildPkiPostBackUrl(submitUri);
        SignForm signData = new SignForm(clientType, tbs, pkiPostBackUrl);
        model.addAttribute("signData", signData);
        return "netmaker-netid_4";
    }

    private String buildPkiPostBackUrl(String submitUri) {
        StringBuilder pkiPostUrl = new StringBuilder();
        pkiPostUrl.append("verify?submitUri=");
        pkiPostUrl.append(submitUri);

        return pkiPostUrl.toString();
    }

    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    public String postback(@RequestParam(value = "signedData") String signedData,
            @RequestParam(value = "submitUri") String submitUri) throws URISyntaxException {

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
}
