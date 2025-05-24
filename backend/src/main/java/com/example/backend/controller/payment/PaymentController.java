package com.example.backend.controller.payment;

import com.example.backend.dto.payment.PaymentRequestDTO;
import com.example.backend.dto.payment.PaymentResponseDTO;
import com.example.backend.dto.payment.PaymentStatusResponse;
import com.example.backend.entity.payment.PaymentOrder;
import com.example.backend.entity.user.User;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.service.impl.payment.PaymentServiceImpl;
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

    private final PaymentServiceImpl paymentService;
    private final UserRepository userRepository;

    @PostMapping("/alipay/create")
    public ResponseEntity<PaymentResponseDTO> createPayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PaymentRequestDTO request) {


        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        log.info("用户 {} 发起支付请求，金额：{} 元", user.getUsername(), request.getAmount());

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
        String outTradeNo = params.get("out_trade_no");
        String tradeNo = params.get("trade_no");
        BigDecimal totalAmount = new BigDecimal(params.get("total_amount"));

        boolean success = paymentService.handlePaymentNotify(outTradeNo, tradeNo, totalAmount);
        return success ? "success" : "failure";
    }


//     支付宝页面跳转回商家页面
    @GetMapping("/alipay/return")
    public ResponseEntity<String> handleAlipayReturn(
            @RequestParam Map<String, String> params) {

        log.info("收到支付宝同步回调，参数：{}", params);

        // 验证签名
        if (!paymentService.verifyAlipaySignature(params)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("非法请求");
        }

        // 提取订单号用于展示
        String orderNo = params.get("out_trade_no");
        return ResponseEntity.status(HttpStatus.FOUND)
//                .header(HttpHeaders.LOCATION, "http://localhost:5173/payment/success?orderNo=" + orderNo)
                .header(HttpHeaders.LOCATION, "https://pkm-system.vercel.app/payment/success?orderNo=" + orderNo)
                .build();
    }

//    支付状态查询
    @GetMapping("/status/{orderNo}")
    public ResponseEntity<PaymentStatusResponse> getPaymentStatus(
            @PathVariable String orderNo,
            @AuthenticationPrincipal UserDetails userDetails) {

        PaymentOrder order = paymentService.getOrderByNo(orderNo);

        // 验证订单归属
        if (!order.getUser().getUsername().equals(userDetails.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(
                new PaymentStatusResponse(
                        order.getOrderNo(),
                        order.getStatus().name(),
                        order.getTransactionId()
                )
        );
    }


}
