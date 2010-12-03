package se.vgregion.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;

public class HttpUtil {

    public static boolean successStatus(HttpResponse response) {
        int status = response.getStatusLine().getStatusCode();
        return status >= 200 && status < 300;
    }

    public static String readContent(HttpEntity entity) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        entity.writeTo(out);

        return out.toString("UTF-8");
    }

    public static HttpEntity createEntity(String s) {
        try {
            return new StringEntity(s, "UTF-8");
        } catch (UnsupportedEncodingException shouldNeverHappen) {
            throw new RuntimeException(shouldNeverHappen);
        }
    }

    public static void closeQuitely(HttpResponse response) {
        if (response != null && response.getEntity() != null) {
            try {
                response.getEntity().getContent().close();
            } catch (IOException ignore) {
            } catch (IllegalStateException ignore) {
            }
        }
    }
}
