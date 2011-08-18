package se.vgregion.web.ftp;

import static junit.framework.Assert.*;

import java.io.File;

import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.ssl.SslConfigurationFactory;
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;

public final class FtpServerFactoryHelper {
    private static final File FTPSERVER_KEYSTORE = new File(getBaseDir(), "src/test/resources/ftpserver.jks");
    private static final File USERS_FILE = new File(getBaseDir(), "src/test/resources/user.properties");

    private FtpServerFactoryHelper() {
    }

    private static File getBaseDir() {
        // check Maven system prop first and use if set
        String basedir = System.getProperty("basedir");
        if (basedir != null) {
            return new File(basedir);
        } else {
            return new File(".");
        }
    }

    private static SslConfigurationFactory createSslConfiguration() {
        SslConfigurationFactory sslConfigFactory = new SslConfigurationFactory();
        sslConfigFactory.setKeystoreFile(FTPSERVER_KEYSTORE);
        sslConfigFactory.setKeystorePassword("password");
        sslConfigFactory.setSslProtocol("TLS");
        sslConfigFactory.setClientAuthentication("true");
        sslConfigFactory.setKeyPassword("password");

        return sslConfigFactory;
    }

    public static FtpServerFactory createServer() throws Exception {
        assertTrue(USERS_FILE.getAbsolutePath() + " must exist", USERS_FILE.exists());

        FtpServerFactory serverFactory = new FtpServerFactory();
        serverFactory.setConnectionConfig(new ConnectionConfigFactory().createConnectionConfig());

        ListenerFactory listenerFactory = new ListenerFactory();
        listenerFactory.setPort(2222);
        listenerFactory.setDataConnectionConfiguration(new DataConnectionConfigurationFactory()
                .createDataConnectionConfiguration());
        listenerFactory.setSslConfiguration(createSslConfiguration().createSslConfiguration());
        listenerFactory.setImplicitSsl(false);
        serverFactory.addListener("secure", listenerFactory.createListener());

        listenerFactory = new ListenerFactory();
        listenerFactory.setPort(2221);
        listenerFactory.setDataConnectionConfiguration(new DataConnectionConfigurationFactory()
                .createDataConnectionConfiguration());
        serverFactory.addListener("default", listenerFactory.createListener());

        PropertiesUserManagerFactory umFactory = new PropertiesUserManagerFactory();
        umFactory.setAdminName("admin");
        umFactory.setPasswordEncryptor(new ClearTextPasswordEncryptor());
        umFactory.setFile(USERS_FILE);

        serverFactory.setUserManager(umFactory.createUserManager());

        return serverFactory;
    }
}
