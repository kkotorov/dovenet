package com.richmax.dovenet.service.data;

import lombok.Data;

@Data
public class PigeonDTO {
    private Long id;
    private Long ringNumber;
    private String name;
    private int age;
    private String color;
    private String gender;
    private String status;
    private Long fatherId;
    private Long motherId;

    private UserDTO owner;
}
