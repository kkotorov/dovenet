package com.richmax.dovenet.mapper;

import com.richmax.dovenet.repository.data.User;
import com.richmax.dovenet.service.data.UserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDto(User user);
    User toEntity(UserDTO dto);
}
