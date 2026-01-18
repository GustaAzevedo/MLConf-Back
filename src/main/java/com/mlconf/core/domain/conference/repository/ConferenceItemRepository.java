package com.mlconf.core.domain.conference.repository;

import com.mlconf.core.domain.conference.model.ConferenceItem;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConferenceItemRepository {

    Optional<ConferenceItem> findBySessionIdAndPackageId(UUID sessionId, String packageId);

    List<ConferenceItem> findAllBySessionId(UUID sessionId);

    void save(UUID sessionId, ConferenceItem item);

    void saveAll(UUID sessionId, Collection<ConferenceItem> items);
}
