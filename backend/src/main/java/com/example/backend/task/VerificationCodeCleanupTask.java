package com.example.backend.task;

import com.example.backend.repository.user.EmailVerificationCodeRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class VerificationCodeCleanupTask {


    private final EmailVerificationCodeRepository codeRepository;


    public VerificationCodeCleanupTask( EmailVerificationCodeRepository codeRepository) {
        this.codeRepository = codeRepository;
    }

    @Scheduled(cron = "00 39 16 * * ?") // 每天凌晨3点执行
    @Transactional
    public void cleanupExpiredCodes() {
        Instant now = Instant.now();
        codeRepository.deleteByExpirationTimeBefore(now);
        System.out.println("已清理过期验证码记录");
    }


}
