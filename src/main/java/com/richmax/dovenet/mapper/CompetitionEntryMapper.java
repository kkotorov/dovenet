package com.richmax.dovenet.mapper;

import com.richmax.dovenet.repository.data.CompetitionEntry;
import com.richmax.dovenet.service.data.CompetitionEntryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CompetitionMapper.class, PigeonMapper.class})
public interface CompetitionEntryMapper {

    @Mapping(target = "pigeon.competitionEntries", ignore = true)
    CompetitionEntryDTO toDto(CompetitionEntry entry);

    @Mapping(target = "id", ignore = true)
    CompetitionEntry toEntity(CompetitionEntryDTO dto);
}
