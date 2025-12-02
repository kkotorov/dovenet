package com.richmax.dovenet.mapper;

import com.richmax.dovenet.repository.data.BreedingSeason;
import com.richmax.dovenet.service.data.BreedingSeasonDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BreedingPairMapper.class})
public interface BreedingSeasonMapper {

    BreedingSeasonDTO toDto(BreedingSeason season);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "pairs", ignore = true)
    BreedingSeason toEntity(BreedingSeasonDTO dto);
}
