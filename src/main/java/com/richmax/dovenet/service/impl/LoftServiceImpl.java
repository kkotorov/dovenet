package com.richmax.dovenet.service.impl;

import com.richmax.dovenet.exception.LoftNotFoundException;
import com.richmax.dovenet.exception.UnauthorizedActionException;
import com.richmax.dovenet.repository.LoftRepository;
import com.richmax.dovenet.repository.UserRepository;
import com.richmax.dovenet.repository.PigeonRepository;
import com.richmax.dovenet.repository.data.Loft;
import com.richmax.dovenet.repository.data.User;
import com.richmax.dovenet.service.LoftService;
import com.richmax.dovenet.service.data.LoftDTO;
import com.richmax.dovenet.mapper.LoftMapper;

import com.richmax.dovenet.service.data.PigeonDTO;
import com.richmax.dovenet.service.data.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoftServiceImpl implements LoftService {

    private final LoftRepository loftRepository;
    private final UserRepository userRepository;
    private final PigeonRepository pigeonRepository;

    private final LoftMapper loftMapper;

    // ----------------------------------------------------
    // CREATE LOFT
    // ----------------------------------------------------
    @Override
    public LoftDTO createLoft(LoftDTO dto, String username) {

        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Username does not exist"));

        Loft loft = loftMapper.toEntity(dto);
        loft.setOwner(owner);

        Loft saved = loftRepository.save(loft);
        LoftDTO result = loftMapper.toDto(saved);

        // computed field
        result.setPigeonCount(0);

        return result;
    }

    // ----------------------------------------------------
    // GET ALL USER LOFTS
    // ----------------------------------------------------
    @Override
    public List<LoftDTO> getUserLofts(String username) {

        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return loftRepository.findByOwnerId(owner.getId())
                .stream()
                .map(loft -> {
                    LoftDTO dto = loftMapper.toDto(loft);
                    dto.setPigeonCount((int) pigeonRepository.countByLoftId(loft.getId()));
                    return dto;
                })
                .toList();
    }

    // ----------------------------------------------------
    // GET SINGLE LOFT
    // ----------------------------------------------------
    @Override
    public LoftDTO getLoftById(Long id, String username) {

        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Loft loft = loftRepository.findById(id)
                .orElseThrow(() -> new LoftNotFoundException("Loft not found"));

        if (!loft.getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedActionException("You do not own this loft");
        }

        LoftDTO dto = loftMapper.toDto(loft);
        dto.setPigeonCount((int) pigeonRepository.countByLoftId(id));

        return dto;
    }

    // ----------------------------------------------------
    // UPDATE LOFT
    // ----------------------------------------------------
    @Override
    public LoftDTO updateLoft(Long id, LoftDTO dto, String username) {

        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Loft loft = loftRepository.findById(id)
                .orElseThrow(() -> new LoftNotFoundException("Loft not found"));

        if (!loft.getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedActionException("You cannot update a loft you don't own");
        }

        // Update only non-null fields
        if (dto.getName() != null) loft.setName(dto.getName());
        if (dto.getType() != null) loft.setType(dto.getType());
        if (dto.getDescription() != null) loft.setDescription(dto.getDescription());
        if (dto.getAddress() != null) loft.setAddress(dto.getAddress());
        if (dto.getCapacity() != null) loft.setCapacity(dto.getCapacity());
        if (dto.getLoftSize() != null) loft.setLoftSize(dto.getLoftSize());
        if (dto.getGpsLatitude() != null) loft.setGpsLatitude(dto.getGpsLatitude());
        if (dto.getGpsLongitude() != null) loft.setGpsLongitude(dto.getGpsLongitude());

        Loft saved = loftRepository.save(loft);

        LoftDTO result = loftMapper.toDto(saved);
        result.setPigeonCount((int) pigeonRepository.countByLoftId(id));

        return result;
    }

    // ----------------------------------------------------
    // DELETE LOFT
    // ----------------------------------------------------
    @Override
    public void deleteLoft(Long id, String username) {

        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Loft loft = loftRepository.findById(id)
                .orElseThrow(() -> new LoftNotFoundException("Loft not found"));

        if (!loft.getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedActionException("You cannot delete a loft you don't own");
        }

        // 1️⃣ Remove loft reference from pigeons
        pigeonRepository.findByLoftIdAndOwnerId(loft.getId(), owner.getId())
                .forEach(pigeon -> {
                    pigeon.setLoft(null);
                    pigeonRepository.save(pigeon);
                });

        // 2️⃣ Delete the loft
        loftRepository.delete(loft);
    }

}
