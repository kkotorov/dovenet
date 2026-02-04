package com.richmax.dovenet.service;

import com.richmax.dovenet.service.data.BreedingPairDTO;
import com.richmax.dovenet.service.data.BreedingSeasonDTO;

import java.util.List;

public interface BreedingService {

    // --- SEASONS ---
    List<BreedingSeasonDTO> getSeasons(String email);

    BreedingSeasonDTO createSeason(BreedingSeasonDTO dto, String email);

    BreedingSeasonDTO getSeason(Long id, String email);

    BreedingSeasonDTO updateSeason(Long id, BreedingSeasonDTO dto, String email);

    void deleteSeason(Long id, String email);

    // --- PAIRS ---
    List<BreedingPairDTO> getPairs(Long seasonId, String email);

    BreedingPairDTO getPair(Long id, String email);

    BreedingPairDTO addPair(Long seasonId, BreedingPairDTO dto, String email);

    BreedingPairDTO updatePair(Long id, BreedingPairDTO dto, String email);

    void deletePair(Long id, String email);

    // --- OFFSPRING ---
    BreedingPairDTO addOffspring(Long pairId, Long pigeonId, String email);

    BreedingPairDTO removeOffspring(Long pairId, Long pigeonId, String email);
}
