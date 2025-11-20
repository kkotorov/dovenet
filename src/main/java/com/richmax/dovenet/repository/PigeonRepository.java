package com.richmax.dovenet.repository;

import com.richmax.dovenet.repository.data.Pigeon;
import com.richmax.dovenet.repository.data.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface PigeonRepository extends JpaRepository<Pigeon, Long> {
    // --- ID / Ring Number ---
    Optional<Pigeon> findByRingNumber(String ringNumber);
    boolean existsByRingNumber(String ringNumber);
    List<Pigeon> findByOwnerUsernameAndRingNumberStartingWith(String username, String ringPrefix);
    List<Pigeon> findByFatherRingNumberOrMotherRingNumber(String fatherRing, String motherRing);

    // --- Name ---
    List<Pigeon> findByNameIgnoreCase(String name);
    List<Pigeon> findByNameContainingIgnoreCase(String namePart);
    List<Pigeon> findByNameStartingWithIgnoreCase(String prefix);
    List<Pigeon> findByNameEndingWithIgnoreCase(String suffix);

    // --- Color ---
    List<Pigeon> findByColorIgnoreCase(String color);
    List<Pigeon> findByColorContainingIgnoreCase(String colorPart);
    List<Pigeon> findByColorStartingWithIgnoreCase(String prefix);
    List<Pigeon> findByColorEndingWithIgnoreCase(String suffix);

    // --- Gender ---
    List<Pigeon> findByGenderIgnoreCase(String gender);

    // --- Status ---
    List<Pigeon> findByStatusIgnoreCase(String status);

    // --- BirthDate ---
    List<Pigeon> findByBirthDate(LocalDate birthDate);
    List<Pigeon> findByBirthDateBefore(LocalDate date);
    List<Pigeon> findByBirthDateAfter(LocalDate date);
    List<Pigeon> findByBirthDateBetween(LocalDate start, LocalDate end);

    // --- Father / Mother ---
    List<Pigeon> findByFatherRingNumber(String fatherRingNumber);
    List<Pigeon> findByMotherRingNumber(String motherRingNumber);

    // --- Associations ---
    List<Pigeon> findByOwner(User owner);
    List<Pigeon> findByOwnerId(Long ownerId);

    // --- Nested / association queries ---
    List<Pigeon> findByCompetitions_NameIgnoreCase(String competitionName);
    List<Pigeon> findByCompetitions_Date(LocalDate competitionDate);

    // --- Sorting
    List<Pigeon> findAllByOrderByNameAsc();
    List<Pigeon> findAllByStatusOrderByBirthDateAsc(String status);
    List<Pigeon> findAllByColorOrderByNameAsc(String color);

    // by loft
    List<Pigeon> findByLoftIdAndOwnerId(Long loftId, Long ownerId);

}
