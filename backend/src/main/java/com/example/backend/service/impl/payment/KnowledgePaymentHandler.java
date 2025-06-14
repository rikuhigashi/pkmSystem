package com.example.backend.service.impl.payment;

import com.example.backend.dto.payment.PaymentRequestDTO;
import com.example.backend.dto.payment.PaymentResponseDTO;
import com.example.backend.entity.knowledge.Knowledge;
import com.example.backend.entity.payment.PaymentOrder;
import com.example.backend.entity.user.User;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.knowledge.KnowledgeRepository;
import com.example.backend.service.payment.PaymentHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KnowledgePaymentHandler implements PaymentHandler {

    private final KnowledgeRepository knowledgeRepository;
    private final AlipayServiceImpl alipayService;

    @Override
    public PaymentResponseDTO handle(User user, PaymentRequestDTO request) throws ResourceNotFoundException {
        Long knowledgeId = request.knowledgeId();
        Knowledge knowledge = knowledgeRepository.findById(knowledgeId)
                .orElseThrow(() -> new ResourceNotFoundException("知识不存在"));

        if (knowledge.getPrice() == null ||
                knowledge.getPrice().compareTo(request.amount().doubleValue()) != 0) {
            throw new IllegalArgumentException("知识价格不匹配");
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("knowledgeId", knowledgeId);
        payload.put("knowledgeTitle", knowledge.getTitle());

        return alipayService.createAlipayOrder(
                user,
                request.amount(),
                request.subject(),
                "知识购买: " + knowledge.getTitle(),
                PaymentOrder.PayType.KNOWLEDGE,
                knowledge,
                payload
        );
    }
}