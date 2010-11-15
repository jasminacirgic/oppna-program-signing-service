/**
 * 
 */
package se.vgregion.security.sign;

/**
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 * 
 */
public class SignForm {
    private String pkiPostBackUrl;
    private String tbs;
    private String clientType;

    public SignForm() {
    }

    public SignForm(String clientType, String tbs, String pkiPostBackUrl) {
        this.clientType = clientType;
        this.tbs = tbs;
        this.pkiPostBackUrl = pkiPostBackUrl;
    }

    public String getTbs() {
        return tbs;
    }

    public void setTbs(String tbs) {
        this.tbs = tbs;
    }

    public String getPkiPostBackUrl() {
        return pkiPostBackUrl;
    }

    public void setPkiPostBackUrl(String pkiPostBackUrl) {
        this.pkiPostBackUrl = pkiPostBackUrl;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }
}
