package se.vgregion.web.signaturestorage.impl;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.apache.commons.lang.StringUtils;

import se.vgregion.web.ftp.SimpleFtpUploadClient;
import se.vgregion.web.signaturestorage.SignatureStorage;
import se.vgregion.web.signaturestorage.SignatureStoreageException;

public class FtpSignatureStorage implements SignatureStorage {
    private SimpleFtpUploadClient uploadClient;

    public FtpSignatureStorage(SimpleFtpUploadClient simpleFtpUploadClient) {
        this.uploadClient = simpleFtpUploadClient;
    }

    @Override
    public String submitSignature(URI submitUri, String signature, String signatureName)
            throws SignatureStoreageException {
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
