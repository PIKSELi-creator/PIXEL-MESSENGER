package com.pixelchat.server.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class HealthController {
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of("status", "ok", "service", "PIXEL CHAT", "apiVersion", "v1");
    }

    @GetMapping("/capabilities")
    public Map<String, Object> capabilities() {
        return Map.of(
            "implementedFoundation", List.of("health", "capabilities", "security-boundary", "postgres-config", "flyway-config", "websocket-infrastructure"),
            "plannedModules", List.of("email-auth", "profiles", "contacts", "direct-chats", "groups", "message-history", "reactions", "attachments", "blocks", "reports", "push-notifications", "calls", "admin-tools"),
            "productionReady", false
        );
    }
}
