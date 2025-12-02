package com.richmax.dovenet.controller;

import com.richmax.dovenet.service.BreedingService;
import com.richmax.dovenet.service.data.BreedingPairDTO;
import com.richmax.dovenet.service.data.BreedingSeasonDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/breeding")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BreedingController {

    private final BreedingService breedingService;

    // --- SEASONS ---

    @GetMapping("/seasons")
    public List<BreedingSeasonDTO> getSeasons(Authentication authentication) {
        return breedingService.getSeasons(authentication.getName());
    }

    @PostMapping("/seasons")
    public BreedingSeasonDTO createSeason(
            @RequestBody BreedingSeasonDTO dto,
            Authentication authentication
    ) {
        return breedingService.createSeason(dto, authentication.getName());
    }

    @GetMapping("/seasons/{id}")
    public BreedingSeasonDTO getSeason(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return breedingService.getSeason(id, authentication.getName());
    }

    @PatchMapping("/seasons/{id}")
    public BreedingSeasonDTO updateSeason(
            @PathVariable Long id,
            @RequestBody BreedingSeasonDTO dto,
            Authentication authentication
    ) {
        return breedingService.updateSeason(id, dto, authentication.getName());
    }

    @DeleteMapping("/seasons/{id}")
    public void deleteSeason(
            @PathVariable Long id,
            Authentication authentication
    ) {
        breedingService.deleteSeason(id, authentication.getName());
    }


    // --- PAIRS ---

    @GetMapping("/seasons/{seasonId}/pairs")
    public List<BreedingPairDTO> getPairs(
            @PathVariable Long seasonId,
            Authentication authentication
    ) {
        return breedingService.getPairs(seasonId, authentication.getName());
    }

    @PostMapping("/seasons/{seasonId}/pairs")
    public BreedingPairDTO addPair(
            @PathVariable Long seasonId,
            @RequestBody BreedingPairDTO dto,
            Authentication authentication
    ) {
        return breedingService.addPair(seasonId, dto, authentication.getName());
    }

    @GetMapping("/pairs/{id}")
    public BreedingPairDTO getPair(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return breedingService.getPair(id, authentication.getName());
    }

    @PatchMapping("/pairs/{id}")
    public BreedingPairDTO updatePair(
            @PathVariable Long id,
            @RequestBody BreedingPairDTO dto,
            Authentication authentication
    ) {
        return breedingService.updatePair(id, dto, authentication.getName());
    }

    @DeleteMapping("/pairs/{id}")
    public void deletePair(
            @PathVariable Long id,
            Authentication authentication
    ) {
        breedingService.deletePair(id, authentication.getName());
    }


    // --- OFFSPRING ---
    @PostMapping("/pairs/{pairId}/offspring/{pigeonId}")
    public BreedingPairDTO addOffspring(
            @PathVariable Long pairId,
            @PathVariable Long pigeonId,
            Authentication authentication
    ) {
        return breedingService.addOffspring(pairId, pigeonId, authentication.getName());
    }

    @DeleteMapping("/pairs/{pairId}/offspring/{pigeonId}")
    public BreedingPairDTO removeOffspring(
            @PathVariable Long pairId,
            @PathVariable Long pigeonId,
            Authentication authentication
    ) {
        return breedingService.removeOffspring(pairId, pigeonId, authentication.getName());
    }
}
