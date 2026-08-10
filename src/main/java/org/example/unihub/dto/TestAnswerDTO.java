package org.example.unihub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestAnswerDTO {
    private String answer;
    private Boolean isCorrect;
    private Integer pointsEarned;
    private Long questionId;
    private Long studentId;
} 