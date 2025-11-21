package com.richmax.dovenet.service;

import com.richmax.dovenet.service.data.CompetitionEntryDTO;

import java.util.List;

public interface CompetitionEntryService {

    CompetitionEntryDTO addPigeonToCompetition(CompetitionEntryDTO dto, String username);

    CompetitionEntryDTO updateEntry(Long id, CompetitionEntryDTO dto, String username);

    void removeEntry(Long id, String username);

    List<CompetitionEntryDTO> getEntriesForCompetition(Long competitionId, String username);

    List<CompetitionEntryDTO> getEntriesForPigeon(Long pigeonId, String username);
}
