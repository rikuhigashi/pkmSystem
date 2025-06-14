package com.example.backend.controller.side;

import com.example.backend.annotation.CurrentUser;
import com.example.backend.dto.side.SidedatumDto;
import com.example.backend.entity.side.Sidedatum;
import com.example.backend.entity.user.User;
import com.example.backend.service.impl.side.SideServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/sideData")
public class SideController {

    private final SideServiceImpl sideService;


    @GetMapping("/all")
    public ResponseEntity<List<SidedatumDto>> getAllSideData(@CurrentUser User user) {


        List<SidedatumDto> data = sideService.getAllSideDataByUser(user);
        return ResponseEntity.ok(data);
    }


    //    通过id获取数据
    @GetMapping("/full/{id}")
    public ResponseEntity<SidedatumDto> getFullData(@PathVariable Integer id, @CurrentUser User user) {


        String email = user.getUsername();
        SidedatumDto data = sideService.getFullSidedata(id, email);

        return ResponseEntity.ok(data);
    }


    //    添加数据
    @PostMapping("/add")
    public ResponseEntity<SidedatumDto> addSideData(@RequestBody SidedatumDto sidedatumDto, @CurrentUser User user) {


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


    /**
     * 将数据移动到指定标签
     *
     * @return 移入标签
     */
    @PutMapping("/{id}/tag/{tagId}")
    public ResponseEntity<SidedatumDto> moveToTag(
            @PathVariable Integer id,
            @PathVariable Integer tagId,
            @CurrentUser User user) {
        String email = user.getUsername();
        SidedatumDto data = sideService.moveToTag(id, tagId, email);
        return ResponseEntity.ok(data);
    }

    /**
     * 从标签中移除数据
     *
     * @return 移出标签
     */
    @DeleteMapping("/{id}/tag")
    public ResponseEntity<SidedatumDto> removeFromTag(
            @PathVariable Integer id,
            @CurrentUser User user) {
        String email = user.getUsername();
        SidedatumDto data = sideService.removeFromTag(id, email);
        return ResponseEntity.ok(data);
    }


}