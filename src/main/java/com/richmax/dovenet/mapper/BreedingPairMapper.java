package com.richmax.dovenet.mapper;

import com.richmax.dovenet.repository.data.BreedingPair;
import com.richmax.dovenet.repository.data.Pigeon;
import com.richmax.dovenet.service.data.BreedingPairDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BreedingPairMapper {

    @Mapping(source = "season.id", target = "seasonId")
    @Mapping(source = "malePigeon.id", target = "maleId")
    @Mapping(source = "femalePigeon.id", target = "femaleId")
    @Mapping(source = "malePigeon.ringNumber", target = "maleRing")
    @Mapping(source = "femalePigeon.ringNumber", target = "femaleRing")
    @Mapping(source = "offspring", target = "offspringIds")
    BreedingPairDTO toDto(BreedingPair pair);

    @Mapping(target = "season", ignore = true)
    @Mapping(target = "malePigeon", ignore = true)
    @Mapping(target = "femalePigeon", ignore = true)
    @Mapping(target = "offspring", ignore = true)
    BreedingPair toEntity(BreedingPairDTO dto);

    default List<Long> mapOffspring(List<Pigeon> offspring) {
        if (offspring == null) return List.of();
        return offspring.stream()
                .map(Pigeon::getId)
                .toList();
    }
}
