package com.mlconf.core.application.usecase.impl;

import com.mlconf.core.application.dto.SessionSnapshotDTO;
import com.mlconf.core.application.mapper.SessionSnapshotMapper;
import com.mlconf.core.application.usecase.CreateSessionUseCase;
import com.mlconf.core.domain.conference.model.ConferenceSession;
import com.mlconf.core.domain.conference.repository.ConferenceItemRepository;
import com.mlconf.core.domain.conference.repository.ConferenceSessionRepository;

import java.time.Clock;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class CreateSessionUseCaseImpl implements CreateSessionUseCase {

    private final ConferenceSessionRepository sessionRepository;
    private final ConferenceItemRepository itemRepository;
    private final Clock clock;

    public CreateSessionUseCaseImpl(ConferenceSessionRepository sessionRepository,
                                    ConferenceItemRepository itemRepository,
                                    Clock clock) {
        this.sessionRepository = Objects.requireNonNull(sessionRepository, "sessionRepository must not be null");
        this.itemRepository = Objects.requireNonNull(itemRepository, "itemRepository must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    @Override
    public SessionSnapshotDTO execute() {
        var session = new ConferenceSession(UUID.randomUUID(), ConferenceSession.Status.OPEN, clock.instant(), null);
        var saved = sessionRepository.save(session);
        var items = itemRepository.findAllBySessionId(saved.getId());
        return SessionSnapshotMapper.toSnapshot(saved, items == null ? List.of() : items);
    }
}
