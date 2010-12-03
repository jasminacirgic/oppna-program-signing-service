package se.vgregion.web.appx;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

@Controller
public class ClientXController {
    private Set<Signature> signatures = new HashSet<Signature>();

    public class Signature extends AbstractEntity<Integer> {
        byte[] pksc7;

        private Signature(byte[] pkcs7) {
            this.pksc7 = pkcs7;
        }

        @Override
        public Integer getId() {
            return Arrays.hashCode(pksc7);
        }

        @Override
        public String toString() {
            return new String(pksc7);
        }
    }

    @ModelAttribute("signatures")
    public Collection<Signature> getSignatures() {
        return signatures;
    }

    @RequestMapping(value = "/saveSignature", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.MOVED_TEMPORARILY)
    public void postback(HttpServletRequest req, HttpServletResponse response, @RequestBody String pkcs7) {
        byte[] decodedPkcs7 = Base64.decodeBase64(pkcs7);
        signatures.add(new Signature(decodedPkcs7));
        System.out.println("Ctxpath: " + req.getContextPath());
        System.out.println("svrlpath: " + req.getServletPath());
        String relocate = "http://" + req.getLocalName() + ":7080" + req.getContextPath() + req.getServletPath()
                + "/showSignStatus";
        System.out.println("Redirecting to: " + relocate);
        response.setHeader("Location", relocate);
    }

    @RequestMapping(value = "/showSignStatus", method = RequestMethod.GET)
    public String status() {
        System.setProperty("javax.net.debug", "ssl");
        return "showSignatures";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String signForm() {
        return "signForm";
    }
}
