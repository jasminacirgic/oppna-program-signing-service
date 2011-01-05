package se.vgregion.web;

import static org.springframework.http.HttpStatus.Series.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

/**
 * Utility class for handling http messages.
 * 
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 * 
 */
public final class HttpUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);

    private HttpUtil() {
    }

    /**
     * Finds out if the response is a Success: 200 &le; <code>httpStatusCode</code> &lt; 300.
     * 
     * @param response
     *            a httpResponse
     * @return true if response status is success, false otherwise.
     */
    public static boolean successStatus(HttpResponse response) {
        HttpStatus.Series status;
        try {
            status = HttpStatus.valueOf(response.getStatusLine().getStatusCode()).series();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
            return false;
        }
        return SUCCESSFUL.equals(status);
    }

    /**
     * Creates an UTF-8 encoded {@link HttpEntity} from a string.
     * 
     * @param s
     *            the string to create the entity from
     * @return an httpEntity
     */
    public static HttpEntity createEntity(String s) {
        try {
            return new StringEntity(s, "UTF-8");
        } catch (UnsupportedEncodingException shouldNeverHappen) {
            throw new RuntimeException(shouldNeverHappen);
        }
    }

    /**
     * Close the response content for reading.
     * 
     * @param response
     *            the response to close for reading
     */
    public static void closeQuitely(HttpResponse response) {
        if (response != null && response.getEntity() != null) {
            try {
                response.getEntity().getContent().close();
            } catch (IOException ignore) {
                LOGGER.debug("Unable to close response content stream.", ignore);
            } catch (IllegalStateException ignore) {
                LOGGER.debug("Unable to close response content stream.", ignore);
            }
        }
    }
}
