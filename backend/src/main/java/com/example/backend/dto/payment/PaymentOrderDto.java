package com.example.backend.dto.payment;

import com.example.backend.entity.payment.PaymentOrder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for {@link PaymentOrder}
 */
public record PaymentOrderDto(Long id,
                              String orderNo,
                              BigDecimal amount,
                              String subject,
                              String body,
                              PaymentOrder.PayType payType,
                              PaymentOrder.OrderStatus status,
                              LocalDateTime createTime,
                              LocalDateTime expireTime,
                              String transactionId
) implements Serializable {
}