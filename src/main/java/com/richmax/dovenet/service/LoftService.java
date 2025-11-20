package com.richmax.dovenet.service;

import com.richmax.dovenet.service.data.LoftDTO;

import java.util.List;

public interface LoftService {

    LoftDTO createLoft(LoftDTO dto, String username);

    List<LoftDTO> getUserLofts(String username);

    LoftDTO getLoftById(Long id, String username);

    LoftDTO updateLoft(Long id, LoftDTO dto, String username);

    void deleteLoft(Long id, String username);
}
