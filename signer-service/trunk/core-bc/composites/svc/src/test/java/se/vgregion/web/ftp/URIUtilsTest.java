package se.vgregion.web.ftp;

import static org.junit.Assert.*;

import java.net.URI;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

public class URIUtilsTest {

    private static final String HOST = "example.com";
    private static final int PORT = 1234;
    private static final int DEFAULT_PORT = 21;
    private static final String USER_NAME = "userName";
    private static final String PASSWORD = "password";
    private static final String PATH = "/path/to/some/resource";
    private static final String PROTOCOL = "ftp";

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public final void shouldExtractUserName() throws Exception {
        // Given
        URI uri = new URI(PROTOCOL + "://" + USER_NAME + ":" + PASSWORD + "@" + HOST + ":" + PORT + PATH);

        // When
        String userName = URIUtils.extractUserName(uri);

        // Then
        assertEquals(USER_NAME, userName);
    }

    @Test
    public final void shouldReturnNullWhenNoUserNameInURI() throws Exception {
        // Given
        URI uri = new URI(PROTOCOL + "://" + HOST + ":" + PORT + PATH);

        // When
        String userName = URIUtils.extractUserName(uri);

        // Then
        assertNull(userName);
    }

    @Test
    public final void shouldExtractPassword() throws Exception {
        // Given
        URI uri = new URI(PROTOCOL + "://" + USER_NAME + ":" + PASSWORD + "@" + HOST + ":" + PORT + PATH);

        // When
        String password = URIUtils.extractPassword(uri);

        // Then
        assertEquals(PASSWORD, password);
    }

    @Test
    public final void shouldReturnNullWhenNoPasswordInURI() throws Exception {
        // Given
        URI uri = new URI(PROTOCOL + "://" + HOST + ":" + PORT + PATH);

        // When
        String password = URIUtils.extractPassword(uri);

        // Then
        assertNull(password);
    }

    @Test
    public final void shouldExtractPort() throws Exception {
        // Given
        URI uri = new URI(PROTOCOL + "://" + USER_NAME + ":" + PASSWORD + "@" + HOST + ":" + PORT + PATH);

        // When
        int port = URIUtils.extractPort(uri, DEFAULT_PORT);

        // Then
        assertEquals(PORT, port);
    }

    @Test
    public final void shouldReturnDefaultPortWhenNoPortInURI() throws Exception {
        // Given
        URI uri = new URI(PROTOCOL + "://" + USER_NAME + ":" + PASSWORD + "@" + HOST + PATH);

        // When
        int port = URIUtils.extractPort(uri, DEFAULT_PORT);

        // Then
        assertEquals(DEFAULT_PORT, port);
    }

    @Test
    public final void shouldExtractHost() throws Exception {
        // Given
        URI uri = new URI(PROTOCOL + "://" + USER_NAME + ":" + PASSWORD + "@" + HOST + ":" + PORT + PATH);

        // When
        String host = URIUtils.extractHost(uri);

        // Then
        assertEquals(HOST, host);
    }

    @Test
    public final void shouldReturnNullWhenNoHostInURI() throws Exception {
        // Given
        URI uri = new URI(PROTOCOL + "://" + PATH);

        // When
        String host = URIUtils.extractHost(uri);

        // Then
        assertNull(host);
    }

    @Test
    public final void shouldExtractPath() throws Exception {
        // Given
        URI uri = new URI(PROTOCOL + "://" + USER_NAME + ":" + PASSWORD + "@" + HOST + ":" + PORT + PATH);

        // When
        String path = URIUtils.extractPath(uri);

        // Then
        assertEquals(PATH, path);
    }

    @Test
    public final void shouldReturnEmptyStringWhenNoPathInURI() throws Exception {
        // Given
        URI uri = new URI(PROTOCOL + "://" + USER_NAME + ":" + PASSWORD + "@" + HOST + ":" + PORT);

        // When
        String path = URIUtils.extractPath(uri);

        // Then
        assertEquals(StringUtils.EMPTY, path);
    }

}
