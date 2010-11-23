package se.vgregion.web.ftp;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.security.KeyStore;

import javax.annotation.Resource;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.impl.DefaultFtpServer;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.ssl.SslConfigurationFactory;
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:fake-ftp-server-config.xml")
public class SimpleFtpUploadTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleFtpUploadTest.class);

    private static final File FTPCLIENT_KEYSTORE = new File(getBaseDir(), "src/test/resources/signer-service.jks");
    private static final File FTPSERVER_KEYSTORE = new File(getBaseDir(), "src/test/resources/ftpserver.jks");
    private static final File USERS_FILE = new File(getBaseDir(), "src/test/resources/user.properties");

    protected DefaultFtpServer ftpServer;
    FTPSClient ftpClient;

    @Resource(name = "userName")
    private String validUserName;

    @Resource(name = "password")
    private String validPassword;

    @Resource(name = "port")
    private Integer validPort;

    private String invalidUserName = "invalidpwd";
    private String invalidPassword = "invalidpwd";
    private Integer invalidPort = 7777;

    protected KeyManager clientKeyManager;

    protected TrustManager clientTrustManager;

    @Before
    public void setUp() throws Exception {
        if (LOGGER.isDebugEnabled()) {
            System.setProperty("javax.net.debug", "ssl");
        }
        // cast to internal class to get access to getters
        ftpServer = (DefaultFtpServer) createServer().createServer();
        ftpServer.start();
        connectClient();
    }

    @After
    public void tearDown() throws Exception {
        try {
            ftpClient.disconnect();
        } catch (Exception e) {
            // ignore
        }

        if (ftpServer != null) {
            ftpServer.stop();
        }
    }

    public static File getBaseDir() {
        // check Maven system prop first and use if set
        String basedir = System.getProperty("basedir");
        if (basedir != null) {
            return new File(basedir);
        } else {
            return new File(".");
        }
    }

    protected SslConfigurationFactory createSslConfiguration() {
        SslConfigurationFactory sslConfigFactory = new SslConfigurationFactory();
        sslConfigFactory.setKeystoreFile(FTPSERVER_KEYSTORE);
        sslConfigFactory.setKeystorePassword(validPassword);
        sslConfigFactory.setSslProtocol("TLS");
        sslConfigFactory.setClientAuthentication("true");
        sslConfigFactory.setKeyPassword(validPassword);

        return sslConfigFactory;
    }

    protected FtpServerFactory createServer() throws Exception {
        assertTrue(USERS_FILE.getAbsolutePath() + " must exist", USERS_FILE.exists());

        FtpServerFactory serverFactory = new FtpServerFactory();
        serverFactory.setConnectionConfig(new ConnectionConfigFactory().createConnectionConfig());

        ListenerFactory listenerFactory = new ListenerFactory();
        listenerFactory.setPort(2222);
        listenerFactory.setDataConnectionConfiguration(new DataConnectionConfigurationFactory()
                .createDataConnectionConfiguration());
        listenerFactory.setSslConfiguration(createSslConfiguration().createSslConfiguration());
        listenerFactory.setImplicitSsl(false);

        serverFactory.addListener("default", listenerFactory.createListener());

        PropertiesUserManagerFactory umFactory = new PropertiesUserManagerFactory();
        umFactory.setAdminName("admin");
        umFactory.setPasswordEncryptor(new ClearTextPasswordEncryptor());
        umFactory.setFile(USERS_FILE);

        serverFactory.setUserManager(umFactory.createUserManager());

        return serverFactory;
    }

    protected void connectClient() throws Exception {
        ftpClient = createFTPClient();
        ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        doConnect();
    }

    protected void doConnect() throws Exception {
        try {
            ftpClient.connect("localhost", 2222);
        } catch (FTPConnectionClosedException e) {
            // try again
            Thread.sleep(200);
            ftpClient.connect("localhost", 2222);
        }
    }

    protected FTPSClient createFTPClient() throws Exception {
        ftpClient = new FTPSClient();

        FileInputStream fin = new FileInputStream(FTPCLIENT_KEYSTORE);
        KeyStore store = KeyStore.getInstance("jks");
        store.load(fin, validPassword.toCharArray());
        fin.close();

        // initialize key manager factory
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory
                .getDefaultAlgorithm());
        keyManagerFactory.init(store, validPassword.toCharArray());

        clientKeyManager = keyManagerFactory.getKeyManagers()[0];

        ftpClient.setKeyManager(clientKeyManager);
        ftpClient.setAuthValue("TLS");
        return ftpClient;
    }

    @Test
    public void shouldConnectAndLoginToSecureFtpServer() throws Exception {
        ftpClient.login("admin", "admin");
        ftpClient.enterLocalActiveMode();
        ftpClient.execPROT("P");
        ftpClient.noop();
        ftpClient.execPROT("C");
        ftpClient.logout();
        // boolean success = ftpClient.storeFile("xyz.p7",
        // new ByteArrayInputStream("anders testar en fil på ftp".getBytes()));
        // if (!success) {
        // LOGGER.error("Unable to upload file");
        // }
        // System.out.println("STATUS::::: " + ftpClient.stat());
        // URI ftpUri = new URI("ftps://" + validUserName + ":" + validPassword + "@localhost:" + validPort + "/");
        // SimpleFtpUploadClient ftpClient = new SimpleFtpUploadClient(ftpUri);
        // try {
        // assertTrue(ftpClient.connectAndLogin());
        // } finally {
        // ftpClient.logoutAndDissconnect();
        // }
    }

    @Test
    @Ignore
    public void shouldConnectAndLoginToFtpServer() throws Exception {
        URI ftpUri = new URI("ftp://" + validUserName + ":" + validPassword + "@localhost:" + validPort + "/");
        SimpleFtpUploadClient ftpClient = new SimpleFtpUploadClient(ftpUri);
        try {
            assertTrue(ftpClient.connectAndLogin());
        } finally {
            ftpClient.logoutAndDissconnect();
        }
    }

    @Test
    @Ignore
    public void shouldNotConnectToFtpServerWithInvalidUserName() throws Exception {
        URI ftpUri = new URI("ftp://" + invalidUserName + ":" + validPassword + "@localhost:" + validPort + "/");
        SimpleFtpUploadClient ftpClient = new SimpleFtpUploadClient(ftpUri);
        try {
            assertFalse(ftpClient.connectAndLogin());
            assertEquals("FTP server refused connection.", ftpClient.readErrorMessage());
        } finally {
            ftpClient.logoutAndDissconnect();
        }
    }

    @Test
    @Ignore
    public void shouldNotConnectToFtpServerWithInvalidPassword() throws Exception {
        URI ftpUri = new URI("ftp://" + validUserName + ":" + invalidPassword + "@localhost:" + validPort + "/");
        SimpleFtpUploadClient ftpClient = new SimpleFtpUploadClient(ftpUri);
        try {
            assertFalse(ftpClient.connectAndLogin());
            assertEquals("FTP server refused connection.", ftpClient.readErrorMessage());
        } finally {
            ftpClient.logoutAndDissconnect();
        }
    }

    @Test
    @Ignore
    public void shouldNotConnectToFtpServerWithInvalidPort() throws Exception {
        URI ftpUri = new URI("ftp://" + validUserName + ":" + validPassword + "@invalidhost:" + validPort + "/");
        SimpleFtpUploadClient ftpClient = new SimpleFtpUploadClient(ftpUri);
        try {
            assertFalse(ftpClient.connectAndLogin());
            String eMsg = ftpClient.readErrorMessage();
            System.out.println(eMsg);
            assertTrue(eMsg.contains("FTP server refused connection"));
        } finally {
            ftpClient.logoutAndDissconnect();
        }
    }

    @Test
    @Ignore
    public void shouldNotConnectToFtpServerWithInvalidHost() throws Exception {
        URI ftpUri = new URI("ftp://" + validUserName + ":" + validPassword + "@localhost:" + invalidPort + "/");
        SimpleFtpUploadClient ftpClient = new SimpleFtpUploadClient(ftpUri);
        try {
            assertFalse(ftpClient.connectAndLogin());
            String eMsg = ftpClient.readErrorMessage();
            System.out.println(eMsg);
            assertTrue(eMsg.contains("FTP server refused connection"));
        } finally {
            ftpClient.logoutAndDissconnect();
        }
    }

    @Test
    @Ignore
    public void shouldDissconnectFromFtpServer() throws Exception {
        URI ftpUri = new URI("ftp://" + validUserName + ":" + validPassword + "@localhost:" + validPort + "/");
        SimpleFtpUploadClient ftpClient = new SimpleFtpUploadClient(ftpUri);
        try {
            ftpClient.connectAndLogin();
        } finally {
            assertTrue(ftpClient.logoutAndDissconnect());
        }
    }

    @Test
    @Ignore
    public void shouldUploadFileToServer() throws Exception {
        String path = "signatures";
        String fileName = "test";
        URI ftpUri = new URI("ftp://" + validUserName + ":" + validPassword + "@localhost:" + validPort + "/"
                + path);
        SimpleFtpUploadClient ftpClient = new SimpleFtpUploadClient(ftpUri);
        final byte[] byteArrayToSave = "This String is 76 characters long and will be converted to an array of bytes"
                .getBytes();

        try {
            ftpClient.connectAndLogin();
            ftpClient.upload(byteArrayToSave, fileName);

            // FileEntry file = (FileEntry) ftpServer.getFileSystem().getEntry(
            // "/" + path + "/" + fileName + ".p7");
            // InputStream savedFile = file.createInputStream();

            // assertArrayEquals(byteArrayToSave, IoUtil.readBytes(savedFile));
        } finally {
            assertTrue(ftpClient.logoutAndDissconnect());
        }
    }
}
