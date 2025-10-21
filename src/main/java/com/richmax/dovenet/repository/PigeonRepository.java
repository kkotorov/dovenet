package com.richmax.dovenet.repository;

import com.richmax.dovenet.repository.data.Pigeon;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PigeonRepository extends JpaRepository<Pigeon, Long> {
    Pigeon findByRingNumber(Long ringNumber);
}
