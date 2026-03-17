package com.edsonuso.collabapi.auth.job;

import com.edsonuso.collabapi.auth.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupJob {
    private final RefreshTokenRepository tokenRepository;

    @Scheduled(fixedRate = 6 * 60 * 60 * 1000, initialDelay = 60_000) // 6 horas
    @Transactional
    public void purgeExpiredTokens() {
        int deleted = tokenRepository.deleteExpiredAndRevoke(Instant.now());
        if (deleted > 0) {
            log.info("Cleanup: {} refresh tokens expirados/revogados removidos", deleted);
        }
    }

}
