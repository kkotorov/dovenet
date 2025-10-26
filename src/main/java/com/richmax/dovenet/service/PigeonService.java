package com.richmax.dovenet.service;

import com.richmax.dovenet.repository.data.Pigeon;
import com.richmax.dovenet.repository.data.User;
import com.richmax.dovenet.service.data.PigeonDTO;
import com.richmax.dovenet.service.data.UserDTO;

import java.util.List;

public interface PigeonService {
    Pigeon addPigeon(Pigeon pigeon);

    void deletePigeon(Long id);

    PigeonDTO convertToDto(Pigeon pigeon);

    Pigeon convertToEntity(PigeonDTO pigeonDTO);

}
