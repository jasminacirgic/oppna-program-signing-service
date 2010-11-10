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
    private int clientType;

    public SignForm() {
    }

    public SignForm(int clientType, String tbs, String pkiPostBackUrl) {
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

    public int getClientType() {
        return clientType;
    }

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public String getPkiPostBackUrl() {
        return pkiPostBackUrl;
    }

    public void setPkiPostBackUrl(String pkiPostBackUrl) {
        this.pkiPostBackUrl = pkiPostBackUrl;
    }
}
