package com.mlconf.core.application.usecase.impl;

import com.mlconf.core.application.dto.ImportRequestDTO;
import com.mlconf.core.application.dto.SessionSnapshotDTO;
import com.mlconf.core.application.error.NotFoundException;
import com.mlconf.core.application.mapper.SessionSnapshotMapper;
import com.mlconf.core.application.usecase.ImportConferenceListUseCase;
import com.mlconf.core.domain.conference.model.ConferenceItem;
import com.mlconf.core.domain.conference.model.ConferenceSession;
import com.mlconf.core.domain.conference.repository.ConferenceItemRepository;
import com.mlconf.core.domain.conference.repository.ConferenceSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class ImportConferenceListUseCaseImpl implements ImportConferenceListUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ImportConferenceListUseCaseImpl.class);

    private final ConferenceSessionRepository sessionRepository;
    private final ConferenceItemRepository itemRepository;

    public ImportConferenceListUseCaseImpl(ConferenceSessionRepository sessionRepository,
                                           ConferenceItemRepository itemRepository) {
        this.sessionRepository = Objects.requireNonNull(sessionRepository, "sessionRepository must not be null");
        this.itemRepository = Objects.requireNonNull(itemRepository, "itemRepository must not be null");
    }

    @Override
    public SessionSnapshotDTO execute(UUID sessionId, ImportRequestDTO request) {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        Objects.requireNonNull(request, "request must not be null");

        ConferenceSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new NotFoundException("session not found: " + sessionId));

        List<ConferenceItem> existingItems = itemRepository.findAllBySessionId(sessionId);
        List<ConferenceItem> toPersist = session.importConferenceList(existingItems, request.packageIds());
        itemRepository.saveAll(sessionId, toPersist);

        logger.info("Conference list imported: sessionId={}, items={}", sessionId, toPersist.size());

        List<ConferenceItem> resultItems = itemRepository.findAllBySessionId(sessionId);
        return SessionSnapshotMapper.toSnapshot(session, resultItems);
    }
}
