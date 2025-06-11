package com.example.backend.service.impl.payment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.example.backend.config.AlipayConfig;
import com.example.backend.dto.payment.PaymentRequestDTO;
import com.example.backend.dto.payment.PaymentResponseDTO;
import com.example.backend.dto.payment.PaymentStatusResponse;
import com.example.backend.entity.knowledge.Knowledge;
import com.example.backend.entity.knowledge.KnowledgePurchase;
import com.example.backend.entity.payment.PaymentOrder;
import com.example.backend.entity.user.User;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.mapper.knowledge.KnowledgePurchaseRepository;
import com.example.backend.repository.knowledge.KnowledgeRepository;
import com.example.backend.repository.payment.PaymentOrderRepository;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.service.payment.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlipayServiceImpl implements PaymentService {

    private final PaymentOrderRepository paymentOrderRepository;
    private final UserRepository userRepository;
    private final KnowledgeRepository knowledgeRepository;
    private final KnowledgePurchaseRepository knowledgePurchaseRepository;
    private final AlipayConfig alipayConfig;
    private final AlipayClient alipayClient;

    private static final String PRODUCT_CODE = "FAST_INSTANT_TRADE_PAY";
    private static final int ORDER_EXPIRE_MINUTES = 15;
    private static final String CHARSET = "UTF-8"; // 添加字符集常量

    @Transactional
    @Override
    public PaymentResponseDTO createPaymentOrder(User user, PaymentRequestDTO request) {
        log.info("创建支付订单，用户: {}, 金额: {}, 知识ID: {}",
                user.getUsername(), request.amount(), request.knowledgeId());

        // 处理知识购买
        if (request.knowledgeId() != null) {
            return createKnowledgePayment(user, request);
        }
        // 处理充值
        else if (request.subject().contains("充值")) {
            return createRechargeOrder(user, request);
        }
        // 默认VIP支付
        else {
            return createVipOrder(user, request);
        }
    }

    @Transactional
    @Override
    public boolean handlePaymentNotify(String outTradeNo, String tradeNo, BigDecimal totalAmount) {
        log.info("处理支付宝异步通知，订单号: {}", outTradeNo);

        return paymentOrderRepository.findByOrderNo(outTradeNo)
                .map(order -> {
                    if (order.getStatus() != PaymentOrder.OrderStatus.CREATED) {
                        log.warn("订单已处理，忽略重复通知，订单号: {}", outTradeNo);
                        return true;
                    }

                    if (order.getAmount().compareTo(totalAmount) != 0) {
                        log.error("金额不一致，订单号: {}，通知金额: {}", outTradeNo, totalAmount);
                        return false;
                    }

                    // 更新订单状态
                    order.setStatus(PaymentOrder.OrderStatus.SUCCESS);
                    order.setTransactionId(tradeNo);
                    order.setPayTime(LocalDateTime.now());
                    paymentOrderRepository.save(order);

                    User user = userRepository.findById(order.getUser().getId())
                            .orElseThrow(() -> new RuntimeException("用户不存在"));

                    // 根据支付类型处理
                    switch (order.getPayType()) {
                        case KNOWLEDGE:
                            handleKnowledgePurchase(order);
                            break;
                        case BALANCE_RECHARGE:
                            handleBalanceRecharge(order, user);
                            break;
                        case ALIPAY: // VIP
                            handleVipPurchase(order, user);
                            break;
                    }

                    log.info("支付成功处理，订单号: {}", outTradeNo);
                    return true;
                })
                .orElseGet(() -> {
                    log.error("订单不存在，订单号: {}", outTradeNo);
                    return false;
                });
    }

    // 处理知识购买
    private void handleKnowledgePurchase(PaymentOrder order) {
        Knowledge knowledge = order.getKnowledge();
        if (knowledge == null) {
            log.error("知识购买订单缺少关联知识, 订单号: {}", order.getOrderNo());
            return;
        }

        // 创建购买记录 - 确保关联双向设置
        KnowledgePurchase purchase = new KnowledgePurchase();
        purchase.setKnowledge(knowledge);
        purchase.setUser(order.getUser());
        purchase.setPaymentOrder(order);  // 设置关联
        purchase.setPurchasedAt(Instant.now());

        // 设置反向关联
        order.setKnowledgePurchase(purchase);

        // 更新购买计数
        knowledge.setPurchaseCount(knowledge.getPurchaseCount() + 1);
        knowledgeRepository.save(knowledge);

        log.info("用户 {} 成功购买知识: {}", order.getUser().getUsername(), knowledge.getTitle());
    }

    // 处理余额充值
    private void handleBalanceRecharge(PaymentOrder order, User user) {
        BigDecimal currentBalance = user.getBalance() != null ?
                user.getBalance() : BigDecimal.ZERO;
        user.setBalance(currentBalance.add(order.getAmount()));
        userRepository.save(user);
        log.info("用户 {} 充值成功，金额: {}", user.getUsername(), order.getAmount());
    }

    // 处理VIP购买
    private void handleVipPurchase(PaymentOrder order, User user) {
        user.setVipActive(true);
        user.setVipExpireTime(LocalDateTime.now().plusDays(30));
        userRepository.save(user);
        log.info("用户 {} 成功开通VIP", user.getUsername());
    }

    @Override
    public PaymentStatusResponse getPaymentStatus(String orderNo, String username) {
        PaymentOrder order = paymentOrderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new EntityNotFoundException("订单不存在"));

        if (!order.getUser().getUsername().equals(username)) {
            throw new SecurityException("无权访问此订单");
        }

        return new PaymentStatusResponse(
                order.getOrderNo(),
                order.getStatus().name(),
                order.getTransactionId()
        );
    }

    @Override
    public boolean verifyAlipaySignature(Map<String, String> params) {
        try {
            // 1. 检查必要参数
            String[] requiredParams = {"out_trade_no", "trade_no", "total_amount", "sign", "sign_type"};
            for (String param : requiredParams) {
                if (!params.containsKey(param)) {
                    log.error("缺少必要参数: {}", param);
                    return false;
                }
            }

            // 2. 检查签名类型
            String signType = params.get("sign_type");
            if (!"RSA2".equals(signType)) {
                log.error("不支持的签名类型: {}", signType);
                return false;
            }

            // 3. 直接使用原始参数进行验签（不要过滤掉sign和sign_type）
            return AlipaySignature.rsaCheckV1(
                    params, // 关键修改：使用完整参数
                    cleanKey(alipayConfig.getAlipayPublicKey()),
                    CHARSET,
                    signType
            );
        } catch (AlipayApiException e) {
            log.error("支付宝签名验证异常: {}", e.getMessage());
            log.error("验签参数: {}", params);
            return false;
        }
    }


    private PaymentOrder buildPaymentOrder(
            User user,
            BigDecimal amount,
            String subject,
            String body,
            PaymentOrder.PayType payType
    ) {
        return PaymentOrder.builder()
                .orderNo(generateOrderNo())
                .amount(amount)
                .subject(subject)
                .body(body)
                .payType(payType)
                .status(PaymentOrder.OrderStatus.CREATED)
                .user(user)
                .createTime(LocalDateTime.now())
                .expireTime(LocalDateTime.now().plusMinutes(ORDER_EXPIRE_MINUTES))
                .build();
    }

    // 通用创建订单方法
    private PaymentResponseDTO createAlipayOrder(
            User user,
            BigDecimal amount,
            String subject,
            String body,
            PaymentOrder.PayType payType,
            Knowledge knowledge,
            Map<String, Object> payload) {

        PaymentOrder order = buildPaymentOrder(user, amount, subject, body, payType);
        order.setKnowledge(knowledge);

        order.setPayload(JSON.toJSONString(payload));
        paymentOrderRepository.save(order);

        if (payType == PaymentOrder.PayType.KNOWLEDGE && knowledge != null) {
            KnowledgePurchase purchase = new KnowledgePurchase();
            purchase.setKnowledge(knowledge);
            purchase.setUser(user);
            purchase.setPaymentOrder(order); // 设置关联
            purchase.setPurchasedAt(Instant.now());

            // 设置反向关联
            order.setKnowledgePurchase(purchase);
        }


        try {
            String payForm = generateAlipayForm(order);
            return PaymentResponseDTO.builder()
                    .orderNo(order.getOrderNo())
                    .payForm(payForm)
                    .expireTime(order.getExpireTime())
                    .build();
        } catch (AlipayApiException e) {
            log.error("支付宝接口调用失败: {}", e.getMessage());
            throw new RuntimeException("支付请求创建失败", e);
        }
    }


    // 创建知识支付订单
    private PaymentResponseDTO createKnowledgePayment(User user, PaymentRequestDTO request) {
        Long knowledgeId = request.knowledgeId();
        Knowledge knowledge = knowledgeRepository.findById(knowledgeId)
                .orElseThrow(() -> new ResourceNotFoundException("知识不存在"));

        // 验证价格是否匹配
        if (knowledge.getPrice() == null ||
                knowledge.getPrice().compareTo(request.amount().doubleValue()) != 0) {
            throw new IllegalArgumentException("知识价格不匹配");
        }

        // 构建负载数据
        Map<String, Object> payload = new HashMap<>();
        payload.put("knowledgeId", knowledgeId);
        payload.put("knowledgeTitle", knowledge.getTitle());

        return createAlipayOrder(
                user,
                request.amount(),
                request.subject(),
                "知识购买: " + knowledge.getTitle(),
                PaymentOrder.PayType.KNOWLEDGE,
                knowledge, // 关联知识
                payload
        );
    }

    // 创建VIP订单
    private PaymentResponseDTO createVipOrder(User user, PaymentRequestDTO request) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("vipType", "monthly");

        return createAlipayOrder(
                user,
                request.amount(),
                request.subject(),
                "VIP会员购买",
                PaymentOrder.PayType.ALIPAY,
                null,
                payload
        );
    }

    // 创建充值订单
    private PaymentResponseDTO createRechargeOrder(User user, PaymentRequestDTO request) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("rechargeType", "balance");

        return createAlipayOrder(
                user,
                request.amount(),
                request.subject(),
                "账户余额充值",
                PaymentOrder.PayType.BALANCE_RECHARGE,
                null,
                payload
        );
    }


    private String generateAlipayForm(PaymentOrder order) throws AlipayApiException {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(alipayConfig.getNotifyUrl());
        request.setReturnUrl(alipayConfig.getReturnUrl());

        request.setBizContent(new JSONObject()
                .fluentPut("out_trade_no", order.getOrderNo())
                .fluentPut("product_code", PRODUCT_CODE)
                .fluentPut("total_amount", String.format("%.2f", order.getAmount()))
                .fluentPut("subject", order.getSubject())
                .fluentPut("body", order.getBody())
                .toString()
        );

        AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
        if (!response.isSuccess()) {
            throw new AlipayApiException(response.getSubMsg());
        }
        return response.getBody();
    }

    private String generateOrderNo() {
        return DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now())
                + UUID.randomUUID().toString().substring(0, 8);
    }

    private String cleanKey(String key) {
        return key.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
    }
}