package com.richmax.dovenet.mapper;

import com.richmax.dovenet.repository.data.Pigeon;
import com.richmax.dovenet.service.data.PigeonDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PigeonMapper {

    PigeonDTO toDto(Pigeon pigeon);
    Pigeon toEntity(PigeonDTO pigeonDTO);
}
