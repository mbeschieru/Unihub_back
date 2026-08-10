package org.example.unihub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LaboratoryDTO {
    private Long id;
    private String title;
    private Integer number;
    private String description;
} 