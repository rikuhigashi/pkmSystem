package com.example.backend.controller.payment;

import com.example.backend.dto.payment.PaymentRequestDTO;
import com.example.backend.dto.payment.PaymentResponseDTO;
import com.example.backend.dto.payment.PaymentStatusResponse;
import com.example.backend.entity.user.User;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.service.impl.payment.AlipayServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;




@Slf4j
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final AlipayServiceImpl paymentService;
    private final UserRepository userRepository;

    @PostMapping("/alipay/create")
    public ResponseEntity<PaymentResponseDTO> createPayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PaymentRequestDTO request) {

        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

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



        log.info("签名(sign): {}", params.get("sign"));
        log.info("签名类型(sign_type): {}", params.get("sign_type"));

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
            @AuthenticationPrincipal UserDetails userDetails) {

        PaymentStatusResponse response = paymentService.getPaymentStatus(orderNo, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

//    @PostMapping("/recharge")
//    public ResponseEntity<PaymentResponseDTO> rechargeBalance(
//            @AuthenticationPrincipal UserDetails userDetails,
//            @RequestBody @Valid RechargeRequestDTO request) {
//
//        String email = userDetails.getUsername();
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("用户不存在"));
//
//        log.info("用户 {} 发起充值请求，金额：{} 元", user.getUsername(), request.amount());
//
//        try {
//            PaymentResponseDTO response = paymentService.createRechargeOrder(user, request.amount());
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            log.error("充值订单创建失败: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
}
