package com.example.backend.controller.knowledge;

import com.example.backend.dto.common.PageResponse;
import com.example.backend.dto.knowledge.KnowledgeDTO;
import com.example.backend.dto.knowledge.KnowledgeRequest;
import com.example.backend.entity.user.User;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.service.impl.knowledge.KnowledgeServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        // 创建分页和排序
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(direction, sortField)
        );

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
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUserFromDetails(userDetails);


        KnowledgeDTO knowledge = knowledgeService.getKnowledgeDetails(id, user);

        // 检查用户是否已购买（包括作者）
//        boolean isPurchased = knowledgeService.isKnowledgePurchased(id, user.getId());

//        return ResponseEntity.ok(knowledge.withPurchased(isPurchased));
        return ResponseEntity.ok(knowledge);
    }

    @PostMapping("/{id}/purchase")
    public ResponseEntity<BigDecimal> purchaseKnowledge(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        BigDecimal newBalance = knowledgeService.purchaseKnowledge(id, user);
        return ResponseEntity.ok(newBalance);
    }

    // 获取用户的知识权限ID列表
    @GetMapping("/user-knowledge-ids")
    public ResponseEntity<List<Long>> getUserKnowledgeIds(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUserFromDetails(userDetails);
        List<Long> knowledgeIds = knowledgeService.getUserKnowledgeIds(user.getId());
        return ResponseEntity.ok(knowledgeIds);
    }

    // 从认证信息获取用户
    private User getUserFromDetails(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

}
