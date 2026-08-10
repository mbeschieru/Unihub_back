package org.example.unihub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LaboratoryRequest {
    private String title;
    private Integer number;
    private String description;
}
