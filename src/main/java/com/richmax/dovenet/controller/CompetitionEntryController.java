package com.richmax.dovenet.controller;

import com.richmax.dovenet.service.CompetitionEntryService;
import com.richmax.dovenet.service.data.CompetitionEntryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competition-entries")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CompetitionEntryController {

    private final CompetitionEntryService entryService;

    @GetMapping("/competition/{competitionId}")
    public List<CompetitionEntryDTO> getEntriesForCompetition(@PathVariable Long competitionId,
                                                              Authentication auth) {
        return entryService.getEntriesForCompetition(competitionId, auth.getName());
    }

    @GetMapping("/pigeon/{pigeonId}")
    public List<CompetitionEntryDTO> getEntriesForPigeon(@PathVariable Long pigeonId,
                                                         Authentication auth) {
        return entryService.getEntriesForPigeon(pigeonId, auth.getName());
    }

    @PostMapping
    public CompetitionEntryDTO addEntry(@RequestBody CompetitionEntryDTO dto,
                                        Authentication auth) {
        return entryService.addPigeonToCompetition(dto, auth.getName());
    }

    @PatchMapping("/{id}")
    public CompetitionEntryDTO updateEntry(@PathVariable Long id,
                                           @RequestBody CompetitionEntryDTO dto,
                                           Authentication auth) {
        return entryService.updateEntry(id, dto, auth.getName());
    }

    @DeleteMapping("/{id}")
    public void removeEntry(@PathVariable Long id, Authentication auth) {
        entryService.removeEntry(id, auth.getName());
    }
}
