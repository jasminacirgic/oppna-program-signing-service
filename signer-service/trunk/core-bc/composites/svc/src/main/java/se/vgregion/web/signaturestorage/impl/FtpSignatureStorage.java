package se.vgregion.web.signaturestorage.impl;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.vgregion.signera.signature._1.SignatureEnvelope;
import se.vgregion.web.ftp.SimpleFtpUploadClient;
import se.vgregion.web.security.services.SignatureServiceOsif;
import se.vgregion.web.signaturestorage.SignatureStorage;
import se.vgregion.web.signaturestorage.SignatureStoreageException;

/**
 * An Ftp-implementation of {@link SignatureStorage}. Has functionality to submit a signature to an ftp server
 * using ftp or ftps.
 * 
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 */
public class FtpSignatureStorage implements SignatureStorage {
    private static final Logger LOGGER = LoggerFactory.getLogger(SignatureServiceOsif.class);
    private SimpleFtpUploadClient uploadClient;

    /**
     * Constructs an instance of {@link FtpSignatureStorage}.
     * 
     * @param simpleFtpUploadClient
     *            an ftp client to use when submiting the signature
     */
    public FtpSignatureStorage(SimpleFtpUploadClient simpleFtpUploadClient) {
        this.uploadClient = simpleFtpUploadClient;
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.web.signaturestorage.SignatureStorage#submitSignature(java.net.URI, java.lang.String,
     * java.lang.String)
     */
    @Override
    public String submitSignature(URI submitUri, SignatureEnvelope envelope)
            throws SignatureStoreageException {
        if (StringUtils.isBlank(envelope.getSignatureName())) {
            throw new IllegalArgumentException("Signature name is not allowed to be empty");
        }
        try {
            if (!uploadClient.connect(submitUri) || !uploadClient.login()) {
                throw new SignatureStoreageException(uploadClient.readErrorMessage());
            }
            if (!uploadClient.upload(new ByteArrayInputStream(envelope.getSignature().getBytes("UTF-8")),
                    envelope.getSignatureName())) {
                throw new SignatureStoreageException(uploadClient.readErrorMessage());
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.debug("Invalid charset", e);
        } finally {
            if (!uploadClient.logoutAndDisconnect()) {
                throw new SignatureStoreageException(uploadClient.readErrorMessage());
            }
        }
        return StringUtils.EMPTY;
    }

}
