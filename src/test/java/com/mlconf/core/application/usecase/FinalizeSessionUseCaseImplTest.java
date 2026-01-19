package com.mlconf.core.application.usecase;

import com.mlconf.core.application.error.NotFoundException;
import com.mlconf.core.application.usecase.impl.FinalizeSessionUseCaseImpl;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FinalizeSessionUseCaseImplTest {

    @Test
    void finalizesSessionAndReturnsSnapshot() {
        var sessionId = UUID.fromString("00000000-0000-0000-0000-000000000040");
        var session = new ConferenceSession(sessionId, ConferenceSession.Status.OPEN, Instant.parse("2026-01-18T22:30:00Z"), null);
        var sessionRepository = new InMemorySessionRepository(session);
        var itemRepository = new InMemoryItemRepository(
                new ConferenceItem("PKG-1", ItemState.PENDING, null),
                new ConferenceItem("PKG-2", ItemState.CONFIRMED, null)
        );
        var clock = Clock.fixed(Instant.parse("2026-01-18T22:40:00Z"), ZoneOffset.UTC);

        var useCase = new FinalizeSessionUseCaseImpl(sessionRepository, itemRepository, clock);

        var snapshot = useCase.execute(sessionId);

        assertThat(snapshot.sessionId()).isEqualTo(sessionId);
        assertThat(snapshot.finalizedAt()).isEqualTo(Instant.parse("2026-01-18T22:40:00Z"));
        assertThat(snapshot.items().stream().filter(i -> i.packageId().equals("PKG-1")).findFirst().orElseThrow().state())
                .isEqualTo(ItemState.MISSING);
    }

    @Test
    void rejectsWhenSessionNotFound() {
        var sessionRepository = new InMemorySessionRepository();
        var itemRepository = new InMemoryItemRepository();
        var clock = Clock.systemUTC();
        var useCase = new FinalizeSessionUseCaseImpl(sessionRepository, itemRepository, clock);

        assertThatThrownBy(() -> useCase.execute(UUID.randomUUID()))
                .isInstanceOf(NotFoundException.class);
    }

    private static final class InMemorySessionRepository implements ConferenceSessionRepository {
        private final List<ConferenceSession> saved = new ArrayList<>();

        private InMemorySessionRepository(ConferenceSession... sessions) {
            saved.addAll(List.of(sessions));
        }

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
        private final List<ConferenceItem> items = new ArrayList<>();

        private InMemoryItemRepository(ConferenceItem... seed) {
            items.addAll(List.of(seed));
        }

        @Override
        public Optional<ConferenceItem> findBySessionIdAndPackageId(UUID sessionId, String packageId) {
            return items.stream().filter(i -> i.getPackageId().equals(packageId)).findFirst();
        }

        @Override
        public List<ConferenceItem> findAllBySessionId(UUID sessionId) {
            return new ArrayList<>(items);
        }

        @Override
        public void save(UUID sessionId, ConferenceItem item) {
            items.removeIf(i -> i.getPackageId().equals(item.getPackageId()));
            items.add(item);
        }

        @Override
        public void saveAll(UUID sessionId, java.util.Collection<ConferenceItem> newItems) {
            items.clear();
            items.addAll(newItems);
        }
    }
}
