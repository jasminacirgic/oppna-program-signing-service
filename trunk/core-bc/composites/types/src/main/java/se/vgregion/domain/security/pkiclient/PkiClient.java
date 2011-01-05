package se.vgregion.domain.security.pkiclient;

/**
 * Java 5 enumeration of Pki Clients.
 * 
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 * 
 */
public enum PkiClient {

    /**
     * PKI Client used when signing with certificates supplied by Nordea.
     */
    NEXUS_PERSONAL_4(4),
    /**
     * PKI Client used when signing with certificates supplied by Telia and SITHS.
     */
    NETMAKER_NETID_4(5),
    /**
     * PKI Client used when signing with certificates supplied by BankId.
     */
    NEXUS_PERSONAL_4X(6);

    private int id;

    private PkiClient(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}