package com.richmax.dovenet.controller;

import com.richmax.dovenet.repository.data.Pigeon;
import com.richmax.dovenet.service.impl.PigeonServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pigeons")
@CrossOrigin(origins = "*")
public class PigeonController {
    private final PigeonServiceImpl pigeonService;

    public PigeonController(PigeonServiceImpl pigeonService) {
        this.pigeonService = pigeonService;
    }

    @GetMapping
    public List<Pigeon> getAllPigeons() {
        return pigeonService.getAllPigeons();
    }

    @PostMapping
    public Pigeon addPigeon(@RequestBody Pigeon pigeon) {
        return pigeonService.addPigeon(pigeon);
    }

    @DeleteMapping("/{id}")
    public void deletePigeon(@PathVariable Long id) {
        pigeonService.deletePigeon(id);
    }
}
