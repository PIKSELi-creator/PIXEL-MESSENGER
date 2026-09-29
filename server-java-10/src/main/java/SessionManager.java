import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final long SESSION_LIFETIME_MS =
            30L * 24L * 60L * 60L * 1000L;

    private static final int TOKEN_BYTES = 32;

    private final SecureRandom random =
            new SecureRandom();

    private final Map<String, Session> sessions =
            new ConcurrentHashMap<>();

    public String createSession(User user) {

        cleanupExpired();

        String token = generateToken();

        Session session =
                new Session(
                        token,
                        user.getId(),
                        System.currentTimeMillis()
                );

        sessions.put(token, session);

        return token;
    }

    public Optional<String> getUserId(
            String token
    ) {

        if (token == null ||
                token.isBlank()) {
            return Optional.empty();
        }

        Session session =
                sessions.get(token);

        if (session == null) {
            return Optional.empty();
        }

        if (isExpired(session)) {
            sessions.remove(token);
            return Optional.empty();
        }

        return Optional.of(session.userId);
    }

    public boolean isValid(String token) {
        return getUserId(token).isPresent();
    }

    public void invalidate(String token) {

        if (token != null) {
            sessions.remove(token);
        }
    }

    public int activeSessions() {
        cleanupExpired();
        return sessions.size();
    }

    private String generateToken() {

        byte[] bytes =
                new byte[TOKEN_BYTES];

        random.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private boolean isExpired(
            Session session
    ) {

        return System.currentTimeMillis()
                - session.createdAt
                > SESSION_LIFETIME_MS;
    }

    private void cleanupExpired() {

        long now =
                System.currentTimeMillis();

        sessions.entrySet().removeIf(
                entry ->
                        now - entry.getValue().createdAt
                                > SESSION_LIFETIME_MS
        );
    }

    private static class Session {

        final String token;
        final String userId;
        final long createdAt;

        Session(
                String token,
                String userId,
                long createdAt
        ) {
            this.token = token;
            this.userId = userId;
            this.createdAt = createdAt;
        }
    }
}
