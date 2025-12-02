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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<BreedingSeasonDTO> getSeasons(String username) {
        return seasonRepository.findByOwnerUsername(username)
                .stream()
                .map(breedingSeasonMapper::toDto)
                .toList();
    }

    @Override
    public BreedingSeasonDTO createSeason(BreedingSeasonDTO dto, String username) {
        User user = getUser(username);

        BreedingSeason season = breedingSeasonMapper.toEntity(dto);
        season.setOwner(user);

        return breedingSeasonMapper.toDto(seasonRepository.save(season));
    }

    @Override
    public BreedingSeasonDTO getSeason(Long id, String username) {
        BreedingSeason season = seasonRepository.findById(id)
                .orElseThrow(() -> new BreedingSeasonNotFoundException("Breeding season not found"));

        ensureOwner(season.getOwner().getUsername(), username);

        return breedingSeasonMapper.toDto(season);
    }

    @Override
    public BreedingSeasonDTO updateSeason(Long id, BreedingSeasonDTO dto, String username) {
        BreedingSeason season = seasonRepository.findById(id)
                .orElseThrow(() -> new BreedingSeasonNotFoundException("Breeding season not found"));

        ensureOwner(season.getOwner().getUsername(), username);

        if (dto.getName() != null) season.setName(dto.getName());
        if (dto.getStartDate() != null) season.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) season.setEndDate(dto.getEndDate());

        return breedingSeasonMapper.toDto(seasonRepository.save(season));
    }

    @Override
    public void deleteSeason(Long id, String username) {
        BreedingSeason season = seasonRepository.findById(id)
                .orElseThrow(() -> new BreedingSeasonNotFoundException("Season not found"));

        ensureOwner(season.getOwner().getUsername(), username);

        seasonRepository.delete(season);
    }


    // -------------------------------------------------------------
    // PAIRS
    // -------------------------------------------------------------
    @Override
    public List<BreedingPairDTO> getPairs(Long seasonId, String username) {
        BreedingSeason season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new BreedingSeasonNotFoundException("Season not found"));

        ensureOwner(season.getOwner().getUsername(), username);

        return pairRepository.findBySeasonId(seasonId)
                .stream()
                .map(breedingPairMapper::toDto)
                .toList();
    }

    @Override
    public BreedingPairDTO getPair(Long id, String username) {
        BreedingPair pair = pairRepository.findById(id)
                .orElseThrow(() -> new BreedingPairNotFoundException("Pair not found"));

        ensureOwner(pair.getSeason().getOwner().getUsername(), username);

        return breedingPairMapper.toDto(pair);
    }

    @Override
    @Transactional
    public BreedingPairDTO addPair(Long seasonId, BreedingPairDTO dto, String username) {
        BreedingSeason season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new BreedingSeasonNotFoundException("Season not found"));

        ensureOwner(season.getOwner().getUsername(), username);

        Pigeon male = getOwnedPigeon(dto.getMaleId(), username);
        Pigeon female = getOwnedPigeon(dto.getFemaleId(), username);

        BreedingPair pair = breedingPairMapper.toEntity(dto);
        pair.setSeason(season);
        pair.setMalePigeon(male);
        pair.setFemalePigeon(female);

        return breedingPairMapper.toDto(pairRepository.save(pair));
    }

    @Override
    @Transactional
    public BreedingPairDTO updatePair(Long id, BreedingPairDTO dto, String username) {
        BreedingPair pair = pairRepository.findById(id)
                .orElseThrow(() -> new BreedingPairNotFoundException("Pair not found"));

        ensureOwner(pair.getSeason().getOwner().getUsername(), username);

        if (dto.getBreedingDate() != null) pair.setBreedingDate(dto.getBreedingDate());
        if (dto.getNotes() != null) pair.setNotes(dto.getNotes());

        if (dto.getMaleId() != null) {
            Pigeon male = getOwnedPigeon(dto.getMaleId(), username);
            pair.setMalePigeon(male);
        }

        if (dto.getFemaleId() != null) {
            Pigeon female = getOwnedPigeon(dto.getFemaleId(), username);
            pair.setFemalePigeon(female);
        }

        return breedingPairMapper.toDto(pairRepository.save(pair));
    }

    @Override
    public void deletePair(Long id, String username) {
        BreedingPair pair = pairRepository.findById(id)
                .orElseThrow(() -> new BreedingPairNotFoundException("Pair not found"));

        ensureOwner(pair.getSeason().getOwner().getUsername(), username);

        pairRepository.delete(pair);
    }


    // -------------------------------------------------------------
    // OFFSPRING
    // -------------------------------------------------------------
    @Override
    @Transactional
    public BreedingPairDTO addOffspring(Long pairId, Long pigeonId, String username) {
        BreedingPair pair = pairRepository.findById(pairId)
                .orElseThrow(() -> new BreedingPairNotFoundException("Pair not found"));

        ensureOwner(pair.getSeason().getOwner().getUsername(), username);

        Pigeon baby = getOwnedPigeon(pigeonId, username);

        if (!pair.getOffspring().contains(baby)) {
            pair.getOffspring().add(baby);
        }

        return breedingPairMapper.toDto(pair);
    }


    // -------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------
    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private void ensureOwner(String ownerUsername, String requester) {
        if (!ownerUsername.equals(requester)) {
            throw new UnauthorizedActionException("You do not own this resource");
        }
    }

    private Pigeon getOwnedPigeon(Long pigeonId, String username) {
        Pigeon pigeon = pigeonRepository.findById(pigeonId)
                .orElseThrow(() -> new PigeonNotFoundException("Pigeon not found"));

        if (!pigeon.getOwner().getUsername().equals(username))
            throw new UnauthorizedActionException("This pigeon does not belong to you");

        return pigeon;
    }
}
