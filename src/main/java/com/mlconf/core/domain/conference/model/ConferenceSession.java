package com.mlconf.core.domain.conference.model;

import com.mlconf.core.domain.common.DomainException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class ConferenceSession {

    public enum Status {
        OPEN,
        FINALIZED
    }

    private final UUID id;
    private final Status status;
    private final Instant createdAt;
    private final Instant finalizedAt;

    public ConferenceSession(UUID id, Status status, Instant createdAt, Instant finalizedAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");

        if (status == Status.OPEN && finalizedAt != null) {
            throw new DomainException("finalizedAt must be null when status is OPEN");
        }
        if (status == Status.FINALIZED && finalizedAt == null) {
            throw new DomainException("finalizedAt must not be null when status is FINALIZED");
        }
        this.finalizedAt = finalizedAt;
    }

    public UUID getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getFinalizedAt() {
        return finalizedAt;
    }
}
