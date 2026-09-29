import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class AuthService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
            );

    private static final Pattern USERNAME_PATTERN =
            Pattern.compile(
                    "^[A-Za-z0-9_]{3,20}$"
            );

    private final UserStore userStore;
    private final VerificationService verificationService;

    private final Map<String, PendingRegistration>
            pendingRegistrations =
            new ConcurrentHashMap<>();

    public AuthService(
            UserStore userStore,
            VerificationService verificationService
    ) {
        this.userStore = userStore;
        this.verificationService =
                verificationService;
    }

    public String startRegistration(
            String email,
            String username,
            String password
    ) throws Exception {

        email = normalizeEmail(email);
        username = username.trim();

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return "ERR|BAD_EMAIL";
        }

        if (!USERNAME_PATTERN.matcher(username).matches()) {
            return "ERR|BAD_USERNAME";
        }

        if (password == null ||
                password.length() < 8 ||
                password.length() > 128) {

            return "ERR|BAD_PASSWORD";
        }

        if (userStore.emailExists(email)) {
            return "ERR|EMAIL_EXISTS";
        }

        if (userStore.usernameExists(username)) {
            return "ERR|USERNAME_EXISTS";
        }

        String code =
                verificationService.createCode(email);

        String passwordHash =
                PasswordHasher.hash(password);

        pendingRegistrations.put(
                email,
                new PendingRegistration(
                        email,
                        username,
                        passwordHash
                )
        );

        try {

            GmailSender.sendVerificationCode(
                    email,
                    code
            );

        } catch (Exception e) {

            pendingRegistrations.remove(email);

            throw e;
        }

        return "OK|VERIFICATION_SENT";
    }

    public String verifyRegistration(
            String email,
            String code
    ) throws IOException {

        email = normalizeEmail(email);

        PendingRegistration pending =
                pendingRegistrations.get(email);

        if (pending == null) {
            return "ERR|REGISTRATION_NOT_FOUND";
        }

        if (!verificationService.verify(
                email,
                code
        )) {
            return "ERR|INVALID_VERIFICATION_CODE";
        }

        if (userStore.emailExists(email)) {
            pendingRegistrations.remove(email);
            return "ERR|EMAIL_EXISTS";
        }

        if (userStore.usernameExists(
                pending.username
        )) {
            pendingRegistrations.remove(email);
            return "ERR|USERNAME_EXISTS";
        }

        User user =
                User.create(
                        pending.email,
                        pending.username,
                        pending.passwordHash
                );

        user.setEmailVerified(true);

        userStore.add(user);

        pendingRegistrations.remove(email);

        return "OK|REGISTERED|" +
                user.getId();
    }

    public Optional<User> authenticate(
            String email,
            String password
    ) {

        email = normalizeEmail(email);

        Optional<User> result =
                userStore.findByEmail(email);

        if (result.isEmpty()) {
            return Optional.empty();
        }

        User user = result.get();

        if (!user.isEmailVerified()) {
            return Optional.empty();
        }

        if (!PasswordHasher.verify(
                password,
                user.getPasswordHash()
        )) {
            return Optional.empty();
        }

        return Optional.of(user);
    }

    private static String normalizeEmail(
            String email
    ) {
        return email
                .trim()
                .toLowerCase();
    }

    private static class PendingRegistration {

        final String email;
        final String username;
        final String passwordHash;

        PendingRegistration(
                String email,
                String username,
                String passwordHash
        ) {
            this.email = email;
            this.username = username;
            this.passwordHash =
                    passwordHash;
        }
    }
}
