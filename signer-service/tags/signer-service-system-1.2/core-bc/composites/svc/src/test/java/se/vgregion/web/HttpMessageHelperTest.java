package se.vgregion.web;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.HttpStatus.*;

import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

public class HttpMessageHelperTest {
    private static final String ANY_URI = "http://example.com";

    @Mock
    private HttpResponse httpResponse;
    @Mock
    private StatusLine statusLine;
    @Mock
    private Header header;

    private HttpMessageHelper httpHelper = new HttpMessageHelper();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        given(httpResponse.getStatusLine()).willReturn(statusLine);
    }

    @Test
    public final void shouldReturnTrueIsResponseStatusIsSuccessful() throws Exception {
        // Given
        given(statusLine.getStatusCode()).willReturn(OK.value()); // Status 200
        // When
        boolean success = httpHelper.successStatus(httpResponse);
        // Then
        assertTrue(success);
    }

    @Test
    public final void shouldReturnFalseIsResponseStatusIsNotSuccessful() throws Exception {
        // Given
        given(statusLine.getStatusCode()).willReturn(BAD_REQUEST.value()); // Status 400
        // When
        boolean success = httpHelper.successStatus(httpResponse);
        // Then
        assertFalse(success);

        // Given
        given(statusLine.getStatusCode()).willReturn(MULTIPLE_CHOICES.value()); // Status 300
        // When
        success = httpHelper.successStatus(httpResponse);
        // Then
        assertFalse(success);
    }

    @Test
    public final void shouldReturnFalseIsResponseStatusIsInvalid() throws Exception {
        // Given
        given(statusLine.getStatusCode()).willThrow(new IllegalArgumentException());
        // When
        boolean success = httpHelper.successStatus(httpResponse);
        // Then
        assertFalse(success);
    }

    @Test
    public final void shouldCreateANewHttpEntity() throws Exception {
        HttpEntity entity = httpHelper.createEntity("entity string");
        assertNotNull(entity);
    }

    @Test
    public final void shouldCreateANewHttpPost() throws Exception {
        HttpPost post = httpHelper.createHttpPostMethod(new URI(ANY_URI));
        assertNotNull(post);
    }

    @Test
    public final void shouldReturnResponseStatusCode() throws Exception {
        // Given
        HttpStatus expectedStatus = BAD_REQUEST;
        given(statusLine.getStatusCode()).willReturn(expectedStatus.value());

        // When
        HttpStatus status = httpHelper.getResponseStatusCode(httpResponse);

        // Then
        assertEquals(expectedStatus, status);
    }

    @Test
    public final void shouldReturnLocationHeader() throws Exception {
        // Given
        given(httpResponse.getFirstHeader("Location")).willReturn(header);
        given(header.getValue()).willReturn(ANY_URI);

        // When
        String location = httpHelper.getLocationHeader(httpResponse);

        // Then
        assertEquals(ANY_URI, location);
    }

}
