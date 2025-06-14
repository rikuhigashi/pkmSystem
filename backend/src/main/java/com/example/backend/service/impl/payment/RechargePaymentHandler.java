package com.example.backend.service.impl.payment;

import com.example.backend.dto.payment.PaymentRequestDTO;
import com.example.backend.dto.payment.PaymentResponseDTO;
import com.example.backend.entity.payment.PaymentOrder;
import com.example.backend.entity.user.User;
import com.example.backend.service.payment.PaymentHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RechargePaymentHandler implements PaymentHandler {

    private final AlipayServiceImpl alipayService;

    @Override
    public PaymentResponseDTO handle(User user, PaymentRequestDTO request) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("rechargeType", "balance");

        return alipayService.createAlipayOrder(
                user,
                request.amount(),
                request.subject(),
                "账户余额充值",
                PaymentOrder.PayType.BALANCE_RECHARGE,
                null,
                payload
        );
    }
}