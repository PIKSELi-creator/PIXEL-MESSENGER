import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordHasher {

    private static final int ITERATIONS = 210_000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    private PasswordHasher() {
    }

    public static String hash(String password) {

        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException(
                    "PASSWORD_EMPTY"
            );
        }

        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);

        byte[] hash = derive(
                password.toCharArray(),
                salt,
                ITERATIONS,
                KEY_LENGTH
        );

        return "pbkdf2$" +
                ITERATIONS +
                "$" +
                Base64.getEncoder().encodeToString(salt) +
                "$" +
                Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verify(
            String password,
            String stored
    ) {

        try {
            String[] parts =
                    stored.split("\\$", -1);

            if (parts.length != 4) {
                return false;
            }

            if (!parts[0].equals("pbkdf2")) {
                return false;
            }

            int iterations =
                    Integer.parseInt(parts[1]);

            byte[] salt =
                    Base64.getDecoder().decode(parts[2]);

            byte[] expected =
                    Base64.getDecoder().decode(parts[3]);

            byte[] actual =
                    derive(
                            password.toCharArray(),
                            salt,
                            iterations,
                            expected.length * 8
                    );

            return constantTimeEquals(
                    actual,
                    expected
            );

        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] derive(
            char[] password,
            byte[] salt,
            int iterations,
            int keyLength
    ) {

        try {
            PBEKeySpec spec =
                    new PBEKeySpec(
                            password,
                            salt,
                            iterations,
                            keyLength
                    );

            try {
                SecretKeyFactory factory =
                        SecretKeyFactory.getInstance(
                                "PBKDF2WithHmacSHA256"
                        );

                return factory
                        .generateSecret(spec)
                        .getEncoded();

            } finally {
                spec.clearPassword();
            }

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Не удалось вычислить парольный хэш",
                    e
            );
        }
    }

    private static boolean constantTimeEquals(
            byte[] a,
            byte[] b
    ) {

        if (a.length != b.length) {
            return false;
        }

        int result = 0;

        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }

        return result == 0;
    }
}
