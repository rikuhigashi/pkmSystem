package com.example.backend.service.knowledge;

import com.example.backend.dto.knowledge.KnowledgeDTO;
import com.example.backend.dto.knowledge.KnowledgeRequest;
import com.example.backend.entity.user.User;
import com.example.backend.exception.UnauthorizedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public interface KnowledgeService {
    @Transactional
    KnowledgeDTO createKnowledge(User user, KnowledgeRequest request);

    @Transactional
    KnowledgeDTO updateKnowledge(Long id, User user, KnowledgeRequest request) throws UnauthorizedException;

    @Transactional
    void deleteKnowledge(Long id, User user) throws UnauthorizedException;

    Page<KnowledgeDTO> searchKnowledge(String query, Pageable pageable);


    Page<KnowledgeDTO> getUserKnowledge(Integer userId, Pageable pageable);

    Page<KnowledgeDTO> getPurchasedKnowledge(Long userId, Pageable pageable);

    KnowledgeDTO getKnowledgeDetails(Long id, User user);

    @Transactional
    BigDecimal purchaseKnowledge(Long knowledgeId, User user);

    boolean isKnowledgePurchased(Long knowledgeId, Integer userId);

    // 获取用户有权限的知识ID列表
    List<Long> getUserKnowledgeIds(Integer userId);
}
