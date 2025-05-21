package com.example.backend.utils.Image;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
public class ImageUtils {

    /**
     * 压缩JSON中的Base64图片
     */
    public static void compressBase64ImagesInJson(Map<String, Object> json, double quality) {
        List<Map<String, Object>> images = extractImagesFromJson(json);
        images.forEach(img -> {
            String base64 = (String) img.get("src");
            String compressed = compressBase64(base64, quality);
            img.put("src", compressed);
        });
    }

    /**
     * 压缩Base64图片
     */
    private static String compressBase64(String base64, double quality) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64.split(",")[1]);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Thumbnails.of(inputStream)
                    .scale(1.0)
                    .outputQuality(quality)
                    .outputFormat("JPEG")
                    .toOutputStream(outputStream);

            return "data:image/jpeg;base64," +
                    Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            log.error("图片压缩失败", e);
            return base64; // 返回原数据
        }
    }

    /**
     * 提取JSON中的图片节点
     */
    private static List<Map<String, Object>> extractImagesFromJson(Map<String, Object> json) {
        List<Map<String, Object>> images = new ArrayList<>();
        traverseJson(json, images);
        return images;
    }

    /**
     * 递归遍历JSON结构
     */
    private static void traverseJson(Object node, List<Map<String, Object>> images) {
        if (node instanceof Map) {
            Map<String, Object> mapNode = (Map<String, Object>) node;
            if (isImageNode(mapNode)) {
                Map<String, Object> attrs = (Map<String, Object>) mapNode.get("attrs");
                if (attrs.containsKey("src")) {
                    images.add(attrs);
                }
            }
            // 递归遍历所有值
            for (Object value : mapNode.values()) {
                traverseJson(value, images);
            }
        } else if (node instanceof List) {
            for (Object item : (List<?>) node) {
                traverseJson(item, images);
            }
        }
    }

    /**
     * 判断是否为图片节点
     */
    private static boolean isImageNode(Map<String, Object> node) {
        return node.containsKey("type") &&
                "image".equals(node.get("type")) &&
                node.containsKey("attrs") &&
                node.get("attrs") instanceof Map;
    }


}
