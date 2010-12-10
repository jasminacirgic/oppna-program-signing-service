/**
 * 
 */
package se.vgregion.security.sign;

/**
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 * 
 */
public class SignForm {
    private String submitUri;
    private String nonce;
    private String tbs;

    public SignForm() {
    }

    public SignForm(String tbs, String submitUri, String nonce) {
        this.tbs = tbs;
        this.submitUri = submitUri;
        this.setNonce(nonce);
    }

    public String getTbs() {
        return tbs;
    }

    public void setTbs(String tbs) {
        this.tbs = tbs;
    }

    public void setSubmitUri(String submitUri) {
        this.submitUri = submitUri;
    }

    public String getSubmitUri() {
        return submitUri;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getNonce() {
        return nonce;
    }
}
