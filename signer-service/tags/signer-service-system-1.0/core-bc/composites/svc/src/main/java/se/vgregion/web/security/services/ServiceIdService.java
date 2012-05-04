package se.vgregion.web.security.services;

import java.io.IOException;

/**
 * Service interface for managing serviceIds.
 *
 * @author Patrik Bergström
 */
public interface ServiceIdService {

    /**
     * Tests whether the given serviceId is allowed.
     *
     * @param serviceId serviceId
     * @return <code>true</code> if the serviceId exists or <code>false</code> otherwise
     */
    boolean containsServiceId(String serviceId);

    /**
     * The the application name associated with the given serviceId.
     *
     * @param serviceId serviceId
     * @return the application name
     */
    String getApplicationName(String serviceId);

    /**
     * Stores a serviceId and an associated application name to the underlying repository.
     *
     * @param serviceId serviceId
     * @param appName appName
     * @throws IOException if anything fails in relation to input or output
     */
    void storeServiceId(String serviceId, String appName) throws IOException;

}
