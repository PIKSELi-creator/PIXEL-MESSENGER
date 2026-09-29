import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SessionStore {

    private static final long SESSION_LIFETIME_MS =
            30L * 24 * 60 * 60 * 1000;

    private static final SecureRandom RANDOM =
            new SecureRandom();

    private final Path file;

    private final Map<String, SessionData> sessions =
            new ConcurrentHashMap<>();

    public SessionStore(Path file) throws IOException {
        this.file = file;

        Path parent = file.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        if (Files.exists(file)) {
            load();
        }
    }

    public synchronized String create(String userId)
            throws IOException {

        String token = generateToken();
        String tokenHash = hashToken(token);

        long expiresAt =
                System.currentTimeMillis()
                        + SESSION_LIFETIME_MS;

        sessions.put(
                tokenHash,
                new SessionData(
                        userId,
                        expiresAt
                )
        );

        save();

        return token;
    }

    public Optional<String> getUserId(String token)
            throws IOException {

        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        String tokenHash = hashToken(token);

        SessionData session =
                sessions.get(tokenHash);

        if (session == null) {
            return Optional.empty();
        }

        if (session.expiresAt <
                System.currentTimeMillis()) {

            synchronized (this) {
                sessions.remove(tokenHash);
                save();
            }

            return Optional.empty();
        }

        return Optional.of(session.userId);
    }

    public synchronized void invalidate(String token)
            throws IOException {

        if (token == null || token.isBlank()) {
            return;
        }

        sessions.remove(hashToken(token));
        save();
    }

    private String generateToken() {

        byte[] bytes = new byte[32];

        RANDOM.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private static String hashToken(String token) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            token.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            StringBuilder result =
                    new StringBuilder();

            for (byte b : hash) {

                result.append(
                        String.format(
                                "%02x",
                                b
                        )
                );
            }

            return result.toString();

        } catch (Exception e) {

            throw new IllegalStateException(
                    "SESSION_HASH_ERROR",
                    e
            );
        }
    }

    private synchronized void save()
            throws IOException {

        Path temp =
                file.resolveSibling(
                        file.getFileName()
                                + ".tmp"
                );

        long now =
                System.currentTimeMillis();

        List<String> lines =
                new ArrayList<>();

        for (Map.Entry<String, SessionData> entry :
                sessions.entrySet()) {

            SessionData session =
                    entry.getValue();

            if (session.expiresAt > now) {

                lines.add(
                        entry.getKey()
                                + "|"
                                + session.userId
                                + "|"
                                + session.expiresAt
                );
            }
        }

        Files.write(
                temp,
                lines,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );

        try {

            Files.move(
                    temp,
                    file,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
            );

        } catch (AtomicMoveNotSupportedException e) {

            Files.move(
                    temp,
                    file,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }

    private void load()
            throws IOException {

        List<String> lines =
                Files.readAllLines(
                        file,
                        StandardCharsets.UTF_8
                );

        long now =
                System.currentTimeMillis();

        for (String line : lines) {

            String[] parts =
                    line.split("\\|", -1);

            if (parts.length != 3) {
                continue;
            }

            try {

                String tokenHash = parts[0];
                String userId = parts[1];

                long expiresAt =
                        Long.parseLong(parts[2]);

                if (expiresAt > now &&
                        tokenHash.matches("[a-f0-9]{64}")) {

                    sessions.put(
                            tokenHash,
                            new SessionData(
                                    userId,
                                    expiresAt
                            )
                    );
                }

            } catch (Exception ignored) {
                // Повреждённые записи пропускаем.
            }
        }
    }

    private static class SessionData {

        final String userId;
        final long expiresAt;

        SessionData(
                String userId,
                long expiresAt
        ) {
            this.userId = userId;
            this.expiresAt = expiresAt;
        }
    }
}
