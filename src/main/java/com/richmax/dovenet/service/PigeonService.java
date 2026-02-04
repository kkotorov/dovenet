package com.richmax.dovenet.service;

import com.richmax.dovenet.repository.data.Pigeon;
import com.richmax.dovenet.service.data.CompetitionEntryDTO;
import com.richmax.dovenet.service.data.PigeonDTO;
import com.richmax.dovenet.service.data.PigeonPedigreeDTO;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface PigeonService {
    List<PigeonDTO> getAllPigeons(String email);

    PigeonDTO getPigeonById(Long id, Authentication authentication);

    PigeonDTO createPigeon(PigeonDTO pigeonDTO, String email);

    PigeonDTO updatePigeon(Long id, PigeonDTO pigeonDTO, String email);

    void deletePigeon(Long id, String email);

    List<PigeonDTO> getPigeonParents(Long id, String email);

    PigeonDTO convertToDto(Pigeon pigeon);

    Pigeon convertToEntity(PigeonDTO pigeonDTO);

    PigeonPedigreeDTO getPedigree(Long pigeonId, String email);

    byte[] generatePedigreePdf(Long id, String email);

    List<String> searchRings(String q, String email);

    List<PigeonDTO> getPigeonChildren(Long id, String email);

    List<PigeonDTO> getPigeonsInLoft(Long loftId, String email);

    List<CompetitionEntryDTO> getCompetitionsForPigeon(Long pigeonId, String email);

    PigeonDTO getPublicPigeon(Long pigeonId);
}
