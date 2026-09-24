import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PublicKeyStore {
    private final Path file;
    private final Map<String, String> keys = new ConcurrentHashMap<>();
    private final Object lock = new Object();

    public PublicKeyStore(Path file) throws IOException {
        this.file = file;
        Path parent = file.getParent();
        if (parent != null) Files.createDirectories(parent);
        if (Files.exists(file)) load();
    }

    public Optional<String> get(String username) {
        if (username == null || username.isBlank()) return Optional.empty();
        return Optional.ofNullable(keys.get(normalize(username)));
    }

    public void put(String username, String publicKey) throws IOException {
        String user = normalize(username);
        if (user.isBlank()) throw new IllegalArgumentException("INVALID_USERNAME");
        if (publicKey == null || publicKey.isBlank()) {
            throw new IllegalArgumentException("INVALID_PUBLIC_KEY");
        }
        synchronized (lock) {
            keys.put(user, clean(publicKey));
            save();
        }
    }

    public boolean remove(String username) throws IOException {
        synchronized (lock) {
            boolean removed = keys.remove(normalize(username)) != null;
            if (removed) save();
            return removed;
        }
    }

    public int size() { return keys.size(); }

    private void load() throws IOException {
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            if (line.isBlank()) continue;
            String[] parts = line.split("\\|", 2);
            if (parts.length != 2) continue;
            keys.put(normalize(parts[0]), unescape(parts[1]));
        }
    }

    private void save() throws IOException {
        Path temp = file.resolveSibling(file.getFileName() + ".tmp");
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, String> e : keys.entrySet()) {
            lines.add(escape(e.getKey()) + "|" + escape(e.getValue()));
        }
        Files.write(temp, lines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        try {
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private static String clean(String value) {
        return value.replace("\n", " ").replace("\r", " ").trim();
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }

    private static String unescape(String value) {
        StringBuilder result = new StringBuilder();
        boolean escaped = false;
        for (char c : value.toCharArray()) {
            if (escaped) {
                result.append(c);
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else {
                result.append(c);
            }
        }
        if (escaped) result.append('\\');
        return result.toString();
    }
}
