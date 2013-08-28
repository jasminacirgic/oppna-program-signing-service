package se.vgregion.appx.cxf;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.jaxrs.client.WebClient;

public final class WebClientFactory {

    private WebClientFactory() {
        // not instantiable by other classes
    }

    /**
     * Factory method for creating a {@link WebClient} with an SSL conduit configuration.
     *
     * @param baseAddress the {@link WebClient} will start the URL of its requests with the <code>baseAdress</code>
     * @return the {@link WebClient}
     */
    public static WebClient create(String baseAddress) {
        SpringBusFactory bf = new SpringBusFactory();
        Bus bus = bf.createBus("webclient-conduit-config.xml");
        bf.setDefaultBus(bus);
        return WebClient.create(baseAddress);
    }
}
