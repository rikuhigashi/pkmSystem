package com.example.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank(message = "新密码不能为空")
        String newPassword,
        @NotBlank(message = "令牌不能为空")
        String token
) {
}
