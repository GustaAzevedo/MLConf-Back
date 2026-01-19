package com.mlconf.adapters.out.persistence;

import com.mlconf.core.domain.conference.model.ConferenceItem;
import com.mlconf.core.domain.conference.model.ConferenceSession;
import com.mlconf.core.domain.conference.model.enums.ItemState;
import com.mlconf.core.domain.conference.repository.ConferenceItemRepository;
import com.mlconf.core.domain.conference.repository.ConferenceSessionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({ConferenceSessionRepositoryAdapter.class, ConferenceItemRepositoryAdapter.class})
class ConferencePersistenceAdaptersTest {

    @Autowired
    private ConferenceSessionRepository sessionRepository;

    @Autowired
    private ConferenceItemRepository itemRepository;

    @Test
    void savesAndLoadsSession() {
        var sessionId = UUID.fromString("00000000-0000-0000-0000-000000000100");
        var session = new ConferenceSession(sessionId, ConferenceSession.Status.OPEN, Instant.parse("2026-01-18T23:00:00Z"), null);

        sessionRepository.save(session);
        var loaded = sessionRepository.findById(sessionId);

        assertThat(loaded).isPresent();
        assertThat(loaded.get().getStatus()).isEqualTo(ConferenceSession.Status.OPEN);
    }

    @Test
    void savesAndLoadsItemsBySession() {
        var sessionId = UUID.fromString("00000000-0000-0000-0000-000000000101");
        var session = new ConferenceSession(sessionId, ConferenceSession.Status.OPEN, Instant.parse("2026-01-18T23:05:00Z"), null);
        sessionRepository.save(session);

        itemRepository.saveAll(sessionId, List.of(
                new ConferenceItem("PKG-1", ItemState.PENDING, null),
                new ConferenceItem("PKG-2", ItemState.CONFIRMED, null)
        ));

        var items = itemRepository.findAllBySessionId(sessionId);

        assertThat(items).hasSize(2);
        assertThat(items.stream().anyMatch(i -> i.getPackageId().equals("PKG-2") && i.getState() == ItemState.CONFIRMED))
                .isTrue();
    }
}
