package com.example.backend.dto.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;


import java.math.BigDecimal;


public record RechargeRequestDTO(
        @NotNull(message = "金额不能为空")
        @DecimalMin(value = "0.01", message = "金额必须大于0")
        BigDecimal amount
) {
}
