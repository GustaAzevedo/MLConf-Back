package com.mlconf.core.application.usecase;

import com.mlconf.core.application.dto.ScanRequestDTO;
import com.mlconf.core.application.usecase.impl.RegisterScanUseCaseImpl;
import com.mlconf.core.domain.common.DomainException;
import com.mlconf.core.domain.conference.model.ConferenceItem;
import com.mlconf.core.domain.conference.model.ConferenceSession;
import com.mlconf.core.domain.conference.model.enums.ItemState;
import com.mlconf.core.domain.conference.repository.ConferenceItemRepository;
import com.mlconf.core.domain.conference.repository.ConferenceSessionRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegisterScanUseCaseImplTest {

    @Test
    void registersScanAndReturnsSnapshot() {
        var sessionId = UUID.fromString("00000000-0000-0000-0000-000000000030");
        var session = new ConferenceSession(sessionId, ConferenceSession.Status.OPEN, Instant.parse("2026-01-18T22:20:00Z"), null);
        var sessionRepository = new InMemorySessionRepository(session);
        var itemRepository = new InMemoryItemRepository(
                new ConferenceItem("PKG-1", ItemState.PENDING, null),
                new ConferenceItem("PKG-2", ItemState.PENDING, null)
        );

        var useCase = new RegisterScanUseCaseImpl(sessionRepository, itemRepository);

        var snapshot = useCase.execute(sessionId, new ScanRequestDTO("PKG-2"));

        assertThat(snapshot.items()).hasSize(2);
        assertThat(snapshot.items().stream().filter(i -> i.packageId().equals("PKG-2")).findFirst().orElseThrow().state())
                .isEqualTo(ItemState.CONFIRMED);
    }

    @Test
    void rejectsWhenSessionNotFound() {
        var sessionRepository = new InMemorySessionRepository();
        var itemRepository = new InMemoryItemRepository();
        var useCase = new RegisterScanUseCaseImpl(sessionRepository, itemRepository);

        assertThatThrownBy(() -> useCase.execute(UUID.randomUUID(), new ScanRequestDTO("PKG-1")))
                .isInstanceOf(DomainException.class);
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
