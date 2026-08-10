package org.example.unihub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentDTO {
    private Long id;
    private String title;
    private String description;
    private Long laboratoryId;
    private LocalDateTime dueDate;
    private Integer totalPoints;
} 