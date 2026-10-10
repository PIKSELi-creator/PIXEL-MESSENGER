package com.pixelchat.server.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailDeliveryService {
    private final ObjectProvider<JavaMailSender> senderProvider;
    @Value("${pixelchat.dev-mode:false}") private boolean devMode;
    @Value("${spring.mail.host:}") private String mailHost;
    @Value("${pixelchat.email.from:no-reply@pixelchat.local}") private String from;
    public EmailDeliveryService(ObjectProvider<JavaMailSender> senderProvider) { this.senderProvider = senderProvider; }

    public String sendVerificationCode(String email, String code) {
        if (mailHost == null || mailHost.isBlank()) {
            if (devMode) {
                System.out.println("[PIXEL CHAT DEV ONLY] Email verification code for " + email + ": " + code);
                return "DEV_LOG";
            }
            return "NOT_CONFIGURED";
        }
        JavaMailSender sender = senderProvider.getIfAvailable();
        if (sender == null) return "NOT_CONFIGURED";
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from); message.setTo(email); message.setSubject("PIXEL CHAT — email verification");
            message.setText("Your verification code is " + code + ". It expires in 10 minutes. If you did not request this, ignore this message.");
            sender.send(message);
            return "SENT";
        } catch (RuntimeException ex) {
            System.err.println("PIXEL CHAT: email delivery failed: " + ex.getClass().getSimpleName());
            return "FAILED";
        }
    }
}
