package com.pixelchat.server.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "app_session")
public class SessionEntity {
    @Id public UUID id;
    @Column(name = "account_id", nullable = false) public UUID accountId;
    @Column(name = "token_hash", nullable = false, unique = true, length = 64) public String tokenHash;
    @Column(name = "created_at", nullable = false) public Instant createdAt;
    @Column(name = "expires_at", nullable = false) public Instant expiresAt;
    @Column(name = "revoked_at") public Instant revokedAt;
    @PrePersist void prePersist() { if (id == null) id = UUID.randomUUID(); if (createdAt == null) createdAt = Instant.now(); }
}
