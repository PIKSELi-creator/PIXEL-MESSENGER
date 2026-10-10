package com.pixelchat.server.security;

import com.pixelchat.server.model.SessionEntity;
import com.pixelchat.server.repository.SessionRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SessionTokenService {
    public static final long TOKEN_TTL_DAYS = 30;
    private final SessionRepository sessions;
    private final SecureRandom random = new SecureRandom();
    public SessionTokenService(SessionRepository sessions) { this.sessions = sessions; }

    @Transactional
    public IssuedToken issue(UUID accountId) {
        byte[] bytes = new byte[32]; random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        Instant expires = Instant.now().plus(TOKEN_TTL_DAYS, ChronoUnit.DAYS);
        SessionEntity session = new SessionEntity();
        session.id = UUID.randomUUID(); session.accountId = accountId; session.tokenHash = hash(token); session.createdAt = Instant.now(); session.expiresAt = expires;
        sessions.save(session);
        return new IssuedToken(token, expires);
    }

    @Transactional
    public void revoke(String token) {
        if (token == null || token.isBlank()) return;
        sessions.findFirstByTokenHashAndRevokedAtIsNull(hash(token)).ifPresent(s -> { s.revokedAt = Instant.now(); sessions.save(s); });
    }
    public static String hash(String token) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8))); }
        catch (Exception e) { throw new IllegalStateException("SHA-256 is unavailable", e); }
    }
    public record IssuedToken(String value, Instant expiresAt) {}
}
