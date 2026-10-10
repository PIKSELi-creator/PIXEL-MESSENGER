package com.pixelchat.server.security;

import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

public final class AuthContext {
    private AuthContext() {}
    public static UUID userId(Authentication auth) {
        if (auth == null || auth.getName() == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        try { return UUID.fromString(auth.getName()); }
        catch (IllegalArgumentException e) { throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid session"); }
    }
}
