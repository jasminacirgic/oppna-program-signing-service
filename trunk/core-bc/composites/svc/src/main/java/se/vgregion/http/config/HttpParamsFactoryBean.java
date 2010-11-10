package se.vgregion.http.config;

import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.springframework.beans.factory.FactoryBean;

public class HttpParamsFactoryBean implements FactoryBean<HttpParams> {

    private HttpParams httpParams = new BasicHttpParams();

    @Override
    public HttpParams getObject() throws Exception {
        return httpParams;
    }

    @Override
    public Class<?> getObjectType() {
        return HttpParams.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setMaxTotalConnections(int maxTotalConnectsions) {
        System.out.println("maxTotalConnectsions: " + maxTotalConnectsions);
        ConnManagerParams.setMaxTotalConnections(httpParams, 100);
    }

    public void setConnectionTimeout(int connectionTimeout) {
        HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
    }

    public void setSoTimeout(int soTimeout) {
        HttpConnectionParams.setSoTimeout(httpParams, 10000);
    }
}
