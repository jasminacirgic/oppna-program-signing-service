package se.vgregion.web.signaturestorage;

import java.io.IOException;
import java.net.URI;

public interface SignatureStorage {
    String submitSignature(URI submitUri, byte[] pkcs7, String signatureName) throws SignatureStoreageException, IOException;
}
