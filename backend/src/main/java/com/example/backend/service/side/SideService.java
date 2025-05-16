package com.example.backend.service.side;

import com.example.backend.dto.side.SidedatumDto;
import com.example.backend.entity.side.Sidedatum;
import com.example.backend.entity.user.User;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface SideService {

    //    获取全部数据
    List<SidedatumDto> getAllSideDataByUser(User user);

    //    根据id获取数据
    SidedatumDto getFullSidedata(Integer id, String userEmail);

    @Transactional
    SidedatumDto addSideData(SidedatumDto sidedatumDto);

    //    更新
    SidedatumDto updataItem(Integer id, SidedatumDto sidedatumDto);

    //    复制
    SidedatumDto copySideData(Integer id);

    //    删除
    void deledeSideData(Integer id);


    @Transactional
    void approveData(Integer id);

    @Transactional
    void rejectData(Integer id);

    Page<Sidedatum> getPendingData(String name, Instant expiredBefore, Pageable pageable);



}