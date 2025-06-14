package com.example.backend.controller.payment;

import com.example.backend.annotation.CurrentUser;
import com.example.backend.dto.payment.PaymentRequestDTO;
import com.example.backend.dto.payment.PaymentResponseDTO;
import com.example.backend.dto.payment.PaymentStatusResponse;
import com.example.backend.entity.user.User;
import com.example.backend.service.impl.payment.AlipayServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Map;




@Slf4j
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final AlipayServiceImpl paymentService;

    @PostMapping("/alipay/create")
    public ResponseEntity<PaymentResponseDTO> createPayment(
            @CurrentUser User user,
            @Valid @RequestBody PaymentRequestDTO request) {
        try {
            PaymentResponseDTO response = paymentService.createPaymentOrder(user, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("支付订单创建失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/alipay/notify")
    public String handleAlipayNotify(@RequestParam Map<String, String> params) {
        log.info("接收到支付宝异步通知，参数：{}", params);

        if (!paymentService.verifyAlipaySignature(params)) {
            log.error("支付宝通知签名验证失败");
            return "failure";
        }

        String outTradeNo = params.get("out_trade_no");
        String tradeNo = params.get("trade_no");
        BigDecimal totalAmount = new BigDecimal(params.get("total_amount"));

        boolean success = paymentService.handlePaymentNotify(outTradeNo, tradeNo, totalAmount);
        return success ? "success" : "failure";
    }

    @GetMapping("/alipay/return")
    public ResponseEntity<String> handleAlipayReturn(
            @RequestParam Map<String, String> params) {
        log.info("收到支付宝同步回调，参数：{}", params);

        if (!paymentService.verifyAlipaySignature(params)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("非法请求");
        }

        String orderNo = params.get("out_trade_no");
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, "https://pkm-system.vercel.app/payment/success?orderNo=" + orderNo)
                .build();
    }

    @GetMapping("/status/{orderNo}")
    public ResponseEntity<PaymentStatusResponse> getPaymentStatus(
            @PathVariable String orderNo,
            @CurrentUser User user) {
        PaymentStatusResponse response = paymentService.getPaymentStatus(orderNo, user.getUsername());
        return ResponseEntity.ok(response);
    }
}
