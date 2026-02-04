package com.richmax.dovenet.service;

import com.richmax.dovenet.service.data.LoftDTO;

import java.util.List;

public interface LoftService {

    LoftDTO createLoft(LoftDTO dto, String email);

    List<LoftDTO> getUserLofts(String email);

    LoftDTO getLoftById(Long id, String email);

    LoftDTO updateLoft(Long id, LoftDTO dto, String email);

    void deleteLoft(Long id, String email);
}
