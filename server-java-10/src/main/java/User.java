import java.util.UUID;

public class User {

    private final String id;
    private final String email;
    private final String username;
    private final String passwordHash;
    private final long createdAt;
    private boolean emailVerified;

    public User(
            String id,
            String email,
            String username,
            String passwordHash,
            long createdAt,
            boolean emailVerified
    ) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
        this.emailVerified = emailVerified;
    }

    public static User create(
            String email,
            String username,
            String passwordHash
    ) {
        return new User(
                UUID.randomUUID().toString(),
                email,
                username,
                passwordHash,
                System.currentTimeMillis(),
                false
        );
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean verified) {
        this.emailVerified = verified;
    }
}
