package se.vgregion.web.security.verification;

import java.security.SignatureException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sll.wsdl.soap.osif.Osif;
import se.sll.wsdl.soap.osif.VerifySignatureRequest;
import se.sll.wsdl.soap.osif.VerifySignatureResponse;
import se.vgregion.domain.security.pkiclient.PkiClient;

public class SignatureValidator {
    private static Logger LOGGER = LoggerFactory.getLogger(SignatureValidator.class);

    private Osif osif;
    private String policy;

    public SignatureValidator(Osif osif) {
        this.osif = osif;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public void validate(String signature, String tbs, PkiClient pkiClient) throws SignatureException {
        VerifySignatureRequest request = createSignatureRequest(signature, tbs, pkiClient);
        VerifySignatureResponse response = osif.verifySignature(request);
        validateResponse(response);
    }

    private void validateResponse(VerifySignatureResponse response) throws SignatureException {
        if (response.getStatus().getErrorCode() != 0) {
            String errorMsg = response.getStatus().getErrorGroupDescription() + ": "
                    + response.getStatus().getErrorCodeDescription();
            LOGGER.error(errorMsg);

            throw new SignatureException(response.getStatus().getErrorGroupDescription());
        }
    }

    private VerifySignatureRequest createSignatureRequest(String signature, String tbs, PkiClient pkiClient) {
        VerifySignatureRequest request = new VerifySignatureRequest();
        request.setTbsText(tbs);
        request.setNonce("MTIzNDU2Nzg5MGFiYw==");
        request.setProvider(pkiClient.getId());
        request.setSignature(signature);
        request.setPolicy(policy);
        return request;
    }

    // private String getNounce() {
    // GenerateChallengeResponse resp = osif.generateChallenge(new GenerateChallengeRequest());
    // System.out.println(resp.getChallenge());
    // return resp.getChallenge();
    // }
}
