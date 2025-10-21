package com.richmax.dovenet.service.impl;

import com.richmax.dovenet.repository.data.Pigeon;
import com.richmax.dovenet.repository.PigeonRepository;
import com.richmax.dovenet.service.PigeonService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PigeonServiceImpl implements PigeonService {
    private final PigeonRepository pigeonRepository;

    public PigeonServiceImpl(PigeonRepository pigeonRepository) {
        this.pigeonRepository = pigeonRepository;
    }

    public List<Pigeon> getAllPigeons() {
        return pigeonRepository.findAll();
    }

    public Pigeon addPigeon(Pigeon pigeon){
        return pigeonRepository.save(pigeon);
    }

    public void deletePigeon(Long id){
        pigeonRepository.deleteById(id);
    }
}
