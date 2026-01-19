package com.mlconf.core.application.usecase.impl;

import com.mlconf.core.application.dto.SessionSnapshotDTO;
import com.mlconf.core.application.error.NotFoundException;
import com.mlconf.core.application.mapper.SessionSnapshotMapper;
import com.mlconf.core.application.usecase.FinalizeSessionUseCase;
import com.mlconf.core.domain.conference.model.ConferenceItem;
import com.mlconf.core.domain.conference.model.ConferenceSession;
import com.mlconf.core.domain.conference.repository.ConferenceItemRepository;
import com.mlconf.core.domain.conference.repository.ConferenceSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class FinalizeSessionUseCaseImpl implements FinalizeSessionUseCase {

    private static final Logger logger = LoggerFactory.getLogger(FinalizeSessionUseCaseImpl.class);

    private final ConferenceSessionRepository sessionRepository;
    private final ConferenceItemRepository itemRepository;
    private final Clock clock;

    public FinalizeSessionUseCaseImpl(ConferenceSessionRepository sessionRepository,
                                      ConferenceItemRepository itemRepository,
                                      Clock clock) {
        this.sessionRepository = Objects.requireNonNull(sessionRepository, "sessionRepository must not be null");
        this.itemRepository = Objects.requireNonNull(itemRepository, "itemRepository must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    @Override
    public SessionSnapshotDTO execute(UUID sessionId) {
        Objects.requireNonNull(sessionId, "sessionId must not be null");

        ConferenceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("session not found: " + sessionId));

        List<ConferenceItem> currentItems = itemRepository.findAllBySessionId(sessionId);
        ConferenceSession.FinalizeResult result = session.finalizeSession(currentItems, clock.instant());

        ConferenceSession savedSession = sessionRepository.save(result.session());
        for (var item : result.items()) {
            itemRepository.save(sessionId, item);
        }

        logger.info("Session finalized: sessionId={}", sessionId);

        List<ConferenceItem> resultItems = itemRepository.findAllBySessionId(sessionId);
        return SessionSnapshotMapper.toSnapshot(savedSession, resultItems);
    }
}
