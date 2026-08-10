package org.example.unihub.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "openai_calls")
public class OpenAiCall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    @Lob
    private String prompt;

    @Column(nullable = false, length = 4000)
    @Lob
    private String response;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer tokensUsed;

    @Column(name = "task_id")
    private Long taskId;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
} 