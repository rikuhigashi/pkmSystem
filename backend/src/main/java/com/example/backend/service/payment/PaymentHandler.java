package com.example.backend.service.payment;

import com.example.backend.dto.payment.PaymentRequestDTO;
import com.example.backend.dto.payment.PaymentResponseDTO;
import com.example.backend.entity.user.User;
import com.example.backend.exception.ResourceNotFoundException;

public interface PaymentHandler {
    PaymentResponseDTO handle(User user, PaymentRequestDTO request) throws ResourceNotFoundException;
}
