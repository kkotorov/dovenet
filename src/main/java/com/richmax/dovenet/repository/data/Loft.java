package com.richmax.dovenet.repository.data;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Loft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;        // e.g. Racing Loft
    private String type;        // Racing, Breeding, Young Birds
    private String description; // optional

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
}
