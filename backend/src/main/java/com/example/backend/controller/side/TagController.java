package com.example.backend.controller.side;

import com.example.backend.dto.side.TagDto;
import com.example.backend.entity.user.User;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.service.impl.side.TagServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagServiceImpl tagService;
    private final UserRepository userRepository;

    /**
     * 创建标签
     *
     * @param tagDto 标签数据传输对象
     * @param userDetails 当前认证用户的详细信息
     * @return 创建的标签信息
     */
    @PostMapping("/createTag")
    public ResponseEntity<TagDto> createTag(
            @RequestBody TagDto tagDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return ResponseEntity.ok(tagService.createTag(tagDto, user));
    }

    /**
     * 更新标签名称
     *
     * @param tagId 标签ID
     * @param newName 新的标签名称
     * @param userDetails 当前认证用户的详细信息
     * @return 更新后的标签信息
     */
    @PatchMapping("/{tagId}/name")
    public ResponseEntity<TagDto> updateTagName(
            @PathVariable Integer tagId,
            @RequestParam String newName,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return ResponseEntity.ok(tagService.updateTagName(tagId, newName, user));
    }

    /**
     * 删除标签
     *
     * @param tagId 标签ID
     * @param userDetails 当前认证用户的详细信息
     * @return 无内容响应
     */
    @DeleteMapping("/{tagId}")
    public ResponseEntity<Void> deleteTag(
            @PathVariable Integer tagId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        tagService.deleteTag(tagId, user);
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取用户的所有标签
     *
     * @param userDetails 当前认证用户的详细信息
     * @return 用户标签列表
     */
    @GetMapping("/getUserTags")
    public ResponseEntity<List<TagDto>> getUserTags(
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return ResponseEntity.ok(tagService.getUserTags(user));
    }
}
