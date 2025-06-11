package com.example.backend.service.impl.knowledge;

import com.example.backend.dto.knowledge.KnowledgeDTO;
import com.example.backend.dto.knowledge.KnowledgeRequest;
import com.example.backend.entity.knowledge.Knowledge;
import com.example.backend.entity.knowledge.KnowledgePurchase;
import com.example.backend.entity.payment.PaymentOrder;
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
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
        knowledge.setTitle(request.title());
        knowledge.setContent(request.content());
        knowledge.setAuthor(user);
        knowledge.setIsEncrypted(request.isEncrypted());
        knowledge.setPrice(request.price() != null ? request.price() : 0.0);
        knowledge.setTags(request.tags());
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

        knowledge.setTitle(request.title());
        knowledge.setContent(request.content());
        knowledge.setIsEncrypted(request.isEncrypted());
        knowledge.setPrice(request.price() != null ? request.price() : 0.0);
        knowledge.setTags(request.tags());

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
        if (StringUtils.hasText(query)) {
            // 使用支持分页的搜索方法
            return knowledgeRepository.findByTitleContainingIgnoreCaseAndStatus(
                    query,
                    Knowledge.Status.PUBLISHED,
                    pageable
            ).map(KnowledgeDTO::fromEntity);
        } else {
            return knowledgeRepository.findByStatus(
                    Knowledge.Status.PUBLISHED,
                    pageable
            ).map(KnowledgeDTO::fromEntity);
        }
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

    // 获取用户已购买的知识ID列表
    public List<Long> getPurchasedKnowledgeIds(Integer userId) {
        return purchaseRepository.findKnowledgeIdsByUserId(userId);
    }


    @Override
    public KnowledgeDTO getKnowledgeDetails(Long id, User user) {
        Knowledge knowledge = knowledgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("知识不存在"));

        // 检查用户是否是作者
        boolean isAuthor = knowledge.getAuthor().getId().equals(user.getId());


        // 如果是作者直接返回完整内容
        if (isAuthor) {
            KnowledgeDTO dto = KnowledgeDTO.fromEntity(knowledge);
            dto.setPurchased(true); // 标记为已购买
            return dto;
        }

        // 检查用户是否购买
        boolean hasPurchased = purchaseRepository.existsByKnowledgeIdAndUserId(id, user.getId());

        // 如果是加密知识且用户未购买/不是作者，则隐藏内容
        if (knowledge.getIsEncrypted() && !hasPurchased) {
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

        // 创建DTO并设置购买状态
        KnowledgeDTO dto = KnowledgeDTO.fromEntity(knowledge);
        dto.setPurchased(isAuthor || hasPurchased); // 设置购买状态

        return dto;
    }

    @Transactional
    @Override
    public BigDecimal purchaseKnowledge(Long knowledgeId, User user) {
        Knowledge knowledge = knowledgeRepository.findById(knowledgeId)
                .orElseThrow(() -> new ResourceNotFoundException("知识不存在"));

        // 检查是否已购买
        if (purchaseRepository.existsByKnowledgeIdAndUserId(knowledgeId, user.getId())) {
            return user.getBalance();
        }

        // 检查余额是否足够
        if (user.getBalance().compareTo(BigDecimal.valueOf(knowledge.getPrice())) < 0) {
            throw new IllegalStateException("余额不足，请先充值");
        }

        // 扣除余额
        BigDecimal newBalance = user.getBalance().subtract(BigDecimal.valueOf(knowledge.getPrice()));
        user.setBalance(newBalance);
        userRepository.save(user);

        PaymentOrder paymentOrder = PaymentOrder.builder()
                .orderNo("BALANCE-" + System.currentTimeMillis() + "-" + knowledgeId)
                .amount(BigDecimal.valueOf(knowledge.getPrice()))
                .subject("知识购买: " + knowledge.getTitle())
                .body("余额支付购买知识")
                .payType(PaymentOrder.PayType.BALANCE)
                .status(PaymentOrder.OrderStatus.SUCCESS)
                .user(user)
                .knowledge(knowledge)
                .createTime(LocalDateTime.now())
                .payTime(LocalDateTime.now())
                .build();


        // 创建购买记录
        KnowledgePurchase purchase = new KnowledgePurchase();
        purchase.setKnowledge(knowledge);
        purchase.setUser(user);
        purchase.setPaymentOrder(paymentOrder);
        purchaseRepository.save(purchase);

        // 更新购买计数
        knowledge.setPurchaseCount(knowledge.getPurchaseCount() + 1);
        knowledgeRepository.save(knowledge);
        return newBalance;
    }

    @Override
    public boolean isKnowledgePurchased(Long knowledgeId, Integer userId) {
        // 获取知识
        Knowledge knowledge = knowledgeRepository.findById(knowledgeId)
                .orElseThrow(() -> new ResourceNotFoundException("知识不存在"));

        // 作者自动拥有访问权限
        if (knowledge.getAuthor().getId().equals(userId)) {
            return true;
        }

        // 检查购买记录
        return purchaseRepository.existsByKnowledgeIdAndUserId(knowledgeId, userId);
    }


    // 获取用户有权限的知识ID列表
    @Override
    public List<Long> getUserKnowledgeIds(Integer userId) {
        // 获取用户创建的知识ID
        List<Long> createdIds = knowledgeRepository.findIdsByAuthorId(userId);

        // 获取用户购买的知识ID
        List<Long> purchasedIds = purchaseRepository.findKnowledgeIdsByUserId(userId);

        // 合并并去重
        Set<Long> allIds = new HashSet<>(createdIds);
        allIds.addAll(purchasedIds);
        return new ArrayList<>(allIds);
    }

}
