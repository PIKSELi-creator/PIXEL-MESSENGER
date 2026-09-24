import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class MessageStore {
    private final Path file;
    private final List<Message> messages = new CopyOnWriteArrayList<>();
    private final Object lock = new Object();

    public MessageStore(Path file) throws IOException {
        this.file = file;
        Path parent = file.getParent();
        if (parent != null) Files.createDirectories(parent);
        if (Files.exists(file)) load();
    }

    public List<Message> findConversation(String userA, String userB) {
        List<Message> result = new ArrayList<>();
        for (Message message : messages) {
            boolean direct = message.getSenderId().equals(userA)
                    && message.getRecipientId().equals(userB);
            boolean reverse = message.getSenderId().equals(userB)
                    && message.getRecipientId().equals(userA);
            if (direct || reverse) result.add(message);
        }
        result.sort(Comparator.comparingLong(Message::getCreatedAt));
        return result;
    }

    public void add(Message message) throws IOException {
        synchronized (lock) {
            messages.add(message);
            save();
        }
    }

    public void markRead(String messageId, String recipientId) throws IOException {
        synchronized (lock) {
            for (Message message : messages) {
                if (message.getId().equals(messageId)
                        && message.getRecipientId().equals(recipientId)) {
                    message.setRead(true);
                    save();
                    return;
                }
            }
        }
    }

    private void load() throws IOException {
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            if (line.isBlank()) continue;
            String[] p = line.split("\\|", -1);
            if (p.length != 6) continue;
            try {
                messages.add(new Message(
                        p[0], p[1], p[2], Long.parseLong(p[3]), p[4],
                        Boolean.parseBoolean(p[5])));
            } catch (Exception ignored) {
                System.out.println("Пропущена повреждённая запись сообщения.");
            }
        }
    }

    private void save() throws IOException {
        Path temp = file.resolveSibling(file.getFileName() + ".tmp");
        List<String> lines = new ArrayList<>();
        for (Message message : messages) {
            lines.add(safe(message.getId()) + "|"
                    + safe(message.getSenderId()) + "|"
                    + safe(message.getRecipientId()) + "|"
                    + message.getCreatedAt() + "|"
                    + safe(message.getEncryptedPayload()) + "|"
                    + message.isRead());
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

    private static String safe(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|")
                .replace("\n", "").replace("\r", "");
    }
}
