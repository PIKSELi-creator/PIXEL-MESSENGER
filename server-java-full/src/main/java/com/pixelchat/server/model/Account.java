package com.pixelchat.server.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "app_account")
public class Account {
    @Id public UUID id;
    @Column(nullable = false, unique = true, length = 320) public String email;
    @Column(nullable = false, unique = true, length = 32) public String username;
    @Column(name = "display_name", nullable = false, length = 64) public String displayName;
    @Column(name = "password_hash", nullable = false, length = 255) public String passwordHash;
    @Column(name = "email_verified", nullable = false) public boolean emailVerified;
    @Column(nullable = false, length = 280) public String bio = "";
    @Column(name = "avatar_url", length = 2048) public String avatarUrl;
    @Column(name = "created_at", nullable = false) public Instant createdAt;
    @Column(name = "updated_at", nullable = false) public Instant updatedAt;

    @PrePersist void prePersist() {
        Instant now = Instant.now();
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = now;
        updatedAt = now;
        if (bio == null) bio = "";
    }
    @PreUpdate void preUpdate() { updatedAt = Instant.now(); }
}
