package com.pixelchat.server.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "chat_room")
public class ChatRoom {
    @Id public UUID id;
    @Column(nullable = false, length = 16) public String kind;
    @Column(length = 100) public String title;
    @Column(name = "created_by", nullable = false) public UUID createdBy;
    @Column(name = "created_at", nullable = false) public Instant createdAt;
    @PrePersist void prePersist() { if (id == null) id = UUID.randomUUID(); if (createdAt == null) createdAt = Instant.now(); }
}
