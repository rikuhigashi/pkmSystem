package com.example.backend.repository.side;

import com.example.backend.entity.side.Tag;
import com.example.backend.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Integer> {
    boolean existsByNameAndUser(String name, User user);

    List<Tag> findByUser(User user);
}