package com.richmax.dovenet.repository;

import com.richmax.dovenet.repository.data.Pigeon;
import com.richmax.dovenet.repository.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PigeonRepository extends JpaRepository<Pigeon, Long> {
    // --- ID / Ring Number ---
    Optional<Pigeon> findByRingNumber(String ringNumber);
    boolean existsByRingNumber(String ringNumber);
    List<Pigeon> findByOwnerAndRingNumberStartingWith(User owner, String ringPrefix); // New method
    List<Pigeon> findByFatherRingNumberOrMotherRingNumber(String fatherRing, String motherRing);

    // --- Associations ---
    List<Pigeon> findByOwner(User owner);

    // by loft
    List<Pigeon> findByLoftIdAndOwnerId(Long loftId, Long ownerId);
    long countByLoftId(Long loftId);
}
