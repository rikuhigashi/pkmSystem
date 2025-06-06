package com.example.backend.repository.knowledge;

import com.example.backend.entity.knowledge.Knowledge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


//知识仓库
public interface KnowledgeRepository extends JpaRepository<Knowledge, Long> {
    // 搜索知识（按标题或标签）
    @Query("SELECT k FROM Knowledge k " +
            "WHERE (k.title LIKE %:query% OR :query IN elements(k.tags)) " +
            "AND k.status = 'PUBLISHED'")
    Page<Knowledge> searchKnowledge(@Param("query") String query, Pageable pageable);

    // 获取用户的知识
    Page<Knowledge> findByAuthorIdAndStatus(Integer authorId, Knowledge.Status status, Pageable pageable);



    @Query("SELECT p.knowledge FROM KnowledgePurchase p WHERE p.user.id = :userId")
    Page<Knowledge> findPurchasedByUser(@Param("userId") Integer userId, Pageable pageable);

//    // 获取已购买的知识
//    @Query("SELECT k FROM Knowledge k JOIN k.purchases p WHERE p.user.id = :userId")
//    Page<Knowledge> findPurchasedByUser(@Param("userId") Long userId, Pageable pageable);
}