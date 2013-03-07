package se.vgregion.domain.security.pkiclient;

import static se.vgregion.signera.signature._1.SignatureFormat.*;

import java.util.Locale;

import se.vgregion.signera.signature._1.SignatureFormat;

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
    NEXUS_PERSONAL_4(4, CMS),
    /**
     * PKI Client used when signing with certificates supplied by Telia and SITHS.
     */
    NETMAKER_NETID_4(5, CMS),
    /**
     * PKI Client used when signing with certificates supplied by BankId.
     */
    NEXUS_PERSONAL_4X(6, XMLDIGSIG),

    MOBILE_BANKID(7, XMLDIGSIG);

    private int id;
    private SignatureFormat signatureFormat;

    private PkiClient(int id, SignatureFormat format) {
        this.id = id;
        this.signatureFormat = format;
    }

    public int getId() {
        return id;
    }

    public SignatureFormat getSignatureFormat() {
        return signatureFormat;
    }

    @Override
    public String toString() {
        return name().toLowerCase(new Locale("sv"));
    }
}