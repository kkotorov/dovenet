package com.richmax.dovenet.service.data;

import lombok.Data;

@Data
public class LoftDTO {
    private Long id;
    private String name;
    private String type;
    private String description;
    private UserDTO owner;
}
