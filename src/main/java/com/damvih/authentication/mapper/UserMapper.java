package com.damvih.authentication.mapper;

import com.damvih.authentication.dto.UserDto;
import com.damvih.authentication.dto.UserResponseDto;
import com.damvih.authentication.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface UserMapper {

    UserDto toDto(User user);
    UserResponseDto toResponseDto(UserDto userDto);

}
