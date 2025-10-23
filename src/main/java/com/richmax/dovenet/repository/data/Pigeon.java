package com.richmax.dovenet.repository.data;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
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

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
}
