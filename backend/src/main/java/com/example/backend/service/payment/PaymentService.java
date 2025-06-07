package com.example.backend.service.payment;



import com.example.backend.dto.payment.PaymentRequestDTO;
import com.example.backend.dto.payment.PaymentResponseDTO;
import com.example.backend.dto.payment.PaymentStatusResponse;
import com.example.backend.entity.user.User;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentService {

    @Transactional
    PaymentResponseDTO createPaymentOrder(User user, PaymentRequestDTO request);

    @Transactional
    boolean handlePaymentNotify(String outTradeNo, String tradeNo, BigDecimal totalAmount);

    PaymentStatusResponse getPaymentStatus(String orderNo, String username);

    @Transactional
    PaymentResponseDTO createRechargeOrder(User user, BigDecimal amount);

    boolean verifyAlipaySignature(Map<String, String> params);
}
