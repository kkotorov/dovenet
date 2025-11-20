package com.richmax.dovenet.controller;

import com.richmax.dovenet.service.LoftService;
import com.richmax.dovenet.service.data.LoftDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lofts")
@RequiredArgsConstructor
public class LoftController {

    private final LoftService loftService;

    // Create a new loft
    @PostMapping
    public LoftDTO createLoft(@RequestBody LoftDTO dto, Authentication authentication) {
        return loftService.createLoft(dto, authentication.getName());
    }

    // Get all lofts of the authenticated user
    @GetMapping
    public List<LoftDTO> getUserLofts(Authentication authentication) {
        return loftService.getUserLofts(authentication.getName());
    }

    // Get a specific loft by ID
    @GetMapping("/{id}")
    public LoftDTO getLoft(@PathVariable Long id, Authentication authentication) {
        return loftService.getLoftById(id, authentication.getName());
    }

    // Update a loft by ID
    @PutMapping("/{id}")
    public LoftDTO updateLoft(@PathVariable Long id, @RequestBody LoftDTO dto, Authentication authentication) {
        return loftService.updateLoft(id, dto, authentication.getName());
    }

    // Delete a loft by ID
    @DeleteMapping("/{id}")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void deleteLoft(@PathVariable Long id, Authentication authentication) {
        loftService.deleteLoft(id, authentication.getName());
    }
}
