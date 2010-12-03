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

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class HttpSignatureStorage implements SignatureStorage {

    private DefaultHttpClient httpClient;

    public HttpSignatureStorage(DefaultHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public String save(URI submitUri, byte[] pkcs7, String signatureName) throws SignatureStoreageException,
            IOException {
        HttpPost httpPost = new HttpPost(submitUri);
        System.out.println("Posting signature to: " + submitUri);
        HttpEntity entity = HttpUtil.createEntity(Base64.encode(pkcs7));
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
