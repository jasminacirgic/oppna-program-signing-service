package se.vgregion.security.sign;

import java.security.SignatureException;
import java.util.Collection;
import java.util.Collections;

import se.vgregion.domain.security.pkiclient.ELegType;
import se.vgregion.domain.security.pkiclient.ELegTypeRepository;
import se.vgregion.web.security.services.SignatureData;
import se.vgregion.web.security.services.SignatureService;

public abstract class AbstractSignController {
    private SignatureService signatureService;
    private ELegTypeRepository eLegTypes;

    public AbstractSignController(SignatureService signatureService, ELegTypeRepository eLegTypes) {
        this.signatureService = signatureService;
        this.eLegTypes = eLegTypes;
    }

    protected SignatureService getSignatureService() {
        return signatureService;
    }

    protected ELegTypeRepository geteLegTypes() {
        return eLegTypes;
    }

    public Collection<ELegType> getClientTypes() {
        return Collections.unmodifiableCollection(eLegTypes.findAll());
    }

    public String prepareSign(SignatureData signData) throws SignatureException {
        encodeTbs(signData);
        signData.setNonce(signatureService.generateNonce(signData.getClientType().getPkiClient()));
        return signData.getClientType().getPkiClient().toString();
    }

    public boolean verifySignature(SignatureData signData) throws SignatureException {
        encodeTbs(signData);
        signatureService.verifySignature(signData);
        return true;
    }

    private void encodeTbs(SignatureData signData) throws SignatureException {
        String encodedTbs = signatureService.encodeTbs(signData.getTbs(), signData.getClientType().getPkiClient());
        signData.setEncodedTbs(encodedTbs);
    }
}
