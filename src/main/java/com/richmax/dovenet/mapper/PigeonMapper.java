package com.richmax.dovenet.mapper;

import com.richmax.dovenet.repository.data.Pigeon;
import com.richmax.dovenet.service.data.PigeonDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PigeonMapper {

    @Mapping(source = "loft.id", target = "loftId")
    PigeonDTO toDto(Pigeon pigeon);

    // When converting DTO → Entity, ignore loft for now
    // Loft will be set manually in your service using loftId
    @Mapping(target = "loft", ignore = true)
    Pigeon toEntity(PigeonDTO pigeonDTO);
}
