package com.mlconf.core.domain.conference.model;

import com.mlconf.core.domain.common.DomainException;
import com.mlconf.core.domain.conference.model.enums.ItemState;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConferenceSessionRulesTest {

    @Test
    void importConferenceListCreatesPendingItems() {
        var session = openSession();

        var items = session.importConferenceList(List.of(), List.of("PKG-1", "PKG-2"));

        assertThat(items).hasSize(2);
        assertThat(items.get(0).getPackageId()).isEqualTo("PKG-1");
        assertThat(items.get(0).getState()).isEqualTo(ItemState.PENDING);
        assertThat(items.get(1).getPackageId()).isEqualTo("PKG-2");
        assertThat(items.get(1).getState()).isEqualTo(ItemState.PENDING);
    }

    @Test
    void importConferenceListRejectsSecondImportWhenItemsExist() {
        var session = openSession();
        var existing = List.of(new ConferenceItem("PKG-1", ItemState.PENDING, null));

        assertThatThrownBy(() -> session.importConferenceList(existing, List.of("PKG-1")))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void registerScanConfirmsPendingItem() {
        var session = openSession();
        var items = List.of(
                new ConferenceItem("PKG-1", ItemState.PENDING, null),
                new ConferenceItem("PKG-2", ItemState.PENDING, null)
        );

        var updated = session.registerScan(items, "PKG-2");

        assertThat(updated.get(0).getState()).isEqualTo(ItemState.PENDING);
        assertThat(updated.get(1).getState()).isEqualTo(ItemState.CONFIRMED);
    }

    @Test
    void registerScanIsIdempotentWhenAlreadyConfirmed() {
        var session = openSession();
        var items = List.of(
                new ConferenceItem("PKG-1", ItemState.PENDING, null),
                new ConferenceItem("PKG-2", ItemState.CONFIRMED, null)
        );

        var updated = session.registerScan(items, "PKG-2");

        assertThat(updated.get(1).getState()).isEqualTo(ItemState.CONFIRMED);
    }

    @Test
    void registerScanRejectsFinalizedSession() {
        var session = finalizedSession();
        var items = List.of(new ConferenceItem("PKG-1", ItemState.PENDING, null));

        assertThatThrownBy(() -> session.registerScan(items, "PKG-1"))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void registerScanRejectsUnknownPackageId() {
        var session = openSession();
        var items = List.of(new ConferenceItem("PKG-1", ItemState.PENDING, null));

        assertThatThrownBy(() -> session.registerScan(items, "PKG-404"))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void finalizeSessionTransformsPendingToMissing() {
        var session = openSession();
        var items = List.of(
                new ConferenceItem("PKG-1", ItemState.PENDING, null),
                new ConferenceItem("PKG-2", ItemState.CONFIRMED, null)
        );

        var now = Instant.parse("2026-01-18T21:00:00Z");
        var result = session.finalizeSession(items, now);

        assertThat(result.session().isFinalized()).isTrue();
        assertThat(result.session().getFinalizedAt()).isEqualTo(now);
        assertThat(result.items().get(0).getState()).isEqualTo(ItemState.MISSING);
        assertThat(result.items().get(1).getState()).isEqualTo(ItemState.CONFIRMED);
    }

    private static ConferenceSession openSession() {
        return new ConferenceSession(
                UUID.fromString("00000000-0000-0000-0000-000000000010"),
                ConferenceSession.Status.OPEN,
                Instant.parse("2026-01-18T19:00:00Z"),
                null
        );
    }

    private static ConferenceSession finalizedSession() {
        return new ConferenceSession(
                UUID.fromString("00000000-0000-0000-0000-000000000011"),
                ConferenceSession.Status.FINALIZED,
                Instant.parse("2026-01-18T19:00:00Z"),
                Instant.parse("2026-01-18T19:30:00Z")
        );
    }
}
