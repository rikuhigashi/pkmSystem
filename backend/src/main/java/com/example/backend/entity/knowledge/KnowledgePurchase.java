package com.example.backend.entity.knowledge;

import com.example.backend.entity.payment.PaymentOrder;
import com.example.backend.entity.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

//购买记录实体
@Entity
@Table(name = "knowledge_purchase")
@Getter
@Setter
public class KnowledgePurchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "knowledge_id", nullable = false)
    private Knowledge knowledge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "payment_order_id", nullable = false)
    private PaymentOrder paymentOrder;// 关联支付订单

    @CreationTimestamp
    private Instant purchasedAt;
}
