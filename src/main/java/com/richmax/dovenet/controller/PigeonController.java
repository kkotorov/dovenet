package com.richmax.dovenet.controller;

import com.richmax.dovenet.repository.PigeonRepository;
import com.richmax.dovenet.repository.UserRepository;
import com.richmax.dovenet.repository.data.Pigeon;
import com.richmax.dovenet.repository.data.User;
import com.richmax.dovenet.service.PigeonService;
import com.richmax.dovenet.service.data.PigeonDTO;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pigeons")
@CrossOrigin(origins = "*")
public class PigeonController {
    private final PigeonService pigeonService;
    private final PigeonRepository pigeonRepository;
    private final UserRepository userRepository;

    public PigeonController(PigeonService pigeonService, UserRepository userRepository, PigeonRepository pigeonRepository) {
        this.pigeonService = pigeonService;
        this.pigeonRepository = pigeonRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<PigeonDTO> getMyPigeons(Authentication authentication) {
        String username = authentication.getName();
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Username does not exist"));

        List<Pigeon> pigeons = pigeonRepository.findByOwner(owner);

        return pigeons.stream()
                .map(pigeonService::convertToDto)
                .toList();
    }

    @PostMapping
    public PigeonDTO addPigeon(@RequestBody PigeonDTO pigeonDTO, Authentication authentication) {
        String username = authentication.getName();
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Username does not exist"));

        // Convert DTO to entity
        Pigeon pigeon = pigeonService.convertToEntity(pigeonDTO);
        pigeon.setOwner(owner);

        Pigeon savedPigeon = pigeonService.addPigeon(pigeon);

        // Convert back to DTO
        return pigeonService.convertToDto(savedPigeon);
    }


    @DeleteMapping("/{id}")
    public void deletePigeon(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        User owner = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Username does not exist"));
        Pigeon pigeon = pigeonRepository.findById(id).orElseThrow(() -> new RuntimeException("Pigeon does not exist"));

        if (!pigeon.getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("You cannot delete pigeons you don’t own");
        }

        pigeonService.deletePigeon(id);
    }
}
