package se.vgregion.web.signaturestorage.impl;

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
    public String save(URI submitUri, byte[] pkcs7, String signatureName) throws SignatureStoreageException {
        try {
            if (!uploadClient.connect(submitUri) || !uploadClient.login()) {
                throw new SignatureStoreageException(uploadClient.readErrorMessage());
            }
            if (!uploadClient.upload(pkcs7, signatureName)) {
                throw new SignatureStoreageException(uploadClient.readErrorMessage());
            }
        } finally {
            if (!uploadClient.logoutAndDisconnect()) {
                throw new SignatureStoreageException(uploadClient.readErrorMessage());
            }
        }
        return StringUtils.EMPTY;
    }
}
