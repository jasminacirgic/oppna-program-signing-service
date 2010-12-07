package se.vgregion.web.security;

import java.util.Map;

import javax.xml.ws.BindingProvider;

public class OsifUtil {
    static public void setEndpointAddress(Object port, String address) {
        BindingProvider bp = (BindingProvider) port;
        Map<String, Object> context = bp.getRequestContext();
        context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, address);
    }
}
