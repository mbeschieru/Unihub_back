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
public class TaskSubmissionDTO {
    private Long id;
    private LocalDateTime submissionDate;
    private String content;
    private Integer grade;
    private String feedback;
    private Long taskId;
    private Long studentId;
} 