package se.vgregion.web.signaturestorage.impl;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Matchers.*;

import java.io.InputStream;
import java.net.URI;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import se.vgregion.web.ftp.SimpleFtpUploadClient;
import se.vgregion.web.signaturestorage.SignatureStoreageException;

public class FtpSignatureStorageTest {

    private static final String SIGNATURE_NAME = "signaturename";
    private static final String SIGNATURE = "signature";
    @Mock
    private SimpleFtpUploadClient uploadClient;
    private FtpSignatureStorage signatureStorage;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        signatureStorage = new FtpSignatureStorage(uploadClient);
    }

    @Test
    public final void shouldReturnEmptyStringIfSubmitIsSuccessful() throws Exception {
        // Given
        given(uploadClient.connect(any(URI.class))).willReturn(true);
        given(uploadClient.login()).willReturn(true);
        given(uploadClient.upload(any(InputStream.class), anyString())).willReturn(true);
        given(uploadClient.logoutAndDisconnect()).willReturn(true);

        // When
        String actual = signatureStorage.submitSignature(null, SIGNATURE, SIGNATURE_NAME);

        // Then
        assertEquals(StringUtils.EMPTY, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void shouldThrowIllegalArgumentExceptionIfSignatureIsNull() throws Exception {
        signatureStorage.submitSignature(null, null, SIGNATURE_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void shouldThrowIllegalArgumentExceptionIfSignatureIsEmpty() throws Exception {
        signatureStorage.submitSignature(null, "", SIGNATURE_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void shouldThrowIllegalArgumentExceptionIfSignatureNameIsNull() throws Exception {
        signatureStorage.submitSignature(null, SIGNATURE, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void shouldThrowIllegalArgumentExceptionIfSignatureNameIsEmpty() throws Exception {
        signatureStorage.submitSignature(null, SIGNATURE, "");
    }

    @Test(expected = SignatureStoreageException.class)
    public final void shouldThrowSignatureStoreageExceptionIfConnectionFails() throws Exception {
        // Given
        given(uploadClient.connect(any(URI.class))).willReturn(false);
        given(uploadClient.readErrorMessage()).willReturn("");
        given(uploadClient.logoutAndDisconnect()).willReturn(true);

        // When
        signatureStorage.submitSignature(null, SIGNATURE, SIGNATURE_NAME);
    }

    @Test(expected = SignatureStoreageException.class)
    public final void shouldThrowSignatureStoreageExceptionIfLoginFails() throws Exception {
        // Given
        given(uploadClient.connect(any(URI.class))).willReturn(true);
        given(uploadClient.login()).willReturn(false);
        given(uploadClient.readErrorMessage()).willReturn("");
        given(uploadClient.logoutAndDisconnect()).willReturn(true);

        // When
        signatureStorage.submitSignature(null, SIGNATURE, SIGNATURE_NAME);
    }

    @Test(expected = SignatureStoreageException.class)
    public final void shouldThrowSignatureStoreageExceptionIfUploadFails() throws Exception {
        // Given
        given(uploadClient.connect(any(URI.class))).willReturn(true);
        given(uploadClient.login()).willReturn(true);
        given(uploadClient.upload(any(InputStream.class), anyString())).willReturn(false);
        given(uploadClient.readErrorMessage()).willReturn("");
        given(uploadClient.logoutAndDisconnect()).willReturn(true);

        // When
        signatureStorage.submitSignature(null, SIGNATURE, SIGNATURE_NAME);
    }

    @Test(expected = SignatureStoreageException.class)
    public final void shouldThrowSignatureStoreageExceptionIfLogoutAndDisconnectFails() throws Exception {
        // Given
        given(uploadClient.connect(any(URI.class))).willReturn(true);
        given(uploadClient.login()).willReturn(true);
        given(uploadClient.upload(any(InputStream.class), anyString())).willReturn(true);
        given(uploadClient.logoutAndDisconnect()).willReturn(false);

        // When
        signatureStorage.submitSignature(null, SIGNATURE, SIGNATURE_NAME);
    }
}
