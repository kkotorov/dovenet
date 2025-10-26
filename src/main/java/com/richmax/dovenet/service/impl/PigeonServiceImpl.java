package com.richmax.dovenet.service.impl;

import com.richmax.dovenet.exception.PigeonNotFoundException;
import com.richmax.dovenet.exception.UserNotFoundException;
import com.richmax.dovenet.mapper.PigeonMapper;
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
    private final PigeonMapper pigeonMapper;

    public PigeonServiceImpl(PigeonRepository pigeonRepository, PigeonMapper pigeonMapper) {
        this.pigeonRepository = pigeonRepository;
        this.pigeonMapper = pigeonMapper;
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
        return pigeonMapper.toDto(pigeon);
    }

    public Pigeon convertToEntity(PigeonDTO pigeonDTO) {
        return pigeonMapper.toEntity(pigeonDTO);
    }

}
