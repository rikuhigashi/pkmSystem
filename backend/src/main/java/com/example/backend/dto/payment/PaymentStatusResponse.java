package com.example.backend.dto.payment;

public record PaymentStatusResponse(
        String orderNo,
        String status,  // SUCCESS, CREATED, EXPIRED, FAILED
        String transactionId
) {
}
