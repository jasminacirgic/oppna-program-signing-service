/**
 * 
 */
package se.vgregion.web.security.services;

import java.net.URI;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.builder.ToStringBuilder;

import se.vgregion.domain.security.pkiclient.ELegType;
import se.vgregion.domain.security.pkiclient.PkiClient;

/**
 * Bean containing various signature data.
 * 
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 * 
 */
public class SignatureData {
    private URI submitUri;
    private String nonce = "";
    private String tbs = "";
    private String encodedTbs = "";
    private ELegType clientType = null;
    private String signature = null;

    public String getTbs() {
        return tbs;
    }

    public void setTbs(String tbs) {
        this.tbs = tbs;
    }

    public void setEncodedTbs(String encodedTbs) {
        this.encodedTbs = encodedTbs;
    }

    public String getEncodedTbs() {
        return encodedTbs;
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

    /**
     * The nonce is Base64 encoded.
     * 
     * @return the Base64 encoded nonce
     */
    public String getEncodedNonce() {
        return encode(nonce);
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSignature() {
        return signature;
    }

    private static String encode(String s) {
        return Base64.encodeBase64String(s.getBytes()).trim();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public void setClientType(ELegType clientType) {
        this.clientType = clientType;
    }

    public PkiClient getPkiClient() {
        return clientType.getPkiClient();
    }

    public ELegType getClientType() {
        return clientType;
    }

}
