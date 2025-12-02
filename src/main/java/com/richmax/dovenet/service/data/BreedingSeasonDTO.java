package com.richmax.dovenet.service.data;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class BreedingSeasonDTO {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;

    private List<BreedingPairDTO> pairs = new ArrayList<>();
}
