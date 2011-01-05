package se.vgregion.web.security.services;

import java.io.IOException;
import java.net.URI;
import java.security.SignatureException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * Signature Service class. Contains methods to process a signature in different ways, such as verify and save it.
 * 
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 */
public class SignatureService implements ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(SignatureService.class);

    private SignatureStorage storage = null;

    private ApplicationContext applicationContext;
    private Osif osif;
    private String policy;

    /**
     * Constructs a signature service instance. The serviceId is used by the OSIF service provider to identify the
     * consumer of the service.
     * 
     * @param osif
     *            an instance of {@link Osif}
     * @param serviceId
     *            the serviceId - used by the OSIF service provider to identify the consumer of the service
     * @see <a href="http://sveid.episerverhotell.net/upload/OSIF%20API%20Specifikation%202%200.pdf">OSIF
     *      API-Specifikation 2.0</a>
     */
    public SignatureService(Osif osif, String serviceId) {
        this.osif = osif;
        this.policy = serviceId;
    }

    /**
     * Verifies message from the client when signing data.
     * 
     * @param signData
     *            the data to sign.
     * @throws SignatureException
     *             if verification of the signature is invalid in some way.
     */
    public void verifySignature(SignatureData signData) throws SignatureException {
        VerifySignatureRequest request = createSignatureRequest(signData);
        VerifySignatureResponse response = osif.verifySignature(request);
        validateResponse(response);
    }

    /**
     * Encodes the tbs - To Be Signed according to the pki clients requirement.
     * 
     * @param tbs
     *            data To Be Signed.
     * @param provider
     *            the pki client used when signing the tbs
     * @return returns a base64-encoded string of tbs
     * @throws SignatureException
     *             if something went wrong when encoding the tbs
     */
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

    /**
     * Generates a random string used to prevent replay attacks. The number is generated according to the pki
     * clients requirement.
     * 
     * @param provider
     *            the pki client used for signing
     * @return returns a random string
     * @throws SignatureException
     *             if the generation of the nonce faild
     */
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

    /**
     * Saves the signature using the information supplied in signData. Where and how the signature is saved is
     * supplied in signData and if the signature is saved as a file a random name is created. To set you own name
     * use {@link SignatureService#save(SignatureData, String)} instead. The method can return a string containing
     * a callback url, the method client should redirect to the callback url.
     * 
     * @param signData
     *            information about the signature
     * @return a callback url
     * @throws SignatureException
     *             if the save was unsuccessful
     */
    public String save(SignatureData signData) throws SignatureException {
        String signature = signData.getSignature();
        return save(signData, UUID.nameUUIDFromBytes(signature.getBytes()).toString());
    }

    /**
     * Saves the signature using the information supplied in signData ie. where and how the signature is saved. The
     * method can return a string containing a callback url, the method client should redirect to the callback url.
     * 
     * @param signData
     *            information about the signature
     * @param signatureName
     *            the name under which the signature is saved
     * @return a callback url
     * @throws SignatureException
     *             if the save was unsuccessful
     */
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

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.
     * ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
