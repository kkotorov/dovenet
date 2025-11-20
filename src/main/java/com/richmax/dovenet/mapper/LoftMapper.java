package com.richmax.dovenet.mapper;

import com.richmax.dovenet.repository.data.Loft;
import com.richmax.dovenet.service.data.LoftDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoftMapper {
    LoftDTO toDto(Loft entity);
    Loft toEntity(LoftDTO dto);
}
