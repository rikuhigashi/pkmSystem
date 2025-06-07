package com.example.backend.service.impl.knowledge;

import com.example.backend.dto.knowledge.KnowledgeDTO;
import com.example.backend.dto.knowledge.KnowledgeRequest;
import com.example.backend.entity.knowledge.Knowledge;
import com.example.backend.entity.knowledge.KnowledgePurchase;
import com.example.backend.entity.user.User;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.exception.UnauthorizedException;
import com.example.backend.mapper.knowledge.KnowledgePurchaseRepository;
import com.example.backend.repository.knowledge.KnowledgeRepository;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.service.knowledge.KnowledgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
public class KnowledgeServiceImpl implements KnowledgeService {

    private final KnowledgeRepository knowledgeRepository;
    private final KnowledgePurchaseRepository purchaseRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public KnowledgeDTO createKnowledge(User user, KnowledgeRequest request) {
        Knowledge knowledge = new Knowledge();
        knowledge.setTitle(request.getTitle());
        knowledge.setContent(request.getContent());
        knowledge.setAuthor(user);
        knowledge.setIsEncrypted(request.getIsEncrypted());
        knowledge.setPrice(request.getPrice() != null ? request.getPrice() : 0.0);
        knowledge.setTags(request.getTags());
        knowledge.setStatus(Knowledge.Status.PUBLISHED);

        Knowledge saved = knowledgeRepository.save(knowledge);
        return KnowledgeDTO.fromEntity(saved);
    }

    @Transactional
    @Override
    public KnowledgeDTO updateKnowledge(Long id, User user, KnowledgeRequest request) throws UnauthorizedException {
        Knowledge knowledge = knowledgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("知识不存在"));

        if (!knowledge.getAuthor().getId().equals(user.getId())) {
            throw new UnauthorizedException("无权修改此知识");
        }

        knowledge.setTitle(request.getTitle());
        knowledge.setContent(request.getContent());
        knowledge.setIsEncrypted(request.getIsEncrypted());
        knowledge.setPrice(request.getPrice() != null ? request.getPrice() : 0.0);
        knowledge.setTags(request.getTags());

        Knowledge updated = knowledgeRepository.save(knowledge);
        return KnowledgeDTO.fromEntity(updated);
    }

    @Transactional
    @Override
    public void deleteKnowledge(Long id, User user) throws UnauthorizedException {
        Knowledge knowledge = knowledgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("知识不存在"));

        if (!knowledge.getAuthor().getId().equals(user.getId())) {
            throw new UnauthorizedException("无权删除此知识");
        }

        knowledge.setStatus(Knowledge.Status.DELETED);
        knowledgeRepository.save(knowledge);
    }

    @Override
    public Page<KnowledgeDTO> searchKnowledge(String query, Pageable pageable) {
        return knowledgeRepository.searchKnowledge(query, pageable)
                .map(KnowledgeDTO::fromEntity);
    }

    @Override
    public Page<KnowledgeDTO> getUserKnowledge(Integer userId, Pageable pageable) {
        return knowledgeRepository.findByAuthorIdAndStatus(userId, Knowledge.Status.PUBLISHED, pageable)
                .map(KnowledgeDTO::fromEntity);
    }

    @Override
    public Page<KnowledgeDTO> getPurchasedKnowledge(Long userId, Pageable pageable) {

        return knowledgeRepository.findPurchasedByUser(userId.intValue(), pageable)
                .map(KnowledgeDTO::fromEntity);
    }

    @Override
    public KnowledgeDTO getKnowledgeDetails(Long id, User user) {
        Knowledge knowledge = knowledgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("知识不存在"));

        // 检查用户是否购买或是否是作者
        boolean hasAccess = knowledge.getAuthor().getId().equals(user.getId()) ||
                purchaseRepository.existsByKnowledgeIdAndUserId(id, user.getId());

        // 如果是加密知识且用户未购买/不是作者，则隐藏内容
        if (knowledge.getIsEncrypted() && !hasAccess) {
            return KnowledgeDTO.builder()
                    .id(knowledge.getId())
                    .title(knowledge.getTitle())
                    .authorId(knowledge.getAuthor().getId())
                    .authorName(knowledge.getAuthor().getUsername())
                    .price(knowledge.getPrice())
                    .isEncrypted(true)
                    .tags(knowledge.getTags())
                    .createdAt(knowledge.getCreatedAt())
                    .purchaseCount(knowledge.getPurchaseCount())
                    .build();
        }

        return KnowledgeDTO.fromEntity(knowledge);
    }

    @Transactional
    @Override
    public void purchaseKnowledge(Long knowledgeId, User user) {
        Knowledge knowledge = knowledgeRepository.findById(knowledgeId)
                .orElseThrow(() -> new ResourceNotFoundException("知识不存在"));

        // 检查是否已购买
        if (purchaseRepository.existsByKnowledgeIdAndUserId(knowledgeId, user.getId())) {
            throw new IllegalStateException("您已购买此知识");
        }

        // 检查余额是否足够
        if (user.getBalance().compareTo(BigDecimal.valueOf(knowledge.getPrice())) < 0) {
            throw new IllegalStateException("余额不足，请先充值");
        }

        // 扣除余额
        BigDecimal newBalance = user.getBalance().subtract(BigDecimal.valueOf(knowledge.getPrice()));
        user.setBalance(newBalance);
        userRepository.save(user);


        // 创建购买记录
        KnowledgePurchase purchase = new KnowledgePurchase();
        purchase.setKnowledge(knowledge);
        purchase.setUser(user);
        purchaseRepository.save(purchase);

        // 更新购买计数
        knowledge.setPurchaseCount(knowledge.getPurchaseCount() + 1);
        knowledgeRepository.save(knowledge);
    }





}
