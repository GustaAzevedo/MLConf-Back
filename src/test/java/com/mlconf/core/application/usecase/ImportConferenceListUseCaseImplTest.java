package com.mlconf.core.application.usecase;

import com.mlconf.core.application.dto.ImportRequestDTO;
import com.mlconf.core.application.usecase.impl.ImportConferenceListUseCaseImpl;
import com.mlconf.core.application.error.NotFoundException;
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

class ImportConferenceListUseCaseImplTest {

    @Test
    void importsConferenceListAndReturnsSnapshot() {
        var sessionId = UUID.fromString("00000000-0000-0000-0000-000000000020");
        var session = new ConferenceSession(sessionId, ConferenceSession.Status.OPEN, Instant.parse("2026-01-18T22:10:00Z"), null);
        var sessionRepository = new InMemorySessionRepository(session);
        var itemRepository = new InMemoryItemRepository();

        var useCase = new ImportConferenceListUseCaseImpl(sessionRepository, itemRepository);

        var snapshot = useCase.execute(sessionId, new ImportRequestDTO(List.of("PKG-1", "PKG-2")));

        assertThat(snapshot.sessionId()).isEqualTo(sessionId);
        assertThat(snapshot.items()).hasSize(2);
        assertThat(snapshot.items().get(0).state()).isEqualTo(ItemState.PENDING);
        assertThat(itemRepository.savedAll).hasSize(1);
    }

    @Test
    void rejectsWhenSessionNotFound() {
        var sessionRepository = new InMemorySessionRepository();
        var itemRepository = new InMemoryItemRepository();
        var useCase = new ImportConferenceListUseCaseImpl(sessionRepository, itemRepository);

        assertThatThrownBy(() -> useCase.execute(UUID.randomUUID(), new ImportRequestDTO(List.of("PKG-1"))))
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
        private final List<List<ConferenceItem>> savedAll = new ArrayList<>();

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
            savedAll.add(new ArrayList<>(newItems));
        }
    }
}
