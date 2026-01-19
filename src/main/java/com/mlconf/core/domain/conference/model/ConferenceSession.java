package com.mlconf.core.domain.conference.model;

import com.mlconf.core.domain.common.DomainException;

import com.mlconf.core.domain.conference.model.enums.ItemState;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    public boolean isFinalized() {
        return status == Status.FINALIZED;
    }

    public boolean isOpen() {
        return status == Status.OPEN;
    }

    public List<ConferenceItem> importConferenceList(List<ConferenceItem> existingItems, List<String> packageIds) {
        requireOpen();
        Objects.requireNonNull(existingItems, "existingItems must not be null");
        Objects.requireNonNull(packageIds, "packageIds must not be null");

        if (!existingItems.isEmpty()) {
            throw new DomainException("conference list already imported for this session");
        }
        if (packageIds.isEmpty()) {
            throw new DomainException("packageIds must not be empty");
        }

        var dedup = new HashSet<String>();
        var items = new ArrayList<ConferenceItem>(packageIds.size());

        for (var raw : packageIds) {
            if (raw == null || raw.isBlank()) {
                throw new DomainException("packageId must not be blank");
            }
            var packageId = raw.trim();
            if (!dedup.add(packageId)) {
                throw new DomainException("packageIds must not contain duplicates: " + packageId);
            }
            items.add(new ConferenceItem(packageId, ItemState.PENDING, null));
        }

        return List.copyOf(items);
    }

    public List<ConferenceItem> registerScan(List<ConferenceItem> currentItems, String packageId) {
        requireOpen();
        Objects.requireNonNull(currentItems, "currentItems must not be null");

        if (packageId == null || packageId.isBlank()) {
            throw new DomainException("packageId must not be blank");
        }

        var normalized = packageId.trim();
        var updated = new ArrayList<ConferenceItem>(currentItems.size());
        var found = false;

        for (var item : currentItems) {
            if (item.getPackageId().equals(normalized)) {
                found = true;
                updated.add(item.confirm());
            } else {
                updated.add(item);
            }
        }

        if (!found) {
            throw new DomainException("packageId not found in conference list: " + normalized);
        }

        return List.copyOf(updated);
    }

    public FinalizeResult finalizeSession(List<ConferenceItem> currentItems, Instant now) {
        Objects.requireNonNull(currentItems, "currentItems must not be null");
        Objects.requireNonNull(now, "now must not be null");

        if (isFinalized()) {
            return new FinalizeResult(this, List.copyOf(currentItems));
        }

        var finalizedSession = finalizeAt(now);
        var updated = new ArrayList<ConferenceItem>(currentItems.size());

        for (var item : currentItems) {
            updated.add(item.markMissingIfPending());
        }

        return new FinalizeResult(finalizedSession, List.copyOf(updated));
    }

    public ConferenceSession finalizeAt(Instant finalizeAt) {
        Objects.requireNonNull(finalizeAt, "finalizeAt must not be null");

        if (isFinalized()) {
            return this;
        }

        return new ConferenceSession(this.id, Status.FINALIZED, this.createdAt, finalizeAt);
    }

    public record FinalizeResult(ConferenceSession session, List<ConferenceItem> items) {
        public FinalizeResult {
            Objects.requireNonNull(session, "session must not be null");
            Objects.requireNonNull(items, "items must not be null");
        }
    }

    private void requireOpen() {
        if (isFinalized()) {
            throw new DomainException("session is finalized");
        }
    }
}
