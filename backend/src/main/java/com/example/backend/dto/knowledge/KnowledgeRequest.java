package com.example.backend.dto.knowledge;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * @param content Tiptap JSON内容
 */ //知识请求DTO

public record KnowledgeRequest(
        @NotBlank(message = "标题不能为空") String title,
        @NotBlank(message = "内容不能为空") String content,
        @NotNull @JsonProperty("encrypted") Boolean isEncrypted,
        Double price, List<String> tags
) {
}
