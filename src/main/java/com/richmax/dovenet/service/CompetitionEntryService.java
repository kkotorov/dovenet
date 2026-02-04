package com.richmax.dovenet.service;

import com.richmax.dovenet.service.data.CompetitionEntryDTO;

import java.util.List;

public interface CompetitionEntryService {

    CompetitionEntryDTO addPigeonToCompetition(CompetitionEntryDTO dto, String email);

    CompetitionEntryDTO updateEntry(Long id, CompetitionEntryDTO dto, String email);

    void removeEntry(Long id, String email);

    List<CompetitionEntryDTO> getEntriesForCompetition(Long competitionId, String email);

    List<CompetitionEntryDTO> getEntriesForPigeon(Long pigeonId, String email);
}
