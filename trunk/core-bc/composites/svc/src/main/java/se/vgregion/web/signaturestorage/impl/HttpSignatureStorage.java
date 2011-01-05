package se.vgregion.web.signaturestorage.impl;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.http.HttpStatus;

import se.vgregion.web.HttpUtil;
import se.vgregion.web.signaturestorage.SignatureStorage;
import se.vgregion.web.signaturestorage.SignatureStoreageException;

/**
 * An Http-implementation of {@link SignatureStorage}. Has functionality to submit a signature to an http server
 * using http or https.
 * 
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 */
public class HttpSignatureStorage implements SignatureStorage {

    private DefaultHttpClient httpClient;

    /**
     * Constructs an instance of {@link HttpSignatureStorage}.
     * 
     * @param httpClient
     *            an http client to use when submiting the signature
     */
    public HttpSignatureStorage(DefaultHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.web.signaturestorage.SignatureStorage#submitSignature(java.net.URI, java.lang.String,
     * java.lang.String)
     */
    @Override
    public String submitSignature(URI submitUri, String signature, String signatureName)
            throws SignatureStoreageException, IOException {
        HttpPost httpPost = new HttpPost(submitUri);
        HttpEntity entity = HttpUtil.createEntity(signature);
        httpPost.setEntity(entity);
        HttpResponse response = httpClient.execute(httpPost);
        String returnLocation = null;

        try {
            if (response.getStatusLine().getStatusCode() == HttpStatus.MOVED_TEMPORARILY.value()) {
                returnLocation = response.getFirstHeader("Location").getValue();
            } else if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
                throw new SignatureStoreageException(response.getStatusLine().toString());
            }
        } finally {
            HttpUtil.closeQuitely(response);
        }
        return returnLocation;
    }
}
