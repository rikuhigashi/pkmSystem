package com.example.backend.entity.payment;

import com.example.backend.entity.knowledge.Knowledge;
import com.example.backend.entity.knowledge.KnowledgePurchase;
import com.example.backend.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_order")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String orderNo;      // 商户订单号
    private BigDecimal amount;   // 金额（单位：元）
    private String subject;      // 订单标题
    private String body;         // 订单描述

    @Enumerated(EnumType.STRING)
    private PayType payType;     // 支付方式

    @Enumerated(EnumType.STRING)
    private OrderStatus status;  // 订单状态

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;           // 关联用户

    private LocalDateTime createTime;
    private LocalDateTime expireTime; // 订单过期时间
    private String transactionId;     // 第三方支付号
    private LocalDateTime payTime;    // 支付时间

    // 负载数据（JSON格式）
    @Column(columnDefinition = "TEXT")
    private String payload;

    // 添加与Knowledge的关联
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "knowledge_id", referencedColumnName = "id")
    private Knowledge knowledge;

    // 添加与KnowledgePurchase的关联
    @OneToOne(mappedBy = "paymentOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private KnowledgePurchase knowledgePurchase;

    public enum PayType {
        ALIPAY,
        WECHAT,
        BALANCE,        // 余额支付
        BALANCE_RECHARGE, // 余额充值
        KNOWLEDGE       // 知识购买
    }

    public enum OrderStatus {
        CREATED, SUCCESS, CLOSED, FAILED
    }
}
