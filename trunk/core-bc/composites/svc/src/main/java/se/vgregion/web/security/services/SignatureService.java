package se.vgregion.web.security.services;

import java.io.IOException;
import java.net.URI;
import java.security.SignatureException;
import java.util.UUID;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import se.vgregion.web.signaturestorage.SignatureStorage;
import se.vgregion.web.signaturestorage.SignatureStoreageException;

@Service
public class SignatureService implements ApplicationContextAware {
    private SignatureStorage storage = null;

    private ApplicationContext applicationContext;

    public String save(URI submitUrl, byte[] pkcs7) throws SignatureException {
        return save(submitUrl, pkcs7, UUID.nameUUIDFromBytes(pkcs7).toString());
    }

    public String save(URI submitUrl, byte[] pkcs7, String signatureName) throws SignatureException {
        setupIOBackend(submitUrl.getScheme());
        if (storage == null) {
            throw new SignatureException(new IllegalStateException(
                    "No storage is configured for the specified protocol"));
        }
        String forwardString = null;
        try {
            forwardString = storage.save(submitUrl, pkcs7, signatureName);
        } catch (SignatureStoreageException e) {
            throw new SignatureException(e.getMessage(), e);
        } catch (IOException e) {
            throw new SignatureException(e.getMessage(), e);
        }
        return forwardString;
    }

    private void setupIOBackend(String protocol) {
        String beanName = protocol + "-signature-storage";
        if (applicationContext.containsBean(beanName)) {
            storage = (SignatureStorage) applicationContext.getBean(beanName);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
