package com.example.backend.dto.payment;

import com.example.backend.entity.payment.PaymentOrder;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.backend.entity.payment.PaymentOrder}
 */
@Value
public class PaymentOrderDto implements Serializable {
    Long id;
    String orderNo;
    BigDecimal amount;
    String subject;
    String body;
    PaymentOrder.PayType payType;
    PaymentOrder.OrderStatus status;
    LocalDateTime createTime;
    LocalDateTime expireTime;
    String transactionId;
}