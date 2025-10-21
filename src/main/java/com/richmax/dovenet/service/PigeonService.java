package com.richmax.dovenet.service;

import com.richmax.dovenet.model.Pigeon;
import com.richmax.dovenet.repository.PigeonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PigeonService {
    private final PigeonRepository pigeonRepository;

    public PigeonService(PigeonRepository pigeonRepository) {
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
