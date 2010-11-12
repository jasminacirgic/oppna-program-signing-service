package se.vgregion.web.signaturestorage.impl;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.params.BasicHttpParams;
import org.springframework.http.HttpStatus;

import se.vgregion.web.signaturestorage.SignatureStorage;
import se.vgregion.web.signaturestorage.SignatureStoreageException;


public class HttpSignatureStorage implements SignatureStorage {

    private HttpClient httpClient;

    public HttpSignatureStorage(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public String save(URI submitUri, byte[] pkcs7, String signatureName) throws SignatureStoreageException {
        HttpPost httpPost = new HttpPost(submitUri);

        BasicHttpParams params = new BasicHttpParams();
        params.setParameter("signature", pkcs7);
        httpPost.setParams(params);

        HttpResponse response;

        try {
            response = httpClient.execute(httpPost);
        } catch (IOException e) {
            throw new SignatureStoreageException(e);
        }

        String returnLocation = null;
        if (response.getStatusLine().getStatusCode() == HttpStatus.MOVED_TEMPORARILY.value()) {
            returnLocation = response.getFirstHeader("Location").getValue();
        }
        return returnLocation;
    }

}
