package com.pixelchat.server.api;

import com.pixelchat.server.security.SessionTokenService;
import com.pixelchat.server.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService auth;
    private final SessionTokenService tokens;
    public AuthController(AuthService auth, SessionTokenService tokens) { this.auth = auth; this.tokens = tokens; }

    public record RegisterRequest(@Email @NotBlank @Size(max = 320) String email,
                                  @NotBlank @Pattern(regexp = "[A-Za-z0-9_]{3,32}") String username,
                                  @NotBlank @Size(max = 64) String displayName,
                                  @NotBlank @Size(min = 10, max = 128) String password) {}
    public record VerifyRequest(@NotBlank @Email String email, @NotBlank @Pattern(regexp = "[0-9]{6}") String code) {}
    public record ResendRequest(@NotBlank @Email String email) {}
    public record LoginRequest(@NotBlank String identity, @NotBlank @Size(max = 128) String password) {}

    @PostMapping("/register") public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(auth.register(r.email(), r.username(), r.displayName(), r.password()));
    }
    @PostMapping("/verify-email") public Map<String, Object> verify(@Valid @RequestBody VerifyRequest r) { return auth.verifyEmail(r.email(), r.code()); }
    @PostMapping("/resend-verification") public Map<String, Object> resend(@Valid @RequestBody ResendRequest r) { return auth.resendCode(r.email()); }
    @PostMapping("/login") public Map<String, Object> login(@Valid @RequestBody LoginRequest r) { return auth.login(r.identity(), r.password()); }
    @PostMapping("/logout") public Map<String, Object> logout(@RequestHeader(value = "Authorization", required = false) String header, Authentication authentication) {
        if (header != null && header.startsWith("Bearer ")) tokens.revoke(header.substring(7).trim());
        return Map.of("ok", true, "message", "Session revoked");
    }
}
