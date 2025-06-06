package com.example.backend.mapper.knowledge;

import com.example.backend.entity.knowledge.KnowledgePurchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KnowledgePurchaseRepository extends JpaRepository<KnowledgePurchase, Long> {
    boolean existsByKnowledgeIdAndUserId(Long id, Integer id1);
}