package com.damvih.mapper;

import com.damvih.dto.UserDto;
import com.damvih.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface UserMapper {

    UserDto toDto(User user);

}
