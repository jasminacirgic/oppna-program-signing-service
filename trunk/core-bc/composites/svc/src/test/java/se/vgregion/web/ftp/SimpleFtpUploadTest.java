package se.vgregion.web.ftp;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.net.URI;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockftpserver.core.util.IoUtil;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:fake-ftp-server-config.xml")
public class SimpleFtpUploadTest {
    @Autowired
    private FakeFtpServer fakeFtpServer;

    @Resource(name = "userName")
    private String validUserName;

    @Resource(name = "password")
    private String validPassword;

    @Resource(name = "port")
    private String validPort;

    private String invalidUserName = "invalidpwd";
    private String invalidPassword = "invalidpwd";
    private String invalidPort = "7777";

    @Before
    public void setUpBeforeClass() throws Exception {
        fakeFtpServer.start();
    }

    @After
    public void tearDownAfterClass() throws Exception {
        fakeFtpServer.stop();
    }

    @Test
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

            FileEntry file = (FileEntry) fakeFtpServer.getFileSystem().getEntry(
                    "/" + path + "/" + fileName + ".p7");
            InputStream savedFile = file.createInputStream();

            assertArrayEquals(byteArrayToSave, IoUtil.readBytes(savedFile));
        } finally {
            assertTrue(ftpClient.logoutAndDissconnect());
        }
    }
}
