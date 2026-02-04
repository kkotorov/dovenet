package com.richmax.dovenet.service;

import com.richmax.dovenet.service.data.CompetitionDTO;

import java.util.List;

public interface CompetitionService {

    CompetitionDTO createCompetition(CompetitionDTO dto, String email);

    CompetitionDTO updateCompetition(Long id, CompetitionDTO dto, String email);

    void deleteCompetition(Long id, String email);

    List<CompetitionDTO> getUserCompetitions(String email);

    CompetitionDTO getCompetitionById(Long id, String email);
}
