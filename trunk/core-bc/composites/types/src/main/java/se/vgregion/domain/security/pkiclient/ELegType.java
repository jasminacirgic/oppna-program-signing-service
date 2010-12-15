package se.vgregion.domain.security.pkiclient;

import org.apache.commons.lang.builder.ToStringBuilder;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

public class ELegType extends AbstractEntity<String> {

    private String name;
    private String description;
    private PkiClient pkiClient;

    public ELegType() {
        // Needed by spring
    }

    public ELegType(String name, String description, PkiClient pkiClient) {
        this.name = name;
        this.description = description;
        this.pkiClient = pkiClient;
    }

    @Override
    public String getId() {
        return name;
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
        return ToStringBuilder.reflectionToString(this).toString();
    }
}
