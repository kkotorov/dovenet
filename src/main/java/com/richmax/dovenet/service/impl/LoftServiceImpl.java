package com.richmax.dovenet.service.impl;

import com.richmax.dovenet.repository.data.Loft;
import com.richmax.dovenet.repository.data.User;
import com.richmax.dovenet.repository.UserRepository;
import com.richmax.dovenet.repository.LoftRepository;
import com.richmax.dovenet.exception.UnauthorizedActionException;
import com.richmax.dovenet.exception.LoftNotFoundException;

import com.richmax.dovenet.service.LoftService;
import com.richmax.dovenet.service.data.LoftDTO;
import com.richmax.dovenet.service.data.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoftServiceImpl implements LoftService {

    private final LoftRepository loftRepository;
    private final UserRepository userRepository;

    @Override
    public LoftDTO createLoft(LoftDTO dto, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Username does not exist"));

        Loft loft = new Loft();
        loft.setName(dto.getName());
        loft.setType(dto.getType());
        loft.setDescription(dto.getDescription());
        loft.setOwner(owner);

        return convertToDto(loftRepository.save(loft));
    }

    @Override
    public List<LoftDTO> getUserLofts(String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return loftRepository.findByOwnerId(owner.getId())
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public LoftDTO getLoftById(Long id, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Loft loft = loftRepository.findById(id)
                .orElseThrow(() -> new LoftNotFoundException("Loft not found"));

        if (!loft.getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedActionException("You do not own this loft");
        }

        return convertToDto(loft);
    }

    @Override
    public LoftDTO updateLoft(Long id, LoftDTO dto, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Loft loft = loftRepository.findById(id)
                .orElseThrow(() -> new LoftNotFoundException("Loft not found"));

        if (!loft.getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedActionException("You cannot update a loft you don't own");
        }

        if (dto.getName() != null) loft.setName(dto.getName());
        if (dto.getType() != null) loft.setType(dto.getType());
        if (dto.getDescription() != null) loft.setDescription(dto.getDescription());

        return convertToDto(loftRepository.save(loft));
    }

    @Override
    public void deleteLoft(Long id, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Loft loft = loftRepository.findById(id)
                .orElseThrow(() -> new LoftNotFoundException("Loft not found"));

        if (!loft.getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedActionException("You cannot delete a loft you don't own");
        }

        loftRepository.delete(loft);
    }

    // -------------------------------
    // Conversion Helpers
    // -------------------------------
    private LoftDTO convertToDto(Loft loft) {
        LoftDTO dto = new LoftDTO();
        dto.setId(loft.getId());
        dto.setName(loft.getName());
        dto.setType(loft.getType());
        dto.setDescription(loft.getDescription());

        UserDTO owner = new UserDTO();
        owner.setId(loft.getOwner().getId());
        owner.setUsername(loft.getOwner().getUsername());
        dto.setOwner(owner);

        return dto;
    }
}
