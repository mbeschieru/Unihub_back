package org.example.unihub.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "rate_limits")
public class RateLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String key; // IP address or user identifier

    @Column(nullable = false)
    private Integer requestCount;

    @Column(nullable = false)
    private LocalDateTime windowStart;

    @Column(nullable = false)
    private String type; // "IP" or "USER"

    @PrePersist
    protected void onCreate() {
        if (windowStart == null) {
            windowStart = LocalDateTime.now();
        }
    }
} 