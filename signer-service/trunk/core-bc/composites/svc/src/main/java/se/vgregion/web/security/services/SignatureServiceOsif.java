package se.vgregion.web.security.services;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SignatureException;
import java.util.UUID;

import com.logica.mbi.service.v1_0.CollectRequestType;
import com.logica.mbi.service.v1_0.CollectResponseType;
import com.logica.mbi.service.v1_0.SignRequestType;
import com.logica.mbi.service.v1_0.SignResponseType;
import com.logica.mbi.service.v1_0_0.MbiFault;
import com.logica.mbi.service.v1_0_0.MbiServicePortType;
import org.apache.commons.codec.binary.Base64;
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
import se.vgregion.signera.signature._1.SignatureEnvelope;
import se.vgregion.web.signaturestorage.SignatureStorage;
import se.vgregion.web.signaturestorage.SignatureStoreageException;

/**
 * Signature Service class. Contains methods to process a signature in different ways, such as verify and save it.
 *
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 */
public class SignatureServiceOsif implements ApplicationContextAware, SignatureService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SignatureServiceOsif.class);

    private ApplicationContext applicationContext;
    private Osif osif;
    private MbiServicePortType mbiServicePortType;
    private String policy;
    private String displayName;

    /**
     * Constructs a signature service instance. The serviceId is used by the OSIF service provider to identify the
     * consumer of the service.
     *
     * @param osif      an instance of {@link Osif}
     * @param serviceId the serviceId - used by the OSIF service provider to identify the consumer of the service
     * @see <a href="http://sveid.episerverhotell.net/upload/OSIF%20API%20Specifikation%202%200.pdf">OSIF
     *      API-Specifikation 2.0</a>
     */
    public SignatureServiceOsif(Osif osif, String serviceId, MbiServicePortType mbiServicePortType, String displayName) {
        this.osif = osif;
        this.mbiServicePortType = mbiServicePortType;
        /*
         * In the osif spec the consumer identifier is called policy while Logica,
         * as the osif serivce provider, uses the term serviceId for the same thing.
         */
        this.policy = serviceId;
        this.displayName = displayName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.web.security.services.SignatureService#verifySignature(se.vgregion.web.security.services.
     * SignatureData)
     */
    @Override
    public void verifySignature(SignatureData signData) throws SignatureException {
        VerifySignatureRequest request = createSignatureRequest(signData);
        VerifySignatureResponse response = osif.verifySignature(request);
        validateResponse(response);
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.web.security.services.SignatureService#encodeTbs(java.lang.String,
     * se.vgregion.domain.security.pkiclient.PkiClient)
     */
    @Override
    public String encodeTbs(String tbs, PkiClient provider) throws SignatureException {
        if (provider.getId() == PkiClient.NEXUS_PERSONAL_4X.getId()) {
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
        return Base64.encodeBase64String(tbs.getBytes()).trim();
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
        if (signData.getClientType() != null && signData.getPkiClient() != null) {
            request.setProvider(signData.getPkiClient().getId());
        }
        request.setSignature(signData.getSignature());
        request.setPolicy(policy);
        return request;
    }

    /*
     * (non-Javadoc)
     * 
     * @see SignatureService#generateNonce(PkiClient)
     */
    @Override
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * se.vgregion.web.security.services.SignatureService#save(se.vgregion.web.security.services.SignatureData)
     */
    @Override
    public String save(SignatureData signData) throws SignatureException {
        return save(signData, UUID.nameUUIDFromBytes(signData.toString().getBytes()).toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * se.vgregion.web.security.services.SignatureService#save(se.vgregion.web.security.services.SignatureData,
     * java.lang.String)
     */
    @Override
    public String save(SignatureData signData, String signatureName) throws SignatureException {
        SignatureEnvelope envelope = SignatureEnvelopeFactory.createSignatureEnvelope(signatureName, signData
                .getPkiClient().getSignatureFormat(), signData.getSignature());
        return submitEnvelope(signData, envelope);
    }

    /*
     * (non-Javadoc)
     * 
     * @see SignatureService#abort(SignatureData)
     */
    @Override
    public String abort(SignatureData signData) throws SignatureException {
        SignatureEnvelope envelope = SignatureEnvelopeFactory.createSignatureEnvelope(signData.getErrorCode(),
                SignErrorCode.getErrorMessage(signData.getErrorCode()));

        return submitEnvelope(signData, envelope);
    }

    @Override
    public String sendMobileSignRequest(SignatureData signData) throws SignatureException {
        SignRequestType signRequest = new SignRequestType();
        signRequest.setUserVisibleData(signData.getEncodedTbs());
        signRequest.setPolicy(policy);
        signRequest.setDisplayName(displayName);
        signRequest.setPersonalNumber(signData.getPersonalNumber());

        try {
            SignResponseType responseType = mbiServicePortType.sign(signRequest);
            return responseType.getOrderRef();
        } catch (MbiFault mbiFault) {
            throw new SignatureException(mbiFault);
        }
    }

    @Override
    public CollectResponseType collectRequest(String orderRef) throws SignatureException {
        try {
            CollectRequestType collectRequest = new CollectRequestType();
            collectRequest.setOrderRef(orderRef);
            collectRequest.setPolicy(policy);
            collectRequest.setDisplayName(displayName);

            return mbiServicePortType.collect(collectRequest);
        } catch (MbiFault mbiFault) {
            throw new SignatureException(mbiFault);
        }
    }

    private String submitEnvelope(SignatureData signData, SignatureEnvelope envelope) throws SignatureException {
        URI submitUri;
        try {
            submitUri = new URI(signData.getSubmitUriDecoded());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        SignatureStorage storage = getIOBackend(submitUri.getScheme());
        if (storage == null) {
            throw new SignatureException(new IllegalStateException(
                    "No storage is configured for the specified protocol"));
        }
        String forwardString = null;

        try {
            forwardString = storage.submitSignature(submitUri, envelope);
        } catch (SignatureStoreageException e) {
            throw new SignatureException(e.getMessage(), e);
        } catch (IOException e) {
            throw new SignatureException(e.getMessage(), e);
        }
        return forwardString;
    }

    private SignatureStorage getIOBackend(String protocol) {
        String beanName = protocol + "-signature-storage";
        if (applicationContext.containsBean(beanName)) {
            return (SignatureStorage) applicationContext.getBean(beanName);
        }
        throw new IllegalArgumentException("Unsupported protocol in submitUri: " + protocol);
    }

    /*
     * (non-Javadoc)
     * 
     * @see ApplicationContextAware#setApplicationContext(ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

}
