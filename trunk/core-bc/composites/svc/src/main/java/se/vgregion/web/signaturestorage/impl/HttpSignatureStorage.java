package se.vgregion.web.signaturestorage.impl;

import static org.springframework.http.HttpStatus.*;

import java.io.IOException;
import java.net.URI;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.springframework.http.HttpStatus;

import se.vgregion.web.HttpMessageHelper;
import se.vgregion.web.signaturestorage.SignatureStorage;
import se.vgregion.web.signaturestorage.SignatureStoreageException;

/**
 * An Http-implementation of {@link SignatureStorage}. Has functionality to submit a signature to an http server
 * using http or https.
 * 
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 */
public class HttpSignatureStorage implements SignatureStorage {

    private HttpClient httpClient;
    private HttpMessageHelper httpHelper;

    /**
     * Constructs an instance of {@link HttpSignatureStorage}.
     * 
     * @param httpClient
     *            an http client to use when submiting the signature
     */
    public HttpSignatureStorage(HttpClient httpClient, HttpMessageHelper httpHelper) {
        this.httpClient = httpClient;
        this.httpHelper = httpHelper;
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
        if (StringUtils.isBlank(signature)) {
            throw new IllegalArgumentException("Signature is not allowed to be empty");
        }
        if (StringUtils.isBlank(signatureName)) {
            throw new IllegalArgumentException("Signature name is not allowed to be empty");
        }
        if (submitUri == null) {
            throw new IllegalArgumentException("Submit Uri name is not allowed to be null");
        }

        HttpPost httpPost = httpHelper.createHttpPostMethod(submitUri);
        HttpEntity entity = httpHelper.createEntity(signature);
        httpPost.setEntity(entity);

        HttpResponse response = httpClient.execute(httpPost);
        String returnLocation = StringUtils.EMPTY;

        try {
            HttpStatus responseStatus = httpHelper.getResponseStatusCode(response);
            if (MOVED_TEMPORARILY.equals(responseStatus)) {
                returnLocation = httpHelper.getLocationHeader(response);
            } else if (!OK.equals(responseStatus)) {
                throw new SignatureStoreageException("Invalid status code");
            }
        } finally {
            httpHelper.closeQuitely(response);
        }
        return returnLocation;
    }
}
