package se.vgregion.security.infrastructure.inmemory;

import java.util.Collection;

import se.vgregion.dao.domain.patterns.repository.inmemory.AbstractInMemoryRepository;
import se.vgregion.security.domain.pkiclient.ELegType;

public class InMemELegTypeRepository extends AbstractInMemoryRepository<ELegType, String> {
    public Collection<ELegType> store(Collection<ELegType> types) {
        for (ELegType eLegType : types) {
            super.store(eLegType);
        }
        return types;
    }

}
