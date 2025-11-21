package com.richmax.dovenet.mapper;

import com.richmax.dovenet.repository.data.Pigeon;
import com.richmax.dovenet.service.data.PigeonDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CompetitionMapper.class})
public interface PigeonMapper {

    @Mapping(source = "loft.id", target = "loftId")
    // Map competition entries as IDs or ignore them for DTO to avoid recursion
    @Mapping(target = "competitionEntries", ignore = true)
    PigeonDTO toDto(Pigeon pigeon);

    @Mapping(target = "loft", ignore = true)
    @Mapping(target = "competitionEntries", ignore = true)
    Pigeon toEntity(PigeonDTO pigeonDTO);
}
