package com.richmax.dovenet.repository;

import com.richmax.dovenet.repository.data.Pigeon;
import com.richmax.dovenet.repository.data.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PigeonRepository extends JpaRepository<Pigeon, Long> {
    Pigeon findByRingNumber(Long ringNumber);
    List<Pigeon> findByOwner(User owner);
}
