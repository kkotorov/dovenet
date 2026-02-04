package com.richmax.dovenet.repository;

import com.richmax.dovenet.repository.data.BreedingSeason;
import com.richmax.dovenet.repository.data.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BreedingSeasonRepository extends JpaRepository<BreedingSeason, Long> {
    List<BreedingSeason> findByOwner(User owner);

    void deleteByOwner(User owner);
}
