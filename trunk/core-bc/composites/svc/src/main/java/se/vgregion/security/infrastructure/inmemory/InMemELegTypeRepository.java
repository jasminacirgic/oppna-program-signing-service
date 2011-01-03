package se.vgregion.security.infrastructure.inmemory;

import java.util.Collection;

import se.vgregion.dao.domain.patterns.repository.inmemory.AbstractInMemoryRepository;
import se.vgregion.domain.security.pkiclient.ELegType;
import se.vgregion.domain.security.pkiclient.ELegTypeRepository;

/**
 * A repository containing all available type of e-legitimation. This implementation stores the e-leg types in
 * memory.
 * 
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 * 
 */
public class InMemELegTypeRepository extends AbstractInMemoryRepository<ELegType, String> implements
        ELegTypeRepository {

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.domain.security.pkiclient.ELegTypeRepository#store(java.util.Collection)
     */
    @Override
    public void store(Collection<ELegType> types) {
        for (ELegType eLegType : types) {
            super.store(eLegType);
        }
    }

}
