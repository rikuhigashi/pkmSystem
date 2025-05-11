package com.example.backend.dto.user;

import com.example.backend.entity.side.Sidedatum;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
public class AdminUserDto {
    private String email;
    private String username;
    private List<Map<String, Object>> tiptapJsons;


    /**
     * 侧边栏数据详情（仅管理员可见）
     */
    @Data
    public static class AdminSideData {
        private Integer id;          // 数据ID
        private String tiptapJson;   // 富文本内容
        private Sidedatum.Status status; // 审核状态
        private Instant expiredAt;   // 过期时间
    }

}
