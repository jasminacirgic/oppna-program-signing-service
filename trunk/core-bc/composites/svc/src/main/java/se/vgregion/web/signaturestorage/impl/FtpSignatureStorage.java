package se.vgregion.web.signaturestorage.impl;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.apache.commons.lang.StringUtils;

import se.vgregion.web.ftp.SimpleFtpUploadClient;
import se.vgregion.web.signaturestorage.SignatureStorage;
import se.vgregion.web.signaturestorage.SignatureStoreageException;

/**
 * An Ftp-implementation of {@link SignatureStorage}. Has functionality to submit a signature to an ftp server
 * using ftp or ftps.
 * 
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 */
public class FtpSignatureStorage implements SignatureStorage {
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
    public String submitSignature(URI submitUri, String signature, String signatureName)
            throws SignatureStoreageException {
        if (StringUtils.isBlank(signature)) {
            throw new IllegalArgumentException("Signature is not allowed to be empty");
        }
        if (StringUtils.isBlank(signatureName)) {
            throw new IllegalArgumentException("Signature name is not allowed to be empty");
        }
        try {
            if (!uploadClient.connect(submitUri) || !uploadClient.login()) {
                throw new SignatureStoreageException(uploadClient.readErrorMessage());
            }
            if (!uploadClient.upload(new ByteArrayInputStream(signature.getBytes("UTF-8")), signatureName)) {
                throw new SignatureStoreageException(uploadClient.readErrorMessage());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            if (!uploadClient.logoutAndDisconnect()) {
                throw new SignatureStoreageException(uploadClient.readErrorMessage());
            }
        }
        return StringUtils.EMPTY;
    }
}
