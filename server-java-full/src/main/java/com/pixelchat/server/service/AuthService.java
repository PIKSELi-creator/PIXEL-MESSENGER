package com.pixelchat.server.service;

import com.pixelchat.server.model.Account;
import com.pixelchat.server.model.EmailVerificationCode;
import com.pixelchat.server.repository.AccountRepository;
import com.pixelchat.server.repository.EmailVerificationCodeRepository;
import com.pixelchat.server.security.SessionTokenService;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final AccountRepository accounts;
    private final EmailVerificationCodeRepository codes;
    private final PasswordEncoder passwords;
    private final SessionTokenService tokens;
    private final EmailDeliveryService emailDelivery;
    private final SecureRandom random = new SecureRandom();
    public AuthService(AccountRepository accounts, EmailVerificationCodeRepository codes, PasswordEncoder passwords, SessionTokenService tokens, EmailDeliveryService emailDelivery) {
        this.accounts = accounts; this.codes = codes; this.passwords = passwords; this.tokens = tokens; this.emailDelivery = emailDelivery;
    }

    @Transactional
    public Map<String, Object> register(String email, String username, String displayName, String password) {
        email = email.trim().toLowerCase(); username = username.trim().toLowerCase(); displayName = displayName.trim();
        if (accounts.existsByEmailIgnoreCase(email)) throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        if (accounts.existsByUsernameIgnoreCase(username)) throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
        Account account = new Account(); account.id = UUID.randomUUID(); account.email = email; account.username = username;
        account.displayName = displayName.isBlank() ? username : displayName; account.passwordHash = passwords.encode(password); account.emailVerified = false;
        accounts.save(account);
        String code = String.format("%06d", random.nextInt(1_000_000));
        storeCode(account.id, code);
        String delivery = emailDelivery.sendVerificationCode(email, code);
        return Map.of("ok", true, "message", "Account created. Verify the email before logging in.", "emailDelivery", delivery, "verificationExpiresInSeconds", 600);
    }

    @Transactional
    public Map<String, Object> resendCode(String email) {
        Account account = accounts.findFirstByEmailIgnoreCaseOrUsernameIgnoreCase(email.trim(), email.trim()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        if (account.emailVerified) throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already verified");
        codes.findByAccountIdAndUsedAtIsNull(account.id).forEach(c -> { c.usedAt = Instant.now(); codes.save(c); });
        String code = String.format("%06d", random.nextInt(1_000_000)); storeCode(account.id, code);
        return Map.of("ok", true, "emailDelivery", emailDelivery.sendVerificationCode(account.email, code), "verificationExpiresInSeconds", 600);
    }

    @Transactional
    public Map<String, Object> verifyEmail(String email, String code) {
        Account account = accounts.findFirstByEmailIgnoreCaseOrUsernameIgnoreCase(email.trim(), email.trim()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        if (account.emailVerified) return Map.of("ok", true, "verified", true, "message", "Email already verified");
        EmailVerificationCode item = codes.findTopByAccountIdAndUsedAtIsNullOrderByCreatedAtDesc(account.id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No active code. Request a new one."));
        if (item.expiresAt.isBefore(Instant.now())) { item.usedAt = Instant.now(); codes.save(item); return Map.of("ok", false, "verified", false, "message", "Code expired"); }
        if (item.attempts >= 5) { item.usedAt = Instant.now(); codes.save(item); return Map.of("ok", false, "verified", false, "message", "Too many attempts. Request a new code."); }
        if (!passwords.matches(code.trim(), item.codeHash)) {
            item.attempts++; if (item.attempts >= 5) item.usedAt = Instant.now(); codes.save(item);
            return Map.of("ok", false, "verified", false, "attemptsRemaining", Math.max(0, 5 - item.attempts));
        }
        item.usedAt = Instant.now(); account.emailVerified = true; codes.save(item); accounts.save(account);
        return Map.of("ok", true, "verified", true, "message", "Email verified. You can now log in.");
    }

    @Transactional
    public Map<String, Object> login(String identity, String password) {
        Account account = accounts.findFirstByEmailIgnoreCaseOrUsernameIgnoreCase(identity.trim(), identity.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!passwords.matches(password, account.passwordHash)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        if (!account.emailVerified) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Verify your email before logging in");
        var issued = tokens.issue(account.id);
        return Map.of("accessToken", issued.value(), "tokenType", "Bearer", "expiresAt", issued.expiresAt().toString(), "user", Map.of("id", account.id.toString(), "username", account.username, "displayName", account.displayName));
    }

    private void storeCode(UUID accountId, String code) {
        EmailVerificationCode item = new EmailVerificationCode(); item.id = UUID.randomUUID(); item.accountId = accountId;
        item.codeHash = passwords.encode(code); item.createdAt = Instant.now(); item.expiresAt = Instant.now().plus(10, ChronoUnit.MINUTES); item.attempts = 0;
        codes.save(item);
    }
}
