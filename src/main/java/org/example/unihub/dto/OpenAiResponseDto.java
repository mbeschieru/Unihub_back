package org.example.unihub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiResponseDto {
    private Long id;
    private String prompt;
    private String response;
    private LocalDateTime timestamp;
    private Integer tokensUsed;
    private Long userId;
    private Long taskId;
} 