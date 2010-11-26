package se.vgregion.web.signaturestorage;

import java.net.URI;

public interface SignatureStorage {
    String save(URI submitUri, byte[] pkcs7, String signatureName) throws SignatureStoreageException;
}
