package com.example.backend.repository.user;

import com.example.backend.dto.user.UserDto;
import com.example.backend.entity.user.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.sidedata")
    List<User> findAllWithSidedata();
}