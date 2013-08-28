package se.vgregion.web.signaturestorage.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.springframework.http.HttpStatus;
import org.springframework.oxm.Marshaller;
import se.vgregion.signera.signature._1.SignatureEnvelope;
import se.vgregion.web.HttpMessageHelper;
import se.vgregion.web.signaturestorage.SignatureStorage;
import se.vgregion.web.signaturestorage.SignatureStoreageException;

import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;

import static org.springframework.http.HttpStatus.MOVED_TEMPORARILY;
import static org.springframework.http.HttpStatus.OK;
/**
 * An Http-implementation of {@link SignatureStorage}. Has functionality to submit a signature to an http server
 * using http or https.
 * 
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 */
public class HttpSignatureStorage implements SignatureStorage {

    private Marshaller marshaller;
    private HttpClient httpClient;
    private HttpMessageHelper httpHelper;


    /**
     * Constructs an instance of {@link HttpSignatureStorage}.
     * 
     * @param httpClient
     *            an http client to use when submiting the signature
     * @param httpHelper
     *            an Helper Class to get various information out from http messages
     * @param marshaller marshaller to use when "serializing" to xml
     * 
     */
    public HttpSignatureStorage(HttpClient httpClient, HttpMessageHelper httpHelper,
            Marshaller marshaller) {
        this.httpClient = httpClient;
        this.httpHelper = httpHelper;
        this.marshaller = marshaller;
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.web.signaturestorage.SignatureStorage#submitSignature(java.net.URI, java.lang.String,
     * java.lang.String)
     */
    @Override
    public String submitSignature(URI submitUri, SignatureEnvelope envelope)
            throws SignatureStoreageException, IOException {
        if (submitUri == null) {
            throw new IllegalArgumentException("Submit Uri name is not allowed to be null");
        }

        HttpPost httpPost = httpHelper.createHttpPostMethod(submitUri);

        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        marshaller.marshal(envelope, new StreamResult(boas));
        HttpEntity entity = httpHelper.createEntity(boas.toString("UTF-8"));
        httpPost.setHeader("Content-type", "text/xml");
        httpPost.setEntity(entity);

        HttpResponse response = httpClient.execute(httpPost);
        String returnLocation = StringUtils.EMPTY;

        try {
            HttpStatus responseStatus = httpHelper.getResponseStatusCode(response);
            if (MOVED_TEMPORARILY.value() == responseStatus.value()) {
                returnLocation = httpHelper.getLocationHeader(response);
            } else if (OK.value() != responseStatus.value()) {
                String reasonPhrase = response.getStatusLine().getReasonPhrase();
                throw new SignatureStoreageException("Invalid status code: " + responseStatus.toString() + " - "
                        + reasonPhrase);
            }
        } finally {
            httpHelper.closeQuitely(response);
        }
        return returnLocation;
    }
}
