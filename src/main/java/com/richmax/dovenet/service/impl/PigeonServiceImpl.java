package com.richmax.dovenet.service.impl;

import com.richmax.dovenet.exception.PigeonNotFoundException;
import com.richmax.dovenet.exception.UserNotFoundException;
import com.richmax.dovenet.repository.data.Pigeon;
import com.richmax.dovenet.repository.PigeonRepository;
import com.richmax.dovenet.repository.data.User;
import com.richmax.dovenet.service.PigeonService;
import com.richmax.dovenet.service.UserService;
import com.richmax.dovenet.service.data.PigeonDTO;
import com.richmax.dovenet.service.data.UserDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PigeonServiceImpl implements PigeonService {
    private final PigeonRepository pigeonRepository;
    private final UserService userService;

    public PigeonServiceImpl(PigeonRepository pigeonRepository, UserService userService) {
        this.pigeonRepository = pigeonRepository;
        this.userService = userService;
    }

    public Pigeon addPigeon(Pigeon pigeon){
        return pigeonRepository.save(pigeon);
    }

    public void deletePigeon(Long id){

        if (!pigeonRepository.existsById(id)) {
            throw new PigeonNotFoundException("Pigeon with ID " + id + " does not exist");
        }

        pigeonRepository.deleteById(id);
    }

    public PigeonDTO convertToDto(Pigeon pigeon) {
        PigeonDTO dto = new PigeonDTO();
        dto.setId(pigeon.getId());
        dto.setRingNumber(pigeon.getRingNumber());
        dto.setName(pigeon.getName());
        dto.setAge(pigeon.getAge());
        dto.setColor(pigeon.getColor());
        dto.setGender(pigeon.getGender());
        dto.setStatus(pigeon.getStatus());
        dto.setFatherId(pigeon.getFatherId());
        dto.setMotherId(pigeon.getMotherId());

        if (pigeon.getOwner() != null) {
            UserDTO ownerDto = new UserDTO();
            ownerDto.setId(pigeon.getOwner().getId());
            ownerDto.setUsername(pigeon.getOwner().getUsername());
            ownerDto.setEmail(pigeon.getOwner().getEmail());
            dto.setOwner(ownerDto);
        }

        return dto;
    }

    public Pigeon convertToEntity(PigeonDTO pigeonDTO) {
        Pigeon pigeon = new Pigeon();
        pigeon.setRingNumber(pigeonDTO.getRingNumber());
        pigeon.setName(pigeonDTO.getName());
        pigeon.setAge(pigeonDTO.getAge());
        pigeon.setColor(pigeonDTO.getColor());
        pigeon.setGender(pigeonDTO.getGender());
        pigeon.setStatus(pigeonDTO.getStatus());
        pigeon.setFatherId(pigeonDTO.getFatherId());
        pigeon.setMotherId(pigeonDTO.getMotherId());

        if (pigeonDTO.getOwner() != null && pigeonDTO.getOwner().getId() != null) {
            User owner = userService.findById(pigeonDTO.getOwner().getId());
            if (owner == null) {
                throw new UserNotFoundException("Owner with ID " + pigeonDTO.getOwner().getId() + " not found");
            }
            pigeon.setOwner(owner);
        }

        return pigeon;
    }

}
