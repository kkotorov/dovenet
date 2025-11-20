package com.richmax.dovenet.controller;

import com.richmax.dovenet.service.data.LoftDTO;
import com.richmax.dovenet.service.LoftService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lofts")
@RequiredArgsConstructor
public class LoftController {

    private final LoftService loftService;

    @PostMapping
    public LoftDTO createLoft(
            @RequestBody LoftDTO dto,
            Authentication authentication) {
        return loftService.createLoft(dto, authentication.getName());
    }

    @GetMapping
    public List<LoftDTO> getUserLofts(Authentication authentication) {
        return loftService.getUserLofts(authentication.getName());
    }

    @GetMapping("/{id}")
    public LoftDTO getLoft(
            @PathVariable Long id,
            Authentication authentication) {
        return loftService.getLoftById(id, authentication.getName());
    }

    @PutMapping("/{id}")
    public LoftDTO updateLoft(
            @PathVariable Long id,
            @RequestBody LoftDTO dto,
            Authentication authentication) {
        return loftService.updateLoft(id, dto, authentication.getName());
    }

    @DeleteMapping("/{id}")
    public void deleteLoft(
            @PathVariable Long id,
            Authentication authentication) {
        loftService.deleteLoft(id, authentication.getName());
    }
}
