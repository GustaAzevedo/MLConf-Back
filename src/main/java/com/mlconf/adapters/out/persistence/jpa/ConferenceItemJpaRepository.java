package com.mlconf.adapters.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConferenceItemJpaRepository extends JpaRepository<ConferenceItemJpaEntity, Long> {

    Optional<ConferenceItemJpaEntity> findBySessionIdAndPackageId(UUID sessionId, String packageId);

    List<ConferenceItemJpaEntity> findAllBySessionId(UUID sessionId);
}
