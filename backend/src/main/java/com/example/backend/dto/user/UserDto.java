package com.example.backend.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Data;


/**
 * DTO for {@link com.example.backend.entity.user.User}
 */
@Data
public class UserDto  {
    Integer id;

    @Size(max = 100)
    String username;
    @Size(max = 255)
    String email;
}