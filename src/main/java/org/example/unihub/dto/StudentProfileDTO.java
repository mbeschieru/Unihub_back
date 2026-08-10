package org.example.unihub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StudentProfileDTO extends BaseUserProfileDTO {
    private String studentId;
    private Integer yearOfStudy;
    private String studentGroup;
} 