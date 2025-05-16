package com.example.backend.controller.admin;


import com.example.backend.dto.common.PageResponse;
import com.example.backend.dto.user.AdminSidedatumDto;
import com.example.backend.dto.user.AdminUserDto;
import com.example.backend.entity.side.Sidedatum;
import com.example.backend.service.impl.side.SideServiceImpl;
import com.example.backend.service.impl.user.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {


    private final UserServiceImpl userService;
    private final SideServiceImpl sideService;

    /**
     * 获取所有用户及其数据
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AdminUserDto> getAllUsersWithSideData() {
        return userService.getAllUsersWithSideData();
    }

    /**
     * 批准数据
     * @param id 数据ID
     */
    @PutMapping("/sidedata/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> approveData(@PathVariable Integer id) {
        sideService.approveData(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 拒绝数据
     * @param id 数据ID
     */
    @PutMapping("/sidedata/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> rejectData(@PathVariable Integer id) {
        sideService.rejectData(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取所有待审核数据
     */
    @GetMapping("/sidedata/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<AdminSidedatumDto>> getPendingData(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant expiredBefore,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {

        Page<Sidedatum> pendingData = sideService.getPendingData(name, expiredBefore, pageable);
        List<AdminSidedatumDto> dtos = pendingData.getContent().stream()
                .map(this::convertToAdminSidedatumDto)
                .collect(Collectors.toList());


        PageResponse<AdminSidedatumDto> response = new PageResponse<>(
                dtos,
                pendingData.getNumber(),
                pendingData.getSize(),
                pendingData.getTotalElements(),
                pendingData.getTotalPages()
        );
        return ResponseEntity.ok(response);

    }


    private AdminSidedatumDto convertToAdminSidedatumDto(Sidedatum side) {
        AdminSidedatumDto dto = new AdminSidedatumDto();
        dto.setId(side.getId());
        dto.setName(side.getName());
        dto.setHref(side.getHref());
        dto.setIcon(side.getIcon());
        dto.setTiptapJson(side.getTiptapJson());
        dto.setExpiredAt(side.getExpiredAt());
        dto.setStatus(side.getStatus());
        return dto;
    }

}