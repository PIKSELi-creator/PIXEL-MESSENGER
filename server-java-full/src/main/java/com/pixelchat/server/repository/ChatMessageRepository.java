package com.pixelchat.server.repository;

import com.pixelchat.server.model.ChatMessage;
import java.util.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    List<ChatMessage> findByChatIdOrderByCreatedAtDesc(UUID chatId, Pageable pageable);
    Optional<ChatMessage> findFirstByChatIdOrderByCreatedAtDesc(UUID chatId);
}
