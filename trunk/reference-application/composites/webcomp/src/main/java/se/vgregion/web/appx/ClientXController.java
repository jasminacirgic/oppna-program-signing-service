package se.vgregion.web.appx;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class ClientXController {
    @Autowired
    private SignatureRepository signatures;

    @ModelAttribute("signatures")
    public Collection<Signature> getSignatures() {
        return signatures.findAll();
    }

    @RequestMapping(value = "/saveSignature", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.MOVED_TEMPORARILY)
    public void postback(HttpServletRequest req, HttpServletResponse response, @RequestBody byte[] signature) {
        signatures.store(new Signature(signature));
        String relocate = "http://" + req.getLocalName() + ":7080" + req.getContextPath() + req.getServletPath()
                + "/showSignStatus";
        response.setHeader("Location", relocate);
    }

    @RequestMapping(value = "/showSignStatus", method = RequestMethod.GET)
    public String status() {
        return "showSignatures";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String signForm() {
        return "signForm";
    }

    @RequestMapping(value = "/clean", method = RequestMethod.POST)
    public String cleanSignatures() {
        signatures.removeAll();
        return "showSignatures";
    }
}
