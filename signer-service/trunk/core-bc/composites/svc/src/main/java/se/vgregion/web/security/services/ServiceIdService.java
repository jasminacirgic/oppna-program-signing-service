package se.vgregion.web.security.services;

import java.io.IOException;

/**
 * @author Patrik Bergström
 */
public interface ServiceIdService {

    boolean containsServiceId(String serviceId);

    String getAppName(String serviceId);

    void storeServiceId(String serviceId, String appName) throws IOException;

}
