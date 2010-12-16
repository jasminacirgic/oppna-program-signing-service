package se.vgregion.web.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Formatter;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SimpleFtpUploadClient implements ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleFtpUploadClient.class);

    private FTPClient ftpClient;
    private URI uri;
    private String userName;
    private String password;
    private String path;
    private String errorMessage;
    private Formatter formatter = new Formatter();

    private ApplicationContext applicationContext;

    private void setupClient() {
        String protocol = uri.getScheme().toLowerCase();
        if (protocol.equals("ftps")) {
            ftpClient = applicationContext.getBean(protocol + "-client", FTPSClient.class);
        } else if (protocol.equals("ftp")) {
            ftpClient = applicationContext.getBean(protocol + "-client", FTPClient.class);
        } else {
            throw new IllegalArgumentException("Unknown protocol. Allowed protocols are ftps and ftp");
        }
        ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        extractUriInfo();
    }

    protected void extractUriInfo() {
        userName = URIUtils.extractUserName(uri);
        password = URIUtils.extractPassword(uri);
        path = URIUtils.extractPath(uri);
    }

    public boolean connect(URI uri) {
        this.uri = uri;
        setupClient();
        try {
            connectClient(URIUtils.extractHost(uri), URIUtils.extractPort(uri, ftpClient.getDefaultPort()));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            writeErrorMessage(e.getMessage());
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            writeErrorMessage(e.getMessage());
            return false;
        }
        return true;
    }

    private void connectClient(String host, int port) throws IOException {
        try {
            ftpClient.connect(host, port);
        } catch (FTPConnectionClosedException e) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e1) {
                LOGGER.warn("Thread was interrupted while waiting to retry ftp login: ({})", e1.getMessage());
            }
            ftpClient.connect(host, port);
        }
    }

    public boolean login() {
        if (ftpClient == null) {
            throw new IllegalStateException("No ftp-client is found");
        }
        boolean success = true;
        if (ftpClient.isConnected()) {
            try {
                ftpClient.login(userName, password);
                if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                    success = handleLoginError();
                }
            } catch (IOException e) {
                success = handleLoginError();
            }
        } else {
            writeErrorMessage("Unable to login, client is not connected.");
            success = false;
        }
        return success;
    }

    private boolean handleLoginError() {
        writeErrorMessage("FTP server refused login: %1s", ftpClient.getReplyString());
        logoutAndDisconnect();
        return false;
    }

    public boolean logoutAndDisconnect() {
        boolean success = true;
        if (ftpClient == null) {
            LOGGER.debug("No valid ftp client was found when trying to disconnect");
            return true;
        }
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (IOException e) {
            writeErrorMessage("Unable to disconnect from ftp server: ({})", e.getMessage());
        } finally {
            success = !ftpClient.isConnected();
            clearState();
        }
        return success;
    }

    private void clearState() {
        ftpClient = null;
    }

    public boolean upload(InputStream data, String fileName) {
        if (ftpClient == null) {
            throw new IllegalStateException("No ftp-client is found");
        }
        if (!createAndChangeWorkingDirectory() || !uploadFile(data, fileName)) {
            writeErrorMessage("Unable to upload file to: %1$s", uri.toString());
            return false;
        }
        return true;
    }

    private boolean createAndChangeWorkingDirectory() {
        boolean success = true;
        try {
            if (!ftpClient.changeWorkingDirectory(path)) {
                success = ftpClient.makeDirectory(path);
                success = ftpClient.changeWorkingDirectory(path);
            }
        } catch (IOException e) {
            success = false;
            LOGGER.warn("Unable to change working directory to: {}", path);
        }
        return success;
    }

    private boolean uploadFile(InputStream data, String fileName) {
        try {
            return ftpClient.storeFile(fileName + ".p7", data);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e.getCause());
            return false;
        }
    }

    public String readErrorMessage() {
        String message = errorMessage;
        errorMessage = "";
        return message;
    }

    private void writeErrorMessage(String message) {
        errorMessage = message;
        LOGGER.warn(errorMessage);
    }

    private void writeErrorMessage(String message, Object... args) {
        errorMessage = formatter.format(message, args).toString();
        LOGGER.warn(errorMessage, args);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
