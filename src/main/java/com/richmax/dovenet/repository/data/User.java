package com.richmax.dovenet.repository.data;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@Column(unique = true)
    private String username;
    //@Column(unique = true)
    private String email;
    private String password;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Pigeon> pigeons;
}
