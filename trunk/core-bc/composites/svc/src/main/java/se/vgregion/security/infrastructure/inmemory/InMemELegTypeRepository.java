package se.vgregion.security.infrastructure.inmemory;

import java.util.Collection;

import se.vgregion.dao.domain.patterns.repository.inmemory.AbstractInMemoryRepository;
import se.vgregion.domain.security.pkiclient.ELegType;
import se.vgregion.domain.security.pkiclient.ELegTypeRepository;

public class InMemELegTypeRepository extends AbstractInMemoryRepository<ELegType, String> implements
        ELegTypeRepository {

    @Override
    public Collection<ELegType> store(Collection<ELegType> types) {
        for (ELegType eLegType : types) {
            super.store(eLegType);
        }
        return types;
    }

}
