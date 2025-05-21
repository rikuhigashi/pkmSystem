package com.example.backend.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(

        @Size(max = 100) String username
) {}
