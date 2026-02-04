package com.richmax.dovenet.repository;

import com.richmax.dovenet.repository.data.Competition;
import com.richmax.dovenet.repository.data.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {

    List<Competition> findByOwner(User owner);

    void deleteByOwner(User owner);
}
