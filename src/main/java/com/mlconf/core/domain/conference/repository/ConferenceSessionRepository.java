package com.mlconf.core.domain.conference.repository;

import com.mlconf.core.domain.conference.model.ConferenceSession;

import java.util.Optional;
import java.util.UUID;

public interface ConferenceSessionRepository {

    Optional<ConferenceSession> findById(UUID sessionId);

    ConferenceSession save(ConferenceSession session);
}
