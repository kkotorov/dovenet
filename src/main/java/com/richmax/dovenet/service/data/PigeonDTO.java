package com.richmax.dovenet.service.data;

import com.richmax.dovenet.repository.data.Competition;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class PigeonDTO {
    private Long id;
    private String ringNumber;

    private String name;
    private String color;
    private String gender;
    private String status;

    private LocalDate birthDate;

    private String fatherRingNumber;
    private String motherRingNumber;

    private UserDTO owner;
    private List<Competition> competitions = new ArrayList<>();
}
