package com.example.backend.controller.payment;

import com.example.backend.dto.payment.PaymentRequestDTO;
import com.example.backend.service.payment.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PaymentController.class)
@Import(PaymentController.class)
@ExtendWith(MockitoExtension.class)
class PaymentControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private PaymentService paymentService;

    @Test
    @WithMockUser(username = "test@example.com")
    void createPayment_ValidRequest() throws Exception {
        // 测试逻辑保持不变
        PaymentRequestDTO request = new PaymentRequestDTO(
                new BigDecimal("5.00"),
                "测试订单"
        );

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNo").exists());
    }

    @Test
    void createPayment_InvalidAmount() throws Exception {
        PaymentRequestDTO request = new PaymentRequestDTO(
                new BigDecimal("-1.00"),
                "测试订单"
        );

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
