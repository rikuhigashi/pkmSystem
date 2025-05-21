package com.example.backend.service.payment;


import com.example.backend.dto.payment.PaymentRequestDTO;
import com.example.backend.dto.payment.PaymentResponseDTO;
import com.example.backend.entity.payment.PaymentOrder;
import com.example.backend.entity.user.User;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

public interface PaymentService {

    @Transactional
    PaymentResponseDTO createPaymentOrder(User user, PaymentRequestDTO requestDTO);


    @Transactional
    boolean handlePaymentNotify(String outTradeNo, String tradeNo, BigDecimal totalAmount);

    PaymentOrder getOrderByNo(String orderNo);
}
