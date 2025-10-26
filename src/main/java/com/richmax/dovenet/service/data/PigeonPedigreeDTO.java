package com.richmax.dovenet.service.data;

import lombok.Data;

@Data
public class PigeonPedigreeDTO {
    private PigeonDTO pigeon;
    private PigeonDTO father;
    private PigeonDTO mother;
    private PigeonDTO paternalGrandfather;
    private PigeonDTO paternalGrandmother;
    private PigeonDTO maternalGrandfather;
    private PigeonDTO maternalGrandmother;
}
