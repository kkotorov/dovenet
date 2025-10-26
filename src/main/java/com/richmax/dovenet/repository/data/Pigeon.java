package com.richmax.dovenet.repository.data;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data// generate getters,setters,hash,equals,toString for all fields
public class Pigeon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE) // Lombok won’t generate a setter
    private Long id;

    @Column(unique = true, nullable = false)
    private Long ringNumber;

    private String name;
    private String color;
    private String gender;
    private String status; //alive, deceased, sold, active, etc

    private LocalDate birthDate;

    private Long fatherRingNumber;
    private Long motherRingNumber;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "pigeon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Competition> competitions = new ArrayList<>();
}
