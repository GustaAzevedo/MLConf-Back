package com.mlconf.adapters.out.persistence.mapper;

import com.mlconf.adapters.out.persistence.jpa.ConferenceItemJpaEntity;
import com.mlconf.adapters.out.persistence.jpa.ConferenceSessionJpaEntity;
import com.mlconf.core.domain.conference.model.ConferenceItem;
import com.mlconf.core.domain.conference.model.ConferenceSession;
import com.mlconf.core.domain.conference.model.enums.IssueCategory;
import com.mlconf.core.domain.conference.model.enums.ItemState;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class ConferenceMapper {

    private ConferenceMapper() {
    }

    public static ConferenceSessionJpaEntity toEntity(ConferenceSession session) {
        return new ConferenceSessionJpaEntity(
                session.getId(),
                session.getStatus().name(),
                session.getCreatedAt(),
                session.getFinalizedAt()
        );
    }

    public static ConferenceSession toDomain(ConferenceSessionJpaEntity entity) {
        return new ConferenceSession(
                entity.getId(),
                ConferenceSession.Status.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getFinalizedAt()
        );
    }

    public static ConferenceItemJpaEntity toEntity(UUID sessionId, ConferenceItem item) {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        return new ConferenceItemJpaEntity(
                sessionId,
                item.getPackageId(),
                item.getState().name(),
                item.getIssueCategory() == null ? null : item.getIssueCategory().name()
        );
    }

    public static ConferenceItem toDomain(ConferenceItemJpaEntity entity) {
        IssueCategory issue = entity.getIssueCategory() == null ? null : IssueCategory.valueOf(entity.getIssueCategory());
        return new ConferenceItem(entity.getPackageId(), ItemState.valueOf(entity.getState()), issue);
    }

    public static List<ConferenceItem> toDomainItems(List<ConferenceItemJpaEntity> entities) {
        return entities.stream().map(ConferenceMapper::toDomain).toList();
    }
}
