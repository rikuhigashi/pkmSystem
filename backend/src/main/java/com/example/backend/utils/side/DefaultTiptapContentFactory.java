package com.example.backend.utils.side;

import java.util.*;

/**
 * 用于生成新用户注册时的默认富文本内容
 */
public class DefaultTiptapContentFactory {

    public static Map<String, Object> createWelcomeDocument() {
        return Map.of(
                "type", "doc",
                "content", List.of(
                        heading("欢迎使用 PKM 系统", 1),
                        paragraph("这是你的第一个知识文档，可以用来记录笔记、想法或任务清单。"),
                        shortcutList(),
                        image("https://via.placeholder.com/600x200", "示例图片", "拖放图片到编辑器即可上传")
                )
        );
    }

    private static Map<String, Object> heading(String text, int level) {
        return Map.of(
                "type", "heading",
                "attrs", Map.of("level", level),
                "content", List.of(text(text))
        );
    }

    private static Map<String, Object> paragraph(String text) {
        return Map.of(
                "type", "paragraph",
                "content", List.of(text(text))
        );
    }

    private static Map<String, Object> image(String src, String alt, String title) {
        return Map.of(
                "type", "paragraph",
                "content", List.of(
                        Map.of("type", "image", "attrs", Map.of(
                                "src", src,
                                "alt", alt,
                                "title", title
                        ))
                )
        );
    }

    private static Map<String, Object> shortcutList() {
        return Map.of(
                "type", "bulletList",
                "content", List.of(
                        listItem(List.of(
                                text("加粗：Ctrl+B ("),
                                markedText("示例", "bold"),
                                text(")")
                        )),
                        listItem(List.of(
                                text("斜体：Ctrl+I ("),
                                markedText("示例", "italic"),
                                text(")")
                        )),
                        listItem(List.of(
                                text("链接：Ctrl+K ("),
                                markedText("示例链接", "link", Map.of("href", "https://example.com")),
                                text(")")
                        )),
                        listItem(List.of(
                                text("颜色 + 高亮 ("),
                                markedText("示例文本", List.of(
                                        Map.of("type", "textStyle", "attrs", Map.of("color", "#3B82F6")),
                                        Map.of("type", "highlight", "attrs", Map.of("color", "#FDE047"))
                                )),
                                text(")")
                        ))
                )
        );
    }

    private static Map<String, Object> listItem(List<Map<String, Object>> textContent) {
        return Map.of(
                "type", "listItem",
                "content", List.of(
                        Map.of(
                                "type", "paragraph",
                                "content", textContent
                        )
                )
        );
    }

    private static Map<String, Object> text(String value) {
        return Map.of("type", "text", "text", value);
    }

    private static Map<String, Object> markedText(String value, String markType) {
        return Map.of(
                "type", "text",
                "text", value,
                "marks", List.of(Map.of("type", markType))
        );
    }

    private static Map<String, Object> markedText(String value, String markType, Map<String, Object> attrs) {
        return Map.of(
                "type", "text",
                "text", value,
                "marks", List.of(Map.of("type", markType, "attrs", attrs))
        );
    }

    private static Map<String, Object> markedText(String value, List<Map<String, Object>> marks) {
        return Map.of(
                "type", "text",
                "text", value,
                "marks", marks
        );
    }
}
