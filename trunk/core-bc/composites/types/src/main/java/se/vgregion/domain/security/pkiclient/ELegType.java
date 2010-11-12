package se.vgregion.domain.security.pkiclient;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;
import se.vgregion.dao.domain.patterns.entity.EntityBuilder;

public class ELegType extends AbstractEntity<ELegType, Integer> {

    public static class ELegTypeBuilder implements EntityBuilder<ELegTypeBuilder, ELegType> {

        private ELegType entity = new ELegType();

        public ELegTypeBuilder id(Integer id) {
            entity.id = id;
            return this;
        }

        public ELegTypeBuilder name(String name) {
            entity.name = name;
            return this;
        }

        public ELegTypeBuilder description(String description) {
            entity.description = description;
            return this;
        }

        public ELegType build() {
            return entity;
        }
    }

    private Integer id;
    private String name;
    private String description;

    private ELegType() {
        // Used by the builder pattern
    }

    public ELegType(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
