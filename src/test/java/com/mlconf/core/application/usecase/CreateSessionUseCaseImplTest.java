package com.mlconf.core.application.usecase;

import com.mlconf.core.application.usecase.impl.CreateSessionUseCaseImpl;
import com.mlconf.core.domain.conference.model.ConferenceItem;
import com.mlconf.core.domain.conference.model.ConferenceSession;
import com.mlconf.core.domain.conference.model.enums.ItemState;
import com.mlconf.core.domain.conference.repository.ConferenceItemRepository;
import com.mlconf.core.domain.conference.repository.ConferenceSessionRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CreateSessionUseCaseImplTest {

    @Test
    void createsSessionAndReturnsSnapshot() {
        var clock = Clock.fixed(Instant.parse("2026-01-18T22:00:00Z"), ZoneOffset.UTC);
        var sessionRepository = new InMemorySessionRepository();
        var itemRepository = new InMemoryItemRepository();

        var useCase = new CreateSessionUseCaseImpl(sessionRepository, itemRepository, clock);

        var snapshot = useCase.execute();

        assertThat(snapshot.sessionId()).isNotNull();
        assertThat(snapshot.status()).isEqualTo("OPEN");
        assertThat(snapshot.createdAt()).isEqualTo(Instant.parse("2026-01-18T22:00:00Z"));
        assertThat(snapshot.finalizedAt()).isNull();
        assertThat(snapshot.items()).isEmpty();
        assertThat(sessionRepository.saved).hasSize(1);
    }

    private static final class InMemorySessionRepository implements ConferenceSessionRepository {
        private final List<ConferenceSession> saved = new ArrayList<>();

        @Override
        public Optional<ConferenceSession> findById(UUID sessionId) {
            return saved.stream().filter(s -> s.getId().equals(sessionId)).findFirst();
        }

        @Override
        public ConferenceSession save(ConferenceSession session) {
            saved.add(session);
            return session;
        }
    }

    private static final class InMemoryItemRepository implements ConferenceItemRepository {

        @Override
        public Optional<ConferenceItem> findBySessionIdAndPackageId(UUID sessionId, String packageId) {
            return Optional.empty();
        }

        @Override
        public List<ConferenceItem> findAllBySessionId(UUID sessionId) {
            return List.of();
        }

        @Override
        public void save(UUID sessionId, ConferenceItem item) {
        }

        @Override
        public void saveAll(UUID sessionId, java.util.Collection<ConferenceItem> items) {
        }
    }
}
