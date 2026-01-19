package com.mlconf.core.application.usecase.impl;

import com.mlconf.core.application.dto.SessionSnapshotDTO;
import com.mlconf.core.application.error.NotFoundException;
import com.mlconf.core.application.mapper.SessionSnapshotMapper;
import com.mlconf.core.application.usecase.ExportSessionResultUseCase;
import com.mlconf.core.domain.conference.model.ConferenceItem;
import com.mlconf.core.domain.conference.model.ConferenceSession;
import com.mlconf.core.domain.conference.repository.ConferenceItemRepository;
import com.mlconf.core.domain.conference.repository.ConferenceSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class ExportSessionResultUseCaseImpl implements ExportSessionResultUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ExportSessionResultUseCaseImpl.class);

    private final ConferenceSessionRepository sessionRepository;
    private final ConferenceItemRepository itemRepository;

    public ExportSessionResultUseCaseImpl(ConferenceSessionRepository sessionRepository,
                                          ConferenceItemRepository itemRepository) {
        this.sessionRepository = Objects.requireNonNull(sessionRepository, "sessionRepository must not be null");
        this.itemRepository = Objects.requireNonNull(itemRepository, "itemRepository must not be null");
    }

    @Override
    public SessionSnapshotDTO execute(UUID sessionId) {
        Objects.requireNonNull(sessionId, "sessionId must not be null");

        ConferenceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("session not found: " + sessionId));

        List<ConferenceItem> items = itemRepository.findAllBySessionId(sessionId);

        logger.info("Session exported: sessionId={}", sessionId);

        return SessionSnapshotMapper.toSnapshot(session, items);
    }
}
