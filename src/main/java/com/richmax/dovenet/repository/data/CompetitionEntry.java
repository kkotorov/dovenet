    package com.richmax.dovenet.repository.data;

    import jakarta.persistence.*;
    import lombok.Data;

    @Entity
    @Data
    public class CompetitionEntry {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "competition_id")
        private Competition competition;

        @ManyToOne
        @JoinColumn(name = "pigeon_id")
        private Pigeon pigeon;

        private Integer place;          // Ranking of the pigeon
        private Double score;           // Optional points or performance score
        private Double actualDistanceKm; // Optional: actual distance flown
        private Double flightTimeHours;  // Optional: time to return
        private String notes;           // Optional notes (weather, condition, etc.)
    }
