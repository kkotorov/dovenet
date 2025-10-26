package com.richmax.dovenet.service;

import com.richmax.dovenet.repository.data.Pigeon;
import com.richmax.dovenet.service.data.PigeonDTO;

import java.util.List;

public interface PigeonService {
    List<PigeonDTO> getAllPigeons(String username);
    PigeonDTO createPigeon(PigeonDTO pigeonDTO, String username);
    PigeonDTO updatePigeon(Long id, PigeonDTO pigeonDTO, String username);
    void deletePigeon(Long id, String username);
    PigeonDTO convertToDto(Pigeon pigeon);
    Pigeon convertToEntity(PigeonDTO pigeonDTO);
}
