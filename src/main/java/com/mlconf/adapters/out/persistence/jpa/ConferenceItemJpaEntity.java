package com.mlconf.adapters.out.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.UUID;

@Entity
@Table(
        name = "conference_item",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_conference_item_session_package", columnNames = {"session_id", "package_id"})
        },
        indexes = {
                @Index(name = "idx_conference_item_session", columnList = "session_id")
        }
)
public class ConferenceItemJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private UUID sessionId;

    @Column(name = "package_id", nullable = false, length = 80)
    private String packageId;

    @Column(name = "state", nullable = false, length = 20)
    private String state;

    @Column(name = "issue_category", length = 50)
    private String issueCategory;

    protected ConferenceItemJpaEntity() {
    }

    public ConferenceItemJpaEntity(UUID sessionId, String packageId, String state, String issueCategory) {
        this.sessionId = sessionId;
        this.packageId = packageId;
        this.state = state;
        this.issueCategory = issueCategory;
    }

    public Long getId() {
        return id;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public String getPackageId() {
        return packageId;
    }

    public String getState() {
        return state;
    }

    public String getIssueCategory() {
        return issueCategory;
    }
}
