package com.richmax.dovenet.repository.data;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Competition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;       // name of the competition
    private LocalDate date;    // when it happened
    private int place;         // the pigeon’s ranking
    private double score;      // optional, points earned
    private String notes;      // extra info, e.g., "good weather, fast flight"

    @ManyToOne
    @JoinColumn(name = "pigeon_id")
    private Pigeon pigeon;     // the pigeon this competition record belongs to
}
