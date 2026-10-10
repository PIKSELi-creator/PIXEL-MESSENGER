package com.pixelchat.server.repository;

import com.pixelchat.server.model.ContactRelation;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRelationRepository extends JpaRepository<ContactRelation, UUID> {
    Optional<ContactRelation> findByOwnerIdAndContactIdAndState(UUID ownerId, UUID contactId, String state);
    List<ContactRelation> findByOwnerIdAndStateOrContactIdAndState(UUID ownerId, String state1, UUID contactId, String state2);
    List<ContactRelation> findByContactIdAndState(UUID contactId, String state);
    boolean existsByOwnerIdAndContactIdAndState(UUID ownerId, UUID contactId, String state);
    long deleteByOwnerIdAndContactId(UUID ownerId, UUID contactId);
}
