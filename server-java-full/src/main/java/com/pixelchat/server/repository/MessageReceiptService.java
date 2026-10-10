package com.pixelchat.server.repository;

import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;

@Repository
public class MessageReceiptService {
    private final JdbcTemplate jdbc;
    public MessageReceiptService(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    @Transactional
    public int markMessagesRead(UUID chatId, UUID userId) {
        return jdbc.update("INSERT INTO message_receipt (message_id, user_id, delivered_at, read_at) " +
            "SELECT m.id, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM chat_message m " +
            "WHERE m.chat_id = ? AND m.sender_id <> ? AND m.deleted_at IS NULL " +
            "ON CONFLICT (message_id, user_id) DO UPDATE SET " +
            "delivered_at = COALESCE(message_receipt.delivered_at, EXCLUDED.delivered_at), read_at = EXCLUDED.read_at",
            userId, chatId, userId);
    }
}
