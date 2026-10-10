package com.pixelchat.server.repository;

import com.pixelchat.server.model.ChatMember;
import com.pixelchat.server.model.ChatMemberId;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMemberRepository extends JpaRepository<ChatMember, ChatMemberId> {
    boolean existsByChatIdAndUserId(UUID chatId, UUID userId);
    List<ChatMember> findByUserIdOrderByJoinedAtDesc(UUID userId);
    List<ChatMember> findByChatId(UUID chatId);
}
