/**
 * 
 */
package se.vgregion.web.security.services;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.builder.ToStringBuilder;
import se.vgregion.domain.security.pkiclient.ELegType;
import se.vgregion.domain.security.pkiclient.PkiClient;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Bean containing various signature data.
 * 
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 * 
 */
public class SignatureData implements Serializable {

    private static final long serialVersionUID = -4504464078848411704L;

    private String submitUri;
    private String nonce = "";
    private String tbs = "";
    private String encodedTbs = "";
    private ELegType clientType = null;
    private String signature = "";
    private int errorCode = 0;
    private String ticket;
    private String personalNumber;

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

    public void setSubmitUri(String submitUri) {
        try {
            if (!submitUri.startsWith("BASE64")) {
                // Validate
                try {
                    new URI(submitUri);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                this.submitUri = "BASE64" + Base64.encodeBase64String(submitUri.getBytes("UTF-8"));
            } else {
                this.submitUri = submitUri;
            }
        } catch (UnsupportedEncodingException e) {
            // won't happen
        }
    }

    public String getSubmitUri() {
        return submitUri;
    }

    public String getSubmitUriDecoded() {
        try {
            return new String(Base64.decodeBase64(submitUri.replaceAll("BASE64", "")), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // won't happen
            throw new RuntimeException("won't happen");
        }
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

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }

    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }
}
