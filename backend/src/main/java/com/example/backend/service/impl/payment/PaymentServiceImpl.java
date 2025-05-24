package com.example.backend.service.impl.payment;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.example.backend.dto.payment.PaymentRequestDTO;
import com.example.backend.dto.payment.PaymentResponseDTO;
import com.example.backend.entity.payment.PaymentOrder;
import com.example.backend.entity.user.User;
import com.example.backend.repository.payment.PaymentOrderRepository;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.service.payment.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentOrderRepository paymentOrderRepository;
    private final UserRepository userRepository;

    @Value("${alipay.app-id}")
    private String appId;

    @Value("${alipay.merchant-private-key}")
    private String merchantPrivateKey;

    @Value("${alipay.alipay-public-key}")
    private String alipayPublicKey;

    @Value("${alipay.notify-url}")
    private String notifyUrl;

    private static final String GATEWAY_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    private static final String CHARSET = "UTF-8";
    private static final String FORMAT = "JSON";
    private static final String SIGN_TYPE = "RSA2";
    private static final String PRODUCT_CODE = "FAST_INSTANT_TRADE_PAY";
    private static final int ORDER_EXPIRE_MINUTES = 15;

    private String cleanKey(String key) {
        return key.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", ""); // 去除所有空格和换行
    }


    /**
     * 验证支付宝签名
     * @param params 支付宝回调请求的所有参数
     * @return 验证结果
     */
    public boolean verifyAlipaySignature(Map<String, String> params) {
        try {
            // 检查必要参数是否存在
            if (!params.containsKey("sign") || !params.containsKey("sign_type")) {
                log.error("签名验证失败：缺少必要参数 sign 或 sign_type");
                return false;
            }

            //  获取签名类型
            String signType = params.get("sign_type");
            if (!"RSA2".equals(signType)) {
                log.error("不支持的签名类型：{}", signType);
                return false;
            }

            // 移除不需要参与签名的参数
            Map<String, String> filteredParams = params.entrySet().stream()
                    .filter(entry -> !"sign".equals(entry.getKey()))
                    .filter(entry -> !"sign_type".equals(entry.getKey()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue
                    ));

            // 使用支付宝SDK验证签名
            boolean isValid = AlipaySignature.rsaCheckV1(
                    filteredParams,
                    alipayPublicKey,
                    "UTF-8",
                    signType
            );

            if (!isValid) {
                log.warn("支付宝签名验证失败，可能遭受非法请求");
            }
            return isValid;

        } catch (AlipayApiException e) {
            log.error("支付宝签名验证异常：{}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("系统异常：{}", e.getMessage());
            return false;
        }
    }


    @Transactional
    @Override
    public PaymentResponseDTO createPaymentOrder(User user, PaymentRequestDTO requestDTO) {
        // 生成唯一订单号
        String orderNo = generateOrderNo();

        // 创建并保存支付订单
        PaymentOrder order = PaymentOrder.builder()
                .orderNo(orderNo)
                .amount(requestDTO.getAmount())
                .subject(requestDTO.getSubject())
                .body("PKM System Sponsorship")
                .payType(PaymentOrder.PayType.ALIPAY)
                .status(PaymentOrder.OrderStatus.CREATED)
                .user(user)
                .createTime(LocalDateTime.now())
                .expireTime(LocalDateTime.now().plusMinutes(ORDER_EXPIRE_MINUTES))
                .build();

        paymentOrderRepository.save(order);

        try {
            // 清理私钥和公钥
            String cleanedPrivateKey = cleanKey(merchantPrivateKey);
            String cleanedPublicKey = cleanKey(alipayPublicKey);

            // 初始化支付宝客户端
            AlipayClient alipayClient = new DefaultAlipayClient(
                    GATEWAY_URL,
                    appId,
                    cleanedPrivateKey,
                    FORMAT,
                    CHARSET,
                    cleanedPublicKey,
                    SIGN_TYPE
            );

            // 创建支付请求
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            request.setNotifyUrl(notifyUrl);

            // 设置业务参数
            request.setBizContent(new JSONObject()
                    .fluentPut("out_trade_no", orderNo)
                    .fluentPut("product_code", PRODUCT_CODE)
                    .fluentPut("total_amount", requestDTO.getAmount().setScale(2, RoundingMode.HALF_UP))
                    .fluentPut("subject", requestDTO.getSubject())
//                    .fluentPut("return_url", "http://localhost:5173/payment/success")
                   .fluentPut("return_url", "https://ecd2-171-39-22-163.ngrok-free.app/payment/success")
                    .fluentPut("notify_url", notifyUrl)
                    .toString()
            );

            // 获取支付表单
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            if (!response.isSuccess()) {
                throw new AlipayApiException(response.getSubMsg());
            }

            return PaymentResponseDTO.builder()
                    .orderNo(orderNo)
                    .payForm(response.getBody())
                    .expireTime(order.getExpireTime())
                    .build();

        } catch (AlipayApiException e) {
            log.error("支付宝接口调用失败: {}", e.getMessage());
            throw new RuntimeException("支付请求创建失败", e);
        }
    }

    @Transactional
    @Override
    public boolean handlePaymentNotify(String outTradeNo, String tradeNo, BigDecimal totalAmount) {
        log.info("处理支付宝异步通知，订单号: {}", outTradeNo);

        return paymentOrderRepository.findByOrderNo(outTradeNo)
                .map(order -> {
                    // 验证订单状态
                    if (order.getStatus() != PaymentOrder.OrderStatus.CREATED) {
                        log.warn("订单已处理，忽略重复通知，订单号: {}", outTradeNo);
                        return true;
                    }

                    // 验证金额一致性
                    if (order.getAmount().compareTo(totalAmount) != 0) {
                        log.error("金额不一致，订单号: {}，通知金额: {}", outTradeNo, totalAmount);
                        return false;
                    }

                    // 更新订单状态
                    order.setStatus(PaymentOrder.OrderStatus.SUCCESS);
                    order.setTransactionId(tradeNo);
                    order.setPayTime(LocalDateTime.now());

                    // 激活用户VIP状态
                    User user = userRepository.findById(order.getUser().getId())
                            .orElseThrow(() -> new RuntimeException("用户不存在"));


                    user.setVipActive(true); // 激活VIP状态
                    user.setVipExpireTime(LocalDateTime.now().plusDays(30)); // 设置VIP过期时间为30天后

                    userRepository.save(user);
                    paymentOrderRepository.save(order);

                    log.info("支付成功处理，订单号: {}", outTradeNo);
                    return true;
                })
                .orElseGet(() -> {
                    log.error("订单不存在，订单号: {}", outTradeNo);
                    return false;
                });
    }

    private String generateOrderNo() {
        return DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now())
                + UUID.randomUUID().toString().substring(0, 8);
    }


    @Override
    public PaymentOrder getOrderByNo(String orderNo) {
        return paymentOrderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> {
                    log.error("订单不存在: {}", orderNo);
                    return new EntityNotFoundException("订单不存在");
                });
    }

}
