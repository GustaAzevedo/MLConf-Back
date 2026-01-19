package com.mlconf.core.application.usecase.impl;

import com.mlconf.core.application.dto.ScanRequestDTO;
import com.mlconf.core.application.dto.SessionSnapshotDTO;
import com.mlconf.core.application.mapper.SessionSnapshotMapper;
import com.mlconf.core.application.usecase.RegisterScanUseCase;
import com.mlconf.core.domain.common.DomainException;
import com.mlconf.core.domain.conference.model.ConferenceItem;
import com.mlconf.core.domain.conference.model.ConferenceSession;
import com.mlconf.core.domain.conference.repository.ConferenceItemRepository;
import com.mlconf.core.domain.conference.repository.ConferenceSessionRepository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class RegisterScanUseCaseImpl implements RegisterScanUseCase {

    private final ConferenceSessionRepository sessionRepository;
    private final ConferenceItemRepository itemRepository;

    public RegisterScanUseCaseImpl(ConferenceSessionRepository sessionRepository,
                                   ConferenceItemRepository itemRepository) {
        this.sessionRepository = Objects.requireNonNull(sessionRepository, "sessionRepository must not be null");
        this.itemRepository = Objects.requireNonNull(itemRepository, "itemRepository must not be null");
    }

    @Override
    public SessionSnapshotDTO execute(UUID sessionId, ScanRequestDTO request) {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        Objects.requireNonNull(request, "request must not be null");

        ConferenceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new DomainException("session not found: " + sessionId));

        List<ConferenceItem> currentItems = itemRepository.findAllBySessionId(sessionId);
        List<ConferenceItem> updatedItems = session.registerScan(currentItems, request.packageId());

        for (var item : updatedItems) {
            itemRepository.save(sessionId, item);
        }

        List<ConferenceItem> resultItems = itemRepository.findAllBySessionId(sessionId);
        return SessionSnapshotMapper.toSnapshot(session, resultItems);
    }
}
