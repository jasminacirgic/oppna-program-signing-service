package se.vgregion.web.signaturestorage.impl;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.springframework.http.HttpStatus;

import se.vgregion.web.signaturestorage.SignatureStorage;
import se.vgregion.web.signaturestorage.SignatureStoreageException;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class HttpSignatureStorage implements SignatureStorage {

    private HttpClient httpClient;

    public HttpSignatureStorage(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public String save(URI submitUri, byte[] pkcs7, String signatureName) throws SignatureStoreageException {
        HttpResponse response;
        HttpPost httpPost = new HttpPost(submitUri);
        System.out.println("Posting signature to: " + submitUri);

        try {
            StringEntity entity = new StringEntity(Base64.encode(pkcs7));
            httpPost.setEntity(entity);
            response = httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
            throw new SignatureStoreageException(e);
        }

        String returnLocation = null;
        if (response.getStatusLine().getStatusCode() == HttpStatus.MOVED_TEMPORARILY.value()) {
            returnLocation = response.getFirstHeader("Location").getValue();
        } else if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
            throw new SignatureStoreageException(response.getStatusLine().toString());
        }
        return returnLocation;
    }

}
