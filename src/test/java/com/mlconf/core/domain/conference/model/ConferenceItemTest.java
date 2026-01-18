package com.mlconf.core.domain.conference.model;

import com.mlconf.core.domain.common.DomainException;
import com.mlconf.core.domain.conference.model.enums.ItemState;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConferenceItemTest {

    @Test
    void createsItem() {
        var item = new ConferenceItem("PKG-1", ItemState.PENDING, null);

        assertThat(item.getPackageId()).isEqualTo("PKG-1");
        assertThat(item.getState()).isEqualTo(ItemState.PENDING);
        assertThat(item.getIssueCategory()).isNull();
    }

    @Test
    void rejectsBlankPackageId() {
        assertThatThrownBy(() -> new ConferenceItem(" ", ItemState.PENDING, null))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void rejectsNullState() {
        assertThatThrownBy(() -> new ConferenceItem("PKG-1", null, null))
                .isInstanceOf(NullPointerException.class);
    }
}
