package com.richmax.dovenet.repository;

import com.richmax.dovenet.repository.data.BreedingPair;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BreedingPairRepository extends JpaRepository<BreedingPair, Long> {
    List<BreedingPair> findBySeasonId(Long seasonId);
}
