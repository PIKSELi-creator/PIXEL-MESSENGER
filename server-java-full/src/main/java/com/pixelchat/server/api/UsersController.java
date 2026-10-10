package com.pixelchat.server.api;

import com.pixelchat.server.model.Account;
import com.pixelchat.server.repository.AccountRepository;
import jakarta.validation.constraints.Size;
import java.util.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UsersController {
    private final AccountRepository accounts;
    public UsersController(AccountRepository accounts) { this.accounts = accounts; }
    @GetMapping("/search") public List<UserView> search(@RequestParam @Size(min = 2, max = 32) String q) {
        return accounts.searchVerified(q.trim(), PageRequest.of(0, 25)).stream().map(UsersController::view).toList();
    }
    public static UserView view(Account a) { return new UserView(a.id, a.username, a.displayName, a.bio == null ? "" : a.bio, a.avatarUrl == null ? "" : a.avatarUrl); }
    public record UserView(UUID id, String username, String displayName, String bio, String avatarUrl) {}
}
