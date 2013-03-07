package se.vgregion.domain.security.pkiclient;

import org.apache.commons.lang.builder.ToStringBuilder;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

import java.io.Serializable;

/**
 * Stores data about a type of e-legitimation. Existing e-legitimation is:
 * <ul>
 * <li>BankId</li>
 * <li>Nordea</li>
 * <li>Telia</li>
 * <li>SITSH</li>
 * </ul>
 * 
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 */
public class ELegType extends AbstractEntity<String> implements Serializable {
    // Instances of this bean is created through spring configuration.

    private static final long serialVersionUID = 5037231646277785506L;

    private String id;
    private String name;
    private String description;
    private PkiClient pkiClient;

    /**
     * Constructs a new {@link ELegType}.
     * 
     * @param name
     *            the name of the e-legitimation
     * @param description
     *            a description of the e-legitimation
     * @param pkiClient
     *            the pki client to use when signing data with this e-legitimation
     */
    public ELegType(String id, String name, String description, PkiClient pkiClient) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.pkiClient = pkiClient;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public PkiClient getPkiClient() {
        return pkiClient;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
