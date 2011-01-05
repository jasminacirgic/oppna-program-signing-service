package se.vgregion.web.signaturestorage.impl;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Matchers.*;
import static org.springframework.http.HttpStatus.*;

import java.net.URI;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import se.vgregion.web.HttpMessageHelper;
import se.vgregion.web.signaturestorage.SignatureStoreageException;

public class HttpSignatureStorageTest {
    private static final String SIGNATURE_NAME = "signaturename";
    private static final String SIGNATURE = "signature";
    private static final String REDIRECT_URI = "http://example.com";
    private static URI anyUri;

    @Mock
    private HttpMessageHelper httpHelper;
    @Mock
    private HttpClient httpClient;
    @Mock
    private HttpPost httpPost;
    @Mock
    private HttpResponse httpResponse;

    private HttpSignatureStorage signatureStorage;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        signatureStorage = new HttpSignatureStorage(httpClient, httpHelper);
        anyUri = new URI(REDIRECT_URI);
    }

    @Test
    public final void shouldReturnRedirectUriIfSubmitResponseIssuesAnRedirect() throws Exception {
        // Given
        given(httpHelper.createHttpPostMethod(any(URI.class))).willReturn(httpPost);
        given(httpHelper.createEntity(anyString())).willReturn(null);
        given(httpClient.execute(any(HttpPost.class))).willReturn(httpResponse);
        given(httpHelper.getLocationHeader(any(HttpResponse.class))).willReturn(REDIRECT_URI);
        given(httpHelper.getResponseStatusCode(any(HttpResponse.class))).willReturn(MOVED_TEMPORARILY);

        // When
        String actualRedirectUri = signatureStorage.submitSignature(anyUri, SIGNATURE, SIGNATURE_NAME);

        // Then
        assertEquals(REDIRECT_URI, actualRedirectUri);
    }

    @Test
    public final void shouldReturnEmptyStringIfSubmitResponseIsSuccessful() throws Exception {
        // Given
        given(httpHelper.createHttpPostMethod(any(URI.class))).willReturn(httpPost);
        given(httpHelper.createEntity(anyString())).willReturn(null);
        given(httpClient.execute(any(HttpPost.class))).willReturn(httpResponse);
        given(httpHelper.getLocationHeader(any(HttpResponse.class))).willReturn(REDIRECT_URI);
        given(httpHelper.getResponseStatusCode(any(HttpResponse.class))).willReturn(OK);
        // When
        String actualRedirectUri = signatureStorage.submitSignature(anyUri, SIGNATURE, SIGNATURE_NAME);

        // Then
        assertEquals(StringUtils.EMPTY, actualRedirectUri);
    }

    @Test(expected = SignatureStoreageException.class)
    public final void shouldThrowSignatureStoreageExceptionIfSubmitFails() throws Exception {
        // Given
        given(httpHelper.createHttpPostMethod(any(URI.class))).willReturn(httpPost);
        given(httpHelper.createEntity(anyString())).willReturn(null);
        given(httpClient.execute(any(HttpPost.class))).willReturn(httpResponse);
        given(httpHelper.getLocationHeader(any(HttpResponse.class))).willReturn(REDIRECT_URI);
        given(httpHelper.getResponseStatusCode(any(HttpResponse.class))).willReturn(BAD_REQUEST);

        // When
        signatureStorage.submitSignature(anyUri, SIGNATURE, SIGNATURE_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void shouldThrowIllegalArgumentExceptionIfSignatureIsNull() throws Exception {
        signatureStorage.submitSignature(anyUri, null, SIGNATURE_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void shouldThrowIllegalArgumentExceptionIfSignatureIsEmpty() throws Exception {
        signatureStorage.submitSignature(anyUri, "", SIGNATURE_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void shouldThrowIllegalArgumentExceptionIfSignatureNameIsNull() throws Exception {
        signatureStorage.submitSignature(anyUri, SIGNATURE, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void shouldThrowIllegalArgumentExceptionIfSignatureNameIsEmpty() throws Exception {
        signatureStorage.submitSignature(anyUri, SIGNATURE, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public final void shouldThrowIllegalArgumentExceptionIfSubmitUriIsNull() throws Exception {
        signatureStorage.submitSignature(null, SIGNATURE, SIGNATURE_NAME);
    }

}
