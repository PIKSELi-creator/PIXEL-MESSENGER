import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Base64;

public class MessageService {
    private static final int MAX_PAYLOAD_LENGTH = 16000;

    private final UserStore userStore;
    private final SessionStore sessionStore;
    private final MessageStore messageStore;

    public MessageService(UserStore userStore, SessionStore sessionStore,
                          MessageStore messageStore) {
        this.userStore = userStore;
        this.sessionStore = sessionStore;
        this.messageStore = messageStore;
    }

    public String sendMessage(String sessionToken, String recipientRef,
                              String encryptedPayload) throws Exception {
        Optional<String> senderId = sessionStore.getUserId(sessionToken);
        if (senderId.isEmpty()) return "ERR|INVALID_SESSION";
        if (recipientRef == null || recipientRef.isBlank()) return "ERR|BAD_RECIPIENT";
        if (encryptedPayload == null || encryptedPayload.isBlank()
                || encryptedPayload.length() > MAX_PAYLOAD_LENGTH) {
            return "ERR|BAD_PAYLOAD";
        }
        try {
            Base64.getDecoder().decode(encryptedPayload);
        } catch (IllegalArgumentException e) {
            return "ERR|PAYLOAD_NOT_BASE64";
        }

        Optional<User> recipient = userStore.findById(recipientRef);
        if (recipient.isEmpty()) recipient = userStore.findByUsername(recipientRef);
        if (recipient.isEmpty()) return "ERR|RECIPIENT_NOT_FOUND";
        if (recipient.get().getId().equals(senderId.get())) return "ERR|SELF_MESSAGE";

        Message message = Message.create(senderId.get(), recipient.get().getId(),
                encryptedPayload);
        messageStore.add(message);
        return "OK|MESSAGE_SENT|" + message.getId() + "|" + message.getCreatedAt();
    }

    public String getMessages(String sessionToken, String peerRef) throws Exception {
        Optional<String> currentId = sessionStore.getUserId(sessionToken);
        if (currentId.isEmpty()) return "ERR|INVALID_SESSION";

        Optional<User> peer = userStore.findById(peerRef);
        if (peer.isEmpty()) peer = userStore.findByUsername(peerRef);
        if (peer.isEmpty()) return "ERR|USER_NOT_FOUND";

        List<Message> messages = messageStore.findConversation(
                currentId.get(), peer.get().getId());
        StringBuilder packed = new StringBuilder();
        for (Message message : messages) {
            if (packed.length() > 0) packed.append('\n');
            packed.append(message.getId()).append('\t')
                    .append(message.getSenderId()).append('\t')
                    .append(message.getRecipientId()).append('\t')
                    .append(message.getCreatedAt()).append('\t')
                    .append(message.isRead()).append('\t')
                    .append(message.getEncryptedPayload());
        }
        String encoded = Base64.getEncoder().encodeToString(
                packed.toString().getBytes(StandardCharsets.UTF_8));
        return "OK|MESSAGES|" + messages.size() + "|" + encoded;
    }

    public String markRead(String sessionToken, String messageId) throws Exception {
        Optional<String> userId = sessionStore.getUserId(sessionToken);
        if (userId.isEmpty()) return "ERR|INVALID_SESSION";
        if (messageId == null || messageId.isBlank()) return "ERR|BAD_MESSAGE_ID";
        messageStore.markRead(messageId, userId.get());
        return "OK|MARKED_READ";
    }
}
