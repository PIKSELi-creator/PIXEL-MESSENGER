package com.pixelchat.server.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "chat_member")
@IdClass(ChatMemberId.class)
public class ChatMember {
    @Id @Column(name = "chat_id") public UUID chatId;
    @Id @Column(name = "user_id") public UUID userId;
    @Column(nullable = false, length = 16) public String role;
    @Column(name = "joined_at", nullable = false) public Instant joinedAt;
    @PrePersist void prePersist() { if (joinedAt == null) joinedAt = Instant.now(); if (role == null) role = "MEMBER"; }
}
