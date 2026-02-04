package com.richmax.dovenet.service.impl;

import com.richmax.dovenet.exception.CompetitionEntryNotFoundException;
import com.richmax.dovenet.exception.UnauthorizedActionException;
import com.richmax.dovenet.mapper.CompetitionMapper;
import com.richmax.dovenet.repository.CompetitionRepository;
import com.richmax.dovenet.repository.UserRepository;
import com.richmax.dovenet.repository.data.Competition;
import com.richmax.dovenet.repository.data.User;
import com.richmax.dovenet.service.CompetitionService;
import com.richmax.dovenet.service.data.CompetitionDTO;
import com.richmax.dovenet.types.SubscriptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompetitionServiceImpl implements CompetitionService {

    private final CompetitionRepository competitionRepository;
    private final UserRepository userRepository;
    private final CompetitionMapper competitionMapper;

    @Override
    public CompetitionDTO createCompetition(CompetitionDTO dto, String email) {
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email " + email + " not found"));

        Competition competition = competitionMapper.toEntity(dto);
        competition.setOwner(owner);

        return competitionMapper.toDto(competitionRepository.save(competition));
    }

    @Override
    public CompetitionDTO updateCompetition(Long id, CompetitionDTO dto, String email) {
        Competition competition = competitionRepository.findById(id)
                .orElseThrow(() -> new CompetitionEntryNotFoundException("Competition not found"));

        if (!competition.getOwner().getEmail().equals(email)) {
            throw new UnauthorizedActionException("You cannot update competitions you don't own");
        }

        // Update fields
        if (dto.getName() != null) competition.setName(dto.getName());
        if (dto.getDate() != null) competition.setDate(dto.getDate());
        if (dto.getStartLatitude() != null) competition.setStartLatitude(dto.getStartLatitude());
        if (dto.getStartLongitude() != null) competition.setStartLongitude(dto.getStartLongitude());
        if (dto.getDistanceKm() != null) competition.setDistanceKm(dto.getDistanceKm());
        if (dto.getNotes() != null) competition.setNotes(dto.getNotes());
        if (dto.getTemperatureC() != null) competition.setTemperatureC(dto.getTemperatureC());
        if (dto.getWindSpeedKmH() != null) competition.setWindSpeedKmH(dto.getWindSpeedKmH());
        if (dto.getWindDirection() != null) competition.setWindDirection(dto.getWindDirection());
        if (dto.getRain() != null) competition.setRain(dto.getRain());
        if (dto.getConditionsNotes() != null) competition.setConditionsNotes(dto.getConditionsNotes());

        return competitionMapper.toDto(competitionRepository.save(competition));
    }

    @Override
    public void deleteCompetition(Long id, String email) {
        Competition competition = competitionRepository.findById(id)
                .orElseThrow(() -> new CompetitionEntryNotFoundException("Competition not found"));

        if (!competition.getOwner().getEmail().equals(email)) {
            throw new UnauthorizedActionException("You cannot delete competitions you don't own");
        }

        competitionRepository.delete(competition);
    }

    @Override
    public List<CompetitionDTO> getUserCompetitions(String email) {
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email " + email + " not found"));

        return competitionRepository.findByOwner(owner).stream()
                .map(competitionMapper::toDto)
                .toList();
    }

    @Override
    public CompetitionDTO getCompetitionById(Long id, String email) {
        Competition competition = competitionRepository.findById(id)
                .orElseThrow(() -> new CompetitionEntryNotFoundException("Competition not found"));

        if (!competition.getOwner().getEmail().equals(email)) {
            throw new UnauthorizedActionException("You cannot access competitions you don't own");
        }

        return competitionMapper.toDto(competition);
    }

    public boolean hasActiveSubscription(User user) {
        return user.getSubscription() != SubscriptionType.FREE
                && user.getSubscriptionValidUntil() != null
                && user.getSubscriptionValidUntil().isAfter(LocalDateTime.now());
    }

}
