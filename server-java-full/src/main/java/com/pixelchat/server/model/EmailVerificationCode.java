package com.pixelchat.server.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "email_verification_code")
public class EmailVerificationCode {
    @Id public UUID id;
    @Column(name = "account_id", nullable = false) public UUID accountId;
    @Column(name = "code_hash", nullable = false, length = 255) public String codeHash;
    @Column(name = "created_at", nullable = false) public Instant createdAt;
    @Column(name = "expires_at", nullable = false) public Instant expiresAt;
    @Column(name = "used_at") public Instant usedAt;
    @Column(nullable = false) public int attempts;
    @PrePersist void prePersist() { if (id == null) id = UUID.randomUUID(); if (createdAt == null) createdAt = Instant.now(); }
}
