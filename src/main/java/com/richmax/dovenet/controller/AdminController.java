package com.richmax.dovenet.controller;

import com.richmax.dovenet.service.AdminService;
import com.richmax.dovenet.service.data.*;
import com.richmax.dovenet.types.SubscriptionType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // --- User Management ---

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PostMapping("/users/{id}/reset-password")
    public ResponseEntity<String> triggerPasswordReset(@PathVariable Long id) {
        adminService.triggerPasswordReset(id);
        return ResponseEntity.ok("Password reset email sent");
    }

    @PostMapping("/users/{id}/expire-subscription")
    public ResponseEntity<String> expireSubscription(@PathVariable Long id) {
        adminService.expireSubscription(id);
        return ResponseEntity.ok("Subscription expired immediately");
    }

    @PostMapping("/users/{id}/cancel-subscription")
    public ResponseEntity<String> cancelSubscription(@PathVariable Long id) {
        adminService.cancelSubscription(id);
        return ResponseEntity.ok("Subscription cancelled (will expire at period end)");
    }

    @PostMapping("/users/{id}/activate-subscription")
    public ResponseEntity<String> activateSubscription(
            @PathVariable Long id,
            @RequestParam SubscriptionType type,
            @RequestParam int durationMonths
    ) {
        adminService.activateSubscription(id, type, durationMonths);
        return ResponseEntity.ok("Subscription manually activated");
    }

    // --- Pigeon Management ---

    @GetMapping("/users/{userId}/pigeons")
    public ResponseEntity<List<PigeonDTO>> getPigeonsForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getPigeonsForUser(userId));
    }

    @PostMapping("/users/{userId}/pigeons")
    public ResponseEntity<PigeonDTO> createPigeonForUser(@PathVariable Long userId, @RequestBody PigeonDTO pigeonDTO) {
        return ResponseEntity.ok(adminService.createPigeonForUser(userId, pigeonDTO));
    }

    @PutMapping("/pigeons/{pigeonId}")
    public ResponseEntity<PigeonDTO> updatePigeon(@PathVariable Long pigeonId, @RequestBody PigeonDTO pigeonDTO) {
        return ResponseEntity.ok(adminService.updatePigeon(pigeonId, pigeonDTO));
    }

    @DeleteMapping("/pigeons/{pigeonId}")
    public ResponseEntity<String> deletePigeon(@PathVariable Long pigeonId) {
        adminService.deletePigeon(pigeonId);
        return ResponseEntity.ok("Pigeon deleted successfully");
    }

    // --- Loft Management ---

    @GetMapping("/users/{userId}/lofts")
    public ResponseEntity<List<LoftDTO>> getLoftsForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getLoftsForUser(userId));
    }

    @PostMapping("/users/{userId}/lofts")
    public ResponseEntity<LoftDTO> createLoftForUser(@PathVariable Long userId, @RequestBody LoftDTO loftDTO) {
        return ResponseEntity.ok(adminService.createLoftForUser(userId, loftDTO));
    }

    @PutMapping("/lofts/{loftId}")
    public ResponseEntity<LoftDTO> updateLoft(@PathVariable Long loftId, @RequestBody LoftDTO loftDTO) {
        return ResponseEntity.ok(adminService.updateLoft(loftId, loftDTO));
    }

    @DeleteMapping("/lofts/{loftId}")
    public ResponseEntity<String> deleteLoft(@PathVariable Long loftId) {
        adminService.deleteLoft(loftId);
        return ResponseEntity.ok("Loft deleted successfully");
    }

    // --- Breeding Management ---

    @GetMapping("/users/{userId}/breeding-seasons")
    public ResponseEntity<List<BreedingSeasonDTO>> getSeasonsForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getSeasonsForUser(userId));
    }

    @PostMapping("/users/{userId}/breeding-seasons")
    public ResponseEntity<BreedingSeasonDTO> createSeasonForUser(@PathVariable Long userId, @RequestBody BreedingSeasonDTO seasonDTO) {
        return ResponseEntity.ok(adminService.createSeasonForUser(userId, seasonDTO));
    }

    @PutMapping("/breeding-seasons/{seasonId}")
    public ResponseEntity<BreedingSeasonDTO> updateSeason(@PathVariable Long seasonId, @RequestBody BreedingSeasonDTO seasonDTO) {
        return ResponseEntity.ok(adminService.updateSeason(seasonId, seasonDTO));
    }

    @DeleteMapping("/breeding-seasons/{seasonId}")
    public ResponseEntity<String> deleteSeason(@PathVariable Long seasonId) {
        adminService.deleteSeason(seasonId);
        return ResponseEntity.ok("Season deleted successfully");
    }

    @GetMapping("/breeding-seasons/{seasonId}/pairs")
    public ResponseEntity<List<BreedingPairDTO>> getPairsForSeason(@PathVariable Long seasonId) {
        return ResponseEntity.ok(adminService.getPairsForSeason(seasonId));
    }

    @PostMapping("/breeding-seasons/{seasonId}/pairs")
    public ResponseEntity<BreedingPairDTO> createPairForSeason(@PathVariable Long seasonId, @RequestBody BreedingPairDTO pairDTO) {
        return ResponseEntity.ok(adminService.createPairForSeason(seasonId, pairDTO));
    }

    @PutMapping("/breeding-pairs/{pairId}")
    public ResponseEntity<BreedingPairDTO> updatePair(@PathVariable Long pairId, @RequestBody BreedingPairDTO pairDTO) {
        return ResponseEntity.ok(adminService.updatePair(pairId, pairDTO));
    }

    @DeleteMapping("/breeding-pairs/{pairId}")
    public ResponseEntity<String> deletePair(@PathVariable Long pairId) {
        adminService.deletePair(pairId);
        return ResponseEntity.ok("Pair deleted successfully");
    }

    @GetMapping("/breeding-pairs/{pairId}/offspring")
    public ResponseEntity<List<PigeonDTO>> getOffspringForPair(@PathVariable Long pairId) {
        return ResponseEntity.ok(adminService.getOffspringForPair(pairId));
    }

    @PostMapping("/breeding-pairs/{pairId}/offspring/{pigeonId}")
    public ResponseEntity<String> addOffspringToPair(@PathVariable Long pairId, @PathVariable Long pigeonId) {
        adminService.addOffspringToPair(pairId, pigeonId);
        return ResponseEntity.ok("Offspring added successfully");
    }

    @DeleteMapping("/breeding-pairs/{pairId}/offspring/{pigeonId}")
    public ResponseEntity<String> removeOffspringFromPair(@PathVariable Long pairId, @PathVariable Long pigeonId) {
        adminService.removeOffspringFromPair(pairId, pigeonId);
        return ResponseEntity.ok("Offspring removed successfully");
    }

    // --- Competition Management ---

    @GetMapping("/users/{userId}/competitions")
    public ResponseEntity<List<CompetitionDTO>> getCompetitionsForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getCompetitionsForUser(userId));
    }

    @PostMapping("/users/{userId}/competitions")
    public ResponseEntity<CompetitionDTO> createCompetitionForUser(@PathVariable Long userId, @RequestBody CompetitionDTO competitionDTO) {
        return ResponseEntity.ok(adminService.createCompetitionForUser(userId, competitionDTO));
    }

    @PutMapping("/competitions/{competitionId}")
    public ResponseEntity<CompetitionDTO> updateCompetition(@PathVariable Long competitionId, @RequestBody CompetitionDTO competitionDTO) {
        return ResponseEntity.ok(adminService.updateCompetition(competitionId, competitionDTO));
    }

    @DeleteMapping("/competitions/{competitionId}")
    public ResponseEntity<String> deleteCompetition(@PathVariable Long competitionId) {
        adminService.deleteCompetition(competitionId);
        return ResponseEntity.ok("Competition deleted successfully");
    }

    @GetMapping("/competitions/{competitionId}/entries")
    public ResponseEntity<List<CompetitionEntryDTO>> getEntriesForCompetition(@PathVariable Long competitionId) {
        return ResponseEntity.ok(adminService.getEntriesForCompetition(competitionId));
    }

    @PostMapping("/competitions/{competitionId}/entries")
    public ResponseEntity<CompetitionEntryDTO> addEntryToCompetition(@PathVariable Long competitionId, @RequestBody CompetitionEntryDTO entryDTO) {
        return ResponseEntity.ok(adminService.addEntryToCompetition(competitionId, entryDTO));
    }

    @DeleteMapping("/competition-entries/{entryId}")
    public ResponseEntity<String> removeEntry(@PathVariable Long entryId) {
        adminService.removeEntry(entryId);
        return ResponseEntity.ok("Entry removed successfully");
    }
}
