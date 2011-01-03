package se.vgregion.web.ftp;

import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;

import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * Simple factory for shared {@link KeyManager} instances. Allows for central setup of KeyManagers in Spring XML
 * bean definitions.
 * 
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 * 
 */
public class KeyManagerFactoryBean extends AbstractFactoryBean<KeyManager> {

    private KeyStore keyStore;
    private String password;

    /**
     * Set keyStore, populated via Spring XML property element.
     */
    public void setKeyStore(KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    /**
     * Set keyStore password, populated via Spring XML property element.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Class<?> getObjectType() {
        return KeyManager.class;
    }

    @Override
    protected KeyManager createInstance() throws Exception {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory
                .getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password.toCharArray());

        return keyManagerFactory.getKeyManagers()[0];
    }

}
