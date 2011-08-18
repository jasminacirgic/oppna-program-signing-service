package se.vgregion.web.security.services;

import static org.junit.Assert.*;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;

public class SignatureDataTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public final void shouldBase64EncodeNonce() {
        // Given
        String nonce = "any_random_string";
        String base64EncodedNonce = Base64.encodeBase64String(nonce.getBytes()).trim();
        SignatureData signData = new SignatureData();
        signData.setNonce(nonce);

        // When
        String actualEncodedNonce = signData.getEncodedNonce();

        // Then
        assertEquals(base64EncodedNonce, actualEncodedNonce);
    }
}
