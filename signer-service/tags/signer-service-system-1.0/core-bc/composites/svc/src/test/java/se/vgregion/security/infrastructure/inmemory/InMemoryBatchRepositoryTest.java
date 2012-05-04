package se.vgregion.security.infrastructure.inmemory;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

public class InMemoryBatchRepositoryTest {
    private InMemoryBatchRepository<MockEntity, Integer> repo = new InMemoryBatchRepository<MockEntity, Integer>();

    @Test
    public void shouldStoreACollection() throws Exception {
        // Given
        Collection<MockEntity> entities = Arrays.asList(new MockEntity(0), new MockEntity(1), new MockEntity(2));

        // When
        repo.store(entities);

        // Then
        assertEquals(3, repo.findAll().size());
    }

    private static class MockEntity extends AbstractEntity<Integer> {
        private Integer id;

        public MockEntity(Integer id) {
            this.id = id;
        }

        @Override
        public Integer getId() {
            return id;
        }
    }
}
