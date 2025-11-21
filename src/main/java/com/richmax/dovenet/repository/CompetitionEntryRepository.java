package com.richmax.dovenet.repository;

import com.richmax.dovenet.repository.data.CompetitionEntry;
import com.richmax.dovenet.repository.data.Competition;
import com.richmax.dovenet.repository.data.Pigeon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompetitionEntryRepository extends JpaRepository<CompetitionEntry, Long> {

    List<CompetitionEntry> findByCompetition(Competition competition);

    List<CompetitionEntry> findByPigeon(Pigeon pigeon);

    List<CompetitionEntry> findByCompetitionId(Long competitionId);

    List<CompetitionEntry> findByPigeonId(Long pigeonId);
}
