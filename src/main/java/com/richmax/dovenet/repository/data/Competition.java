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

    private String name;            // Competition name
    private LocalDate date;         // Date of the competition

    private Double startLatitude;   // Starting location coordinates
    private Double startLongitude;

    private Double distanceKm;      // Planned distance (optional, km)
    private String notes;           // Optional notes about the competition

    private Double temperatureC;
    private Double windSpeedKmH;
    private String windDirection;
    private Boolean rain;
    private String conditionsNotes;


    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;             // User who owns this competition
}
