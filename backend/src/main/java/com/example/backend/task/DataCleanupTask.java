package com.example.backend.task;

import com.example.backend.entity.side.Sidedatum;
import com.example.backend.repository.side.SidedatumRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
public class DataCleanupTask {
    private final SidedatumRepository sidedatumRepository;

    public DataCleanupTask(SidedatumRepository sidedatumRepository) {
        this.sidedatumRepository = sidedatumRepository;
    }

    @Scheduled(cron = "0 0 3 * * ?", zone = "UTC") // 每天凌晨3点执行
    @Transactional
    public void purgeExpiredData() {
        log.info("开始执行数据清理任务...");
        int deletedCount = sidedatumRepository.deleteByStatusInAndExpiredAtBefore(
                List.of(Sidedatum.Status.PENDING, Sidedatum.Status.REJECTED),
                Instant.now()
        );
        log.info("已清理过期数据数量: {}", deletedCount);
    }


}
