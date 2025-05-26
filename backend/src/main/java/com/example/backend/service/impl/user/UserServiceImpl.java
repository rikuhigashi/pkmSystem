package com.example.backend.service.impl.user;


import com.example.backend.dto.user.AdminUserDto;
import com.example.backend.dto.user.UserDto;
import com.example.backend.dto.user.UserUpdateRequest;
import com.example.backend.entity.user.User;
import com.example.backend.mapper.user.UserMapper;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.service.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


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

    @Transactional
    @Override
    public UserDto updateUser(Integer id, UserUpdateRequest updateRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("用户不存在"));

        // 更新用户名
        user.setUsername(updateRequest.username());
        user.setUpdatedAt(Instant.now()); // 更新修改时间

        User savedUser = userRepository.save(user); // 保存
        return userMapper.toDto(savedUser);
    }

}
