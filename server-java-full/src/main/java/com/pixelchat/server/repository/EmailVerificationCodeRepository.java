package com.pixelchat.server.repository;

import com.pixelchat.server.model.EmailVerificationCode;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerificationCode, UUID> {
    Optional<EmailVerificationCode> findTopByAccountIdAndUsedAtIsNullOrderByCreatedAtDesc(UUID accountId);
    List<EmailVerificationCode> findByAccountIdAndUsedAtIsNull(UUID accountId);
}
