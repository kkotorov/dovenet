package com.richmax.dovenet.service;

import com.richmax.dovenet.repository.data.Pigeon;
import java.util.List;

public interface PigeonService {

    List<Pigeon> getAllPigeons();

    Pigeon addPigeon(Pigeon pigeon);

    void deletePigeon(Long id);
}
