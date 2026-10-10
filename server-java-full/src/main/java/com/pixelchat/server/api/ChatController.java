package com.pixelchat.server.api;

import com.pixelchat.server.model.*;
import com.pixelchat.server.repository.*;
import com.pixelchat.server.security.AuthContext;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/chats")
public class ChatController {
    private final AccountRepository accounts;
    private final ChatRoomRepository chats;
    private final ChatMemberRepository members;
    private final ChatMessageRepository messages;
    private final com.pixelchat.server.repository.MessageReceiptService receipts;
    public ChatController(AccountRepository accounts, ChatRoomRepository chats, ChatMemberRepository members, ChatMessageRepository messages, com.pixelchat.server.repository.MessageReceiptService receipts) {
        this.accounts = accounts; this.chats = chats; this.members = members; this.messages = messages; this.receipts = receipts;
    }
    public record DirectChatRequest(@NotBlank @Pattern(regexp = "[A-Za-z0-9_]{3,32}") String username) {}
    public record GroupChatRequest(@NotBlank @Size(min = 2, max = 100) String title, @Size(min = 1, max = 99) List<@Pattern(regexp = "[A-Za-z0-9_]{3,32}") String> usernames) {}
    public record SendMessageRequest(@NotBlank @Size(max = 10000) String text) {}
    public record ChatView(UUID id, String kind, String title, Instant createdAt) {}
    public record MessageView(UUID id, UUID chatId, UUID senderId, String text, String type, Instant createdAt) {}

    @PostMapping("/direct") @Transactional public ChatView direct(@Valid @RequestBody DirectChatRequest request, Authentication auth) {
        UUID me = AuthContext.userId(auth);
        Account other = accounts.findByUsernameIgnoreCase(request.username()).filter(a -> a.emailVerified).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (me.equals(other.id)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot create a chat with yourself");
        Optional<ChatRoom> existing = chats.findDirectChat(me, other.id);
        if (existing.isPresent()) return view(existing.get());
        return createRoom("DIRECT", null, me, List.of(me, other.id));
    }

    @PostMapping("/groups") @Transactional public ChatView group(@Valid @RequestBody GroupChatRequest request, Authentication auth) {
        UUID me = AuthContext.userId(auth);
        LinkedHashSet<UUID> ids = new LinkedHashSet<>(); ids.add(me);
        if (request.usernames() != null) for (String username : request.usernames()) {
            Account a = accounts.findByUsernameIgnoreCase(username).filter(x -> x.emailVerified).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + username));
            ids.add(a.id);
        }
        if (ids.size() < 2) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A group needs at least two members");
        if (ids.size() > 100) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group is too large");
        return createRoom("GROUP", request.title().trim(), me, new ArrayList<>(ids));
    }

    @GetMapping public List<ChatView> list(Authentication auth) {
        UUID me = AuthContext.userId(auth);
        return members.findByUserIdOrderByJoinedAtDesc(me).stream().map(m -> chats.findById(m.chatId).orElse(null)).filter(Objects::nonNull).map(ChatController::view).toList();
    }

    @PostMapping("/{chatId}/messages") @Transactional public MessageView send(@PathVariable UUID chatId, @Valid @RequestBody SendMessageRequest request, Authentication auth) {
        UUID me = AuthContext.userId(auth); requireMember(chatId, me);
        ChatMessage message = new ChatMessage(); message.id = UUID.randomUUID(); message.chatId = chatId; message.senderId = me; message.payload = request.text().trim(); message.payloadType = "TEXT"; message.createdAt = Instant.now();
        messages.save(message);
        return messageView(message);
    }

    @GetMapping("/{chatId}/messages") public List<MessageView> history(@PathVariable UUID chatId, @RequestParam(defaultValue = "50") @Min(1) @Max(100) int limit, Authentication auth) {
        requireMember(chatId, AuthContext.userId(auth));
        List<ChatMessage> found = messages.findByChatIdOrderByCreatedAtDesc(chatId, PageRequest.of(0, limit));
        return found.stream().map(ChatController::messageView).toList();
    }

    @PostMapping("/{chatId}/read") @Transactional public Map<String, Object> markRead(@PathVariable UUID chatId, Authentication auth) {
        UUID me = AuthContext.userId(auth); requireMember(chatId, me);
        int marked = receipts.markMessagesRead(chatId, me);
        return Map.of("ok", true, "markedMessages", marked);
    }

    private ChatView createRoom(String kind, String title, UUID createdBy, List<UUID> ids) {
        ChatRoom room = new ChatRoom(); room.id = UUID.randomUUID(); room.kind = kind; room.title = title; room.createdBy = createdBy; room.createdAt = Instant.now(); chats.save(room);
        for (UUID id : ids) { ChatMember m = new ChatMember(); m.chatId = room.id; m.userId = id; m.role = id.equals(createdBy) ? "OWNER" : "MEMBER"; m.joinedAt = Instant.now(); members.save(m); }
        return view(room);
    }
    private void requireMember(UUID chatId, UUID userId) {
        if (!chats.existsById(chatId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat not found");
        if (!members.existsByChatIdAndUserId(chatId, userId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not a member of this chat");
    }
    private static ChatView view(ChatRoom c) { return new ChatView(c.id, c.kind, c.title, c.createdAt); }
    private static MessageView messageView(ChatMessage m) { return new MessageView(m.id, m.chatId, m.senderId, m.deletedAt == null ? m.payload : "", m.payloadType, m.createdAt); }
}
