import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String senderId;
    private final String recipientId;
    private final long createdAt;
    private final String encryptedPayload;
    private boolean read;

    public Message(String id, String senderId, String recipientId,
                   long createdAt, String encryptedPayload, boolean read) {
        this.id = id;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.createdAt = createdAt;
        this.encryptedPayload = encryptedPayload;
        this.read = read;
    }

    public static Message create(String senderId, String recipientId,
                                  String encryptedPayload) {
        return new Message(UUID.randomUUID().toString(), senderId, recipientId,
                System.currentTimeMillis(), encryptedPayload, false);
    }

    public String getId() { return id; }
    public String getSenderId() { return senderId; }
    public String getRecipientId() { return recipientId; }
    public long getCreatedAt() { return createdAt; }
    public String getEncryptedPayload() { return encryptedPayload; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
}
