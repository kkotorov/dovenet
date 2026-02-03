package com.richmax.dovenet.service;

import com.richmax.dovenet.service.data.*;
import com.richmax.dovenet.types.SubscriptionType;

import java.util.List;

public interface AdminService {
    // User Management
    List<UserDTO> getAllUsers();
    UserDTO getUserById(Long id);
    void deleteUser(Long id);
    void triggerPasswordReset(Long userId);
    void expireSubscription(Long userId);
    void cancelSubscription(Long userId); // Calls Stripe
    void activateSubscription(Long userId, SubscriptionType type, int durationMonths);

    // Pigeon Management
    List<PigeonDTO> getPigeonsForUser(Long userId);
    PigeonDTO createPigeonForUser(Long userId, PigeonDTO pigeonDTO);
    PigeonDTO updatePigeon(Long pigeonId, PigeonDTO pigeonDTO);
    void deletePigeon(Long pigeonId);

    // Loft Management
    List<LoftDTO> getLoftsForUser(Long userId);
    LoftDTO createLoftForUser(Long userId, LoftDTO loftDTO);
    LoftDTO updateLoft(Long loftId, LoftDTO loftDTO);
    void deleteLoft(Long loftId);

    // Breeding Management
    List<BreedingSeasonDTO> getSeasonsForUser(Long userId);
    BreedingSeasonDTO createSeasonForUser(Long userId, BreedingSeasonDTO seasonDTO);
    BreedingSeasonDTO updateSeason(Long seasonId, BreedingSeasonDTO seasonDTO);
    void deleteSeason(Long seasonId);

    List<BreedingPairDTO> getPairsForSeason(Long seasonId);
    BreedingPairDTO createPairForSeason(Long seasonId, BreedingPairDTO pairDTO);
    BreedingPairDTO updatePair(Long pairId, BreedingPairDTO pairDTO);
    void deletePair(Long pairId);

    List<PigeonDTO> getOffspringForPair(Long pairId);
    void addOffspringToPair(Long pairId, Long pigeonId);
    void removeOffspringFromPair(Long pairId, Long pigeonId);

    // Competition Management
    List<CompetitionDTO> getCompetitionsForUser(Long userId);
    CompetitionDTO createCompetitionForUser(Long userId, CompetitionDTO competitionDTO);
    CompetitionDTO updateCompetition(Long competitionId, CompetitionDTO competitionDTO);
    void deleteCompetition(Long competitionId);

    List<CompetitionEntryDTO> getEntriesForCompetition(Long competitionId);
    CompetitionEntryDTO addEntryToCompetition(Long competitionId, CompetitionEntryDTO entryDTO);
    void removeEntry(Long entryId);
}
