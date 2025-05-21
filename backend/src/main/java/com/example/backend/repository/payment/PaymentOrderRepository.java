package com.example.backend.repository.payment;

import aj.org.objectweb.asm.commons.Remapper;
import com.example.backend.entity.payment.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> {
    Optional<PaymentOrder> findByOrderNo(String orderNo);
}