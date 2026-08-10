package org.example.unihub.repository;

import org.example.unihub.entity.RateLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RateLimitRepository extends JpaRepository<RateLimit, Long> {
    Optional<RateLimit> findByKeyAndType(String key, String type);
    
    @Modifying
    @Query("DELETE FROM RateLimit r WHERE r.windowStart < ?1")
    void deleteExpiredWindows(LocalDateTime cutoffTime);
    
    @Query("SELECT COUNT(r) FROM RateLimit r WHERE r.key = ?1 AND r.type = ?2 AND r.windowStart >= ?3")
    long countRequestsInWindow(String key, String type, LocalDateTime windowStart);
} 