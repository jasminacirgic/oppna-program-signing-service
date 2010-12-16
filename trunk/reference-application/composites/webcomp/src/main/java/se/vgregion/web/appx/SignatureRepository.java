package se.vgregion.web.appx;

import org.springframework.stereotype.Repository;

import se.vgregion.dao.domain.patterns.repository.inmemory.AbstractInMemoryRepository;

@Repository
public class SignatureRepository extends AbstractInMemoryRepository<Signature, Integer> {

}
