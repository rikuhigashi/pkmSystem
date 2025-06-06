package com.example.backend.controller.knowledge;

import com.example.backend.dto.common.PageResponse;
import com.example.backend.dto.knowledge.KnowledgeDTO;
import com.example.backend.dto.knowledge.KnowledgeRequest;
import com.example.backend.entity.user.User;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.service.impl.knowledge.KnowledgeServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeServiceImpl knowledgeService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<KnowledgeDTO> createKnowledge(
            @RequestBody KnowledgeRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        // 获取当前认证用户
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        KnowledgeDTO knowledge = knowledgeService.createKnowledge(user, request);
        return ResponseEntity.ok(knowledge);
    }

    @PutMapping("/{id}")
    public ResponseEntity<KnowledgeDTO> updateKnowledge(
            @PathVariable Long id,
            @RequestBody KnowledgeRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        // 获取当前认证用户
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        KnowledgeDTO knowledge = knowledgeService.updateKnowledge(id, user, request);
        return ResponseEntity.ok(knowledge);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKnowledge(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        // 获取当前认证用户
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        knowledgeService.deleteKnowledge(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<KnowledgeDTO>> searchKnowledge(
            @RequestParam(required = false) String query,
            Pageable pageable) {
        Page<KnowledgeDTO> result = knowledgeService.searchKnowledge(query, pageable);
        return ResponseEntity.ok(new PageResponse<>(result));
    }

    @GetMapping("/my")
    public ResponseEntity<PageResponse<KnowledgeDTO>> getUserKnowledge(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable) {
        User user = getUserFromDetails(userDetails);
        Page<KnowledgeDTO> result = knowledgeService.getUserKnowledge(user.getId(), pageable);
        return ResponseEntity.ok(new PageResponse<>(result));
    }

    @GetMapping("/purchased")
    public ResponseEntity<PageResponse<KnowledgeDTO>> getPurchasedKnowledge(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable) {
        User user = getUserFromDetails(userDetails);
        Page<KnowledgeDTO> result = knowledgeService.getPurchasedKnowledge(user.getId().longValue(), pageable);
        return ResponseEntity.ok(new PageResponse<>(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<KnowledgeDTO> getKnowledgeDetails(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        // 获取当前认证用户
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        KnowledgeDTO knowledge = knowledgeService.getKnowledgeDetails(id, user);
        return ResponseEntity.ok(knowledge);
    }

    @PostMapping("/{id}/purchase")
    public ResponseEntity<Void> purchaseKnowledge(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        // 获取当前认证用户
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        knowledgeService.purchaseKnowledge(id, user);
        return ResponseEntity.ok().build();
    }

    // 从认证信息获取用户
    private User getUserFromDetails(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

}
