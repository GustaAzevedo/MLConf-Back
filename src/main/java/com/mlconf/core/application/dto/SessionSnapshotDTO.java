package com.mlconf.core.application.dto;

import com.mlconf.core.domain.conference.model.enums.IssueCategory;
import com.mlconf.core.domain.conference.model.enums.ItemState;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record SessionSnapshotDTO(
        UUID sessionId,
        String status,
        Instant createdAt,
        Instant finalizedAt,
        List<ItemDTO> items
) {

    public SessionSnapshotDTO {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(items, "items must not be null");
    }

    public record ItemDTO(String packageId, ItemState state, IssueCategory issueCategory) {
        public ItemDTO {
            if (packageId == null || packageId.isBlank()) {
                throw new IllegalArgumentException("packageId must not be blank");
            }
            Objects.requireNonNull(state, "state must not be null");
        }
    }
}
