package com.richmax.dovenet.service.data;

import lombok.Data;

@Data
public class LoftDTO {

    private Long id;
    private String name;
    private String type;
    private String description;

    private String address;
    private Integer capacity;

    private Double loftSize;
    private Double gpsLatitude;
    private Double gpsLongitude;

    private int pigeonCount;

    private UserDTO owner;
}
