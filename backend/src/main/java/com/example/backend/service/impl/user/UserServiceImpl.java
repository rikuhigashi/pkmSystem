package com.example.backend.service.impl.user;


import com.example.backend.dto.user.AdminUserDto;
import com.example.backend.dto.user.UserDto;
import com.example.backend.entity.user.User;
import com.example.backend.mapper.user.UserMapper;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }


    @Override
    public List<UserDto> getAllUsers() {
        List<User> userListData = userRepository.findAll();
        return userMapper.toDtoList(userListData);
    }


    @Override
    public List<AdminUserDto> getAllUsersWithSideData() {
        List<User> users = userRepository.findAllWithSidedata();
        return userMapper.toAdminDtoList(users);
    }



}
