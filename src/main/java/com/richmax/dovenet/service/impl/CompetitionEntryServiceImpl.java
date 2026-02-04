package com.richmax.dovenet.service.impl;

import com.richmax.dovenet.exception.CompetitionEntryNotFoundException;
import com.richmax.dovenet.exception.UnauthorizedActionException;
import com.richmax.dovenet.mapper.CompetitionEntryMapper;
import com.richmax.dovenet.repository.*;
import com.richmax.dovenet.repository.data.*;
import com.richmax.dovenet.service.CompetitionEntryService;
import com.richmax.dovenet.service.data.CompetitionEntryDTO;
import com.richmax.dovenet.types.SubscriptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompetitionEntryServiceImpl implements CompetitionEntryService {

    private final CompetitionEntryRepository entryRepository;
    private final CompetitionRepository competitionRepository;
    private final PigeonRepository pigeonRepository;
    private final CompetitionEntryMapper entryMapper;

    @Override
    public CompetitionEntryDTO addPigeonToCompetition(CompetitionEntryDTO dto, String email) {
        Competition competition = competitionRepository.findById(dto.getCompetition().getId())
                .orElseThrow(() -> new RuntimeException("Competition not found"));

        if (!competition.getOwner().getEmail().equals(email)) {
            throw new UnauthorizedActionException("You cannot add pigeons to competitions you don't own");
        }

        Pigeon pigeon = pigeonRepository.findById(dto.getPigeon().getId())
                .orElseThrow(() -> new RuntimeException("Pigeon not found"));

        entryRepository.findByCompetitionAndPigeon(competition, pigeon)
                .ifPresent(e -> {
                    throw new RuntimeException("This pigeon is already entered in this competition");
                });

        CompetitionEntry entry = entryMapper.toEntity(dto);
        entry.setCompetition(competition);
        entry.setPigeon(pigeon);

        return entryMapper.toDto(entryRepository.save(entry));
    }

    @Override
    public CompetitionEntryDTO updateEntry(Long id, CompetitionEntryDTO dto, String email) {
        CompetitionEntry entry = entryRepository.findById(id)
                .orElseThrow(() -> new CompetitionEntryNotFoundException("Entry not found"));

        if (!entry.getCompetition().getOwner().getEmail().equals(email)) {
            throw new UnauthorizedActionException("You cannot update entries you don't own");
        }

        if (dto.getPlace() != null) entry.setPlace(dto.getPlace());
        if (dto.getScore() != null) entry.setScore(dto.getScore());
        if (dto.getActualDistanceKm() != null) entry.setActualDistanceKm(dto.getActualDistanceKm());
        if (dto.getFlightTimeHours() != null) entry.setFlightTimeHours(dto.getFlightTimeHours());
        if (dto.getNotes() != null) entry.setNotes(dto.getNotes());

        return entryMapper.toDto(entryRepository.save(entry));
    }

    @Override
    public void removeEntry(Long id, String email) {
        CompetitionEntry entry = entryRepository.findById(id)
                .orElseThrow(() -> new CompetitionEntryNotFoundException("Entry not found"));

        if (!entry.getCompetition().getOwner().getEmail().equals(email)) {
            throw new UnauthorizedActionException("You cannot remove entries you don't own");
        }

        entryRepository.delete(entry);
    }

    @Override
    public List<CompetitionEntryDTO> getEntriesForCompetition(Long competitionId, String email) {
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new RuntimeException("Competition not found"));

        if (!competition.getOwner().getEmail().equals(email)) {
            throw new UnauthorizedActionException("You cannot view entries you don't own");
        }

        return entryRepository.findByCompetition(competition)
                .stream()
                .map(entryMapper::toDto)
                .toList();
    }

    @Override
    public List<CompetitionEntryDTO> getEntriesForPigeon(Long pigeonId, String email) {
        Pigeon pigeon = pigeonRepository.findById(pigeonId)
                .orElseThrow(() -> new RuntimeException("Pigeon not found"));

        return entryRepository.findByPigeon(pigeon)
                .stream()
                .filter(e -> e.getCompetition().getOwner().getEmail().equals(email))
                .map(entryMapper::toDto)
                .toList();
    }

    public boolean hasActiveSubscription(User user) {
        return user.getSubscription() != SubscriptionType.FREE
                && user.getSubscriptionValidUntil() != null
                && user.getSubscriptionValidUntil().isAfter(LocalDateTime.now());
    }

}
