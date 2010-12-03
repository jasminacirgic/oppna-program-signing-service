package se.vgregion.web.ftp;

import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;

import org.springframework.beans.factory.config.AbstractFactoryBean;

public class KeyManagerFactoryBean extends AbstractFactoryBean<KeyManager> {

    private KeyStore keyStore;
    private String password;

    public void setKeyStore(KeyStore keyStore) {
        this.keyStore = keyStore;
    }

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
        System.out.println(keyStore + " -> " + password);
        keyManagerFactory.init(keyStore, password.toCharArray());

        return keyManagerFactory.getKeyManagers()[0];
    }

}
