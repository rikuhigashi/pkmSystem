package com.example.backend.entity.side;

import com.example.backend.entity.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "sidedata", indexes = {
        @Index(name = "idx_created_at", columnList = "created_at"), // 排序字段索引
        @Index(name = "idx_status", columnList = "status"),         // 过滤字段索引
        @Index(name = "idx_status_created_at", columnList = "status, createdAt DESC")
})
public class Sidedatum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @Column(name = "icon")
    private String icon;

    @ColumnDefault("0")
    @Column(name = "current")
    private Boolean current;

    @Size(max = 255)
    @Column(name = "href")
    private String href;

    @Size(max = 255)
    @Column(name = "name")
    private String name;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    @CreationTimestamp
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    @UpdateTimestamp
    private Instant updatedAt;


    @Column(name = "tiptap_json", columnDefinition = "JSON")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> tiptapJson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;


    private Instant expiredAt; // 数据过期时间

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @ColumnDefault("'PENDING'") // 初始状态为待审核
    private Status status;      // 审核状态

    @Column(columnDefinition = "TEXT")
    private String rejectReason; //拒绝原因


    public enum Status {
        PENDING,  // 待审核
        APPROVED, // 已通过
        REJECTED,  // 已拒绝
        REJECTED_PENDING //待处理拒绝
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;
}