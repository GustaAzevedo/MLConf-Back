package com.mlconf.domain.conference.repository;

import com.mlconf.domain.conference.model.ConferenceSession;

import java.util.Optional;
import java.util.UUID;

public interface ConferenceSessionRepository {

    Optional<ConferenceSession> findById(UUID sessionId);

    ConferenceSession save(ConferenceSession session);
}
