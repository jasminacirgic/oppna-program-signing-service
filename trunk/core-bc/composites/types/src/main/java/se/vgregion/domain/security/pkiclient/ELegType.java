package se.vgregion.domain.security.pkiclient;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

public class ELegType extends AbstractEntity<String> {

    private String name;
    private String description;
    private String pkiClientName;

    public ELegType(String name, String description, String pkiClientName) {
        this.name = name;
        this.description = description;
        this.pkiClientName = pkiClientName;
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

    public String getPkiClientName() {
        return pkiClientName;
    }
}
