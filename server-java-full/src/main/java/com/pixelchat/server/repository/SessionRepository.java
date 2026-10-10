package com.pixelchat.server.repository;

import com.pixelchat.server.model.SessionEntity;
import java.time.Instant;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<SessionEntity, UUID> {
    Optional<SessionEntity> findFirstByTokenHashAndRevokedAtIsNullAndExpiresAtAfter(String tokenHash, Instant now);
    Optional<SessionEntity> findFirstByTokenHashAndRevokedAtIsNull(String tokenHash);
}
