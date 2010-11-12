package se.vgregion.web.ftp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Formatter;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleFtpUploadClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleFtpUploadClient.class);

    private static final int DEFAULT_FTP_PORT = 21;
    private FTPClient ftpClient;
    private URI uri;
    private String userName;
    private String password;
    private int port;
    private String host;
    private String path;
    private String errorMessage;
    private Formatter formatter = new Formatter();

    public SimpleFtpUploadClient(URI uri) {
        ftpClient = new FTPClient();
        this.uri = uri;
        userName = URIUtils.extractUserName(uri);
        password = URIUtils.extractPassword(uri);
        port = URIUtils.extractPort(uri, DEFAULT_FTP_PORT);
        host = URIUtils.extractHost(uri);
        path = URIUtils.extractPath(uri);
    }

    public boolean connectAndLogin() throws IOException {
        try {
            ftpClient.connect(host, port);
        } catch (IOException e) {
            e.printStackTrace();
            writeErrorMessage("FTP server refused connection. Cause: %1$s.", e.getMessage());
            return false;
        }
        ftpClient.login(userName, password);
        int reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            logoutAndDissconnect();
            writeErrorMessage("FTP server refused connection.");
            return false;
        }
        return true;
    }

    public boolean logoutAndDissconnect() {
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
            writeErrorMessage("Unable to disconnect from ftp server %1$s", e.getMessage());
        }
        return !ftpClient.isConnected();
    }

    public boolean upload(byte[] file, String fileName) throws IOException {
        boolean success = ftpClient.changeWorkingDirectory(path);
        if (success) {
            success = ftpClient.storeFile(fileName + ".p7", new ByteArrayInputStream(file));
        }
        if (!success) {
            writeErrorMessage("Unable to upload file to: %1$s", uri.toString());
        }
        return success;
    }

    public String readErrorMessage() {
        String message = errorMessage;
        errorMessage = "";
        return message;
    }

    private void writeErrorMessage(String message) {
        LOGGER.warn(message);
        errorMessage = formatter.format(message).toString();
    }

    private void writeErrorMessage(String message, Object... args) {
        errorMessage = formatter.format(message, args).toString();
    }

}
