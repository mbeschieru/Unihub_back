package org.example.unihub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestQuestionDTO {
    private String question;
    private String correctAnswer;
    private Integer points;
    private Long testId;
} 