package com.example.backend.dto.payment;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class PaymentResponseDTO {
    String orderNo;
    String payForm;
    LocalDateTime expireTime;
}