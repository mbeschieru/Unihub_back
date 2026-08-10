package org.example.unihub.repository;

import org.example.unihub.entity.User;
import org.example.unihub.entity.OpenAiCall;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OpenAiCallRepository extends JpaRepository<OpenAiCall, Long> {
    
    @Query("SELECT COUNT(o) FROM OpenAiCall o WHERE o.user = ?1 AND o.timestamp >= ?2")
    long countCallsByUserAndTimeAfter(User user, LocalDateTime time);
    
    List<OpenAiCall> findByUserOrderByTimestampDesc(User user);

    List<OpenAiCall> findByUserAndTaskIdOrderByTimestampAsc(User user, Long taskId);
} 