package se.vgregion.web.ftp;

import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * Simple factory for shared {@link TrustManager} instances. Allows for central setup of TrustManagers in Spring XML
 * bean definitions.
 * 
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 * 
 */
public class TrustManagerFactoryBean extends AbstractFactoryBean<TrustManager> {

    private KeyStore keyStore = null;

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
    protected TrustManager createInstance() throws Exception {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory
                .getDefaultAlgorithm());

        trustManagerFactory.init(keyStore);

        return trustManagerFactory.getTrustManagers()[0];
    }

}
