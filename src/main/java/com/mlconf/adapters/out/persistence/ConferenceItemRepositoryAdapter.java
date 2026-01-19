package com.mlconf.adapters.out.persistence;

import com.mlconf.adapters.out.persistence.jpa.ConferenceItemJpaRepository;
import com.mlconf.adapters.out.persistence.mapper.ConferenceMapper;
import com.mlconf.core.domain.conference.model.ConferenceItem;
import com.mlconf.core.domain.conference.repository.ConferenceItemRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ConferenceItemRepositoryAdapter implements ConferenceItemRepository {

    private final ConferenceItemJpaRepository repository;

    public ConferenceItemRepositoryAdapter(ConferenceItemJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<ConferenceItem> findBySessionIdAndPackageId(UUID sessionId, String packageId) {
        return repository.findBySessionIdAndPackageId(sessionId, packageId).map(ConferenceMapper::toDomain);
    }

    @Override
    public List<ConferenceItem> findAllBySessionId(UUID sessionId) {
        return ConferenceMapper.toDomainItems(repository.findAllBySessionId(sessionId));
    }

    @Override
    public void save(UUID sessionId, ConferenceItem item) {
        repository.save(ConferenceMapper.toEntity(sessionId, item));
    }

    @Override
    public void saveAll(UUID sessionId, Collection<ConferenceItem> items) {
        var entities = items.stream().map(item -> ConferenceMapper.toEntity(sessionId, item)).toList();
        repository.saveAll(entities);
    }
}
