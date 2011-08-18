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

    private KeyStore keyStore = null;
    private String password = "";

    /**
     * Set keyStore, populated via Spring XML property element.
     * 
     * @param keyStore
     *            the keyStore
     */
    public void setKeyStore(KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    /**
     * Set keyStore password, populated via Spring XML property element.
     * 
     * @param password
     *            the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Return the type of object that this FactoryBean creates.
     * 
     * @return the type of object that this FactoryBean creates
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    @Override
    public Class<?> getObjectType() {
        return KeyManager.class;
    }

    /**
     * The method that construct the {@link KeyManager} returned by this factory.
     * <p>
     * Invoked on initialization of this FactoryBean in case of a singleton; else, on each {@link #getObject()}
     * call.
     * 
     * @return the keyManager returned by this factory
     * @throws Exception
     *             if an exception occurred during object creation
     * @see #getObject()
     */
    @Override
    protected KeyManager createInstance() throws Exception {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory
                .getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password.toCharArray());

        return keyManagerFactory.getKeyManagers()[0];
    }

}
