package se.vgregion.domain.security.pkiclient;

import java.util.Collection;

import se.vgregion.dao.domain.patterns.repository.Repository;

/**
 * A repository containing different types of e-legitimation.
 * 
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 * 
 */
public interface ELegTypeRepository extends Repository<ELegType, String> {

    /**
     * Convenience method for store a batch of {@link ELegType}.
     * 
     * @param types
     *            a Collection of {@link ELegType} to store in the repository
     */
    public void store(Collection<ELegType> types);

}
