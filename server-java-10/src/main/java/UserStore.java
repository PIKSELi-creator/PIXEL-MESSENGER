import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserStore {

    private final Path file;

    private final Map<String, User> usersById =
            new ConcurrentHashMap<>();

    private final Map<String, String> idByEmail =
            new ConcurrentHashMap<>();

    private final Map<String, String> idByUsername =
            new ConcurrentHashMap<>();

    private final Object lock = new Object();

    public UserStore(Path file) throws IOException {
        this.file = file;

        Files.createDirectories(file.getParent());

        if (Files.exists(file)) {
            load();
        }
    }

    public Optional<User> findById(String id) {
        return Optional.ofNullable(usersById.get(id));
    }

    public Optional<User> findByEmail(String email) {
        String id = idByEmail.get(normalizeEmail(email));

        if (id == null) {
            return Optional.empty();
        }

        return findById(id);
    }

    public Optional<User> findByUsername(String username) {
        String id = idByUsername.get(
                normalizeUsername(username)
        );

        if (id == null) {
            return Optional.empty();
        }

        return findById(id);
    }

    public boolean emailExists(String email) {
        return idByEmail.containsKey(
                normalizeEmail(email)
        );
    }

    public boolean usernameExists(String username) {
        return idByUsername.containsKey(
                normalizeUsername(username)
        );
    }

    public void add(User user) throws IOException {
        synchronized (lock) {

            if (emailExists(user.getEmail())) {
                throw new IllegalArgumentException(
                        "EMAIL_ALREADY_EXISTS"
                );
            }

            if (usernameExists(user.getUsername())) {
                throw new IllegalArgumentException(
                        "USERNAME_ALREADY_EXISTS"
                );
            }

            usersById.put(user.getId(), user);

            idByEmail.put(
                    normalizeEmail(user.getEmail()),
                    user.getId()
            );

            idByUsername.put(
                    normalizeUsername(user.getUsername()),
                    user.getId()
            );

            save();
        }
    }

    public void update(User user) throws IOException {
        synchronized (lock) {
            usersById.put(user.getId(), user);
            save();
        }
    }

    private void load() throws IOException {

        for (String line :
                Files.readAllLines(
                        file,
                        StandardCharsets.UTF_8
                )) {

            if (line.isBlank()) {
                continue;
            }

            String[] p = line.split("\\|", -1);

            if (p.length != 6) {
                continue;
            }

            try {
                User user = new User(
                        p[0],
                        p[1],
                        p[2],
                        p[3],
                        Long.parseLong(p[4]),
                        Boolean.parseBoolean(p[5])
                );

                usersById.put(
                        user.getId(),
                        user
                );

                idByEmail.put(
                        normalizeEmail(user.getEmail()),
                        user.getId()
                );

                idByUsername.put(
                        normalizeUsername(user.getUsername()),
                        user.getId()
                );

            } catch (Exception ignored) {
                System.out.println(
                        "Пропущена повреждённая запись пользователя."
                );
            }
        }
    }

    private void save() throws IOException {

        Path temp =
                file.resolveSibling(
                        file.getFileName() + ".tmp"
                );

        List<String> lines = new ArrayList<>();

        for (User user : usersById.values()) {

            lines.add(
                    safe(user.getId()) + "|" +
                    safe(user.getEmail()) + "|" +
                    safe(user.getUsername()) + "|" +
                    safe(user.getPasswordHash()) + "|" +
                    user.getCreatedAt() + "|" +
                    user.isEmailVerified()
            );
        }

        Files.write(
                temp,
                lines,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );

        Files.move(
                temp,
                file,
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE
        );
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private static String normalizeUsername(String username) {
        return username.trim().toLowerCase(Locale.ROOT);
    }

    private static String safe(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("|", "\\|")
                .replace("\n", "")
                .replace("\r", "");
    }
}
