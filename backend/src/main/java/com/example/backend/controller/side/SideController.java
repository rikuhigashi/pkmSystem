package com.example.backend.controller.side;

import com.example.backend.dto.side.SidedatumDto;
import com.example.backend.entity.side.Sidedatum;
import com.example.backend.entity.user.User;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.service.side.SideService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/sideData")
public class SideController {

    private final SideService sideService;
    private final UserRepository userRepository;

    @Autowired
    public SideController(SideService sideService, UserRepository userRepository) {
        this.sideService = sideService;
        this.userRepository = userRepository;
    }


    @GetMapping("/all")
    public ResponseEntity<List<SidedatumDto>> getAllSideData(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        List<SidedatumDto> data = sideService.getAllSideDataByUser(user);
        return ResponseEntity.ok(data);
    }


    //    通过id获取数据
    @GetMapping("/full/{id}")
    public ResponseEntity<SidedatumDto> getFullData(@PathVariable Integer id, @AuthenticationPrincipal UserDetails userDetails) {



        String email = userDetails.getUsername();
        SidedatumDto data = sideService.getFullSidedata(id, email);

        return ResponseEntity.ok(data);
    }


    //    添加数据
    @PostMapping("/add")
    public ResponseEntity<SidedatumDto> addSideData(@RequestBody SidedatumDto sidedatumDto, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("用户不存在"));


        // 设置用户 ID
        SidedatumDto updatedDto = sidedatumDto.toBuilder()
                .userId(user.getId())  // 设置新的 userId
                .status(Sidedatum.Status.APPROVED) // 设置状态为 PENDING
                .build();

        SidedatumDto savaData = sideService.addSideData(updatedDto);

        return ResponseEntity.ok(savaData);
    }

    //    更新数据
    @PatchMapping("/{id}")
    public ResponseEntity<SidedatumDto> updateSideData(
            @PathVariable Integer id,
            @Valid @RequestBody SidedatumDto sidedatumDto) {

        SidedatumDto updatedDto = sidedatumDto.toBuilder()
                .status(Sidedatum.Status.PENDING)  // 新增此行
                .build();


        return ResponseEntity.ok(sideService.updataItem(id, updatedDto));
    }



    //    复制
    @PostMapping("/{id}/copy")
    public ResponseEntity<SidedatumDto> copySideData(@PathVariable Integer id) {
        SidedatumDto copiedData = sideService.copySideData(id);
        return ResponseEntity.ok(copiedData);
    }

    //    删除数据
    @DeleteMapping("/{id}")
    public ResponseEntity<SidedatumDto> deleteSideData(@PathVariable Integer id) {

        sideService.deledeSideData(id);

        return ResponseEntity.noContent().build();
    }

}