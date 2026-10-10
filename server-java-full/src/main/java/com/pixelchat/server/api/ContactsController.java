package com.pixelchat.server.api;

import com.pixelchat.server.model.Account;
import com.pixelchat.server.model.ContactRelation;
import com.pixelchat.server.repository.AccountRepository;
import com.pixelchat.server.repository.ContactRelationRepository;
import com.pixelchat.server.security.AuthContext;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import java.time.Instant;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/contacts")
public class ContactsController {
    private final AccountRepository accounts;
    private final ContactRelationRepository relations;
    public ContactsController(AccountRepository accounts, ContactRelationRepository relations) { this.accounts = accounts; this.relations = relations; }
    public record ContactRequest(@Pattern(regexp = "[A-Za-z0-9_]{3,32}") String username) {}

    @PostMapping("/requests") @Transactional public Map<String, Object> request(@Valid @RequestBody ContactRequest r, Authentication auth) {
        UUID me = AuthContext.userId(auth);
        Account target = accounts.findByUsernameIgnoreCase(r.username()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (!target.emailVerified) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        if (me.equals(target.id)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot add yourself");
        if (relations.existsByOwnerIdAndContactIdAndState(me, target.id, "ACCEPTED") || relations.existsByOwnerIdAndContactIdAndState(target.id, me, "ACCEPTED"))
            return Map.of("ok", true, "state", "ACCEPTED", "message", "Already connected");
        var reverse = relations.findByOwnerIdAndContactIdAndState(target.id, me, "PENDING");
        if (reverse.isPresent()) { ContactRelation rel = reverse.get(); rel.state = "ACCEPTED"; relations.save(rel); return Map.of("ok", true, "state", "ACCEPTED"); }
        if (relations.existsByOwnerIdAndContactIdAndState(me, target.id, "PENDING")) return Map.of("ok", true, "state", "PENDING");
        ContactRelation rel = new ContactRelation(); rel.id = UUID.randomUUID(); rel.ownerId = me; rel.contactId = target.id; rel.state = "PENDING"; rel.createdAt = Instant.now(); relations.save(rel);
        return Map.of("ok", true, "state", "PENDING", "requestId", rel.id.toString());
    }

    @GetMapping public List<ContactView> list(Authentication auth) {
        UUID me = AuthContext.userId(auth);
        return relations.findByOwnerIdAndStateOrContactIdAndState(me, "ACCEPTED", me, "ACCEPTED").stream()
            .map(r -> me.equals(r.ownerId) ? r.contactId : r.ownerId).distinct()
            .map(id -> accounts.findById(id).orElse(null)).filter(Objects::nonNull)
            .map(a -> new ContactView(a.id, a.username, a.displayName)).toList();
    }

    @GetMapping("/requests") public List<ContactView> incoming(Authentication auth) {
        UUID me = AuthContext.userId(auth);
        return relations.findByContactIdAndState(me, "PENDING").stream().map(r -> accounts.findById(r.ownerId).orElse(null)).filter(Objects::nonNull)
            .map(a -> new ContactView(a.id, a.username, a.displayName)).toList();
    }

    @PostMapping("/requests/{requesterId}/accept") @Transactional public Map<String, Object> accept(@PathVariable UUID requesterId, Authentication auth) {
        UUID me = AuthContext.userId(auth);
        ContactRelation relation = relations.findByOwnerIdAndContactIdAndState(requesterId, me, "PENDING")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pending request not found"));
        relation.state = "ACCEPTED"; relations.save(relation);
        return Map.of("ok", true, "state", "ACCEPTED");
    }

    @DeleteMapping("/{userId}") @Transactional public Map<String, Object> remove(@PathVariable UUID userId, Authentication auth) {
        UUID me = AuthContext.userId(auth);
        long removed = relations.deleteByOwnerIdAndContactId(me, userId) + relations.deleteByOwnerIdAndContactId(userId, me);
        return Map.of("ok", true, "removed", removed > 0);
    }
    public record ContactView(UUID id, String username, String displayName) {}
}
