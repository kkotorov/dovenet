package com.richmax.dovenet.service.impl;

import com.richmax.dovenet.exception.*;
import com.richmax.dovenet.mapper.BreedingPairMapper;
import com.richmax.dovenet.mapper.BreedingSeasonMapper;
import com.richmax.dovenet.repository.BreedingPairRepository;
import com.richmax.dovenet.repository.BreedingSeasonRepository;
import com.richmax.dovenet.repository.PigeonRepository;
import com.richmax.dovenet.repository.UserRepository;
import com.richmax.dovenet.repository.data.BreedingPair;
import com.richmax.dovenet.repository.data.BreedingSeason;
import com.richmax.dovenet.repository.data.Pigeon;
import com.richmax.dovenet.repository.data.User;
import com.richmax.dovenet.service.BreedingService;
import com.richmax.dovenet.service.data.BreedingPairDTO;
import com.richmax.dovenet.service.data.BreedingSeasonDTO;
import com.richmax.dovenet.types.SubscriptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BreedingServiceImpl implements BreedingService {

    private final BreedingSeasonRepository seasonRepository;
    private final BreedingPairRepository pairRepository;
    private final PigeonRepository pigeonRepository;
    private final UserRepository userRepository;

    private final BreedingSeasonMapper breedingSeasonMapper;
    private final BreedingPairMapper breedingPairMapper;

    // -------------------------------------------------------------
    // SEASONS
    // -------------------------------------------------------------
    @Override
    public List<BreedingSeasonDTO> getSeasons(String email) {
        User user = getUser(email);
        return seasonRepository.findByOwner(user)
                .stream()
                .map(breedingSeasonMapper::toDto)
                .toList();
    }

    @Override
    public BreedingSeasonDTO createSeason(BreedingSeasonDTO dto, String email) {
        User user = getUser(email);

        BreedingSeason season = breedingSeasonMapper.toEntity(dto);
        season.setOwner(user);

        return breedingSeasonMapper.toDto(seasonRepository.save(season));
    }

    @Override
    public BreedingSeasonDTO getSeason(Long id, String email) {
        BreedingSeason season = seasonRepository.findById(id)
                .orElseThrow(() -> new BreedingSeasonNotFoundException("Breeding season not found"));

        ensureOwner(season.getOwner().getEmail(), email);

        return breedingSeasonMapper.toDto(season);
    }

    @Override
    public BreedingSeasonDTO updateSeason(Long id, BreedingSeasonDTO dto, String email) {
        BreedingSeason season = seasonRepository.findById(id)
                .orElseThrow(() -> new BreedingSeasonNotFoundException("Breeding season not found"));

        ensureOwner(season.getOwner().getEmail(), email);

        if (dto.getName() != null) season.setName(dto.getName());
        if (dto.getStartDate() != null) season.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) season.setEndDate(dto.getEndDate());

        return breedingSeasonMapper.toDto(seasonRepository.save(season));
    }

    @Override
    public void deleteSeason(Long id, String email) {
        BreedingSeason season = seasonRepository.findById(id)
                .orElseThrow(() -> new BreedingSeasonNotFoundException("Season not found"));

        ensureOwner(season.getOwner().getEmail(), email);

        seasonRepository.delete(season);
    }


    // -------------------------------------------------------------
    // PAIRS
    // -------------------------------------------------------------
    @Override
    public List<BreedingPairDTO> getPairs(Long seasonId, String email) {
        BreedingSeason season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new BreedingSeasonNotFoundException("Season not found"));

        ensureOwner(season.getOwner().getEmail(), email);

        return pairRepository.findBySeasonId(seasonId)
                .stream()
                .map(breedingPairMapper::toDto)
                .toList();
    }

    @Override
    public BreedingPairDTO getPair(Long id, String email) {
        BreedingPair pair = pairRepository.findById(id)
                .orElseThrow(() -> new BreedingPairNotFoundException("Pair not found"));

        ensureOwner(pair.getSeason().getOwner().getEmail(), email);

        return breedingPairMapper.toDto(pair);
    }

    @Override
    @Transactional
    public BreedingPairDTO addPair(Long seasonId, BreedingPairDTO dto, String email) {
        BreedingSeason season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new BreedingSeasonNotFoundException("Season not found"));

        ensureOwner(season.getOwner().getEmail(), email);

        Pigeon male = getOwnedPigeon(dto.getMaleId(), email);
        Pigeon female = getOwnedPigeon(dto.getFemaleId(), email);

        BreedingPair pair = breedingPairMapper.toEntity(dto);
        pair.setSeason(season);
        pair.setMalePigeon(male);
        pair.setFemalePigeon(female);

        pair.setInbred(isInbred(male, female));

        return breedingPairMapper.toDto(pairRepository.save(pair));
    }


    @Override
    @Transactional
    public BreedingPairDTO updatePair(Long id, BreedingPairDTO dto, String email) {
        BreedingPair pair = pairRepository.findById(id)
                .orElseThrow(() -> new BreedingPairNotFoundException("Pair not found"));

        ensureOwner(pair.getSeason().getOwner().getEmail(), email);

        if (dto.getBreedingDate() != null) pair.setBreedingDate(dto.getBreedingDate());
        if (dto.getNotes() != null) pair.setNotes(dto.getNotes());

        if (dto.getMaleId() != null) {
            Pigeon male = getOwnedPigeon(dto.getMaleId(), email);
            pair.setMalePigeon(male);
        }

        if (dto.getFemaleId() != null) {
            Pigeon female = getOwnedPigeon(dto.getFemaleId(), email);
            pair.setFemalePigeon(female);
        }

        return breedingPairMapper.toDto(pairRepository.save(pair));
    }

    @Override
    public void deletePair(Long id, String email) {
        BreedingPair pair = pairRepository.findById(id)
                .orElseThrow(() -> new BreedingPairNotFoundException("Pair not found"));

        ensureOwner(pair.getSeason().getOwner().getEmail(), email);

        pairRepository.delete(pair);
    }


    // -------------------------------------------------------------
    // OFFSPRING
    // -------------------------------------------------------------
    @Override
    @Transactional
    public BreedingPairDTO addOffspring(Long pairId, Long pigeonId, String email) {
        BreedingPair pair = pairRepository.findById(pairId)
                .orElseThrow(() -> new BreedingPairNotFoundException("Pair not found"));

        ensureOwner(pair.getSeason().getOwner().getEmail(), email);

        Pigeon baby = getOwnedPigeon(pigeonId, email);

        if (!pair.getOffspring().contains(baby)) {
            pair.getOffspring().add(baby);
        }

        return breedingPairMapper.toDto(pair);
    }


    // -------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------
    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));
    }

    private void ensureOwner(String ownerEmail, String requesterEmail) {
        if (!ownerEmail.equals(requesterEmail)) {
            throw new UnauthorizedActionException("You do not own this resource");
        }
    }

    private Pigeon getOwnedPigeon(Long pigeonId, String email) {
        Pigeon pigeon = pigeonRepository.findById(pigeonId)
                .orElseThrow(() -> new PigeonNotFoundException("Pigeon not found"));

        if (!pigeon.getOwner().getEmail().equals(email))
            throw new UnauthorizedActionException("This pigeon does not belong to you");

        return pigeon;
    }

    private boolean isInbred(Pigeon male, Pigeon female) {
        if (male == null || female == null) return false;

        String maleFather = male.getFatherRingNumber();
        String maleMother = male.getMotherRingNumber();
        String femaleFather = female.getFatherRingNumber();
        String femaleMother = female.getMotherRingNumber();

        // parent-child
        if (male.getRingNumber().equals(femaleFather) || male.getRingNumber().equals(femaleMother)) return true;
        if (female.getRingNumber().equals(maleFather) || female.getRingNumber().equals(maleMother)) return true;

        // grandparent-grandchild (only check from male -> female)
        if ((maleFather != null && (maleFather.equals(femaleFather) || maleFather.equals(femaleMother))) ||
                (maleMother != null && (maleMother.equals(femaleFather) || maleMother.equals(femaleMother)))) {
            return true;
        }

        return false;
    }

    @Override
    @Transactional
    public BreedingPairDTO removeOffspring(Long pairId, Long pigeonId, String email) {
        BreedingPair pair = pairRepository.findById(pairId)
                .orElseThrow(() -> new BreedingPairNotFoundException("Pair not found"));

        ensureOwner(pair.getSeason().getOwner().getEmail(), email);

        Pigeon baby = getOwnedPigeon(pigeonId, email);

        pair.getOffspring().remove(baby);

        return breedingPairMapper.toDto(pair);
    }

    public boolean hasActiveSubscription(User user) {
        return user.getSubscription() != SubscriptionType.FREE
                && user.getSubscriptionValidUntil() != null
                && user.getSubscriptionValidUntil().isAfter(LocalDateTime.now());
    }

}
