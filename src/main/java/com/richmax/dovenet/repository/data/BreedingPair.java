package com.richmax.dovenet.repository.data;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class BreedingPair {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "season_id")
    private BreedingSeason season;

    @ManyToOne
    @JoinColumn(name = "male_id")
    private Pigeon malePigeon;

    @ManyToOne
    @JoinColumn(name = "female_id")
    private Pigeon femalePigeon;

    private LocalDate breedingDate;

    private String notes;

    @ManyToMany
    @JoinTable(
            name = "breedingpair_offspring",
            joinColumns = @JoinColumn(name = "pair_id"),
            inverseJoinColumns = @JoinColumn(name = "pigeon_id")
    )
    private List<Pigeon> offspring = new ArrayList<>();

    //@Column(nullable = false, columnDefinition = "boolean default false")
    private boolean inbred = false;
}
