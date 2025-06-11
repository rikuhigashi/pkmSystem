package com.example.backend.dto.knowledge;

import com.example.backend.entity.knowledge.Knowledge;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class KnowledgeDTO {
    private Long id;
    private String title;
    private String content;
    private Integer authorId;
    private String authorName;
    private Double price;
    @JsonProperty("encrypted")
    private Boolean isEncrypted;
    private List<String> tags;
    private Instant createdAt;
    private Integer purchaseCount;
    private Boolean purchased; // 标记用户是否已购买

    public static KnowledgeDTO fromEntity(Knowledge knowledge) {
        return KnowledgeDTO.builder()
                .id(knowledge.getId())
                .title(knowledge.getTitle())
                .content(knowledge.getContent())
                .authorId(knowledge.getAuthor().getId())
                .authorName(knowledge.getAuthor().getUsername())
                .price(knowledge.getPrice())
                .isEncrypted(knowledge.getIsEncrypted())
                .tags(knowledge.getTags())
                .createdAt(knowledge.getCreatedAt())
                .purchaseCount(knowledge.getPurchaseCount())
                .build();
    }

    // 设置购买状态的方法
    public KnowledgeDTO withPurchased(Boolean purchased) {
        this.purchased = purchased;
        return this;
    }
}
