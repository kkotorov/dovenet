package com.richmax.dovenet.service;

import com.richmax.dovenet.service.data.CompetitionDTO;

import java.util.List;

public interface CompetitionService {

    CompetitionDTO createCompetition(CompetitionDTO dto, String username);

    CompetitionDTO updateCompetition(Long id, CompetitionDTO dto, String username);

    void deleteCompetition(Long id, String username);

    List<CompetitionDTO> getUserCompetitions(String username);

    CompetitionDTO getCompetitionById(Long id, String username);
}
