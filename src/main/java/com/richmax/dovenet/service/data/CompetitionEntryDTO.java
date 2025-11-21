package com.richmax.dovenet.service.data;

import lombok.Data;

@Data
public class CompetitionEntryDTO {
    private Long id;
    private CompetitionDTO competition;
    private PigeonDTO pigeon;

    private Integer place;
    private Double score;
    private Double actualDistanceKm;
    private Double flightTimeHours;
    private String notes;
}
