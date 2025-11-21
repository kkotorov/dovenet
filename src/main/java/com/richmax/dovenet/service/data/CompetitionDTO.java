package com.richmax.dovenet.service.data;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CompetitionDTO {
    private Long id;
    private String name;
    private LocalDate date;
    private Double startLatitude;
    private Double startLongitude;
    private Double distanceKm;
    private String notes;

    private Double temperatureC;
    private Double windSpeedKmH;
    private String windDirection;
    private Boolean rain;
    private String conditionsNotes;

    private UserDTO owner;
}
