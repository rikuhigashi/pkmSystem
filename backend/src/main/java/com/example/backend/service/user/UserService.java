package com.example.backend.service.user;

import com.example.backend.dto.user.AdminUserDto;
import com.example.backend.dto.user.UserDto;
import com.example.backend.dto.user.UserUpdateRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    List<AdminUserDto> getAllUsersWithSideData();

    @Transactional
    UserDto updateUser(Integer id, UserUpdateRequest updateRequest);
}
