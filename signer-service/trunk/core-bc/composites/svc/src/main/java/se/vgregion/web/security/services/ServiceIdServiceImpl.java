package se.vgregion.web.security.services;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * @author Patrik Bergström
 */
@Service
public class ServiceIdServiceImpl implements ServiceIdService {

    private File propertyFile;

    public ServiceIdServiceImpl(String fileName) throws IOException {
        propertyFile = new File(fileName);
        if (!propertyFile.exists()) {
            propertyFile.createNewFile();
        }
    }

    @Override
    public boolean containsServiceId(String serviceId) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getAppName(String serviceId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void storeServiceId(String serviceId, String appName) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
