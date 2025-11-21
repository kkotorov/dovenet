package com.richmax.dovenet.mapper;

import com.richmax.dovenet.repository.data.Competition;
import com.richmax.dovenet.service.data.CompetitionDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CompetitionMapper {
    CompetitionDTO toDto(Competition competition);
    Competition toEntity(CompetitionDTO dto);
}
