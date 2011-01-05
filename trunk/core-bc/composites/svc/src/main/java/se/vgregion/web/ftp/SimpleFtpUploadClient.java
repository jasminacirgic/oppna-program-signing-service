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

/**
 * An ftp-client whos only purpose is to provide a simple way for uploading files to an ftp server.
 * 
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 * 
 */
public class SimpleFtpUploadClient implements ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleFtpUploadClient.class);

    private FTPClient ftpClient;
    private URI ftpServerUri;
    private String userName;
    private String password;
    private String path;
    private String errorMessage;

    private ApplicationContext applicationContext;

    private void setupClient() {
        String protocol = ftpServerUri.getScheme().toLowerCase();
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
        userName = URIUtils.extractUserName(ftpServerUri);
        password = URIUtils.extractPassword(ftpServerUri);
        path = URIUtils.extractPath(ftpServerUri);
    }

    /**
     * Connect to an ftp server. Allowed protocols are <code>ftp</code> and <code>ftps</code>. If an error arise an
     * error message can be read from {@link SimpleFtpUploadClient#readErrorMessage()}.
     * 
     * @param uri
     *            a connection-uri to the ftp-server. For example: <code>ftp://user:password@host:port/path</code>
     * 
     * @return true if logout and disconnect is successful, false if there is a problem.
     * 
     * @see SimpleFtpUploadClient#errorMessage
     */
    public boolean connect(URI uri) {
        this.ftpServerUri = uri;
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

    /**
     * Log in to an ftp server. If an error arise an error message can be read from
     * {@link SimpleFtpUploadClient#readErrorMessage()}.
     * 
     * @return true if logout and disconnect is successful, false if there is a problem.
     * 
     * @see SimpleFtpUploadClient#errorMessage
     */
    public boolean login() {
        if (ftpClient == null) {
            throw new IllegalStateException("No ftp-connection is found, create one before trying to login.");
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

    /**
     * Logout and disconnect from existing ftp connection. If an error arise an error message can be read from
     * {@link SimpleFtpUploadClient#readErrorMessage()}.
     * 
     * @return true if logout and disconnect is successful, false if there is a problem.
     * 
     * @see SimpleFtpUploadClient#errorMessage
     */
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

    /**
     * Upload some data to the ftp server. If an error arise an error message can be read from
     * {@link SimpleFtpUploadClient#readErrorMessage()}.
     * 
     * @param data
     *            the data to upload
     * @param fileName
     *            the name of the file the data will be stored under
     * @return true if logout and disconnect is successful, false if there is a problem.
     * 
     * @see SimpleFtpUploadClient#errorMessage
     */
    public boolean upload(InputStream data, String fileName) {
        if (ftpClient == null) {
            throw new IllegalStateException("No ftp-client is found");
        }
        if (!createAndChangeWorkingDirectory() || !uploadFile(data, fileName)) {
            writeErrorMessage("Unable to upload file to: %1$s", ftpServerUri.toString());
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

    /**
     * If an error arise within the ftp-transaction an error messages is exposed through this method. Once the
     * error messages is read it is removed.
     * 
     * @return an error message
     */
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
        errorMessage = new Formatter().format(message, args).toString();
        LOGGER.warn(errorMessage, args);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.
     * ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
