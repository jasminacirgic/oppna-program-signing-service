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
import org.springframework.oxm.Marshaller;

import se.vgregion.proxy.signera.signature.SignatureEnvelope;
import se.vgregion.proxy.signera.signature.SignatureFormat;
import se.vgregion.web.HttpMessageHelper;
import se.vgregion.web.security.services.SignatureEnvelopeFactory;
import se.vgregion.web.signaturestorage.SignatureStoreageException;

public class HttpSignatureStorageTest {
    private static final String SIGNATURE_NAME = "signaturename";
    private static final SignatureFormat SIGNATURE_FORMAT = SignatureFormat.CMS;
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

    private SignatureEnvelope envelope;

    private HttpSignatureStorage signatureStorage;
    @Mock
    private Marshaller marshaller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        signatureStorage = new HttpSignatureStorage(httpClient, httpHelper, marshaller);
        anyUri = new URI(REDIRECT_URI);
        envelope = SignatureEnvelopeFactory.createSignatureEnvelope(SIGNATURE_NAME, SIGNATURE_FORMAT, SIGNATURE);
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
        String actualRedirectUri = signatureStorage.submitSignature(anyUri, envelope);

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
        String actualRedirectUri = signatureStorage.submitSignature(anyUri, envelope);

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
        signatureStorage.submitSignature(anyUri, envelope);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void shouldThrowIllegalArgumentExceptionIfSubmitUriIsNull() throws Exception {
        signatureStorage.submitSignature(null, envelope);
    }

}
