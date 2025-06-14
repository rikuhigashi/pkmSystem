package com.example.backend.controller.knowledge;

import com.example.backend.annotation.CurrentUser;
import com.example.backend.dto.common.PageResponse;
import com.example.backend.dto.knowledge.KnowledgeDTO;
import com.example.backend.dto.knowledge.KnowledgeRequest;
import com.example.backend.entity.user.User;
import com.example.backend.service.impl.knowledge.KnowledgeServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeServiceImpl knowledgeService;

    @PostMapping
    public ResponseEntity<KnowledgeDTO> createKnowledge(
            @RequestBody KnowledgeRequest request,
            @CurrentUser User user) {
        KnowledgeDTO knowledge = knowledgeService.createKnowledge(user, request);
        return ResponseEntity.ok(knowledge);
    }

    @PutMapping("/{id}")
    public ResponseEntity<KnowledgeDTO> updateKnowledge(
            @PathVariable Long id,
            @RequestBody KnowledgeRequest request,
            @CurrentUser User user) {
        KnowledgeDTO knowledge = knowledgeService.updateKnowledge(id, user, request);
        return ResponseEntity.ok(knowledge);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKnowledge(
            @PathVariable Long id,
            @CurrentUser User user) {
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

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        Page<KnowledgeDTO> result = knowledgeService.searchKnowledge(query, pageable);
        return ResponseEntity.ok(new PageResponse<>(result));
    }

    @GetMapping("/my")
    public ResponseEntity<PageResponse<KnowledgeDTO>> getUserKnowledge(
            @CurrentUser User user,
            Pageable pageable) {
        Page<KnowledgeDTO> result = knowledgeService.getUserKnowledge(user.getId(), pageable);
        return ResponseEntity.ok(new PageResponse<>(result));
    }

    @GetMapping("/purchased")
    public ResponseEntity<PageResponse<KnowledgeDTO>> getPurchasedKnowledge(
            @CurrentUser User user,
            Pageable pageable) {
        Page<KnowledgeDTO> result = knowledgeService.getPurchasedKnowledge(user.getId().longValue(), pageable);
        return ResponseEntity.ok(new PageResponse<>(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<KnowledgeDTO> getKnowledgeDetails(
            @PathVariable("id") Long id,
            @CurrentUser User user) {
        KnowledgeDTO knowledge = knowledgeService.getKnowledgeDetails(id, user);
        return ResponseEntity.ok(knowledge);
    }

    @PostMapping("/{id}/purchase")
    public ResponseEntity<BigDecimal> purchaseKnowledge(
            @PathVariable Long id,
            @CurrentUser User user) {
        BigDecimal newBalance = knowledgeService.purchaseKnowledge(id, user);
        return ResponseEntity.ok(newBalance);
    }

    @GetMapping("/user-knowledge-ids")
    public ResponseEntity<List<Long>> getUserKnowledgeIds(
            @CurrentUser User user) {
        List<Long> knowledgeIds = knowledgeService.getUserKnowledgeIds(user.getId());
        return ResponseEntity.ok(knowledgeIds);
    }
}
