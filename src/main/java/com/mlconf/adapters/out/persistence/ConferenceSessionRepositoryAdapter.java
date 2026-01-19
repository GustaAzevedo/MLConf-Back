package com.mlconf.adapters.out.persistence;

import com.mlconf.adapters.out.persistence.jpa.ConferenceSessionJpaRepository;
import com.mlconf.adapters.out.persistence.mapper.ConferenceMapper;
import com.mlconf.core.domain.conference.model.ConferenceSession;
import com.mlconf.core.domain.conference.repository.ConferenceSessionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class ConferenceSessionRepositoryAdapter implements ConferenceSessionRepository {

    private final ConferenceSessionJpaRepository repository;

    public ConferenceSessionRepositoryAdapter(ConferenceSessionJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<ConferenceSession> findById(UUID sessionId) {
        return repository.findById(sessionId).map(ConferenceMapper::toDomain);
    }

    @Override
    public ConferenceSession save(ConferenceSession session) {
        var entity = ConferenceMapper.toEntity(session);
        var saved = repository.save(entity);
        return ConferenceMapper.toDomain(saved);
    }
}
