package com.richmax.dovenet.model;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data// generate getters,setters,hash,equals,toString for all fields
public class Pigeon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE) // Lombok won’t generate a setter
    private Long id;

    private Long ringNumber;
    private String name;
    private int age;
    private String color;
    private String gender;
    private String status; //alive, deceased, sold, active, etc

    private long fatherId;
    private long motherId;

    public Pigeon(Long ringNumber, String name, String color, String gender, String status) {
        this.ringNumber = ringNumber;
        this.name = name;
        this.color = color;
        this.gender = gender;
        this.status = status;
    }

    // Default constructor required by JPA
    public Pigeon() {
    }
}
