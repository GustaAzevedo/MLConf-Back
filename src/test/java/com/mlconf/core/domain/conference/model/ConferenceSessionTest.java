package com.mlconf.core.domain.conference.model;

import com.mlconf.core.domain.common.DomainException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConferenceSessionTest {

    @Test
    void createsOpenSession() {
        var id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        var createdAt = Instant.parse("2026-01-03T00:00:00Z");

        var session = new ConferenceSession(id, ConferenceSession.Status.OPEN, createdAt, null);

        assertThat(session.getId()).isEqualTo(id);
        assertThat(session.getStatus()).isEqualTo(ConferenceSession.Status.OPEN);
        assertThat(session.getCreatedAt()).isEqualTo(createdAt);
        assertThat(session.getFinalizedAt()).isNull();
    }

    @Test
    void rejectsOpenSessionWithFinalizedAt() {
        var id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        var createdAt = Instant.parse("2026-01-03T00:00:00Z");
        var finalizedAt = Instant.parse("2026-01-03T01:00:00Z");

        assertThatThrownBy(() -> new ConferenceSession(id, ConferenceSession.Status.OPEN, createdAt, finalizedAt))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void rejectsFinalizedSessionWithoutFinalizedAt() {
        var id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        var createdAt = Instant.parse("2026-01-03T00:00:00Z");

        assertThatThrownBy(() -> new ConferenceSession(id, ConferenceSession.Status.FINALIZED, createdAt, null))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void rejectsNullId() {
        var createdAt = Instant.parse("2026-01-03T00:00:00Z");

        assertThatThrownBy(() -> new ConferenceSession(null, ConferenceSession.Status.OPEN, createdAt, null))
                .isInstanceOf(NullPointerException.class);
    }
}
