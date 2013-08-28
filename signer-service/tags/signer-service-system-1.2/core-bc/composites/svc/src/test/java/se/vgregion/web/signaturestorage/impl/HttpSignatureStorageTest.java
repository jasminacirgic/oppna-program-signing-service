package se.vgregion.web.signaturestorage.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.params.HttpParams;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.oxm.Marshaller;
import se.vgregion.signera.signature._1.SignatureEnvelope;
import se.vgregion.signera.signature._1.SignatureFormat;
import se.vgregion.web.HttpMessageHelper;
import se.vgregion.web.security.services.SignatureEnvelopeFactory;
import se.vgregion.web.signaturestorage.SignatureStoreageException;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.HttpStatus.*;

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
    @Mock
    private HttpParams httpParams;

    private SignatureEnvelope envelope;

    private HttpSignatureStorage signatureStorage;
    @Mock
    private Marshaller marshaller;

    static {
        try {
            anyUri = new URI(REDIRECT_URI);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        signatureStorage = new HttpSignatureStorage(httpClient, httpHelper, marshaller);
        envelope = SignatureEnvelopeFactory.createSignatureEnvelope(SIGNATURE_NAME, SIGNATURE_FORMAT, SIGNATURE);
        given(httpPost.getParams()).willReturn(httpParams);
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
        given(httpResponse.getStatusLine()).willReturn(new BasicStatusLine(new HttpVersion(1, 1), BAD_REQUEST.value()
                , "Bad since..."));

        // When
        signatureStorage.submitSignature(anyUri, envelope);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void shouldThrowIllegalArgumentExceptionIfSubmitUriIsNull() throws Exception {
        signatureStorage.submitSignature(null, envelope);
    }

}
