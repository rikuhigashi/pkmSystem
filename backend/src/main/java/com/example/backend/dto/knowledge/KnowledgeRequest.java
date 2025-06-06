package com.example.backend.dto.knowledge;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.util.List;

//知识请求DTO
@Value
public class KnowledgeRequest {
    @NotBlank(message = "标题不能为空")
    String title;

    @NotBlank(message = "内容不能为空")
    String content; // Tiptap JSON内容

    @NotNull
    Boolean isEncrypted;

    Double price;

    List<String> tags;
}
