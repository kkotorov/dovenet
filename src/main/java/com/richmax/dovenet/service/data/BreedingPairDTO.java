package com.richmax.dovenet.service.data;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class BreedingPairDTO {
    private Long id;

    private Long seasonId;

    private Long maleId;
    private Long femaleId;

    private String maleRing;
    private String femaleRing;

    private LocalDate breedingDate;
    private String notes;

    private List<Long> offspringIds = new ArrayList<>();
}
