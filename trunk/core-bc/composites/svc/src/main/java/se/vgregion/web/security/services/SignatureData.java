/**
 * 
 */
package se.vgregion.web.security.services;

import java.net.URI;

import org.apache.commons.lang.builder.ToStringBuilder;

import se.vgregion.domain.security.pkiclient.PkiClient;

/**
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 * 
 */
public class SignatureData {
    private URI submitUri;
    private String postbackUrl;
    private String nonce;
    private String tbs;
    private PkiClient pkiClient;
    private String encodedTbs;
    private String signature;

    public SignatureData(String tbs, String encocedTbs, URI submitUri, String nonce, String postbackUrl,
            PkiClient pkiClient) {
        this.tbs = tbs;
        this.encodedTbs = encocedTbs;
        this.submitUri = submitUri;
        this.nonce = nonce;
        this.postbackUrl = postbackUrl;
        this.pkiClient = pkiClient;
    }

    public String getTbs() {
        return tbs;
    }

    public void setTbs(String tbs) {
        this.tbs = tbs;
    }

    public void setSubmitUri(URI submitUri) {
        this.submitUri = submitUri;
    }

    public URI getSubmitUri() {
        return submitUri;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getNonce() {
        return nonce;
    }

    public void setPostbackUrl(String postbackUrl) {
        this.postbackUrl = postbackUrl;
    }

    public String getPostbackUrl() {
        return postbackUrl;
    }

    public void setPkiClient(PkiClient pkiClient) {
        this.pkiClient = pkiClient;
    }

    public PkiClient getPkiClient() {
        return pkiClient;
    }

    public void setEncodedTbs(String encodedTbs) {
        this.encodedTbs = encodedTbs;
    }

    public String getEncodedTbs() {
        return encodedTbs;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSignature() {
        return signature;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }
}
