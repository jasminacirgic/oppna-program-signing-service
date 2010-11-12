package se.vgregion.web.signaturestorage.impl;

import java.io.IOException;
import java.net.URI;

import org.apache.commons.lang.StringUtils;

import se.vgregion.web.ftp.SimpleFtpUploadClient;
import se.vgregion.web.signaturestorage.SignatureStorage;
import se.vgregion.web.signaturestorage.SignatureStoreageException;

public class FtpSignatureStorage implements SignatureStorage {
    @Override
    public String save(URI submitUri, byte[] pkcs7, String signatureName) throws SignatureStoreageException {
        SimpleFtpUploadClient uploadClient = new SimpleFtpUploadClient(submitUri);
        try {
            if (!uploadClient.connectAndLogin()) {
                throw new SignatureStoreageException(uploadClient.readErrorMessage());
            }
            if (!uploadClient.upload(pkcs7, signatureName)) {
                throw new SignatureStoreageException(uploadClient.readErrorMessage());
            }
        } catch (IOException e) {
            throw new SignatureStoreageException(e);
        } finally {
            if (!uploadClient.logoutAndDissconnect()) {
                throw new SignatureStoreageException("Unable to disconnect from FTP connection.");
            }
        }
        return StringUtils.EMPTY;
    }

}
