package com.example.backend.mapper.knowledge;

import com.example.backend.entity.knowledge.KnowledgePurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KnowledgePurchaseRepository extends JpaRepository<KnowledgePurchase, Long> {
    boolean existsByKnowledgeIdAndUserId(Long id, Integer id1);

    @Query("SELECT kp.knowledge.id FROM KnowledgePurchase kp WHERE kp.user.id = :userId")
    List<Long> findKnowledgeIdsByUserId(Integer userId);

    // 查询用户创建的知识ID
    @Query("SELECT k.id FROM Knowledge k WHERE k.author.id = :authorId")
    List<Long> findIdsByAuthorId(@Param("authorId") Integer authorId);
}