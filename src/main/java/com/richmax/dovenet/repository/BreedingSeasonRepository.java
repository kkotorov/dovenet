package com.richmax.dovenet.repository;

import com.richmax.dovenet.repository.data.BreedingSeason;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BreedingSeasonRepository extends JpaRepository<BreedingSeason, Long> {
    List<BreedingSeason> findByOwnerUsername(String username);
}