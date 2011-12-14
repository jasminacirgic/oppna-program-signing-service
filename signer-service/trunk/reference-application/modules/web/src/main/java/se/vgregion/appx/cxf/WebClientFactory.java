package se.vgregion.appx.cxf;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.jaxrs.client.WebClient;

public class WebClientFactory {
    
    public static WebClient create(String baseAddress) {
        SpringBusFactory bf = new SpringBusFactory();
        Bus bus = bf.createBus("webclient-conduit-config.xml");
        bf.setDefaultBus(bus);
        return WebClient.create(baseAddress);
    }
}
