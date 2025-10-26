package com.richmax.dovenet.mapper;

import com.richmax.dovenet.repository.data.User;
import com.richmax.dovenet.service.data.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    //UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    @Mapping(source = "username", target = "username")
    @Mapping(source = "email", target = "email")
    UserDTO toDto(User user);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "email", target = "email")
    User toEntity(UserDTO dto);
}
