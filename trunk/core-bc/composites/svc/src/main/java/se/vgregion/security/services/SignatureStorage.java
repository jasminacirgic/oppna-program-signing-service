package se.vgregion.security.services;

import java.io.IOException;

public interface SignatureStorage {
    String save(String submitUri, byte[] pkcs7) throws IOException;
}
