package com.mlconf.core.domain.conference.model;

import com.mlconf.core.domain.common.DomainException;
import com.mlconf.core.domain.conference.model.enums.IssueCategory;
import com.mlconf.core.domain.conference.model.enums.ItemState;

import java.util.Objects;

public final class ConferenceItem {

    private final String packageId;
    private final ItemState state;
    private final IssueCategory issueCategory;

    public ConferenceItem(String packageId, ItemState state, IssueCategory issueCategory) {
        if (packageId == null || packageId.isBlank()) {
            throw new DomainException("packageId must not be blank");
        }
        this.packageId = packageId;
        this.state = Objects.requireNonNull(state, "state must not be null");
        this.issueCategory = issueCategory;
    }

    public String getPackageId() {
        return packageId;
    }

    public ItemState getState() {
        return state;
    }

    public IssueCategory getIssueCategory() {
        return issueCategory;
    }
}
