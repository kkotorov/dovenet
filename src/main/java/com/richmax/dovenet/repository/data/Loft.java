package com.richmax.dovenet.repository.data;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Loft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;
    private String description;

    private String address;
    private Integer capacity;

    private Double loftSize;       //sq meters
    private Double gpsLatitude;
    private Double gpsLongitude;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
}
