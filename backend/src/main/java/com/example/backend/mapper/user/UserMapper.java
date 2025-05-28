package com.example.backend.mapper.user;

import com.example.backend.dto.user.AdminUserDto;
import com.example.backend.dto.user.UserDto;
import com.example.backend.entity.side.Sidedatum;
import com.example.backend.entity.user.User;
import org.mapstruct.*;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    User toEntity(UserDto userDto);

    UserDto toDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserDto userDto, @MappingTarget User user);

    List<UserDto> toDtoList(List<User> userListData);

    @Mapping(target = "tiptapJsons", source = "sidedata")
    AdminUserDto toAdminDto(User user);


    default List<Map<String, Object>> mapSidedata(Set<Sidedatum> sidedata) {
        if (sidedata == null) return Collections.emptyList();
        return sidedata.stream()
                .map(Sidedatum::getTiptapJson)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    List<AdminUserDto> toAdminDtoList(List<User> users);
}