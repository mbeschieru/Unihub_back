package org.example.unihub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClassRequest {
    private String name;
    private String description;
    private Integer year;
} 