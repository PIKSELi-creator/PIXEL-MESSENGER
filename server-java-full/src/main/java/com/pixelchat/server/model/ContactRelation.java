package com.pixelchat.server.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "contact_relation")
public class ContactRelation {
    @Id public UUID id;
    @Column(name = "owner_id", nullable = false) public UUID ownerId;
    @Column(name = "contact_id", nullable = false) public UUID contactId;
    @Column(nullable = false, length = 24) public String state;
    @Column(name = "created_at", nullable = false) public Instant createdAt;
    @PrePersist void prePersist() { if (id == null) id = UUID.randomUUID(); if (createdAt == null) createdAt = Instant.now(); if (state == null) state = "PENDING"; }
}
