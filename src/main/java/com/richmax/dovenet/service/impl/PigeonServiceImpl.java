package com.richmax.dovenet.service.impl;

import com.richmax.dovenet.exception.PigeonNotFoundException;
import com.richmax.dovenet.exception.UnauthorizedActionException;
import com.richmax.dovenet.exception.UserNotFoundException;
import com.richmax.dovenet.mapper.PigeonMapper;
import com.richmax.dovenet.repository.UserRepository;
import com.richmax.dovenet.repository.data.Pigeon;
import com.richmax.dovenet.repository.PigeonRepository;
import com.richmax.dovenet.repository.data.User;
import com.richmax.dovenet.service.PigeonService;
import com.richmax.dovenet.service.data.PigeonDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PigeonServiceImpl implements PigeonService {
    private final PigeonRepository pigeonRepository;
    private final PigeonMapper pigeonMapper;
    private final UserRepository userRepository;

    public PigeonServiceImpl(PigeonRepository pigeonRepository, PigeonMapper pigeonMapper, UserRepository userRepository) {
        this.pigeonRepository = pigeonRepository;
        this.pigeonMapper = pigeonMapper;
        this.userRepository = userRepository;
    }

    @Override
    public List<PigeonDTO> getAllPigeons(String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Username does not exist"));

        return pigeonRepository.findByOwner(owner).stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public PigeonDTO getPigeonById(Long id, String username) {
        // Get user
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Username does not exist"));

        // Find pigeon
        Pigeon pigeon = pigeonRepository.findById(id)
                .orElseThrow(() -> new PigeonNotFoundException("Pigeon with ID " + id + " does not exist"));

        // Ensure it belongs to the authenticated user
        if (!pigeon.getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedActionException("You cannot access pigeons you don't own");
        }

        // Return DTO
        return convertToDto(pigeon);
    }

    @Override
    public PigeonDTO createPigeon(PigeonDTO pigeonDTO, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Username does not exist"));

        Pigeon pigeon = convertToEntity(pigeonDTO);
        pigeon.setOwner(owner);
        Pigeon saved = pigeonRepository.save(pigeon);

        return convertToDto(saved);
    }

    @Override
    @Transactional
    public PigeonDTO updatePigeon(Long id, PigeonDTO pigeonDTO, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Username does not exist"));

        Pigeon pigeon = pigeonRepository.findById(id)
                .orElseThrow(() -> new PigeonNotFoundException("Pigeon with ID " + id + " does not exist"));
        if (!pigeon.getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedActionException("You cannot update pigeons you don’t own");
        }

        // Update allowed fields
        if (pigeonDTO.getName() != null) pigeon.setName(pigeonDTO.getName());
        if (pigeonDTO.getColor() != null) pigeon.setColor(pigeonDTO.getColor());
        if (pigeonDTO.getGender() != null) pigeon.setGender(pigeonDTO.getGender());
        if (pigeonDTO.getStatus() != null) pigeon.setStatus(pigeonDTO.getStatus());
        if (pigeonDTO.getBirthDate() != null) pigeon.setBirthDate(pigeonDTO.getBirthDate());
        if (pigeonDTO.getFatherRingNumber() != null) pigeon.setFatherRingNumber(pigeonDTO.getFatherRingNumber());
        if (pigeonDTO.getMotherRingNumber() != null) pigeon.setMotherRingNumber(pigeonDTO.getMotherRingNumber());

        // Allow transferring to a new owner
        if (pigeonDTO.getOwner() != null) {
            User newOwner = userRepository.findById(pigeonDTO.getOwner().getId())
                    .orElseThrow(() -> new RuntimeException("New owner does not exist"));
            pigeon.setOwner(newOwner);
        }

        return convertToDto(pigeonRepository.save(pigeon));
    }

    @Override
    public void deletePigeon(Long id, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Username does not exist"));

        Pigeon pigeon = pigeonRepository.findById(id)
                .orElseThrow(() -> new PigeonNotFoundException("Pigeon does not exist"));

        if (!pigeon.getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("You cannot delete pigeons you don’t own");
        }

        pigeonRepository.deleteById(id);
    }

    public PigeonDTO convertToDto(Pigeon pigeon) {
        return pigeonMapper.toDto(pigeon);
    }

    public Pigeon convertToEntity(PigeonDTO pigeonDTO) {
        return pigeonMapper.toEntity(pigeonDTO);
    }
}
