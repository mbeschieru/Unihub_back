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
public class FinalTestRequest {
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Integer totalPoints;
} 