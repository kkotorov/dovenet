package com.richmax.dovenet.service;

import com.richmax.dovenet.repository.data.Pigeon;
import com.richmax.dovenet.service.data.PigeonDTO;

public interface PigeonService {
    Pigeon addPigeon(Pigeon pigeon);

    void deletePigeon(Long id);

    PigeonDTO convertToDto(Pigeon pigeon);

    Pigeon convertToEntity(PigeonDTO pigeonDTO);

}
