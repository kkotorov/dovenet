package com.richmax.dovenet.service.impl;

import com.richmax.dovenet.exception.*;
import com.richmax.dovenet.mapper.*;
import com.richmax.dovenet.repository.*;
import com.richmax.dovenet.repository.data.*;
import com.richmax.dovenet.service.AdminService;
import com.richmax.dovenet.service.BillingService;
import com.richmax.dovenet.service.UserService;
import com.richmax.dovenet.service.data.*;
import com.richmax.dovenet.types.SubscriptionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserService userService;
    private final BillingService billingService;
    
    private final PigeonRepository pigeonRepository;
    private final PigeonMapper pigeonMapper;
    
    private final LoftRepository loftRepository;
    private final LoftMapper loftMapper;
    
    private final BreedingSeasonRepository seasonRepository;
    private final BreedingSeasonMapper seasonMapper;
    
    private final BreedingPairRepository pairRepository;
    private final BreedingPairMapper pairMapper;
    
    private final CompetitionRepository competitionRepository;
    private final CompetitionMapper competitionMapper;
    
    private final CompetitionEntryRepository entryRepository;
    private final CompetitionEntryMapper entryMapper;

    public AdminServiceImpl(UserRepository userRepository, UserMapper userMapper, UserService userService, BillingService billingService, 
                            PigeonRepository pigeonRepository, PigeonMapper pigeonMapper,
                            LoftRepository loftRepository, LoftMapper loftMapper,
                            BreedingSeasonRepository seasonRepository, BreedingSeasonMapper seasonMapper,
                            BreedingPairRepository pairRepository, BreedingPairMapper pairMapper,
                            CompetitionRepository competitionRepository, CompetitionMapper competitionMapper,
                            CompetitionEntryRepository entryRepository, CompetitionEntryMapper entryMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.userService = userService;
        this.billingService = billingService;
        this.pigeonRepository = pigeonRepository;
        this.pigeonMapper = pigeonMapper;
        this.loftRepository = loftRepository;
        this.loftMapper = loftMapper;
        this.seasonRepository = seasonRepository;
        this.seasonMapper = seasonMapper;
        this.pairRepository = pairRepository;
        this.pairMapper = pairMapper;
        this.competitionRepository = competitionRepository;
        this.competitionMapper = competitionMapper;
        this.entryRepository = entryRepository;
        this.entryMapper = entryMapper;
    }

    // --- User Management ---

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return userMapper.toDto(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        userService.deleteUserByEmail(user.getEmail());
    }

    @Override
    public void triggerPasswordReset(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        userService.initiatePasswordReset(user.getEmail());
    }

    @Override
    public void expireSubscription(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        userService.expireSubscriptionNow(user.getEmail());
    }

    @Override
    public void cancelSubscription(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        billingService.cancelSubscription(user.getEmail());
    }

    @Override
    public void activateSubscription(Long userId, SubscriptionType type, int durationMonths) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        user.setSubscription(type);
        user.setSubscriptionValidUntil(LocalDateTime.now().plusMonths(durationMonths));
        user.setAutoRenew(false); // Manual activation, no auto-renew
        
        userRepository.save(user);
    }

    // --- Pigeon Management ---

    @Override
    public List<PigeonDTO> getPigeonsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return pigeonRepository.findByOwner(user).stream()
                .map(pigeonMapper::toDto)
                .toList();
    }

    @Override
    public PigeonDTO createPigeonForUser(Long userId, PigeonDTO pigeonDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        Pigeon pigeon = pigeonMapper.toEntity(pigeonDTO);
        pigeon.setOwner(user);
        
        return pigeonMapper.toDto(pigeonRepository.save(pigeon));
    }

    @Override
    @Transactional
    public PigeonDTO updatePigeon(Long pigeonId, PigeonDTO pigeonDTO) {
        Pigeon pigeon = pigeonRepository.findById(pigeonId)
                .orElseThrow(() -> new PigeonNotFoundException("Pigeon not found"));
        
        if (pigeonDTO.getName() != null) pigeon.setName(pigeonDTO.getName());
        if (pigeonDTO.getRingNumber() != null) pigeon.setRingNumber(pigeonDTO.getRingNumber());
        if (pigeonDTO.getColor() != null) pigeon.setColor(pigeonDTO.getColor());
        if (pigeonDTO.getGender() != null) pigeon.setGender(pigeonDTO.getGender());
        if (pigeonDTO.getStatus() != null) pigeon.setStatus(pigeonDTO.getStatus());
        
        return pigeonMapper.toDto(pigeonRepository.save(pigeon));
    }

    @Override
    public void deletePigeon(Long pigeonId) {
        if (!pigeonRepository.existsById(pigeonId)) {
            throw new PigeonNotFoundException("Pigeon not found");
        }
        pigeonRepository.deleteById(pigeonId);
    }

    // --- Loft Management ---

    @Override
    public List<LoftDTO> getLoftsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return loftRepository.findByOwnerId(user.getId()).stream()
                .map(loftMapper::toDto)
                .toList();
    }

    @Override
    public LoftDTO createLoftForUser(Long userId, LoftDTO loftDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        Loft loft = loftMapper.toEntity(loftDTO);
        loft.setOwner(user);
        
        return loftMapper.toDto(loftRepository.save(loft));
    }

    @Override
    @Transactional
    public LoftDTO updateLoft(Long loftId, LoftDTO loftDTO) {
        Loft loft = loftRepository.findById(loftId)
                .orElseThrow(() -> new LoftNotFoundException("Loft not found"));
        
        if (loftDTO.getName() != null) loft.setName(loftDTO.getName());
        if (loftDTO.getDescription() != null) loft.setDescription(loftDTO.getDescription());
        
        return loftMapper.toDto(loftRepository.save(loft));
    }

    @Override
    public void deleteLoft(Long loftId) {
        if (!loftRepository.existsById(loftId)) {
            throw new LoftNotFoundException("Loft not found");
        }
        loftRepository.deleteById(loftId);
    }

    // --- Breeding Management ---

    @Override
    public List<BreedingSeasonDTO> getSeasonsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return seasonRepository.findByOwner(user).stream()
                .map(seasonMapper::toDto)
                .toList();
    }

    @Override
    public BreedingSeasonDTO createSeasonForUser(Long userId, BreedingSeasonDTO seasonDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        BreedingSeason season = seasonMapper.toEntity(seasonDTO);
        season.setOwner(user);
        
        return seasonMapper.toDto(seasonRepository.save(season));
    }

    @Override
    @Transactional
    public BreedingSeasonDTO updateSeason(Long seasonId, BreedingSeasonDTO seasonDTO) {
        BreedingSeason season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new BreedingSeasonNotFoundException("Season not found"));
        
        if (seasonDTO.getName() != null) season.setName(seasonDTO.getName());
        if (seasonDTO.getStartDate() != null) season.setStartDate(seasonDTO.getStartDate());
        if (seasonDTO.getEndDate() != null) season.setEndDate(seasonDTO.getEndDate());
        
        return seasonMapper.toDto(seasonRepository.save(season));
    }

    @Override
    public void deleteSeason(Long seasonId) {
        if (!seasonRepository.existsById(seasonId)) {
            throw new BreedingSeasonNotFoundException("Season not found");
        }
        seasonRepository.deleteById(seasonId);
    }

    @Override
    public List<BreedingPairDTO> getPairsForSeason(Long seasonId) {
        return pairRepository.findBySeasonId(seasonId).stream()
                .map(pairMapper::toDto)
                .toList();
    }

    @Override
    public BreedingPairDTO createPairForSeason(Long seasonId, BreedingPairDTO pairDTO) {
        BreedingSeason season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new BreedingSeasonNotFoundException("Season not found"));
        
        BreedingPair pair = pairMapper.toEntity(pairDTO);
        pair.setSeason(season);
        
        if (pairDTO.getMaleId() != null) {
            pair.setMalePigeon(pigeonRepository.findById(pairDTO.getMaleId()).orElse(null));
        }
        if (pairDTO.getFemaleId() != null) {
            pair.setFemalePigeon(pigeonRepository.findById(pairDTO.getFemaleId()).orElse(null));
        }
        
        return pairMapper.toDto(pairRepository.save(pair));
    }

    @Override
    @Transactional
    public BreedingPairDTO updatePair(Long pairId, BreedingPairDTO pairDTO) {
        BreedingPair pair = pairRepository.findById(pairId)
                .orElseThrow(() -> new BreedingPairNotFoundException("Pair not found"));
        
        if (pairDTO.getNotes() != null) pair.setNotes(pairDTO.getNotes());
        if (pairDTO.getBreedingDate() != null) pair.setBreedingDate(pairDTO.getBreedingDate());
        
        return pairMapper.toDto(pairRepository.save(pair));
    }

    @Override
    public void deletePair(Long pairId) {
        if (!pairRepository.existsById(pairId)) {
            throw new BreedingPairNotFoundException("Pair not found");
        }
        pairRepository.deleteById(pairId);
    }

    @Override
    public List<PigeonDTO> getOffspringForPair(Long pairId) {
        BreedingPair pair = pairRepository.findById(pairId)
                .orElseThrow(() -> new BreedingPairNotFoundException("Pair not found"));
        
        return pair.getOffspring().stream()
                .map(pigeonMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void addOffspringToPair(Long pairId, Long pigeonId) {
        BreedingPair pair = pairRepository.findById(pairId)
                .orElseThrow(() -> new BreedingPairNotFoundException("Pair not found"));
        
        Pigeon pigeon = pigeonRepository.findById(pigeonId)
                .orElseThrow(() -> new PigeonNotFoundException("Pigeon not found"));
        
        if (!pair.getOffspring().contains(pigeon)) {
            pair.getOffspring().add(pigeon);
            pairRepository.save(pair);
        }
    }

    @Override
    @Transactional
    public void removeOffspringFromPair(Long pairId, Long pigeonId) {
        BreedingPair pair = pairRepository.findById(pairId)
                .orElseThrow(() -> new BreedingPairNotFoundException("Pair not found"));
        
        Pigeon pigeon = pigeonRepository.findById(pigeonId)
                .orElseThrow(() -> new PigeonNotFoundException("Pigeon not found"));
        
        pair.getOffspring().remove(pigeon);
        pairRepository.save(pair);
    }

    // --- Competition Management ---

    @Override
    public List<CompetitionDTO> getCompetitionsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return competitionRepository.findByOwner(user).stream()
                .map(competitionMapper::toDto)
                .toList();
    }

    @Override
    public CompetitionDTO createCompetitionForUser(Long userId, CompetitionDTO competitionDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        Competition competition = competitionMapper.toEntity(competitionDTO);
        competition.setOwner(user);
        
        return competitionMapper.toDto(competitionRepository.save(competition));
    }

    @Override
    @Transactional
    public CompetitionDTO updateCompetition(Long competitionId, CompetitionDTO competitionDTO) {
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new CompetitionEntryNotFoundException("Competition not found"));
        
        if (competitionDTO.getName() != null) competition.setName(competitionDTO.getName());
        if (competitionDTO.getDate() != null) competition.setDate(competitionDTO.getDate());
        // ... other fields
        
        return competitionMapper.toDto(competitionRepository.save(competition));
    }

    @Override
    public void deleteCompetition(Long competitionId) {
        if (!competitionRepository.existsById(competitionId)) {
            throw new CompetitionEntryNotFoundException("Competition not found");
        }
        competitionRepository.deleteById(competitionId);
    }

    @Override
    public List<CompetitionEntryDTO> getEntriesForCompetition(Long competitionId) {
        return entryRepository.findByCompetitionId(competitionId).stream()
                .map(entryMapper::toDto)
                .toList();
    }

    @Override
    public CompetitionEntryDTO addEntryToCompetition(Long competitionId, CompetitionEntryDTO entryDTO) {
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new CompetitionEntryNotFoundException("Competition not found"));
        
        CompetitionEntry entry = entryMapper.toEntity(entryDTO);
        entry.setCompetition(competition);
        
        return entryMapper.toDto(entryRepository.save(entry));
    }

    @Override
    public void removeEntry(Long entryId) {
        entryRepository.deleteById(entryId);
    }
}
