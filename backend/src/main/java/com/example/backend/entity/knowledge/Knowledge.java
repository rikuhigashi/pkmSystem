package com.example.backend.entity.knowledge;

import com.example.backend.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;

//知识实体
@Entity
@Table(name = "knowledge")
@Getter
@Setter
public class Knowledge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content; // 存储Tiptap JSON内容

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author;

    @Column(nullable = false)
    private Double price = 0.0;

    @Column(nullable = false)
    private Boolean isEncrypted = false; //是否加密

    @ElementCollection
    @CollectionTable(name = "knowledge_tags", joinColumns = @JoinColumn(name = "knowledge_id"))
    @Column(name = "tag")
    private List<String> tags;

    @Column(nullable = false)
    private Integer purchaseCount = 0; // 购买次数

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PUBLISHED;

    public enum Status {
        DRAFT,       // 草稿
        PUBLISHED,   // 已发布
        DELETED      // 已删除
    }
}
