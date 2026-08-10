package org.example.unihub.config.scheduler;

import org.example.unihub.config.filter.RateLimitingFilter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RateLimiterResetScheduler {

    private final RateLimitingFilter rateLimitingFilter;

    public RateLimiterResetScheduler(RateLimitingFilter rateLimitingFilter) {
        this.rateLimitingFilter = rateLimitingFilter;
    }

    @Scheduled(fixedRate = 60000) // Reset every minute
    public void resetRateLimiterCounts() {
        rateLimitingFilter.resetCounts();
    }
} 