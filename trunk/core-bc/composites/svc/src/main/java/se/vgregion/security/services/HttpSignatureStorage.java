package se.vgregion.security.services;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.params.BasicHttpParams;
import org.springframework.http.HttpStatus;

public class HttpSignatureStorage implements SignatureStorage {

    private HttpClient httpClient;

    public HttpSignatureStorage(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public String save(String submitUri, byte[] pkcs7) throws IOException {
        HttpPost httpPost = new HttpPost(submitUri);

        BasicHttpParams params = new BasicHttpParams();
        params.setParameter("signature", pkcs7);
        httpPost.setParams(params);

        HttpResponse response = httpClient.execute(httpPost);

        String returnLocation = null;
        if (response.getStatusLine().getStatusCode() == HttpStatus.MOVED_TEMPORARILY.value()) {
            returnLocation = response.getFirstHeader("Location").getValue();
        }
        return returnLocation;
    }
}
