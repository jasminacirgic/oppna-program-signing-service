package se.vgregion.security.infrastructure.inmemory;

import java.io.Serializable;
import java.util.Collection;

import se.vgregion.dao.domain.patterns.entity.Entity;
import se.vgregion.dao.domain.patterns.repository.inmemory.InMemoryRepository;

/**
 * An in memory implementation of {@link Repository} extended with batch methods.
 * 
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 * 
 */
public class InMemoryBatchRepository<T extends Entity<ID>, ID extends Serializable> extends
        InMemoryRepository<T, ID> {

    /**
     * Convenience method for store a batch of T.
     * 
     * @param types
     *            a Collection of T to store in the repository
     */
    public void store(Collection<T> types) {
        for (T t : types) {
            super.store(t);
        }
    }

}
