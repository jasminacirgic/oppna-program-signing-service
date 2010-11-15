package se.vgregion.domain.security.pkiclient;

import java.util.Collection;

import se.vgregion.dao.domain.patterns.repository.inmemory.AbstractInMemoryRepository;

public class ELegTypeRepository extends AbstractInMemoryRepository<ELegType, String> {
    public Collection<ELegType> store(Collection<ELegType> types) {
        for (ELegType eLegType : types) {
            super.store(eLegType);
        }
        return types;
    }

}
