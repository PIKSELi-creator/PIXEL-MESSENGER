package com.pixelchat.server.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class ChatMemberId implements Serializable {
    public UUID chatId;
    public UUID userId;
    public ChatMemberId() {}
    public ChatMemberId(UUID chatId, UUID userId) { this.chatId = chatId; this.userId = userId; }
    @Override public boolean equals(Object o) { if (this == o) return true; if (!(o instanceof ChatMemberId that)) return false; return Objects.equals(chatId, that.chatId) && Objects.equals(userId, that.userId); }
    @Override public int hashCode() { return Objects.hash(chatId, userId); }
}
