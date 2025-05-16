package com.example.backend.service.user;

import com.example.backend.dto.user.AdminUserDto;
import com.example.backend.dto.user.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    List<AdminUserDto> getAllUsersWithSideData();


}
