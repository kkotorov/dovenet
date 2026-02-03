package com.richmax.dovenet.controller;

import com.richmax.dovenet.service.PigeonService;
import com.richmax.dovenet.service.data.CompetitionEntryDTO;
import com.richmax.dovenet.service.data.PigeonDTO;
import com.richmax.dovenet.service.data.PigeonPedigreeDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/pigeons")
@CrossOrigin(origins = "*")
public class PigeonController {
    private final PigeonService pigeonService;

    public PigeonController(PigeonService pigeonService) {
        this.pigeonService = pigeonService;
    }

    @GetMapping
    public List<PigeonDTO> getMyPigeons(Authentication authentication) {
        return pigeonService.getAllPigeons(authentication.getName());
    }

    @GetMapping("/{id}")
    public PigeonDTO getPigeonById(@PathVariable Long id, Authentication authentication) {
        // Pass the full Authentication object to allow role checking
        return pigeonService.getPigeonById(id, authentication);
    }

    @PostMapping
    public PigeonDTO createPigeon(@RequestBody PigeonDTO pigeonDTO, Authentication authentication) {
        return pigeonService.createPigeon(pigeonDTO, authentication.getName());
    }

    @PatchMapping("/{id}")
    public PigeonDTO updatePigeon(
            @PathVariable Long id,
            @RequestBody PigeonDTO pigeonDTO,
            Authentication authentication) {
        String username = authentication.getName();
        return pigeonService.updatePigeon(id, pigeonDTO, username);
    }

    @DeleteMapping("/{id}")
    public void deletePigeon(@PathVariable Long id, Authentication authentication) {
        pigeonService.deletePigeon(id, authentication.getName());
    }

    @GetMapping("/{id}/parents")
    public List<PigeonDTO> getPigeonParents(@PathVariable Long id, Authentication authentication) {
        return pigeonService.getPigeonParents(id, authentication.getName());
    }

    @GetMapping("/{id}/pedigree")
    public PigeonPedigreeDTO getPedigree(@PathVariable Long id, Authentication authentication) {
        return pigeonService.getPedigree(id, authentication.getName());
    }

    @GetMapping("/{id}/pedigree/pdf")
    public ResponseEntity<byte[]> downloadPedigreePdf(@PathVariable Long id, Authentication authentication) {
        byte[] pdfBytes = pigeonService.generatePedigreePdf(id, authentication.getName());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pedigree_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/search-rings")
    public List<String> searchRings(@RequestParam String q, Authentication authentication) {
        String username = authentication.getName();
        return pigeonService.searchRings(q, username);
    }

    @GetMapping("/{id}/children")
    public List<PigeonDTO> getPigeonChildren(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        return pigeonService.getPigeonChildren(id, username);
    }

    @GetMapping("/loft/{loftId}")
    public List<PigeonDTO> getPigeonsInLoft(
            @PathVariable Long loftId,
            Authentication authentication
    ) {
        return pigeonService.getPigeonsInLoft(loftId, authentication.getName());
    }

    @GetMapping("/{id}/competitions")
    public List<CompetitionEntryDTO> getCompetitionsForPigeon(@PathVariable Long id, Authentication authentication) {
        return pigeonService.getCompetitionsForPigeon(id, authentication.getName());
    }

    @GetMapping("/public/{id}")
    public PigeonDTO getPigeonPublic(@PathVariable Long id) {
        return pigeonService.getPublicPigeon(id);
    }

    @GetMapping("/public/{id}/competitions")
    public List<CompetitionEntryDTO> getPublicCompetitionsForPigeon(@PathVariable Long id, Authentication authentication) {
        return pigeonService.getCompetitionsForPigeon(id, authentication.getName());
    }
}
