package com.pixelchat.server.repository;

import com.pixelchat.server.model.ChatRoom;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {
    @Query(value = "SELECT c.* FROM chat_room c WHERE c.kind = 'DIRECT' AND EXISTS (SELECT 1 FROM chat_member m WHERE m.chat_id=c.id AND m.user_id=:a) AND EXISTS (SELECT 1 FROM chat_member m WHERE m.chat_id=c.id AND m.user_id=:b) AND (SELECT COUNT(*) FROM chat_member m WHERE m.chat_id=c.id)=2 ORDER BY c.created_at LIMIT 1", nativeQuery = true)
    Optional<ChatRoom> findDirectChat(@Param("a") UUID a, @Param("b") UUID b);
}
