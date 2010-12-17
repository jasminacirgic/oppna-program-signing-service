package se.vgregion.web.security.services;

import java.io.IOException;
import java.net.URI;
import java.security.SignatureException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import se.sll.wsdl.soap.osif.EncodeTBSRequest;
import se.sll.wsdl.soap.osif.EncodeTBSResponse;
import se.sll.wsdl.soap.osif.GenerateChallengeRequest;
import se.sll.wsdl.soap.osif.GenerateChallengeResponse;
import se.sll.wsdl.soap.osif.Osif;
import se.sll.wsdl.soap.osif.VerifySignatureRequest;
import se.sll.wsdl.soap.osif.VerifySignatureResponse;
import se.vgregion.domain.security.pkiclient.PkiClient;
import se.vgregion.web.signaturestorage.SignatureStorage;
import se.vgregion.web.signaturestorage.SignatureStoreageException;

public class SignatureService implements ApplicationContextAware {
    private static Logger LOGGER = LoggerFactory.getLogger(SignatureService.class);

    private SignatureStorage storage = null;

    private ApplicationContext applicationContext;
    private Osif osif;
    private String policy;

    public SignatureService(Osif osif, String serviceId) {
        this.osif = osif;
        this.policy = serviceId;
    }

    public void verifySignature(SignatureData signData) throws SignatureException {
        VerifySignatureRequest request = createSignatureRequest(signData);
        VerifySignatureResponse response = osif.verifySignature(request);
        validateResponse(response);
    }

    public String encodeTbs(String tbs, PkiClient provider) throws SignatureException {
        EncodeTBSRequest request = new EncodeTBSRequest();
        request.setPolicy(policy);
        request.setProvider(provider.getId());
        request.setTbsText(tbs);
        EncodeTBSResponse response = osif.encodeTBS(request);
        if (response.getStatus().getErrorCode() != 0) {
            String errorMsg = response.getStatus().getErrorGroupDescription() + ": "
                    + response.getStatus().getErrorCodeDescription();
            LOGGER.error(errorMsg);

            throw new SignatureException(response.getStatus().getErrorGroupDescription() + ": "
                    + response.getStatus().getErrorCodeDescription());
        }
        return response.getText();
    }

    private void validateResponse(VerifySignatureResponse response) throws SignatureException {
        if (response.getStatus().getErrorCode() != 0) {
            String errorMsg = response.getStatus().getErrorGroupDescription() + ": "
                    + response.getStatus().getErrorCodeDescription();
            LOGGER.error(errorMsg);

            throw new SignatureException(response.getStatus().getErrorGroupDescription() + ": "
                    + response.getStatus().getErrorCodeDescription());
        }
    }

    private VerifySignatureRequest createSignatureRequest(SignatureData signData) {
        VerifySignatureRequest request = new VerifySignatureRequest();
        request.setTbsText(signData.getEncodedTbs());
        request.setNonce(signData.getEncodedNonce());
        request.setProvider(signData.getClientType().getPkiClient().getId());
        request.setSignature(signData.getSignature());
        request.setPolicy(policy);
        return request;
    }

    public String generateNonce(PkiClient provider) throws SignatureException {
        GenerateChallengeRequest request = new GenerateChallengeRequest();
        request.setPolicy(policy);
        request.setProvider(provider.getId());
        GenerateChallengeResponse response = osif.generateChallenge(request);
        if (response.getStatus().getErrorCode() != 0) {
            String errorMsg = response.getStatus().getErrorGroupDescription() + ": "
                    + response.getStatus().getErrorCodeDescription();
            LOGGER.error(errorMsg);

            throw new SignatureException(response.getStatus().getErrorGroupDescription() + ": "
                    + response.getStatus().getErrorCodeDescription());
        }
        return response.getChallenge();
    }

    public String save(SignatureData signData) throws SignatureException {
        String signature = signData.getSignature();
        return save(signData, UUID.nameUUIDFromBytes(signature.getBytes()).toString());
    }

    public String save(SignatureData signData, String signatureName) throws SignatureException {

        URI submitUri = signData.getSubmitUri();
        setupIOBackend(submitUri.getScheme());

        if (storage == null) {
            throw new SignatureException(new IllegalStateException(
                    "No storage is configured for the specified protocol"));
        }
        String forwardString = null;

        try {
            forwardString = storage.submitSignature(submitUri, signData.getSignature(), signatureName);
        } catch (SignatureStoreageException e) {
            throw new SignatureException(e.getMessage(), e);
        } catch (IOException e) {
            throw new SignatureException(e.getMessage(), e);
        }
        return forwardString;
    }

    private void setupIOBackend(String protocol) {
        String beanName = protocol + "-signature-storage";
        if (applicationContext.containsBean(beanName)) {
            storage = (SignatureStorage) applicationContext.getBean(beanName);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
