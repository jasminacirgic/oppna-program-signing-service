package se.vgregion.security.sign;

import java.security.SignatureException;
import java.util.Collection;

import org.springframework.web.bind.annotation.RequestBody;

import se.vgregion.domain.security.pkiclient.ELegType;
import se.vgregion.domain.security.pkiclient.ELegTypeRepository;
import se.vgregion.web.security.services.SignatureData;
import se.vgregion.web.security.services.SignatureService;

public class RestSignController extends AbstractSignController {

    public RestSignController(SignatureService signatureService, ELegTypeRepository eLegTypes) {
        super(signatureService, eLegTypes);
    }

    @Override
    public Collection<ELegType> getClientTypes() {
        return super.getClientTypes();
    }

    @Override
    public String prepareSign(@RequestBody SignatureData signData) throws SignatureException {
        return super.prepareSign(signData);
    }

    @Override
    public boolean verifySignature(@RequestBody SignatureData signData) throws SignatureException {
        return super.verifySignature(signData);
    }
}
