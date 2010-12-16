package se.vgregion.web.signaturestorage;

import java.io.IOException;
import java.net.URI;

public interface SignatureStorage {
    String submitSignature(URI submitUri, String signature, String signatureName) throws SignatureStoreageException,
            IOException;
}
