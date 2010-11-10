package se.vgregion.security.services;

import java.io.IOException;
import java.net.URL;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class SignatureService implements ApplicationContextAware {
    private SignatureStorage storage = null;

    private ApplicationContext applicationContext;

    public String save(URL submitUrl, byte[] pkcs7) throws IOException {
        setupIOBacker(submitUrl.getProtocol());
        if (storage == null) {
            throw new IllegalStateException("No storage is configured for the specified protocol");
        }

        return storage.save(submitUrl.toString(), pkcs7);

    }

    private void setupIOBacker(String protocol) {
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
