package org.example.unihub.errors;

import org.springframework.http.HttpStatus;

public class RateLimitException extends BaseException {
    public RateLimitException(String message) {
        super(message, HttpStatus.TOO_MANY_REQUESTS, "RATE_LIMIT_ERROR");
    }

    public static RateLimitException exceeded() {
        return new RateLimitException("Too many requests. Please try again later");
    }

    public static RateLimitException exceededWithRetryAfter(int seconds) {
        return new RateLimitException(String.format("Too many requests. Please try again after %d seconds", seconds));
    }
} 