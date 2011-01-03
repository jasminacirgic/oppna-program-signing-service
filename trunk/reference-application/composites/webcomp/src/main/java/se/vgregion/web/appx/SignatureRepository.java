package se.vgregion.web.appx;

import java.util.Collection;

import org.springframework.stereotype.Repository;

import se.vgregion.dao.domain.patterns.repository.inmemory.AbstractInMemoryRepository;

@Repository
public class SignatureRepository extends AbstractInMemoryRepository<Signature, Integer> {
    public void removeAll() {
        Collection<Signature> signatures = findAll();
        for (Signature signature : signatures) {
            remove(signature);
        }
    }
}
