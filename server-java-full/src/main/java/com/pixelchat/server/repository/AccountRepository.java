package com.pixelchat.server.repository;

import com.pixelchat.server.model.Account;
import java.util.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByUsernameIgnoreCase(String username);
    Optional<Account> findFirstByEmailIgnoreCaseOrUsernameIgnoreCase(String email, String username);
    Optional<Account> findByUsernameIgnoreCase(String username);
    @Query("select a from Account a where a.emailVerified = true and (lower(a.username) like lower(concat('%', :q, '%')) or lower(a.displayName) like lower(concat('%', :q, '%'))) order by a.username")
    List<Account> searchVerified(@Param("q") String q, Pageable pageable);
}
