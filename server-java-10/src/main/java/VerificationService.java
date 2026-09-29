import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VerificationService {

    private static final long CODE_LIFETIME_MS =
            10 * 60 * 1000L;

    private static final int MAX_ATTEMPTS = 5;

    private static final long RESEND_COOLDOWN_MS =
            60 * 1000L;

    private final SecureRandom random =
            new SecureRandom();

    private final Map<String, Verification> codes =
            new ConcurrentHashMap<>();

    public String createCode(String email) {

        String key = normalize(email);

        Verification previous = codes.get(key);

        long now = System.currentTimeMillis();

        if (previous != null &&
                now - previous.createdAt <
                        RESEND_COOLDOWN_MS) {

            throw new IllegalStateException(
                    "RESEND_TOO_SOON"
            );
        }

        int codeNumber =
                100000 + random.nextInt(900000);

        String code =
                String.valueOf(codeNumber);

        Verification verification =
                new Verification(
                        hash(code),
                        now
                );

        codes.put(key, verification);

        return code;
    }

    public boolean verify(
            String email,
            String code
    ) {

        String key = normalize(email);

        Verification verification =
                codes.get(key);

        if (verification == null) {
            return false;
        }

        long now =
                System.currentTimeMillis();

        if (now - verification.createdAt >
                CODE_LIFETIME_MS) {

            codes.remove(key);
            return false;
        }

        if (verification.attempts >=
                MAX_ATTEMPTS) {

            codes.remove(key);
            return false;
        }

        verification.attempts++;

        boolean valid =
                constantTimeEquals(
                        verification.codeHash,
                        hash(code)
                );

        if (valid) {
            codes.remove(key);
        }

        return valid;
    }

    public long getRemainingSeconds(
            String email
    ) {

        Verification verification =
                codes.get(normalize(email));

        if (verification == null) {
            return 0;
        }

        long remaining =
                CODE_LIFETIME_MS -
                (System.currentTimeMillis()
                        - verification.createdAt);

        return Math.max(
                0,
                remaining / 1000
        );
    }

    private static String normalize(
            String email
    ) {
        return email
                .trim()
                .toLowerCase();
    }

    private static String hash(
            String value
    ) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance(
                            "SHA-256"
                    );

            byte[] result =
                    digest.digest(
                            value.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            return Base64.getEncoder()
                    .encodeToString(result);

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static boolean constantTimeEquals(
            String a,
            String b
    ) {

        return MessageDigest.isEqual(
                a.getBytes(StandardCharsets.UTF_8),
                b.getBytes(StandardCharsets.UTF_8)
        );
    }

    private static class Verification {

        final String codeHash;
        final long createdAt;

        int attempts;

        Verification(
                String codeHash,
                long createdAt
        ) {
            this.codeHash = codeHash;
            this.createdAt = createdAt;
        }
    }
}
