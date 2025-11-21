package com.richmax.dovenet.controller;

import com.richmax.dovenet.service.CompetitionService;
import com.richmax.dovenet.service.data.CompetitionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CompetitionController {

    private final CompetitionService competitionService;

    @GetMapping
    public List<CompetitionDTO> getMyCompetitions(Authentication auth) {
        return competitionService.getUserCompetitions(auth.getName());
    }

    @GetMapping("/{id}")
    public CompetitionDTO getCompetition(@PathVariable Long id, Authentication auth) {
        return competitionService.getCompetitionById(id, auth.getName());
    }

    @PostMapping
    public CompetitionDTO createCompetition(@RequestBody CompetitionDTO dto, Authentication auth) {
        return competitionService.createCompetition(dto, auth.getName());
    }

    @PatchMapping("/{id}")
    public CompetitionDTO updateCompetition(@PathVariable Long id,
                                            @RequestBody CompetitionDTO dto,
                                            Authentication auth) {
        return competitionService.updateCompetition(id, dto, auth.getName());
    }

    @DeleteMapping("/{id}")
    public void deleteCompetition(@PathVariable Long id, Authentication auth) {
        competitionService.deleteCompetition(id, auth.getName());
    }
}
