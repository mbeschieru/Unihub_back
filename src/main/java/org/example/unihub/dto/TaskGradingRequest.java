package org.example.unihub.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskGradingRequest {
    @NotNull(message = "Grade is required")
    @Min(value = 0, message = "Grade must be at least 0")
    @Max(value = 100, message = "Grade cannot exceed 100")
    private Integer grade;

    @NotBlank(message = "Feedback is required")
    private String feedback;
} 