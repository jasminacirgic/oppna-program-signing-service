package se.vgregion.web.ftp;

import static org.junit.Assert.*;

import java.net.URI;

import org.apache.ftpserver.FtpServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:fake-ftp-server-config.xml")
public class SimpleFtpUploadTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleFtpUploadTest.class);

    private static FtpServer ftpServer;

    @Autowired
    private SimpleFtpUploadClient ftpClient;

    private String validUserName = "admin";
    private String validPassword = "admin";
    private Integer validPort = 2222;

    private String invalidUserName = "invalidpwd";
    private String invalidPassword = "invalidpwd";
    private Integer invalidPort = 7777;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        if (LOGGER.isDebugEnabled()) {
            System.setProperty("javax.net.debug", "ssl");
        }
        ftpServer = MyFtpServerFactory.createServer().createServer();
        ftpServer.start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        ftpServer.stop();
    }

    @Test
    public void shouldConnectAndLoginToSecureFtpServer() throws Exception {
        connectToFtpServer(new URI("ftps://" + validUserName + ":" + validPassword + "@localhost:" + validPort
                + "/"));
    }

    @Test
    public void shouldConnectAndLoginToFtpServer() throws Exception {
        connectToFtpServer(new URI("ftp://" + validUserName + ":" + validPassword + "@localhost:" + validPort
                + "/"));
    }

    @Test
    public void shouldNotLoginToSecureFtpServerWithInvalidUserName() throws Exception {
        String errorMsg = failLoginToFtpServer(new URI("ftps://" + invalidUserName + ":" + validPassword
                + "@localhost:" + validPort + "/"));
        assertTrue(errorMsg.contains("FTP server refused login"));
    }

    @Test
    public void shouldNotLoginToFtpServerWithInvalidUserName() throws Exception {
        String errorMsg = failLoginToFtpServer(new URI("ftp://" + invalidUserName + ":" + validPassword
                + "@localhost:" + validPort + "/"));
        assertTrue(errorMsg.contains("FTP server refused login"));
    }

    @Test
    public void shouldNotConnectToSecureFtpServerWithInvalidPassword() throws Exception {
        String errorMsg = failLoginToFtpServer(new URI("ftps://" + validUserName + ":" + invalidPassword
                + "@localhost:" + validPort + "/"));
        assertTrue(errorMsg.contains("FTP server refused login"));
    }

    @Test
    public void shouldNotConnectToFtpServerWithInvalidPassword() throws Exception {
        String errorMsg = failLoginToFtpServer(new URI("ftp://" + validUserName + ":" + invalidPassword
                + "@localhost:" + validPort + "/"));
        assertTrue(errorMsg.contains("FTP server refused login"));
    }

    @Test
    public void shouldNotConnectToSecureFtpServerWithInvalidHost() throws Exception {
        String errorMsg = failConnectToFtpServer(new URI("ftps://" + validUserName + ":" + validPassword
                + "@invalidhost:" + validPort + "/"));
        assertEquals("invalidhost", errorMsg);
    }

    @Test
    public void shouldNotConnectToFtpServerWithInvalidHost() throws Exception {
        String errorMsg = failConnectToFtpServer(new URI("ftps://" + validUserName + ":" + validPassword
                + "@invalidhost:" + validPort + "/"));
        assertEquals("invalidhost", errorMsg);
    }

    @Test
    public void shouldNotConnectToSecureFtpServerWithInvalidPort() throws Exception {
        String errorMsg = failConnectToFtpServer(new URI("ftps://" + validUserName + ":" + validPassword
                + "@localhost:" + invalidPort + "/"));
        assertEquals("Connection refused", errorMsg);
    }

    @Test
    public void shouldNotConnectToFtpServerWithInvalidPort() throws Exception {
        String errorMsg = failConnectToFtpServer(new URI("ftp://" + validUserName + ":" + validPassword
                + "@localhost:" + invalidPort + "/"));
        assertEquals("Connection refused", errorMsg);
    }

    @Test
    public void shouldDissconnectFromFtpServer() throws Exception {
        URI ftpUri = new URI("ftp://" + validUserName + ":" + validPassword + "@localhost:" + validPort + "/");
        try {
            assertTrue(ftpClient.connect(ftpUri));
            assertTrue(ftpClient.login());
        } finally {
            assertTrue(ftpClient.logoutAndDisconnect());
        }
    }

    @Test
    public void shouldUploadFileToServer() throws Exception {
        uploadFileToServer("ftp");
    }

    @Test
    public void shouldUploadFileToSecureServer() throws Exception {
        uploadFileToServer("ftps");
    }

    private void uploadFileToServer(String protocol) throws Exception {
        String path = "signatures";
        String fileName = "test";
        URI ftpUri = new URI(protocol + "://" + validUserName + ":" + validPassword + "@localhost:" + validPort
                + "/" + path);
        final byte[] byteArrayToSave = "This String is 76 characters long and will be converted to an array of bytes"
                .getBytes();

        try {
            assertTrue(ftpClient.connect(ftpUri));
            assertTrue(ftpClient.login());
            assertTrue(ftpClient.upload(byteArrayToSave, fileName));

            // FileEntry file = (FileEntry) ftpServer.getFileSystem().getEntry(
            // "/" + path + "/" + fileName + ".p7");
            // InputStream savedFile = file.createInputStream();

            // assertArrayEquals(byteArrayToSave, IoUtil.readBytes(savedFile));
        } finally {
            assertTrue(ftpClient.logoutAndDisconnect());
        }
    }

    private void connectToFtpServer(URI ftpUri) throws Exception {
        try {
            assertTrue(ftpClient.connect(ftpUri));
            assertTrue(ftpClient.login());
        } finally {
            ftpClient.logoutAndDisconnect();
        }
    }

    private String failConnectToFtpServer(URI ftpUri) throws Exception {
        String errorMsg = null;
        try {
            assertFalse(ftpClient.connect(ftpUri));
            errorMsg = ftpClient.readErrorMessage();
        } finally {
            ftpClient.logoutAndDisconnect();
        }
        return errorMsg;
    }

    private String failLoginToFtpServer(URI ftpUri) throws Exception {
        String errorMsg = null;
        try {
            assertTrue(ftpClient.connect(ftpUri));
            assertFalse(ftpClient.login());
            errorMsg = ftpClient.readErrorMessage();
        } finally {
            ftpClient.logoutAndDisconnect();
        }
        return errorMsg;
    }
}
