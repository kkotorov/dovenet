package com.richmax.dovenet.controller;

import com.richmax.dovenet.model.Pigeon;
import com.richmax.dovenet.service.PigeonService;
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
