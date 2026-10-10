package com.pixelchat.server.api;

import com.pixelchat.server.model.Account;
import com.pixelchat.server.repository.AccountRepository;
import com.pixelchat.server.security.AuthContext;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {
    private final AccountRepository accounts;
    public ProfileController(AccountRepository accounts) { this.accounts = accounts; }
    public record ProfileUpdate(@Size(min = 1, max = 64) String displayName, @Size(max = 280) String bio, @Size(max = 2048) String avatarUrl) {}

    @GetMapping("/me") public Map<String, Object> me(Authentication auth) {
        Account a = account(auth);
        return Map.of("id", a.id.toString(), "email", a.email, "username", a.username, "displayName", a.displayName, "emailVerified", a.emailVerified, "bio", a.bio == null ? "" : a.bio, "avatarUrl", a.avatarUrl == null ? "" : a.avatarUrl, "createdAt", a.createdAt.toString());
    }
    @PatchMapping("/me") public Map<String, Object> update(@Valid @RequestBody ProfileUpdate r, Authentication auth) {
        Account a = account(auth);
        if (r.displayName() != null) { if (r.displayName().isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "displayName cannot be blank"); a.displayName = r.displayName().trim(); }
        if (r.bio() != null) a.bio = r.bio().trim();
        if (r.avatarUrl() != null) a.avatarUrl = r.avatarUrl().isBlank() ? null : r.avatarUrl().trim();
        accounts.save(a);
        return Map.of("ok", true, "displayName", a.displayName, "bio", a.bio == null ? "" : a.bio, "avatarUrl", a.avatarUrl == null ? "" : a.avatarUrl);
    }
    private Account account(Authentication auth) { return accounts.findById(AuthContext.userId(auth)).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account not found")); }
}
