package com.richmax.dovenet.service;

import com.richmax.dovenet.service.data.BreedingPairDTO;
import com.richmax.dovenet.service.data.BreedingSeasonDTO;

import java.util.List;

public interface BreedingService {

    // --- SEASONS ---
    List<BreedingSeasonDTO> getSeasons(String username);

    BreedingSeasonDTO createSeason(BreedingSeasonDTO dto, String username);

    BreedingSeasonDTO getSeason(Long id, String username);

    BreedingSeasonDTO updateSeason(Long id, BreedingSeasonDTO dto, String username);

    void deleteSeason(Long id, String username);

    // --- PAIRS ---
    List<BreedingPairDTO> getPairs(Long seasonId, String username);

    BreedingPairDTO getPair(Long id, String username);

    BreedingPairDTO addPair(Long seasonId, BreedingPairDTO dto, String username);

    BreedingPairDTO updatePair(Long id, BreedingPairDTO dto, String username);

    void deletePair(Long id, String username);

    // --- OFFSPRING ---
    BreedingPairDTO addOffspring(Long pairId, Long pigeonId, String username);
}
