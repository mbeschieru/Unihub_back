package org.example.unihub.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.cache.Cache;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
@EnableScheduling
public class CacheConfig {

    public static final String USERS_CACHE = "users";
    public static final String USER_PROFILES_CACHE = "userProfiles";

    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(java.util.Arrays.asList(
            USERS_CACHE,
            USER_PROFILES_CACHE
        ));
        
        // Configure cache-specific settings
        Map<String, CacheSettings> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put(USERS_CACHE, new CacheSettings(1000, Duration.ofMinutes(30)));
        cacheConfigurations.put(USER_PROFILES_CACHE, new CacheSettings(1000, Duration.ofMinutes(60)));
        return cacheManager;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }

    @Bean
    public CacheErrorHandler errorHandler() {
        return new SimpleCacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                // Log the error but don't throw it
                System.err.println("Cache get error: " + exception.getMessage());
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                // Log the error but don't throw it
                System.err.println("Cache put error: " + exception.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                // Log the error but don't throw it
                System.err.println("Cache evict error: " + exception.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                // Log the error but don't throw it
                System.err.println("Cache clear error: " + exception.getMessage());
            }
        };
    }

    private static class CacheSettings {
        private final int maxSize;
        private final Duration ttl;

        public CacheSettings(int maxSize, Duration ttl) {
            this.maxSize = maxSize;
            this.ttl = ttl;
        }

        public int getMaxSize() {
            return maxSize;
        }

        public Duration getTtl() {
            return ttl;
        }
    }
} 