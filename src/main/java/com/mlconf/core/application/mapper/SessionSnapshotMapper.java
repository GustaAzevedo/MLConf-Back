package com.mlconf.core.application.mapper;

import com.mlconf.core.application.dto.SessionSnapshotDTO;
import com.mlconf.core.domain.conference.model.ConferenceItem;
import com.mlconf.core.domain.conference.model.ConferenceSession;

import java.util.List;

public final class SessionSnapshotMapper {

    private SessionSnapshotMapper() {
    }

    public static SessionSnapshotDTO toSnapshot(ConferenceSession session, List<ConferenceItem> items) {
        var itemDtos = items.stream()
                .map(item -> new SessionSnapshotDTO.ItemDTO(
                        item.getPackageId(),
                        item.getState(),
                        item.getIssueCategory()
                ))
                .toList();

        return new SessionSnapshotDTO(
                session.getId(),
                session.getStatus().name(),
                session.getCreatedAt(),
                session.getFinalizedAt(),
                itemDtos
        );
    }
}
