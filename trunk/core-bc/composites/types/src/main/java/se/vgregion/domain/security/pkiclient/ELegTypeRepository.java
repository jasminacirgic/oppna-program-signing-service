package se.vgregion.domain.security.pkiclient;

import java.util.Collection;

import se.vgregion.dao.domain.patterns.repository.Repository;

public interface ELegTypeRepository extends Repository<ELegType, String> {
    public Collection<ELegType> store(Collection<ELegType> types);

}
