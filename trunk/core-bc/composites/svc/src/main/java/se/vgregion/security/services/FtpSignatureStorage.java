package se.vgregion.security.services;

import java.io.IOException;

public class FtpSignatureStorage implements SignatureStorage {
    @Override
    public String save(String submitUri, byte[] pkcs7) throws IOException {
        throw new UnsupportedOperationException("FtpSignatureStorage is not yet implemented");
    }
}
