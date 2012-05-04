package se.vgregion.web;

import static org.springframework.http.HttpStatus.Series.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

/**
 * Helper class for handling http messages.
 * 
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 * 
 */
public class HttpMessageHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpMessageHelper.class);

    /**
     * Finds out if the response is a Success: 200 &le; <code>httpStatusCode</code> &lt; 300.
     * 
     * @param response
     *            a httpResponse
     * @return true if response status is success, false otherwise.
     */
    public boolean successStatus(HttpResponse response) {
        HttpStatus.Series status;
        try {
            status = getResponseStatusCode(response).series();
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
    public HttpEntity createEntity(String s) {
        try {
            return new StringEntity(s, "UTF-8");
        } catch (UnsupportedEncodingException shouldNeverHappen) {
            throw new RuntimeException(shouldNeverHappen);
        }
    }

    /**
     * Creates a {@link HttpPost} method for a specific uri.
     * 
     * @param uri
     *            the uri to post to
     * @return a new {@link HttpPost}
     */
    public HttpPost createHttpPostMethod(URI uri) {
        return new HttpPost(uri);
    }

    /**
     * Close the response content for reading.
     * 
     * @param response
     *            the response to close for reading
     */
    public void closeQuitely(HttpResponse response) {
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

    /**
     * Returns the {@link HttpStatus} of a {@link HttpResponse}.
     * 
     * @param response
     *            the {@link HttpResponse} to check for status
     * @return the {@link HttpStatus} of an {@link HttpResponse}
     */
    public HttpStatus getResponseStatusCode(HttpResponse response) {
        return HttpStatus.valueOf(response.getStatusLine().getStatusCode());
    }

    /**
     * Returns the Location Header from an {@link HttpResponse}.
     * 
     * @param response
     *            the HttpResponse
     * @return the Location Header from an {@link HttpResponse}
     */
    public String getLocationHeader(HttpResponse response) {
        return response.getFirstHeader("Location").getValue();
    }
}
