package com.pixelchat.server.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "chat_message")
public class ChatMessage {
    @Id public UUID id;
    @Column(name = "chat_id", nullable = false) public UUID chatId;
    @Column(name = "sender_id", nullable = false) public UUID senderId;
    @Column(nullable = false, columnDefinition = "text") public String payload;
    @Column(name = "payload_type", nullable = false, length = 24) public String payloadType;
    @Column(name = "reply_to") public UUID replyTo;
    @Column(name = "edited_at") public Instant editedAt;
    @Column(name = "deleted_at") public Instant deletedAt;
    @Column(name = "created_at", nullable = false) public Instant createdAt;
    @PrePersist void prePersist() { if (id == null) id = UUID.randomUUID(); if (createdAt == null) createdAt = Instant.now(); if (payloadType == null) payloadType = "TEXT"; }
}
